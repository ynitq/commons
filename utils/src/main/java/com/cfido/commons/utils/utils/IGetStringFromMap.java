package com.cfido.commons.utils.utils;

/**
 * <pre>
 * 用于StringUtil中的getStringFromMap方法
 * </pre>
 * 
 * @see StringUtils#getStringFromMap(String, IGetStringFromMap)
 * 
 * @author 梁韦江 2015年8月4日
 */
public interface IGetStringFromMap {
	String get(String key);
}
