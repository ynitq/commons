package com.cfido.commons.spring.dict.inf.form;

import org.springframework.web.multipart.MultipartFile;

import com.cfido.commons.annotation.api.AForm;
import com.cfido.commons.annotation.bean.AComment;

/**
 * <pre>
 * 用于导入xml的表单
 * </pre>
 * 
 * @author 梁韦江 2016年11月17日
 */
@AForm
public class DictImportForm {

	private MultipartFile file;

	private boolean cleanOld;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public boolean isCleanOld() {
		return cleanOld;
	}

	@AComment(value = "导入时，是否清空原有数据")
	public void setCleanOld(boolean cleanOld) {
		this.cleanOld = cleanOld;
	}

}
