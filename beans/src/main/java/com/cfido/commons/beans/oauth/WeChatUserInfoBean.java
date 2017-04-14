package com.cfido.commons.beans.oauth;

import java.util.List;

/**
 * <pre>
 * 微信返回的用户信息， 例子：
 * 
 * {
    "openid": "oLVPpjqs9BhvzwPj5A-vTYAX3GLc",
    "nickname": "刺猬宝宝",
    "sex": 1,
    "language": "简体中文",
    "city": "深圳",
    "province": "广东",
    "country": "中国",
    "headimgurl": "http://wx.qlogo.cn/mmopen/utpKYf69VAbCRDRlbUsPsdQN38DoibCkrU6SAMCSNx558eTaLVM8PyM6jlEGzOrH67hyZibIZPXu4BK1XNWzSXB3Cs4qpBBg18/0",
    "privilege": []
}
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月15日
 */
public class WeChatUserInfoBean {
	/** 用户标识 */
	private String openId;

	/** 用户昵称 */
	private String nickname;

	/** 性别（1是男性，2是女性，0是未知） */
	private int sex;

	/** 国家 */
	private String country;

	/** 省份 */
	private String province;

	/** 城市 */
	private String city;

	/** 用户头像链接 */
	private String headimgurl;

	/** 用户特权信息，json 数组，如微信沃卡用户为（chinaunicom） */
	private List<String> privilegeList;

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}


	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public List<String> getPrivilegeList() {
		return privilegeList;
	}

	public void setPrivilegeList(List<String> privilegeList) {
		this.privilegeList = privilegeList;
	}
}
