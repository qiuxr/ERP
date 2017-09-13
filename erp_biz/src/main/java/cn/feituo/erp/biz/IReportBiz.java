package cn.feituo.erp.biz;


import java.util.Date;
import java.util.List;
import java.util.Map;


public interface IReportBiz {


	List<?> orderReport(Date startDate, Date endDate);
	
	List<Map<String,Object>> trendReport(int year);
	
	
	/**
	 * 获取每年的全国销售分布数据
	 * @param year  年份
	 * @return
	 */
	List<Map<String, Object>> getSaleDistribute(int year);
	
	/**
	 * 销售退货统计
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<?> returnOrderReport(Date startDate, Date endDate);


	/**年销售退货金额
	 * @param year
	 * @return
	 */
	List<?> returnReport(int year);

	
}