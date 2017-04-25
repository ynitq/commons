package com.cfido.commons.spring.jmxInWeb.inf.form;

import javax.management.MBeanParameterInfo;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cfido.commons.annotation.api.AForm;
import com.cfido.commons.spring.jmxInWeb.exception.MyInvalidParamTypeException;
import com.cfido.commons.spring.jmxInWeb.exception.MyMalformedObjectNameException;
import com.cfido.commons.utils.utils.OpenTypeUtil;

/**
 * <pre>
 * 执行mbean操作form
 * </pre>
 * 
 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a> 2015年10月15日
 */
@AForm
public class JwInvokeOptForm extends JwObjectNameForm {

	@NotNull(message = "optName不能为空")
	@Size(min = 1, message = "optName不能为空")
	private String optName;

	private String[] paramValue;
	private String[] paramType;

	public void verify() throws MyMalformedObjectNameException, MyInvalidParamTypeException {

		if (this.paramType != null) {
			for (String type : paramType) {
				if (!OpenTypeUtil.isOpenType(type)) {
					throw new MyInvalidParamTypeException(type);
				}
			}
		}
	}

	public String getOptName() {
		return optName;
	}

	public void setOptName(String optName) {
		this.optName = optName;
	}

	public String[] getParamValue() {
		return paramValue;
	}

	public void setParamValue(String[] paramValue) {
		this.paramValue = paramValue;
	}

	public String[] getParamType() {
		return paramType;
	}

	public void setParamType(String[] paramType) {
		this.paramType = paramType;
	}

	/**
	 * 调用的操作是否有参数
	 * 
	 * @return
	 * @throws MyInvalidParamTypeException
	 */
	public ParamInfo getParamInfo() throws MyInvalidParamTypeException {
		if (this.paramType != null && this.paramValue != null && this.paramType.length == this.paramValue.length) {
			return new ParamInfo(this.paramType, this.paramValue);
		} else {
			return new ParamInfo();
		}
	}

	public class ParamInfo {
		private final String[] types;
		private final Object[] values;

		private ParamInfo() {
			this.types = new String[0];
			this.values = new Object[0];
		}

		private ParamInfo(String[] types, String[] paramValue) throws MyInvalidParamTypeException {
			this.types = types;
			this.values = new Object[types.length];
			for (int i = 0; i < types.length; i++) {

				if (!OpenTypeUtil.isOpenType(types[i])) {
					throw new MyInvalidParamTypeException(types[i]);
				}

				values[i] = OpenTypeUtil.parserFromString(paramValue[i], types[i], "");
			}
		}

		public boolean isMath(MBeanParameterInfo[] params) {
			if (params.length != this.types.length) {
				return false;
			}

			for (int i = 0; i < params.length; i++) {
				if (!params[i].getType().equals(types[i])) {
					return false;
				}

				i++;
			}
			return true;
		}

		public String[] getTypes() {
			return types;
		}

		public Object[] getValues() {
			return values;
		}

		public String getOperationsInfo() {
			StringBuffer buff = new StringBuffer();

			boolean first = true;
			for (String type : types) {
				if (!first) {
					buff.append(", ");
				} else {
					first = false;
				}
				buff.append(type);
			}

			return String.format("%s(%s)", optName, buff.toString());
		}

	}

}
