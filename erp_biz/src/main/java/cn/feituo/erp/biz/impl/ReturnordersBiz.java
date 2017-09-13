package cn.feituo.erp.biz.impl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.CacheManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import cn.feituo.erp.entity.Emp;
import cn.feituo.erp.entity.Returnorderdetail;
import cn.feituo.erp.entity.Returnorders;
import cn.feituo.erp.biz.IReturnordersBiz;
import cn.feituo.erp.dao.IEmpDao;
import cn.feituo.erp.dao.IReturnordersDao;
import cn.feituo.erp.dao.ISupplierDao;
import cn.feituo.erp.exception.ErpException;
import redis.clients.jedis.Jedis;
/**
 * 退货订单业务逻辑类
 * @author Administrator
 *
 */
public class ReturnordersBiz extends BaseBiz<Returnorders> implements IReturnordersBiz {

	private IReturnordersDao returnordersDao;
	private IEmpDao empDao;
	private ISupplierDao supplierDao;
	private CacheManager cacheManager;
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void setReturnordersDao(IReturnordersDao returnordersDao) {
		this.returnordersDao = returnordersDao;
		super.setBaseDao(this.returnordersDao);
	}

	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}
	
	private Jedis Jedis;
	public void setJedis(Jedis jedis) {
		Jedis = jedis;
	}

	/*重写add方法，添加日期，状态，金额，核对新旧订单数量*/
	@Override
	public void add(Returnorders returnorders){
		returnorders.setCreatetime(new Date());
		returnorders.setState(Returnorders.STATE_CREATE);
		
		/*从jedis查找对应KEY值的Map,KEY值为原采购或销售订单号*/
		String key ="user5_9_orders_"+returnorders.getOrdersuuid();
		Map<Long, Long> oldmap=null;
		if (null!=Jedis.get(key)) {
			oldmap = JSON.parseObject(Jedis.get(key), new TypeReference<Map<Long, Long>>(){});		
		}else{
			throw new ErpException("原订单为空,请查看");
		}
		
		
		Map<Long, Long> newmap=new HashMap<Long, Long>();
		double sumMoney=0;
		List<Returnorderdetail> returnorderdetails = returnorders.getReturnorderdetails();
		/*存储所有新增订单明细所有对应商品类型的商品数量*/
		for (Returnorderdetail returnorderdetail : returnorderdetails) {
			if (!newmap.containsKey(returnorderdetail.getGoodsuuid())){
				newmap.put(returnorderdetail.getGoodsuuid(), returnorderdetail.getNum());
			}else{
				newmap.put(returnorderdetail.getGoodsuuid(), returnorderdetail.getNum()+newmap.get(returnorderdetail.getGoodsuuid()));
			}
			/*取得订单明细遍历值金额，关联到新订单，设置订单明细状态*/
           	sumMoney+=returnorderdetail.getMoney();
			returnorderdetail.setReturnorders(returnorders);
			returnorderdetail.setState(Returnorderdetail.STATE_NOT_OUT);	
		}
		/*遍历2个新旧Map,同一商品类型比对数量，如果新订单商品数量大于原订单，报错*/
		for (Long oldkey : oldmap.keySet()) {
			if (newmap.containsKey(oldkey)) {
				Long newmapnum=newmap.get(oldkey);
				Long oldmapnum=oldmap.get(oldkey);
				if (newmapnum>oldmapnum) {
					throw new ErpException("商品数量超出原订单商品剩余数量，请核对");
				}
			}
		}
		
		/*添加总金额到订单，执行add存订单*/
		returnorders.setTotalmoney(sumMoney);
		returnordersDao.add(returnorders);
	}
	
	/*重写list方法回显Name*/
	@Override
	public List<Returnorders> getListByPage(Returnorders t1,Returnorders t2,Object param,int firstResult, int maxResults){
		List<Returnorders> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		//设置名称
		for(Returnorders o : list){
			o.setCreaterName(getEmpName(o.getCreater()));
			o.setCheckerName(getEmpName(o.getChecker()));
			o.setEnderName(getEmpName(o.getEnder()));
			o.setSupplierName(getSupplierName(o.getSupplieruuid()));
		}	
		return list;
	}
	
	/*添加审核方法，添加审核人，时间，状态，存进Dao*/
	@Override
	public void docheck(Long uuid, Long empuuid) {
		Returnorders returnorders = returnordersDao.get(uuid);
		if (!Returnorders.STATE_CREATE.equals(returnorders.getState())) {
			throw new ErpException("订单已审核");
		}
		returnorders.setChecker(empuuid);
		returnorders.setChecktime(new Date());
		returnorders.setState(Returnorders.STATE_CHECK);
		
	}
	
	private String getEmpName(Long uuid){
		if(null == uuid){
			return null;
		}
		String empName = cacheManager.getCache("myCache").get("emp_" + uuid, String.class);
		if(null == empName){
			Emp emp = empDao.get(uuid);
			empName = emp.getName();
			cacheManager.getCache("myCache").put("emp_" + uuid, empName);
		}
		return empName;
	}
	
	/**
	 * 获取供应商名称
	 * @param uuid
	 * @param supplierNameMap 供应商编号与名称的缓存
	 * @return
	 */
	private String getSupplierName(Long uuid){
		if(null == uuid){
			return null;
		}
		String supplierName = cacheManager.getCache("myCache").get("supplier_" + uuid, String.class);
		if(null == supplierName){
			supplierName = supplierDao.get(uuid).getName();
			cacheManager.getCache("myCache").put("supplier_" + uuid, supplierName);
		}
		return supplierName;
	}
	
}
