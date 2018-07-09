package com.cfido.commons.spring.dict.inf.form;

import javax.validation.constraints.NotNull;

import com.cfido.commons.annotation.api.AForm;
import com.cfido.commons.annotation.bean.AComment;

/**
 * <pre>
 * 字典key表单
 * </pre>
 * 
 * @author 梁韦江 2015年8月4日
 */
@AForm
public class DictKeyForm {

	@NotNull
	private String key;

	public String getKey() {
		return key;
	}

	@AComment(value = "关键词")
	public void setKey(String key) {
		this.key = key;
	}

}
