package com.cfido.commons.utils.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 图像组件
 * 
 * @author HY
 *
 */
public class ImageUtil {

	/**
	 * 检验图片是否需要调整大小
	 * 
	 * @param bfimg
	 *            图片对象
	 * @param minWidth
	 *            宽度下限
	 * @param minHeight
	 *            高度下限
	 * @return boolean true需要调整,false不需要调整
	 */
	public static boolean isImgInSize(BufferedImage bfimg, int minWidth, int minHeight) {
		if (bfimg.getWidth() > minWidth && bfimg.getHeight() > minHeight)
			return true;
		return false;
	}

	/**
	 * 图片裁剪
	 * 
	 * @throws IOException
	 */
	public static void cutImage(String src, String dest, Integer x, Integer y, int w, int h) throws IOException {
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("jpg");
		ImageReader reader = iterator.next();
		InputStream in = new FileInputStream(src);
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		int imageIndex = 0;
		if (x == null) {
			x = (reader.getWidth(imageIndex) - w) / 2;
		}
		if (y == null) {
			y = (reader.getHeight(imageIndex) - h) / 2;
		}
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		ImageIO.write(bi, "jpg", new File(dest));
	}

	/**
	 * 图片裁剪--从中间开始
	 * 
	 * @throws IOException
	 */
	public static void cutImage(String src, String dest, int w, int h) throws IOException {
		cutImage(src, dest, null, null, w, h);
	}

	/**
	 * 图片缩放.
	 *
	 * @param srcImg
	 *            原始图片
	 * @param destImgFilePath
	 *            缩放后图片文件保存路径
	 * @param w
	 *            图片宽度
	 * @param h
	 *            图片高度
	 * @throws IOException
	 */
	public static void zoomImage(BufferedImage srcImg, String destImgFilePath, int w, int h) throws IOException {

		w = Math.max(w, 5);
		h = Math.max(h, 5);

		double wbs = (double) srcImg.getWidth() / (double) w;
		double hbs = (double) srcImg.getHeight() / (double) h;

		if (wbs < hbs) {
			w = (int) (srcImg.getWidth() / wbs);
			h = (int) (srcImg.getHeight() / wbs);
		} else {
			w = (int) (srcImg.getWidth() / hbs);
			h = (int) (srcImg.getHeight() / hbs);
		}

		Image imgTemp = srcImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);

		double wr = w * 1.0 / srcImg.getWidth();
		double hr = h * 1.0 / srcImg.getHeight();
		AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
		imgTemp = ato.filter(srcImg, null);

		File destFile = new File(destImgFilePath);
		ImageIO.write((BufferedImage) imgTemp, destImgFilePath.substring(destImgFilePath.lastIndexOf(".") + 1), destFile);
	}

	/**
	 * 图片缩放.
	 *
	 * @param srcImgFilePath
	 *            原始图片文件路径
	 * @param destImgFilePath
	 *            缩放后图片文件保存路径
	 * @param w
	 *            图片宽度
	 * @param h
	 *            图片高度
	 * @throws IOException
	 */
	public static void zoomImage(String srcImgFilePath, String destImgFilePath, int w, int h) throws IOException {
		File srcFile = new File(srcImgFilePath);
		BufferedImage bufImg = ImageIO.read(srcFile);

		zoomImage(bufImg, destImgFilePath, w, h);
	}

	public static Random RANDOM = new Random();
	public static String RAND_CODE_STR_SET = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";// 随机产生的字符串
	public static int RAND_CODE_IMG_WIDTH = 80;// 图片宽
	public static int RAND_CODE_IMG_HEIGHT = 26;// 图片高
	public static int RAND_CODE_STR_LEN = 4;// 随机产生字符数量

	/**
	 * 生成随机图片
	 * 
	 * @param request
	 * @param response
	 * @param randomCodeSessionKey
	 *            放到存储到session中的key
	 * @throws IOException
	 */
	public static String getRandcode(HttpServletRequest request, HttpServletResponse response, String randomCodeSessionKey)
			throws IOException {
		HttpSession session = request.getSession();
		// BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
		BufferedImage image = new BufferedImage(RAND_CODE_IMG_WIDTH, RAND_CODE_IMG_HEIGHT, BufferedImage.TYPE_INT_BGR);
		Graphics g = image.getGraphics();// 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
		g.fillRect(0, 0, RAND_CODE_IMG_WIDTH, RAND_CODE_IMG_HEIGHT);
		g.setColor(getRandColor(110, 133));
		// 绘制随机字符
		String randomString = "";
		for (int i = 1; i <= RAND_CODE_STR_LEN; i++) {
			randomString = drowString(g, randomString, i);
		}
		session.removeAttribute(randomCodeSessionKey);
		session.setAttribute(randomCodeSessionKey, randomString);
		g.dispose();
		ImageIO.write(image, "JPEG", response.getOutputStream());// 将内存中的图片通过流动形式输出到客户端

		return randomString;
	}

	/**
	 * 获得字体
	 */
	private static Font getFont() {
		return new Font("Fixedsys", Font.CENTER_BASELINE, 18);
	}

	/**
	 * 获得颜色
	 */
	private static Color getRandColor(int fc, int bc) {
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + RANDOM.nextInt(bc - fc - 16);
		int g = fc + RANDOM.nextInt(bc - fc - 14);
		int b = fc + RANDOM.nextInt(bc - fc - 18);
		return new Color(r, g, b);
	}

	/**
	 * 绘制字符串
	 */
	private static String drowString(Graphics g, String randomString, int i) {
		g.setFont(getFont());
		g.setColor(new Color(RANDOM.nextInt(101), RANDOM.nextInt(111), RANDOM.nextInt(121)));
		String rand = String.valueOf(getRandomString(RANDOM.nextInt(RAND_CODE_STR_SET.length())));
		randomString += rand;
		g.translate(RANDOM.nextInt(3), RANDOM.nextInt(3));
		g.drawString(rand, 13 * i, 16);
		return randomString;
	}

	/**
	 * 获取随机的字符
	 */
	public static String getRandomString(int num) {
		return String.valueOf(RAND_CODE_STR_SET.charAt(num));
	}
}
