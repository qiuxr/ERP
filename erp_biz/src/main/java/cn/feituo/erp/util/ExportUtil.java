package cn.feituo.erp.util;


import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import net.sf.jxls.transformer.XLSTransformer;

/**
 * @author Administrator
 *表格导出工具类
 */
public class ExportUtil {
	/**
	 * @param obj 数据对象集合
	 * @param templeteResourcePath 模板路径
	 * @param templeteName 模板表格的名字
	 * @return
	 * @throws Exception
	 */
	
	private static  Map<String,Object> model = new HashMap<String,Object>();
	
	
	public static Map<String, Object> getModel() {
		return model;
	}


	public static HSSFWorkbook export(Object obj,String templeteResourcePath,String templeteName) throws Exception{
		//导入模板
				HSSFWorkbook book = new HSSFWorkbook(new ClassPathResource(templeteResourcePath).getInputStream());
				
				model.put("objs", obj);
				model.put("templeteName", templeteName);
				//创建转换器
				 XLSTransformer transformer = new XLSTransformer();
				 //利用把对象模型写入工作空间中
				 transformer.transformWorkbook(book, model);
				 
				 return book;
	}
}
