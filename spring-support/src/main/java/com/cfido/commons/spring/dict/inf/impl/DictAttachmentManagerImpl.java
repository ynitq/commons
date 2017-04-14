package com.cfido.commons.spring.dict.inf.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cfido.commons.annotation.api.AApiServerImpl;
import com.cfido.commons.beans.apiExceptions.SimpleApiException;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.spring.dict.core.DictAdminWebUser;
import com.cfido.commons.spring.dict.core.DictCoreService;
import com.cfido.commons.spring.dict.inf.IDictAttechmentManager;
import com.cfido.commons.spring.dict.inf.form.DictAttachmentEditForm;
import com.cfido.commons.spring.dict.inf.form.DictKeyForm;
import com.cfido.commons.spring.dict.inf.responses.DictAttachmentSearchResponse;
import com.cfido.commons.spring.dict.inf.responses.DictAttachmentVo;
import com.linzi.common.loginCheck.ANeedCheckLogin;

/**
 * <pre>
 * 字典管理的实现类
 * </pre>
 * 
 * @author 梁韦江 2016年11月16日
 */
@Service
@AApiServerImpl
@ANeedCheckLogin(userClass = DictAdminWebUser.class)
public class DictAttachmentManagerImpl implements IDictAttechmentManager {

	@Autowired
	private DictCoreService coreService;

	@Override
	public DictAttachmentSearchResponse list() {

		List<DictAttachmentVo> list = this.coreService.getAllAttechmentsFromMap();

		DictAttachmentSearchResponse res = new DictAttachmentSearchResponse();
		res.setList(list);

		return res;
	}

	@Override
	public DictAttachmentSearchResponse save(DictAttachmentEditForm form) throws BaseApiException, IOException {

		if (!StringUtils.hasText(form.getKey())) {
			throw new SimpleApiException("键值不能为空");
		}

		// 保存
		this.coreService.saveAttachmentRow(form);

		// 返回列表结果，用于更新页面的列表页
		return this.list();
	}

	@Override
	public DictAttachmentSearchResponse delete(DictKeyForm form) throws BaseApiException, IOException {
		// 删除
		this.coreService.deleteAttachmengRow(form.getKey());

		return this.list();
	}
}
