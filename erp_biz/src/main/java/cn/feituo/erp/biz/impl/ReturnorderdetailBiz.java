package cn.feituo.erp.biz.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import cn.feituo.erp.entity.Orderdetail;
import cn.feituo.erp.entity.Returnorderdetail;
import cn.feituo.erp.entity.Returnorders;
import cn.feituo.erp.entity.Storedetail;
import cn.feituo.erp.entity.Storeoper;
import cn.feituo.erp.biz.IReturnorderdetailBiz;
import cn.feituo.erp.dao.IReturnorderdetailDao;
import cn.feituo.erp.dao.IStoredetailDao;
import cn.feituo.erp.dao.IStoreoperDao;
import cn.feituo.erp.exception.ErpException;
import redis.clients.jedis.Jedis;
/**
 * 退货订单明细业务逻辑类
 * @author Administrator
 *
 */
public class ReturnorderdetailBiz extends BaseBiz<Returnorderdetail> implements IReturnorderdetailBiz {
	private IStoredetailDao storedetailDao;
	private IStoreoperDao storeoperDao;
	private IReturnorderdetailDao returnorderdetailDao;
	public void setReturnorderdetailDao(IReturnorderdetailDao returnorderdetailDao) {
		this.returnorderdetailDao = returnorderdetailDao;
		super.setBaseDao(this.returnorderdetailDao);
	}
	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
	}
	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
	}
	
	private Jedis jedis;
	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}
	/*采购退货出库业务层方法*/
	@Override
	public void doOutStore(Long uuid,Long empuuid,Long storeuuid,Long ordersuuid){
		 Returnorderdetail returnorderdetail = returnorderdetailDao.get(uuid);
		
		if (!Returnorderdetail.STATE_NOT_OUT.equals(returnorderdetail.getState())) {
			throw new ErpException("该商品以出库");
		}
		/*更新出库订单明细，出库状态，出库人，出库日期，仓库名*/
		returnorderdetail.setState(Returnorderdetail.STATE_OUT);
		returnorderdetail.setEnder(empuuid);
		returnorderdetail.setEndtime(new Date());
		returnorderdetail.setStoreuuid(storeuuid);
		
		/*更新仓库明细*/
		Storedetail storedetail = new Storedetail();
		storedetail.setStoreuuid(storeuuid);
		storedetail.setGoodsuuid(returnorderdetail.getGoodsuuid());
		List<Storedetail> storedetailList = storedetailDao.getList(storedetail, null, null);
		if (null!=storedetailList && storedetailList.size()>0) {
			Storedetail oldstoredetail = storedetailList.get(0);
			Long nums= oldstoredetail.getNum()-returnorderdetail.getNum();
			if (nums<=0) {
				throw new ErpException("您所选择的仓库库存不足");
			}
			oldstoredetail.setNum(nums);
		}else{
			throw new ErpException("库存不足");
		}
		/*更新仓库变更*/
		Storeoper storeoper = new Storeoper();
		storeoper.setEmpuuid(empuuid);
		storeoper.setGoodsuuid(returnorderdetail.getGoodsuuid());
		storeoper.setNum(returnorderdetail.getNum());
		storeoper.setOpertime(returnorderdetail.getEndtime());
		storeoper.setStoreuuid(storeuuid);
		storeoper.setType(Storeoper.TYPE_OUT);
		storeoperDao.add(storeoper);
		
		/*更新订单状态*/
		Returnorders returnorders = returnorderdetail.getReturnorders();
		Returnorderdetail countparam = new Returnorderdetail();
		countparam.setReturnorders(returnorders);
		countparam.setState(Returnorderdetail.STATE_NOT_OUT);
		long count = returnorderdetailDao.getCount(countparam, null, null);
		List<Returnorderdetail> list2 = returnorderdetailDao.getList(countparam, null, null);
	    System.out.println(list2.toArray());
	    
		if (count==0) {
			returnorders.setState(Returnorders.STATE_END);
			returnorders.setEnder(empuuid);
			returnorders.setEndtime(returnorderdetail.getEndtime());
		}
		
		/*退货成功后原订单数量减去退货订单值，更新原订单商品数量，存到Map中，并存进jedis*/
		Map<Long, Long> oldmap =null;
		String key ="user5_9_orders_"+ordersuuid;
		if (null!=jedis.get(key)) {
			oldmap = JSON.parseObject(jedis.get(key), new TypeReference<Map<Long, Long>>(){});
		}else{
			throw new ErpException("原订单不能为空，请核对");
		}
		
		if (oldmap.containsKey(returnorderdetail.getGoodsuuid())) {
			Long oldGoodsnum =oldmap.get(returnorderdetail.getGoodsuuid());
			Long newGoodsnum =oldGoodsnum-returnorderdetail.getNum();
			if (newGoodsnum>=0) {
				oldmap.put(returnorderdetail.getGoodsuuid(), newGoodsnum);
			}else{
				throw new ErpException("原采购订单异常");
			}
		}
		jedis.set(key, JSON.toJSONString(oldmap));
		
	}
	
	
	
	
	
	
	/*销售退货入库业务层方法*/
	public void doInStore(Long uuid,Long empuuid,Long storeuuid,Long ordersuuid){
		Returnorderdetail returnorderdetail = returnorderdetailDao.get(uuid);
		if (!Returnorderdetail.STATE_NO_IN.equals(returnorderdetail.getState())) {
			new ErpException("该商品以出库");
		}
		returnorderdetail.setState(Orderdetail.STATE_IN);
		returnorderdetail.setEnder(empuuid);
		returnorderdetail.setEndtime(new Date());
		returnorderdetail.setStoreuuid(storeuuid);
		
		/*更新仓库明细*/
		Storedetail storedetail = new Storedetail();
		storedetail.setGoodsuuid(returnorderdetail.getGoodsuuid());
		storedetail.setStoreuuid(storeuuid);
		List<Storedetail> list = storedetailDao.getList(storedetail, null, null);
		if (null!=list && list.size()>0) {
			Storedetail oldstoredetail = list.get(0);
			Long nums= oldstoredetail.getNum()+returnorderdetail.getNum();
			oldstoredetail.setNum(nums);
		}else{
			storedetail.setNum(returnorderdetail.getNum());
			storedetailDao.add(storedetail);
		}
		
		/*更新仓库变更*/
		Storeoper storeoper = new Storeoper();
		storeoper.setEmpuuid(empuuid);
		storeoper.setGoodsuuid(returnorderdetail.getGoodsuuid());
		storeoper.setNum(returnorderdetail.getNum());
		storeoper.setOpertime(new Date());
		storeoper.setStoreuuid(storeuuid);
		storeoper.setType(Storeoper.TYPE_IN);
		storeoperDao.add(storeoper);
		
		/*更新订单状态*/
		Returnorders returnorders = returnorderdetail.getReturnorders();
		Returnorderdetail countparam = new Returnorderdetail();
		countparam.setReturnorders(returnorders);
		countparam.setState(Returnorderdetail.STATE_NO_IN);
		long count = returnorderdetailDao.getCount(countparam, null, null);
		
		if (count==0) {
			returnorders.setState(Returnorders.STATE_END);
			returnorders.setEnder(empuuid);
			returnorders.setEndtime(returnorderdetail.getEndtime());
		}
		
		/*退货成功后原订单数量减去退货订单值，更新原订单商品数量，存到Map中，并存进jedis*/
		Map<Long, Long> formap =null;
		String key ="user5_9_orders_"+ordersuuid;
		if (null!=jedis.get(key)) {
			formap = JSON.parseObject(jedis.get(key), new TypeReference<Map<Long, Long>>(){});
		}else{
			throw new ErpException("原订单不能为空，请核对");
		}
		
		if (formap.containsKey(returnorderdetail.getGoodsuuid())) {
			Long oldGoodsnum =formap.get(returnorderdetail.getGoodsuuid());
			Long newGoodsnum =oldGoodsnum-returnorderdetail.getNum();
			if (newGoodsnum>=0) {
				formap.put(returnorderdetail.getGoodsuuid(), newGoodsnum);
			}else{
				throw new ErpException("原销售订单异常");
			}
		}
		jedis.set(key, JSON.toJSONString(formap));

	}
	
	
	
}
