var url='returnorders_listByPage';
var oper=Request['oper'];
var type=Request['type'];

$(function(){
	/*判断是否是我的订单增加状态*/
	if(oper=='myReturnOrders'){
		if(type*1==1){
			url+='?t1.type=1';	
			document.title='采购退货登记'
		}
		if(type*1==2){
			url+='?t1.type=2';
			document.title='销售退货登记'
		}
	}
	/*判断是否是待审核增加状态*/
	if(oper=='doCheck'){
		if(type*1==1){
			url+='?t1.type=1&t1.state=0';
			document.title='采购退货审核'
		}
		if(type*1==2){
			url+='?t1.type=2&t1.state=0';
			document.title='销售退货审核'
		}
	}
	/*判断是否是待出库增加状态*/
	if(oper=='doOutStore'){
		url+='?t1.type=1&t1.state=1';
		document.title='采购退货出库'
	}
	if(oper=='doInStore'){
		url+='?t1.type=2&t1.state=1';
		document.title='销售退货入库'
	}
	/*初始化主列表*/
	$('#grid').datagrid({
		url:url,
		columns:getColumns(),
		singleSelect: true,
		pagination: true,
		fitColumns:true,
		onDblClickRow:function(rowIndex, rowData){
			$('#uuid').html(rowData.uuid);
			$('#createtime').html(formatterDate(rowData.createtime));
			$('#checktime').html(formatterDate(rowData.checktime));
			$('#endtime').html(formatterDate(rowData.endtime));
			$('#createrName').html(rowData.createrName);
			$('#checkerName').html(rowData.checkerName);
			$('#enderName').html(rowData.enderName);
			if(type*1==1){
				$('#supplierType').html('供应商');	
			}
			if(type*1==2){
				$('#supplierType').html('客户');	
			}
			$('#supplierName').html(rowData.supplierName);
			$('#state').html(returnOrderstate(rowData.state));
			$('#ordersuuid').html(rowData.ordersuuid);
			
		    $('#returnOrdersDlg').dialog('open');
			
			$('#itemgrid').datagrid('loadData',rowData.returnorderdetails);
		}
	});
	
	/*退货登记初始化*/
	$('#itemaddDlg').dialog({
		title: '增加退货订单',//窗口标题
		width: 700,//窗口宽度
		height: 300,//窗口高度
		closed: true,//窗口是是否为关闭状态, true：表示关闭
		modal: true,//模式窗口
	});
	
	/*我的订单申请添加退货订单*/
	if(oper=='myReturnOrders'){
		if (type*1==1) {
			$('#suppliervalue').html('供应商');
			var _text='采购退货登记'
		}
		if (type*1==2) {
			$('#suppliervalue').html('客户');
			var _text='销售退货登记'
		}
		/*主表增加订单申请按钮*/
		$('#grid').datagrid({
			toolbar:[{
				text:_text,
				iconCls:'icon-add',
				handler:function(){
					$('#itemaddDlg').dialog('open')
				}
			}]
		})
	}
	
	
	/*订单详情弹窗初始化,判断OPER设置，增加审核工具按钮*/
	if (oper=='doCheck') {
		$('#returnOrdersDlg').dialog({
			toolbar:[{
				text:'审核',
				iconCls:'icon-search',
				handler:doCheck
			}]	
		})
	}
	$('#returnOrdersDlg').dialog({
		title: '订单详情',//窗口标题
		width: 700,//窗口宽度
		height: 300,//窗口高度
		closed: true,//窗口是是否为关闭状态, true：表示关闭
		modal: true,//模式窗口
	});
	
	/*点击单个订单详情明细列表初始化*/
	$('#itemgrid').datagrid({
		columns:[[ 
		            {field:'uuid',title:'编号',width:100},
		  		    {field:'goodsuuid',title:'商品编号',width:100},
		  		    {field:'goodsname',title:'商品名称',width:100},
		  		    {field:'price',title:'价格',width:100},
		  		    {field:'num',title:'数量',width:100},
		  		    {field:'money',title:'金额',width:100},
		  		    {field:'state',title:'状态',width:100,formatter:returnorderdetailstate}   
		          ]],
		singleSelect: true,
		fitColumns:true
	});
	
	/*当URL地址添加出库或入库字段时,为单个订单窗口订单明细添加点击事件，开启入库或出库窗口*/
	if(oper=='doInStore'||oper=='doOutStore'){
		$('#itemgrid').datagrid({
			onDblClickRow:function(rowIndex, rowData){
				 $('#id').val(rowData.uuid);
				 var ordersuuid=$('#ordersuuid').html();
				 $('#ordersuuidDostore').val(ordersuuid);
				 $('#goodsuuid').html(rowData.goodsuuid);
				 $('#goodsname').html(rowData.goodsname);
				 $('#num').html(rowData.num);
				 /*入库或出库窗口开启,并判断明细状态选择是否开弹窗*/
				 if (rowData.state*1==0) {
					 $('#itemDlg').dialog('open');	
				 }
			}
		})
	}
	
	if (type*1==1) {
		/*入库窗口初始化*/
		$('#itemDlg').dialog({
			title: '采购退货出库',//窗口标题
			width: 300,//窗口宽度
			height: 200,//窗口高度
			closed: true,//窗口是是否为关闭状态, true：表示关闭
			modal: true,//模式窗口
			buttons:[{
				text:'出库',
				iconCls:'icon-save',
				handler:doInStore
			}]
		});
	}
	if (type*1==2) {
		$('#itemDlg').dialog({
			title: '销售退货入库',//窗口标题
			width: 300,//窗口宽度
			height: 200,//窗口高度
			closed: true,//窗口是是否为关闭状态, true：表示关闭
			modal: true,//模式窗口
			buttons:[{
				text:'入库',
				iconCls:'icon-save',
				handler:doInStore
			}]
		});
	}
	
	
	
})

