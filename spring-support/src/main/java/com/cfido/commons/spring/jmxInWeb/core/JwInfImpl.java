package com.cfido.commons.spring.jmxInWeb.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
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
import org.springframework.util.Assert;

import com.cfido.commons.annotation.api.AApiServerImpl;
import com.cfido.commons.beans.apiExceptions.SystemErrorException;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.apiServer.impl.CommonSuccessResponse;
import com.cfido.commons.beans.exceptions.security.InvalidPasswordException;
import com.cfido.commons.beans.form.LoginForm;
import com.cfido.commons.loginCheck.ANeedCheckLogin;
import com.cfido.commons.loginCheck.IWebUser;
import com.cfido.commons.spring.jmxInWeb.JmxInWebProperties;
import com.cfido.commons.spring.jmxInWeb.exception.MyAttrNotFoundException;
import com.cfido.commons.spring.jmxInWeb.exception.MyJmException;
import com.cfido.commons.spring.jmxInWeb.exception.MyMBeanNotFoundException;
import com.cfido.commons.spring.jmxInWeb.exception.MyMalformedObjectNameException;
import com.cfido.commons.spring.jmxInWeb.exception.MyOperationNotFoundException;
import com.cfido.commons.spring.jmxInWeb.inf.IJmxInWeb;
import com.cfido.commons.spring.jmxInWeb.inf.form.JwChangeAttrForm;
import com.cfido.commons.spring.jmxInWeb.inf.form.JwInvokeOptForm;
import com.cfido.commons.spring.jmxInWeb.inf.form.JwObjectNameForm;
import com.cfido.commons.spring.jmxInWeb.inf.response.JwInvokeOptResponse;
import com.cfido.commons.spring.jmxInWeb.inf.response.JwMBeanInfoResponse;
import com.cfido.commons.spring.jmxInWeb.inf.response.JwMBeanListResponse;
import com.cfido.commons.spring.jmxInWeb.models.DomainVo;
import com.cfido.commons.spring.jmxInWeb.models.MBeanVo;
import com.cfido.commons.spring.security.IUserServiceForRememberMe;
import com.cfido.commons.spring.security.LoginContext;
import com.cfido.commons.spring.security.RememberMeUserHandler;
import com.cfido.commons.utils.utils.LogUtil;
import com.cfido.commons.utils.utils.OpenTypeUtil;
import com.cfido.commons.utils.utils.PasswordEncoder;

/**
 * <pre>
 * 接口的实现类
 * </pre>
 * 
 * @author 梁韦江 2017年4月25日
 */
