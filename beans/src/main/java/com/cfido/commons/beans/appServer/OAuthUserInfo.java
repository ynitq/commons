package com.cfido.commons.beans.appServer;

/**
 * 用户中心通过oauth传过来的用户信息。
 * 
 * 实际是有shh根据TOwner对象生成的，会用在很多地方
 * 
 */
public class OAuthUserInfo {

	/** 用户ID */
	private Integer id;

	/** 创建时间 */
	private long createTime;

	/** 注册信息中填写的电话，不一定就是账号 */
	private String phone;

	/** cityName */
	private String cityName;

	/** realname */
	private String realname;

	/** 登陆用的用户账号，我们目前就是手机号了 */
	private String username;

	/** address */
	private String address;

	/** 联系人电话 */
	private String contactsPhone;

	/** 法人姓名 */
	private String legalName;

	/** 用户类型 **/
	private Integer ownerType;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactsPhone() {
		return contactsPhone;
	}

	public void setContactsPhone(String contactsPhone) {
		this.contactsPhone = contactsPhone;
	}

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public Integer getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(Integer ownerType) {
		this.ownerType = ownerType;
	}

}
