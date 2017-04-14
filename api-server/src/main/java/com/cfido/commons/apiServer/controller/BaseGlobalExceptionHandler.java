package com.cfido.commons.apiServer.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfido.commons.beans.apiExceptions.SystemErrorException;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.apiServer.impl.CommonErrorResponse;
import com.cfido.commons.utils.utils.LogUtil;

/**
 * <pre>
 * 全局的错误拦截器
 * 
 * 在子类需要写 @ControllerAdvice这个注解
 * </pre>
 * 
 * @see ControllerAdvice
 * @author 梁韦江
 *  2016年7月5日
 */
// @ControllerAdvice
public abstract class BaseGlobalExceptionHandler {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BaseGlobalExceptionHandler.class);

	/**
	 * 拦截常规错误
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public @ResponseBody CommonErrorResponse runtimeExceptionHandler(Exception e) {
		// 其他错误时，返回系统错误
		log.error("调用api时发生系统错误 ", e);

		return this.getSystemErrorResponse(e);
	}

	/**
	 * 拦截api逻辑错误
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(BaseApiException.class)
	public @ResponseBody CommonErrorResponse apiExceptionnHandler(BaseApiException e) {
		log.error("调用api时发生逻辑错误: {} ", e.getErrorMsg());

		// 如果是逻辑错误，就将逻辑错误变成response
		return new CommonErrorResponse(e);
	}

	/**
	 * 系统运行时错误
	 * 
	 * @param ex
	 *            Throwable
	 * @return
	 */
	private CommonErrorResponse getSystemErrorResponse(Throwable ex) {
		SystemErrorException error = new SystemErrorException(ex);
		CommonErrorResponse res = new CommonErrorResponse(error);

		if (log.isDebugEnabled()) {
			// 如果是开发模式，将调查错误过程回馈给客户端
			res.setDebugMsg(LogUtil.getTraceString(null, ex));
		}
		return res;

	}
}