@Service
@AApiServerImpl
public class JwInfImpl implements IJmxInWeb {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwInfImpl.class);

	@Autowired
	private RememberMeUserHandler rememberMeUserHandler;

	@Autowired
	private LoginContext loginContext;

	@Autowired
	private JmxInWebProperties prop;

	@Autowired
	private MBeanExporter mBeanExporter;

	/** 在init() 中初始化 */
	private MBeanServer server;

	/** 管理用户是否是在配置文件中定义的 */
	private boolean adminInPorp;

	@PostConstruct
	protected void init() {

		this.server = this.mBeanExporter.getServer();

		// 检查管理用户的认证提供者
		if (this.rememberMeUserHandler.getUserProvider(JwWebUser.class) == null) {
			// 如果没有，就使用配置文件中的信息
			this.rememberMeUserHandler.addUserProvider(this.prop.getAdminUserProvider());
			this.adminInPorp = true;
			log.info("初始化 字典管理用户，账号由配置文件设置");
		} else {
			log.info("初始化 字典管理用户，账号由数据库管理");
			this.adminInPorp = false;
		}
	}

	@Override
	public CommonSuccessResponse login(LoginForm form) throws InvalidPasswordException {
		Assert.notNull(form.getAccount(), "账号不能为空");
		Assert.notNull(form.getPassword(), "密码不能为空");

		log.debug("管理用户 {} 登录后台", form.getAccount());

		// 寻找用户认证供应者
		IUserServiceForRememberMe userProvider = this.rememberMeUserHandler.getUserProvider(JwWebUser.class);
		if (userProvider != null) {
			// 如果存在，就尝试获取用户
			IWebUser user = userProvider.loadUserByUsername(form.getAccount());
			if (user != null) {
				// 如过能获取用户,就检查密码
				PasswordEncoder.checkPassword(form.getPassword(), user.getPassword());

				// 如果密码正确，就返回正常信息
				this.loginContext.onLoginSuccess(user, form.isRememberMe());

				return CommonSuccessResponse.DEFAULT;
			}
		}

		throw new InvalidPasswordException();
	}

	@Override
	public CommonSuccessResponse logout() {
		this.loginContext.onLogout(JwWebUser.class);
		return CommonSuccessResponse.DEFAULT;
	}

	@Override
	@ANeedCheckLogin(userClass = JwWebUser.class)
	public JwMBeanListResponse getMBeanList() throws BaseApiException {

		try {
			// 从Server中查询出所有的MBean
			Set<ObjectInstance> mbeans = server.queryMBeans(null, null);

			Map<String, DomainVo> domainMap = new HashMap<>();
			for (ObjectInstance instance : mbeans) {

				ObjectName name = instance.getObjectName();

				String domainName = name.getDomain();

				if (this.prop.getDomainNameFilter().show(domainName)) {
					// 如果过滤器认为这个domain该显示

					// 检查Map中是否已经有这个Domain，无故没有就添加
					DomainVo domainVo = domainMap.get(domainName);
					if (domainVo == null) {
						domainVo = new DomainVo(domainName);
						domainMap.put(domainName, domainVo);
					}

					// 获取这个Mean的信息
					MBeanInfo info = server.getMBeanInfo(name);

					// 将mbean信息添加到domain的mbean列表中
					domainVo.addMBean(name, info);
				}
			}

			// 将domain map中的所有内容添加到list中
			List<DomainVo> domainList = new LinkedList<>();
			domainList.addAll(domainMap.values());
			// 根据domain名字排序
			Collections.sort(domainList);

			// 封装返回结果
			JwMBeanListResponse res = new JwMBeanListResponse();
			res.setList(domainList);
			return res;

		} catch (JMException e) {
			throw new MyJmException(e);
		}
	}

	@Override
	@ANeedCheckLogin(userClass = JwWebUser.class)
	public CommonSuccessResponse changeAttr(JwChangeAttrForm form) throws BaseApiException {
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

		return CommonSuccessResponse.DEFAULT;
	}

	@Override
	@ANeedCheckLogin(userClass = JwWebUser.class)
	public JwInvokeOptResponse invokeOpt(JwInvokeOptForm form) throws BaseApiException {
		ObjectName name = this.getObjectName(form.getObjectName());

		// Find target attribute
		MBeanInfo info = this.getMBeanInfoByName(name);

		// get param info
		JwInvokeOptForm.ParamInfo paramInfo = form.getParamInfo();

		// 寻找对应的操作
		MBeanOperationInfo[] operations = info.getOperations();
		MBeanOperationInfo targetOperation = null;
		if (operations != null) {
			for (int j = 0; j < operations.length; j++) {
				if (operations[j].getName().equals(form.getOptName())) {
					if (paramInfo.isMath(operations[j].getSignature())) {
						targetOperation = operations[j];
						break;
					}
				}
			}
		}

		// 如果找不到就抛错
		if (targetOperation == null) {
			throw new MyOperationNotFoundException(paramInfo.getOperationsInfo());
		}

		try {
			// 执行操作
			Object returnValue = server.invoke(name, form.getOptName(), paramInfo.getValues(), paramInfo.getTypes());

			if (log.isDebugEnabled()) {
				log.debug(LogUtil.format("invode %s %s value=%s", targetOperation.getReturnType(), paramInfo.getOperationsInfo(),
						returnValue));
			}

			JwInvokeOptResponse res = new JwInvokeOptResponse();
			res.setReturnData(returnValue);
			res.setHasReturn(!targetOperation.getReturnType().equals("void"));
			res.setOpName(form.getOptName());

			return res;
		} catch (JMException e) {
			throw new MyJmException(e);
		}
	}

	@Override
	@ANeedCheckLogin(userClass = JwWebUser.class)
	public JwMBeanInfoResponse getMBeanInfo(JwObjectNameForm form) throws BaseApiException {
		try {
			ObjectName objectName = new ObjectName(form.getObjectName());
			if (!objectName.isPattern() && server.isRegistered(objectName)) {
				MBeanInfo info = server.getMBeanInfo(objectName);
				MBeanVo vo = new MBeanVo(objectName, info);
				vo.setValueFromMBeanServer(server);

				JwMBeanInfoResponse res = new JwMBeanInfoResponse();
				res.setInfo(vo);
				return res;
			} else {
				throw new MyMBeanNotFoundException(form.getObjectName());
			}
		} catch (JMException e) {
			throw new SystemErrorException(e);
		}
	}

	public boolean isAdminInPorp() {
		return adminInPorp;
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

	private ObjectName getObjectName(String nameStr) throws MyMalformedObjectNameException {
		try {
			ObjectName objectName = new ObjectName(nameStr);
			return objectName;
		} catch (MalformedObjectNameException e) {
			throw new MyMalformedObjectNameException(nameStr);
		}
	}

}
