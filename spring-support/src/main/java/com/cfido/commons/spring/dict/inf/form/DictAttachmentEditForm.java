package com.cfido.commons.spring.dict.inf.form;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import com.cfido.commons.annotation.api.AForm;
import com.cfido.commons.annotation.bean.AComment;

/**
 * <pre>
 * 附件编辑表单
 * </pre>
 * 
 * @author 梁韦江 2016年11月16日
 */
@AForm
public class DictAttachmentEditForm {

	@NotEmpty(message = "Key不能为空")
	private String key;

	private MultipartFile file;

	/** 备注 */
	private String memo;

	public String getKey() {
		return key;
	}

	public String getMemo() {
		return memo;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	@AComment(comment = "键值")
	public void setKey(String key) {
		this.key = key;
	}

	@AComment(comment = "备注")
	public void setMemo(String memo) {
		this.memo = memo;
	}

}
