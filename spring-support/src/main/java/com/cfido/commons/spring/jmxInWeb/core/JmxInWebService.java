package com.cfido.commons.spring.jmxInWeb.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.stereotype.Service;

import com.cfido.commons.beans.apiExceptions.SystemErrorException;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.jmxInWeb.JmxInWebProperties;
import com.cfido.commons.spring.jmxInWeb.controller.InvokeOperationParamInfo;
import com.cfido.commons.spring.jmxInWeb.exception.MyAttrNotFoundException;
import com.cfido.commons.spring.jmxInWeb.exception.MyJmException;
import com.cfido.commons.spring.jmxInWeb.exception.MyMBeanNotFoundException;
import com.cfido.commons.spring.jmxInWeb.exception.MyMalformedObjectNameException;
import com.cfido.commons.spring.jmxInWeb.exception.MyOperationNotFoundException;
import com.cfido.commons.spring.jmxInWeb.inf.form.JwChangeAttrForm;
import com.cfido.commons.spring.jmxInWeb.inf.response.JwInvokeOptResponse;
import com.cfido.commons.spring.jmxInWeb.models.DomainVo;
import com.cfido.commons.spring.jmxInWeb.models.MBeanVo;
import com.cfido.commons.utils.utils.ClassUtil;
import com.cfido.commons.utils.utils.ExceptionUtil;
import com.cfido.commons.utils.utils.LogUtil;
import com.cfido.commons.utils.utils.OpenTypeUtil;

/**
 * <pre>
 * MBean服务，可注册mbean，但spring boot都是自动的，所以基本上啥都不用做
 * 
 * 可通过 {@link MBeanExporter#setNamingStrategy(org.springframework.jmx.export.naming.ObjectNamingStrategy)} 改变名字获取规则
 * </pre>
 * 
 * @author 梁韦江 2016年8月11日
 */
