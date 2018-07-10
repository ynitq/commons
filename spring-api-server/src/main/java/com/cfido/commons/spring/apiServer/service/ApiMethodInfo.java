package com.cfido.commons.spring.apiServer.service;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.cfido.commons.annotation.api.AMethod;
import com.cfido.commons.beans.apiExceptions.InvalidLoginStatusException;
import com.cfido.commons.loginCheck.ANeedCheckLogin;
import com.cfido.commons.spring.apiServer.core.ApiServerInitException;
import com.cfido.commons.spring.apiServer.core.ApiServerUtils;
import com.cfido.commons.spring.apiServer.core.FindInferfaceResult;
import com.cfido.commons.spring.apiServer.core.MethodParamVo;
import com.cfido.commons.spring.security.BaseLoginContext;
import com.cfido.commons.utils.utils.ClassDescriber;
import com.cfido.commons.utils.utils.ClassUtil;
import com.cfido.commons.utils.web.BinderUtil;

/**
 * <pre>
 * 保存接口定义中的一个方法的信息
 * </pre>
 * 
 * @author 梁韦江 2016年6月28日
 */
public class ApiMethodInfo<T> {

	/**
	 * 根据接口名和方法名生成 key
	 * 
	 * @param infName
	 *            String
	 * @param methodName
	 *            String
	 * @return String
	 */
	public static String createKey(String infName, String methodName) {
		final String key = String.format("%s/%s", infName, methodName);
		return key;
	}

	private final Method apiMethod;// 接口中找到的方法
	private final Method implMethod; // 实现类中的该方法
	private final AMethod apiMethodAnno; // 接口方法上的注解
	private final Class<?> formClass;// 参数的类型
	private final Object implObj;// 实现类
	private final Class<?> infClass;// 接口类
	private final ANeedCheckLogin loginCheck; // 实现类中的 注解
	private String webUserClasses; // 如果需检查登录情况时，检查的用户类型
	private boolean needLogin = false; // 是否需要检查登录

	private final String memo;
	private final String methodKey;
	private final List<MethodParamVo> paramVoList;

	private final FindInferfaceResult resBean;

	private final Class<? extends T> returnClass; // 接口返还的数据类型

	private final String url;
	private final boolean uploadFile;

	private final Class<T> returnBaseClass; // 返回的数据类型的基类

	public static Comparator<ApiMethodInfo<?>> COMPARATOR = new Comparator<ApiMethodInfo<?>>() {

		@Override
		public int compare(ApiMethodInfo<?> o1, ApiMethodInfo<?> o2) {
			return o1.url.compareTo(o2.url);
		}

	};

	/**
	 * 构造，并检查合法性
	 * 
	 * @param implObj
	 *            实现类对象
	 * @param resBean
	 *            查询的结果bean
	 * @param imethod
	 *            接口中的方法
	 * @param apiMethodAnno
	 *            接口方法上的注解
	 * @throws ApiServerInitException
	 *             ApiServerInitException
	 */
	ApiMethodInfo(Object implObj, FindInferfaceResult resBean, Method imethod, AMethod apiMethodAnno,
			Class<T> returnBaseClass)
			throws ApiServerInitException {

		this.returnBaseClass = returnBaseClass;

		this.implObj = implObj;
		this.apiMethod = imethod;
		this.infClass = resBean.getInfClass();
		this.memo = apiMethodAnno.comment();
		this.apiMethodAnno = apiMethodAnno;

		try {
			this.implMethod = this.implObj.getClass().getMethod(this.apiMethod.getName(),
					this.apiMethod.getParameterTypes());
		} catch (final Exception e) {
			throw new RuntimeException("出现了奇怪的错误，实现类中居然找不到接口定义的方法", e);
		}

		this.resBean = resBean;

		this.loginCheck = this.buildLoginCheck();// 安全注解
		this.formClass = this.buildFormClass();// 先获得表单的class
		this.paramVoList = this.buildParamVoList();// 根据表单class生成表单参数的列表
		this.returnClass = this.buildReturnClass();// 检查返回的类型是否是基于BaseResponse的
		this.uploadFile = this.buildUploadFile();// 根据表单参数的列表，判断接口是否需要上传文件

		this.methodKey = this.buildMethodKey();
		this.url = createKey(this.getInfaceKey(), this.methodKey);

	}

	private ANeedCheckLogin buildLoginCheck() {
		// 先从方法和类上找注解
		ANeedCheckLogin a = ClassUtil.getAnnotationFromMethodAndClass(this.implMethod, ANeedCheckLogin.class);

		if (a != null) {
			this.webUserClasses = a.userClass().getSimpleName();
			this.needLogin = true;
		}

		return a;
	}

	private boolean buildUploadFile() {
		for (final MethodParamVo vo : paramVoList) {
			if (vo.isUploadFile()) {
				return true;
			}
		}
		return false;
	}

	private List<MethodParamVo> buildParamVoList() {
		List<MethodParamVo> list;
		if (this.hasParam()) {
			list = ApiServerUtils.getSetters(formClass);
			Collections.sort(list);
		} else {
			list = new LinkedList<>();
		}
		return list;
	}

	private String buildMethodKey() {
		// 默认的方法名是AMethod的 值
		String key = this.apiMethodAnno.url();
		if (StringUtils.isEmpty(key)) {
			// 如果注解中没有值，就用方法名
			key = this.apiMethod.getName();
		}
		return key;

	}

	@SuppressWarnings("unchecked")
	private Class<? extends T> buildReturnClass() throws ApiServerInitException {
		// 检查返回类型是否是 baseResponse
		final Class<?> tmpClass = this.apiMethod.getReturnType();
		if (!this.returnBaseClass.isAssignableFrom(tmpClass)) {
			final String msg = String.format("接口%s 中 %s的返回类型错误，必须是%s的基类", this.infClass.getSimpleName(),
					this.apiMethod.getName(), this.returnBaseClass.getSimpleName());
			throw new ApiServerInitException(msg);
		} else {
			return (Class<? extends T>) tmpClass;
		}
	}

