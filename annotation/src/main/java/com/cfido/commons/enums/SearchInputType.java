package com.cfido.commons.enums;

/**
 * <pre>
 * 查询条件显示类型
 * </pre>
 * 
 * @author 黄云
 * 2015-6-16
 */
public enum SearchInputType {

	TEXT(0,"text"),
	SELECT(1,"select"),
	DATE(2,"date"),
	TIME(3,"datetime"),
	SELECTVO(4,"selectvo"),
	
	UNKNOW(-100,"unknow");
	
	private SearchInputType(int code,String name){
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
	
	public static SearchInputType parse(int code){
		SearchInputType[] values = SearchInputType.values();
		for(SearchInputType adminType:values){
			if(adminType.code==code)
				return adminType;
		}
		return UNKNOW;
	}
	
}
