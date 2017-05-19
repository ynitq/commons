package com.cfido.commons.codeGen.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cfido.commons.codeGen.CodeGenProperties;
import com.cfido.commons.codeGen.beans.EmbeddedIdBean;
import com.cfido.commons.codeGen.beans.TableBean;
import com.cfido.commons.codeGen.core.MetadataReader.TableInfo;
import com.cfido.commons.utils.utils.MBeanUtils.IgnoreWhenObjectToMap;

/**
 * <pre>
 * 全局的VO，包含了所有的内容
 * </pre>
 * 
 * @author 梁韦江 2016年9月11日
 */
@Component
public class CodeGenContext {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CodeGenContext.class);

	private final List<TableBean> tables = new LinkedList<>();
	private final List<EmbeddedIdBean> entityIds = new LinkedList<>();

	private final Map<String, TableBean> tableMapForName = new HashMap<>();

	@Autowired
	private CodeGenProperties codeGenProperties;

	/** 存储了 entity 额外的接口 */
	private Set<Class<?>> implementClassSet;

	public Set<Class<?>> getEntityImplementClassSet() {
		if (implementClassSet == null) {
			List<String> classNameList = this.codeGenProperties.getEntity().getImplement();
			this.implementClassSet = this.getImplementClassSet(classNameList);
		}
		return this.implementClassSet;
	}

	/**
	 * 根据类名，获得实际的Class，作为 implements
	 * 
	 * @param classNameList
	 * @return
	 */
	private Set<Class<?>> getImplementClassSet(List<String> classNameList) {
		Set<Class<?>> set = new HashSet<>();
		if (classNameList != null && !classNameList.isEmpty()) {
			for (String className : classNameList) {
				try {
					Class<?> clazz = Class.forName(className);
					// 必须能找到类
					if (clazz.isInterface()) {
						// 必须是接口
						set.add(clazz);
					} else {
						String msg = String.format("Entity接口配置中定义的接口 %s 不是接口", className);
						throw new RuntimeException(msg);
					}
				} catch (ClassNotFoundException e) {
					String msg = String.format("Entity接口配置中定义的接口 %s 不存在", className);
					throw new RuntimeException(msg);
				}
			}
		}
		return set;
	}

	public void addEmbeddedId(EmbeddedIdBean vo) {
		this.entityIds.add(vo);
	}

	/**
	 * 增加table的信息
	 * 
	 * @param tableInfo
	 */
	public void addTable(TableInfo tableInfo) {
		TableBean table = new TableBean(this, tableInfo);
		this.tables.add(table);
		this.tableMapForName.put(table.getName(), table);

		log.debug("获取表{}的数据\t {}字段\t{}外键\t{}主键字段",
				tableInfo.getName(), tableInfo.getColumnInfoList().size(),
				tableInfo.getFkInfoList().size(), tableInfo.getPrimaryKeys().size());

	}

	public TableBean getTableByName(String tableName) {
		return this.tableMapForName.get(tableName);
	}

	/**
	 * 在原始数据读取完成后，再次初始化所有的VO
	 */
	public void afterMetadataReaded() {
		log.debug("在原始数据设置完成后，初始化所有的VO, 合计 {} 张表", this.tables.size());

		if (!this.entityIds.isEmpty()) {
			// 目前不支持复合外键
			throw new RuntimeException("本代码生成工具不是不能生成复合外键，但我们并不乐意支持");
		}

		// 每一个表再次初始化一次
		for (TableBean table : this.tables) {
			table.afterMetadataReaded();
		}
	}

	public String getEntityPackage() {
		return this.codeGenProperties.getEntityPackage();
	}

	@IgnoreWhenObjectToMap("返回 CodeGenProperties")
	public CodeGenProperties.Entity getEntityProperties() {
		return codeGenProperties.getEntity();
	}

	@IgnoreWhenObjectToMap("List(TableBean) 返回所有的表的内容")
	public List<TableBean> getTables() {
		return tables;
	}

	public void resetTables() {
		this.tables.clear();
	}

}
