package com.cfido.commons.utils.utils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * <pre>
 * 网络工具，例如获得本机IP
 * </pre>
 * 
 * @author 梁韦江 2015年5月15日
 */
public class NetUtil {
	/**
	 * 地址转换为字符串
	 * 
	 * @param inetAddress
	 * @return
	 */
	private static String address2String(InetAddress inetAddress) {
		byte[] address = inetAddress.getAddress();
		StringBuffer res = new StringBuffer();
		for (int i = 0; i < address.length; i++) {
			if (i > 0)
				res.append(".");
			res.append(String.valueOf(address[i] & 0xff));
		}
		return res.toString();
	}

	/**
	 * 获得本地IP
	 * 
	 * @return
	 */
	public static String getLocalHostIP() {
		return getLocalHostIP(false);
	}

	/**
	 * 是否内网ip有限
	 * 
	 * @param intraIp
	 * @return
	 */
	public static String getLocalHostIP(boolean intraIp) {
		LinkedList<String> list = new LinkedList<String>();

		try {
			// 获取所有的网卡
			Enumeration<NetworkInterface> networkInterfacesEnum = NetworkInterface.getNetworkInterfaces();
			while (networkInterfacesEnum.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfacesEnum.nextElement();

				// 获取这块网卡上的所有ip
				Enumeration<InetAddress> inetAddressEnum = networkInterface.getInetAddresses();
				while (inetAddressEnum.hasMoreElements()) {
					InetAddress inetAddress = inetAddressEnum.nextElement();

					if (!(inetAddress instanceof Inet6Address) && !inetAddress.isLoopbackAddress()) {
						// 排除inet6的地址, 排除127.0.0.1的地址
						String addrStr = address2String(inetAddress);
						if (addrStr.startsWith("192.168") || addrStr.startsWith("10.") || addrStr.startsWith("172")) {
							if (intraIp) {
								// 如果是内网ip优先，找到后就直接返回了
								return addrStr;
							} else {
								// 内网ip放到队伍后面
								list.addLast(addrStr);
							}
						} else {
							// 外网ip防止前面
							list.addFirst(addrStr);
						}
					}
				}
			}

			if (list.isEmpty()) {
				return "127.0.0.1";
			} else {
				return list.getFirst();
			}
		} catch (SocketException e) {
			throw new RuntimeException("getLocalHost ip address error", e);
		}
	}

	public static List<String> getAllIpAddress() {
		LinkedList<String> list = new LinkedList<String>();

		try {
			// 获取所有的网卡
			Enumeration<NetworkInterface> networkInterfacesEnum = NetworkInterface.getNetworkInterfaces();
			while (networkInterfacesEnum.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfacesEnum.nextElement();

				// 获取这块网卡上的所有ip
				Enumeration<InetAddress> inetAddressEnum = networkInterface.getInetAddresses();
				while (inetAddressEnum.hasMoreElements()) {
					InetAddress inetAddress = inetAddressEnum.nextElement();

					if (!(inetAddress instanceof Inet6Address) && !inetAddress.isLoopbackAddress()) {
						// 排除inet6的地址, 排除127.0.0.1的地址
						String addrStr = address2String(inetAddress);
						list.add(addrStr);
					}
				}
			}

		} catch (SocketException e) {
			throw new RuntimeException("getLocalHost ip address error", e);
		}
		return list;

	}

	/**
	 * 是否在同一子网中
	 * 
	 * @param ip1
	 * @param ip2
	 * @return
	 */
	public static boolean isSameSubnet(String ip1, String ip2) {
		if (!isVaildInetAddress(ip1) || !isVaildInetAddress(ip2))
			return false;

		String s1 = ip1.substring(0, ip1.lastIndexOf("."));
		String s2 = ip2.substring(0, ip2.lastIndexOf("."));

		return s1.equals(s2);
	}

	/**
	 * 判断是否合法的网络IP地址
	 * 
	 * @param ipStr
	 * @return
	 */
	public static boolean isVaildInetAddress(String ipStr) {
		if (ipStr == null || ipStr.trim().length() == 0)
			return false;

		String[] na = ipStr.split("\\x2e");
		if (na.length != 4)
			return false;

		for (int i = 0; i < 4; i++) {
			int addr = 0;
			try {
				addr = Integer.parseInt(na[i]);
			} catch (Exception e) {
				return false;
			}

			if (addr < 0 || addr > 254)
				return false;

			if (i == 0 && addr == 0)
				return false;

			if (i == 3 && addr == 0)
				return false;

		}

		return true;
	}

}
