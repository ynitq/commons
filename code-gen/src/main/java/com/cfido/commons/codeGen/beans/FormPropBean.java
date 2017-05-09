package com.cfido.commons.codeGen.beans;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.cfido.commons.codeGen.constants.FormPropEnum;
import com.cfido.commons.utils.utils.StringUtils;

/**
 * <pre>
 * 设置是否保存form
 * </pre>
 * 
 * @author 梁韦江 2016年10月12日
 */
public class FormPropBean {

	private static final String SEP = "|";

	/** 存储字段说明中定义的属性 */
	private final Set<String> propSet = new HashSet<>();

	protected FormPropBean() {

	}

	/**
	 * 解析字段说明，并且返回备注
	 * 
	 * <pre>
	 * 我们在数据库中放代码生成器的信息，可通过修改字段的备注，自动生成表单。
	 * 
	 * 格式：属性1|属性2|...|备注
	 * </pre>
	 * 
	 * @param str
	 * @return
	 */
	protected String parserComment(String str) {
		String comment = null;

		if (StringUtils.hasText(str)) {

			StringTokenizer tokenizer = new StringTokenizer(str, SEP, false);

			while (tokenizer.hasMoreTokens()) {
				comment = tokenizer.nextToken();

				this.propSet.add(comment);
			}
		}

		return comment;
	}

	/**
	 * 是否在list form 中
	 */
	public boolean isInListForm() {
		return this.propSet.contains(FormPropEnum.LF.name());
	}

	/**
	 * 是否需要在edit form中
	 * 
	 * @return
	 */
	public boolean isInEditForm() {
		return this.propSet.contains(FormPropEnum.EF.name());
	}

	/**
	 * 是否是上传文件的类型
	 */
	public boolean isUploadFile() {
		return this.propSet.contains(FormPropEnum.FILE.name());
	}

}
