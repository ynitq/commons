package com.cfido.commons.spring.imageUpload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.beans.apiExceptions.InvalidImageFormatException;
import com.cfido.commons.spring.dict.DictAutoConfig;
import com.cfido.commons.utils.utils.FileUtil;
import com.cfido.commons.utils.utils.ImageEX;

/**
 * <pre>
 * 处理图片上传的服务
 * </pre>
 * 
 * @author 梁韦江 2017年2月23日
 */
@Service
public class ImageUploadService {

	@Autowired
	private ImageUploadProperties prop;

	private final List<String> imageFormats;

	public ImageUploadService() {
		// 可支持的图片格式
		this.imageFormats = Arrays.asList(new String[] {
				"bmp", "gif", "png", "jpg"
		});
	}

	public class ImageProp {
		@AComment("缩略图全路径")
		private String thumbFullPath;
		@AComment("图片宽")
		private int imageWidth;
		@AComment("图片高")
		private int imageHeight;
		@AComment("缩略图宽")
		private int thumbWidth;
		@AComment("缩略图高")
		private int thumbHeight;

		public String getThumbFullPath() {
			return thumbFullPath;
		}

		public int getImageWidth() {
			return imageWidth;
		}

		public int getImageHeight() {
			return imageHeight;
		}

		public int getThumbWidth() {
			return thumbWidth;
		}

		public int getThumbHeight() {
			return thumbHeight;
		}

	}

	/** 描述图片保存的结果 */
	public class SaveResult {
		@AComment("文件扩展名")
		private String extName = "";// 文件扩展名
		@AComment("文件名")
		private String name;
		@AComment("全路径")
		private String fullPath;
		@AComment("文件大小")
		private long fileSize;

		@AComment("上传的是否是图片")
		private boolean image; // 是否是图片
		@AComment("图片的属性")
		private ImageProp imageProp; // 如果是图片，才会有的属性

		public ImageProp getImageProp() {
			return imageProp;
		}

		public String getExtName() {
			return extName;
		}

		public String getName() {
			return name;
		}

		public String getFullPath() {
			return fullPath;
		}

		public boolean isImage() {
			return image;
		}

		public long getFileSize() {
			return fileSize;
		}

		private void updateOriginalFilename(String originalFilename) {
			int index = originalFilename.lastIndexOf(".");
			if (index > 0) {
				// 如果能找到“.” 就用.后面的作为文件扩展名
				this.extName = originalFilename.substring(index + 1).toLowerCase();
				image = ImageUploadService.this.imageFormats.contains(this.extName);
			}

		}

		private void updatePath(String path) {
			String extStr = "";
			if (StringUtils.hasText(extName)) {
				extStr = "." + extName;
			}

			String prefix = path;
			if (!prefix.endsWith("/")) {
				prefix = path + "/";
			}

			this.fullPath = prefix + name + extStr;
			if (image) {
				this.imageProp = new ImageProp();
				// 如果是图片，需要有缩略图
				this.imageProp.thumbFullPath = prefix + name + ImageUploadService.this.prop.getThumb().getPostfix() + extStr;
			}
		}

	}

	public String getThumbPostfix() {
		return this.prop.getThumb().getPostfix();
	}

	/** 根据名字删除旧的附件 */
	public void deleteOldFile(String name, String oldExtName) throws IOException {
		String extStr = "";
		if (StringUtils.hasText(oldExtName)) {
			extStr = "." + oldExtName;
		}

		String prefix = DictAutoConfig.ATTACHMENT_PATH + "/";

		String fullPath = prefix + name + extStr;
		String thumbFullPath = prefix + name + ImageUploadService.this.prop.getThumb().getPostfix() + extStr;

		Files.deleteIfExists(FileSystems.getDefault().getPath(fullPath));
		Files.deleteIfExists(FileSystems.getDefault().getPath(thumbFullPath));
	}

	/**
	 * 保存上传的文件
	 * 
	 * @param multipartFile
	 *            上传的数据
	 * @param path
	 *            图片存放路径
	 * @param name
	 *            文件名（无扩张名）
	 * @return 保存的结果
	 * @throws IOException
	 */
	public SaveResult save(MultipartFile multipartFile, String path, String name)
			throws FileNotFoundException, InvalidImageFormatException, IOException {
		// 检查是否有上传文件
		if (multipartFile == null || multipartFile.isEmpty()) {
			throw new FileNotFoundException("找不到上传的文件");
		}

		SaveResult res = new SaveResult();

		res.name = name;
		// 根据原始文件名分析
		res.updateOriginalFilename(multipartFile.getOriginalFilename());
		// 更新文件全路径
		res.updatePath(path);

		// 分析是否图片前，先将图片保存下来
		Path filePath = FileUtil.save(multipartFile, res.fullPath);
		res.fileSize = filePath.toFile().length();
		if (res.image) {
			try {
				ImageEX old = new ImageEX(filePath.toFile());
				res.imageProp.imageWidth = old.getWidth();
				res.imageProp.imageHeight = old.getHeight();

				// 生成缩略图
				ImageEX thumb = old.chageImageSizeKeepScaled(this.prop.getThumb().getWidth(), this.prop.getThumb().getHeight());
				thumb.outPutImage(res.extName, new File(res.imageProp.thumbFullPath));
				res.imageProp.thumbWidth = thumb.getWidth();
				res.imageProp.thumbHeight = thumb.getHeight();

			} catch (InvalidImageFormatException e) {
				// 如果是格式错误，就删除这个文件
				filePath.toFile().delete();
				// 然后继续抛错
				throw e;
			}
		}

		return res;
	}

}
