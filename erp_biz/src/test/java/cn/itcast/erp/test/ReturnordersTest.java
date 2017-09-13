package cn.itcast.erp.test;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.feituo.erp.entity.Returnorderdetail;
import cn.feituo.erp.entity.Returnorders;
import cn.itcast.erp.biz.IReturnordersBiz;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext_test.xml" })
public class ReturnordersTest {
    @Test
    public void testLogic() {
	ApplicationContext ac = new ClassPathXmlApplicationContext("classpath*:applicationContext*.xml");
	IReturnordersBiz ordersBiz = (IReturnordersBiz) ac.getBean("returnordersBiz");
	Returnorders returnorders = null;
	Returnorderdetail od = null;
	Calendar car = Calendar.getInstance();
	for (int i = 1; i <= 12; i++) {
	    car.set(Calendar.MONTH, i);
	    car.set(Calendar.DAY_OF_MONTH, 1);
	    returnorders = new Returnorders();
	    returnorders.setCreater(11l);
	    returnorders.setCreatetime(car.getTime());
	    returnorders.setType("2");
	    returnorders.setState("0");
	    returnorders.setSupplieruuid(2l);
	    // 明细
	    od = new Returnorderdetail();
	    od.setGoodsuuid(1l);
	    od.setGoodsname("水蜜桃");
	    long num = (long) (Math.random() * 1000);
	    od.setPrice(2.34);
	    od.setNum(num);
	    od.setMoney(num * 2.34);
	    od.setReturnorders(returnorders);
	    od.setState("0");
	    returnorders.setTotalmoney(od.getMoney());
	    returnorders.setReturnorderdetails(new ArrayList<Returnorderdetail>());
	    returnorders.getReturnorderdetails().add(od);
	    ordersBiz.add(returnorders);
	}
    }

}
