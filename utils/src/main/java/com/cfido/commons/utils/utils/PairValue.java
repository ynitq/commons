package com.cfido.commons.utils.utils;

/**
 * <pre>
 * 一对值。
 * 通常用于作为Map的value
 * </pre>
 * 
 * @author 梁韦江
 * 2015年5月15日
 * @param <T>
 * @param <S>
 */
public class PairValue<T, S> {

	private T one;
	private S two;

	public PairValue(T one, S two) {
		this.one = one;
		this.two = two;
	}

	public T getOne() {
		return one;
	}

	public void setOne(T one) {
		this.one = one;
	}

	public S getTwo() {
		return two;
	}

	public void setTwo(S two) {
		this.two = two;
	}

}
