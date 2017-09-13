package cn.feituo.erp.action;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import cn.feituo.erp.biz.IStoredetailBiz;
import cn.feituo.erp.entity.Storealert;
import cn.feituo.erp.entity.Storedetail;
import cn.feituo.erp.exception.ErpException;

/**
 * 仓库库存Action 
 * @author Administrator
 *
 */
public class StoredetailAction extends BaseAction<Storedetail> {
	
	private static final Logger log = LoggerFactory.getLogger(StoredetailAction.class);

	private IStoredetailBiz storedetailBiz;
	private Long goodsuuid;
	private Long storeuuid;

	public void setStoredetailBiz(IStoredetailBiz storedetailBiz) {
		this.storedetailBiz = storedetailBiz;
		super.setBaseBiz(this.storedetailBiz);
	}
	
	/**
	 * 库存预警
	 */
	public void storealertList(){
		List<Storealert> list = storedetailBiz.getStorealertList();
		write(JSON.toJSONString(list));
	}
	
	/**
	 * 发送库存预警邮件
	 */
	public void sendStorealert(){
		try {
			storedetailBiz.sendStorealertMail();
			ajaxReturn(true,"发送预警邮件成功");
		} catch(ErpException e){
			ajaxReturn(false,e.getMessage());
		} catch (Exception e) {
			log.error("发送邮件失败",e);
			ajaxReturn(false,"发送预警邮件失败");
		}
	}
	
	public void getnum(){
		Storedetail storedetail = new Storedetail();
		storedetail.setGoodsuuid(goodsuuid);
		storedetail.setStoreuuid(storeuuid);
		List<Storedetail> list = storedetailBiz.getList(storedetail, null, null);
		if(null == list || list.size()==0){
			storedetail.setNum(-1l);
			list.add(storedetail);
		}
		System.out.println(list.get(0).getNum());
		write(JSON.toJSONString(list));
	}

	public Long getGoodsuuid() {
		return goodsuuid;
	}

	public void setGoodsuuid(Long goodsuuid) {
		this.goodsuuid = goodsuuid;
	}

	public Long getStoreuuid() {
		return storeuuid;
	}

	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}

}
