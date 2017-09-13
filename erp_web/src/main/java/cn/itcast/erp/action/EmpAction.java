package cn.itcast.erp.action;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import cn.feituo.erp.biz.IEmpBiz;
import cn.feituo.erp.entity.Emp;
import cn.feituo.erp.entity.Tree;
import cn.feituo.erp.exception.ErpException;

/**
 * 员工Action 
 * @author Administrator
 *
 */
public class EmpAction extends BaseAction<Emp> {
	
	private static final Logger log = LoggerFactory.getLogger(EmpAction.class);

	private IEmpBiz empBiz;
	private String checkedIds;// 角色ID的字符串，多个角色ID，以逗号分割
	
	private String oldPwd;//原密码
	private String newPwd;//新密码
	
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

	public void setEmpBiz(IEmpBiz empBiz) {
		this.empBiz = empBiz;
		super.setBaseBiz(this.empBiz);
	}

	/**
	 * 修改密码
	 */
	public void updatePwd(){
		Emp loginUser = getLoginUser();
		try {
			empBiz.updatePwd(newPwd, oldPwd, loginUser.getUuid());
			ajaxReturn(true,"修改成功");
		} catch (ErpException e) {
			log.error("修改密码失败",e);
			ajaxReturn(false,e.getMessage());
		} catch (Exception e) {
			log.error("修改密码失败",e);
			ajaxReturn(false,"修改失败");
		}
	}
	
	/**
	 * 重置密码
	 */
	public void updatePwd_reset(){
		try {
			empBiz.updatePwd_reset(newPwd,getId());
			ajaxReturn(true,"重置密码成功");
		} catch (Exception e) {
			log.error("重置密码失败",e);
			ajaxReturn(false,"重置密码失败");
		}
	}
	
	/**
	 * 读取用户角色
	 */
	public void readEmpRole(){
		List<Tree> list = empBiz.readEmpRole(getId());
		write(JSON.toJSONString(list));
	}
	
	/**
	 * 更新用户角色
	 */
	public void updateEmpRole(){
		try {
			empBiz.updateEmpRole(getId(), checkedIds);
			ajaxReturn(true,"更新成功");
		} catch (Exception e) {
			log.error("更新用户角色失败",e);
			ajaxReturn(false,"更新失败");
		}
	}

	public void setCheckedIds(String checkedIds) {
		this.checkedIds = checkedIds;
	}
	
	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}
	
	
	/**
	 * 导出员工表
	 */
	public void export(){
		HttpServletResponse response = ServletActionContext.getResponse();
		//设置头信息，设置文件方式为附件形式打开，中文名字转码，避免中文文件名乱码
		try {
			response.setHeader("Content-Disposition","attachment;filename=emp.xls");
			empBiz.export(response.getOutputStream(),getT1());
			log.info("员工表格导出成功：Goods.xls");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("员工表格导出失败",e.getMessage());
		}
	}
	
	//表格上传
		public void doImport(){
			//判断文件是否为表格
			if(!"application/vnd.ms-excel".equals(fileContentType)){
				if(!fileFileName.endsWith(".xls")){
					ajaxReturn(false, "上传的文件必须为excel格式");
					return;
				}
			}
			
			try {
				empBiz.doImport(new FileInputStream(file));
				ajaxReturn(true, "上传文件成功");
			} catch (ErpException erp) {
				ajaxReturn(false, erp.getMessage());
			} catch (Exception e) {
				log.error("表格上传失败",e);
				ajaxReturn(false, "上传文件失败");
			}
		}

}
