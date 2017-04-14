package com.cfido.commons.utils.ipSeeker.qqwry;

/**
 * 用来封装ip相关信息，目前只有两个字段，ip所在的国家和地区
 */

public class IPLocation {
	private String country;
	private String area;
	private String province;

	public IPLocation() {
		country = area = "";
	}

	public String getArea() {
		return area;
	}

	public IPLocation getCopy() {
		IPLocation ret = new IPLocation();
		ret.country = country;
		ret.area = area;
		return ret;
	}

	public String getCountry() {
		return country;
	}

	public String getProvince() {
		return province;
	}

	@Override
	public String toString() {
		return String.format("%s\t%s\t%s", this.province, this.country, area);
	}

	protected void setArea(String area) {
		// 如果为局域网，纯真IP地址库的地区会显示CZ88.NET,这里把它去掉
		if (area.trim().equals("CZ88.NET")) {
			this.area = "本机或本网络";
		} else {
			this.area = area;
		}
	}
	
	protected void setCountry(String country) {
		if (country != null) {
			this.province = ChinaProvince.getProvinceName(country);
			this.country = country;
		}
	}
}  
