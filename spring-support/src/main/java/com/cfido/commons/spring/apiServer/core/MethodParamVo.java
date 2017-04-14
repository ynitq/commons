package com.cfido.commons.spring.apiServer.core;

/**
 * <pre>
 * 用于描述api form中每一个setter的信息
 * </pre>
 * 
 * @author 梁韦江
 *  2016年7月4日
 */
public class MethodParamVo {

	private String name;
	private String value;
	private String memo;
	private boolean uploadFile;

	private String className;

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
