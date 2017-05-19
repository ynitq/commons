package com.cfido.commons.codeGen.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cfido.commons.codeGen.CodeGenProperties;
import com.cfido.commons.codeGen.CodeGenProperties.FileSaveOption;
import com.cfido.commons.utils.utils.FileUtil;
import com.cfido.commons.utils.utils.LogUtil;

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
@Service
public class CodeGenTemplateService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CodeGenTemplateService.class);

	@Autowired
	private CodeGenProperties codeGenProperties;

	/**
	 * <pre>
	 * 自己实现的从resource中读取模板的TemplateLoader
	 * </pre>
	 * 
	 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a> 2015年9月13日
	 */
	private class ResourceTemplateLoader implements TemplateLoader {

		@Override
		public void closeTemplateSource(Object templateSource) throws IOException {
		}

		@Override
		public Object findTemplateSource(String fileName) throws IOException {
			if (fileName != null) {
				byte[] bytes = FileUtil.loadFileFromClassPath(fileName);
				if (bytes != null) {
					if (log.isDebugEnabled()) {
						log.debug(LogUtil.format("Read template from %s", fileName));
					}

					TemplateSource source = new TemplateSource(fileName, bytes);
					return source;
				}
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

		TemplateSource(String name, byte[] bytes) {
			if (name == null) {
				throw new IllegalArgumentException("name == null");
			}
			if (bytes == null) {
				throw new IllegalArgumentException("bytes == null");
			}
			this.name = name;
			this.source = new String(bytes);
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

	public CodeGenTemplateService() {
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

	/**
	 * 写文件
	 * 
	 * @param packageName
	 *            包名
	 * @param fileName
	 *            java文件名 = 类名 + .java
	 * @param templateName
	 *            模板名字
	 * @param dataModel
	 *            模板数据
	 * @param forceOverride
	 *            是否强制覆盖
	 * @throws IOException
	 */
	public void saveJavaFile(String packageName, String fileName,
			String templateName, Map<String, Object> dataModel,
			boolean forceOverride)
			throws IOException {

		// 需要在这个设置包名
		dataModel.put("package", packageName);

		// 先渲染模板
		String content = this.process(templateName, dataModel);

		if (StringUtils.hasText(content)) {
			// 如果有内容，就保存文件
			String filePath = String.format("%s/%s",
					packageName.replace('.', '/'),
					fileName);

			this.writeFile(this.codeGenProperties.getOutput(), filePath, content, true, forceOverride);
			this.writeFile(this.codeGenProperties.getSample(), filePath, content, true, forceOverride);

			log.debug("生成 Java文件: {}", filePath);
		}
	}

	/**
	 * 写文件
	 * 
	 * @param filePath
	 *            文件相对路径
	 * @param templateName
	 *            模板名字
	 * @param dataModel
	 *            模板数据
	 * @throws IOException
	 */
	public void saveOtherFile(String filePath,
			String templateName, Map<String, Object> dataModel)
			throws IOException {

		// 先渲染模板
		String content = this.process(templateName, dataModel);

		if (StringUtils.hasText(content)) {
			// 如果有内容，就保存文件
			this.writeFile(this.codeGenProperties.getOutput(), filePath, content, false, false);
			this.writeFile(this.codeGenProperties.getSample(), filePath, content, false, false);

			log.info("处理文件: {}", filePath);
		}
	}

	private void writeFile(FileSaveOption option, String fileName, String content,
			boolean isJava, boolean forceOverride) throws IOException {
		String fullPath = String.format("%s/%s",
				isJava ? option.getJava() : option.getOther(),
				fileName);

		boolean save = false;
		boolean override = false;
		
		if (isJava && fileName.endsWith(CodeGenProperties._OVERRIDE_SURFFIX)) {
			// Xxxx_CodeGen.java结尾的文件名，强制覆盖
			save = true;
			override = true;
		}

		save = save || option.isSave();
		override = override || option.isOverride();

		if (save) {
			FileUtil.write(fullPath, content, override);
		}

	}
}
