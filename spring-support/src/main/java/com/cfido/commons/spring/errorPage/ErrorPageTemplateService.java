package com.cfido.commons.spring.errorPage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.cfido.commons.utils.utils.LogUtil;
import com.cfido.commons.utils.utils.StringUtils;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * <pre>
 * FreeMarker模板处理，用于根据Model和View模板合成页面
 * </pre>
 * 
 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a> 2015年9月13日
 */
public class ErrorPageTemplateService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ErrorPageTemplateService.class);

	/**
	 * <pre>
	 * 自己实现的从resource中读取模板的TemplateLoader
	 * </pre>
	 * 
	 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a> 2015年9月13日
	 */
	private class ResourceTemplateLoader implements TemplateLoader {

		private final Map<String, TemplateSource> sourceMap = new HashMap<String, TemplateSource>();

		@Override
		public void closeTemplateSource(Object templateSource) throws IOException {
		}

		@Override
		public Object findTemplateSource(String fileName) throws IOException {
			if (fileName != null) {
				TemplateSource source = this.sourceMap.get(fileName);
				if (source == null) {
					byte[] bytes = StringUtils.loadFileFromClassPath(fileName);
					if (bytes != null) {
						source = new TemplateSource(fileName, bytes);
						// TODO 暂时关闭模板缓存
						// this.sourceMap.put(fileName, source);
						if (log.isDebugEnabled()) {
							log.debug(LogUtil.format("Read template from %s", fileName));
						}
					}
				}
				return source;
			}
			return null;
		}

		@Override
		public long getLastModified(Object templateSource) {
			return ((TemplateSource) templateSource).lastModified;
		}

		@Override
		public Reader getReader(Object templateSource, String encoding) throws IOException {
			return new StringReader(((TemplateSource) templateSource).source);
		}

	}

	/**
	 * <pre>
	 * 用于封装ResourceTemplateLoader的处理对象
	 * </pre>
	 * 
	 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a> 2015年10月15日
	 */
	private class TemplateSource {
		private final String name;
		private final String source;
		private final long lastModified;

		TemplateSource(String name, byte[] bytes) throws UnsupportedEncodingException {
			if (name == null) {
				throw new IllegalArgumentException("name == null");
			}
			if (bytes == null) {
				throw new IllegalArgumentException("bytes == null");
			}
			this.name = name;
			this.source = new String(bytes, "UTF-8");
			this.lastModified = System.currentTimeMillis();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TemplateSource) {
				return this.name.equals(((TemplateSource) obj).name);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.name.hashCode();
		}
	}

	private final Configuration freemarkerCfg;

	protected ErrorPageTemplateService() {
		freemarkerCfg = new Configuration(Configuration.getVersion());
		// 防止freemarker渲染时对value=null的key 解析出错
		freemarkerCfg.setClassicCompatible(true);
		freemarkerCfg.setTemplateLoader(new ResourceTemplateLoader());
	}

	/**
	 * 根据模板和数据生成页面
	 * 
	 * @param templateName
	 *            模板文件名
	 * @param dataModel
	 *            数据
	 * @return
	 */
	public String process(String templateName, Object dataModel) {

		try {
			Template template = this.freemarkerCfg.getTemplate(templateName);
			StringWriter w = new StringWriter();
			PrintWriter out = new PrintWriter(w);
			template.process(dataModel, out);
			return w.toString();
		} catch (Exception ex) {
			return LogUtil.getTraceString(
					String.format("渲染文件 %s 时发生了错误", templateName),
					ex);
		}
	}
}
