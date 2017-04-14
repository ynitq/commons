package com.cfido.commons.utils.utils;

import java.io.IOException;
import java.util.Map;

/**
 * <pre>
 * 获取短信网关的接口
 * </pre>
 * 
 * @author 梁韦江
 *  2016年7月21日
 */
public interface IHttpUtil {

	/**
	 * 就是http client
	 * 
	 * @param url
	 *            url
	 * @param paramMap
	 *            参数
	 * @param postMethod
	 *            是否是post方式
	 * @param header
	 *            额外的 http header
	 * @return
	 * @throws IOException
	 * @throws HttpUtilException
	 */
	public String doExecute(String url, Map<String, Object> paramMap, boolean postMethod,
			Map<String, String> header) throws HttpUtilException, IOException;
}
