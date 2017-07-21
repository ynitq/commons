package com.cfido.commons.spring.utils;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * <pre>
 * 强制将所有页面请求返回的编码都修改为utf8
 * 
 * </pre>
 * 
 * 
 * @author 梁韦江
 */
@Configuration
public class ChangeAllConverterToUtf8 extends WebMvcConfigurerAdapter {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChangeAllConverterToUtf8.class);

	public ChangeAllConverterToUtf8() {
		log.info("强制将所有页面请求返回的编码都修改为utf8");
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

		Charset charset = Charset.forName("UTF-8");

		for (HttpMessageConverter<?> c : converters) {
			if (c instanceof AbstractHttpMessageConverter) {
				((AbstractHttpMessageConverter<?>) c).setDefaultCharset(charset);
			}
		}
	}
}
