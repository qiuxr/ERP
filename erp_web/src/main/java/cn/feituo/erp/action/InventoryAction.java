package cn.feituo.erp.action;
import java.util.List;

import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.feituo.erp.biz.IInventoryBiz;
import cn.feituo.erp.biz.IStoredetailBiz;
import cn.feituo.erp.entity.Emp;
import cn.feituo.erp.entity.Inventory;
import cn.feituo.erp.entity.Storedetail;
import cn.feituo.erp.exception.ErpException;

/**
 * 盘盈盘亏Action 
 * @author Administrator
 *
 */
public class InventoryAction extends BaseAction<Inventory> {

	
	private static final Logger log = LoggerFactory.getLogger(OrdersAction.class);
	private IInventoryBiz inventoryBiz;
	private IStoredetailBiz storedetailBiz;
	
	public void setInventoryBiz(IInventoryBiz inventoryBiz) {
		this.inventoryBiz = inventoryBiz;
		super.setBaseBiz(this.inventoryBiz);
	}
	
	/**
	 * 盈亏审核
	 */
	public void doCheck(){
		//当前登陆用户
		Emp loginUser = getLoginUser();
		if(null == loginUser){
			ajaxReturn(false,"您 还没有登陆");
			return;
		}
		//获取页面传过来的编号
		Long uuid = getId();
		try {
			inventoryBiz.doCheck(uuid, loginUser.getUuid());
			Inventory inventory = inventoryBiz.get(uuid);
			Long goodsuuid = inventory.getGoodsuuid();
			Long storeuuid = inventory.getStoreuuid();
			Storedetail storedetail = new Storedetail();
			storedetail.setGoodsuuid(goodsuuid);
			storedetail.setStoreuuid(storeuuid);
			List<Storedetail> list = storedetailBiz.getList(storedetail, null, null);
			Storedetail storedetail2 = list.get(0);
			if("0".equals(inventory.getType())){
				storedetail2.setNum(storedetail2.getNum()+inventory.getNum());
			}else{
				storedetail2.setNum(storedetail2.getNum()-inventory.getNum());
			}
			storedetailBiz.update(storedetail2);
			
			ajaxReturn(true,"订单审核成功");
		} catch(ErpException e){
			log.error("订单审核失败",e);
			ajaxReturn(false,e.getMessage());
		} catch(UnauthorizedException e){
			ajaxReturn(false,"没有权限");
		} catch (Exception e) {
			log.error("订单审核失败",e);
			ajaxReturn(false,"订单审核失败");
		}
	}


	public void setStoredetailBiz(IStoredetailBiz storedetailBiz) {
		this.storedetailBiz = storedetailBiz;
	}
}
