package cn.feituo.erp.dao;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import cn.feituo.erp.dao.IOrdersDao;
import cn.feituo.erp.entity.Orders;
/**
 * 订单数据访问类
 * @author Administrator
 *
 */
public class OrdersDao extends BaseDao<Orders> implements IOrdersDao {

	/**
	 * 构建查询条件
	 * @param dep1
	 * @param dep2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Orders orders1,Orders orders2,Object param){
		DetachedCriteria dc=DetachedCriteria.forClass(Orders.class);
		if(orders1!=null){
			if(null != orders1.getType() && orders1.getType().trim().length()>0){
				dc.add(Restrictions.eq("type", orders1.getType()));
			}
			if(null != orders1.getState() && orders1.getState().trim().length()>0){
				dc.add(Restrictions.eq("state", orders1.getState()));
			}
			//根据创建者查询
			if(null != orders1.getCreater()){
				dc.add(Restrictions.eq("creater", orders1.getCreater()));
			}
		}
		return dc;
	}

}