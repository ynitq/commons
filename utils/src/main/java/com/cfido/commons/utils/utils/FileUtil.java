package com.cfido.commons.utils.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件处理工具集
 * 
 * @author 梁韦江
 *
 */
public class FileUtil {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 读取文件
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String filePath) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream is = new FileInputStream(new File(filePath));
		byte[] buf = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		is.close();

		return out.toString("UTF-8");
	}

	public static String readFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		String ret = readFile(is);
		is.close();
		return ret;
	}

	public static String readFile(InputStream is) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		byte[] buf = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		byte[] lens = out.toByteArray();
		String result = new String(lens);
		return result;
	}

	/**
	 * 从classpath读取文件
	 * 
	 * @param thisObj
	 *            调用本方法的当前对象，需要从当前对象中获得ClassLoader，所以必须将对象传进来
	 * @param className
	 * @return
	 */
	public static String readFileFromClassPath(Object thisObj, String className) {
		InputStream is = thisObj.getClass().getClassLoader().getResourceAsStream(className);
		if (is != null) {
			try {
				return readFile(is);
			} catch (IOException e) {
				log.error("无法从 classpath中读取资源", e);
			}
		}
		return null;
	}

	/**
	 * 写文件
	 * 
	 * @param path
	 *            文件名和路径
	 * @param content
	 *            要写的内容
	 * @param override
	 *            是否覆盖原文件
	 * @throws IOException
	 */
	public static void write(String path, String content, boolean override) throws IOException {
		if (path == null || content == null) {
			return;
		}

		File file = new File(path);
		if (file.exists() && !override) {
			// 如果文件存在，并且不允许覆盖，就退出了事
			return;
		}

		File dir = file.getParentFile();
		if (!dir.exists()) {
			// 如果目录不存在，就创建目录
			dir.mkdirs();
		}

		if (file.exists()) {
			// 如果文件存在，就删除文件
			file.delete();
		}

		file.createNewFile();
		FileOutputStream out = new FileOutputStream(file);
		out.write(content.getBytes("UTF-8"));
		out.close();
	}

	/**
	 * 写文件
	 * 
	 * @throws IOException
	 */
	public static void write(String path, String content) throws IOException {
		write(path, content, true);
	}

	private static final SimpleDateFormat FORMAT_FOR_SAVE_FILE = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 将上传的文件流，写到一个文件中
	 * 
	 * @param multipartFile
	 *            上传文件对象
	 * @param saveFilePath
	 *            文件准备放置的路径
	 * @return
	 * @throws IOException
	 */
	public static Path save(MultipartFile multipartFile, String saveFilePath) throws IOException {
		if (multipartFile == null || multipartFile.isEmpty()) {
			// 如果上传文件的数据，就返回 null
			return null;
		}

		// 构建文件全路径
		Path filePath = FileSystems.getDefault().getPath(saveFilePath);

		// 如果目录不存在，就创建目录
		File parent = filePath.toFile().getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}

		// 将上传文件流写入文件
		Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		return filePath;
	}

	/**
	 * 生成随机文件名。例如 myroot/20160831/2323456574678.jpg
	 * 
	 * @param multipartFile
	 *            上传文件对象
	 * @param rootPath
	 *            文件准备放置的根路径，保存时会在后面拼接时间。可以是相对路径
	 * @param defaultExtName
	 *            默认的扩展名，如果原始文件没有扩展名，就用这个
	 * @return
	 */
	public static String getRandomFileName(MultipartFile multipartFile, String rootPath, String defaultExtName) {

		Assert.notNull(multipartFile, "multipartFile不能为空");
		Assert.isTrue(!multipartFile.isEmpty(), "multipartFile中必须有文件数据");
		Assert.notNull(rootPath, "rootPath不能为空");

		// 原始文件名
		String originalFilename = multipartFile.getOriginalFilename();

		// 获取文件扩展名
		String extName = defaultExtName;
		int index = originalFilename.lastIndexOf(".");
		if (index > 0) {
			// 如果能找到“.” 就用.后面的作为文件扩展名
			extName = originalFilename.substring(index + 1);
		}
		if (extName == null) {
			// 防止扩展名为 null
			extName = "";
		}

		// 格式例子 myroot/20160831/2323456574678.jpg
		String randomFileName = String.format("%s/%s/%d.%s",
				rootPath, FORMAT_FOR_SAVE_FILE.format(new Date()),
				System.nanoTime(),
				extName);

		return randomFileName;
	}

}
