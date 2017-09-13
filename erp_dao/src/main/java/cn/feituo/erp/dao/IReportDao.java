package cn.feituo.erp.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表Dao
 *
 */
public interface IReportDao {

	/**
	 * 销售统计
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<?> orderReport(Date startDate, Date endDate);
	
	/**
	 * 获取指定年份的销售额
	 * @param year
	 * @return
	 */
	List<Map<String,Object>> getSumMoney(int year);

	/**
	 * 获取每年的全国销售分布数据
	 * @param year  年份
	 * @param provinceMap  省份的数据
	 * @return
	 */
	Map<String, Double> getSaleDistribute(int year, Map<String, Object> provinceMap);
	
	/**
	 * 销售退货统计
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<?> returnOrderReport(Date startDate, Date endDate);

	List<Map<String, Object>> getReturnSumMoney(int year);
}
