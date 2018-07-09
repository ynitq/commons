package com.cfido.commons.spring.imageUpload;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 图片上传服务的相关配置
 * 
 * @author 梁韦江 2017年2月23日
 *
 */
@Configurable
@EnableConfigurationProperties(ImageUploadProperties.class)
@ComponentScan(basePackageClasses = ImageUploadAutoConfig.class)
public class ImageUploadAutoConfig extends WebMvcConfigurerAdapter {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ImageUploadAutoConfig.class);

	public ImageUploadAutoConfig() {
		log.info("自动配置 图片上传服务");
	}

	@Override
	/** 添加url映射目录，方便在没有nginx做前置时，也可以访问到上传的文件 */
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		// 要映射的url, 例如 /upload/xxx.jpg
		String attachmentPathPatterns = String.format("/%s/**", ImageUploadProperties.UPLOAD_DIR_PREFIX);

		// 映射的文件目录
		String attachmentResourceLocations = String.format("file:%s%s/", ImageUploadProperties.WORK_DIR,
				ImageUploadProperties.UPLOAD_DIR_PREFIX);

		// 映射url: /upload/** 到 当前目录的 ./upload目录下
		registry.addResourceHandler(attachmentPathPatterns).addResourceLocations(attachmentResourceLocations);

		log.info("映射url: {}, 到目录: {}", attachmentPathPatterns, attachmentResourceLocations);

		super.addResourceHandlers(registry);
	}

}
