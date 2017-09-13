var xialagood = null;
var xialastore = null;
var type = Request['type'] * 1;

$(function(){
	if(type==1){
		//加载表格数据
		$('#grid').datagrid({
			url:'inventory_listByPage',
			columns:[[
			  		    {field:'uuid',title:'编号',width:100},
			  		    {field:'storeName',title:'仓库',width:100},
			  		    {field:'goodsName',title:'商品',width:100},
			  		    {field:'num',title:'数量',width:100},
			  		    {field:'type',title:'类型',width:100,formatter:formatType},
			  		    {field:'createtime',title:'登记日期',width:100,formatter:formatDate},
			  		    {field:'checktime',title:'审核日期',width:100,formatter:formatDate},
			  		    {field:'createrName',title:'登记人',width:100},
			  		    {field:'checkerName',title:'审核人',width:100},
			  		    {field:'state',title:'状态',width:100,formatter:formatState},
			  		    {field:'remark',title:'备注',width:100}
						]],
			singleSelect: true,
			pagination: true,
			toolbar: [{
				text: '盘盈盘亏登记',
				iconCls: 'icon-add',
				handler: function(){
					//设置保存按钮提交的方法为add
					method = "add";
					//清空表单内容
					$('#editForm').form('clear');
					//编辑窗口
					$('#editDlg').dialog('open');
				}
			}]
		});
	}
	if(type==2){
		//加载表格数据
		$('#grid').datagrid({
			url:'inventory_listByPage',
			columns:[[
			  		    {field:'uuid',title:'编号',width:100},
			  		    {field:'storeName',title:'仓库',width:100},
			  		    {field:'goodsName',title:'商品',width:100},
			  		    {field:'num',title:'数量',width:100},
			  		    {field:'type',title:'类型',width:100,formatter:formatType},
			  		    {field:'createtime',title:'登记日期',width:100,formatter:formatDate},
			  		    {field:'checktime',title:'审核日期',width:100,formatter:formatDate},
			  		    {field:'createrName',title:'登记人',width:100},
			  		    {field:'checkerName',title:'审核人',width:100},
			  		    {field:'state',title:'状态',width:100,formatter:formatState},
			  		    {field:'remark',title:'备注',width:100}
						]],
			singleSelect: true,
			pagination: true,
			onDblClickRow:function(rowIndex, rowData){
				/*
				 在用户双击一行的时候触发，参数包括：
				rowIndex：点击的行的索引值，该索引值从0开始。
				rowData：对应于点击行的记录。
				*/
				if(rowData.state==0){
					doCheck(rowData.uuid,rowData.num,rowData.state);
				}else {
					return ;
				}
			}
		});
	}

	add();
	
	$('#goodxiala').combobox({
		onSelect: function(param){
			$('#argnum').val(null);
			xialagood = param.uuid;
			getnum();
		}
	});
	
	$('#storexiala').combobox({
		onSelect: function(param){
			$('#argnum').val(null);
			xialastore = param.uuid;
			getnum();
		}
	});



});

//初始化新增窗口
function add(){
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load',formData);
	});
	//初始化登记
	$('#editDlg').dialog({
		title: '盘盈盘亏登记',//窗口标题
		width: 300,//窗口宽度
		height: 300,//窗口高度
		closed: true,//窗口是是否为关闭状态, true：表示关闭
		modal: true,//模式窗口
		closable: false,//不显示关闭按钮
		buttons:[{
			text:'保存',
			iconCls: 'icon-save',
			handler:function(){
				//当所有验证都通过后，才可提交
				if(!$('#editForm').form('validate')){
					return;
				}
				//用记输入的部门信息
				var submitData= $('#editForm').serializeJSON();
				$.ajax({
					url: 'inventory_add',
					data: submitData,
					dataType: 'json',
					type: 'post',
					success:function(rtn){
						//{success:true, message: 操作失败}
						$.messager.alert('提示',rtn.message, 'info',function(){
							if(rtn.success){
								//关闭弹出的窗口
								$('#editDlg').dialog('close');
								//刷新表格
								$('#grid').datagrid('reload');
							}
						});
					}
				});
			}
		},{
			text:'关闭',
			iconCls:'icon-cancel',
			handler:function(){
				//关闭弹出的窗口
				$('#editDlg').dialog('close');
				xialagood = null;
				xialastore = null;
			}
		}]
	});
}


/**
 * 审核
 */
function doCheck(uuid,num,state){
	$.messager.confirm("确认","确认要审核吗？",function(yes){
		if(yes){
			$.ajax({
				url:'inventory_doCheck',
				data:{id:uuid},
				dataType:'json',
				type:'post',
				success:function(rtn){
					$.messager.alert("提示",rtn.message,'info',function(){
						if(rtn.success){
							$('#grid').datagrid('reload');
						}
					});
				}
			});
		}
	});
}

/**
 * 日期格式化器
 */
function formatDate(value){
	if(null == value){
		return null;
	}
	return new Date(value).Format('yyyy-MM-dd');
}

/**
 * 订单状态
 */
function formatState(value){
	if(value*1==0){
		return "未审核";
	}
	if(value*1==1){
		return "已审核";
	}
}

/**
 * 明细状态
 */
function formatType(value){
	if(value*1==0){
		return "盘盈";
	}
	if(value*1==1){
		return "盘亏";
	}	
}

function getnum(){
	if(xialagood!=null && xialastore!=null){
		$.ajax({
			url:'storedetail_getnum()',
			data:{storeuuid:xialastore,goodsuuid:xialagood},
			dataType:'json',
			type:'post',
			success:function(rtn){
				var count = rtn[0].num;
				if(count==-1){
					alert("该仓库没有此商品");
				}else{
					$('#argnum').val(count);
				}
			}
		});
	}
}


