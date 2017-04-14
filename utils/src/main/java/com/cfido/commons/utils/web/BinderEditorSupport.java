package com.cfido.commons.utils.web;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.cfido.commons.annotation.form.AFormValidateMethod;
import com.cfido.commons.utils.utils.DateUtil;
import com.cfido.commons.utils.utils.LogUtil;
import com.cfido.commons.utils.utils.StringUtils;

public class BinderEditorSupport {

	static public interface IValue<T> {
		T toValue(String propName, String text);
	}

	private class ToBoolean implements IValue<Boolean> {
		private final boolean allowNull;

		public ToBoolean(boolean allowNull) {
			this.allowNull = allowNull;
		}

		@Override
		public Boolean toValue(String propName, String text) {
			if (this.allowNull) {
				// 如果允许空，就有3种状态，需要根据传入的文字来判断
				if ("true".equalsIgnoreCase(text) || "yes".equals(text)) {
					return true;
				} else if ("false".equals(text) || "no".equals(text)) {
					return false;
				} else {
					return null;
				}
			} else {
				// 如果不允许空，就文字就表示为真
				return true;
			}
		}
	}

	private class ToDate implements IValue<Date> {

		private final ThreadLocal<SimpleDateFormat> savedDateFormat = new ThreadLocal<>();

		@Override
		public Date toValue(String propName, String text) {
			// 日期的处理方式
			boolean toBegin = !"endDate".equals(propName);
			try {
				Date date = this.getDefaultDateFormat().parse(text);
				return DateUtil.ceilDateToDay(date, toBegin);
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * 这个是一种线程安全的做法，可以避免多个线程同时要求paser日期时，可能发生的错误
		 */
		private SimpleDateFormat getDefaultDateFormat() {
			SimpleDateFormat sdf = this.savedDateFormat.get();
			if (sdf == null) {
				sdf = new SimpleDateFormat(BinderUtil.DATE_FORMAT);
				this.savedDateFormat.set(sdf);
			}
			return sdf;
		}
	}

	private class ToDouble implements IValue<Double> {
		private final boolean allowNull;

		public ToDouble(boolean allowNull) {
			this.allowNull = allowNull;
		}

		@Override
		public Double toValue(String propName, String text) {
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
		public Float toValue(String propName, String text) {
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
		public Integer toValue(String propName, String text) {
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
		public Long toValue(String propName, String text) {
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
		public String toValue(String propName, String text) {
			return text;
		}
	}

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BinderEditorSupport.class);

	private static BinderEditorSupport instance = new BinderEditorSupport();

	public static void addEditorSupport(Class<?> clazz, IValue<?> support) {
		instance.toValueMap.put(clazz, support);
	}

	public static void updateObj(HttpServletRequest request, Map<String, String[]> map, Object form, boolean validateForm) {
		Method validate = null;
		for (Method m : form.getClass().getMethods()) {

			String methodName = m.getName();
			if (methodName.startsWith("set") && methodName.length() > 3) {
				String propName = StringUtils.lowerFirstChar(methodName.substring(3));// 参数名

				// 必须是setXXX的方法
				Class<?>[] paramTypes = m.getParameterTypes();
				if (paramTypes.length == 1) {
					// 并且只有一个参数
					Class<?> paramClass = paramTypes[0];

					if (instance.isValidType(paramClass)) {
						// 如果是普通类型
						String[] values = map.get(propName);

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
									Object arg = instance.getValueFormString(propName, values[i], paramClass.getComponentType());
									Array.set(propValue, i, arg);
								}
							} else {
								propValue = instance.getValueFormString(propName, values[0], paramClass);
							}
							try {
								m.invoke(form, propValue);
							} catch (Exception e) {
								LogUtil.traceError(log, e);
							}
						}
					} else if (request != null && MultipartFile.class.isAssignableFrom(paramTypes[0])) {
						// 如果是文件类型
						log.debug("要绑定的属性{} 是文件类型", propName);
						// TODO 上传文件也可能是数组，这里还没有处理
						if (request instanceof StandardMultipartHttpServletRequest) {
							StandardMultipartHttpServletRequest mreq = (StandardMultipartHttpServletRequest) request;
							MultipartFile file = mreq.getFileMap().get(propName);
							try {
								m.invoke(form, file);
							} catch (Exception e) {
								LogUtil.traceError(log, e);
							}
						} else {
							log.warn("{}中 {}字段是需要上传文件，但form的类型不对，请检查form中是否有enctype='multipart/form-data'属性",
									form.getClass().getName(), propName);
						}
					}
				}
			} else if (m.getAnnotation(AFormValidateMethod.class) != null && m.getParameterTypes().length == 0) {
				// 如果该方法不是setter，并且被标记为 form校验的方法，则，在完成了所有的setter后，执行
				validate = m;
			}
		}

		if (validate != null && validateForm) {
			// 执行校验方法
			try {
				validate.invoke(form);
			} catch (Exception e) {
				LogUtil.traceError(log, e);
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

	private Object getValueFormString(String protName, String text, Class<?> paramClass) {
		IValue<?> toValue = this.toValueMap.get(paramClass);
		if (toValue != null) {
			return toValue.toValue(protName, text);
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
