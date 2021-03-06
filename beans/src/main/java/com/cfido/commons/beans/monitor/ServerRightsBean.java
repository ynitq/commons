package com.cfido.commons.beans.monitor;

import java.util.LinkedList;
import java.util.List;

import com.cfido.commons.annotation.bean.AComment;

/**
 * <pre>
 * 服务器的权限
 * 
 * 这个类用于json传递数据，所有的属性都需要有setter和getter，内部类都需要是static的
 * </pre>
 * 
 * @author 梁韦江
 */
public class ServerRightsBean {

	/** 权限组 */
	public static class GroupBean {
		@AComment("权限组显示用名字")
		private String name;

		@AComment("这个权限组的权限列表")
		private List<RightsBean> rights = new LinkedList<>();

		/** 在这个权限组增加权限 */
		public GroupBean addRights(String name, String optId) {
			RightsBean bean = new RightsBean();
			bean.name = name;
			bean.optId = optId;

			this.rights.add(bean);
			return this;
		}

		public String getName() {
			return name;
		}

		public List<RightsBean> getRights() {
			return rights;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setRights(List<RightsBean> rights) {
			this.rights = rights;
		}
	}

	/** 权限 */
	public static class RightsBean {
		/** 显示用的名字 */
		@AComment("权限显示用的名字")
		private String name;

		/** 判断权限用的id */
		@AComment("权限的ID")
		private String optId;

		public String getName() {
			return name;
		}

		public String getOptId() {
			return optId;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setOptId(String optId) {
			this.optId = optId;
		}

	}

	@AComment("服务器权限类型的名字，仅用于显示")
	private String name;

	@AComment("这个类型的服务器的权限组列表")
	private List<GroupBean> groups = new LinkedList<>();

	/** 为这个服务器增加一组权限 */
	public GroupBean addGroup(String name) {
		GroupBean bean = new GroupBean();
		bean.name = name;

		this.groups.add(bean);
		return bean;
	}

	public List<GroupBean> getGroups() {
		return groups;
	}

	public void setGroups(List<GroupBean> groups) {
		this.groups = groups;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
