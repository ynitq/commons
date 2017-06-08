package com.cfido.commons.utils.db;

import org.junit.Test;

import com.cfido.commons.annotation.form.ABuildWhereFieldName;
import com.cfido.commons.beans.form.PageForm;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author 梁韦江 2015年7月19日
 */
public class WhereBuilderTest {

	/**
	 * <pre>
	 * 企业列表查询条件Form
	 * </pre>
	 * 
	 * @author 黄云 2015-6-17
	 */
	public class InsSearchForm extends PageForm {

		private static final long serialVersionUID = 1L;
		private String test;

		@ABuildWhereFieldName(name = "testField")
		public String getTest() {
			return test;
		}

		public void setTest(String test) {
			this.test = test;
		}
	}

	@Test
	public void test() {
		InsSearchForm form = new InsSearchForm();
		form.setTest("test");
		WhereBuilder builder = WhereBuilder.create(form);

		System.out.println(builder.toString());
	}

}
