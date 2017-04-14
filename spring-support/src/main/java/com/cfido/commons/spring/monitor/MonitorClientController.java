/*
 * 文件名：[文件名]
 * 版权：〈版权〉
 * 描述：〈描述〉
 * 修改人：〈修改人〉
 * 修改时间：YYYY-MM-DD
 * 修改单号：〈修改单号〉
 * 修改内容：〈修改内容〉
 */
package com.cfido.commons.spring.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfido.commons.annotation.other.AMonitorIngore;
import com.cfido.commons.beans.monitor.ClientInfoResponse;

/**
 * 用于给监控服务器回调，获取当前系统的信息
 * 
 * @author wjc
 * @date 2016年8月25日
 *
 */
@Controller
@AMonitorIngore
public class MonitorClientController {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(MonitorClientController.class);

	@Autowired
	private MonitorClientContext context;

	@RequestMapping(MonitorUrls.CLIENT_CALLBACK)
	@ResponseBody
	public ClientInfoResponse callback() {
		log.debug("监控服务器检测");
		return this.context.getClientInfo(true);
	}
}
