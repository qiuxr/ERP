package cn.feituo.erp.biz;
import cn.feituo.erp.entity.Inventory;
/**
 * 盘盈盘亏业务逻辑层接口
 * @author Administrator
 *
 */
public interface IInventoryBiz extends IBaseBiz<Inventory>{

	void doCheck(Long uuid, Long empuuid);

}

