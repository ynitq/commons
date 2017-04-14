package com.cfido.commons.beans.apiServer.impl;

/**
 * <pre>
 * 用于描述表单校验信息错误的bean
 * 我们用，用JSR 303 - Bean Validation规范
 * 规范可参考http://www.ibm.com/developerworks/cn/java/j-lo-jsr303/
 * 
 * 样例代码见common-util的其中一个测试用例
 * 
 * javax.validation.ConstraintViolation error;
 * this.fieldName = error.getPropertyPath().toString();
 * this.errorMsg = error.getMessage();
 * 
 * </pre>
 * 
 * @author 梁韦江 2016年6月28日
 */
public class FormValidateErrorInfoBean {

	/**
	 * 校验出错的属性
	 */
	private final String fieldName;

	/**
	 * 错误信息
	 */
	private final String errorMsg;

	public FormValidateErrorInfoBean(String fieldName, String errorMsg) {
		super();
		this.fieldName = fieldName;
		this.errorMsg = errorMsg;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}
