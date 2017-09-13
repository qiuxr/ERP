var year ="";
//提交的方法名称
$(function(){
	var date = new Date();
	$('#yearid').combobox('select',date.getFullYear());
	//加载表格数据
	$('#grid').datagrid({
		url:'report_getSaleDistribute',
		columns:[[
	  		    {field:'name',title:'省份',width:100},
	  		    {field:'y',title:'销售额',width:100,formatter:moneyFormat}
				]],
		singleSelect: true,
		fitColumns:true,
		queryParams:{'year':date.getFullYear()},
		onLoadSuccess:function(data) {
			year = $('#yearid').combobox('getValue');
			showCharts(data.rows);
		}
	});
	
	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load',formData);
	});

});

function moneyFormat(value) {
	if(value > 0) {
		return value.toFixed(2) + "元";
	}else {
		return value + "元";
	}
	
}

function showCharts(allData) {
	var myChart = echarts.init(document.getElementById('charts'));
	var salDataList = new Array();
	$.each(allData, function(i, data) {
		salDataList.push({
			name: data.name,
			value: data.y
		});
	});
	
	function isZero() {
		if(allData[0].y == 0){
			return 10000;
		}else {
			return allData[0].y
		}
	}
	option = {
		    title: {
		        text: year + '年度全国销售额分布图',
		        subtext: '',
		        left: 'center'
		    },
		    tooltip: {
		        trigger: 'item'
		    },
		    legend: {
		        orient: 'vertical',
		        left: 'left',
		        data:['']
		    },
		    visualMap: {
		        min: 0,
		        max: isZero(),
		        left: 'left',
		        top: 'bottom',
		        text: ['高','低'],           // 文本，默认为数值文本
		        calculable: true
		    },
		    toolbox: {
		        show: true,
		        orient: 'vertical',
		        left: 'right',
		        top: 'center',
		        feature: {
		            dataView: {readOnly: false},
		            restore: {},
		            saveAsImage: {}
		        }
		    },
		    series: [
		        {
		            name: '销售额(元)',
		            type: 'map',
		            mapType: 'china',
		            roam: false,
		            label: {
		                normal: {
		                    show: true
		                },
		                emphasis: {
		                    show: true
		                }
		            },
		            data:salDataList
		        }
		    ]
		};
	myChart.setOption(option);
	
}