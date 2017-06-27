package com.cfido.commons.utils.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartException;

import com.cfido.commons.annotation.api.ADataInApiException;
import com.cfido.commons.beans.apiExceptions.MissFieldException;
import com.cfido.commons.beans.apiExceptions.SystemErrorException;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.apiServer.impl.CommonErrorResponse;
import com.cfido.commons.beans.apiServer.impl.FormVaildateErrorResponse;
import com.cfido.commons.beans.exceptions.ValidateFormException;
import com.cfido.commons.utils.utils.MethodUtil.MethodInfoOfGetter;

/**
 * <pre>
 * 用于抛错的工具
 * </pre>
 * 
 * @author 梁韦江 2016年9月2日
 */
public class ExceptionUtil {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ExceptionUtil.class);

	public static void notNull(Object obj, String message) throws MissFieldException {
		if (obj == null) {
			throw new MissFieldException(message);
		}
	}

	public static void hasText(String str, String message) throws MissFieldException {
		if (StringUtils.isEmpty(str)) {
			throw new MissFieldException(message);
		}
	}

	public static void isEmail(String str, String message) throws MissFieldException {
		if (StringUtils.isEmpty(str) || !EmailUtil.isEmail(str)) {
			throw new MissFieldException(message);
		}
	}

	public static void notZero(int value, String message) throws MissFieldException {
		if (value <= 0) {
			throw new MissFieldException(message);
		}
	}

	/**
	 * 根据错误信息，返回不同类型的 json返回
	 */
	public static CommonErrorResponse getErrorResponse(HttpServletResponse response, Throwable exParam, boolean debugMode) {
		CommonErrorResponse res;

		int statusCode = 500;

		if (exParam == null) {
			res = new CommonErrorResponse();
			res.setErrorMsg("未知错误");
		} else {
			Throwable e = exParam;
			if (e instanceof InvocationTargetException) {
				// 如果类型是 InvocationTargetException， 里面的类型才是真正的错误
				e = ((InvocationTargetException) exParam).getTargetException();
			}

			if (e instanceof BaseApiException) {

				BaseApiException e1 = (BaseApiException) e;

				statusCode = e1.getHttpStatusCode();

				log.debug("调用api时发生逻辑错误: {} ", e1.getErrorMsg());

				// 如果是逻辑错误，就将逻辑错误变成response
				res = new CommonErrorResponse(e1);
				// 同时设置额外的数据进去
				res.setExData(getExDataFromApiException(e1));

			} else if (e instanceof ValidateFormException) {
				log.debug("调用api, 表单验证不通过 ");
				res = new FormVaildateErrorResponse((ValidateFormException) e);
			} else {
				// 其他错误时，返回系统错误
				LogUtil.traceError(log, e, "调用api时发生其他错误 ");
				res = getSystemErrorResponse(e, debugMode);
			}
		}

		response.setStatus(statusCode);

		return res;
	}

	/**
	 * 获取ApiException上的额外数据
	 */
	private static Map<String, Object> getExDataFromApiException(BaseApiException ex) {
		if (ex == null) {
			return null;
		}

		Map<String, Object> map = new HashMap<>();
		Class<?> clazz = ex.getClass();

		List<MethodInfoOfGetter> getterList = MethodUtil.findGetter(clazz);
		for (MethodInfoOfGetter mi : getterList) {
			if (mi.isOpenType() && mi.getAnnotation(ADataInApiException.class, false) != null) {
				// 如果是 系统的类型，并且属性上ADataInApiException有注解
				Object value = null;
				try {
					value = mi.getOriginMethod().invoke(ex);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				}
				if (value != null) {
					map.put(mi.getPropName(), value);
				}
			}
		}

		return map;
	}

	private static String getSize(long size) {
		if (size >= 1024 * 1024) {
			long t1 = 10 * size / 1024 / 1024;
			float t2 = t1 / 10f;
			return String.format("%.1fM", t2);
		} else {
			long t1 = 10 * size / 1024;
			float t2 = t1 / 10f;
			return String.format("%.1fK", t2);
		}
	}

	/**
	 * 系统运行时错误
	 * 
	 * @param ex
	 *            Throwable
	 * @param debugMode
	 * 
	 * @return CommonErrorResponse
	 */
	private static CommonErrorResponse getSystemErrorResponse(Throwable ex, boolean debugMode) {

		CommonErrorResponse res;
		if (ex instanceof MultipartException) {
			String msg = "上传文件时发生了错误";

			Throwable root = ((MultipartException) ex).getRootCause();
			if (root != null) {
				if (root instanceof FileSizeLimitExceededException) {
					FileSizeLimitExceededException e = (FileSizeLimitExceededException) root;

					msg = String.format("上传文件不能超过 %s", getSize(e.getPermittedSize()));
				}
			}

			res = new CommonErrorResponse(ex, msg);
		} else {

			SystemErrorException error = new SystemErrorException(ex);
			res = new CommonErrorResponse(error);
		}
		if (debugMode) {
			// 如果是开发模式，将调查错误过程回馈给客户端
			String str = LogUtil.getTraceString(null, ex);
			// 过滤掉中间 \t \r \n 之类的字符
			res.setDebugMsg(com.cfido.commons.utils.utils.StringUtilsEx.trimMiddleWhitespace(str));
		}
		return res;

	}
}
