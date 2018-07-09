package com.cfido.commons.spring.dict.inf.responses;

import com.cfido.commons.spring.dict.schema.DictXml.DictAttachmentRow;

/**
 * <pre>
 * 附件列表返回的结果, 属性与DictAttachmentRow对应
 * </pre>
 * 
 * @see DictAttachmentRow
 * 
 * @author 梁韦江 2017年2月22日
 */
public class DictAttachmentVo {

	private DictAttachmentRow row;

	private String fullPath; // 文件全路径
	private String fullThumbPath; // 如果是图片，还有缩略图的路径

	public void updateFromXml(DictAttachmentRow row, String basePath, String thumbPostfix) {
		this.row = row;

		this.fullPath = String.format("%s/%s/%s.%s", basePath, row.getPathPrefix(), row.getKey(), row.getExtName());
		if (row.isImageFile()) {
			this.fullThumbPath = String.format("%s/%s/%s%s.%s", basePath, row.getPathPrefix(),
					row.getKey(), thumbPostfix, row.getExtName());
		}
	}

	public DictAttachmentRow getRow() {
		return row;
	}

	public void setRow(DictAttachmentRow row) {
		this.row = row;
	}

	public String getFullPath() {
		return fullPath;
	}

	public String getFullThumbPath() {
		return fullThumbPath;
	}

}
