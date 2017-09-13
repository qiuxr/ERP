var existEditIndex=-1;
var ordersurl='orders_listByPage?t1.type='
var goodsurl="";
$(function(){	
	if(type*1==1){
		$('#ordersName').html('原采购订单')
		ordersurl+=type+'&t1.state=3'
	}
	if(type*1==2){
		$('#ordersName').html('原销售订单')
		ordersurl+=type+'&t1.state=1'
	}
	
	/*查看原订单表格初始化*/
	$('#orders').combogrid({    
		panelWidth:750,
		idField:'uuid',    
		textField:'uuid',    
		url:ordersurl, 
		fitColumns:true,
		columns:getOrderColumns(),
	    mode:'remote',
	    pagination:true
	});
    var orders = $('#orders').combogrid('grid');// 获取数据表格对象
   /* 当前对象绑定点击事件触发添加明细*/
    orders.datagrid({
    	 onSelect:function(rowIndex, rowData){
    		delall();
    		
    		$('#ordersgrid').datagrid('reloadFooter',[{
    			num: '合计',
    			money:0,
    		}]);
    		 
			$('#outsupplierName').val(rowData.supplierName);
			$('#supplier').val(rowData.supplieruuid);
			goodsurl='goods_listByOrdersuuid?ordersuuid='+rowData.uuid
		 },
   
	});
 
    /*新增可编辑表格初始化*/
	$('#ordersgrid').datagrid({
		columns:[[
		  		    {field:'goodsuuid',title:'商品编号',width:100,editor:'numberbox'},
		  		    {field:'goodsname',title:'商品名称',width:100,editor:{type:'combobox',options:{
		  		    	/*url:goodsurl,*/
		  		        valueField:'name',
		  		        textField:'name',
		  		        onSelect:function(goods){
		  		        	var goodsuuidEditor = getEditor('goodsuuid');
		  		        	$(goodsuuidEditor.target).val(goods.uuid);
		  		  
		  		        	var priceEditor = getEditor('price');
		  		
		  		        	if (type*1==1) {
		  		        		$(priceEditor.target).val(goods.inprice);
		  		        		
							}
		  		        	if (type*1==2) {
		  		        		$(priceEditor.target).val(goods.outprice);
		  		        		
		  		        	}
		  		        	
		  		        	var forordersnumEditor = getEditor('forordersnum');
		  		        	$(forordersnumEditor.target).val(goods.ordersnum);
		  		        	
		  		        	var storedetailnumEditor = getEditor('storedetailnum');
		  		        	$(storedetailnumEditor.target).val(goods.storedetailnum);
		  		  
		  		  
		  		      	    var numEditor = getEditor('num');
		  		  		    $(numEditor.target).select();
		  		  		    
		  		  		    bindKeyUp();
		  		  		    cal();
		  				    sum();
		
		  			    }
		  		    }}},
		  		    {field:'price',title:'价格',width:100,editor:{type:'numberbox',options:{
		  		    	min:0, 
		  		    	precision:2,
		  		    	disabled:true
		  		     }}},
		  		    {field:'num',title:'数量',width:100,editor:{type:'numberbox',options:{
		  		    	min:1, 
		  		     }}},
		  		     {field:'forordersnum',title:'原订单数量',width:80,editor:{type:'numberbox',options:{
		  		    	disabled:true
		  		     }}},
		  		     {field:'storedetailnum',title:'库存余量',width:80,editor:{type:'numberbox',options:{
		  		    	 disabled:true
		  		     }}},
		  		    {field:'money',title:'金额',width:60,editor:{type:'numberbox',options:{
		  		    	min:0, 
		  		    	precision:2,
		  		    	disabled:true  
		  		     }}}, 

					{field:'-',title:'操作',width:200,formatter: function(value,row,index){
						if (row.num=='合计') {
							return;
						}
						var oper = 
						 '<a href="javascript:void(0)" onclick="del(' + index + ')">删除</a>';
						return oper;
					}}
					]],
		singleSelect: true,
		showFooter:true,
		toolbar: [{
			text: '新增',
			iconCls: 'icon-add',
			handler: function(){
        
				if (existEditIndex>-1) {
					$('#ordersgrid').datagrid('endEdit',existEditIndex);
				}
				$('#ordersgrid').datagrid('appendRow',{
					num: 0,
					money:0,
				});
				existEditIndex=$('#ordersgrid').datagrid('getRows').length-1;
				$('#ordersgrid').datagrid('beginEdit',existEditIndex);
				
				var goodsnameEditor = getEditor('goodsname');
		        goodsnameEditor.target.combobox('reload',goodsurl);
		        
				bindKeyUp();
				
			}
		},'_',{
			text:'保存',
			iconCls: 'icon-save',
			handler:function(){
				if (existEditIndex>-1) {
					$('#ordersgrid').datagrid('endEdit',existEditIndex);
				}
				var submitData= $('#orderForm').serializeJSON();
				if (submitData['t.t.ordersuuid']=="") {
					$.messager.alert('提示','请选择原订单', 'info');
					return;
				}
				if (submitData['t.supplieruuid']=="") {
					$.messager.alert('提示','请选择供应商', 'info');
					return;
				}
				
				var rows = $('#ordersgrid').datagrid('getRows');		    
				/*rows数据json格式传递过去和原表单数据*/
				submitData.json=JSON.stringify(rows);
				$.ajax({
					url: 'returnorders_add?t.type='+type,
					data: submitData,
					dataType: 'json',
					type: 'post',
					success:function(rtn){
						$.messager.alert('提示',rtn.message, 'info',function(){
							if(rtn.success){
								
								$('#orders').combogrid('clear');
								$('#outsupplierName').val('');
								
								$('#ordersgrid').datagrid('loadData',{total:0,rows:[],footer:[{
									num: '合计',
									money:0,
								}]});
							
								$('#itemaddDlg').dialog('close')
								//刷新表格
								$('#grid').datagrid('reload');
								
							}
						});
					}
				});
			}
		}],
		onClickRow:function(rowIndex, rowData){
			if (existEditIndex>-1) {
				$('#ordersgrid').datagrid('endEdit',existEditIndex);
			}
			existEditIndex=rowIndex;
			$('#ordersgrid').datagrid('beginEdit',existEditIndex);
			
			/*点击事件发生重载URL*/
			var goodsnameEditor = getEditor('goodsname');
	        goodsnameEditor.target.combobox('reload',goodsurl);
		},
	});
	
	$('#ordersgrid').datagrid('reloadFooter',[{
		num: '合计',
		money:0,
	}]);
	

})

