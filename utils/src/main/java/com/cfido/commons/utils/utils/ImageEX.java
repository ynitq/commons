package com.cfido.commons.utils.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Random;

import javax.imageio.ImageIO;

import org.springframework.util.Assert;

import com.cfido.commons.beans.apiExceptions.InvalidImageFormatException;

/**
 * 图像工具类：改变图片大小、加水印、验证码图片
 * 
 * @author HY
 */
public class ImageEX {

	// 画图属性
	private final BufferedImage bufimageOjb;// buffer图像

	/**
	 * 初始化对象
	 */
	public ImageEX(BufferedImage bufimage) {
		Assert.notNull(bufimage,"初始化的图片不能为空");
		bufimageOjb = bufimage;

	}

	/** 返回bufimageOjb */
	public BufferedImage getBufimageOjb() {
		return bufimageOjb;
	}

	/**
	 * 根据一个输入流创建一个图像对象
	 * 
	 * @param ins
	 *            输入流
	 * @throws IOException
	 * @throws InvalidImageFormatException
	 */
	public ImageEX(InputStream ins) throws IOException, InvalidImageFormatException {
		bufimageOjb = ImageIO.read(ins);
		this.checkFormat();
	}

	/**
	 * 根据一个文件创建一个图像对象
	 * 
	 * @param f
	 *            文件
	 * @throws IOException
	 * @throws InvalidImageFormatException
	 */
	public ImageEX(File f) throws IOException, InvalidImageFormatException {
		bufimageOjb = ImageIO.read(f);
		this.checkFormat();
	}

	/**
	 * 根据URL 创建图像对象
	 * 
	 * @param url
	 *            图像的URL地址
	 * @throws IOException
	 * @throws InvalidImageFormatException
	 */
	public ImageEX(URL url) throws IOException, InvalidImageFormatException {
		bufimageOjb = ImageIO.read(url);
		this.checkFormat();
	}

	/**
	 * 把图像写到输出流
	 * 
	 * @param formatName
	 *            格式
	 * @param out
	 *            输出流
	 * @return
	 * @throws IOException
	 */
	public void outPutImage(String formatName, OutputStream out) throws IOException {
		try {
			ImageIO.write(this.bufimageOjb, formatName, out);
		} finally {
			out.close();
		}
	}

	/**
	 * 把图像输出到文件中
	 * 
	 * @param formatName
	 *            文件格式
	 * @param f
	 *            文件对象
	 * @return 图片
	 * @throws IOException
	 */
	public void outPutImage(String formatName, File f) throws IOException {
		ImageIO.write(this.bufimageOjb, formatName, f);
	}

	/**
	 * 将转换后图像结果大小
	 * 
	 * @param width
	 *            图像宽
	 * @param height
	 *            图像高
	 * @return 转换完成的图像对象
	 * @throws IOException
	 */
	public ImageEX chageImageSize(int width, int height) {
		if (this.bufimageOjb == null)
			return null;

		// 根据源图像读取
		BufferedImage buffbufimage = null;
		buffbufimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // 创建目的图像对象

		Graphics2D g2d = buffbufimage.createGraphics();
		// ---------- 增加下面的代码使得背景透明 -----------------
		buffbufimage = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.OPAQUE);
		g2d.dispose();

