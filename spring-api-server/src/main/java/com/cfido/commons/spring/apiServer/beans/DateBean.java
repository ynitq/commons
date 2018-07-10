package com.cfido.commons.spring.apiServer.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cfido.commons.annotation.bean.AComment;

/**
 * <pre>
 * 时间类型
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public class DateBean {

	private final long time;
	private String ymd;
	private String full;

	public DateBean(Date date) {
		long time = 0;
		if (date != null) {
			time = date.getTime();
		}
		this.time = time;
	}

	public DateBean(long time) {
		this.time = time;
	}

	@AComment("年月日时分秒")
	public String getFull() {
		if (this.full == null && this.time > 0) {
			Date date = new Date(time);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			this.full = sdf.format(date);
		}
		return full;
	}

	@AComment("原始时间")
	public long getTime() {
		return time;
	}

	@AComment("年月日")
	public String getYmd() {
		if (this.ymd == null && this.time > 0) {
			Date date = new Date(time);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			this.ymd = sdf.format(date);
		}
		return ymd;
	}
}
