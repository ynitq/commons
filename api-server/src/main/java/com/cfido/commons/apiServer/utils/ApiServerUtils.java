package com.cfido.commons.apiServer.utils;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.cfido.commons.annotation.api.AClass;
import com.cfido.commons.annotation.api.AMock;
import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.apiServer.beans.FindInferfaceResult;
import com.cfido.commons.apiServer.vo.MethodParamVo;
import com.cfido.commons.utils.utils.OpenTypeUtil;
import com.cfido.commons.utils.utils.StringUtils;

/**
 * <pre>
 * 反射工具集，基本是api server专用的
 * </pre>
 * 
 * @author 梁韦江
 *  2016年6月30日
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
	 * 只管setter,必须有setter，才可能通过界面设置值
	 * 
	 * @param formClass
	 * @return
	 */
	public static List<MethodParamVo> getParamFromFormClass(Class<?> formClass) {
		List<MethodParamVo> list = new LinkedList<>();

		Method[] methods = formClass.getMethods();
		for (Method m : methods) {
			String methodName = m.getName();

			if (methodName.startsWith("set") && methodName.length() > 3) {
				// 必须是setXXX的方法
				Class<?>[] paramTypes = m.getParameterTypes();
				if (paramTypes.length == 1) {
					// 并且只有一个参数
					Class<?> paramClass = paramTypes[0];
					String propName = StringUtils.lowerFirstChar(methodName.substring(3));// 参数名

					MethodParamVo vo = new MethodParamVo();
					vo.setName(propName);
					vo.setClassName(paramClass.getSimpleName());

					AComment comment = m.getAnnotation(AComment.class);
					if (comment != null) {
						// 如果setter上有备注，就用这个备注
						vo.setMemo(comment.comment());
					}

					if (OpenTypeUtil.isOpenType(paramClass)) {
						// 如果是普通参数


						AMock mock = m.getAnnotation(AMock.class);
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
