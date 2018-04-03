package com.cfido.commons.spring.dict.inf.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cfido.commons.annotation.api.AApiServerImpl;
import com.cfido.commons.spring.debugMode.DebugModeProperties;
import com.cfido.commons.spring.dict.core.DictCoreService;
import com.cfido.commons.spring.dict.inf.IDictPublic;
import com.cfido.commons.spring.dict.inf.responses.DictPublicResponse;

/**
 * <pre>
 * 字典公开接口实现类
 * </pre>
 * 
 * @author 梁韦江
 */
@Service
@AApiServerImpl
public class DictPublicImpl implements IDictPublic {

	@Autowired
	private DictCoreService coreService;

	@Autowired
	private DebugModeProperties debugMode;

	@Override
	public DictPublicResponse getDict() {
		DictPublicResponse res = new DictPublicResponse();
		res.setData(this.coreService.toJsonMap());
		res.setDebugMode(this.debugMode.isDebugMode());
		return res;
	}
}
