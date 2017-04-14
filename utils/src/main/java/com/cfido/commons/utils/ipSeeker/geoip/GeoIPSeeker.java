package com.cfido.commons.utils.ipSeeker.geoip;

import java.io.IOException;

public class GeoIPSeeker {
	private static GeoIPSeeker instance;
	private static Object instanceLock = new Object();

	private LookupService lookupService;

	private GeoIPSeeker() {
	}

	private void init() {
		String fileName = "/" + getClass().getResource("/GeoLiteCity.dat").toExternalForm().substring(6);
		try {
			lookupService = new LookupService(fileName);
		} catch (IOException e) {
		}
	}

	public Location getLocation(String ip) {
		if (this.lookupService != null) {
			return this.lookupService.getLocation(ip);
		} else {
			return null;
		}
	}

	public String getIPLocation(String ip) {
		Location lo = this.getLocation(ip);
		if (lo != null) {
			return lo.countryName + "." + lo.city;
		} else {
			return "*";
		}
	}

	public static GeoIPSeeker getInstance() {
		if (instance == null) {
			synchronized (instanceLock) {
				if (instance == null) {
					instance = new GeoIPSeeker();
					instance.init();
				}
			}
		}

		return instance;
	}

}
