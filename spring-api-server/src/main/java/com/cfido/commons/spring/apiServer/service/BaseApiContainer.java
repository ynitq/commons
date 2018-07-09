package com.cfido.commons.spring.apiServer.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import com.cfido.commons.annotation.api.AMethod;
import com.cfido.commons.spring.apiServer.core.ApiServerInitException;
import com.cfido.commons.spring.apiServer.core.ApiServerUtils;
import com.cfido.commons.spring.apiServer.core.DebugPageVo;
import com.cfido.commons.spring.apiServer.core.FindInferfaceResult;
import com.cfido.commons.utils.utils.ClassUtil;

/**
 * <pre>
 * 基础的api实现类容器
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public abstract class BaseApiContainer<T> {

	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

	private final Class<T> returnBaseClass; // 返回的数据类型的基类

	@Autowired
	private ApplicationContext applicationContext;

	private final Map<String, ApiMethodInfo<T>> apiMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	public BaseApiContainer() {
		this.returnBaseClass = (Class<T>) ClassUtil.getGenericType(getClass(), 0);
		Assert.notNull(this.returnBaseClass, "应该能找到泛型");
	}

	@PostConstruct
	protected void init() throws ApiServerInitException {
		// 自动寻找所有的实现类
		final Map<String, Object> map = this.applicationContext.getBeansWithAnnotation(this.getAutoSearchAnnoClass());
		for (final Object implObj : map.values()) {
			this.addImplToMap(implObj);
		}
	}

	/** 获取用于自动扫描的注解 */
	protected abstract Class<? extends Annotation> getAutoSearchAnnoClass();

	/**
	 * 根据接口名和方法名，返回封装好的对象
	 * 
	 * @param infName
	 * @param methodName
	 * @return
	 */
	public ApiMethodInfo<T> findApiMethod(String infName, String methodName) {
		final String key = ApiMethodInfo.createKey(infName, methodName);
		return this.apiMap.get(key);
	}

	/**
	 * 根据url，返回封装好的对象
	 */
	public ApiMethodInfo<T> findApiMethod(String url) {
		return this.apiMap.get(url);
	}

	/**
	 * 将API接口放入Map
	 * 
	 * @param implObj
	 *            Object
	 * @throws ApiServerInitException
	 *             ApiServerInitException
	 */
	public void addImplToMap(Object implObj) throws ApiServerInitException {
		if (implObj == null) {
			throw new ApiServerInitException("传入的参数不能为空");
		}

		final Class<?> implObjClass = implObj.getClass();
		log.debug("开始分析类 {}", implObjClass.getName());

		final List<FindInferfaceResult> list = ApiServerUtils.findInterface(implObjClass);
		if (list.isEmpty()) {
			throw new ApiServerInitException(String.format("类 %s 中间没有找到有AClass注解的接口", implObjClass.getName()));
		}

		for (final FindInferfaceResult resBean : list) {
			final Method[] methods = resBean.getInfClass().getMethods();

			for (final Method method : methods) {

				final AMethod amethod = method.getAnnotation(AMethod.class);

				if (amethod == null) {
					log.debug("接口 {} 中的方法{} 没有AMethod的注解，忽略", resBean.getInfClass().getName(), method.getName());
					continue;
				} else {
					final ApiMethodInfo<T> info = new ApiMethodInfo<T>(implObj, resBean, method, amethod,
							this.returnBaseClass);

					final String key = info.getUrl();
					if (log.isDebugEnabled()) {
						log.debug("找到接口方法: url={} , {}", key, info.toDebugString());
					}

					if (this.apiMap.containsKey(key)) {
						// 如果发现的重名，必须抛错，禁止服务启动
						final String msg = String.format("出现了重名了，%s#%s", resBean.getInfClass().getSimpleName(),
								method.getName());
						throw new ApiServerInitException(msg);
					}

					this.apiMap.put(key, info);
					this.afterAddToMap(info);
				}
			}

		}

	}

	/** 找到接口方法，并且放到map中后调用，方便子类做额外处理 */
	protected void afterAddToMap(ApiMethodInfo<T> info) {
	}

	/**
	 * debug页面的vo
	 * 
	 * @param apiUrlPrefix
	 * @return
	 */
	public DebugPageVo getDebugPageVo(String apiUrlPrefix) {
		return new DebugPageVo(apiUrlPrefix, this.getMethodList());
	}

	/**
	 * 返回排序后的api方法的list，用于生成调试页面
	 * 
	 * @return
	 */
	private List<ApiMethodInfo<?>> getMethodList() {
		final List<ApiMethodInfo<?>> list = new LinkedList<>();
		list.addAll(this.apiMap.values());
		Collections.sort(list, ApiMethodInfo.COMPARATOR);
		return list;
	}

}
