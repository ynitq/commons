package com.cfido.commons.codeGen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * <pre>
 * 代码生成工具的的相关配置
 * 
 * 
 * 以下是配置的默认参数
 * 
 * # 基础包名，所有的java文件的基础包名，每个模块自行添加后缀
 * codeGen.basePackage = com.linzi.xxx
 * 
 * # 源码的路径，生成时如果发现文件已经存在，不会覆盖原来的文件，但entity文件除外，一定被覆盖
 * codeGen.output.java = src/main/java
 * codeGen.output.other = src/main/webapp
 * codeGen.output.save = true
 * codeGeb.output.override = false
 * 
 * # 生成出来的代码可作为样板文件，自己手动拷贝，下面配置是样板文件存放路径默认值
 * codeGen.sample.java = codeGen/java
 * codeGen.sample.other = codeGen/others
 * codeGen.sample.save = true
 * codeGeb.sample.override = true
 * 
 * # 启动时，自动运行 {@link CodeGenProperties#autorun} 
 * codeGen.autorun.entity = false
 * codeGen.autorun.fileset1 = false
 * codeGen.autorun.templateDir = false
 * 
 * # 生成entity文件时的配置 {@link Entity}
 * codeGen.entity.entityPackage = entity
 * codeGen.entity.template = template/codeGen/entity.ftl
 *  
 * # 指定entity对象的接口。 非必填
 * codeGen.entity.implement[0]=com.linzi.app.appserver.entity.server.IBasePo
 * 
 * # 数据库表名和java类名的对照表， 非必填
 * codeGen.entity.tableNameMap.newses = News
 * 
 * # 可自定义模板的目录，如果文件名带有“__javaClassName__”或者 "__otherFileName__"字样的，
 * 将按数据库的表，循环生成所有的文件。
 * codeGen.templateDir[0]=
 * 
 * </pre>
 * 
 * @author 梁韦江 2016年9月12日
 */
@Component
@PropertySource("classpath:codeGen.properties") // 自定义配置文件时，必须有@Component，否则无法读取文件
@ConfigurationProperties(prefix = "codeGen")
public class CodeGenProperties {

	/** 文件名中有这个字样，或者有.java字样的模板，渲染的结果放到java文件的目录中， 该字样会被替换为TableBean中的对应属性 */
	public final static String PROP_IN_FILE_NAME_FOR_JAVA = "__javaClassName__";

	/** 文件名中有这个字样时，渲染的结果放到其他文件的目录中 */
	public final static String PROP_IN_FILE_NAME_FOR_OTHER = "__otherFileName__";

	/** 文件名包含该字样时，表示一定会覆盖原文件 */
	public final static String _OVERRIDE_SURFFIX = "_CodeGen.java";

	/**
	 * /** 描述文件的类型：可以是输入文件，也可以是模板文件
	 */
	public static class FileSaveOption {
		private String java;

		private String other;

		/** 是否保存到文件 */
		private boolean save;

		/** 保存文件时，如果原来的文件已经存在，是否覆盖原文件 */
		private boolean override;

		public FileSaveOption() {
		}

		public FileSaveOption(String java, String other) {
			super();
			this.java = java;
			this.other = other;
		}

		public String getJava() {
			return java;
		}

		public void setJava(String java) {
			this.java = java;
		}

		public String getOther() {
			return other;
		}

		public void setOther(String other) {
			this.other = other;
		}

		public boolean isSave() {
			return save;
		}

		public void setSave(boolean save) {
			this.save = save;
		}

		public boolean isOverride() {
			return override;
		}

		public void setOverride(boolean override) {
			this.override = override;
		}

	}

	/**
	 * <pre>
	 * 根据数据库table生成 entity代码的配置
	 * </pre>
	 * 
	 * @author 梁韦江 2016年9月13日
	 */
	public static class Entity {

		/** entity的包名 */
		private String entityPackage = "entity";

