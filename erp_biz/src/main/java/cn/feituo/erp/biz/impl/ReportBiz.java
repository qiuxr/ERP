package cn.feituo.erp.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.feituo.erp.biz.IReportBiz;
import cn.feituo.erp.dao.IReportDao;
import cn.feituo.erp.util.ProvinceUtils;

public class ReportBiz implements IReportBiz {
	
	private IReportDao reportDao;

	@Override
	public List<?> orderReport(Date startDate, Date endDate) {
		return reportDao.orderReport(startDate, endDate);
	}

	public void setReportDao(IReportDao reportDao) {
		this.reportDao = reportDao;
	}

	public List<Map<String, Object>> trendReport(int year) {
		List<Map<String, Object>> monthData = this.reportDao.getSumMoney(year);
		//key=月份, value= 数据
		//把数据库存在的月份的数据转成key value格式，存map里去,
		// mapMonthData的key就是月份
		Map<Integer, Map<String,Object>> mapMonthData = new HashMap<Integer, Map<String,Object>>();
		for(Map<String, Object> data : monthData){
			mapMonthData.put((Integer)data.get("name"), data);
		}
		
		//补充月份数据，
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = null;
		for(int i = 1; i<=12; i++){
			//如果判断
			//如果不存在月份数据
			data = mapMonthData.get(i);
			if(null == data){
				data = new HashMap<String,Object>();
				data.put("name", i);
				data.put("y", 0);
				result.add(data);
			}else{
				//存在月份数据
				result.add(data);
			}
		}
		
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see cn.feituo.erp.biz.IReportBiz#getSaleDistribute(int)
	 */
	@Override
	public List<Map<String, Object>> getSaleDistribute(int year) {
		Map<String, Object> provinceMap = (Map<String, Object>) ProvinceUtils.getProvinceMap();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Double> newProvinceMap = reportDao.getSaleDistribute(year, provinceMap); 
		Map<String, Double> sortByValue = ProvinceUtils.sortByValue(newProvinceMap);
		for (String str : sortByValue.keySet()) {
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("name", str);
			hashMap.put("y", sortByValue.get(str));
			resultList.add(hashMap);
		}
		return resultList;
	}
	
	public static void main(String[] args) {
		List<Map<String, Object>> monthData = new ArrayList<Map<String,Object>>();
		Map<String, Object> month = new HashMap<String, Object>();
		month.put("name", 7);
		month.put("y", 9527);
		monthData.add(month);
		
		String data = "[{\"name\":1,\"y\":B0B},{\"name\":2,\"y\":B0B},{\"name\":3,\"y\":B0B},{\"name\":4,\"y\":B0B},{\"name\":5,\"y\":B0B},{\"name\":6,\"y\":B0B},{\"name\":7,\"y\":B0B},{\"name\":8,\"y\":B0B},{\"name\":9,\"y\":B0B},{\"name\":10,\"y\":B0B},{\"name\":11,\"y\":B0B},{\"name\":12,\"y\":B0B}]";
		String repBefore = "\\{\"name\":%d,\"y\":B0B\\}";
		String after = "{\"name\":%d,\"y\":%d}";
		for(Map<String, Object> d : monthData){
			//{name:7,y:B0B}=>{name:7,y:9527}
			data = data.replaceAll(String.format(repBefore, d.get("name")), String.format(after, d.get("name"),d.get("y")));
		}
		System.out.println(data.replaceAll("B", ""));
	}
	
	
	@Override
	public List returnReport(int year) {
		List<Map<String,Object>> monthlist = reportDao.getReturnSumMoney(year);
		//把monthlist集合中的月份遍历出来，解析是否有有月份缺省，缺省部分的进行添加数据处理
		Map<Integer,Map<String,Object>> monthdata = new HashMap<Integer,Map<String,Object>>();
	
		for (Map<String, Object> map : monthlist) {
			//把月份作为key，map做为value重新添加到一个集合中去
			monthdata.put((Integer)map.get("name"), map);
		}
		//创建list集合，接收要传给前端的数据
				List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
				Map<String,Object> data  = null;
				for(int i=1;i<=12;i++){
					data = monthdata.get(i);
					if(data==null){
						//没有数据的月份补充数据
						data = new HashMap<>();
						data.put("name",i+"月" );//补充没有月份的数据
						data.put("y", 0);//补充没有月份的销售额的数据
						result.add(data);
					}else {
						data.put("name", data.get("name")+"月");
						result.add(data);//添加到list集合，满足前端数据格式：[{name:月份,y:销售额},....]
					}
				}
				
				return result;
	}

	@Override
	public List<?> returnOrderReport(Date startDate, Date endDate) {
		return  reportDao.returnOrderReport(startDate,endDate);
	}
	
	


}
