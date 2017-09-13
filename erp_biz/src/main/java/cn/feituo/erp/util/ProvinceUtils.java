package cn.feituo.erp.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProvinceUtils {

	private static Map<String, Object> provinceMap = new HashMap<String, Object>();
	static {
		provinceMap.put("北京", 0d);
		provinceMap.put("天津", 0d);
		provinceMap.put("上海", 0d);
		provinceMap.put("重庆", 0d);
		provinceMap.put("河北", 0d);
		provinceMap.put("河南", 0d);
		provinceMap.put("山东", 0d);
		provinceMap.put("江苏", 0d);
		provinceMap.put("云南", 0d);
		provinceMap.put("辽宁", 0d);
		provinceMap.put("黑龙江", 0d);
		provinceMap.put("湖南", 0d);
		provinceMap.put("安徽", 0d);
		provinceMap.put("新疆", 0d);
		provinceMap.put("浙江", 0d);
		provinceMap.put("江西", 0d);
		provinceMap.put("湖北", 0d);
		provinceMap.put("广西", 0d);
		provinceMap.put("甘肃", 0d);
		provinceMap.put("山西", 0d);
		provinceMap.put("内蒙古", 0d);
		provinceMap.put("陕西", 0d);
		provinceMap.put("吉林", 0d);
		provinceMap.put("福建", 0d);
		provinceMap.put("贵州", 0d);
		provinceMap.put("广东", 0d);
		provinceMap.put("青海", 0d);
		provinceMap.put("西藏", 0d);
		provinceMap.put("四川", 0d);
		provinceMap.put("宁夏", 0d);
		provinceMap.put("海南", 0d);
		provinceMap.put("台湾", 0d);
		provinceMap.put("香港", 0d);
		provinceMap.put("澳门", 0d);
	}

	public static Map<String, Object> getProvinceMap() {
		return provinceMap;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
