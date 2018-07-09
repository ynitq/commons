package com.cfido.commons.spring.dict.inf;

import com.cfido.commons.annotation.api.AClass;
import com.cfido.commons.annotation.api.AMethod;
import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.spring.dict.inf.responses.DictPublicResponse;

/**
 * <pre>
 * 字典管理用户接口
 * </pre>
 * 
 * @author 梁韦江 2016年11月16日
 */
@AClass("dictPublic")
@AComment(value = "字典-公开")
public interface IDictPublic {

	@AMethod(comment = "获得字典定义的所有内容")
	DictPublicResponse getDict();
}
