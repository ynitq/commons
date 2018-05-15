package com.cfido.commons.spring.weChatProxy.service;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.cfido.commons.spring.weChatProxy.WeChatProxyProperties;

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
public class CallBackUrlService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CallBackUrlService.class);

	private final static String URL_MAP_KEY = "weixin:urlMap";

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private WeChatProxyProperties prop;

	private HashOperations<String, String, String> hashOps;

	@ManagedAttribute
	public Map<String, String> getMap() {
		return this.hashOps.entries(URL_MAP_KEY);
	}

	@PostConstruct
	protected void init() {
		log.debug("初始化");
		this.hashOps = this.redisTemplate.opsForHash();

		if (this.prop.getMap() != null && !this.prop.getMap().isEmpty()) {
			for (Map.Entry<String, String> en : this.prop.getMap().entrySet()) {
				this.hashOps.putIfAbsent(URL_MAP_KEY, en.getKey(), en.getValue());
				log.debug("初始化微信认证代理时，自动添加规则 {} -> {}", en.getKey(), en.getValue());
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
