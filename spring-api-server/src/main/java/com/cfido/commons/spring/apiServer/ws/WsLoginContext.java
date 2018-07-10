package com.cfido.commons.spring.apiServer.ws;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cfido.commons.loginCheck.IWebUser;
import com.cfido.commons.spring.apiServer.beans.DateBean;
import com.cfido.commons.spring.apiServer.beans.ws.BasePushBean;
import com.cfido.commons.spring.apiServer.beans.ws.KickoutServerEvent;
import com.cfido.commons.spring.apiServer.beans.ws.PayloadCounter;
import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.security.BaseLoginContext;
import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;

/**
 * <pre>
 * 处理用户登录状态，并可查找在线用户
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
@Service
@ManagedResource(description = "Websocket Api服务")
@ADomainOrder(domainName = CommonMBeanDomainNaming.DOMAIN_API_SERVER, order = CommonMBeanDomainNaming.ORDER)
public class WsLoginContext extends BaseLoginContext {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WsLoginContext.class);

	/** 保存这个连接的信息 */
	public class ConnHandler {
		private final WebSocketSession session;

		// 连接创建的时间
		private final long connectTime = System.currentTimeMillis();

		// 最后一次请求的时间
		private long lastRequestTime;

		private final PayloadCounter upCounter = new PayloadCounter(); // 上行使用情况
		private final PayloadCounter downCounter = new PayloadCounter(); // 下行使用情况

		/** 用户信息 */
		private IWebUser user;

		public ConnHandler(WebSocketSession session) {
			super();
			this.session = session;
		}

		/** 连接时间 */
		public DateBean getConnectTime() {
			return new DateBean(connectTime);
		}

		/** 获取最后一次请求的时间 */
		public DateBean getLastRequestTime() {
			return new DateBean(lastRequestTime);
		}

		/** 获取登陆的用户账号 */
		public String getUserAccount() {
			if (this.user != null) {
				return user.getAccount();
			} else {
				return null;
			}
		}

		/**
		 * 增加请求次数
		 * 
		 * @param payloadLength
		 */
		private void incrementRequest(int payloadLength) {
			// 统计全局的流量
			WsLoginContext.this.upCounter.addPayload(payloadLength);

			// 统计当前连接的流量
			this.upCounter.addPayload(payloadLength);
			// 更新最后请求发生的时间
			this.lastRequestTime = System.currentTimeMillis();
		}

		/** 上行流量统计 */
		public PayloadCounter getUpCounter() {
			return upCounter;
		}

		/** 下行流量统计 */
		public PayloadCounter getDownCounter() {
			return downCounter;
		}

		/** 发送消息 */
		public void sendMessage(BasePushBean event) throws IOException {
			if (session != null && event != null) {
				String responseStr = JSON.toJSONString(event, SerializerFeature.PrettyFormat,
						SerializerFeature.SortField);

				// 封装包
				TextMessage msg = new TextMessage(responseStr);

				// 增加流量使用统计信息
				int payload = msg.getPayloadLength();
				this.downCounter.addPayload(payload); // 统计当前连接
				WsLoginContext.this.downCounter.addPayload(payload); // 统计全局

				// 发送信息
				session.sendMessage(msg);
			}
		}
	}

	/** 所有在线连接，key是session的id */
	private final Map<String, ConnHandler> onlineMap = new ConcurrentHashMap<>();

	/** 所有已登录连接，key是user account */
	private final Map<String, ConnHandler> accountMap = new ConcurrentHashMap<>();

	public final static String HANDLER_SESSION_NAME = "HANDLER_SESSION_NAME";

	private final ThreadLocal<WebSocketSession> sessions = new ThreadLocal<>();

	private final PayloadCounter upCounter = new PayloadCounter(); // 上行使用情况
	private final PayloadCounter downCounter = new PayloadCounter(); // 下行使用情况

	/** 保存 */
	public ConnHandler saveSession(WebSocketSession session) {
		this.sessions.set(session);
		return this.getHandlerFromSession(session);
	}

	/** 发送消息 */
	public void sendMessage(BasePushBean msg) throws IOException {
		// 找到连接信息对象
		ConnHandler handler = this.getHandlerFromSession(getSession());

		handler.sendMessage(msg);
	}

	/** 检查session中是否有用户信息对象，如果没有就添加 */
	private ConnHandler getHandlerFromSession(WebSocketSession session) {
		Assert.notNull(session, "session不能为空");
		ConnHandler handler = (ConnHandler) session.getAttributes().get(HANDLER_SESSION_NAME);
		if (handler == null) {
			// 如果不存在，就新建
			handler = new ConnHandler(session);
			session.getAttributes().put(HANDLER_SESSION_NAME, handler);

			// 新建的同时，放到连接列表中
			this.onlineMap.put(session.getId(), handler);

			log.debug("发现新连接 id:{}", session.getId());
		}
		return handler;
	}

	/** 获取 */
	private WebSocketSession getSession() {
		return this.sessions.get();
	}

	/** 移除 */
	public void removeSession() {
		this.sessions.remove();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IWebUser> T getUser(Class<T> userClass) {
		Assert.notNull(userClass, "userClass is null");
		// 找到连接信息对象
		ConnHandler handler = this.getHandlerFromSession(getSession());

		// 返回用户信息
		return (T) handler.user;
	}

	@Override
	public void onLoginSuccess(IWebUser user) {
		Assert.notNull(user, "user is null");

		// 找到连接信息对象
		ConnHandler handler = this.getHandlerFromSession(getSession());

		// 如果该账号已经登录了，就踢掉原来的旧的连接
		ConnHandler old = this.accountMap.get(user.getAccount());
		if (old != handler) {
			KickoutServerEvent event = new KickoutServerEvent("你已经在其他地方上线");
			try {
				// 先发送事件
				old.sendMessage(event);
				// 然后将用户信息清空
				old.user = null;
				// 但是旧的连接没必要断线
			} catch (IOException e) {
				log.debug("发送踢人事件到 连接(id={}) 时，发生了IO错误");
			}
		}

		// 将用户信息放到handler中
		handler.user = user;

		// 将用户放到在线列表中
		this.accountMap.put(user.getAccount(), handler);
	}

	@Override
	/** 登出时 */
	public void onLogout(Class<? extends IWebUser> userClass) {
		Assert.notNull(userClass, "userClass is null");
		// 找到连接信息对象
		ConnHandler handler = this.getHandlerFromSession(getSession());

		if (handler.user != null) {
			// 从在线用户列表中删除该用户
			this.accountMap.remove(handler.user.getAccount());

			// 将用户信息从handler删除
			handler.user = null;
		}

	}

	/** 增加当前连接的请求次数 */
	public void incrementRequest(int payloadLength) {
		// 找到连接信息对象
		ConnHandler handler = this.getHandlerFromSession(getSession());
		handler.incrementRequest(payloadLength);
	}

	/** 断线时调用 */
	public void afterConnectionClosed(WebSocketSession session) {
		if (session == null) {
			return;
		}

		// 从在线列表中删除
		this.onlineMap.remove(session.getId());
	}

	/** 判断一个账号是否在线 */
	public boolean isAccountOnline(String account) {
		Assert.notNull(account, "account is null");

		return this.accountMap.containsKey(account);
	}

	/** 根据账号获取连接信息 */
	public ConnHandler getConnHandlerByAccount(String account) {
		Assert.notNull(account, "account is null");

		return this.accountMap.get(account);
	}

	@ManagedAttribute(description = "连接总数")
	public int getTotalConnectCount() {
		return this.onlineMap.size();
	}

	@ManagedAttribute(description = "用户总数")
	public int getTotalUserCount() {
		return this.accountMap.size();
	}

	@ManagedOperation(description = "获得在线用户列表")
	public List<String> getUserAccounts() {
		List<String> list = new LinkedList<>(this.accountMap.keySet());
		return list;
	}
}
