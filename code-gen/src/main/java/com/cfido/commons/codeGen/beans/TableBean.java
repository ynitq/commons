package com.cfido.commons.codeGen.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.codeGen.core.CodeGenContext;
import com.cfido.commons.codeGen.core.ImportClassSet;
import com.cfido.commons.codeGen.core.MetadataReader.ColumnInfo;
import com.cfido.commons.codeGen.core.MetadataReader.ForeignKeyInfo;
import com.cfido.commons.codeGen.core.MetadataReader.TableInfo;
import com.cfido.commons.utils.utils.StringUtils;

/**
 * <pre>
 * 描述数据库中的一张表
 * </pre>
 * 
 * @author 梁韦江 2016年9月11日
 */
public class TableBean {

	private final TableInfo tableInfo;
	private final CodeGenContext context;

	private final List<ColumnBean> columns = new LinkedList<>();

	private final ImportClassSet importClassSet = new ImportClassSet();

	private final Map<String, ColumnBean> columnMapForName = new HashMap<>();

	private final String javaClassName;// java文件的类名

	private String implementsStr;

	private boolean hasEmbeddedId;

	/** 是否需要生成 ListForm */
	private boolean needListForm;
	/** 是否需要生成 EditForm */
	private boolean needEditForm;

	private IColumnBean id;

	/** 多对一的字段 */
	private final List<ColumnBean> manyToOne = new LinkedList<>();

	/** 一对多的其他表 */
	private final List<TableBean> oneToMany = new LinkedList<>();

	public TableBean(CodeGenContext context, TableInfo tableInfo) {
		super();
		this.context = context;
		this.tableInfo = tableInfo;

		Assert.hasText(this.tableInfo.getName(), "出现了空表名");

		String prefix = "表:" + this.tableInfo.getName();
		Assert.isTrue(!tableInfo.getPrimaryKeys().isEmpty(), prefix + " 没有定义主键");
		Assert.isTrue(!tableInfo.getColumnInfoList().isEmpty(), prefix + " 没有定义任何字段");

		this.javaClassName = this.context.getEntityProperties().buildTableJavaName(this.tableInfo.getName());

		this.initImport();
		this.initImplements(); // 初始化implements
		this.initColumn();// 构建数据库属性，未建立外键关系
	}

	/** 当读取数据库信息完成后，调用这个方法 */
	public void afterMetadataReaded() {
		this.buildForeignKeyRelation();
	}

	private void buildForeignKeyRelation() {
		for (ForeignKeyInfo fk : this.tableInfo.getFkInfoList()) {
			// 外键关联的表
			TableBean refTable = this.context.getTableByName(fk.getRefTableName());

			// 有外键的字段
			ColumnBean column = this.columnMapForName.get(fk.getColumnName());
			column.setRefTable(refTable);
			this.manyToOne.add(column);// 添加到当前表的 多对1 list

			// 添加到关联表的 1对多 list
			refTable.oneToMany.add(this);
		}
	}

	/**
	 * 获得所有的列
	 */
	public List<ColumnBean> getColumns() {
		return columns;
	}

	public String getComment() {
		return this.tableInfo.getComment();
	}

	public IColumnBean getId() {
		return id;
	}

	/**
	 * entity implements接口 的代码
	 * 
	 */
	public String getImplementsStr() {
		return implementsStr;
	}

	/**
	 * 获得所有的import
	 * 
	 * @return
	 */
	public List<String> getImportList() {
		return this.importClassSet.getImportList();
	}

	/**
	 * 首字母大小的java类名，同时也作为java文件名
	 */
	public String getJavaClassName() {
		return javaClassName;
	}

	/**
	 * 属性名
	 */
	public String getPropName() {
		return StringUtils.lowerFirstChar(this.javaClassName);
	}

	/**
	 * 首字母消息的类名，通常用于作为jsp、js文件名
	 */
	public String getOtherFileName() {
		return StringUtils.lowerFirstChar(this.javaClassName);
	}

	/**
	 * 多对一关系的本表的字段
	 */
	public List<ColumnBean> getManyToOne() {
		return manyToOne;
	}

	public String getName() {
		return this.tableInfo.getName();
	}

	/**
	 * 一对多关系的表名
	 */
	public List<TableBean> getOneToMany() {
		return oneToMany;
	}

	/** 是否是复合主键 */
	public boolean isEmbeddedId() {
		return hasEmbeddedId;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("table(")
				.append(this.tableInfo.getName())
				.append(":")
				.append(this.tableInfo.getComment())
				.append(")");

		return sb.toString();
	}

	/**
	 * 初始化列以后，可以初始化主键
	 */
	private void buildPk(List<ColumnInfo> pkList) {
		if (pkList.size() == 1) {
			// 单一字段主键
			this.id = new SimpleIdBean(this, pkList.get(0));
			this.hasEmbeddedId = false;
		} else {
			// 复合主键
			EmbeddedIdBean idOfEmbeddedVo = new EmbeddedIdBean(this, pkList);
			this.id = idOfEmbeddedVo;
			this.hasEmbeddedId = true;

			// 加到context中，稍后可以生成
			this.context.addEmbeddedId(idOfEmbeddedVo);

			this.importClassSet.add(EmbeddedId.class);
			this.importClassSet.add(AttributeOverrides.class);
			this.importClassSet.add(AttributeOverride.class);
		}

	}

	private void initColumn() {
		List<ColumnInfo> pkList = new LinkedList<>();

		for (ColumnInfo colInfo : this.tableInfo.getColumnInfoList()) {
			String name = colInfo.getColumnName();
			if (this.tableInfo.getPrimaryKeys().contains(name)) {
				// 主键另外放一个地方
				colInfo.setPrimaryKey(true);
				pkList.add(colInfo);
			} else {
				// 主键之外的都放到 colums这个list中
				ColumnBean col = new ColumnBean(this, colInfo);

				columns.add(col);
				this.columnMapForName.put(col.getName(), col);

				// 根据一些属性，增加import
				if (col.isHasNotNull()) {
					this.addImport(NotNull.class);
				}
				if (col.isHasComment()) {
					this.addImport(AComment.class);
				}

				// 检查是否需要生成表单
				this.needEditForm = this.needEditForm || col.getForm().isInEditForm();
				this.needListForm = this.needListForm || col.getForm().isInListForm();
			}
		}

		this.buildPk(pkList);// 构建主键
	}

	private void initImplements() {
		// 获得所有要生成到 java文件中的 接口定义
		Set<Class<?>> set = this.context.getEntityImplementClassSet();

		// 将接口class放到 import中
		this.importClassSet.addAll(set);
		this.importClassSet.add(Serializable.class);
		this.importClassSet.add(Cloneable.class);

		if (!set.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			for (Class<?> clazz : set) {

				sb.append(", ");
				sb.append(clazz.getSimpleName());
			}

			this.implementsStr = sb.toString();
		}

	}

	/**
	 * 增加Import
	 * 
	 * @param clazz
	 */
	protected void addImport(Class<?> clazz) {
		this.importClassSet.add(clazz);
	}

	/**
	 * 是否需要生成ListForm
	 */
	public boolean isNeedListForm() {
		return needListForm;
	}

	/**
	 * 是否需要生成EditForm
	 */
	public boolean isNeedEditForm() {
		return needEditForm;
	}

	protected void initImport() {
		this.importClassSet.add(Serializable.class);
		this.importClassSet.add(Entity.class);
		this.importClassSet.add(Table.class);
		this.importClassSet.add(Column.class);
		this.importClassSet.add(Id.class);
		this.importClassSet.add(NamedQuery.class);
	}
}
