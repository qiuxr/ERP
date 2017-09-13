package cn.feituo.erp.biz;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import cn.feituo.erp.entity.Emp;
import cn.feituo.erp.entity.Tree;
/**
 * 员工业务逻辑层接口
 * @author Administrator
 *
 */
public interface IEmpBiz extends IBaseBiz<Emp>{

	/**
	 * 登陆时验证用户是否存在
	 * @param username
	 * @param pwd
	 * @return
	 */
	Emp findByUsernameAndPwd(String username, String pwd);
	
	/**
	 * 修改密码
	 * @param newPwd
	 * @param oldPwd
	 * @param uuid
	 */
	void updatePwd(String newPwd,String oldPwd, Long uuid);
	
	/**
	 * 重置密码
	 * @param newPwd
	 * @param uuid
	 */
	void updatePwd_reset(String newPwd, Long uuid);
	
	/**
	 * 员工的角色
	 * @param uuid
	 * @return
	 */
	List<Tree> readEmpRole(Long uuid);
	
	/**
	 * 更新员工角色
	 * @param uuid 员工编号
	 * @param checkedIds 角色编号字符串，多个以逗号分割
	 */
	void updateEmpRole(Long uuid, String checkedIds);

	/**
	 * 员工导入
	 * @param is
	 * @throws Exception
	 */
	void doImport(InputStream is) throws Exception;

	/**
	 * 员工导出
	 * @param os
	 * @param t1
	 * @throws Exception
	 */
	void export(OutputStream os, Emp t1) throws Exception;
}

