package cn.feituo.erp.biz;
import cn.feituo.erp.entity.Orderdetail;
/**
 * 订单明细业务逻辑层接口
 * @author Administrator
 *
 */
public interface IOrderdetailBiz extends IBaseBiz<Orderdetail>{

	/**
	 * 入库
	 * @param uuid 明细的编号
	 * @param storeuuid 仓库编号
	 * @param empuuid 库管员员工编号
	 */
	void doInStore(Long uuid,Long storeuuid,Long empuuid);
	
	/**
	 * 出库
	 * @param uuid 明细的编号
	 * @param storeuuid 仓库编号
	 * @param empuuid 库管员员工编号
	 */
	void doOutStore(Long uuid, Long storeuuid, Long empuuid);
}

