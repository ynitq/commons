package com.cfido.commons.utils.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.cfido.commons.annotation.form.AFormValidateMethod;
import com.cfido.commons.beans.apiServer.impl.FormValidateErrorInfoBean;
import com.cfido.commons.beans.exceptions.ValidateFormException;
import com.cfido.commons.utils.utils.LogUtil;

/**
 * FORM绑定工具
 * 
 * @author 梁韦江 2012-3-8
 */
public class BinderUtil {

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(BinderUtil.class);

	public final static String DATE_FORMAT = "yyyy-MM-dd";

	public final static String DATE_FORMAT_HAS_TIME = "yyyy-MM-dd HH:mm";

	/** 默认的重置表单的参数名 */
	public final static String RESET_PARAM_NAME = "reset";

	/** 默认时间查询周期，目前为60天 */
	public final static int DEFAUT_QUERY_DATE_RANGE = 60;

	/**
	 * <pre>
	 * 从request中获得form对象
	 * 大多数情况下，我们只需要调用这个方法
	 * </pre>
	 * 
	 * @param request
	 * @param formClazz
	 *            form的类
	 * @param saveToSession
	 *            是否存储到session
	 * @return
	 */
	public static <T> T bindForm(HttpServletRequest request, Class<T> formClazz, boolean saveToSession) {
		return bindForm(request, formClazz, saveToSession, RESET_PARAM_NAME, true);
	}

	/**
	 * 从request中获得form对象,并且校验
	 * 
	 * @param request
	 * @param formClazz
	 *            form的类
	 * @param saveToSession
	 *            是否存储到session
	 * @return
	 * @throws ValidateFormException
	 *             如果校验失败，这里可获得错误信息
	 */
	public static <T> T bindFormAndValidate(HttpServletRequest request, Class<T> formClazz, boolean saveToSession)
			throws ValidateFormException {
		T form = bindForm(request, formClazz, saveToSession, RESET_PARAM_NAME, true);
		validateForm(form);
		return form;
	}

	/**
	 * <pre>
	 * 从request中获得form对象
	 * 大多数情况下，我们只需要调用这个方法
	 * </pre>
	 * 
	 * @param request
	 * @param formClazz
	 *            form的类
	 * @param saveToSession
	 *            是否存储到session
	 * @return
	 */
	public static <T> T bindForm(HttpServletRequest request, Class<T> formClazz, boolean saveToSession, boolean validate) {
		return bindForm(request, formClazz, saveToSession, RESET_PARAM_NAME, validate);
	}

	/**
	 * 从request中获得form对象
	 * 
	 * @param request
	 * @param formClazz
	 *            form的类
	 * @param saveToSession
	 *            是否保存到session
	 * @param resetParam
	 *            重置form的参数，如果url出现该参数，则创建空实例
	 * @param validate
	 *            是否调用校验
	 * @return
	 * @see AFormValidateMethod 校验的方法需要有 FormValidateMethod 这个声明
	 */
	public static <T> T bindForm(HttpServletRequest request, Class<T> formClazz, boolean saveToSession,
			String resetParam,
			boolean validate) {

		// 将路径信息放到session名字中，防止有session名的重复
		String pathSessionName = getSessionNameOfForm(request, formClazz);

		return bindForm(request, formClazz, saveToSession, resetParam, validate, pathSessionName);
	}

	/**
	 * 从request中获得form对象
	 * 
	 * @param request
	 * @param formClazz
	 *            form的类
	 * @param saveToSession
	 *            是否保存到session
	 * @param resetParam
	 *            重置form的参数，如果url出现该参数，则创建空实例
	 * @param validate
	 *            是否调用校验
	 * @param sessionName
	 *            可指定session的名字
	 * @return 表单对象
	 * @see AFormValidateMethod 校验的方法需要有 FormValidateMethod 这个声明
	 */
	@SuppressWarnings("unchecked")
	public static <T> T bindForm(HttpServletRequest request, Class<T> formClazz, boolean saveToSession,
			String resetParam, boolean validate, String sessionName) {
		try {
			T form = formClazz.newInstance();

			if (request != null && saveToSession) {

				// 如果sessionName不为空，才需要从session中获取
				if (StringUtils.isEmpty(resetParam) || !request.getParameterMap().containsKey(resetParam)) {
					// 如果不需要重置，才需要从session中获取
					form = (T) request.getSession().getAttribute(sessionName);
					if (form == null) {
						// 如果session中没有，就创建新实例
						form = formClazz.newInstance();
					}
				}
				request.getSession().setAttribute(sessionName, form);
			}

			Map<String, String[]> map = new HashMap<>();
			Enumeration<String> en = request.getParameterNames();
			while (en.hasMoreElements()) {
				String key = en.nextElement();
				String[] value = request.getParameterValues(key);
				map.put(key, value);
			}

			BinderEditorSupport.updateObj(request, map, form, validate);

			return form;
		} catch (Exception e) {
			LogUtil.traceError(log, e);
			return null;
		}
	}

	/**
	 * 从session中获取表单
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param formClazz
	 *            表单类型
	 * @param sessionName
	 *            session的名字
	 * @return
	 */
	public static <T> T getFormFromSession(HttpServletRequest request, Class<T> formClazz, String sessionName) {

		Assert.hasText(sessionName, "sessionName不能为空");

		@SuppressWarnings("unchecked")
		T form = (T) request.getSession().getAttribute(sessionName);

		if (form == null) {
			try {
				form = formClazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("创建表单实例时出错了 表单:" + formClazz.getName(), e);
			}
		}
		return form;

	}

	/**
	 * 根据表单类型和url，构建保存到session中的名字
	 * 
	 * @param request
	 * @param formClazz
	 *            表单类型
	 * @return
	 */
	public static String getSessionNameOfForm(HttpServletRequest request, Class<?> formClazz) {
		// 将路径信息放到session名字中，防止有session名的重复
		String pathSessionName = String.format("bindForm:%s:%s",
				request.getRequestURI(),
				formClazz.getName());
		return pathSessionName;

	}

	/**
	 * 校验form，并且返回一个可通过json返回到页面的list
	 * 
	 * @param form
	 *            要验证的表单
	 * @throws ValidateFormException
	 *             验证出错时的信息
	 */
	public static void validateForm(Object form) throws ValidateFormException {
		if (form == null) {
			return;
		}

		// 验证某一个对象
		Validator validator = javax.validation.Validation.buildDefaultValidatorFactory().getValidator();

		// 验证某个对象,，其实也可以只验证其中的某一个属性的
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(form);

		if (!constraintViolations.isEmpty()) {

			List<FormValidateErrorInfoBean> list = new LinkedList<>();
			// 如果验证通过，就没有error
			for (ConstraintViolation<Object> error : constraintViolations) {
				String fieldName = error.getPropertyPath().toString();
				String errorMsg = error.getMessage();

				FormValidateErrorInfoBean bean = new FormValidateErrorInfoBean(fieldName, errorMsg);

				list.add(bean);
			}

			ValidateFormException ex = new ValidateFormException(form, list);

			throw ex;
		}
	}
}
