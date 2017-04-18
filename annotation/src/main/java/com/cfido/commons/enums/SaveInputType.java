package com.cfido.commons.enums;

/**
 * <pre>
 * 查询条件显示类型
 * </pre>
 * 
 * @author 黄云
 * 2015-6-16
 */
public enum SaveInputType {

	TEXT(0,"text"),//文本框
	SELECT(1,"select"),//下拉框
	DATE(2,"date"),//日期控件
	RADIO(3,"radio"),//单选框
	CHECKBOX(4,"checkbox"),//复选框
	FILE(5,"file"),//附件上传控件
	TEXTAREA(6,"textarea"),//大文本框
	BACKGROUND(7,"background"),//背景
	TEXTEDIT(8,"textedit"),//文本编辑器
	IMG(9,"img"),//图片上传控件
	SELECTVO(10,"selectvo"),//下拉框--forVo
	CHECKBOXVO(11,"checkboxvo"),//复选框--forVo
	TIME(12,"datetime"),//日期控件
	PWD(13,"pwd"),//密码
	SEARCHVO(14,"searchvo"),//数据查询
	ONE2MANY(15,"one2many"),//数据查询
	
	UNKNOW(-100,"unknow");
	
	private SaveInputType(int code,String name){
		this.code=code;
		this.name=name;
	}
	
	private int code;
	private String name;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public static SaveInputType parse(int code){
		SaveInputType[] values = SaveInputType.values();
		for(SaveInputType adminType:values){
			if(adminType.code==code)
				return adminType;
		}
		return UNKNOW;
	}
	
}
