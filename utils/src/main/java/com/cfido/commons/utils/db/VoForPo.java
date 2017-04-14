package com.cfido.commons.utils.db;

import com.cfido.commons.annotation.form.AVoForPoOrder;
import com.cfido.commons.utils.utils.ClassUtil;
import com.cfido.commons.utils.utils.LogUtil;
import com.cfido.commons.utils.utils.StringUtils;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * <pre>
 * 所有通过po延伸过来的vo的基类
 * </pre>
 * 
 * @author 梁韦江 2015年8月4日
 */
public class VoForPo<T> {
	private T po;

	public T getPo() {
		return po;
	}

	public void setPo(T po) {
		this.po = po;
	}
	
	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(VoForPo.class);
	
	/**
	 * 级联查询的表赋值到vo
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T getORM(Object obj){
		if(obj.getClass().getSimpleName().equals("Object[]")){
			try {
				Class<T> clazz = (Class<T>) ClassUtil.getGenericType(this.getClass(), 0);
				T tClass = clazz.newInstance();
				Object[] o = (Object[])obj;
//				BeanUtils.copyProperties(o[0], tClass);
				Method[] methods = clazz.getDeclaredMethods();
				int i = 0;
				for (Method m : methods) {
					if(m.getReturnType().getName().contains("com.linzi.shh.orm")
							||m.getReturnType().getName().contains(".entity")){
						String mName = m.getReturnType().getSimpleName();
						String fieldName = StringUtils.lowerFirstChar(m.getName().substring(3));
						int order = -1;
						String className = "";
						boolean hasant = clazz.getDeclaredField(fieldName).isAnnotationPresent(AVoForPoOrder.class);
						if(hasant){
							AVoForPoOrder vfp = clazz.getDeclaredField(fieldName).getAnnotation(AVoForPoOrder.class);
							order = vfp.order();
							className = vfp.className();
						}
						Method method = clazz.getDeclaredMethod(String.format("set%s", m.getName().substring(3)), m.getReturnType());
						for (Object ob : o) {
//							if(ob!=null && i!=0){
							if(ob != null){
								String sname = ob.getClass().getSimpleName();
								if(sname.substring(1).equals(mName.substring(1))){
									if(hasant){
										if(sname.equals(className) && order == i){
											method.invoke(tClass, ob);
											i++;
											break;
										}
									}else{
										method.invoke(tClass, ob);
										break;
									}
//									continue;
								}
							}
//							i++;
						}
					}
				}
				return tClass;
			} catch (Exception e) {
				LogUtil.traceError(log, e);
			} 
		}
		return null;
	}
	
	/**
	 * 级联查询的表赋值到voList
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> getORMList(Object obj){
		List<T> list = new LinkedList<T>();
		if(obj.getClass().getSimpleName().contains("List")){
			List<Object> o = (List<Object>)obj;
			if(o!=null&&o.size()>0){
				for (Object objects : o) {
					if(objects instanceof Object[]){
						list.add(getORM(objects));
					}else{
						try{
							Class<T> clazz = (Class<T>) ClassUtil.getGenericType(this.getClass(), 0);
							T tClass = clazz.newInstance();
							BeanUtils.copyProperties(objects, tClass);
							list.add(tClass);
						}catch(Exception e){
							LogUtil.traceError(log, e);
						}
					}
				}
				return list;
			}
		}
		return null;
	}
}
