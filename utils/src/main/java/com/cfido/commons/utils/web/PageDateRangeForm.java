package com.cfido.commons.utils.web;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.cfido.commons.annotation.form.ABuildWhereExclude;
import com.cfido.commons.annotation.form.ABuildWhereOptStr;
import com.cfido.commons.beans.form.PageForm;
import com.cfido.commons.utils.utils.DateUtil;

@Deprecated
public class PageDateRangeForm extends PageForm implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private String endDateStr;
	private String startDateStr;

	private Date startDate;

	private Date endDate;

	@ABuildWhereOptStr(optStr = "<=")
	public Date getEndDate() {
		return endDate;
	}

	@ABuildWhereExclude
	public String getEndDateStr() {
		return endDateStr;
	}

	@ABuildWhereOptStr(optStr = ">=")
	public Date getStartDate() {
		return startDate;
	}

	@ABuildWhereExclude
	public String getStartDateStr() {
		return startDateStr;
	}

	/**
	 * 字符串类型变成时间类型，
	 * 
	 * @param str
	 * @param isStartDate
	 *            如果为false，自动添加23:59:59
	 * @return
	 */
	private Date parserDate(String str, boolean isStartDate) {
		Date date;
		try {
			date = DATE_FORMAT.parse(str);
		} catch (Throwable e) {
			date = new Date();
		}
		return DateUtil.ceilDateToDay(date, isStartDate);
	}

	public void setEndDateStr(String endDateStr) {
		this.endDateStr = endDateStr;
		this.endDate = this.parserDate(endDateStr, false);
	}

	public void setStartDateStr(String startDateStr) {
		this.startDateStr = startDateStr;
		this.startDate = this.parserDate(startDateStr, true);
	}

	/**
	 * 校验时间为：默认从开始到现在
	 */
	public void verifyDateForBegin() {
		if (this.startDateStr == null) {
			this.startDateStr = "2013-01-01";
		}
		this.startDate = this.parserDate(startDateStr, true);

		if (this.endDateStr == null) {
			this.endDateStr = DATE_FORMAT.format(new Date());
		}
		this.endDate = this.parserDate(endDateStr, false);
	}

	/**
	 * 校验时间为：默认从前多少天到现在
	 */
	public void verifyDateForDays(int days) {
		if (this.startDateStr == null) {
			long now = System.currentTimeMillis();
			this.startDate = DateUtil.ceilDateToDay(new Date(now - TimeUnit.DAYS.toMillis(days)), true);
			this.startDateStr = DATE_FORMAT.format(startDate);
		} else {
			this.startDate = this.parserDate(startDateStr, true);
		}

		if (this.endDateStr == null) {
			this.endDateStr = DATE_FORMAT.format(new Date());
		}
		this.endDate = this.parserDate(endDateStr, false);
	}

}
