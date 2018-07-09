
package com.cfido.commons.spring.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 * redis的相关配置
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月15日
 */
@Configuration
public class RedisConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RedisConfig.class);

	public RedisConfig() {
		log.info("自动配置 Redis");
	}

	@Bean
	public RedisTemplate<String, ?> redisTemplate(RedisConnectionFactory factory) {

		log.debug("创建 RedisTemplate<String, Object>");

		StringRedisTemplate template = new StringRedisTemplate(factory);

		// 设置对象序列化工具，可以使得要缓存的对象不需要实现Serializable接口
		template.setValueSerializer(this.getRedisSerializer());
		template.afterPropertiesSet();

		return template;
	}

	/**
	 * value的序列号工具，用json格式的，客户端查看的时候会方便点。
	 * 
	 * @return
	 */
	private RedisSerializer<?> getRedisSerializer() {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);

		return jackson2JsonRedisSerializer;
	}
}
