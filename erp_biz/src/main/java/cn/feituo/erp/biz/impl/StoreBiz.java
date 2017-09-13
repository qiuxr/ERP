package cn.feituo.erp.biz.impl;
import cn.feituo.erp.entity.Store;
import cn.feituo.erp.biz.IStoreBiz;
import cn.feituo.erp.dao.IStoreDao;
/**
 * 仓库业务逻辑类
 * @author Administrator
 *
 */
public class StoreBiz extends BaseBiz<Store> implements IStoreBiz {

	private IStoreDao storeDao;
	
	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
		super.setBaseDao(this.storeDao);
	}
	
}
