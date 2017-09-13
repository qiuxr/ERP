package cn.feituo.erp.biz.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.feituo.erp.entity.Storeoper;
import cn.feituo.erp.biz.IStoreoperBiz;
import cn.feituo.erp.dao.IEmpDao;
import cn.feituo.erp.dao.IGoodsDao;
import cn.feituo.erp.dao.IStoreDao;
import cn.feituo.erp.dao.IStoreoperDao;
/**
 * 仓库操作记录业务逻辑类
 * @author Administrator
 *
 */
public class StoreoperBiz extends BaseBiz<Storeoper> implements IStoreoperBiz {

	private IStoreoperDao storeoperDao;
	private IEmpDao empDao;
	private IGoodsDao goodsDao;
	private IStoreDao storeDao;
	
	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
		super.setBaseDao(this.storeoperDao);
	}
	
	/**
	 * 条件查询
	 * @param t1
	 * @return
	 */
	public List<Storeoper> getListByPage(Storeoper t1,Storeoper t2,Object param,int firstResult, int maxResults){
		List<Storeoper> list = super.getListByPage(t1,t2,param,firstResult, maxResults);
		Map<Long,String> goodsNameMap = new HashMap<Long, String>();
		Map<Long,String> storeNameMap = new HashMap<Long, String>();
		Map<Long,String> empNameMap = new HashMap<Long, String>();
		for(Storeoper so : list){
			so.setEmpName(getName(so.getEmpuuid(),empNameMap,empDao));
			so.setStoreName(getName(so.getStoreuuid(),storeNameMap,storeDao));
			so.setGoodsName(getName(so.getGoodsuuid(),goodsNameMap,goodsDao));
		}
		return list;
	}
	
	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
	}
	
}
