package com.cfido.commons.spring.apiServer.core;

/**
 * <pre>
 * 用于描述api form中每一个setter的信息
 * </pre>
 * 
 * @author 梁韦江 2016年7月4日
 */
public class MethodParamVo {

	/** setter 名 */
	private String name;
	/** 默认值 */
	private String value;
	/** 备注 */
	private String memo;
	/** 是否文件上传字段 */
	private boolean uploadFile;
	/** setter的类 */
	private String className;
	/** 是否可空 */
	private boolean notNull;

	/** 参数是否是数组 */
	private boolean array;

	public boolean isArray() {
		return array;
	}

	public void setArray(boolean array) {
		this.array = array;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public boolean isUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(boolean uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isCheckBox() {
		return "boolean".equalsIgnoreCase(this.className);
	}

}
