package cn.feituo.erp.dao;

import java.util.List;

import cn.feituo.erp.entity.Goods;
import cn.feituo.erp.entity.Ordersingoods;
/**
 * 商品数据访问接口
 * @author Administrator
 *
 */
public interface IGoodsDao extends IBaseDao<Goods>{

	List<Ordersingoods> listByOrdersuuid(Long ordersuuid);

}
