$(function(){
	$('#grid').datagrid({
		url: 'report_returnOrderReport',
		columns:[[
		    {field:'name',title:'商品类型',width:100},
		    {field:'y',title:'退货额',width:100}
		]],
		onLoadSuccess:function(_data){
			//在数据加载成功的时候触发。
			var rows = _data.rows;
			for(var i = 0; i < rows.length; i++){
				rows[i].colorIndex = i;
			}
			showChart(_data.rows);
		}
	});
	
	//查询
	$('#btnSearch').bind('click',function(){
		var queryParam = $('#searchForm').serializeJSON();
		if(queryParam.endDate != ''){
			queryParam.endDate += " 23:59:59";
		}
		$('#grid').datagrid('load',queryParam);
	});
	//showChart();
});

function showChart(_data){
	var colors = ['#E066FF', '#32CD32','#EEEE00'];
	
	$('#pieChart').highcharts({
        chart: {
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 45,
                beta: 0
            }
        },
        title: {
            text: '销售退货统计报表'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                depth: 35,
                dataLabels: {
                    enabled: true,
                    format: '{point.name}'
                },
                showInLegend: true
            }
        },
        colors:colors,
        series: [{
            type: 'pie',
            name: '比例',
            data: _data
        }],
        credits: {
	    	href: "www.itheima.com",
	    	text: "www.itheima.com"
    	}
    });
}