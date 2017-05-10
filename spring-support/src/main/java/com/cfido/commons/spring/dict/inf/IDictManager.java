package com.cfido.commons.spring.dict.inf;

import com.cfido.commons.annotation.api.AClass;
import com.cfido.commons.annotation.api.AMethod;
import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.apiServer.impl.CommonSuccessResponse;
import com.cfido.commons.spring.dict.inf.form.DictImportForm;
import com.cfido.commons.spring.dict.inf.form.DictKeyForm;
import com.cfido.commons.spring.dict.inf.form.DictRowEditForm;
import com.cfido.commons.spring.dict.inf.form.KeySearchPageForm;
import com.cfido.commons.spring.dict.inf.responses.DictKeySearchResponse;

/**
 * <pre>
 * 字典管理接口
 * </pre>
 * 
 * @author 梁韦江 2016年11月16日
 */
@AClass("dictManager")
@AComment(value = "字典词条管理")
public interface IDictManager {

	@AMethod(comment = "根据关键字搜索", saveFormToSession = true)
	DictKeySearchResponse search(KeySearchPageForm form);

	@AMethod(comment = "新增或者保存键值")
	DictKeySearchResponse save(DictRowEditForm form) throws BaseApiException;

	@AMethod(comment = "导入xml")
	CommonSuccessResponse importXml(DictImportForm form) throws BaseApiException;

	@AMethod(comment = "删除")
	DictKeySearchResponse delete(DictKeyForm form) throws BaseApiException;

}
