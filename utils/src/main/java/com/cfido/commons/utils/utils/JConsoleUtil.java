package com.cfido.commons.utils.utils;

import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

public class JConsoleUtil {

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(JConsoleUtil.class);

	private static final String JCONSOLE_URL = "service:jmx:rmi://localhost:%d/jndi/rmi://localhost:%d/jmxrmi";

	/**
	 * 注册用jsconsole监控的url
	 * 
	 * @param jconsoleFirstPort
	 * @param jconsoleSecondPort
	 * @throws Exception
	 */
	public static void regJConsoleUrl(int jconsoleFirstPort, int jconsoleSecondPort) throws Exception {

		String jconsole_url = String.format(JCONSOLE_URL, jconsoleFirstPort, jconsoleSecondPort);

		log.fatal("jconsole url:" + String.format(jconsole_url));
		LocateRegistry.createRegistry(jconsoleSecondPort);
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		JMXServiceURL url = new JMXServiceURL(jconsole_url);
		JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
		cs.start();

	}

}
