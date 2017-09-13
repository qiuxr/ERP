package cn.itcast.erp.action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.feituo.erp.biz.IReturnorderdetailBiz;
import cn.feituo.erp.entity.Emp;
import cn.feituo.erp.entity.Returnorderdetail;
import cn.feituo.erp.exception.ErpException;

/**
 * 退货订单明细Action 
 * @author Administrator
 *
 */
public class ReturnorderdetailAction extends BaseAction<Returnorderdetail> {
	private static final Logger log = LoggerFactory.getLogger(ReturnorderdetailAction.class);
	private IReturnorderdetailBiz returnorderdetailBiz;

	public void setReturnorderdetailBiz(IReturnorderdetailBiz returnorderdetailBiz) {
		this.returnorderdetailBiz = returnorderdetailBiz;
		super.setBaseBiz(this.returnorderdetailBiz);
	}
	
	private Long storeuuid;
	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}
    private Long ordersuuid;
    public void setOrdersuuid(Long ordersuuid) {
		this.ordersuuid = ordersuuid;
	}
    
    /*明细出库方法的，传到BIZ登陆用户emp.getUuid()，仓库storeuuid，订单ordersuuid，明细getId()*/
	public void doOutStore(){
		
		Emp emp = getLoginUser();
		if (emp==null) {
			ajaxReturn(false, "用户未登陆");
			return;
		}
		try {		
			returnorderdetailBiz.doOutStore(getId(), emp.getUuid(), storeuuid,ordersuuid);
			ajaxReturn(true, "出库成功");
		} catch (ErpException e) {
			ajaxReturn(false, e.getMessage());	
		} catch (Exception e) {
			ajaxReturn(false, "出库失败");
			log.error("出库失败");
		}
	}
	
	 /*明细入库方法的，传到BIZ登陆用户emp.getUuid()，仓库storeuuid，订单ordersuuid，明细getId()*/
	public void doInStore(){
		 Emp emp = getLoginUser();
		 if (emp==null) {
			ajaxReturn(false, "用户未登陆");
			return;
		 }
		 try {		
			 returnorderdetailBiz.doInStore(getId(), emp.getUuid(), storeuuid,ordersuuid);
				ajaxReturn(true, "入库成功");
			} catch (ErpException e) {
				ajaxReturn(false, e.getMessage());	
			} catch (Exception e) {
				ajaxReturn(false, "入库失败");
				log.error("入库失败");
			}
	}

}
