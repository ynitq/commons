package com.cfido.commons.spring.utils;

import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.jmxInWeb.BaseMBeanDomainNaming;

/**
 * <pre>
 * 为本项目所有MBean 统一domain用的基类
 * </pre>
 * 
 * @author 梁韦江 2017年4月27日
 */
@ADomainOrder(CommonMBeanDomainNaming.ORDER)
public abstract class CommonMBeanDomainNaming extends BaseMBeanDomainNaming {

	public final static int ORDER = 20;

	public final static String DOMAIN = "Commons通用框架";

	@Override
	protected String getMBeanDomain() {
		return DOMAIN;
	}

}