/*主列表动态显示方法*/
function getColumns(){
	if (type*1==1) {
	   return [[
	            {field:'uuid',title:'编号',width:100},
	  		    {field:'createtime',title:'录入日期',width:100,formatter:formatterDate},
	  		    {field:'checktime',title:'审核日期',width:100,formatter:formatterDate},
	  		    {field:'endtime',title:'出库日期',width:100,formatter:formatterDate},
	  		    
	  		    {field:'createrName',title:'下单员',width:100},
	  		    {field:'checkerName',title:'审核员',width:100},
	  		    {field:'enderName',title:'库管员',width:100},
	  		    {field:'supplierName',title:'供应商',width:100},
	  		    {field:'totalmoney',title:'总金额',width:100},
	  		    {field:'state',title:'订单状态',width:100,formatter:returnOrderstate},
	  		    {field:'waybillsn',title:'运单号',width:100},
	  		    
			  ]];
	}
	if (type*1==2) {
		return    [[
					 {field:'uuid',title:'编号',width:100},
					  {field:'createtime',title:'录入日期',width:100,formatter:formatterDate},
					  {field:'checktime',title:'审核日期',width:100,formatter:formatterDate},
					  {field:'endtime',title:'入库日期',width:100,formatter:formatterDate},
					  
					  {field:'createrName',title:'下单员',width:100},
					  {field:'checkerName',title:'审核员',width:100},
					  {field:'enderName',title:'库管员',width:100},
					  {field:'supplierName',title:'客户',width:100},
					  {field:'totalmoney',title:'总金额',width:100},
					  {field:'state',title:'订单状态',width:100,formatter:returnOrderstate},
					  {field:'waybillsn',title:'运单号',width:100},
				   
				  ]];
	}
}

/*日期转换方法*/
function formatterDate(value){
	if(null==value){
		return;
	}
	 return new Date(value).Format('yyyy-MM-dd');
}

/*订单状态显示方法*/
function returnOrderstate (value){
	if (type*1==1) {
		switch (value*1){ 
		case 0:return '未审核';
		case 1:return '已审核';
		case 2:return '已出库';
		}	
	}
	if (type*1==2) {
		switch (value*1){ 
		case 0:return '未审核';
		case 1:return '已审核';
		case 2:return '已入库';
		}	
	}
}

/*订单明细状态显示方法*/
function returnorderdetailstate (value){
	if (type*1==1) {
		switch (value*1){ 
		case 0:return '未出库';
		case 1:return '已出库';
		}	
	}
	if (type*1==2) {
		switch (value*1){ 
		case 0:return '未入库';
		case 1:return '已入库';
		}
	}
}
/*审核方法*/
function doCheck(){
	$.messager.confirm('审核','您确认想要审核吗？',function(y){    
	    if (y){    
	       $.ajax({
	    	   url:'returnorders_docheck?id='+$('#uuid').html(),
	    	   dataType:'json',
	    	   type:'post',
	    	   success:function(rtn){
	    		   $.messager.alert('提示',rtn.message, 'info',function(){
						if(rtn.success){
							
							$('#returnOrdersDlg').dialog('close');
							//刷新表格
							$('#grid').datagrid('reload');
						}
				   });
	    	   }
	       }) 
	    }    
	});  
}

/*出入库方法*/
function doInStore(){
	var url="";
	var message="";
    var cmessage="";
	if (type*1==2) {
		url='returnorderdetail_doInStore';
		message='您确认想要入库吗？';
		cmessage='入库'
	}
	if (type*1==1) {
		url='returnorderdetail_doOutStore';
		message='您确认想要出库吗？';
		cmessage='出库';
	}
	$.messager.confirm(cmessage,message,function(y){    
	    if (y){
	       var submitData= $('#itemForm').serializeJSON();
	       if (submitData.storeuuid=="") {
	    	   $.messager.alert('提示','请选择仓库', 'info');
	    	   return;
		   }
	       $.ajax({
	    	   url:url,
	    	   data:submitData,
	    	   dataType:'json',
	    	   type:'post',
	    	   success:function(rtn){
	    		   $.messager.alert('提示',rtn.message, 'info',function(){
						if(rtn.success){
							
							$('#itemDlg').dialog('close');
						
							$('#itemgrid').datagrid('getSelected').state=1;
							var data=$('#itemgrid').datagrid('getData');
							$('#itemgrid').datagrid('loadData',data)
							
							var allIn=true;
							$.each(data.rows,function(i,r){
								if (r.state==0) {
									allIn=false;
									return false;
								}
							})
							if (allIn==true) {
								$('#returnOrdersDlg').dialog('close');
								
								$('#grid').datagrid('reload');
							}
							
						}
				   });
	    	   }
	       }) 
	    }    
	});  
}