/*
 * 文件名：[文件名]
 * 版权：〈版权〉
 * 描述：〈描述〉
 * 修改人：〈修改人〉
 * 修改时间：YYYY-MM-DD
 * 修改单号：〈修改单号〉
 * 修改内容：〈修改内容〉
 */
package com.cfido.commons.spring.monitor;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * 监控服务的相关配置
 * 
 * @author wjc
 * @date 2016年8月15日
 *
 */
@Configurable
@EnableConfigurationProperties(MonitorClientProperties.class)
@ComponentScan(basePackageClasses = MonitorClientAutoConfig.class)
public class MonitorClientAutoConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(MonitorClientAutoConfig.class);

	public MonitorClientAutoConfig() {
		log.info("自动配置 监控客户端");
	}

}
