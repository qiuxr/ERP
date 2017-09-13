package cn.feituo.erp.dao;

import java.util.List;

import cn.feituo.erp.entity.Storealert;
import cn.feituo.erp.entity.Storedetail;
/**
 * 仓库库存数据访问接口
 * @author Administrator
 *
 */
public interface IStoredetailDao extends IBaseDao<Storedetail>{

	/**
	 * 商品库存预警列表
	 * @return
	 */
	List<Storealert> getStorealertList();
}
