package com.cfido.commons.utils.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.http.ParseException;
import org.apache.http.StatusLine;

/**
 * <pre>
 * http util 发出的错误
 * </pre>
 * 
 * @author 梁韦江
 *  2016年7月21日
 */
public class HttpUtilException extends IOException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8340460717191021685L;

	private final String message;

	public HttpUtilException(ParseException ex) {
		super(ex);
		this.message = "http返回信息解析错误";
	}

	public HttpUtilException(UnsupportedEncodingException e) {
		super(e);
		this.message = "编码错误";
	}

	public HttpUtilException(URISyntaxException e) {
		super(e);
		this.message = "url格式错误";
	}

	public HttpUtilException(StatusLine statusLine, String content) {
		super();
		this.message = String.format("返回码:%d  原因:%s 内容:%s",
				statusLine.getStatusCode(),
				statusLine.getReasonPhrase(),
				content);
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
