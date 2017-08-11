package com.cfido.commons.utils.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Zip压缩和解压的工具类，支持多线程
 * 
 * @author liangwj
 * 
 */
public class ZipUtil {

	private static int CACHE_SIZE = 1024;

	private static final ThreadLocal<ZipUtil> zipUtilthreadLocal = new ThreadLocal<>();

	/**
	 * 压缩
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static byte[] zip(byte input[]) throws IOException {
		return getZipUtilFromThreadLocal()._compressBytes(input);
	}

	public static byte[] unzip(byte input[]) throws DataFormatException, IOException {
		return getZipUtilFromThreadLocal()._decompressBytes(input);
	}

	/**
	 * 解压
	 * 
	 * @return
	 */
	private static ZipUtil getZipUtilFromThreadLocal() {
		ZipUtil res = zipUtilthreadLocal.get();
		if (res == null) {
			res = new ZipUtil();
			zipUtilthreadLocal.set(res);
		}
		return res;
	}

	private final Deflater compresser = new Deflater();

	private final Inflater decompresser = new Inflater();

	private ZipUtil() {

	}

	private byte[] _compressBytes(byte input[]) throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream(input.length);
		try {
			this.compresser.setInput(input);
			this.compresser.finish();

			byte[] buf = new byte[CACHE_SIZE];
			int got;
			while (!compresser.finished()) {
				got = compresser.deflate(buf);
				byteOut.write(buf, 0, got);
			}
			byteOut.close();
		} finally {
			this.compresser.reset();
		}

		return byteOut.toByteArray();
	}

	private byte[] _decompressBytes(byte input[]) throws DataFormatException, IOException {

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream(input.length);
		try {
			this.decompresser.setInput(input);
			byte[] buf = new byte[CACHE_SIZE];
			int got;
			while (!decompresser.finished()) {
				got = decompresser.inflate(buf);
				byteOut.write(buf, 0, got);
			}
		} finally {
			byteOut.close();
			this.decompresser.reset();
		}
		return byteOut.toByteArray();
	}

}
