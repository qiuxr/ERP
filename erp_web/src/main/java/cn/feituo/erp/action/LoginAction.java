package cn.feituo.erp.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import cn.feituo.erp.biz.IEmpBiz;
import cn.feituo.erp.entity.Emp;

/**
 * 用户的登陆/出Action
 *
 */
public class LoginAction {
	
	private static final Logger log = LoggerFactory.getLogger(LoginAction.class);
	
	private IEmpBiz empBiz;
	
	private String username;//登陆名
	private String pwd;//密码

	public void checkUser2(){
		Emp loginUser = null;
		try {
			loginUser = empBiz.findByUsernameAndPwd(username, pwd);
			if(null == loginUser){
				ajaxReturn(false,"用户名或密码不正确");
			}else{
				//把登陆的用户放入值栈中
				ServletActionContext.getContext().getSession().put("loginUser", loginUser);
				ajaxReturn(true,"登陆成功");				
			}
		} catch (Exception e) {
			ajaxReturn(false,"登陆失败");
			log.error("登陆失败",e);
		}
	}
	
	/**
	 * 登陆
	 */
	public void checkUser(){
		//创建令牌
		UsernamePasswordToken upt = new UsernamePasswordToken(username,pwd);
		//获取主题
		Subject subject = SecurityUtils.getSubject();
		//执行主题的登陆方法
		try {
			subject.login(upt);
			ajaxReturn(true,"登陆成功");
		} catch (AuthenticationException e) {
			ajaxReturn(false,"登陆失败");
			log.error("登陆失败",e);
		}
	}
	
	/**
	 * 显示登陆的用户名
	 */
	public void showName(){
		//获取登陆的用户
		//Emp loginUser = (Emp)ServletActionContext.getContext().getSession().get("loginUser");
		Emp emp = (Emp) SecurityUtils.getSubject().getPrincipal();
		if(null == emp) {
			ajaxReturn(false,"");
		}else {
			Map<String, Object> empMap = new HashMap<String, Object>();
			empMap.put("username", emp.getName());
			empMap.put("loginempuuid", emp.getUuid());
			ajaxReturn(true,empMap);
		}
	}
	
	/**
	 * 退出登陆
	 */
	public void loginOut(){
		//ServletActionContext.getContext().getSession().remove("loginUser");
		SecurityUtils.getSubject().logout();
	}
	
	/**
	 * 返回前端操作结果
	 * @param success
	 * @param message
	 */
	public void ajaxReturn(boolean success, Object message){
		//返回前端的JSON数据
		Map<String, Object> rtn = new HashMap<String, Object>();
		rtn.put("success",success);
		rtn.put("message",message);
		write(JSON.toJSONString(rtn));
	}
	
	/**
	 * 输出字符串到前端
	 * @param jsonString
	 */
	public void write(String jsonString){
		try {
			//响应对象
			HttpServletResponse response = ServletActionContext.getResponse();
			//设置编码
			response.setContentType("text/html;charset=utf-8"); 
			//输出给页面
			response.getWriter().write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setEmpBiz(IEmpBiz empBiz) {
		this.empBiz = empBiz;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}