		/** entity模板路径 */
		private String template = "template/codeGen/entity/entity.ftl";

		/** 复合id的模板，虽然我们目前没写完 */
		private String idTemplate = "template/codeGen/entity/entityId.ftl";

		/** 表名 -- 类名 的转换规则 */
		private final Map<String, String> tableNameMap = new HashMap<>();

		/** entity 的接口 */
		private final List<String> implement = new LinkedList<>();

		/**
		 * 根据数据库的表名，生成表对应的java 类名
		 * 
		 * @param tableName
		 * @return
		 */
		public String buildTableJavaName(String tableName) {
			String res = this.tableNameMap.get(tableName);
			if (res != null) {
				return StringUtils.capitalize(res);
			} else {
				String name = com.cfido.commons.utils.utils.StringUtilsEx.toUpperCamelCase(tableName, true);
				// if (name.endsWith("s")) {
				// name = name.substring(0, name.length() - 1);
				// }

				return name;
			}
		}

		public String getIdTemplate() {
			return idTemplate;
		}

		public List<String> getImplement() {
			return implement;
		}

		public Map<String, String> getTableNameMap() {
			return tableNameMap;
		}

		public String getTemplate() {
			return template;
		}

		public void setEntityPackage(String entityPackage) {
			this.entityPackage = entityPackage;
		}

		public void setIdTemplate(String idTemplate) {
			this.idTemplate = idTemplate;
		}

		public void setTemplate(String template) {
			this.template = template;
		}

		public String getEntityPackage() {
			return entityPackage;
		}

	}

	/**
	 * 存放正式文件的路径，如果文件已经存在，是不会覆盖的
	 */
	private final FileSaveOption output = new FileSaveOption("src/main/java", "src/main/webapp");

	/**
	 * 存放生成出来的样板文件的路径，每次生成时都覆盖
	 */
	private final FileSaveOption sample = new FileSaveOption("codeGen/java", "codeGen/others");

	private final List<String> templateDir = new LinkedList<>();

	private Entity entity = new Entity();

	/** 自动运行的内容，entity , fileset1, template */
	private final Map<String, String> autorun = new HashMap<>();

	private String basePackage;

	private String entityPackage;

	public String getBasePackage() {
		return basePackage;
	}

	public Entity getEntity() {
		return entity;
	}

	/**
	 * 获得entity的包名
	 * 
	 * @return
	 */
	public String getEntityPackage() {
		if (StringUtils.isEmpty(this.entityPackage)) {
			this.entityPackage = this.basePackage + "." + this.entity.entityPackage;
		}
		return this.entityPackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public FileSaveOption getOutput() {
		return output;
	}

	public FileSaveOption getSample() {
		return sample;
	}

	public List<String> getTemplateDir() {
		return templateDir;
	}

	public CodeGenProperties() {
		// 源码目录的默认配置是：生成文件，但不覆盖原文件
		this.output.save = true;
		this.output.override = false;

		// 样例目录的默认配置是：生成文件，并覆盖原文件
		this.sample.save = true;
		this.sample.override = true;
	}

	public Map<String, String> getAutorun() {
		return autorun;
	}

	/**
	 * 是否自动生成entity文件
	 */
	public boolean isAutoRunEntity() {
		return this.isAutoRun("entity");
	}

	/**
	 * 是否自动生成 套装1 的文件
	 */
	public boolean isAutoRunFileSet1() {
		return this.isAutoRun("fileset1");
	}

	/**
	 * 是否自动生成 自定义模板 的文件
	 */
	public boolean isAutoRunTemplate() {
		return this.isAutoRun("templateDir");
	}

	private boolean isAutoRun(String key) {
		String val = this.autorun.get(key);
		return val != null && val.equalsIgnoreCase("true");
	}

	public void setEntityPackage(String entityPackage) {
		this.entityPackage = entityPackage;
	}

}
