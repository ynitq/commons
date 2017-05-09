package com.cfido.commons.utils.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * <pre>
 * 用于将字符串转换成为对应的类型
 * </pre>
 * 
 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a> 2015年10月15日
 */
public class OpenTypeUtil {

	private abstract class BaseConverter<T> {
		abstract T toValue(String text, String defaultValue);

		String getdefaultValue() {
			return "";
		}

		String toStringValue(Object t) {
			return t.toString();
		}
	}

	private class ToBigDecimal extends BaseConverter<BigDecimal> {

		@Override
		public BigDecimal toValue(String text, String defaultValue) {
			String temp = text == null ? defaultValue : text;
			if (StringUtils.hasText(temp)) {
				try {
					return new BigDecimal(temp);
				} catch (NumberFormatException e) {
					return new BigDecimal(0);
				}
			} else {
				return null;
			}
		}

		@Override
		String getdefaultValue() {
			return "0";
		}
	}

	private class ToBigInteger extends BaseConverter<BigInteger> {

		@Override
		public BigInteger toValue(String text, String defaultValue) {
			String temp = text == null ? defaultValue : text;
			if (StringUtils.hasText(temp)) {
				try {
					return new BigInteger(temp);
				} catch (NumberFormatException e) {
					return new BigInteger("0");
				}
			} else {
				return null;
			}
		}

		@Override
		String getdefaultValue() {
			return "0";
		}
	}

	private class ToBoolean extends BaseConverter<Boolean> {

		@Override
		public Boolean toValue(String text, String defaultValue) {
			if ("null".equalsIgnoreCase(text)) {
				return null;
			}

			if ("false".equalsIgnoreCase(text)) {
				return false;
			}

			return true;
		}

		@Override
		String getdefaultValue() {
			return "false";
		}
	}

	private class ToByte extends BaseConverter<Byte> {

		private final boolean nullable;

		public ToByte(boolean nullable) {
			super();
			this.nullable = nullable;
		}

		@Override
		public Byte toValue(String text, String defaultValue) {
			try {
				return Byte.parseByte(text, 16);
			} catch (Exception e) {
				if (this.nullable) {
					return null;
				} else {
					return 0;
				}
			}
		}

		@Override
		String getdefaultValue() {
			return "0";
		}
	}

	private class ToChar extends BaseConverter<Character> {

		private final boolean nullable;

		public ToChar(boolean nullable) {
			super();
			this.nullable = nullable;
		}

		@Override
		public Character toValue(String text, String defaultValue) {
			if (StringUtils.hasText(text)) {
				return text.charAt(0);
			} else {
				if (this.nullable) {
					return null;
				} else {
					return '\0';
				}
			}
		}
	}

	private class ToDate extends BaseConverter<Date> {

		@Override
		public Date toValue(String text, String defaultValue) {
			// 日期的处理方式
			Date date;
			try {
				date = dateFormat.parse(text == null ? defaultValue : text);
			} catch (Exception e) {
				date = new Date();
			}
			return date;
		}

		@Override
		String getdefaultValue() {
			return dateFormat.format(new Date());
		}

		@Override
		String toStringValue(Object t) {
			if (t == null) {
				return "";
			} else {
				return dateFormat.format(t);
			}
		}
	}

	private class ToDouble extends BaseConverter<Double> {

		private final boolean nullable;

		public ToDouble(boolean nullable) {
			super();
			this.nullable = nullable;
		}

		@Override
		public Double toValue(String text, String defaultValue) {
			try {
				return Double.parseDouble(text);
			} catch (Exception e) {
				if (this.nullable) {
					try {
						// 默认值也可能是解析不成功的
						return Double.parseDouble(defaultValue);
					} catch (Exception e1) {
						return null;
					}
				} else {
					return 0.0d;
				}
			}
		}

		@Override
		String getdefaultValue() {
			return "0";
		}
	}

	private class ToFloat extends BaseConverter<Float> {

		private final boolean nullable;

		public ToFloat(boolean nullable) {
			super();
			this.nullable = nullable;
		}

		@Override
		public Float toValue(String text, String defaultValue) {
			try {
				return Float.parseFloat(text);
			} catch (Exception e) {
				if (this.nullable) {

					try {
						// 默认值也可能是解析不成功的
						return Float.parseFloat(defaultValue);
					} catch (Exception e1) {
						return null;
					}

				} else {
					return 0f;
				}
			}
		}

		@Override
		String getdefaultValue() {
			return "0";
		}
	}

	private class ToInt extends BaseConverter<Integer> {

		private final boolean nullable;

		public ToInt(boolean nullable) {
			super();
			this.nullable = nullable;
		}

		@Override
		public Integer toValue(String text, String defaultValue) {
			try {
				return Integer.parseInt(text);
			} catch (Exception e) {
				if (this.nullable) {

					try {
						// 默认值也可能是解析不成功的
						return Integer.parseInt(defaultValue);
					} catch (Exception e1) {
						return null;
					}

				} else {
					return 0;
				}
			}
		}

