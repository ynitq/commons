package com.cfido.commons.apiServer.utils;

import com.cfido.commons.annotation.api.AMock;

/**
 * <pre>
 * 用于测试的bean，只有getter
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月25日
 */
public class MyTestBean {

	@AMock(id = true)
	public int getId() {
		return 0;
	}

	public String getMyString() {
		return null;
	}


}
