package com.cfido.commons.spring.apiServer.ajax;

import java.lang.annotation.Annotation;

import org.springframework.stereotype.Service;

import com.cfido.commons.annotation.api.AApiServerImpl;
import com.cfido.commons.beans.apiServer.BaseResponse;
import com.cfido.commons.spring.apiServer.service.BaseApiContainer;

/**
 * <pre>
 * 用于存储所有api的容器
 * </pre>
 * 
 * @author 梁韦江 2016年7月4日
 */
@Service
public class AjaxApiMapContainer extends BaseApiContainer<BaseResponse> {

	@Override
	protected Class<? extends Annotation> getAutoSearchAnnoClass() {
		return AApiServerImpl.class;
	}

}