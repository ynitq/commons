
package com.cfido.commons.spring.redis;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * <pre>
 * 用 redis 作为cache 的相关配置
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月15日
 */
@Configuration
@EnableAsync
@EnableConfigurationProperties(RedisCacheProperties.class)
public class RedisCacheConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RedisCacheConfig.class);

	public RedisCacheConfig() {
		log.info("自动配置 Redis Cache");
	}

	@Autowired
	private RedisCacheProperties prop;

	/**
	 * 查看异步执行cache功能的的管理器，同时也是二级缓存
	 * 
	 * @return
	 */
	@Bean
	public ASyncCacheExecuter asyncCacheExecuter() {
		return new ASyncCacheExecuter(
				this.prop.getMemoryCache().getExpireTimeInSec(),
				this.prop.getMemoryCache().getMaxSize());
	}

	@Bean
	public CacheManager cacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {

		RedisCacheManager redisCm = new RedisCacheManager(redisTemplate);

		ASyncCacheManager asyncCm = new ASyncCacheManager(redisCm);

		// 设置每个cache的超时
		asyncCm.setExpires(this.prop.getExpire().getMap());
		// 设置默认的超时
		asyncCm.setDefaultExpiration(this.prop.getExpire().getDefaultInSec());

		if (log.isDebugEnabled()) {
			if (!this.prop.getExpire().getMap().isEmpty()) {
				log.debug("RedisCache 超时设置  ");
				for (Map.Entry<String, Long> en : this.prop.getExpire().getMap().entrySet()) {
					log.info("\t Cache [{}] : {} 秒超时", en.getKey(), en.getValue());
				}
			}
		}

		return asyncCm;
	}

	/**
	 * 用于根据方法中的参数生成key的生成器
	 * 
	 * @return
	 */
	@Bean
	public KeyGenerator commonKeyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
				sb.append(target.getClass().getSimpleName());
				sb.append(method.getName());
				for (Object obj : params) {
					if (obj instanceof Pageable) {
						Pageable page = (Pageable) obj;
						sb.append(String.format("_page:%d_%d_", page.getPageSize(), page.getPageNumber()));
					} else {
						sb.append(obj);
					}
				}

				String key = sb.toString();

				log.debug("Cache 的key={}", key);

				return key;
			}
		};
	}
}
