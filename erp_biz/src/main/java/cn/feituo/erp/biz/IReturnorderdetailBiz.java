package cn.feituo.erp.biz;
import cn.feituo.erp.entity.Returnorderdetail;
/**
 * 退货订单明细业务逻辑层接口
 * @author Administrator
 *
 */
public interface IReturnorderdetailBiz extends IBaseBiz<Returnorderdetail>{
	void doOutStore(Long id, Long uuid, Long storeuuid,Long ordersuuid);

	void doInStore(Long id, Long uuid, Long storeuuid,Long ordersuuid);
}

