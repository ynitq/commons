package com.cfido.commons.beans.others;

/**
 * <pre>
 * 过滤器
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月25日
 */
public interface IFilter<T> {

	boolean isMatch(T obj);

}
