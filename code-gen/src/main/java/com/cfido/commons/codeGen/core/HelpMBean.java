package com.cfido.commons.codeGen.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import com.cfido.commons.codeGen.CodeGenProperties;
import com.cfido.commons.codeGen.beans.TableBean;
import com.cfido.commons.utils.utils.DateUtil;
import com.cfido.commons.utils.utils.MBeanUtils;

/**
 * <pre>
 * 用于生成帮助信息
 * </pre>
 * 
 * @author 梁韦江 2016年10月12日
 */
@ManagedResource(description = "帮助文档", objectName = "CodeGen代码生成器: name=HelpMBean")
@Service
public class HelpMBean {

	/**
	 * <pre>
	 * 传给freemarker的数据的内容
	 * </pre>
	 * 
	 * @author 梁韦江 2016年10月12日
	 */
	public class FreeMarkerModelRoot {

		public String getContext() {
			return "CodeGenContext中的内容";
		}

		public CodeGenProperties getProp() {
			return HelpMBean.this.codeGenProperties;
		}

		public String getDate() {
			return DateUtil.dateFormat(new Date());
		}

		public String getTable() {
			return "如果是需要遍历所有表的模板，这里是一个表的TableBean";
		}
	}

	@Autowired
	private CodeGenContext context;

	@Autowired
	private CodeGenProperties codeGenProperties;

	@ManagedAttribute(description = "传给freemarker的数据的内容")
	public Map<String, ?> getFreemarkerModelRoot() throws Exception {
		Map<String, Object> map = new HashMap<>();

		map.put("context", this.getContextJsonMap());
		map.put("table", "如果是需要遍历所有表的模板，这里是一个表的TableBean");
		map.put("date", DateUtil.dateFormat(new Date()));
		map.put("prop", MBeanUtils.objectToMap(this.codeGenProperties));
		map.put("package", "如果生成的是java文件，这个是java类所在的包");

		return map;
	}

	@ManagedAttribute(description = "一个表的数据的例子")
	public Map<String, ?> getTableBeanSimple() throws Exception {
		List<TableBean> tables = this.context.getTables();
		if (!tables.isEmpty()) {
			return MBeanUtils.objectToMap(tables.get(0));
		}
		return null;
	}

	private Map<String, Object> getContextJsonMap() throws Exception {
		Map<String, Object> map = MBeanUtils.objectToMap(this.context);
		// map.put("tables", "List<TableBean> 所有的表内容");
		return map;
	}
}
