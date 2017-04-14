package com.cfido.commons.spring.apiServer.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.cfido.commons.annotation.api.AForm;
import com.cfido.commons.annotation.api.AMethod;
import com.cfido.commons.beans.apiExceptions.InvalidLoginStatusException;
import com.cfido.commons.beans.apiServer.ApiCommonCode;
import com.cfido.commons.beans.apiServer.BaseResponse;
import com.cfido.commons.beans.exceptions.security.PermissionDeniedException;
import com.cfido.commons.spring.apiServer.service.ApiServerController;
import com.cfido.commons.spring.utils.MockDataCreater;
import com.cfido.commons.utils.utils.ClassUtil;
import com.cfido.commons.utils.utils.LogUtil;
import com.cfido.commons.utils.utils.StringUtils;
import com.cfido.commons.utils.web.BinderUtil;
import com.linzi.common.loginCheck.ANeedCheckLogin;
import com.linzi.common.loginCheck.IWebUser;
import com.linzi.common.loginCheck.IWebUserProvider;

/**
 * <pre>
 * 保存接口定义中的一个方法的信息
 * </pre>
 * 
 * @author 梁韦江
 *  2016年6月28日
 */
public class ApiMethodInfo implements Comparable<ApiMethodInfo> {

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
		String key = String.format("%s/%s", infName, methodName);
		return key;
	}

	private final Method apiMethod;// 接口中找到的方法
	private final Method implMethod; // 实现类中的该方法
	private final AMethod apiMethodAnno; // 接口方法上的注解
	private final Class<?> formClass;// 参数的类型
	private final Object implObj;// 实现类
	private final Class<?> infClass;// 接口类
	private final ANeedCheckLogin loginCheck; // 实现类中的 注解

	private final String memo;
	private final String methodKey;
	private final List<MethodParamVo> paramVoList;

	private final FindInferfaceResult resBean;

	private final Class<? extends BaseResponse> returnClass; // 接口返还的数据类型

	private final String url;
	private final boolean uploadFile;

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
	public ApiMethodInfo(Object implObj, FindInferfaceResult resBean, Method imethod, AMethod apiMethodAnno)
			throws ApiServerInitException {
		super();

		this.implObj = implObj;
		this.apiMethod = imethod;
		this.infClass = resBean.getInfClass();
		this.memo = apiMethodAnno.comment();
		this.apiMethodAnno = apiMethodAnno;

		try {
			this.implMethod = this.implObj.getClass().getMethod(this.apiMethod.getName(), this.apiMethod.getParameterTypes());
		} catch (Exception e) {
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

		this.checkCreateMockData();
	}

	private ANeedCheckLogin buildLoginCheck() {
		// 先从方法上找注解
		ANeedCheckLogin a = this.implMethod.getAnnotation(ANeedCheckLogin.class);

		if (a == null) {
			// 如果方法上没有注解，就在实现类上找
			a = ClassUtil.getAnnotation(this.implObj.getClass(), ANeedCheckLogin.class);
		}
		return a;
	}

	private boolean buildUploadFile() {
		for (MethodParamVo vo : paramVoList) {
			if (vo.isUploadFile()) {
				return true;
			}
		}
		return false;
	}

	private List<MethodParamVo> buildParamVoList() {
		List<MethodParamVo> list;
		if (this.hasParam()) {
			list = ApiServerUtils.getParamFromFormClass(formClass);
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
	private Class<? extends BaseResponse> buildReturnClass() throws ApiServerInitException {
		// 检查返回类型是否是 baseResponse
		Class<?> tmpClass = this.apiMethod.getReturnType();
		if (!BaseResponse.class.isAssignableFrom(tmpClass)) {
			String msg = String.format("接口%s 中 %s的返回类型错误，必须是BaseResponse", this.infClass.getSimpleName(),
					this.apiMethod.getName());
			throw new ApiServerInitException(msg);
		} else {
			return (Class<? extends BaseResponse>) tmpClass;
		}
	}

	private Class<?> buildFormClass() throws ApiServerInitException {
		int paramCount = this.apiMethod.getParameterTypes().length;
		if (paramCount > 1) {
			String msg = String.format("接口%s 中 %s的参数错误，不能大于1个", this.infClass.getSimpleName(),
					this.apiMethod.getName());
			throw new ApiServerInitException(msg);
		}

		if (paramCount == 1) {
			// 如果有参数就记录下来
			Class<?> formClass = this.apiMethod.getParameterTypes()[0];
			if (!formClass.isAnnotationPresent(AForm.class)) {
				String msg = String.format("接口%s 中 %s的参数错误，Form的类%s 必须有AFrom注解", this.infClass.getSimpleName(),
						this.apiMethod.getName(), formClass.getName());
				throw new ApiServerInitException(msg);
			}
			return formClass;
		} else {
			return null;
		}
	}

	@Override
	public int compareTo(ApiMethodInfo o) {
		return this.url.compareTo(o.url);
	}

	/**
	 * 生成模拟数据
	 * 
	 * @return BaseResponse
	 * @throws Exception
	 *             Exception
	 */
	public BaseResponse createMockData() throws Exception {
		BaseResponse res = MockDataCreater.newInstance(this.returnClass);
		res.setCode(ApiCommonCode.RESPONSE_OK);
		return res;
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
	 * 根据返回类型，生成模拟返回数据
	 * 
	 * @return String
	 */
	public String getDefaultMockData() {
		try {
			BaseResponse res = this.createMockData();
			return JSON.toJSONString(res, true);
		} catch (Exception ex) {
			return LogUtil.getTraceString("生成样例数据时出错了", ex);
		}
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

	/**
	 * 返回该方法返回对象的class
	 * 
	 * @return Class extends BaseResponse
	 */
	public Class<? extends BaseResponse> getReturnClass() {
		return returnClass;
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
	public BaseResponse invoke(Object form) throws Exception {
		Object res = null;
		if (!this.hasParam() || form == null) {
			res = this.apiMethod.invoke(implObj);
		} else {
			BinderUtil.validateForm(form);
			res = this.apiMethod.invoke(implObj, form);
		}

		return (BaseResponse) res;
	}

	/**
	 * 检查登录情况，该方法需要用到的 IWebUserProvider 需要从外部注入。所有可用在 invoke前仔细
	 * 
	 * @see ApiServerController#onBeforeInvoke
	 * 
	 * @param loginedUserProvider
	 *            已登录用户的查询接口
	 * @throws InvalidLoginStatusException
	 *             InvalidLoginStatusException
	 * 
	 * @see IWebUserProvider 已登录用户的容器
	 */
	public void checkRightUseLoginCheck(IWebUserProvider loginedUserProvider) throws InvalidLoginStatusException {
		if (this.loginCheck == null) {

			return;
		}

		Map<Class<? extends IWebUser>, IWebUser> userMap = new HashMap<>(4);
		for (Class<? extends IWebUser> userClass : this.loginCheck.userClass()) {
			IWebUser user = loginedUserProvider.getUser(userClass);
			if (user != null) {
				userMap.put(userClass, user);
			}
		}

		if (userMap.isEmpty()) {
			// 如果没找到登录用户就抛错
			throw new InvalidLoginStatusException();
		}

		if (StringUtils.isEmpty(this.loginCheck.optId())) {
			// 如果没有指定需要特殊检查的权限id，就直接通过
			return;
		}

		// 遍历所有的用户，检查指定要特殊检查的权限id
		for (IWebUser user : userMap.values()) {
			if (user.checkRights(this.loginCheck.optId())) {
				// 只要有其中一个用户能通过权限校验，就当通过了
				return;
			}
		}

		// 如果所有用户的权限都无法满足当前权限,就抛错
		throw new PermissionDeniedException();
	}

	/**
	 * 调试用，输出调试信息
	 * 
	 * @return String
	 */
	public String toDebugString() {
		StringBuffer sb = new StringBuffer();

		sb.append("实现类:").append(this.implObj.getClass().getSimpleName());
		sb.append(" ");
		if (this.hasParam()) {
			sb.append("参数：").append(this.formClass.getSimpleName());
		}

		return sb.toString();
	}

	/**
	 * 检查该方法是否能生成模拟数据
	 * 
	 * @throws ApiServerInitException
	 *             ApiServerInitException
	 */
	private void checkCreateMockData() throws ApiServerInitException {

		// 返回的类型必须能生成实例
		try {
			this.returnClass.newInstance();
		} catch (Exception e) {
			String msg = String.format("接口%s 中 %s的返回类型%s 无法创建实例，必须有无参数的构造方法",
					this.infClass.getSimpleName(),
					this.apiMethod.getName(), this.returnClass.getName());
			throw new ApiServerInitException(msg);
		}
	}

	/**
	 * 该方法调用时，是否需要检查登录状态
	 */
	public boolean isNeedLogin() {
		return this.loginCheck != null;
	}

	/**
	 * 该方法调用时，是否需要检查指定用户类型的登录状态
	 */
	public boolean isNeedLogin(Class<? extends IWebUser> webUserClass) {
		if (this.loginCheck == null) {
			return false;
		}

		for (Class<?> clazz : this.loginCheck.userClass()) {
			if (clazz == webUserClass) {
				return true;
			}
		}

		return false;
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
		if (this.loginCheck==null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();

		for (Class<?> clazz : this.loginCheck.userClass()) {
			if (sb.length() > 0) {
				sb.append(',');
			}
			sb.append(clazz.getSimpleName());
		}

		return sb.toString();
	}

	/**
	 * 是否需要将form保存到session中
	 */
	public boolean isSaveFormToSession() {
		return this.apiMethodAnno.saveFormToSession();
	}

}