		buffbufimage.getGraphics().drawImage(bufimageOjb.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null); // 绘制目的图像
		return new ImageEX(buffbufimage);
	}

	public int getWidth() {
		return this.bufimageOjb.getWidth();
	}

	public int getHeight() {
		return this.bufimageOjb.getHeight();
	}

	/**
	 * 把当前图片作为水印，印刷到目标图片上
	 * 
	 * @param targetbufimage
	 *            -- 目标图片
	 * @param x
	 *            --x坐标
	 * @param y
	 *            --y坐标
	 */
	public ImageEX pressImage(BufferedImage targetbufimage, int x, int y) {
		Assert.notNull(targetbufimage, "目标图片不能为空");

		// 生成和目标文件相同尺寸的空白图片
		int width = targetbufimage.getWidth();
		int height = targetbufimage.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.createGraphics();

		try {
			// 先将目标图片画上去
			g.drawImage(targetbufimage, 0, 0, width, height, null);

			// 水印文件
			int wideth_biao = this.bufimageOjb.getWidth();
			int height_biao = this.bufimageOjb.getHeight();
			g.drawImage(this.bufimageOjb, (width - wideth_biao) / 2,
					(height - height_biao) / 2, wideth_biao, height_biao, null);
			// 水印文件结束
		} finally {
			g.dispose();
		}

		// 将新的图片作为新的对象返回
		return new ImageEX(image);
	}

	/**
	 * 打印文字水印图片
	 * 
	 * @param pressText
	 *            --文字
	 * @param fontName
	 *            -- 字体名
	 * @param fontStyle
	 *            -- 字体样式
	 * @param color
	 *            -- 字体颜色
	 * @param fontSize
	 *            -- 字体大小
	 * @param x
	 *            X
	 * 
	 * @param y
	 *            Y
	 * 
	 */
	public ImageEX pressText(String pressText, String fontName, int fontStyle,
			Color color, int fontSize, int x, int y) {
		// 生成同尺寸空白图
		int width = this.bufimageOjb.getWidth();
		int height = this.bufimageOjb.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.createGraphics();

		try {
			// 将原图先画上去
			g.drawImage(this.bufimageOjb, 0, 0, width, height, null);
			g.setColor(color);

			// 画文字
			g.drawString(pressText, width - fontSize - x, height - fontSize / 2 - y);
		} finally {
			g.dispose();
		}

		// 生成新对象返回
		return new ImageEX(image);
	}

	/**
	 * 创建一个验证码的图片
	 * 
	 * @param width1
	 *            图片宽
	 * @param height1
	 *            图片高度
	 * @param str1
	 *            验证码
	 * @param cntline
	 *            线条数
	 * @return 图片
	 */
	public static ImageEX createCheckCodeImage(int width1, int height1, String str1,
			int cntline) {

		Random sum = new Random();
		BufferedImage image = new BufferedImage(width1, height1,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics(); // 获得该图上下文
		try {
			g.setColor(Color.WHITE); // 背景色
			g.fillRect(0, 0, width1, height1);
			g.drawRect(0, 0, width1 - 1, height1 - 1);
			/* g.setColor(getRandColor(125,200)); */// 随机颜色
			// 字体样式
			for (int m = 0; m < str1.length(); m++) {
				g.setFont(new Font("Times New Roman", Font.PLAIN, 18)); // 字体大小
				g.setColor(getRandColor(0, 255));
				String cstr = str1.substring(m, m + 1);
				g.drawString(cstr, (width1 / 4 + 2) * m, (height1 / 2) + 5); // 字体间距
			}
			// 干扰点\线
			for (int i = 0; i < 3; i++) {
				int x = sum.nextInt(width1);
				int y = sum.nextInt(height1);
				g.setColor(getRandColor(50, 255));
				int lineX = sum.nextInt(height1);
				int lineY = sum.nextInt(width1);
				g.drawLine(x, y, lineX, lineY);
			}
			for (int i = 0; i < cntline; i++) {
				int x = sum.nextInt(width1);
				int y = sum.nextInt(height1);
				g.setColor(getRandColor(100, 200));
				g.drawLine(x, y, x + 2, y + 2);
			}
		} finally {
			g.dispose();
		}
		return new ImageEX(image);
	}

	// 设置随机颜色函数
	// 设置随机颜色
	private static Color getRandColor(int fc, int bc) {// 给定范围获得随机颜色
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int gc = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, gc, b);
	}

	/**
	 * 按比例创建另一个大小的图像,改变后的图像不能超过最大宽度或最大高度
	 * 
	 * @param maxWidth
	 *            改变后的最大宽度
	 * @param maxHeight
	 *            改变后的最大高度
	 */
	public ImageEX chageImageSizeKeepScaled(int maxWidth, int maxHeight) {

		Assert.isTrue(maxWidth > 0,"宽度必须>0");
		Assert.isTrue(maxHeight > 0,"高度必须>0");

		double widthRate = (bufimageOjb.getWidth() * 1d) / maxWidth;
		double heightRate = (bufimageOjb.getHeight() * 1d) / maxHeight;

		double realRate = widthRate;
		if (heightRate > realRate) {
			realRate = heightRate;
		}

		int realWidth = (int) (bufimageOjb.getWidth() / realRate);
		int realHeight = (int) (bufimageOjb.getHeight() / realRate);

		// 根据源图像读取
		BufferedImage buffbufimage = null;
		buffbufimage = new BufferedImage(realWidth, realHeight, BufferedImage.TYPE_INT_RGB); // 创建目的图像对象

		Graphics2D g2d = buffbufimage.createGraphics();
		// ---------- 增加下面的代码使得背景透明 -----------------
		buffbufimage = g2d.getDeviceConfiguration().createCompatibleImage(realWidth, realHeight, Transparency.OPAQUE);
		g2d.dispose();

		buffbufimage.getGraphics().drawImage(bufimageOjb.getScaledInstance(realWidth, realHeight, Image.SCALE_SMOOTH), 0, 0,
				null); // 绘制目的图像
		return new ImageEX(buffbufimage);
	}

	/**
	 * 检查格式是否非法
	 * 
	 * @throws InvalidImageFormatException
	 */
	public void checkFormat() throws InvalidImageFormatException {
		if (this.bufimageOjb == null) {
			throw new InvalidImageFormatException();
		}
	}

}
