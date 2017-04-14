package com.cfido.commons.apiServer.utils;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.cfido.commons.apiServer.utils.MockDataCreater;
import com.cfido.commons.beans.apiServer.impl.CommonSuccessResponse;

/**
 * <pre>
 * MockDataCreater的测试，测试生成模拟数据
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月25日
 */
public class MockDataCreaterTest {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MockDataCreaterTest.class);

	@Test
	public void test() {

	

	}

	public void test2() {
		CommonSuccessResponse res = MockDataCreater.newInstance(CommonSuccessResponse.class);
		log.debug("JSON:\n{}", JSON.toJSONString(res, true));
	}

}
