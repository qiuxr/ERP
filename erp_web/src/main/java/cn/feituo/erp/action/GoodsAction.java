package cn.feituo.erp.action;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import cn.feituo.erp.biz.IGoodsBiz;
import cn.feituo.erp.entity.Goods;
import cn.feituo.erp.entity.Ordersingoods;
import cn.feituo.erp.exception.ErpException;

/**
 * 商品Action 
 * @author Administrator
 *
 */
public class GoodsAction extends BaseAction<Goods> {
	private static final Logger log = LoggerFactory.getLogger(GoodsAction.class);
	private IGoodsBiz goodsBiz;
	private String fileFileName;//上传的文件名
	private File file;//上传到文件对象
	private String fileContentType;//上传的文件类型
	
	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
		
	}

	public void setFile(File file) {
		this.file = file;
		
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public void setGoodsBiz(IGoodsBiz goodsBiz) {
		this.goodsBiz = goodsBiz;
		super.setBaseBiz(this.goodsBiz);
	}
	
	/**
	 * 商品表格导出
	 */
	public void export(){
		HttpServletResponse response = ServletActionContext.getResponse();
		//设置头信息，设置文件方式为附件形式打开，中文名字转码，避免中文文件名乱码
		try {
			response.setHeader("Content-Disposition","attachment;filename=Goods.xls");
			goodsBiz.export(response.getOutputStream(),getT1());
			log.info("商品表格导出成功：Goods.xls");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("商品表格导出失败",e.getMessage());
		}
	}
	
	/**
	 * 商品表格导入
	 */
	public void doImport(){
		//判断文件是否为表格
				if(!"application/vnd.ms-excel".equals(fileContentType)){
					if(!fileFileName.endsWith(".xls")){
						ajaxReturn(false, "上传的文件必须为excel格式");
						return;
					}
				}
				
				try {
					goodsBiz.doImport(new FileInputStream(file));
					ajaxReturn(true, "上传文件成功");
					log.info("商品表格上传成功");
				} catch (ErpException erp) {
					ajaxReturn(false, erp.getMessage());
				} catch (Exception e) {
					log.error("商品表格上传失败",e);
					ajaxReturn(true, "上传文件失败");
				}
	}
	
    private Long ordersuuid;
	
	public void setOrdersuuid(Long ordersuuid) {
		this.ordersuuid = ordersuuid;
	}
    
	/*添加退货明细查询视图查询订单明细及其他数据*/
	public void listByOrdersuuid(){
		List<Ordersingoods> list = goodsBiz.listByOrdersuuid(ordersuuid);
		String listString = JSON.toJSONString(list);
		write(listString);
	}

}
