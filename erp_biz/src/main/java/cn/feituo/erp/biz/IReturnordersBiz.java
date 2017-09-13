package cn.feituo.erp.biz;
import cn.feituo.erp.entity.Returnorders;
/**
 * 退货订单业务逻辑层接口
 * @author Administrator
 *
 */
public interface IReturnordersBiz extends IBaseBiz<Returnorders>{
	void docheck(Long uuid, Long empuuid);

}

