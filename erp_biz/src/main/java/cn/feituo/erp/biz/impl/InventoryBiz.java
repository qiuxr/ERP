package cn.feituo.erp.biz.impl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import cn.feituo.erp.entity.Emp;
import cn.feituo.erp.entity.Inventory;
import cn.feituo.erp.entity.Orders;
import cn.feituo.erp.biz.IInventoryBiz;
import cn.feituo.erp.dao.IEmpDao;
import cn.feituo.erp.dao.IGoodsDao;
import cn.feituo.erp.dao.IInventoryDao;
import cn.feituo.erp.dao.IStoreDao;
import cn.feituo.erp.exception.ErpException;
/**
 * 盘盈盘亏业务逻辑类
 * @author Administrator
 *
 */
public class InventoryBiz extends BaseBiz<Inventory> implements IInventoryBiz {

	private IInventoryDao inventoryDao;
	private IGoodsDao goodsDao;
	private IStoreDao storeDao;
	private IEmpDao empDao;
	
	public void setInventoryDao(IInventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
		super.setBaseDao(this.inventoryDao);
	}
	
	public void add(Inventory inventory){
		Long count = inventory.getArgnum()-inventory.getTruenum();
		if (count>=0) {
			inventory.setType(Inventory.TYPE_DOWN);
			inventory.setNum(count);
		}else {
			inventory.setType(Inventory.TYPE_UP);
			inventory.setNum(count*-1);
		}
		inventory.setCreatetime(new Date());
		Emp loginUser = (Emp)SecurityUtils.getSubject().getPrincipal();
		inventory.setCreater(loginUser.getUuid());
		inventory.setState(Inventory.CHECK_NO);
		inventoryDao.add(inventory);
	}
	
	@Override
	public List<Inventory> getListByPage(Inventory t1, Inventory t2, Object param, int firstResult, int maxResults) {
		List<Inventory> list = super.getListByPage(t1,t2,param,firstResult, maxResults);
		Map<Long,String> goodsNameMap = new HashMap<Long, String>();
		Map<Long,String> storeNameMap = new HashMap<Long, String>();
		Map<Long,String> createrNameMap = new HashMap<Long, String>();
		Map<Long,String> checkerkNameMap = new HashMap<Long, String>();
		for(Inventory inventory : list){
			inventory.setGoodsName(getName(inventory.getGoodsuuid(),goodsNameMap,goodsDao));
			inventory.setStoreName(getName(inventory.getStoreuuid(),storeNameMap,storeDao));
			inventory.setCreaterName(getName(inventory.getCreater(),createrNameMap,empDao));
			inventory.setCheckerName(getName(inventory.getChecker(),checkerkNameMap,empDao));
		}
		return list;
	}

	
	@Override
	public void doCheck(Long uuid, Long empuuid) {
		//获取订单进入持久化状态
		Inventory inventory = inventoryDao.get(uuid);
		//1. 判断状态是否为未审核
		if(!Inventory.CHECK_NO.equals(inventory.getState())){
			throw new ErpException("该订单已经审核过了");
		}
		//2. 修改状态为 已审核
		inventory.setState(Inventory.CHECK_YES);
		//3. 审核时间
		inventory.setChecktime(new Date());
		//4. 审核人
		inventory.setChecker(empuuid);
	}
	
	
	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
	}

	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	
	
}
