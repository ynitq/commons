package com.cfido.commons.utils.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.springframework.util.Assert;

/**
 * <pre>
 * 资源扫描器，从classpath中扫描所有的文件
 * </pre>
 * 
 * @author 梁韦江 2016年9月28日
 */
public class ResourceScaner {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ResourceScaner.class);

	/**
	 * 在classpath和jar文件中扫描资源
	 * 
	 * @param prefix
	 *            资源前缀，可以是包名，也可以是目录名
	 * @return 资源名字的列表
	 * @throws IOException
	 */
	public static List<String> scan(String prefix) throws IOException {
		ResourceScaner scaner = new ResourceScaner(prefix);
		scaner.doScan();

		List<String> list = new LinkedList<>();
		list.addAll(scaner.scanResult);

		Collections.sort(list);
		return list;
	}

	/** 存储扫描结果 */
	private final Set<String> scanResult = new HashSet<>();

	/** 扫描的路径前缀 */
	private final String pathPrefix;

	private final ClassLoader classLoader;

	private ResourceScaner(String prefix) {

		Assert.hasText(prefix,"前缀不能为空");

		this.pathPrefix = this.buildPrefix(prefix);
		this.classLoader = Thread.currentThread().getContextClassLoader();
	}

	private String buildPrefix(String pathPrefix) {
		String prefix = pathPrefix.replace('.', '/');

		if (prefix.endsWith("/")) {
			return prefix.substring(0, prefix.length() - 1);
		} else {
			return prefix;
		}
	}

	/**
	 * 扫描
	 */
	private void doScan() throws IOException {

		// 获取已这个前缀开头的所有资源
		Enumeration<URL> dirs = this.classLoader.getResources(pathPrefix);

		// 循环遍历所有的目录
		while (dirs.hasMoreElements()) {
			URL url = dirs.nextElement();
			String protocol = url.getProtocol();

			if ("file".equals(protocol)) {
				log.info("扫描file类型的class文件 {}", url);
				doScanPackageClassesByFile(this.pathPrefix, new File(url.getFile()));
			} else if ("jar".equals(protocol)) {
				log.info("扫描jar文件中的类...." + url);
				doScanPackageClassesByJar(url);
			}
		}
	}

	/**
	 * 以文件的方式扫描包下的所有Class文件
	 * 
	 * @param parent
	 *            用于拼接扫描结果的parent
	 * @param dir
	 *            要扫描的目录
	 */
	private void doScanPackageClassesByFile(String parent, File dir) {
		if (!dir.exists() || !dir.isDirectory()) {
			// 如果不是目录就返回
			return;
		}

		// 获取目录的内容
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义文件过滤规则
			@Override
			public boolean accept(File file) {
				// 排除内部类
				String filename = file.getName();
				if (filename.startsWith(".")) {
					// 忽略以.开头的文件，例如.svn
					return false;
				}
				if (file.isFile()) {
					// 忽略内部类
					if (filename.indexOf('$') > 0) {
						return false;
					}
				}
				return true;
			}
		});

		for (File file : dirfiles) {

			String curName = parent + "/" + file.getName();

			if (file.isDirectory()) {
				// 如果是目录就递归扫描
				doScanPackageClassesByFile(curName, file);
			} else {
				this.scanResult.add(curName);
			}
		}
	}

	/**
	 * 以jar的方式扫描包下的所有Class文件<br>
	 * 
	 * @param url
	 * @throws IOException
	 */
	private void doScanPackageClassesByJar(URL url) throws IOException {
		JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String resourceName = entry.getName();

			// 忽略不是以这个前缀开头的文件，忽略目录
			if (!resourceName.startsWith(pathPrefix) || entry.isDirectory()) {
				continue;
			}

			// 忽略内部类
			if (resourceName.indexOf('$') != -1) {
				continue;
			}

			this.scanResult.add(resourceName);
		}
	}
}
