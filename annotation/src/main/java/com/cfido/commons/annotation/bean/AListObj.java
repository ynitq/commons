package com.cfido.commons.annotation.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.linzi.common.enums.SaveInputType;

/**
 * <pre>
 * 列表字段标识
 * </pre>
 * 
 * @author 黄云
 * 2015年11月11日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE , ElementType.FIELD })
@Documented
public @interface AListObj {
	
	public abstract String comment();
	
	public abstract int length() default 10;
	
	public abstract String option() default "AUSV";//a-add,u-update,-s-select,v-view,c-change(对应审核字段)
	
	public abstract String optionName() default "";
	
	public abstract SaveInputType cType() default SaveInputType.TEXT;//保存控件类型
	
	public abstract SaveInputType vType() default SaveInputType.IMG;//保存控件类型
	
	public abstract Class<?> selectEnum() default Object.class;
	
	
	//----------下拉关联数据指向-----------
	
	public abstract Class<?> selectVo() default Object.class;//下拉数据源VO
	
	public abstract String selectVoName() default "name";//下拉展示字段
	
	public abstract boolean isChild() default false;//是否子项
	
	public abstract boolean isParent() default false;//是否本表父类
	
	public abstract String parentName() default "";//级联的父级字段
	
}
