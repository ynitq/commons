package com.cfido.commons.spring.imageUpload;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * 图片上传服务的相关配置
 * 
 * @author 梁韦江 2017年2月23日
 *
 */
@Configurable
@EnableConfigurationProperties(ImageUploadProperties.class)
@ComponentScan(basePackageClasses = ImageUploadAutoConfig.class)
public class ImageUploadAutoConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ImageUploadAutoConfig.class);

	public ImageUploadAutoConfig() {
		log.info("自动配置 图片上传服务");
	}

}
