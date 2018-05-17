package com.cfido.commons.spring.monitor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cfido.commons.annotation.other.AMonitorIngore;
import com.cfido.commons.spring.security.ActionInfo;
import com.cfido.commons.spring.security.ILoginCheckExtService;
import com.cfido.commons.spring.security.LoginExtCheckException;

/**
 * <pre>
 * 统计访问次数的服务
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
@Service
public class CountRequestExtService implements ILoginCheckExtService {

	@Autowired()
	private MonitorClientContext monitorClientContext;

	@Override
	public void beforeCheckRight(ActionInfo info, HttpServletResponse response, HttpServletRequest request) {
		if (!info.isHasTarget()) {
			// 只统计动态请求
			return;
		}

		AMonitorIngore monitorIngore = info.getAnnotationFromMethodAndClass(AMonitorIngore.class);
		if (monitorIngore != null) {
			// 如果这个方法没有被忽略，就增加访问次数统计
			this.monitorClientContext.addRequest();
		}
	}

	@Override
	public void afterCheckRight(ActionInfo info, HttpServletResponse response, HttpServletRequest request)
			throws LoginExtCheckException {

	}

}
