package com.cfido.commons.beans.apiServer;

/**
 * <pre>
 * 和客户端交互时，通用的返回代码
 * </pre>
 * 
 * @author 梁韦江
 *  2016年6月24日
 */
public interface ApiCommonCode {

	/**
	 * 返回结果正常 :{@value}
	 * 
	 * TODO 成功的返回码不应该和http的状态码混为一谈，为兼容现有代码，临时用一下
	 * 
	 */
	public static final String RESPONSE_OK = "200";

	// ----------------------- 系统用错误代码 1000开始---------------------------
}
