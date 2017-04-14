package com.cfido.commons.utils.db;

import java.io.Serializable;

public class PageNumBean implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3616562967448665501L;
	private int pageNo = 1;
	private boolean bol = false;

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public boolean isBol() {
		return bol;
	}

	public void setBol(boolean bol) {
		this.bol = bol;
	}

	@Override
	public String toString() {
		return "PageNumBean [pageNo=" + pageNo + ", bol=" + bol + "]";
	}

}
