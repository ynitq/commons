package com.cfido.commons.spring.security;

import com.cfido.commons.beans.apiExceptions.InvalidLoginStatusException;
import com.cfido.commons.loginCheck.ANeedCheckLogin;

/** 对ANeedCheckLogin 进行检查 */
public interface INeedLoginChecker {

	void checkRight(ANeedCheckLogin loginCheck) throws InvalidLoginStatusException;

}
