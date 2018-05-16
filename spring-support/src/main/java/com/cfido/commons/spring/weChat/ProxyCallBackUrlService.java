package com.cfido.commons.spring.weChat;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;

/**
 * <pre>
 * 管理微信callback后，回调url的服务
 * </pre>
 * 
 * @author 梁韦江
 */
@Service
@ManagedResource(description = "管理微信callback后回调的url")
@ADomainOrder(domainName = CommonMBeanDomainNaming.DOMAIN_WECHAT, order = CommonMBeanDomainNaming.ORDER)
@ConditionalOnProperty(prefix = "wechat", name = "proxy", havingValue = "true", matchIfMissing = false) // 如果enable=true开开启本服务
public class ProxyCallBackUrlService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProxyCallBackUrlService.class);

	private final static String URL_MAP_KEY = "weixin:urlMap";

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private WeChatProperties prop;

	private HashOperations<String, String, String> hashOps;

	@ManagedAttribute
	public Map<String, String> getMap() {
		return this.hashOps.entries(URL_MAP_KEY);
	}

	@PostConstruct
	protected void init() {
		log.info("自动配置-微信回调转发功能，可JMX中管理");

		this.hashOps = this.redisTemplate.opsForHash();

		Map<String, String> proxyMap = this.prop.getProxyMap();

		if (proxyMap != null && !proxyMap.isEmpty()) {
			for (Map.Entry<String, String> en : proxyMap.entrySet()) {
				this.hashOps.putIfAbsent(URL_MAP_KEY, en.getKey(), en.getValue());
				log.debug("微信回调转发: 初始化时自动添加规则 {} -> {}", en.getKey(), en.getValue());
			}
		}
	}

	@ManagedOperation(description = "根据key获取url")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "key", description = "key"),
			@ManagedOperationParameter(name = "code", description = "微信授权码")
	})
	public String getUrl(String key, String code) {
		if (StringUtils.hasText(key) && StringUtils.hasText(code)) {
			String url = this.hashOps.get(URL_MAP_KEY, key);
			if (StringUtils.hasText(url)) {
				int index = url.indexOf("?");
				if (index > 0) {
					return url + "&code=" + code;
				} else {
					return url + "?code=" + code;
				}
			}
		}

		return null;
	}

	@ManagedOperation(description = "设置url")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "key", description = "key"),
			@ManagedOperationParameter(name = "url", description = "url")
	})
	public void putUrl(String key, String url) {
		if (StringUtils.hasText(key) && StringUtils.hasText(url)) {
			this.hashOps.put(URL_MAP_KEY, key, url);
		}
	}

	@ManagedOperation(description = "删除key")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "key", description = "key"),
	})
	public void deleteKey(String key) {
		if (StringUtils.hasText(key)) {
			this.hashOps.delete(URL_MAP_KEY, key);
		}
	}

}