/*根据类型生成查询原订单表格方法*/
function getOrderColumns(){
	if (type*1==1) {
	   return [[
	            {field:'uuid',title:'编号',width:70},
	  		    {field:'createtime',title:'录入日期',width:120,formatter:formatterDate},
	  		    {field:'checktime',title:'审核日期',width:120,formatter:formatterDate},
	  		    {field:'endtime',title:'出库日期',width:120,formatter:formatterDate}, 
	  		    {field:'createrName',title:'下单员',width:100},
	  		    {field:'checkerName',title:'审核员',width:100},
	  		    {field:'enderName',title:'库管员',width:100},
	  		    {field:'supplierName',title:'供应商',width:100},
	  		    {field:'totalmoney',title:'总金额',width:100},
	  		    {field:'state',title:'订单状态',width:100,formatter:function(value){
	  		    	switch(value*1){
		  		    	case 0:return '未审核';
		  				case 1:return '已审核';
		  				case 2:return '已确认';
		  				case 3:return '已入库';
	  		    	}
	  		    }},
	  		    
			  ]];
	}
	if (type*1==2) {
		return    [[
		              {field:'uuid',title:'编号',width:100},
					  {field:'createtime',title:'生成日期',width:100,formatter:formatterDate},
					  {field:'endtime',title:'出库日期',width:100,formatter:formatterDate},
					  {field:'createrName',title:'下单员',width:100},
					  {field:'enderName',title:'库管员',width:100},
					  {field:'supplierName',title:'客户',width:100},
					  {field:'totalmoney',title:'合计金额',width:100},
					  {field:'state',title:'状态',width:100,formatter:function(value){
							switch (value*1){ 
			  				case 0:return '未出库';
			  				case 1:return '已出库';	
			  		    	}
					  }}, 	
				   
				  ]];
	}
}

/*删除当前行清空数据*/
function del(index){
	$('#ordersgrid').datagrid('deleteRow',index);
	var data=$('#ordersgrid').datagrid('getData');
	$('#ordersgrid').datagrid('loadData',data);
    sum();
}

/*清空所有数据*/
function delall(){
	 var item = $('#ordersgrid').datagrid('getRows');   
     for (var i = item.length - 1; i >= 0; i--) {    
         var index = $('#ordersgrid').datagrid('getRowIndex', item[i]);    
         $('#ordersgrid').datagrid('deleteRow', index);    
     }    
}

/*获取编辑行*/
function getEditor(field){
	return $('#ordersgrid').datagrid('getEditor', {index:existEditIndex,field:field});	
}

/*计算当前行总金额*/
function cal(){
		var numEditor = getEditor('num');
		var num= $(numEditor.target).val();
		
		var priceEditor = getEditor('price');
		var price= $(priceEditor.target).val();
		
		var money =(num*price).toFixed(2);	
		var moneyEditor = getEditor('money');
		
	    $(moneyEditor.target).val(money); 
	    $('#ordersgrid').datagrid('getRows')[existEditIndex].money=money;
}

/*统计所有行金额*/
function sum(){
	var rows=$('#ordersgrid').datagrid('getRows');
	var total=0;
	$.each(rows,function(i,r){
		total+=parseFloat(r.money);
	})
	
	$('#ordersgrid').datagrid('reloadFooter',[{
		num: '合计',
		money:total.toFixed(2),
	}]);
	
	return total.toFixed(2);
}

/*用户输入完毕重新计算金额*/
function bindKeyUp(){
	var numEditor = getEditor('num');
	$(numEditor.target).bind('keyup',function(){
		cal();
		sum();
	})
}



