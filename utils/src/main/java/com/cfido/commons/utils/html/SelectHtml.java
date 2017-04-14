package com.cfido.commons.utils.html;

import java.util.LinkedList;
import java.util.List;

/**
 * 用于生成html中的select
 * 
 * @author liangwj
 * 
 */
/**
 * <pre>
 * 
 * </pre>
 * 
 * @author 梁韦江
 * 2015年5月15日
 */
public class SelectHtml {
	class Options {
		private String desc;
		private String value;
	}

	private final List<Options> optList = new LinkedList<Options>();
	private String defaultValue;

	/**
	 * 设置默认值
	 * @param defaultValue 默认值
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * 设置默认值
	 * @param defaultValue 默认值
	 */
	public void setDefaultValue(int defaultValue) {
		this.setDefaultValue(String.valueOf(defaultValue));
	}

	/**
	 * 增加下拉框中的值
	 * @param desc 说明
	 * @param value 值
	 */
	public void addOptions(String desc, String value) {
		Options o = new Options();

		if (desc == null) {
			o.desc = "";
		} else {
			o.desc = desc;
		}
		if (value == null) {
			o.value = "";
		} else {
			o.value = value;
		}

		this.optList.add(o);
	}

	/**
	 * 增加下拉框中的值
	 * @param desc 说明
	 * @param value 值
	 */
	public void addOptions(String desc, int value) {
		this.addOptions(desc, String.valueOf(value));
	}

	/**
	 * 生成HTML代码 
	 * @param name 生成中的html代码中的 name属性的值
	 * @return
	 */
	public String htmlCodeSelect(String name) {
		return this.htmlCodeSelect(name, "");
	}

	/**
	 * @param name 生成中的html代码中的 name属性的值
	 * @param extend html中要额外添加的内容，例如 class="xxx"
	 * @return
	 */
	public String htmlCodeSelect(String name, String extend) {
		StringBuffer strb = new StringBuffer(1280);
		strb.append("<select name='").append(name).append("' ").append(extend).append(" >\n");
		for (Options o : this.optList) {
			strb.append("<option value='").append(o.value).append("'");
			if (this.defaultValue != null && this.defaultValue.equals(o.value)) {
				strb.append(" selected ");
			}
			strb.append(">").append(o.desc).append("</option>\n");
		}
		strb.append("</select>");

		return strb.toString();
	}

}
