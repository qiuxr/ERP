$(function(){
	var array =[];
	var arrayorder =[];
	$("#orderTrendGrid").datagrid({
		onLoadSuccess:function(data){
			$.each(data.rows,function(i,row){
				arrayorder[i]=row.y
			})
			//加载销售统计图
			showTrendChart(array,arrayorder);
		}
		
	})
	
	$("#reportTrendGrid").datagrid({
		columns:[[
		{field:'name',title:'月份',width:200},
		{field:'y',title:'销售退货金额',width:200}
		]],
		onLoadSuccess:function(data){
			$.each(data.rows,function(i,row){
				array[i]=row.y
			});
			
			//加载销售统计图
			showTrendChart(array,arrayorder);
			
		}
		
	})
	
	$("#btnSearch").bind('click',function(){
		var queryParam = $("#searchForm").serializeJSON();
		//提交查询条件，重新查询
		$("#reportTrendGrid").datagrid('load',queryParam);
		$("#orderTrendGrid").datagrid('load',queryParam);
	})
	
	
//默认选择当前年份
	var date = new Date();//获得当前时间
	$("#year").combobox('setValue',date.getFullYear());
//加载数据
	$("#reportTrendGrid").datagrid({
		url:'report_returnTrendCart.action',
		queryParams:{year:date.getFullYear()}//设置请求服务器的时候发送额外参数
	})
	$("#orderTrendGrid").datagrid({
		url:'report_trendReport.action',
		queryParams:{year:date.getFullYear()}//设置请求服务器的时候发送额外参数
	})
})

function showTrendChart(array,arrayorder){
	   // Set up the chart
    var chart = new Highcharts.Chart({
        chart: {
            renderTo: 'container',
            type: 'column',
            margin: 75,
            options3d: {
                enabled: true,
                alpha: 15,
                beta: 15,
                depth: 50,
                viewDistance: 25
            }
        },
        title: {
            text: '销售退货趋势分析'
        },
        subtitle: {
            text: 'ERP-team5'
        },
        plotOptions: {
            column: {
                depth: 25
            }
        },
        xAxis: {
            categories:['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
        },
        yAxis: {
            title: {
                text: 'RMB (￥)'
            }
        },
        series: [{
        	name:'销售退货金额',
            data:array 
        },{
        	name:'销售金额',
            data:arrayorder 
        }
        ],
        credits: {
	    	href: "www.itheima.com",
	    	text: "www.itheima.com"
    	},
    });

    function showValues() {
        $('#R0-value').html(chart.options.chart.options3d.alpha);
        $('#R1-value').html(chart.options.chart.options3d.beta);
    }

    // Activate the sliders
    $('#R0').on('change', function () {
        chart.options.chart.options3d.alpha = this.value;
        showValues();
        chart.redraw(false);
    });
    $('#R1').on('change', function () {
        chart.options.chart.options3d.beta = this.value;
        showValues();
        chart.redraw(false);
    });

    showValues();
}