package com.cfido.commons.codeGen.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cfido.commons.codeGen.core.CodeGenContext;
import com.cfido.commons.codeGen.core.CodeGenMainService;
import com.cfido.commons.codeGen.core.CodeGenTemplateService;
import com.cfido.commons.codeGen.core.HelpMBean;
import com.cfido.commons.codeGen.core.MetadataReader;

/**
 * <pre>
 * 全局的配置
 * </pre>
 * 
 * @author 梁韦江 2016年9月11日
 */
@Configuration
@EnableConfigurationProperties(CodeGenProperties.class)
public class CodeGenAutoConfig {

	@Bean()
	public CodeGenContext codeGenContext() {
		return new CodeGenContext();
	}

	@Bean()
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean()
	public MetadataReader codeGenMetadataReader() {
		return new MetadataReader();
	}

	@Bean
	public CodeGenMainService codeGenMain() {
		return new CodeGenMainService();
	}

	@Bean
	public JdbcTemplate JdbcTemplate() {
		return new JdbcTemplate(this.dataSource());
	}

	@Bean()
	public CodeGenTemplateService codeGenTemplateService() {
		return new CodeGenTemplateService();
	}

	@Bean()
	public HelpMBean helpMBean() {
		return new HelpMBean();
	}

}
