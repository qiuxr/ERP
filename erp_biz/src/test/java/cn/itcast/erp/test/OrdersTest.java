package cn.itcast.erp.test;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.feituo.erp.entity.Orderdetail;
import cn.feituo.erp.entity.Orders;
import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.dao.IOrdersDao;


public class OrdersTest {
	
	
	@Test
	public void testLogic(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath*:applicationContext*.xml");
		IOrdersBiz ordersBiz = (IOrdersBiz)ac.getBean("ordersBiz");
		Orders orders = null;
		Orderdetail od = null;
		Calendar car = Calendar.getInstance();
		for(int i = 1; i<=12; i++){
			car.set(Calendar.MONTH, i);
			car.set(Calendar.DAY_OF_MONTH, 1);
			orders = new Orders();
			orders.setCreater(11l);
			orders.setCreatetime(car.getTime());
			orders.setType("2");
			orders.setState("0");
			orders.setSupplieruuid(2l);
			//明细
			od = new Orderdetail();
			od.setGoodsuuid(1l);
			od.setGoodsname("水蜜桃");
			long num = (long)(Math.random() * 1000);
			od.setPrice(2.34);
			od.setNum(num);
			od.setMoney(num * 2.34);
			od.setOrders(orders);
			od.setState("0");
			orders.setTotalmoney(od.getMoney());
			orders.setOrderdetails(new ArrayList<Orderdetail>());
			orders.getOrderdetails().add(od);
			ordersBiz.add(orders);
		}
	}
	

}
