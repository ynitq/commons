package com.cfido.commons.utils.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.junit.Test;

/**
 * <pre>
 * 一个form校验的例子，用JSR 303 - Bean Validation规范
 * 规范可参考http://www.ibm.com/developerworks/cn/java/j-lo-jsr303/
 * 
 * 在本例中，该规范的实现包是Hibernate的，我们大多数的项目中，因为要使用spring4，所以都默认含有这些包
 * 
 * TODO 我们自己的form的校验可参考这个例子，虽然spring的@Valid可自动调用校验功能，但这和我们做session绑定的工具有冲突，只好自己弄
 * </pre>
 * 
 * @author 梁韦江 2015年7月20日
 */
public class FormValidationTest {

	public class JsonErrorInfoBean {
		private final String fieldName;
		private final String errorMsg;

		public JsonErrorInfoBean(ConstraintViolation<Object> error) {
			this.fieldName = error.getPropertyPath().toString();
			this.errorMsg = error.getMessage();
		}

		public String getFieldName() {
			return fieldName;
		}

		public String getErrorMsg() {
			return errorMsg;
		}

	}

	public class SimapleForm {

		@NotNull(message = "str1不能为空")
		private String str1;

		@NotNull(message = "str2不能为空")
		@Max(value = 6, message = "str2的值不能超过6")
		private String str2;

		public String getStr1() {
			return str1;
		}

		public void setStr1(String str1) {
			this.str1 = str1;
		}

	}

	@Test
	public void test1() {
		SimapleForm form = new SimapleForm();
		form.str2 = "13";
		form.str1 = "1";
		List<JsonErrorInfoBean> list = validateForm(form);

		StringBuffer sb = new StringBuffer(64);// 用于存储验证后的错误信息
		for (JsonErrorInfoBean bean : list) {
			sb.append(String.format("%s:%s\n", bean.getFieldName(), bean.getErrorMsg()));
		}
		System.out.println(sb.toString());
	}

	/**
	 * 校验form，并且返回一个可通过json返回到页面的list
	 * 
	 * @param form
	 * @return
	 */
	private List<JsonErrorInfoBean> validateForm(Object form) {// 验证某一个对象

		List<JsonErrorInfoBean> list = new LinkedList<FormValidationTest.JsonErrorInfoBean>();
		Validator validator = javax.validation.Validation.buildDefaultValidatorFactory()
				.getValidator();

		Set<ConstraintViolation<Object>> constraintViolations = validator
				.validate(form);// 验证某个对象,，其实也可以只验证其中的某一个属性的

		for (ConstraintViolation<Object> e : constraintViolations) {
			list.add(new JsonErrorInfoBean(e));
		}
		return list;
	}
}
