package com.cfido.commons.spring.jmxInWeb.inf.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cfido.commons.annotation.api.AForm;

/**
 * <pre>
 * MBean操作的form的基类
 * </pre>
 * 
 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a> 2015年10月15日
 */
@AForm
public class JwObjectNameForm {

	@NotNull(message = "objectName不能为空")
	@Size(min = 1, message = "objectName不能为空")
	private String objectName;

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
}
