package com.cfido.commons.spring.imageUpload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.beans.apiExceptions.InvalidImageFormatException;
import com.cfido.commons.spring.utils.UniqueIdCreater;
import com.cfido.commons.utils.utils.DateUtil;
import com.cfido.commons.utils.utils.FileUtil;
import com.cfido.commons.utils.utils.ImageEX;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

/**
 * <pre>
 * 处理图片上传的服务
 * </pre>
 * 
 * @author 梁韦江 2017年2月23日
 */
@Service
public class ImageUploadService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ImageUploadService.class);

	@Autowired
	private ImageUploadProperties prop;

	private final List<String> imageFormats;

	public ImageUploadService() {
		// 可支持的图片格式
		this.imageFormats = Arrays.asList(new String[] { "bmp", "gif", "png", "jpg" });
	}

	public class ExifLocationInfo {
		@AComment("拍摄日期")
		private Date createDate;
		@AComment("维度")
		private double lat;
		@AComment("经度")
		private double lon;

		public Date getCreateDate() {
			return createDate;
		}

		public void setCreateDate(Date createDate) {
			this.createDate = createDate;
		}

		public double getLat() {
			return lat;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

		public double getLon() {
			return lon;
		}

		public void setLon(double lon) {
			this.lon = lon;
		}

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

		@AComment("是否有exif信息")
		private boolean hasExif;
		@AComment("exif信息")
		private ExifLocationInfo exif;

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

		public boolean isHasExif() {
			return hasExif;
		}

		public ExifLocationInfo getExif() {
			return exif;
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

		private File originFile; // 上传后的原始文件

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

		// 这里不能用getter
		public File originFile() {
			return originFile;
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
				this.imageProp.thumbFullPath = prefix + name + ImageUploadService.this.prop.getThumb().getPostfix()
						+ extStr;
			}
		}

	}

	public String getThumbPostfix() {
		return this.prop.getThumb().getPostfix();
	}

	/** 根据名字删除旧的附件 */
	public void deleteOldFile(String name, String oldExtName) throws IOException {
		// 查看扩展名
		String extStr = "";
		if (StringUtils.hasText(oldExtName)) {
			extStr = "." + oldExtName;
		}

		// 原始文件的路径 = 前缀 + 文件名 + 扩展名
		String fullPath = ImageUploadProperties.WORK_DIR + name + extStr;
		Files.deleteIfExists(FileSystems.getDefault().getPath(fullPath));

		// 删除缩略图
		String thumbFullPath = ImageUploadProperties.WORK_DIR + name + this.prop.getThumb().getPostfix() + extStr;
		Files.deleteIfExists(FileSystems.getDefault().getPath(thumbFullPath));
	}

	/**
	 * 保存上传的文件, 保存在默认目录下 upload/attectments/yyyyMMdd/465768715787.jpg
	 * 
	 * @param multipartFile
	 *            上传的数据
	 * @see ImageUploadService#DEFAULT_ROOT_PATH 默认保存的目录
	 */
	public SaveResult save(MultipartFile multipartFile)
			throws FileNotFoundException, InvalidImageFormatException, IOException {

		/** 目录例子：upload/attectments/yyyyMMdd */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String path = String.format("%s%s/%s", ImageUploadProperties.UPLOAD_DIR_PREFIX,
				ImageUploadProperties.DEFAULT_ATTACHMENTS_PATH, sdf.format(new Date()));

		// 生成一个唯一性的文件名，避免重复
		String fileName = UniqueIdCreater.createIdInStr();

		return this.save(multipartFile, path, fileName);
	}

	/**
	 * 保存上传的文件
	 * 
	 * @param multipartFile
	 *            上传的数据
	 * @param path
	 *            图片存放路径
	 * @param name
	 *            文件名（无扩展名）
	 * @return 保存的结果
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
		Path filePath = FileUtil.save(multipartFile, ImageUploadProperties.WORK_DIR + res.fullPath);
		res.originFile = filePath.toFile();
		res.fileSize = res.originFile.length();

		if (res.image) {
			try {
				ImageEX old = new ImageEX(res.originFile);
				res.imageProp.imageWidth = old.getWidth();
				res.imageProp.imageHeight = old.getHeight();

				// 生成缩略图
				ImageEX thumb = old.chageImageSizeKeepScaled(this.prop.getThumb().getWidth(),
						this.prop.getThumb().getHeight());
				thumb.outPutImage(res.extName, new File(ImageUploadProperties.WORK_DIR + res.imageProp.thumbFullPath));
				res.imageProp.thumbWidth = thumb.getWidth();
				res.imageProp.thumbHeight = thumb.getHeight();

				if ("jpg".equalsIgnoreCase(res.extName)) {
					ExifLocationInfo exif = this.getExif(res.originFile);
					if (exif != null) {
						res.imageProp.exif = exif;
						res.imageProp.hasExif = true;
					}
				}

			} catch (InvalidImageFormatException e) {
				// 如果是格式错误，就删除这个文件
				res.originFile.delete();
				// 然后继续抛错
				throw e;
			}
		}

		return res;
	}

	/** 从图片中的exif信息中获取位置信息 */
	private ExifLocationInfo getExif(File file) {
		try {
			Metadata metadata = JpegMetadataReader.readMetadata(file);
			GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
			if (gpsDirectory != null) {
				GeoLocation location = gpsDirectory.getGeoLocation();
				if (location != null) {
					log.debug("上传图片 {} 中有地理位置信息:{}", file.getPath(), location.toString());

					ExifLocationInfo info = new ExifLocationInfo();
					info.setLat(location.getLatitude());
					info.setLon(location.getLongitude());
					info.setCreateDate(getCreateDateFromExif(metadata));
					return info;
				}
			}
		} catch (JpegProcessingException | IOException e) {
			log.debug("分析图片位置信息时出错了");
		}

		log.debug("上传的图片{} 中没有地理位置信息", file.getPath());
		return null;
	}

	/** 分析exif中的时间 */
	private static Date parserExifDate(String str) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		try {
			return dateFormat.parse(str);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 从exif中获得GPS的时间信息图片创建的日期
	 * 
	 * @param dir
	 * @return
	 */
	private static Date getCreateDateFromExif(Metadata metadata) {
		Date res = null;

		GpsDirectory gps = metadata.getFirstDirectoryOfType(GpsDirectory.class);
		if (gps != null) {
			String date = gps.getDescription(GpsDirectory.TAG_DATE_STAMP);
			String time = gps.getDescription(GpsDirectory.TAG_TIME_STAMP);
			if (date != null && time != null) {
				res = parserExifDate(date + " " + time);

				if (log.isDebugEnabled()) {
					log.debug("exif中有GPS的信息，从GPS信息中获得创建日期 {}", DateUtil.dateFormat(res));
				}
			}
		}

		if (res == null) {
			ExifSubIFDDirectory sub = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
			if (sub != null) {
				res = parserExifDate(sub.getDescription(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));

				if (log.isDebugEnabled()) {
					log.debug("exif中有相机的信息，从相机信息中获得创建日期 {}", DateUtil.dateFormat(res));
				}
			}
		}

		if (res == null) {
			log.debug("exif中没有GPS的信息，也没有相机的信息");
		}

		return res;
	}
}
