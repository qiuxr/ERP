$(function(){
	$('#grid').datagrid({
		url:'role_list',
		columns:[[
		     {field:'uuid',title:'编号',width:100},
	  		 {field:'name',title:'名称',width:100}
		]],
		singleSelect:true,
		onClickRow:function(rowIndex, rowData){
			$('#tree').tree({
				url: 'role_readRoleMenu?id=' + rowData.uuid,
				animate:true,
				checkbox:true
			});
		}
	});

	$('#btnSave').bind('click',function(){
		var nodes = $('#tree').tree('getChecked');
		var ids = [];
		$.each(nodes, function(i,node){
			//把所有的菜单编号放到ids的数组中
			ids.push(node.id);
		});
		var role = $('#grid').datagrid('getSelected');
		if(null == role){
			$.messager.alert("提示","请选择角色",'info');
			return;
		}
		$.ajax({
			url:'role_updateRoleMenu',
			data:{id:role.uuid,checkedIds:ids.toString()},
			type:'post',
			dataType:'json',
			success:function(rtn){
				$.messager.alert("提示",rtn.message,'info');
			}
		});
	});
});