package cn.feituo.erp.dao.impl;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.feituo.erp.dao.IGoodsDao;
import cn.feituo.erp.entity.Goods;
import cn.feituo.erp.entity.Ordersingoods;
/**
 * 商品数据访问类
 * @author Administrator
 *
 */
public class GoodsDao extends BaseDao<Goods> implements IGoodsDao {

	/**
	 * 构建查询条件
	 * @param dep1
	 * @param dep2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Goods goods1,Goods goods2,Object param){
		DetachedCriteria dc=DetachedCriteria.forClass(Goods.class);
		if(goods1!=null){
			if(null != goods1.getName() && goods1.getName().trim().length()>0){
				dc.add(Restrictions.like("name", goods1.getName(), MatchMode.ANYWHERE));
			}
			if(null != goods1.getOrigin() && goods1.getOrigin().trim().length()>0){
				dc.add(Restrictions.like("origin", goods1.getOrigin(), MatchMode.ANYWHERE));
			}
			if(null != goods1.getProducer() && goods1.getProducer().trim().length()>0){
				dc.add(Restrictions.like("producer", goods1.getProducer(), MatchMode.ANYWHERE));
			}
			if(null != goods1.getUnit() && goods1.getUnit().trim().length()>0){
				dc.add(Restrictions.like("unit", goods1.getUnit(), MatchMode.ANYWHERE));
			}
			//商品类型
			if(null != goods1.getGoodstype() && null != goods1.getGoodstype().getUuid()){
				dc.add(Restrictions.eq("goodstype", goods1.getGoodstype()));
			}

		}
		if(goods2!=null){
			if(null != goods2.getName() && goods2.getName().trim().length()>0){
				dc.add(Restrictions.eq("name", goods2.getName()));
			}
			if(null != goods2.getOrigin() && goods2.getOrigin().trim().length()>0){
				dc.add(Restrictions.eq("origin", goods2.getOrigin()));
			}
			if(null != goods2.getProducer() && goods2.getProducer().trim().length()>0){
				dc.add(Restrictions.eq("producer", goods2.getProducer()));
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
	
	@SuppressWarnings("unchecked")
	@Override
	/*视图查询多个字段值*/
	public List<Ordersingoods> listByOrdersuuid(Long ordersuuid) {
	
		return  (List<Ordersingoods>) getHibernateTemplate().find("from Ordersingoods where ouuid="+ordersuuid);
	}


}
