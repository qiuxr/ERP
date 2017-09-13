package cn.feituo.erp.dao;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.feituo.erp.dao.IStoreDao;
import cn.feituo.erp.entity.Store;
/**
 * 仓库数据访问类
 * @author Administrator
 *
 */
public class StoreDao extends BaseDao<Store> implements IStoreDao {

	/**
	 * 构建查询条件
	 * @param dep1
	 * @param dep2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Store store1,Store store2,Object param){
		DetachedCriteria dc=DetachedCriteria.forClass(Store.class);
		if(store1!=null){
			if(null != store1.getName() && store1.getName().trim().length()>0){
				dc.add(Restrictions.like("name", store1.getName(), MatchMode.ANYWHERE));
			}
			//查询员工下的仓库
			if(null != store1.getEmpuuid()){
				dc.add(Restrictions.eq("empuuid", store1.getEmpuuid()));
			}
		}
		return dc;
	}
	
	public String getName(Long uuid){
		if(null == uuid){
			return null;
		}
		return super.get(uuid).getName();
	}

}
