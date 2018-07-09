package com.cfido.commons.spring.apiServer.core;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import com.cfido.commons.annotation.api.AClass;
import com.cfido.commons.annotation.api.AMock;
import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.utils.utils.MethodUtil;
import com.cfido.commons.utils.utils.MethodUtil.MethodInfoOfSetter;
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

	/**
	 * 获取所有的setter
	 * 
	 * @param formClass
	 * @return
	 */
	public static List<MethodParamVo> getSetters(Class<?> formClass) {
		List<MethodParamVo> list = new LinkedList<>();

		List<MethodInfoOfSetter> setters = MethodUtil.findSetter(formClass);
		for (MethodInfoOfSetter info : setters) {

			Class<?> paramClass = info.getParamClass();
			String propName = info.getPropName();// 参数名

			MethodParamVo vo = new MethodParamVo(propName);

			// 设置是否不允许空
			vo.setNotNull(info.isAnnotationPresent(NotNull.class, false)
					|| info.isAnnotationPresent(NotBlank.class, false));

			if (paramClass.isArray()) {
				// 如果是数组，就用数组的成员类作为参数类
				paramClass = paramClass.getComponentType();
				vo.setArray(true); // 设置参数是否是数组
			}
			vo.setClassName(paramClass.getSimpleName());// 设置类名

			// 设置备注
			AComment comment = info.getAnnotation(AComment.class, false);
			if (comment != null) {
				// 如果setter上有备注，就用这个备注
				vo.setMemo(comment.value());
			}

			if (OpenTypeUtil.isOpenType(paramClass)) {
				// 如果是普通参数

				// 设置默认值
				AMock mock = info.getAnnotation(AMock.class, false);
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

		return list;
	}

}
