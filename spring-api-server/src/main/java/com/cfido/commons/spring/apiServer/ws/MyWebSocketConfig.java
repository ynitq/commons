package com.cfido.commons.spring.apiServer.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * <pre>
 * 我们的 WebSocket 配置
 * </pre>
 * 
 * @author 梁韦江
 */
@Component
@EnableWebMvc
@EnableWebSocket
@Configuration
public class MyWebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MyWebSocketConfig.class);

	/** 每个包的大小限制 500K */
	private static final int BUFFER_SIZE_LIMIT = 500_000;

	@Autowired
	private WebSocketHandlerImpl handler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

		log.debug("注册 WebSocketHandler");

		registry.addHandler(handler, "/wsApi");
		registry.addHandler(handler, "/wsApiSockJs").withSockJS();
	}

	@Bean
	public ServletServerContainerFactoryBean createWebSocketContainer() {
		ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
		container.setMaxTextMessageBufferSize(BUFFER_SIZE_LIMIT);
		container.setMaxBinaryMessageBufferSize(BUFFER_SIZE_LIMIT);
		// container.setAsyncSendTimeout(100_000_000_000_000L);
		// container.setMaxSessionIdleTimeout(100_000_000_000_000L);
		return container;
	}

}
