package com.cfido.commons.spring.dict.inf.form;

import org.springframework.util.StringUtils;

import com.cfido.commons.annotation.api.AForm;
import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.beans.form.PageForm;
import com.cfido.commons.spring.dict.schema.DictXml.DictAttachmentRow;
import com.cfido.commons.spring.dict.schema.DictXml.DictXmlRow;

/**
 * <pre>
 * 字典查询表单
 * </pre>
 * 
 * @author 梁韦江 2015年8月4日
 */
@AForm
public class KeySearchPageForm extends PageForm {

	private static final long serialVersionUID = 1L;

	private String key;

	public String getKey() {
		return key;
	}

	@AComment(value = "关键词")
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 判断这一行中是否包含了查询的关键词
	 */
	public boolean isMatch(DictXmlRow row) {
		if (!StringUtils.hasText(key)) {
			// 搜索关键词为空的时候，返回真
			return true;
		} else {
			// 非空的时候
			if (StringUtils.hasText(row.getKey())) {
				return row.getKey().contains(key);
			}
			return false;
		}
	}

	/**
	 * 判断这一行中是否包含了查询的关键词
	 */
	public boolean isMatch(DictAttachmentRow row) {
		if (!StringUtils.hasText(key)) {
			// 搜索关键词为空的时候，返回真
			return true;
		} else {
			// 非空的时候
			if (StringUtils.hasText(row.getKey())) {
				return row.getKey().contains(key);
			}
			return false;
		}
	}

}
