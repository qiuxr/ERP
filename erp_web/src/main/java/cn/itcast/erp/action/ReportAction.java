package cn.itcast.erp.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import cn.feituo.erp.biz.IReportBiz;
import cn.itcast.erp.util.WebUtil;

/**
 * 报表的action
 * @author Eric
 *
 */
public class ReportAction {

	private IReportBiz reportBiz;
	
	private Date startDate;//开始日期
	private Date endDate; //结束日期
	private int year;//年份
	
	public void orderReport(){
		List<?> reportData = reportBiz.orderReport(startDate, endDate);
		WebUtil.write(JSON.toJSONString(reportData));
	}
	
	public void trendReport(){
		List<Map<String, Object>> reportData = reportBiz.trendReport(year);
		WebUtil.write(JSON.toJSONString(reportData));
	}
	
	/**
	 * 获取每年的全国销售分布数据
	 * @param year  年份
	 * @return
	 */
	public void getSaleDistribute() {
		List<Map<String, Object>> saleDistribute = reportBiz.getSaleDistribute(year);
		String jsonString = JSON.toJSONString(saleDistribute);
		WebUtil.write(jsonString);
	}
	
	/**
	 * 销售退货趋势饼图
	 */
	public void returnOrderReport(){
		List<?> reportData = reportBiz.returnOrderReport(startDate, endDate);
		WebUtil.write(JSON.toJSONString(reportData));
	}
	
	/**
	 * 销售退货趋势柱状图
	 */
	public void returnTrendCart(){
		List list = reportBiz.returnReport(year);
		String jsonString = JSON.toJSONString(list);
		WebUtil.write(jsonString);
	}
	
	
	public void setReportBiz(IReportBiz reportBiz) {
		this.reportBiz = reportBiz;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
