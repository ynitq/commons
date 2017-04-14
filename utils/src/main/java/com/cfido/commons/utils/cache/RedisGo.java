package com.cfido.commons.utils.cache;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * <pre>
 * Jedis
 * </pre>
 * 
 * @author 黄云
 * 2016年7月20日
 */
public class RedisGo {
	
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(RedisGo.class);
	
	private static String url;
	
	private static JedisPool pool;

	public static boolean go = false;
	
	public static void go(){
		go = true;
	}
	
	/**
	 * @return the url
	 */
	public static String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public static void setUrl(String url) {
		go = true;
		RedisGo.url = url;
	}

	public static Jedis getJedis() throws URISyntaxException{
		if(pool==null){
			log.debug("开启一个连接池！");
			open();
		}
		return pool.getResource();
	}
	
	/**
	 * 刷新缓存--用于save，delete
	 * @param key
	 * @param cacheNames
	 * @param idVal
	 * @throws URISyntaxException
	 */
	public static void refresh(String key, String cacheNames, int idVal) throws URISyntaxException{
		Jedis jedis = getJedis();
		try{
			jedis.del(key);
			
			//获取所有跟本实体相关的查询KEY--默认存储为zset
			String zsetKey = String.format("%sList~keys", cacheNames);
			long size = jedis.zcard(zsetKey);
			Set<String> set = jedis.zrange(zsetKey, 0, size);
			for (String k : set) {
				//迭代相关Key并从cache中拿出数据看看是否需要删除相关的key
				String val = jedis.get(k);
				if(val.contains(",[")){
					val = val.substring(val.indexOf(",[")+2, val.indexOf("]]"));
				}
				String[] vals = val.split(",");
				for (String v : vals) {
					if(Integer.parseInt(v)==idVal){
						jedis.del(k);
					}
				}
			}
			jedis.del(zsetKey);
			log.debug("keys:{}", size);
		}catch(Exception e){
			log.error(e.getMessage());
		}finally{
			close(jedis);
		}
	}
	
	/**
	 * 刷新缓存，用于save
	 * @param key
	 * @param cacheNames
	 * @throws URISyntaxException
	 */
	public static void refresh(String key, String cacheNames) throws URISyntaxException{
		Jedis jedis = getJedis();
		try{
			jedis.del(key);
			
			//获取所有跟本实体相关的查询KEY--默认存储为zset
			String zsetKey = String.format("%sList~keys", cacheNames);
			long size = jedis.zcard(zsetKey);
			Set<String> set = jedis.zrange(zsetKey, 0, size);
			for (String k : set) {
				//迭代相关Key并删除所有的ListKey
				jedis.del(k);
			}
			jedis.del(zsetKey);
			log.debug("keys:{}", size);
		}catch(Exception e){
			log.error(e.getMessage());
		}finally{
			close(jedis);
		}
	}
	
	/**
	 * 关闭连接
	 */
	public static void close(Jedis jedis) {
		if(jedis!=null){
			jedis.close();
			log.debug("jedis释放连接："+jedis);
		}
	}

	/**
	 * 创建连接池并获得一个连接
	 * @return
	 * @throws URISyntaxException
	 */
	@SuppressWarnings({ "deprecation"})
	private static Jedis open() throws URISyntaxException {
		JedisPoolConfig config = new JedisPoolConfig();

		config.setMaxTotal(100);
		config.setMaxIdle(20);
		config.setMaxWaitMillis(1000l);
		
		Jedis jedis = null;
		
		if(pool==null){
			pool = new JedisPool(config, new URI(url));
		}
		try {
			jedis = pool.getResource();
		} catch (JedisConnectionException e) {
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		}
		return jedis;
	}
	
}
