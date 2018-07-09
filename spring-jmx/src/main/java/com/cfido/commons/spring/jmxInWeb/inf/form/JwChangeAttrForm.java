package com.cfido.commons.spring.jmxInWeb.inf.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cfido.commons.annotation.api.AForm;

/**
 * <pre>
 * MBean操作的form
 * </pre>
 * 
 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a> 2015年10月15日
 */
@AForm
public class JwChangeAttrForm extends JwObjectNameForm {

	@NotNull(message = "属性名不能为空")
	@Size(min = 1, message = "属性名不能为空")
	private String name;// attr name

	private String value;

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}


	public void setValue(String value) {
		this.value = value;
	}

}
