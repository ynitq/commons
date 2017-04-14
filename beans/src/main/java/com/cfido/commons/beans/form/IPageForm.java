package com.cfido.commons.beans.form;

/**
 * 描述翻页form的接口
 * 
 * @author liangwj
 * 
 */
public interface IPageForm {

	/** 只要1条结果 */
	public static final IPageForm ONE = new IPageForm() {

		@Override
		public void verifyPageNo() {
		}

		@Override
		public int getPageSize() {
			return 1;
		}

		@Override
		public int getPageNo() {
			return 1;
		}
	};

	public static final int DEFAULT_PAGE_SIZE = 15;

	/**
	 * 返回页码，从1开始
	 * 
	 * @return 当前页码
	 */
	int getPageNo();

	/**
	 * 每页的记录数
	 * 
	 * @return pageSize
	 */
	int getPageSize();

	/**
	 * 校验页码
	 */
	void verifyPageNo();
}
