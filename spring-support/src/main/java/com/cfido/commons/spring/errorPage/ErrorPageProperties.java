package com.cfido.commons.spring.errorPage;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.cfido.commons.utils.utils.StringUtils;

/**
 * <pre>
 * 错误页面处理器的 配置参数
 * 
 * 参数在 application.properties 中配置
 * 
 * 
 * 
 * # 配置模板文件的的前缀
 * errorPage.prefix=templates/error_page/
 * # 配置模板文件的后缀
 * errorPage.suffix=.ftl
 * # 默认的模板 
 * errorPage.defaultPage=common
 *  
 * 
 * #可单独配置某种错误类型的页面
 * errorPage.pages.400=error400
 * errorPage.pages.401=....
 * errorPage.pages.402=
 * 
 * </pre>
 * 
 * @author 梁韦江 2016年9月04日
 */
@ConfigurationProperties(prefix = "errorPage")
public class ErrorPageProperties {

	/** 默认的模板 */
	private String defaultPage = "default";

	/** 每个错误类型独立的模板 */
	private final Map<Integer, String> pages = new HashMap<>();

	/** 模板名前缀 */
	private String prefix = "templates/error_page/";

	/** 模板名后缀 */
	private String suffix = ".ftl";

	/**
	 * 根据错误代码获得模板
	 * 
	 * @param status
	 * @return 模板
	 */
	public String getTemplatePath(int status) {
		// 先在map中找
		String page = this.pages.get(status);

		if (StringUtils.isEmpty(page)) {
			// 如果找不到就用默认的
			page = this.defaultPage;
		}

		return this.prefix + page + this.suffix;
	}

	public void setDefaultPage(String defaultPage) {
		this.defaultPage = defaultPage;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}
