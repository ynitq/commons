package com.cfido.commons.codeGen.beans;

import org.junit.Assert;
import org.junit.Test;

import com.cfido.commons.codeGen.beans.FormPropBean;


/**
 * <pre>
 * 对FormPropBean的测试
 * </pre>
 * 
 * @author 梁韦江 2016年10月12日
 */
public class FormPropBeanTest {

	@Test
	public void testParser() {
		String str = "LF|EF|标题|";

		FormPropBean bean = new FormPropBean();
		String comment = bean.parserComment(str);

		Assert.assertEquals("解析出来的备注应该是‘标题’", "标题", comment);
	}

}
