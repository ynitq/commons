package com.cfido.commons.utils.web;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.cfido.commons.annotation.form.AFormNotHtml;
import com.cfido.commons.annotation.form.AFormValidateMethod;
import com.cfido.commons.utils.utils.LogUtil;
import com.cfido.commons.utils.utils.MethodUtil;
import com.cfido.commons.utils.utils.MethodUtil.MethodInfoOfSetter;
import com.cfido.commons.utils.utils.StringUtilsEx;

public class BinderEditorSupport {

	static public interface IValue<T> {
		T toValue(MethodInfoOfSetter m, String text);
	}

	private class ToBoolean implements IValue<Boolean> {
		private final boolean allowNull;

		public ToBoolean(boolean allowNull) {
			this.allowNull = allowNull;
		}

		@Override
		public Boolean toValue(MethodInfoOfSetter m, String text) {
			if (this.allowNull) {
				// 如果允许空，就有3种状态，需要根据传入的文字来判断
				if ("true".equalsIgnoreCase(text) || "yes".equalsIgnoreCase(text)) {
					return true;
				} else if ("false".equalsIgnoreCase(text) || "no".equalsIgnoreCase(text)) {
					return false;
				} else {
					return null;
				}
			} else {
				// 如果不允许空，只有两种状态
				return !"false".equalsIgnoreCase(text);
			}
		}
	}

	private class ToDate implements IValue<Date> {

		@Override
		public Date toValue(MethodInfoOfSetter m, String text) {
			// 日期的处理方式
			if (StringUtils.hasText(text)) {
				try {
					if (text.length() == BinderUtil.DATE_FORMAT.length()) {
						// 如果是yyyy-MM-dd格式
						SimpleDateFormat sdf = new SimpleDateFormat(BinderUtil.DATE_FORMAT);
						Date date = sdf.parse(text);
						return date;
					} else {
						// 如果不是默认格式，就当是 yyyy-MM-dd HH:mm 格式, 长度不对就只能是天灾人祸了
						SimpleDateFormat sdf = new SimpleDateFormat(BinderUtil.DATE_FORMAT_HAS_TIME);
						Date date = sdf.parse(text);
						return date;
					}
				} catch (Exception e) {
					// 如果解析出错，就返回null
				}
			}
			return null;
		}
	}

	private class ToDouble implements IValue<Double> {
		private final boolean allowNull;

		public ToDouble(boolean allowNull) {
			this.allowNull = allowNull;
		}

		@Override
		public Double toValue(MethodInfoOfSetter m, String text) {
			try {
				return Double.parseDouble(text);
			} catch (Exception e) {
				if (!this.allowNull) {
					return 0.0d;
				} else {
					return null;
				}
			}
		}
	}

	private class ToFloat implements IValue<Float> {
		private final boolean allowNull;

		public ToFloat(boolean allowNull) {
			this.allowNull = allowNull;
		}

		@Override
		public Float toValue(MethodInfoOfSetter m, String text) {
			try {
				return Float.parseFloat(text);
			} catch (Exception e) {
				if (!this.allowNull) {
					return 0f;
				} else {
					return null;
				}
			}
		}
	}

	private class ToInt implements IValue<Integer> {

		private final boolean allowNull;

		public ToInt(boolean allowNull) {
			this.allowNull = allowNull;
		}

		@Override
		public Integer toValue(MethodInfoOfSetter m, String text) {
			try {
				return Integer.parseInt(text);
			} catch (Exception e) {
				if (this.allowNull) {
					return null;
				} else {
					return 0;
				}
			}
		}
	}

	private class ToLong implements IValue<Long> {
		private final boolean allowNull;

		public ToLong(boolean allowNull) {
			this.allowNull = allowNull;
		}

		@Override
		public Long toValue(MethodInfoOfSetter m, String text) {
			try {
				return Long.parseLong(text);
			} catch (Exception e) {
				if (!this.allowNull) {
					return 0L;
				} else {
					return null;
				}
			}
		}
	}

	private class ToString implements IValue<String> {

