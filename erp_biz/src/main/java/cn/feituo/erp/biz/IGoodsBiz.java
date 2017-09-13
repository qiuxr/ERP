package cn.feituo.erp.biz;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;

import cn.feituo.erp.entity.Goods;
import cn.feituo.erp.entity.Ordersingoods;
/**
 * 商品业务逻辑层接口
 * @author Administrator
 *
 */
public interface IGoodsBiz extends IBaseBiz<Goods>{

	/**
	 * 商品导入
	 * @param fileInputStream
	 * @throws Exception
	 */
	void doImport(FileInputStream fileInputStream) throws Exception;

	/**
	 * 商品导出
	 * @param os
	 * @param t1
	 * @throws Exception
	 */
	void export(OutputStream os, Goods t1) throws Exception;
	
	
	List<Ordersingoods> listByOrdersuuid(Long ordersuuid);

}

