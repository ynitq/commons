package com.cfido.commons.spring.dict.inf;

import com.cfido.commons.annotation.api.AClass;
import com.cfido.commons.annotation.api.AMethod;
import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.beans.apiServer.impl.CommonSuccessResponse;
import com.cfido.commons.beans.exceptions.security.InvalidPasswordException;
import com.cfido.commons.beans.form.LoginForm;
import com.cfido.commons.spring.dict.inf.form.CreatePasswordForm;
import com.cfido.commons.spring.dict.inf.responses.UserInfoResponse;

/**
 * <pre>
 * 字典管理用户接口
 * </pre>
 * 
 * @author 梁韦江 2016年11月16日
 */
@AClass("dictAdmin")
@AComment(comment = "字典用户相关")
public interface IDictAdminUser {

	@AMethod(comment = "获得当前用户的信息，返回内容中包含是是否已登录的信息")
	UserInfoResponse getCurUser();

	@AMethod(comment = "登录")
	UserInfoResponse login(LoginForm form) throws InvalidPasswordException;

	@AMethod(comment = "登出")
	UserInfoResponse logout();

	@AMethod(comment = "创建密码例子")
	CommonSuccessResponse passwordDemo(CreatePasswordForm form);
}
