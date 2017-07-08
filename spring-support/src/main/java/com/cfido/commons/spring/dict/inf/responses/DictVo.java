package com.cfido.commons.spring.dict.inf.responses;

import org.springframework.util.StringUtils;

import com.cfido.commons.spring.dict.schema.DictXml.DictXmlRow;

/**
 * <pre>
 * 搜索返回的结果, 属性与DictXmlRow对应
 * </pre>
 * 
 * @see DictXmlRow
 * 
 * @author 梁韦江 2015年8月4日
 */
public class DictVo {

	private static final int STRING_LEN_LIMIT = 30;

	private String key;
	private String value;
	private boolean todo;
	private int usedCount;
	private boolean html;
	private int type;
	private String memo;

	/** 标注和搜索关键字的 key的html */
	private String keyWithMark;

	/** 预览的html */
	private String preview;

	public void updateFromXml(DictXmlRow row) {
		this.value = row.getValue();
		this.todo = row.isTodo();
		this.html = row.isHtml();
		this.usedCount = row.getUsedCount();
		this.key = row.getKey();
		this.type = row.getType();
		this.memo = row.getMemo();
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getValue() {
		return value;
	}

	public boolean isTodo() {
		return todo;
	}

	public int getUsedCount() {
		return usedCount;
	}

	public boolean isHtml() {
		return html;
	}

	public String getKey() {
		return key;
	}
	
	public int getType() {
		return type;
	}

	/**
	 * 标准搜索关键字
	 */
	public void updateSearchKeyword(String keyword) {
		if (org.springframework.util.StringUtils.hasText(keyword)) {
			String mark = String.format("<span class='search_mark'>%s</span>", keyword);
			this.keyWithMark = this.key.replace(keyword, mark);
		} else {
			this.keyWithMark = this.key;
		}
	}

	public String getKeyWithMark() {
		return keyWithMark;
	}

	/**
	 * 获得value的30字
	 * 
	 * @return
	 */
	public String getValueSummary() {
		if (!StringUtils.hasText(this.value)) {
			return "";
		} else {
			// 先删除html标签
			String noHtmlValue = com.cfido.commons.utils.utils.StringUtilsEx.delHTMLTag(this.value);
			if (noHtmlValue.length() > STRING_LEN_LIMIT) {
				noHtmlValue = noHtmlValue.substring(0, STRING_LEN_LIMIT) + "...";
			}
			return noHtmlValue;
		}
	}

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

}
