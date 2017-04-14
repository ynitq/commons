package com.cfido.commons.utils.logicObj;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * <pre>
 * 各类info bean的基类，主要功能就是自动获取createBy， createDate等数据
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月29日
 */
public class BaseViewModel<T extends BasePoObj<PO>, PO> {

	protected T obj;
	protected PO po;

	public BaseViewModel() {
		// 如果发现直接调用无参数的构造函数，就需要警告一下
	}

	public BaseViewModel(T obj) {
		Assert.notNull(obj,"逻辑对象不能为空");
		Assert.notNull(obj.getPo(),"逻辑对象的po不能为空");
		this.init(obj);
	}

	public void init(T obj) {
		this.obj = obj;
		this.po = obj.getPo();

		@SuppressWarnings("unchecked")
		Class<PO> poClass = (Class<PO>) obj.getPo().getClass();

		try {
			this.po = poClass.newInstance();
			BeanUtils.copyProperties(obj.getPo(), this.po);
			// 必须拷贝一份po, 业务model层为了数据安全，就修改一些值，防止数据泄露
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public PO getPo() {
		return po;
	}

}
