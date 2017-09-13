package cn.itcast.erp.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSON;

@ServerEndpoint(value = "/empws/empWebSocket")
public class EmpWebSocket {

	// 线程安全的map集合，用来存放每个客户端对应的MyWebSocket对象。所有连接共享, 使用的是线程安全的hashmap集合
	private static ConcurrentHashMap<String, EmpWebSocket> webSocketMap = new ConcurrentHashMap<String, EmpWebSocket>();
	//存储客户端登录在线的用户的id和用户名, 所有连接共享, 使用的是线程安全的hashmap集合
	private static ConcurrentHashMap<String, String> empInfo = new ConcurrentHashMap<String, String>();

	// 定义一个记录当前用户uuid的类型
	private String empuuid;
	// 定义一个记录客户端的聊天名称
	private String username;
	// 与某个客户端的连接会话，通过它来给客户端发送数据
	private Session session;

	public Session getSession() {
		return session;
	}

	public String getUsername() {
		return username;
	}

	public String getEmpuuid() {
		return empuuid;
	}

	/**
	 * 事件方法, 当与客户端连接打开时回调使用
	 * @param session  当前会话对象
	 */
	@OnOpen
	public void onOpen(Session session) {
		this.session = session;

	}

	/**
	 * 事件方法, 当服务端收到客户端消息时触发回调
	 * @param message  收到的客户端发送的消息
	 * @param session  当前会话对象
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		try {
			@SuppressWarnings("unchecked")
			//将客户端传入的json字符串转换成map集合.(事先定义好)
			Map<String, String> messageMap = (Map<String, String>) JSON.parse(message);
			//判断从客户端传入的消息集合中是否包含loginempuuid和username, 如果有则说明该用户已登录, 需要将登录信息(webSocketMap和empInfo)保存.
			if (messageMap.containsKey("loginempuuid") && messageMap.containsKey("username")
					&& !"".equals(messageMap.get("loginempuuid")) && !"".equals(messageMap.get("username"))) {
				//empuuid和username赋值
				empuuid = (String) messageMap.get("loginempuuid");
				username = messageMap.get("username");
				//将当前登录用户的信息保存到empinfo中
				empInfo.put(empuuid, username);
				//将当前websocket对象存入webSocketMap中
				webSocketMap.put(empuuid, this);
				//创建map集合, 将info设置为key, 用户信息设置为value.最终参数为:{"info" : {empuuid : username}}
				Map<String, Map<String, String>> infoMap = new HashMap<String, Map<String, String>>();
				infoMap.put("info", empInfo);
				//转换成json字符串
				String jsonString = JSON.toJSONString(infoMap);
				//将该信息发送给各个客户端, 用于刷新用户列表
				for (String empuuidx : webSocketMap.keySet()) {
					webSocketMap.get(empuuidx).session.getBasicRemote().sendText(jsonString);
				}
			}
			//判断从客户端传入的消息集合中是否包含touuid和tomessage, 如果有则说明该用户要发送tomessage消息给touuid用户
			if (messageMap.containsKey("touuid") && messageMap.containsKey("tomessage")) {
				//对webSocketMap循环遍历, 查找需要发送的用户
				for (String empuuidx : webSocketMap.keySet()) {
					if (empuuidx.equals(messageMap.get("touuid"))) {
						//创建嵌套map集合, 封装参数, 最终参数是{"message":{empuuid : {username : tomessage}}}, message为客户端确认标识
						Map<String, String> messageMap1 = new HashMap<String, String>();
						messageMap1.put(username, messageMap.get("tomessage"));
						Map<String, Map<String, String>> messageMap2 = new HashMap<String, Map<String, String>>();
						messageMap2.put(empuuid, messageMap1);
						Map<String, Map<String, Map<String, String>>> messageMap3 = new HashMap<String, Map<String, Map<String, String>>>();
						messageMap3.put("message", messageMap2);
						//将封装好的参数转换成json字符串发送到客户端
						webSocketMap.get(empuuidx).getSession().getBasicRemote().sendText(JSON.toJSONString(messageMap3));
					}
				}
			}
			//判断从客户端传入的消息集合中是否包含logout, 如果有则说明该用户正常退出登录.
			if (messageMap.containsKey("logout") && !"".equals(messageMap.get("logout"))) {
				//webSocketMap中移除当前websocket对象
				webSocketMap.remove(messageMap.get("logout"));
				//empInfo中移除当前用户登录信息
				empInfo.remove(messageMap.get("logout"));
				//将用户信息清空
				empuuid = null;
				username = null;
				//创建map集合, 将info设置为key, 用户信息设置为value.最终参数为:{"info" : {empuuid : username}}
				Map<String, Map<String, String>> infoMap = new HashMap<String, Map<String, String>>();
				infoMap.put("info", empInfo);
				//转换成json字符串
				String jsonString = JSON.toJSONString(infoMap);
				//将该信息发送给各个客户端, 用于刷新用户列表(用户退出登录, 在线人数减少)
				for (String empuuidx : webSocketMap.keySet()) {
					webSocketMap.get(empuuidx).getSession().getBasicRemote().sendText(jsonString);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 事件方法, 当客户端主动关闭连接时触发回调
	 * @param session  当前会话对象
	 */
	@OnClose
	public void onClose(Session session) {

		//当用户关闭浏览器或主动断开时清空用户登录信息
		if (null != empuuid) {
			try {
			//webSocketMap中移除当前websocket对象
			webSocketMap.remove(empuuid);
			//empInfo中移除当前用户登录信息
			empInfo.remove(empuuid);
			//创建map集合, 将info设置为key, 用户信息设置为value.最终参数为:{"info" : {empuuid : username}}
			Map<String, Map<String, String>> infoMap = new HashMap<String, Map<String, String>>();
			infoMap.put("info", empInfo);
			//转换成json字符串
			String jsonString = JSON.toJSONString(infoMap);
			//将该信息发送给各个客户端, 用于刷新用户列表(用户退出登录, 在线人数减少)
			for (String empuuidx : webSocketMap.keySet()) {
					webSocketMap.get(empuuidx).getSession().getBasicRemote().sendText(jsonString);
				
			}
			} catch (IOException e) {
				
			}
		}
	}

}
