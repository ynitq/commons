package com.cfido.commons.enums;

/**
 * 
 * <pre>
 * jquer-validation定义的各种验证
 * </pre>
 * 
 * @author 黄云
 * 2015年12月9日
 */
public enum ValidEnum {

	REQUIRED(0, "required"),
    EMAIL(1, "email"),
    MOBILE(2, "mobile"),
    ORGCODE(3, "orgCode"),
    CARD(4, "card"),
    CN(5, "cn"),
    URL(6, "url"),
    DATE(7, "date"),
    NUMBER(8, "number"),
    INT(9, "digits"),
    EQUALTO(10, "equalTo"),
    ISNOTZERO(11, "isNotZero"),
    DONE(12, "decimalsForOne"),
    DTOW(13, "decimalsForTwo"),

    UNKNOW(-100,"unknow");

    private ValidEnum(int code,String name){
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

    public static ValidEnum parse(int code){
        ValidEnum[] values = ValidEnum.values();
        for(ValidEnum adminType:values){
            if(adminType.code==code)
                return adminType;
        }
        return UNKNOW;
    }
}
