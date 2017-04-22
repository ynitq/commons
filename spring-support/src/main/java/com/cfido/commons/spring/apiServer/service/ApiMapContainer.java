package com.cfido.commons.spring.apiServer.service;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.cfido.commons.annotation.api.AApiServerImpl;
import com.cfido.commons.annotation.api.AMethod;
import com.cfido.commons.spring.apiServer.core.ApiMethodInfo;
import com.cfido.commons.spring.apiServer.core.ApiServerInitException;
import com.cfido.commons.spring.apiServer.core.ApiServerUtils;
import com.cfido.commons.spring.apiServer.core.DebugPageVo;
import com.cfido.commons.spring.apiServer.core.FindInferfaceResult;

/**
 * <pre>
 * 用于存储所有api的容器
 * </pre>
 * 
 * @author 梁韦江 2016年7月4日
 */
@Service
public class ApiMapContainer {

	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ApplicationContext applicationContext;

	private final Map<String, ApiMethodInfo> apiMap = new HashMap<>();

	public ApiMapContainer() {
	}

	@PostConstruct
	protected void init() throws ApiServerInitException {
		// 自动寻找所有的实现类
		Map<String, Object> map = this.applicationContext.getBeansWithAnnotation(AApiServerImpl.class);
		for (Object implObj : map.values()) {
			this.addImplToMap(implObj);
		}
	}

	/**
	 * 根据接口名和方法名，返回封装好的对象
	 * 
	 * @param infName
	 * @param methodName
	 * @return
	 */
	public ApiMethodInfo findApiMethod(String infName, String methodName) {
		String key = ApiMethodInfo.createKey(infName, methodName);
		return this.apiMap.get(key);
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

		Class<?> implObjClass = implObj.getClass();
		log.debug("开始分析类 {}", implObjClass.getName());

		List<FindInferfaceResult> list = ApiServerUtils.findInterface(implObjClass);
		if (list.isEmpty()) {
			throw new ApiServerInitException(String.format("类 %s 中间没有找到有AClass注解的接口", implObjClass.getName()));
		}

		for (FindInferfaceResult resBean : list) {
			Method[] methods = resBean.getInfClass().getMethods();

			for (Method method : methods) {

				AMethod amethod = method.getAnnotation(AMethod.class);

				if (amethod == null) {
					log.debug("接口 {} 中的方法{} 没有AMethod的注解，忽略", resBean.getInfClass().getName(), method.getName());
					continue;
				} else {
					ApiMethodInfo info = new ApiMethodInfo(implObj, resBean, method, amethod);

					String key = info.getUrl();
					log.debug("找到接口方法: url={} , {}", key, info.toDebugString());

					if (this.apiMap.containsKey(key)) {
						// 如果发现的重名，必须抛错，禁止服务启动
						String msg = String.format("出现了重名了，%s#%s", resBean.getInfClass().getSimpleName(),
								method.getName());
						throw new ApiServerInitException(msg);
					}

					this.apiMap.put(key, info);
				}
			}

		}

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
	private List<ApiMethodInfo> getMethodList() {
		List<ApiMethodInfo> list = new LinkedList<>();
		list.addAll(this.apiMap.values());
		Collections.sort(list);
		return list;
	}

}
