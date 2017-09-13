package cn.feituo.erp.biz.impl;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import cn.feituo.erp.entity.Goods;
import cn.feituo.erp.entity.Goodstype;
import cn.feituo.erp.entity.Ordersingoods;
import cn.feituo.erp.biz.IGoodsBiz;
import cn.feituo.erp.dao.IGoodsDao;
import cn.feituo.erp.dao.IGoodstypeDao;
import cn.feituo.erp.exception.ErpException;
import cn.feituo.erp.util.ExportUtil;
import redis.clients.jedis.Jedis;
/**
 * 商品业务逻辑类
 * @author Administrator
 *
 */
public class GoodsBiz extends BaseBiz<Goods> implements IGoodsBiz {

	private IGoodsDao goodsDao;
	private IGoodstypeDao goodstypeDao;
	
	public void setGoodstypeDao(IGoodstypeDao goodstypeDao) {
		this.goodstypeDao = goodstypeDao;
	}

	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
		super.setBaseDao(this.goodsDao);
	}
	
	@Override
	public void export(OutputStream os,Goods t1) throws Exception {
		//查出所有的商品
		List<Goods> list = goodsDao.getList(t1, null, null);
		HSSFWorkbook workbook = ExportUtil.export(list, "Goods.xls", "商品表");
		//保存工作簿到本地目录
		workbook.write(os);
		workbook.close();
		
	}

	@Override
	public void doImport(FileInputStream fileInputStream) throws Exception {
		HSSFWorkbook book = new HSSFWorkbook(fileInputStream);
		HSSFSheet sheet = book.getSheetAt(0);
		int lastRowNum = sheet.getLastRowNum();
		List<Goodstype> goodstypeList=null;
		for(int i=2;i<lastRowNum;i++){
			//遍历每一行
			Goods goods= new Goods();
			goods.setName(sheet.getRow(i).getCell(0).getStringCellValue());//名字
			goods.setOrigin(sheet.getRow(i).getCell(1).getStringCellValue());//产地
			goods.setProducer(sheet.getRow(i).getCell(2).getStringCellValue());//供应商
			//查询数据库是否存在相同的商品名称，产地和供应商的信息
			List<Goods> list = goodsDao.getList(null, goods, null);
			if(list.size()>0){
				//更新数据
				goods =list.get(0);
			}
			goods.setInprice(sheet.getRow(i).getCell(4).getNumericCellValue());
			goods.setOutprice(sheet.getRow(i).getCell(5).getNumericCellValue());
			goods.setUnit(sheet.getRow(i).getCell(3).getStringCellValue());
			if(list.size()==0){
				//设置类型
				String typeName = sheet.getRow(i).getCell(6).getStringCellValue();
				if(goodstypeList==null){
					goodstypeList = goodstypeDao.getList(null, null, null);
				}
				
				//上传表格的商品类型和数据库商品类型中比较
				for (Goodstype goodstype : goodstypeList) {
					if(goodstype.getName().equals(typeName)){
						goods.setGoodstype(goodstype);
						break;
					}
				}
				if(goods.getGoodstype()==null){
					//商品类型错误的话
					throw new ErpException("不存在"+goods.getName()+"这种商品类型");
				}
			}
			//增加
			goodsDao.add(goods);
		}
		book.close();
		
		
	}
	private Jedis jedis;
	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}

	@Override
	public List<Ordersingoods> listByOrdersuuid(Long ordersuuid) {
		/*根据原定单号查询视图，取得商品明细，库存数量，订单内商品数量*/
		List<Ordersingoods> goodslist = goodsDao.listByOrdersuuid(ordersuuid);
		String key = "user5_9_orders_" + ordersuuid;
		/*从jedis取Map，没有存Map到jedis，Map键是商品类型，Value是此商品类型对应的数量*/
		Map<Long, Long> oldmap = null;
		if(null==jedis.get(key)){
			oldmap=new HashMap<Long, Long>();
			for (Ordersingoods og : goodslist) {
				Long oguuid = og.getUuid();
				Long ogordersnum = og.getOrdersnum();
				if (!oldmap.containsKey(oguuid)) {
					oldmap.put(oguuid, ogordersnum);
				}else{
					oldmap.put(oguuid, ogordersnum+oldmap.get(oguuid));
				}
			}
			jedis.set(key, JSON.toJSONString(oldmap));
		}else{
			/*如果存在Map，取出Map内更新订单内商品总数，传回页面*/
			oldmap=JSON.parseObject(jedis.get(key),new TypeReference<Map<Long, Long>>(){});
			for (Ordersingoods og : goodslist) {
				if (oldmap.containsKey(og.getUuid())) {
					og.setOrdersnum(oldmap.get(og.getUuid()));
				}
			}
		}

		return goodslist;
	}
	
}
