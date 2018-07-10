package com.cfido.commons.spring.apiServer.ws;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSON;
import com.cfido.commons.beans.apiExceptions.ApiNotFoundException;
import com.cfido.commons.spring.apiServer.beans.ws.BaseResponseBean;
import com.cfido.commons.spring.apiServer.beans.ws.BaseSocketResponse;
import com.cfido.commons.spring.apiServer.beans.ws.CmdBean;
import com.cfido.commons.spring.apiServer.beans.ws.ErrorResponseBean;
import com.cfido.commons.spring.apiServer.beans.ws.SuccessResponseBean;
import com.cfido.commons.spring.apiServer.service.ApiMethodInfo;
import com.cfido.commons.spring.apiServer.ws.WsLoginContext.ConnHandler;
import com.cfido.commons.spring.serviceInf.IMonitorClientContext;
import com.cfido.commons.utils.threadPool.IMyTask;
import com.cfido.commons.utils.utils.LogUtil;

/**
 * <pre>
 * WebSocketHandler 实现类， 用于处理web socket事件
 * </pre>
 * 
 * @author 梁韦江
 */
@Service
public class WebSocketHandlerImpl implements WebSocketHandler {

	@Autowired
	private WsApiMapContainer clientApiContainer;

	@Autowired
	private WsLoginContext gameContextHolder;

	@Autowired(required = false)
	private IMonitorClientContext monitorClientContext;

	@Autowired(required = false)
	private IWsExtSecurityService extSecurityService;

	@Autowired
	private WsThreadPool threadPool;

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WebSocketHandlerImpl.class);

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.debug("建立了连接 afterConnectionEstablished:id={}", session.getId());
		this.gameContextHolder.saveSession(session);
	}

	/** 执行任务 */
	private void execTask(WebSocketSession session, WebSocketMessage<?> message) {
		// 先清除线程中保存的session
		this.gameContextHolder.removeSession();

		try {
			CmdBean cmd = this.getCmdBean(message);// 格式转化

			if (cmd != null) {

				ApiMethodInfo<BaseSocketResponse> methodInfo = this.clientApiContainer.findApiMethod(cmd.getUrl());

				Object param = null;

				long start = System.currentTimeMillis();

				BaseResponseBean res = null;
				try {
					if (methodInfo == null) {
						// 找不到api就直接抛错
						throw new ApiNotFoundException(cmd.getUrl());
					}

					if (methodInfo.hasParam()) {
						// 如果该接口有参数，就绑定参数
						param = cmd.parserForm(methodInfo.getFormClass());
						if (param == null) {
							log.warn("准备调用接口: {}, 但客户端没有传递参数过来。原始数据:{}", cmd.getUrl(), cmd.getOriginStr());
							return;
						}
					}

					// 执行接口方法前，保存一下session
					ConnHandler handler = this.gameContextHolder.saveSession(session);

					// 检查权限
					methodInfo.checkRights(gameContextHolder);

					if (this.extSecurityService != null) {
						// 如果需要额外执行安全检查，就执行
						this.extSecurityService.onBeforeInvoke(handler, methodInfo);
					}

					// 统计一下接口调用次数
					if (this.monitorClientContext != null) {
						this.monitorClientContext.addRequest();
					}

					// 执行接口方法
					BaseSocketResponse value = methodInfo.invoke(param);

					// 构造对正常返回结果的封装
					res = new SuccessResponseBean(cmd, value);

				} catch (Throwable exParam) {

					Throwable e = exParam;
					if (e instanceof InvocationTargetException) {
						// 如果类型是 InvocationTargetException， 里面的类型才是真正的错误
						e = ((InvocationTargetException) exParam).getTargetException();
					}

					// 如果出错了，就构造出错时的返回
					res = new ErrorResponseBean(cmd, e);

					if (log.isDebugEnabled()) {
						LogUtil.traceError(log, e, "调用接口时出现错误");
					}
				}

				// 发送结果
				this.gameContextHolder.sendMessage(res);

				long end = System.currentTimeMillis();

				// 暂时关闭这里的log

				if (log.isDebugEnabled()) {
					String paramStr = null;
					if (param != null && message.getPayloadLength() < 1024) {
						// 如果上传的内容超过1K，就不输出在debug log中
						paramStr = JSON.toJSONString(param, true);
					}

					log.debug("调用接口: {} 时间消耗 :{}ms \n绑定表单的结果:{}", cmd.getUrl(), end - start, paramStr);
				}
			}
		} catch (Throwable e) {
			LogUtil.traceError(log, e, "调用接口时出现内部错误");
		}

	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

		this.threadPool.addNewTask(new WebSocketTask(session, message));
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		log.error("传输时发生了错误 handleTransportError, id={}", session.getId());
		session.close();
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

		log.debug("断线时 afterConnectionClosed id={}", session.getId());

		this.gameContextHolder.afterConnectionClosed(session);
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	protected class WebSocketTask implements IMyTask {

		private final WebSocketSession session;

		private final WebSocketMessage<?> message;

		public WebSocketTask(WebSocketSession session, WebSocketMessage<?> message) {
			this.session = session;
			this.message = message;
		}

		@Override
		public void run() {
			WebSocketHandlerImpl.this.execTask(this.session, this.message);
		}

		@Override
		public String getUniqueId() {
			return this.getClass().getName();
		}

	}

	private CmdBean getCmdBean(WebSocketMessage<?> message) {
		if (!(message instanceof TextMessage)) {
			log.warn("收到了非字符型的信息，类型为{}, 该类型忽略", message.getClass().getSimpleName());
			return null;
		}

		String jsonStr = ((TextMessage) message).getPayload();

		CmdBean cmd = null;

		try {
			cmd = JSON.parseObject(jsonStr, CmdBean.class);
		} catch (Throwable e) {
		}

		if (cmd == null) {
			log.debug("无法解析收到的内容");
			return null;
		}

		if (StringUtils.isEmpty(cmd.getUrl())) {
			log.debug("收到命令非法 {}", jsonStr);
			return null;
		}

		cmd.setOriginStr(jsonStr);

		return cmd;

	}

}
