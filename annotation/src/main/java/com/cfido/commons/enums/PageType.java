package com.cfido.commons.enums;

/***
 *  <pre>
 * 单选框共用枚举 查询条件显示类型
 * </pre>
 * @author gu.anni
 *  15/11/26.
 */
public enum PageType {

    EDIT(0,"edit"),//只有编辑--针对配置表
    LIST(1,"list"),//只能查看--带审核等功能
    LIST_NOCHANGE(2,"list_no"),//只能查看--不带审核等功能
    ALL(3,"all"),

    UNKNOW(-100,"unknow");

    private PageType(int code,String name){
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

    public static PageType parse(int code){
        PageType[] values = PageType.values();
        for(PageType adminType:values){
            if(adminType.code==code)
                return adminType;
        }
        return UNKNOW;
    }
}
