package com.cfido.commons.spring.jmxInWeb;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.springframework.jmx.export.naming.SelfNaming;

/**
 * <pre>
 * 可指定domain的Mbean的基类
 * </pre>
 * 
 * @author 梁韦江 2017年4月25日
 */
public abstract class BaseMBeanDomainNaming implements SelfNaming {

	/** 返回指定的doman名字 */
	protected abstract String getMBeanDomain();

	@Override
	public ObjectName getObjectName() throws MalformedObjectNameException {
		String name = String.format("%s:name=%s", this.getMBeanDomain(), this.getClass().getSimpleName());
		return new ObjectName(name);
	}

}
