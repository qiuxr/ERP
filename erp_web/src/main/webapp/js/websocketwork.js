var uuid="";
var username="";
var timeflag = false;

//创建与客户端连接的websocket对象
var ws = new WebSocket("ws://192.168.39.205:8080/erp/empws/empWebSocket");
$(function(){

	// 关闭连接
	function closeWebSocket() {
	    ws.close();
	}


/**
 * 展示会话消息
 */
function showMessage(id, name, textmessage) {
	//如果传入的用户名和登陆的用户名相同的话, 将消息显示在对话窗口的右侧
	if(name == $('#username').html()) {
		var showusername = '<div align="right" style="color: gray;padding-bottom: 2px;padding-left: 8px;padding-right: 8px;padding-top: 4px">'+formatDate()+'     '+ name +'</div>';
		var showmessagetext = '<div align="right" style="color: red;font-size:18px;padding-bottom: 4px;padding-left: 8px;padding-right: 8px;padding-top: 2px">'+ textmessage +'</div>';
	}else {
		//如果传入的用户名和登陆的用户名相同的话, 将消息显示在对话窗口的左侧
		var showusername = '<div align="left" style="color: gray;padding-bottom: 2px;padding-left: 8px;padding-right: 8px;padding-top: 4px">'+ name +'     '+ formatDate() +'</div>';
		var showmessagetext = '<div align="left" style="color: red;font-size:18px;padding-bottom: 4px;padding-left: 8px;padding-right: 8px;padding-top: 2px">'+ textmessage +'</div>';
	}
	//将消息添加到容器中(显示发送的用户时间和消息)
	$('#show_'+id).append(showusername);
	$('#show_'+id).append(showmessagetext);
}
/**
 * 登录的方法(将当前用户存到服务端中), 将登陆之后的用户的uuid和username转换成json字符串发送到服务端"
 */
function login() {
	var empInfo = {'loginempuuid':$('#loginempuuid').val(),'username':$('#username').html()};
	ws.send(JSON.stringify(empInfo));
}

var timeplan1 = setInterval(function() {
	if($('#username').html() != ""){
		login();
		clearInterval(timeplan1);
	}
},500);

/**
 * 监听websocket连接成功时, 执行login()方法
 */
ws.onopen = function(message) {
	login();
};
/**
 * 监听websocket连接关闭时,调用的方法
 */
ws.onclose = function(message) {

};
/**
 * 监听websocket接收到消息时调用的方法
 */
ws.onmessage = function(returnData) {
	if(returnData.data != null) {
		//如果返回的数据不为空时, 转换字符串为json对象
		var jsondata = JSON.parse(returnData.data);
		// 客户端返回存在info时, 表示上线用户列表有变化
		if(jsondata.info != null && jsondata.info != 'undefined') {
			//将用户列表的表头更新, 在线用户的数量
			$('#eastuserlist').html('<div style="height:18px;text-align: center;line-height: 18px;padding:2px;"><font color="red" id="usernumber"></font>位在线用户</div>');
			$('#usernumber').html(getJsonLength(jsondata.info));
			//循环遍历, 获取所有在线用户的信息
			$.each(jsondata.info, function(id, name) {
				//在用户列表中添加div标签, 显示用户列表
				var online = $('#eastuserlist');
				online.append($('<div id="'+id+'" style="height:18px;padding:2px;text-align: center;line-height: 18px;"><a href="#" >'+name+'</a></div>'));
				//获取与当前用户的对话窗, 如果没有, 创建并加载
				if($('#mm_'+ id).length<=0) {
					//创建对话窗容器
					$('#mm').after('<div id="mm_'+id+'"></div>');
					//初始化容器内的内容
					initDialog(id,name);
					//初始化窗口的属性
					$('#mm_'+ id).dialog({
						title: '与\\  '+ name +'  /对话窗口',    
						width: 400,    
						height: 400,    
						closed: true,    
						modal: false,
						buttons:[{
							text:'关闭',
							handler:function() {
								//关闭窗口
								$('#mm_'+ id).dialog('close');
							}
						},{
							text:'发送',
							handler:function() {
								//发送消息时, 调用展示方法, 将发送内容出输出到展示框, 并发送内容, 清空输入框
								showMessage(id, $('#username').html(), $('#send_'+id).html());
								//封装发送信息
								var text =JSON.stringify({'touuid': id, 'tomessage': $('#send_'+id).html()});
								//将展示框滚动条始终位于最下方
								$('#show_'+id).scrollTop( $('#show_'+id).height() );
								//清空输入框
								$('#send_'+id).html('');
								//发送消息
								ws.send(text);
							}
						}]
						
					});
				}
				//将用户列表的每个用户绑定单击事件
				$('#'+id).bind('click',function() {
					//将时间标签置为true. 停止定时器
					timeflag = true;
					//打开会话窗
					$('#mm_'+ id).dialog('open');
					//将展示框滚动条始终位于最下方
					$('#show_'+id).scrollTop( $('#show_'+id).height() );
				});
			});
		// 客户端返回存在info时, 表示有别的用户发消息
		}else if(jsondata.message != null && jsondata.message != 'undefined') {
			//定义一个变量接收用户id信息
			var xxid = "";
			//循环遍历从服务端发送回的json消息数据, 第一重循环获取用户id
			$.each(jsondata.message, function(id, map) {
				//给xxid赋值
				xxid = id;
				//第二重循环获取用户名和用户发送的消息
				$.each(map, function(name, message) {
					//调用展示消息的方法
					showMessage(id, name, message);
					//将展示框滚动条始终位于最下方
					$('#show_'+id).scrollTop( $('#show_'+id).height() );
				});
			});
			//定义一个计数器变量
			var i = 1;
			//判断与用户聊天会话窗是否隐藏
			if(!$('#mm_'+ xxid).parent().is(":hidden")) {
				//如果是打开的, 将时间标签置为true. 停止定时器
				timeflag = true;
			}
			//定义一个定时器, 每当用户有消息时, 将发送方在用户列表上的用户名闪烁显示
			var timeplan = setInterval(function() {
				//如果时间标签为true
				if(timeflag) {
					//将背景色设置为白色
					$('#'+xxid).css("background-color","white");
					//将时间标签重置为false
					timeflag = false;
					//停止定时器
					clearInterval(timeplan);
				}else {
					//如果余数为1, 将div背景色设置为白色
					if(i%2 == 1) {
						$('#'+xxid).css("background-color","white");
					}else {
						//否则将背景色设置为浅蓝色
						$('#'+xxid).css("background-color","#009ad9");
					}
					//计数器自增
					i= i + 1;
				}
			//延迟500ms切换
			},500);
			
		}
	}
};

// 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function() {
    ws.close();
};

/**
 * 退出登录
 */
$('#loginOut').bind("click",function(){
	//点击退出登录, 发送websocket退出登录的指定标识
	ws.send(JSON.stringify({'logout': $('#loginempuuid').val()}));
	$.ajax({
		url: 'login_loginOut',
		type: 'post',
		success:function(rtn){
			location.href="login.html";
		}
	});
});

});

/**
 * 初始化会话窗内的内容
 * @param uuid
 * @param username
 */
function initDialog(uuid,username) {
	$('#mm_'+ uuid).append('<div id="show_'+uuid+
			'" style="width:100%;height:230px;padding-top: 4px;overflow-y:auto"></div><div style="background-color:'+ 
			' #e7eefe;width:100%;height:20px"></div><div id="send_'+uuid+
			'" contenteditable="true" style="font-size:18px;width:100%;height:76px;overflow-y:auto">');
	
}

/**
 * 获取mapjson对象中的数量
 * @param jsonData
 * @returns {Number}
 */
function getJsonLength(jsonData) {  
	var length = 0;  
	for(var ever in jsonData) {  
	    length++;  
	}  
	return length;  
}  

// 格式化日期时间
function formatDate(){
	return new Date().Format('yyyy-MM-dd hh:mm:ss');
}
