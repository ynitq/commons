package com.cfido.commons.utils.utils;

import java.util.LinkedList;
import java.util.List;

import com.cfido.commons.beans.others.IConverter;

/**
 * <pre>
 * 转换器工具，配合IConverter
 * </pre>
 * 
 * @see IConverter
 * 
 * @author 梁韦江
 *  2016年8月31日
 */
public class ConverterUtil {

	/**
	 * 将list转换成为另外一个类型
	 * 
	 * @param list
	 * @param converter
	 * @return
	 */
	public static <SRC, TARGET> List<TARGET> convertList(List<SRC> list, IConverter<SRC, TARGET> converter) {
		if (list == null) {
			return null;
		}
		List<TARGET> res = new LinkedList<>();
		for (SRC po : list) {
			res.add(converter.convert(po));
		}
		return res;
	}
}
