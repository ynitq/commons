package com.cfido.commons.utils.ipSeeker;

import org.junit.Assert;
import org.junit.Test;

import com.cfido.commons.utils.ipSeeker.geoip.GeoIPSeeker;
import com.cfido.commons.utils.ipSeeker.geoip.Location;
import com.cfido.commons.utils.ipSeeker.qqwry.IPLocation;
import com.cfido.commons.utils.ipSeeker.qqwry.IPSeeker;

/**
 * IP地址反查的测试
 * 
 * <pre>
 * test目录下的IP库文件很旧，真的要用的时候，需要更新
 * </pre>
 * 
 * @author liangwj
 * 
 */
public class IpSeekerTest {

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(IpSeekerTest.class);
	
	@Test
	public void testQQWry() {
		log.debug("测试用qq 地址库进行地址反查");
		// QQWry库是国内的，对国内地址的查询很准
		IPLocation lo = IPSeeker.getInstance().getIPLocation("113.133.42.167");
//		System.out.println(ToStringBuilder.reflectionToString(lo, ToStringStyle.MULTI_LINE_STYLE));
		Assert.assertTrue("113.133.42.167 属于陕西", "陕西".equals(lo.getProvince()));
	}

	@Test
	public void testGeoIp() {
		log.debug("测试用GeoIP 地址库进行地址反查");
		// Geo是国外的库，只有英文，而且对中国的不是很准
		Location lo = GeoIPSeeker.getInstance().getLocation("113.133.42.167");
//		System.out.println(ToStringBuilder.reflectionToString(lo, ToStringStyle.MULTI_LINE_STYLE));
		Assert.assertTrue("113.133.42.167 属于 China", "China".equals(lo.countryName));
	}
}