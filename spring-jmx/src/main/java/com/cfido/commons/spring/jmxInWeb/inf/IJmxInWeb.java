package com.cfido.commons.spring.jmxInWeb.inf;

import com.cfido.commons.annotation.api.AClass;
import com.cfido.commons.annotation.api.AMethod;
import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.apiServer.impl.CommonSuccessResponse;
import com.cfido.commons.beans.exceptions.security.InvalidPasswordException;
import com.cfido.commons.beans.form.LoginForm;
import com.cfido.commons.spring.jmxInWeb.inf.form.JwChangeAttrForm;
import com.cfido.commons.spring.jmxInWeb.inf.form.JwInvokeOptForm;
import com.cfido.commons.spring.jmxInWeb.inf.form.JwObjectNameForm;
import com.cfido.commons.spring.jmxInWeb.inf.response.JwInvokeOptResponse;
import com.cfido.commons.spring.jmxInWeb.inf.response.JwMBeanInfoResponse;
import com.cfido.commons.spring.jmxInWeb.inf.response.JwMBeanListResponse;

/**
 * <pre>
 * 字典管理用户接口
 * </pre>
 * 
 * @author 梁韦江 2016年11月16日
 */
@AClass("jmxInWeb")
@AComment(value = "JmxInWeb-JMX管理")
public interface IJmxInWeb {

	@AMethod(comment = "登录")
	CommonSuccessResponse login(LoginForm form) throws InvalidPasswordException;

	@AMethod(comment = "登出")
	CommonSuccessResponse logout();

	@AMethod(comment = "获取所有的MBean")
	JwMBeanListResponse getMBeanList() throws BaseApiException;

	@AMethod(comment = "改变一个属性")
	CommonSuccessResponse changeAttr(JwChangeAttrForm form) throws BaseApiException;

	@AMethod(comment = "调用一个方法")
	JwInvokeOptResponse invokeOpt(JwInvokeOptForm form) throws BaseApiException;

	@AMethod(comment = "获取一个MBean的详情")
	JwMBeanInfoResponse getMBeanInfo(JwObjectNameForm form) throws BaseApiException;

}
