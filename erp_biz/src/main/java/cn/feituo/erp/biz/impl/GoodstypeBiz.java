package cn.feituo.erp.biz.impl;
import cn.feituo.erp.entity.Goodstype;
import cn.feituo.erp.biz.IGoodstypeBiz;
import cn.feituo.erp.dao.IGoodstypeDao;
/**
 * 商品分类业务逻辑类
 * @author Administrator
 *
 */
public class GoodstypeBiz extends BaseBiz<Goodstype> implements IGoodstypeBiz {

	private IGoodstypeDao goodstypeDao;
	
	public void setGoodstypeDao(IGoodstypeDao goodstypeDao) {
		this.goodstypeDao = goodstypeDao;
		super.setBaseDao(this.goodstypeDao);
	}
	
}
