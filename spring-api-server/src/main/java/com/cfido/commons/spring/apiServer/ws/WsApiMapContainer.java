package com.cfido.commons.spring.apiServer.ws;

import java.lang.annotation.Annotation;

import org.springframework.stereotype.Service;

import com.cfido.commons.annotation.api.ClientApiImpl;
import com.cfido.commons.spring.apiServer.beans.ws.BaseSocketResponse;
import com.cfido.commons.spring.apiServer.service.ApiMethodInfo;
import com.cfido.commons.spring.apiServer.service.BaseApiContainer;

/**
 * 为爬虫提供的Api容器
 * 
 * 所有配置了 {@link ClientApiImpl} 这个标注的Api实现都会添加进这个容器
 * 
 * @author lxy
 *
 */
@Service
public class WsApiMapContainer extends BaseApiContainer<BaseSocketResponse> {

	@Override
	protected Class<? extends Annotation> getAutoSearchAnnoClass() {
		return ClientApiImpl.class;
	}
	
	@Override
	protected void afterAddToMap(ApiMethodInfo<BaseSocketResponse> info) {
		
	}

}
