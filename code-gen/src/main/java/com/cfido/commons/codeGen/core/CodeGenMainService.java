package com.cfido.commons.codeGen.core;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.cfido.commons.codeGen.beans.TableBean;
import com.cfido.commons.codeGen.config.CodeGenProperties;
import com.cfido.commons.utils.utils.DateUtil;
import com.cfido.commons.utils.utils.ResourceScaner;

/**
 * <pre>
 * 主启动程序
 * </pre>
 * 
 * @author 梁韦江 2016年9月14日
 */
@ManagedResource(description = "CodeGen代码生成器", objectName = "CodeGen代码生成器: name=CodeGenMainService")
public class CodeGenMainService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CodeGenMainService.class);

	@Autowired
	private DataSourceProperties dataSourceProperties;

	@Autowired
	private MetadataReader metadataReader;

	@Autowired
	private CodeGenProperties codeGenProperties;

	@Autowired
	private CodeGenTemplateService codeGenTemplateService;

	@Autowired
	private CodeGenContext context;

	@ManagedAttribute(description = "数据库")
	public String getCatalog() {
		return this.metadataReader.getCatalog();
	}

	@ManagedAttribute(description = "数据库连接url")
	public String getUrl() throws SQLException {
		return this.dataSourceProperties.getUrl();
	}

	@ManagedAttribute(description = "基础包名")
	public String getBasePackage() {
		return this.codeGenProperties.getBasePackage();
	}

	@ManagedAttribute(description = "Entity的包")
	public String getEntityPackage() {
		return this.codeGenProperties.getEntityPackage();
	}

	@ManagedAttribute(description = "JAVA输出路径")
	public String getOutputPathJava() {
		return this.codeGenProperties.getOutput().getJava();
	}

	@ManagedAttribute(description = "JAVA样本文件保存路径")
	public String getSamplePath() {
		return this.codeGenProperties.getSample().getJava();
	}

	@ManagedAttribute(description = "已经读取的表的数量")
	public int getTableCount() {
		return this.context.getTables().size();
	}

	@PostConstruct
	protected void init() throws SQLException, IOException {
		this.checkProp();// 检查一下配置是否合理

		this.metadataReader.init();// 从数据库读取信息
		log.info("从数据库读取信息完成，共发现 {} 个表", this.context.getTables().size());

		if (this.codeGenProperties.isAutoRunEntity()) {
			log.debug("自动生成Entity文件");
			this.createEntityFiles();
		}

		if (this.codeGenProperties.isAutoRunFileSet1()) {
			log.debug("自动生成 套装1 文件");
			this.createFilesSet1();
		}

		if (this.codeGenProperties.isAutoRunTemplate()) {
			log.debug("自动生成 templateDir定义的模板的 文件");
			this.createByTemplateDir();
		}
	}

	private void checkProp() {
		if (StringUtils.isEmpty(this.codeGenProperties.getBasePackage())) {
			throw new RuntimeException("请配置 codeGen.basePackage");
		}

		if (StringUtils.isEmpty(this.codeGenProperties.getOutput().getJava())) {
			throw new RuntimeException("请配置 codeGen.output.java");
		}
		if (StringUtils.isEmpty(this.codeGenProperties.getSample().getJava())) {
			throw new RuntimeException("请配置 codeGen.sample.java");
		}
	}

	@ManagedOperation(description = "重新从数据库中读取信息")
	public void reReadMetaData() throws SQLException {
		this.metadataReader.init();
	}

	@ManagedOperation(description = "生成 Entity文件")
	public void createEntityFiles() throws IOException {

		if (StringUtils.isEmpty(this.codeGenProperties.getEntity().getEntityPackage())) {
			throw new RuntimeException("请配置 codeGen.entity.entityPackage");
		}

		for (TableBean table : this.context.getTables()) {
			Map<String, Object> map = this.buildModel();

			map.put("table", table);
			this.codeGenTemplateService.saveJavaFile(
					this.codeGenProperties.getEntityPackage(), // 包名
					table.getJavaClassName() + ".java", // 文件名
					this.codeGenProperties.getEntity().getTemplate(), // 模板名
					map, true);
		}
	}

	/**
	 * 构造传给freemarker的model
	 */
	private Map<String, Object> buildModel() {
		Map<String, Object> map = new HashMap<>();
		map.put("context", context);// 将context传给模板
		map.put("prop", this.codeGenProperties);// 将配置文件的内容传给模板
		map.put("date", DateUtil.dateFormat(new Date()));
		return map;
	}

	@ManagedOperation(description = "生成 套装1 的文件，包含了domain、jsp等等")
	public void createFilesSet1() throws IOException {
		this.doCreateFileByTemplate("template/codeGen/fileset1/java");
		this.doCreateFileByTemplate("template/codeGen/fileset1/other");
	}

	@ManagedOperation(description = "生成 templateDir属性定义的模板 的文件")
	public void createByTemplateDir() throws IOException {
		List<String> templateDirList = this.codeGenProperties.getTemplateDir();
		if (!templateDirList.isEmpty()) {
			for (String templateDir : templateDirList) {
				if (StringUtils.hasText(templateDir)) {
					this.doCreateFileByTemplate(templateDir);
				}
			}
		}
	}

	/**
	 * 扫描一个模板目录，并生成文件
	 * 
	 * @param templateDir
	 * @throws IOException
	 */
	private void doCreateFileByTemplate(String templateDir) throws IOException {
		Assert.hasText(templateDir);

		// 扫描，并获得所有的模板
		List<String> templateList = ResourceScaner.scan(templateDir);

		// 遍历所有模板
		for (String templateName : templateList) {
			ParserResult res = this.parser(templateDir, templateName);
			log.debug("发现模板: {}", res.toString());

			if (res.forEachTable) {
				// 如果 对于每个表都需要生成一个文件
				for (TableBean table : this.context.getTables()) {
					Map<String, Object> map = this.buildModel();
					map.put("table", table);

					if (res.java) {
						String fileName = res.fileName.replace(
								CodeGenProperties.PROP_IN_FILE_NAME_FOR_JAVA,
								table.getJavaClassName());

						this.codeGenTemplateService.saveJavaFile(
								res.packageName, // 包名
								fileName, // 文件名
								templateName, // 模板名
								map,false);
					} else {
						String fileName = res.fileName.replace(
								CodeGenProperties.PROP_IN_FILE_NAME_FOR_OTHER,
								table.getOtherFileName());

						this.codeGenTemplateService.saveOtherFile(
								String.format("%s/%s", res.path, fileName), // 文件路径
								templateName, // 模板
								map);

					}
				}

			} else {
				Map<String, Object> map = this.buildModel();
				if (res.java) {
					this.codeGenTemplateService.saveJavaFile(
							res.packageName, // 包名
							res.fileName, // 文件名
							templateName, // 模板名
							map, false);
				} else {
					this.codeGenTemplateService.saveOtherFile(
							String.format("%s/%s", res.path, res.fileName), // 文件路径
							templateName, // 模板
							map);
				}

			}

		}
	}

	/**
	 * <pre>
	 * 模板目录中，每一个模板的解析结果
	 * </pre>
	 * 
	 * @author 梁韦江 2016年9月28日
	 */
	private class ParserResult {
		String fileName;
		String path;
		boolean java;
		String packageName;
		boolean forEachTable;// 是否是每个表都需要生成一个

		@Override
		public String toString() {
			if (this.java) {
				return String.format("Java文件 %s -> %s", this.packageName, this.fileName);
			} else {
				return String.format("其他文件 %s/%s", this.path, this.fileName);
			}
		}
	}

	/**
	 * 解析模板名
	 * 
	 * @param templateDir
	 * @param templateName
	 * @return
	 */
	private ParserResult parser(String templateDir, String templateName) {
		ParserResult res = new ParserResult();

		/**
		 * 获取 相对文件名，
		 * 
		 * <pre>
		 * /template/codeGen/fileset1/domains/__javaClassName__Domain.java.ftl 变成
		 * domains/__javaClassName__Domain.java.ftl
		 * </pre>
		 */
		String nameWithPackage = templateName.substring(templateDir.length() + 1);

		int lastIndex = nameWithPackage.lastIndexOf("/");
		res.fileName = nameWithPackage.substring(lastIndex + 1);
		if (res.fileName.endsWith(".ftl")) {
			res.fileName = res.fileName.substring(0, res.fileName.length() - 4);
		}

		res.path = nameWithPackage.substring(0, lastIndex);
		res.java = res.fileName.endsWith(".java");

		if (res.java) {
			res.packageName = String.format("%s.%s",
					this.codeGenProperties.getBasePackage(),
					res.path.replace("/", "."));
			res.forEachTable = res.fileName.contains(CodeGenProperties.PROP_IN_FILE_NAME_FOR_JAVA);
		} else {
			res.forEachTable = res.fileName.contains(CodeGenProperties.PROP_IN_FILE_NAME_FOR_OTHER);
		}

		return res;
	}
}
