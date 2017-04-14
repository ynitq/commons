package com.cfido.commons.utils.cache;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <pre>
 * Redis 配置
 * 激活了Cache的功能，使得jpa的接口文件中可直接使用redis作为cache
 * 
 * 可在配置文件中配置每个cache的超时时间，格式:
 * 
 * redis.cache.expire.[cache的名字]=时长（秒）
 * 
 * 例如：
 * redis.cache.expire.myCache1=100
 * redis.cache.expire.myCache2=200
 * </pre>
 * 
 * @author 梁韦江
 *  2016年6月16日
 */
// @Configuration
// @ConfigurationProperties(prefix = "redis.cache")
// @EnableCaching
public abstract class BaseRedisCacheConfig extends CachingConfigurerSupport {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BaseRedisCacheConfig.class);

	private Map<String, Long> expire = new HashMap<String, Long>();

	public Map<String, Long> getExpire() {
		return expire;
	}

	public void setExpire(Map<String, Long> expire) {
		this.expire = expire;
	}

	@Bean
	public CacheManager cacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {
		RedisCacheManager cm = new RedisCacheManager(redisTemplate);

		// 这里可以设置没一种cache的超时时间
		// Map<String, Long> expireMap = new HashMap<>();
		// expireMap.put("userId", new Long(100));// 这个cache的超时时间为100秒

		cm.setExpires(expire);

		if (expire != null && !this.expire.isEmpty()) {
			log.info("RedisCache 超时设置 ----- ");
			for (Map.Entry<String, Long> en : expire.entrySet()) {
				log.info("\t Cache [{}] : {} 秒超时", en.getKey(), en.getValue());
			}
		}

		return cm;
	}

	@Bean
	public RedisTemplate<String, ?> redisTemplate(RedisConnectionFactory factory) {

		log.debug("创建 RedisTemplate");

		StringRedisTemplate template = new StringRedisTemplate(factory);

		// 设置对象序列化工具，可以使得要缓存的对象不需要实现Serializable接口
		template.setValueSerializer(this.getRedisSerializer());
		template.afterPropertiesSet();

		return template;
	}

	/**
	 * 对象序列号工具，用json格式的，客户端查看的时候会方便点。
	 * 
	 * @return
	 */
	private RedisSerializer<?> getRedisSerializer() {
		@SuppressWarnings({
				"rawtypes", "unchecked"
		})
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);

		return jackson2JsonRedisSerializer;
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
