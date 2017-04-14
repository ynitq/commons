package com.cfido.commons.beans.form;

import javax.validation.constraints.NotNull;

import com.cfido.commons.annotation.api.AForm;
import com.cfido.commons.annotation.api.AMock;

/**
 * <pre>
 * 通用的IdForm
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月25日
 */
@AForm
public class IdForm {

	@NotNull(message = "id不能为空")
	private Integer id;

	public Integer getId() {
		return id;
	}

	@AMock(value = "1")
	public void setId(Integer id) {
		this.id = id;
	}

}
