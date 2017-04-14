package com.cfido.commons.spring.imageUpload;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <pre>
 * 图片上传服务的配置
 * </pre>
 * 
 * @author 梁韦江 2017年2月23日
 */
@ConfigurationProperties(prefix = "imageUpload")
public class ImageUploadProperties {

	/** 缩略图的参数 */
	public static class Thumb {
		/** 图片最大宽度 */
		private int width = 150;
		/** 图片最大高度 */
		private int height = 150;

		/** 附加在原文件名后的字符 ，例如 me.png 的缩略图就是 me_thumb.png */
		private String postfix = "_thumb";

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public String getPostfix() {
			return postfix;
		}

		public void setPostfix(String postfix) {
			this.postfix = postfix;
		}

	}

	/** 存储缩略图的信息 */
	private Thumb thumb = new Thumb();

	public Thumb getThumb() {
		return thumb;
	}

	public void setThumb(Thumb thumb) {
		this.thumb = thumb;
	}

}
