package com.cfido.commons.utils.logicObj;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 基础的逻辑层。必须是非单例的对象，所以在子类中必须有 Scope的注解，声明不是单例
 * 
 * 注意，Scope的注解只能放在子类中，不能放在基类，否则不生效
 * </pre>
 *
 * @see Component 子类需要有Component的注解
 * @see Scope 子类需要有Scope的注解
 * @see ConfigurableBeanFactory#SCOPE_PROTOTYPE
 *      子类Scope的注解,必须声明这个bean被创建时，是SCOPE_PROTOTYPE的
 * 
 * @author 梁韦江
 *  2016年6月17日
 */
// @Component
// @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BasePoObj<T> {

	protected T po;

	public BasePoObj() {
		super();

		Scope scope = this.getClass().getAnnotation(Scope.class);
		Assert.notNull(scope, "逻辑对象必须有 Scope 注解: " + this.getClass().getName());

		Assert.isTrue(ConfigurableBeanFactory.SCOPE_PROTOTYPE.equals(scope.scopeName()),
				"Scope 的类型必须是 prototype: " + this.getClass().getName());
	}

	/**
	 * 在set了po以后调用，方便子类在初始化数据
	 */
	protected void afterSetPo() {

	}

	public void setPo(T po) {
		this.po = po;
		this.afterSetPo();
	}

	public T getPo() {
		return po;
	}

	/**
	 * 该方法在update前执行，子类可覆盖该方法，进行一些额外的逻辑判断
	 * 
	 * @param obj
	 *            要update的对象
	 * @throws BaseApiException
	 *             逻辑错误
	 */
	protected void checkBeforeUpdate() throws BaseApiException {

	}

	/**
	 * 该方法在delete前执行，子类可覆盖该方法，进行一些额外的逻辑判断
	 * 
	 * @param obj
	 *            要删除的逻辑对象
	 * @throws BaseApiException
	 *             逻辑错误
	 */
	protected void checkBeforeDelete() throws BaseApiException {

	}

}
