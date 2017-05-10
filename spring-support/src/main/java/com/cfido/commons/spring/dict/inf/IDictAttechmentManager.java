package com.cfido.commons.spring.dict.inf;

import java.io.IOException;

import com.cfido.commons.annotation.api.AClass;
import com.cfido.commons.annotation.api.AMethod;
import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.spring.dict.inf.form.DictAttachmentEditForm;
import com.cfido.commons.spring.dict.inf.form.DictKeyForm;
import com.cfido.commons.spring.dict.inf.responses.DictAttachmentSearchResponse;

/**
 * <pre>
 * 字典管理接口
 * </pre>
 * 
 * @author 梁韦江 2016年11月16日
 */
@AClass("dictAttachments")
@AComment(value = "字典-附件文件管理")
public interface IDictAttechmentManager {

	@AMethod(comment = "获取所有附件")
	DictAttachmentSearchResponse list();

	@AMethod(comment = "新增或者保存键值")
	DictAttachmentSearchResponse save(DictAttachmentEditForm form) throws BaseApiException, IOException;

	@AMethod(comment = "删除")
	DictAttachmentSearchResponse delete(DictKeyForm form) throws BaseApiException, IOException;

}