		@Override
		String getdefaultValue() {
			return "0";
		}
	}

	private class ToLong extends BaseConverter<Long> {

		private final boolean nullable;

		public ToLong(boolean nullable) {
			super();
			this.nullable = nullable;
		}

		@Override
		public Long toValue(String text, String defaultValue) {
			try {
				return Long.parseLong(text);
			} catch (Exception e) {
				if (this.nullable) {

					try {
						// 默认值也可能是解析不成果的
						return Long.parseLong(defaultValue);
					} catch (Exception e1) {
						return null;
					}

				} else {
					return 0L;
				}
			}
		}

		@Override
		String getdefaultValue() {
			return "0";
		}
	}

	private class ToString extends BaseConverter<String> {

		@Override
		public String toValue(String text, String defaultValue) {
			return (text == null) ? defaultValue : text;
		}
	}

	private static OpenTypeUtil instance = new OpenTypeUtil();

	/**
	 * 判断这个class的值是否能通过String转过来
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isOpenType(Class<?> clazz) {
		return instance.isValidType(clazz);
	}

	/**
	 * 判断这个class的值是否能通过String转过来
	 * 
	 * @param className
	 * @return
	 */
	public static boolean isOpenType(String className) {
		return instance.converterMap.containsKey(className);
	}

	/**
	 * 将字符串解析为对象
	 * 
	 * @param text
	 * @param paramClass
	 * @param defaultValue
	 * @return
	 */
	public static <T> T parserFromString(String text, Class<T> paramClass, String defaultValue) {
		return instance.getValueFormString(text, paramClass, defaultValue);
	}

	public static Object parserFromString(String text, String classStr, String defaultValue) {
		return instance.getValueFormString(text, classStr, defaultValue);
	}

	public static String toString(Object value, String classStr) {
		BaseConverter<?> toValue = instance.converterMap.get(classStr);
		if (toValue != null) {
			return toValue.toStringValue(value);
		}
		return "";
	}

	/**
	 * 根据class名，获取这个class的默认值
	 * 
	 * @param classNameStr
	 * @return
	 */
	public static String getDefaultValue(String classNameStr) {
		BaseConverter<?> toValue = instance.converterMap.get(classNameStr);
		if (toValue != null) {
			return toValue.getdefaultValue();
		}
		return "";
	}

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:ss");

	private final Map<String, BaseConverter<?>> converterMap = new HashMap<>();

	private OpenTypeUtil() {
		this.addConverter(Date.class, new ToDate());
		this.addConverter(BigDecimal.class, new ToBigDecimal());
		this.addConverter(BigInteger.class, new ToBigInteger());

		this.addConverter(Byte.class, new ToByte(true));
		this.addConverter(byte.class, new ToByte(false));

		this.addConverter(Integer.class, new ToInt(true));
		this.addConverter(int.class, new ToInt(false));

		this.addConverter(Long.class, new ToLong(true));
		this.addConverter(long.class, new ToLong(false));

		this.addConverter(float.class, new ToFloat(false));
		this.addConverter(Float.class, new ToFloat(true));

		this.addConverter(double.class, new ToDouble(false));
		this.addConverter(Double.class, new ToDouble(true));

		this.addConverter(String.class, new ToString());

		this.addConverter(boolean.class, new ToBoolean());
		this.addConverter(Boolean.class, new ToBoolean());

		this.addConverter(Character.class, new ToChar(true));
		this.addConverter(char.class, new ToChar(false));

	}

	public static SimpleDateFormat getDateFormat() {
		return instance.dateFormat;
	}

	/**
	 * 设置Date和字符串互转的时候，日期的格式
	 * 
	 * @param dateFormat
	 */
	public static void setDateFormat(SimpleDateFormat dateFormat) {
		instance.dateFormat = dateFormat;
	}

	private void addConverter(Class<?> clazz, BaseConverter<?> converter) {
		this.converterMap.put(clazz.getName(), converter);
	}

	@SuppressWarnings("unchecked")
	private <T> T getValueFormString(String text, Class<T> paramClass, String defaultValue) {
		BaseConverter<?> toValue = this.converterMap.get(paramClass.getName());
		if (toValue != null) {
			return (T) toValue.toValue(text, defaultValue);
		}
		return null;
	}

	private Object getValueFormString(String text, String classNameStr, String defaultValue) {
		BaseConverter<?> toValue = this.converterMap.get(classNameStr);
		if (toValue != null) {
			return toValue.toValue(text, defaultValue);
		}
		return null;
	}

	private boolean isValidType(Class<?> clazz) {
		if (clazz.isArray()) {
			return this.converterMap.containsKey(clazz.getComponentType().getName());
		} else {
			return this.converterMap.containsKey(clazz.getName());
		}
	}
}
