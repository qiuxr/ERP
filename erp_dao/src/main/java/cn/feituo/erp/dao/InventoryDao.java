package cn.feituo.erp.dao;
import java.util.Calendar;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.feituo.erp.dao.IInventoryDao;
import cn.feituo.erp.entity.Inventory;
/**
 * 盘盈盘亏数据访问类
 * @author Administrator
 *
 */
public class InventoryDao extends BaseDao<Inventory> implements IInventoryDao {

	/**
	 * 构建查询条件
	 * @param dep1
	 * @param dep2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Inventory inventory1,Inventory inventory2,Object param){
		DetachedCriteria dc=DetachedCriteria.forClass(Inventory.class);
		if(inventory1!=null){
			if(null != inventory1.getType() && inventory1.getType().trim().length()>0){
				dc.add(Restrictions.like("type", inventory1.getType(), MatchMode.ANYWHERE));
			}
			if(null != inventory1.getState() && inventory1.getState().trim().length()>0){
				dc.add(Restrictions.like("state", inventory1.getState(), MatchMode.ANYWHERE));
			}
			if(null != inventory1.getRemark() && inventory1.getRemark().trim().length()>0){
				dc.add(Restrictions.like("remark", inventory1.getRemark(), MatchMode.ANYWHERE));
			}
			//提交日期
			if(null != inventory1.getCreatetime()){
				dc.add(Restrictions.ge("opertime",inventory1.getCreatetime()));
			}
			if(null != inventory1.getChecktime()){
				dc.add(Restrictions.ge("opertime",inventory1.getChecktime()));
			}

		}
		if(null != inventory2){
			//结束日期
			if(null != inventory2.getCreatetime()){
				Calendar car = Calendar.getInstance();
				car.setTime(inventory2.getCreatetime());
				car.set(Calendar.HOUR, 23);//23点
				car.set(Calendar.MINUTE, 59);//59分
				car.set(Calendar.SECOND, 59);//秒
				car.set(Calendar.MILLISECOND, 999);//毫秒
				dc.add(Restrictions.le("createtime", car.getTime()));
			}
			if(null != inventory2.getChecktime()){
				Calendar car = Calendar.getInstance();
				car.setTime(inventory2.getChecktime());
				car.set(Calendar.HOUR, 23);//23点
				car.set(Calendar.MINUTE, 59);//59分
				car.set(Calendar.SECOND, 59);//秒
				car.set(Calendar.MILLISECOND, 999);//毫秒
				dc.add(Restrictions.le("checktime", car.getTime()));
			}
		}
		return dc;
	}

}
