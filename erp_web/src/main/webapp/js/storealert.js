$(function(){
	var time = 0;
	$('#grid').datagrid({
		url: 'storedetail_storealertList',
		columns:[[
		    {field:'uuid',title:'商品编号',width:100},
		    {field:'name',title:'商品名称',width:100},
		    {field:'storenum',title:'库存数量',width:100},
		    {field:'outnum',title:'待发货数量',width:100}
		]],
		singleSelect: true,
		toolbar:[{
		    	text:'发送预警邮件',
		    	iconCls:'icon-alert',
		    	handler:function(){
		    		$('#dialogSending').dialog('open');
		    		$.ajax({
		    			url:'storedetail_sendStorealert',
		    			dataType:'json',
		    			type:'post',
		    			success:function(rtn){
		    				if(rtn.success) {
		    					time = 100;
		    				}
	     					setTimeout(function() {
	     						clearInterval(timeplan);
	     						$('#dialogSending').dialog('close');
		     					$.messager.alert('提示',rtn.message,'info');
	     					},500);
		    			}
		    		});
		    		
		    		$('#sending').progressbar({ 
	     				value: 0 
	     			});
		    		
	     			var value = $('#sending').progressbar('getValue'); 
     				var timeplan = setInterval(function() {
     					value += Math.floor((95-value)*Math.random()*0.2); 
    	     			$('#sending').progressbar('setValue', value); 
    	     			if( time == 100) {
    	     				$('#sending').progressbar('setValue', 100); 
    	     			}
     				},100);
		    	}
		    }
		]
	});
	
	$('#dialogSending').dialog({
		title: '邮件发送中',    
	    width: 420,    
	    height: 200,    
	    closed: true,    
	    modal: true 
	}); 
	
});
