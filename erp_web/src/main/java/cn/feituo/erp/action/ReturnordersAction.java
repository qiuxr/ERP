package cn.feituo.erp.action;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import cn.feituo.erp.biz.IReturnordersBiz;
import cn.feituo.erp.entity.Emp;
import cn.feituo.erp.entity.Returnorderdetail;
import cn.feituo.erp.entity.Returnorders;
import cn.feituo.erp.exception.ErpException;

/**
 * 退货订单Action 
 * @author Administrator
 *
 */
public class ReturnordersAction extends BaseAction<Returnorders> {
	private static final Logger log = LoggerFactory.getLogger(ReturnordersAction.class);
	private IReturnordersBiz returnordersBiz;

	public void setReturnordersBiz(IReturnordersBiz returnordersBiz) {
		this.returnordersBiz = returnordersBiz;
		super.setBaseBiz(this.returnordersBiz);
	}
	
	private String json;
	public void setJson(String json) {
		this.json = json;
	}

	public IReturnordersBiz getReturnordersBiz() {
		return returnordersBiz;
	}


	@Override
    /*添加退货订单方法*/
	public void add(){
		/*取出登陆名*/
		 Emp emp = getLoginUser();
		 if (emp==null) {
			ajaxReturn(false, "用户未登陆");
			return;
		 }
		try {
			/*封装returnorders字段值，创建人,明细*/
			Returnorders returnorders = getT();
			returnorders.setCreater(emp.getUuid());
			List<Returnorderdetail> returnorderdetaillist = JSON.parseArray(json,Returnorderdetail.class);
			returnorders.setReturnorderdetails(returnorderdetaillist);
			returnordersBiz.add(returnorders);
			ajaxReturn(true, "添加成功");
		} catch (ErpException e) {
			ajaxReturn(false, e.getMessage());
			log.error("添加错误");
		} catch (Exception e) {
			ajaxReturn(false, "添加失败");
			log.error("添加错误");
		}
		
	}
	
	/*退货订单审核方法*/
	public void docheck(){
		 Emp emp = getLoginUser();
		 if (emp==null) {
			ajaxReturn(false, "用户未登陆");
			return;
		 }
		try {
			/*取出登陆用户empuuid,订单uuid，传到BIZ层*/
			Long empuuid=emp.getUuid();
			Long uuid=getId();
			returnordersBiz.docheck(uuid, empuuid);
			ajaxReturn(true, "审核成功");
		} catch (ErpException e) {
			ajaxReturn(false, e.getMessage());	
		} catch (Exception e) {
			ajaxReturn(false, "审核失败");
			log.error("审核失败");
		}
	}

}