@Service
public class JmxInWebService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JmxInWebService.class);

	@Autowired
	private MBeanExporter mBeanExporter;

	@Autowired
	private JmxInWebProperties prop;

	/** 在init() 中初始化 */
	@Autowired
	private MBeanServer server;

	public void changeAttr(JwChangeAttrForm form) throws BaseApiException {
		MBeanAttributeInfo targetAttribute = null;

		ObjectName name = this.getObjectName(form.getObjectName());

		// Find target attribute
		MBeanInfo info = this.getMBeanInfoByName(name);
		MBeanAttributeInfo[] attributes = info.getAttributes();
		if (attributes != null) {
			for (int i = 0; i < attributes.length; i++) {
				if (attributes[i].getName().equals(form.getName())) {
					targetAttribute = attributes[i];
					break;
				}
			}
		}

		// 如果找不到这个名字的属性，就抛错
		if (targetAttribute == null) {
			throw new MyAttrNotFoundException(form.getName());
		}

		// 将String 转化成为要设置的值
		String type = targetAttribute.getType();
		Object value = OpenTypeUtil.parserFromString(form.getValue(), type, "");

		// 将值设置到属性中
		try {
			server.setAttribute(name, new Attribute(form.getName(), value));
		} catch (JMException e) {
			throw new MyJmException(e);
		}

		if (log.isDebugEnabled()) {
			String valueStr = OpenTypeUtil.toString(value, type);
			log.debug(LogUtil.format("改变属性的值: ObjectName=%s 属性名=%s 表单值=%s 转化后的值=%s", form.getObjectName(),
					form.getName(),
					form.getValue(), valueStr));
		}
	}

	public MBeanVo getMBeanInfo(String objectNameStr) throws BaseApiException {
		ExceptionUtil.hasText(objectNameStr, "objectName不能为空");

		try {
			ObjectName objectName = new ObjectName(objectNameStr);
			if (!objectName.isPattern() && server.isRegistered(objectName)) {
				MBeanInfo info = server.getMBeanInfo(objectName);
				MBeanVo vo = new MBeanVo(objectName, info);
				vo.setValueFromMBeanServer(server);

				return vo;

			} else {
				throw new MyMBeanNotFoundException(objectNameStr);
			}
		} catch (JMException e) {
			throw new SystemErrorException(e);
		}
	}

	public List<DomainVo> getMBeanList() throws BaseApiException {

		try {
			// 从Server中查询出所有的MBean
			Set<ObjectInstance> mbeans = server.queryMBeans(null, null);

			Map<String, DomainVo> domainMap = new HashMap<>();
			for (ObjectInstance instance : mbeans) {

				ObjectName name = instance.getObjectName();

				String domainName = name.getDomain();

				if (this.prop.getDomainNameFilter().show(domainName)) {
					// 如果过滤器认为这个domain该显示

					// 获取这个Mean的信息
					MBeanInfo info = server.getMBeanInfo(name);
					MBeanVo vo = new MBeanVo(name, info);

					// 检查Map中是否已经有这个Domain，无故没有就添加
					DomainVo domainVo = domainMap.get(domainName);
					if (domainVo == null) {
						domainVo = new DomainVo(domainName);
						domainVo.setOrder(this.getDomainOrder(info));// 设置排序的顺序
						domainMap.put(domainName, domainVo);
					}

					// 将mbean信息添加到domain的mbean列表中
					domainVo.addMBean(vo);
				}
			}

			// 将domain map中的所有内容添加到list中
			List<DomainVo> domainList = new LinkedList<>();
			domainList.addAll(domainMap.values());
			// 根据domain名字排序
			Collections.sort(domainList);

			// 封装返回结果
			return domainList;

		} catch (JMException e) {
			throw new MyJmException(e);
		}
	}

	/**
	 * 获取domain排序数
	 * 
	 * @param info
	 * @return
	 * 
	 * @see ADomainOrder
	 */
	private int getDomainOrder(MBeanInfo info) {
		try {
			Class<?> clazz = Class.forName(info.getClassName());
			ADomainOrder ann = ClassUtil.getAnnotation(clazz, ADomainOrder.class);
			if (ann != null) {

				log.debug("MBean:{}, domain设置排序{}", clazz.getName(), ann.value());

				return ann.value();
			} else {
				return 0;
			}

		} catch (ClassNotFoundException e) {
			// 理论上这个是不可能发生的事情
			throw new RuntimeException(e);
		}
	}

	/**
	 * 执行mbean中的一个方法
	 * 
	 * @param form
	 * @return
	 * @throws BaseApiException
	 */
	public JwInvokeOptResponse invokeOpt(String objectName, String optName, InvokeOperationParamInfo paramInfo)
			throws BaseApiException {
		ObjectName name = this.getObjectName(objectName);

		// Find target attribute
		MBeanInfo info = this.getMBeanInfoByName(name);

		// 寻找对应的操作
		MBeanOperationInfo[] operations = info.getOperations();
		MBeanOperationInfo targetOperation = null;
		if (operations != null) {
			for (int j = 0; j < operations.length; j++) {
				if (operations[j].getName().equals(optName)) {
					if (paramInfo.isMath(operations[j].getSignature())) {
						targetOperation = operations[j];
						break;
					}
				}
			}
		}

		// 如果找不到就抛错
		if (targetOperation == null) {
			throw new MyOperationNotFoundException(String.format("%s,参数:%s", optName, paramInfo.toString()));
		}

		try {
			// 执行操作
			Object returnValue = server.invoke(name, optName, paramInfo.getValues(), paramInfo.getTypes());

			if (log.isDebugEnabled()) {
				log.debug("invoke MBean:{}, operationName:{}, 参数:{}, 结果:{}",
						objectName, optName, paramInfo.toString(), returnValue);
			}

			JwInvokeOptResponse res = new JwInvokeOptResponse();
			res.setReturnData(String.valueOf(returnValue));
			res.setHasReturn(!targetOperation.getReturnType().equals("void"));

			return res;

		} catch (JMException e) {
			throw new MyJmException(e);
		}
	}

	/**
	 * 注册一个mbean，默认的domain是obj的包名，名字是类名
	 * 
	 * @param obj
	 */
	public void register(Object obj) {
		if (obj != null) {
			Class<?> clazz = obj.getClass();
			this.register(obj, clazz.getPackage().getName(), clazz.getSimpleName());
		}
	}

	/**
	 * 注册一个mbean，默认名字是类名
	 * 
	 * @param obj
	 */
	public void register(Object obj, String domain) {
		if (obj != null) {
			Class<?> clazz = obj.getClass();
			this.register(obj, domain, clazz.getSimpleName());
		}
	}

	/**
	 * 注册一个mbean
	 * 
	 * @param obj
	 *            mbean的对象
	 * @param domain
	 *            分类名
	 * @param name
	 *            mbean名
	 */
	public void register(Object obj, String domain, String name) {

		try {
			ObjectName oname = ObjectName.getInstance(domain + ":name=" + name);
			if (!this.server.isRegistered(oname)) {
				this.mBeanExporter.registerManagedResource(obj, oname);
			}
		} catch (Exception e) {
			log.error("jmx注册失败", e);
		}
	}

	public void unRegister(Object obj) {
		if (obj != null) {
			Class<?> clazz = obj.getClass();
			this.unRegister(clazz.getPackage().getName(), clazz.getSimpleName());
		}
	}

	/**
	 * 反注册一个mbean
	 * 
	 * @param domain
	 *            分类名
	 * @param name
	 *            mbean名
	 */
	public void unRegister(String domain, String name) {
		try {
			ObjectName oname = ObjectName.getInstance(domain + ":name=" + name);
			if (this.server.isRegistered(oname)) {
				this.server.unregisterMBean(oname);
			}
		} catch (Exception e) {
			log.error("jmx注销注册失败", e);
		}
	}

	/**
	 * 根据输入的字符串查找mbean
	 * 
	 * @param name
	 * @return
	 * @throws JMException
	 * @throws MyMBeanNotFoundException
	 */
	private MBeanInfo getMBeanInfoByName(ObjectName objectName) throws BaseApiException {
		if (server.isRegistered(objectName)) {
			try {
				return server.getMBeanInfo(objectName);
			} catch (JMException e) {
				throw new MyJmException(e);
			}
		} else {
			throw new MyMBeanNotFoundException(objectName.getCanonicalName());
		}
	}

	/**
	 * 将str转为 ObjectName
	 * 
	 * @param nameStr
	 * @return
	 * @throws MyMalformedObjectNameException
	 */
	private ObjectName getObjectName(String nameStr) throws MyMalformedObjectNameException {
		try {
			ObjectName objectName = new ObjectName(nameStr);
			return objectName;
		} catch (MalformedObjectNameException e) {
			throw new MyMalformedObjectNameException(nameStr);
		}
	}

}
