package cn.feituo.erp.biz;
import java.util.List;

import cn.feituo.erp.entity.Storealert;
import cn.feituo.erp.entity.Storedetail;
/**
 * 仓库库存业务逻辑层接口
 * @author Administrator
 *
 */
public interface IStoredetailBiz extends IBaseBiz<Storedetail>{

	/**
	 * 商品库存预警列表
	 * @return
	 */
	List<Storealert> getStorealertList();
	
	/**
	 * 发送预警邮件
	 */
	void sendStorealertMail() throws Exception;
}

