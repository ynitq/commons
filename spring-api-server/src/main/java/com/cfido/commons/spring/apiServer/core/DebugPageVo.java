package com.cfido.commons.spring.apiServer.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cfido.commons.spring.apiServer.service.ApiMethodInfo;

/**
 * <pre>
 * 用于debug界面的vo
 * </pre>
 * 
 * @author 梁韦江 2016年7月4日
 */
public class DebugPageVo {

	/** 用于分类存储所有接口 */
	public class InfGroup {
		private String infKey;// 分组的key
		private String memo;// 备注

		/** 这个分组下的所有api */
		private final List<ApiMethodInfo<?>> methods = new LinkedList<>();

		public String getInfKey() {
			return infKey;
		}

		public void setInfKey(String infKey) {
			this.infKey = infKey;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}

		public List<ApiMethodInfo<?>> getMethods() {
			return methods;
		}

	}

	private final List<ApiMethodInfo<?>> methods;
	private final String apiUrlPrefix;

	/** 所有的分类 */
	private final List<InfGroup> infGroup = new LinkedList<>();

	public DebugPageVo(String apiUrlPrefix, List<ApiMethodInfo<?>> methods) {
		super();
		this.apiUrlPrefix = apiUrlPrefix;
		this.methods = methods;

		Map<String, InfGroup> groupMap = new HashMap<>();
		for (ApiMethodInfo<?> mi : methods) {
			String key = mi.getInfaceKey();

			// 看看是否存在这个分组
			InfGroup group = groupMap.get(key);
			if (group == null) {
				// 如果不存在就添加
				group = new InfGroup();
				group.infKey = key;
				group.memo = mi.getInfMemo();
				groupMap.put(key, group);

				// 同时将分组放到列表中
				this.infGroup.add(group);
			}
			// 将方法放到分组中
			group.methods.add(mi);
		}

		Collections.sort(this.infGroup, new Comparator<InfGroup>() {

			@Override
			public int compare(InfGroup o1, InfGroup o2) {
				// 分组按key字母顺序排序
				return o1.infKey.compareTo(o2.infKey);
			}
		});

	}

	public List<InfGroup> getInfGroup() {
		return infGroup;
	}

	public List<ApiMethodInfo<?>> getMethods() {
		return methods;
	}

	public String getApiUrlPrefix() {
		return apiUrlPrefix;
	}

}
