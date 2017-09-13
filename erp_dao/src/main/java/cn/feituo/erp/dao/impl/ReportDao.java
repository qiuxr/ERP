package cn.feituo.erp.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import cn.feituo.erp.dao.IReportDao;

public class ReportDao extends HibernateDaoSupport implements IReportDao {

    @Override
    public List<?> orderReport(Date startDate, Date endDate) {
	String hql = "select new Map(gt.name as name,sum(od.money) as y) "
		+ "from Goodstype gt, Orderdetail od, Orders o, Goods g "
		+ "where g.goodstype=gt and g.uuid=od.goodsuuid " + "and od.orders = o and o.type='2' ";
	List<Date> queryParam = new ArrayList<Date>();
	if (null != startDate) {
	    queryParam.add(startDate);
	    hql += "and o.createtime>=? ";
	}
	if (null != endDate) {
	    queryParam.add(endDate);
	    hql += "and o.createtime<=? ";
	}
	hql += "group by gt.name";
	return this.getHibernateTemplate().find(hql, queryParam.toArray());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> getSumMoney(int year) {
	String hql = "select new Map(month(o.createtime) as name,sum(od.money) as y) " + "from Orderdetail od,Orders o "
		+ "where od.orders =o " + "and o.type='2' and year(o.createtime)=? " + "group by month(o.createtime)";
	return (List<Map<String, Object>>) this.getHibernateTemplate().find(hql, year);
    }

    // 销售退货金额
    @Override
    public List<Map<String, Object>> getReturnSumMoney(int year) {
	String hql = "select new Map(month(r.createtime) as name,sum(rd.money) as y) from Returnorders r,	Returnorderdetail rd where rd.returnorders=r and r.type='2' and r.state='2' and year(r.createtime)=? group by month(r.createtime)";
	return (List<Map<String, Object>>) getHibernateTemplate().find(hql, year);
    }

    // 销售退货分类统计金额
    @Override
    public List<?> returnOrderReport(Date startDate, Date endDate) {
	String hql = "select new Map(t.name as name,sum(d.money) as y)from Returnorders o,Returnorderdetail d,Goods g,Goodstype t "
		+ "where g.goodstype=t and d.returnorders=o and d.goodsuuid=g.uuid and o.type='2' and o.state='2'";
	List<Date> querryParam = new ArrayList<Date>();
	if (startDate != null) {
	    hql += "and o.createtime>=? ";
	    querryParam.add(startDate);
	}
	if (endDate != null) {
	    hql += "and o.createtime<=? ";
	    querryParam.add(endDate);
	}
	hql += "group by t.name ";
	return getHibernateTemplate().find(hql, querryParam.toArray());
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Double> getSaleDistribute(int year,Map<String, Object> provinceMap) {
		Map<String, Double> newProvinceMap = new HashMap<String, Double>();
		HibernateTemplate hibernateTemplate2 = getHibernateTemplate();
		for (String province : provinceMap.keySet()) {
			String hql = "select new Map('"+ province +"' as province,sum(od.money) as total) "
					+ "from Supplier sr,Orders os,Orderdetail od "
					+ "where sr.uuid = os.supplieruuid and os = od.orders "
					+ "and sr.type = '2' and sr.address like '" + province + "%' and year(os.createtime) = ?";
			List<Map<String, Object>> findList = (List<Map<String, Object>>) hibernateTemplate2.find(hql, year);
			if(null != findList && findList.size() > 0 && null != findList.get(0).get("total")) {
				newProvinceMap.put(province, (Double) findList.get(0).get("total"));
			}else {
				newProvinceMap.put(province,0d);
			}
		}
		return newProvinceMap; 
	}

}