		@Override
		public String toValue(MethodInfoOfSetter m, String text) {
			if (text != null) {

				// 检查是否不允许html
				AFormNotHtml anno = m.getAnnotation(AFormNotHtml.class, true);
				if (anno != null) {
					// 如果不允许html，就删除html标签
					return StringUtilsEx.delHTMLTag(text);
				} else {
					return text.trim();
				}
			} else {
				return null;
			}
		}
	}

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BinderEditorSupport.class);

	private static BinderEditorSupport instance = new BinderEditorSupport();

	public static void addEditorSupport(Class<?> clazz, IValue<?> support) {
		instance.toValueMap.put(clazz, support);
	}

	public static void updateObj(HttpServletRequest request, Map<String, String[]> dateFromRequest, Object form,
			boolean validateForm) {

		// 表单绑定只管setter
		List<MethodInfoOfSetter> setters = MethodUtil.findSetter(form.getClass());

		for (MethodInfoOfSetter methodInfo : setters) {

			String propName = methodInfo.getPropName();
			Class<?> paramClass = methodInfo.getParamClass();

			if (instance.isValidType(paramClass)) {
				// 如果是普通类型
				String[] values = dateFromRequest.get(propName);
				if (values == null) {
					// ajax提交数组时，会自动在名字后面加上 “[]”
					values = dateFromRequest.get(propName + "[]");
				}

				if (values != null && values.length > 0) {

					if (log.isDebugEnabled()) {
						StringBuffer vb = new StringBuffer();
						for (String str : values) {
							vb.append(str).append(",");
						}
						log.debug("需要设置的属性:{}, 类型:{}, 值:{}",
								propName, paramClass.getSimpleName(),
								vb.length() > 40 ? vb.substring(0, 40) : vb.toString());
					}

					Object propValue;

					if (paramClass.isArray()) {
						// 如果要设置的是数组
						propValue = Array.newInstance(paramClass.getComponentType(), values.length);
						for (int i = 0; i < values.length; i++) {
							Object arg = instance.getValueFormString(methodInfo, values[i], paramClass.getComponentType());
							Array.set(propValue, i, arg);
						}
					} else {
						propValue = instance.getValueFormString(methodInfo, values[0], paramClass);
					}
					try {
						methodInfo.getOriginMethod().invoke(form, propValue);
					} catch (Exception e) {
						LogUtil.traceError(log, e);
					}
				}
			} else if (request != null && MultipartFile.class.isAssignableFrom(paramClass)) {
				// 如果是文件类型
				log.debug("要绑定的属性{} 是文件类型", propName);
				// TODO 上传文件也可能是数组，这里还没有处理
				if (request instanceof StandardMultipartHttpServletRequest) {
					StandardMultipartHttpServletRequest mreq = (StandardMultipartHttpServletRequest) request;
					MultipartFile file = mreq.getFileMap().get(propName);
					try {
						methodInfo.getOriginMethod().invoke(form, file);
					} catch (Exception e) {
						LogUtil.traceError(log, e);
					}
				} else {
					log.warn("{}中 {}字段是需要上传文件，但form的类型不对，请检查form中是否有enctype='multipart/form-data'属性",
							form.getClass().getName(), propName);
				}
			}
		}

		if (validateForm) {
			// 如果要校验表单，就搜索用于校验的方法
			Method validate = null;
			for (Method m : form.getClass().getMethods()) {
				Class<?>[] paramType = m.getParameterTypes();
				if (m.isAnnotationPresent(AFormValidateMethod.class) && (paramType == null || paramType.length == 0)) {
					// 校验的方法必须是无参数的，否则没法执行
					validate = m;
					break;
				}
			}

			if (validate != null) {
				// 执行校验方法
				try {
					validate.invoke(form);
				} catch (Throwable e) {
					// 如果校验的方法出错了，我们一点办法都没有
					LogUtil.traceError(log, e);
				}
			}
		}
	}

	private final Map<Class<?>, IValue<?>> toValueMap = new HashMap<>();

	private BinderEditorSupport() {
		this.toValueMap.put(Date.class, new ToDate());

		this.toValueMap.put(int.class, new ToInt(false));
		this.toValueMap.put(Integer.class, new ToInt(true));

		this.toValueMap.put(Long.class, new ToLong(true));
		this.toValueMap.put(long.class, new ToLong(false));

		this.toValueMap.put(float.class, new ToFloat(false));
		this.toValueMap.put(Float.class, new ToFloat(true));

		this.toValueMap.put(double.class, new ToDouble(false));
		this.toValueMap.put(Double.class, new ToDouble(true));

		this.toValueMap.put(String.class, new ToString());

		this.toValueMap.put(boolean.class, new ToBoolean(false));
		this.toValueMap.put(Boolean.class, new ToBoolean(true));
	}

	private Object getValueFormString(MethodInfoOfSetter m, String text, Class<?> paramClass) {
		IValue<?> toValue = this.toValueMap.get(paramClass);
		if (toValue != null) {
			return toValue.toValue(m, text);
		}
		return null;
	}

	boolean isValidType(Class<?> clazz) {
		if (clazz.isArray()) {
			return this.toValueMap.containsKey(clazz.getComponentType());
		} else {
			return this.toValueMap.containsKey(clazz);
		}
	}
}
