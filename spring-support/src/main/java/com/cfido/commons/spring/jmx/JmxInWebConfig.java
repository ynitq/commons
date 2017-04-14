package com.cfido.commons.spring.jmx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * 自动启动jmx的web管理界面
 * 
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月11日
 */
@Configuration
@EnableConfigurationProperties(JmxInWebProperties.class)
public class JmxInWebConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JmxInWebConfig.class);

	public JmxInWebConfig() {
		log.info("自动配置 JmxInWeb");
	}

	@Autowired
	private JmxInWebProperties prop;

	@Autowired(required = false)
	private ServerProperties serverProperties;

	@Bean
	public JmxInWebService jmxInWebService() {
		JmxInWebService s = new JmxInWebService();

		int port = this.prop.getPort();

		if (this.prop.getPort() < 0) {
			if (this.serverProperties != null) {
				port = this.serverProperties.getPort() + 1;

				log.warn("配置文件中没有 jmx.port的配置，自动参照 server.port生成端口: {}", port);
			} else {
				port = 8081;
				log.warn("配置文件中没有 jmx.port的配置，自动设置为 8081");
			}
		}

		s.setPort(port);

		return s;
	}

}
