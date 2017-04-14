package com.cfido.commons.beans.sms;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cfido.commons.annotation.api.AForm;

/**
 * <pre>
 * 发送短信的表单
 * </pre>
 * 
 * @author 梁韦江
 *  2016年7月20日
 */
@AForm
public class SendSmsForm {
	@Size(min = 1, message = "至少要有一个手机号")
	private List<String> phones = new LinkedList<>();

	@NotNull(message = "要发送的文本不能为空")
	@Size(min = 1, message = "至少发送一个字符")
	private String text;

	/**
	 * 可选参数，扩展码，用户定义扩展码，3位
	 */
	private String extno;

	public List<String> getPhones() {
		return phones;
	}

	public String getText() {
		return text;
	}

	public void setPhones(List<String> phones) {
		this.phones = phones;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getExtno() {
		return extno;
	}

	public void setExtno(String extno) {
		this.extno = extno;
	}

	public void addPhone(String phone) {
		this.phones.add(phone);
	}

}
