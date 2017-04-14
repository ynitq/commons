package com.cfido.commons.utils.utils;

/**
 * 对byte数组进行位移动操作的工具
 * 
 * @author 梁韦江 2011-6-2
 */
public class BitUtils {

	public static void shiftRight(byte[] src, int bit) {
		if (src.length == 0) {
			return;
		}

		int b = bit % 8;
		if (b == 0) {
			return;
		}

		int mask = (byte) (0xff >> (8 - b));
		int last = src[src.length - 1] & 0xff;

		for (int i = 0; i < src.length; i++) {
			int hight = (byte) ((last & mask) << (8 - b));
			last = src[i] & 0xff;
			src[i] = (byte) ((last >> b) | hight);
		}
	}
	
	public static void shiftLeft(byte[] src, int bit) {
		if (src.length==0) {
			return ;
		}
		
		int b = bit % 8;
		if (b ==0) {
			return ;
		}
		
		int mask = 0xff << (8 - b) & 0xff;
		
		int last = src[0];
		
		for (int i = src.length - 1; i >= 0; i--) {
			int hight = (last & mask) >> (8 - b);
			last = src[i] & 0xff;
			src[i] = (byte) ((last << b) | hight);
		}
	}


	public static String toBinaryString(byte[] src) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < src.length; i++) {
			for (int j = 0; j < 8; j++) {
				int p = (src[i] << j & 0x80) / 0x80;
				sb.append(p);
			}
			sb.append(" ");
		}
		return sb.toString();
	}

}