	private Class<?> buildFormClass() throws ApiServerInitException {
		final int paramCount = this.apiMethod.getParameterTypes().length;
		if (paramCount > 1) {
			final String msg = String.format("接口%s 中 %s的参数错误，不能大于1个", this.infClass.getSimpleName(),
					this.apiMethod.getName());
			throw new ApiServerInitException(msg);
		}

		if (paramCount == 1) {
			// 如果有参数就记录下来
			final Class<?> formClass = this.apiMethod.getParameterTypes()[0];
			// if (!formClass.isAnnotationPresent(AForm.class)) {
			// String msg = String.format("接口%s 中 %s的参数错误，Form的类%s 必须有AFrom注解",
			// this.infClass.getSimpleName(),
			// this.apiMethod.getName(), formClass.getName());
			// throw new ApiServerInitException(msg);
			// }
			return formClass;
		} else {
			return null;
		}
	}

	/**
	 * 返回接口方法
	 * 
	 * @return Method
	 */
	public Method getApiMethod() {
		return apiMethod;
	}

	/**
	 * 返回类型的说明
	 * 
	 * @return String
	 */
	public String getReturnClassDesc() {
		return ClassDescriber.create(this.returnClass);
	}

	/**
	 * 获得表单的class
	 * 
	 * @return Class
	 */
	public Class<?> getFormClass() {
		return formClass;
	}

	/**
	 * 返回实现类
	 * 
	 * @return Object
	 */
	public Object getImplObj() {
		return implObj;
	}

	/**
	 * 获得接口的key
	 * 
	 * @return String
	 */
	public String getInfaceKey() {
		return this.resBean.getInfKey();
	}

	/**
	 * 获得该方法所在的接口的class
	 * 
	 * @return Class
	 */
	public Class<?> getInfClass() {
		return infClass;
	}

	/**
	 * 对接口class的说明
	 */
	public String getInfMemo() {
		return this.resBean.getInfMemo();
	}

	public String getKey() {
		return String.format("%s/%s", this.getInfClass().getSimpleName(), this.apiMethod.getName());
	}

	/**
	 * 获得备注
	 * 
	 * @return String
	 */
	public String getMemo() {
		if (StringUtils.isEmpty(this.memo)) {
			return this.url;
		} else {
			return this.memo;
		}
	}

	/**
	 * 获得方法的key
	 * 
	 * @return String
	 */
	public String getMethodKey() {
		return methodKey;
	}

	/**
	 * 获得表单中的所有属性
	 * 
	 * @return List-MethodParamVo
	 */
	public List<MethodParamVo> getParamVoList() {
		return paramVoList;
	}

	public String getUrl() {
		return url;
	}

	/**
	 * 该方法是否存在参数
	 * 
	 * @return boolean
	 */
	public boolean hasParam() {
		return this.formClass != null;
	}

	/**
	 * 执行实现类中的方法。
	 * 
	 * <pre>
	 * 执行前会校验一下form,如果不通过，会返回FormVaildateErrorResponse
	 * </pre>
	 * 
	 * @param form
	 *            Object
	 * @return BaseResponse
	 * @throws Exception
	 *             Exception
	 */
	@SuppressWarnings("unchecked")
	public T invoke(Object form) throws Exception {
		Object res = null;
		if (!this.hasParam() || form == null) {
			res = this.apiMethod.invoke(implObj);
		} else {
			BinderUtil.validateForm(form);
			res = this.apiMethod.invoke(implObj, form);
		}

		return (T) res;
	}

	/**
	 * 检查登录情况
	 * 
	 * @param loginContext
	 *            登录状态管理器
	 * @throws InvalidLoginStatusException
	 * 
	 */
	public void checkRights(BaseLoginContext loginContext) throws InvalidLoginStatusException {
		loginContext.checkRight(loginCheck);
	}

	/**
	 * 调试用，输出调试信息
	 * 
	 * @return String
	 */
	public String toDebugString() {
		final StringBuffer sb = new StringBuffer();

		sb.append("实现类:").append(this.implObj.getClass().getSimpleName());
		sb.append(" ");
		sb.append("返回类型:").append(this.returnClass.getSimpleName());
		sb.append(" ");
		if (this.hasParam()) {
			sb.append("参数：").append(this.formClass.getSimpleName());
		}

		return sb.toString();
	}

	/**
	 * 该方法调用时，是否需要检查登录状态
	 */
	public boolean isNeedLogin() {
		return this.needLogin;
	}

	/**
	 * 接口中是否需要上传文件
	 * 
	 * @return boolean
	 */
	public boolean isUploadFile() {
		return uploadFile;
	}

	/**
	 * 获得权限id，用于页面显示
	 * 
	 * @return String
	 */
	public String getOptId() {
		if (this.loginCheck != null) {
			return this.loginCheck.optId();
		}
		return null;
	}

	/**
	 * 获得所有的认证用户的类型，用于页面显示
	 * 
	 * @return String
	 */
	public String getWebUserClasses() {
		return this.webUserClasses;
	}

	/**
	 * 是否需要将form保存到session中
	 */
	public boolean isSaveFormToSession() {
		return this.apiMethodAnno.saveFormToSession();
	}

	public void setWebUserClasses(String webUserClasses) {
		this.webUserClasses = webUserClasses;
	}

	public void setNeedLogin(boolean needLogin) {
		this.needLogin = needLogin;
	}

	public Method getImplMethod() {
		return implMethod;
	}

}
