$(function(){
	$("#reportGrid").datagrid({
		url:'report_returnPieReport.action',
		columns:[[
		{field:'name',title:'商品类别',width:200},
		{field:'y',title:'销售额',width:200}
		]],
		
		onLoadSuccess:function(data){
			//加载销售统计图
			showPieChart(data.rows);
		}
	
	})
	
	$("#btnSearch").bind('click',function(){
		var formdata = $("#searchForm").serializeJSON();
		//日期转化,不为空就把截至日期的最后一天加上
		if(formdata.date2!=null){
			formdata['endDate']=formdata['endDate']+" 23:59:59";
		}
		$("#reportGrid").datagrid('load',formdata);
		
		
	})
	
	
})

//饼状图显示方法
function showPieChart(data){
	 $('#pieChart').highcharts({
	        chart: {
	            plotBackgroundColor: '#eee',
	            plotBorderWidth: null,
	            plotShadow: false,
	            type: 'pie'
	        },
	        title: {
	            text: '退货销售统计图'
	        },
	        tooltip: {
	            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
	        },
	        plotOptions: {
	            pie: {
	                allowPointSelect: true,
	                cursor: 'pointer',
	                dataLabels: {
	                    enabled: true,
	                    format: '<b>{point.name}</b>: {point.percentage:.1f} %',
	                    style: {
	                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
	                    }
	                }
	            }
	        },
	        series: [{
	            name: "比例",
	            colorByPoint: true,
	            data:data
	        }]
	    });
}