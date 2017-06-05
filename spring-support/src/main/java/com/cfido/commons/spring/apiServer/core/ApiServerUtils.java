package com.cfido.commons.spring.apiServer.core;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cfido.commons.annotation.api.AClass;
import com.cfido.commons.annotation.api.AMock;
import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.utils.utils.ClassUtil;
import com.cfido.commons.utils.utils.OpenTypeUtil;

/**
 * <pre>
 * 反射工具集，基本是api server专用的
 * </pre>
 * 
 * @author 梁韦江 2016年6月30日
 */
public class ApiServerUtils {

	/**
	 * 将实现类中所有的AClass的接口都找出来，一个实现类可能实现了多个接口
	 * 
	 * @param impl
	 * @return
	 */
	public static List<FindInferfaceResult> findInterface(Class<?> impl) {

		List<FindInferfaceResult> list = new LinkedList<>();

		Class<?>[] infClassAry = impl.getInterfaces();
		if (infClassAry != null) {
			for (Class<?> infClass : infClassAry) {
				// 循环检查所有的接口是否有该注解
				AClass aclass = infClass.getAnnotation(AClass.class);
				if (aclass != null) {
					list.add(new FindInferfaceResult(aclass, infClass));
				}
			}
		}
		return list;
	}

	/** 找出一个类中所有标准了不允许空的属性 */
	private static Set<String> findNotNull(Class<?> formClass) {

		Set<String> set = new HashSet<>();

		// NotNull和 NotBlank 都是不允许空的
		set.addAll(ClassUtil.getAllAnnoFromField(formClass, NotNull.class).keySet());
		set.addAll(ClassUtil.getAllAnnoFromField(formClass, NotBlank.class).keySet());

		return set;

	}

	/**
	 * 获取所有的setter
	 * 
	 * @param formClass
	 * @return
	 */
	public static List<MethodParamVo> getSetters(Class<?> formClass) {
		List<MethodParamVo> list = new LinkedList<>();

		// 先在属性上找到所有的注解
		Map<String, AComment> commentInFieldMap = ClassUtil.getAllAnnoFromField(formClass, AComment.class);
		Map<String, AMock> mockInFieldMap = ClassUtil.getAllAnnoFromField(formClass, AMock.class);

		// 找到所有不允许空的属性
		Set<String> notNullFieldSet = findNotNull(formClass);

		Method[] methods = formClass.getMethods();
		for (Method m : methods) {
			String methodName = m.getName();

			if (methodName.startsWith("set") && methodName.length() > 3) {
				// 必须是setXXX的方法
				Class<?>[] paramTypes = m.getParameterTypes();
				if (paramTypes.length == 1) {
					// 并且只有一个参数
					String propName = StringUtils.uncapitalize(methodName.substring(3));// 参数名

					MethodParamVo vo = new MethodParamVo(propName);
					vo.setNotNull(notNullFieldSet.contains(propName)); // 设置是否不允许空

					Class<?> paramClass = paramTypes[0];
					if (paramClass.isArray()) {
						// 如果是数组，就用数组的成员类作为参数类
						paramClass = paramClass.getComponentType();
						vo.setArray(true); // 设置参数是否是数组
					}
					vo.setClassName(paramClass.getSimpleName());// 设置类名

					// 设置备注
					AComment comment = m.getAnnotation(AComment.class);
					if (comment == null) {
						comment = commentInFieldMap.get(propName);
					}
					if (comment != null) {
						// 如果setter上有备注，就用这个备注
						vo.setMemo(comment.value());
					}

					if (OpenTypeUtil.isOpenType(paramClass)) {
						// 如果是普通参数

						// 设置默认值
						AMock mock = m.getAnnotation(AMock.class);
						if (mock == null) {
							mock = mockInFieldMap.get(propName);
						}
						if (mock != null) {
							vo.setValue(mock.value());
						} else {
							// 设置一个默认值
							vo.setValue(OpenTypeUtil.getDefaultValue(paramClass.getName()));
						}

						list.add(vo);
					} else if (MultipartFile.class.isAssignableFrom(paramClass)) {
						// 如果是要上传文件
						vo.setUploadFile(true);
						list.add(vo);
					}
				}
			}
		}

		return list;
	}

}
