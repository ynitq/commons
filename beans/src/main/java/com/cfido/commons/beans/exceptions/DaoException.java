package com.cfido.commons.beans.exceptions;

/**
 * <pre>
 * 数据保存异常封装.封装起来是为了方便外层捕获
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月24日
 */
public class DaoException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new dao exception.
	 */
	public DaoException() {
		super();
	}

	/**
	 * Instantiates a new dao exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new dao exception.
	 *
	 * @param message
	 *            the message
	 */
	public DaoException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new dao exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public DaoException(Throwable cause) {
		super(cause);
	}

}
