package com.cfido.commons.utils.html;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;

import com.linzi.common.loginCheck.IWebUser;

/**
 * <pre>
 * 用于方便在页面生成菜单的帮助类。
 * </pre>
 * 
 * @author 梁韦江 2016年8月23日
 */
public class MenuVo {

	/**
	 * <pre>
	 * 菜单中的一项, 用于在header中显示菜单
	 * </pre>
	 * 
	 * @author 梁韦江 2015年6月6日
	 */
	public class MenuItem {

		private final String optId; // 菜单的唯一标示符，也作为权限检查的id
		private final String name;// 菜单项的名字
		private final String url;// 菜单的url
		private final String uri;// 没用问号参数的uri
		private final boolean newWin; // 是否在新窗口打开

		private boolean active;// 是否激活
		private boolean open; // 是否是打开状态
		private boolean hasRights = false; // 用于根据权限生成用户菜单时，用的临时变量
		private final MenuItem parent; // 父菜单
		private final String iconCssClass;// 菜单图标的css样式

		private final List<MenuItem> submenuList = new LinkedList<MenuItem>();

		/**
		 * 克隆菜单
		 */
		private MenuItem cloneMenu() {
			return new MenuItem(this.parent, this.optId, this.name, this.url, this.iconCssClass, this.newWin);
		}

		/**
		 * 一级菜单，并且没用子菜单
		 * 
		 * @param id
		 * @param name
		 * @param url
		 */
		private MenuItem(MenuItem parent, String id, String name, String url, String iconCssClass, boolean newWin) {
			super();

			Assert.notNull(id, "optId 不能为空");
			Assert.notNull(name, "name 不能为空");

			this.parent = parent;
			this.optId = id;
			this.name = name;
			this.url = url;
			this.uri = buildUri(url);
			this.newWin = newWin;
			this.iconCssClass = iconCssClass;

			Assert.notNull(iconCssClass, "菜单的图标样式不能为空");
		}

		/**
		 * 添加新菜单，因为我们只有两层的菜单，所以子菜单必须有url
		 * 
		 * @param id
		 * @param name
		 * @param url
		 * @return 返回的是父菜单，而不是新增的菜单
		 */
		public MenuItem addSubMenu(String id, String name, String url) {
			return this.addSubMenu(id, name, url, "icon-check", false);
		}

		public MenuItem addSubMenu(String id, String name, String url, String iconCssClass) {
			return this.addSubMenu(id, name, url, iconCssClass, false);
		}

		/**
		 * 添加新菜单，因为我们只有两层的菜单，所以子菜单必须有url
		 * 
		 * @param id
		 *            权限id
		 * @param name
		 *            名字
		 * @param url
		 *            url
		 * @param iconCssClass
		 *            前缀，例如 图标
		 * @param newWin
		 *            是否在新窗口打开
		 * @return
		 */
		public MenuItem addSubMenu(String id, String name, String url, String iconCssClass, boolean newWin) {
			MenuItem sub = new MenuItem(this, id, name, url, iconCssClass, newWin);
			this.submenuList.add(sub);
			return this;
		}

		public String getId() {
			return optId;
		}

		/**
		 * 根据是否打开和是否激活，返回不同的class
		 * 
		 * @return
		 */
		public String getClassStr() {
			if (this.active || this.open) {
				StringBuffer sb = new StringBuffer();
				sb.append(" class='");
				if (this.active) {
					sb.append(MenuVo.this.htmlClassForActive);
					sb.append(' ');
				}
				if (this.open) {
					sb.append(MenuVo.this.htmlClassForOpen);
				}
				sb.append("'");
				return sb.toString();
			} else {
				return "";
			}
		}

		public String getName() {
			return name;
		}

		public List<MenuItem> getSubmenuList() {
			return submenuList;
		}

		public String getUrl() {
			return url;
		}

		/**
		 * 是否有子菜单
		 * 
		 * @return
		 */
		public boolean isHasSubMenu() {
			return !this.submenuList.isEmpty();
		}

		public String getIconCssClass() {
			return this.iconCssClass;
		}

		private void checkRights(IWebUser user) {
			this.hasRights = user.checkRights(this.optId);

			// 如果对主菜单没用权限，则对子菜单也没用权限
			if (this.isHasSubMenu()) {
				for (MenuItem item : this.submenuList) {
					item.checkRights(user);
				}
			}
		}

		/**
		 * @return html中target的代码
		 */
		public String getTargetStr() {
			if (this.newWin) {
				return MenuVo.this.htmlNewWindow;
			} else {
				return "";
			}
		}

		/**
		 * 设置当前菜单,
		 * 
		 * @param optId
		 */
		private void setCurMenuById(String optId) {
			if (this.optId.equals(optId)) {
				// 如果当前url和菜单url匹配，就表示该菜单属于激活
				this.active = true;
			} else if (!this.submenuList.isEmpty()) {
				// 如果不匹配，就检查子菜单
				for (MenuItem item : this.submenuList) {
					if (item.optId.equals(optId)) {
						item.active = true;
						this.open = true;
					}
				}
			}
		}

		/**
		 * 设置当前菜单,
		 * 
		 * @param uriParam
		 */
		private void setCurMenuByUrl(String uriParam) {
			if (uriParam == null) {
				return;
			}

			if (this.isHasSubMenu()) {
				// 如果有子菜单，就匹配子菜单
				for (MenuItem item : this.submenuList) {
					if (item.isMatchUri(uriParam)) {
						// 如果子菜单匹配了，子菜单设置为激活状态
						item.active = true;

						// 同时当前菜单也设置为打开状态
						this.open = true;
						this.active = true;
						return;
					}
				}
			} else {
				if (this.isMatchUri(uriParam)) {
					// 如果当前url和菜单url匹配，就表示该菜单属于激活
					this.active = true;
					return;
				}
			}
		}

		/**
		 * 输入的uri是否和当前的uri匹配
		 * 
		 * @param uriParam
		 * @return
		 */
		private boolean isMatchUri(String uriParam) {
			return uriParam != null && uriParam.equals(this.uri);
		}

		private void addDebugInfo(StringBuffer sb) {
			sb.append(name);
			if (this.uri != null) {
				sb.append(" - ").append(this.uri);
			}

			if (this.hasRights) {
				sb.append(" 有权限");
			}

			sb.append("\n");

			if (this.isHasSubMenu()) {
				for (MenuItem sub : submenuList) {
					sb.append("\t");

					sub.addDebugInfo(sb);
				}
			}
		}
	}

	/** 一级菜单 */
	private final List<MenuItem> menus = new LinkedList<>();

	private String htmlClassForActive = "active";// 菜单激活时的class
	private String htmlClassForOpen = "open";// 菜单打开时的class
	private String htmlNewWindow = "target='_blank'";// 打开新窗口

	/**
	 * 增加一级菜单,默认没有链接，不开新窗口
	 * 
	 * @param optId
	 *            权限ID
	 * @param name
	 *            菜单名
	 * @return
	 */
	public MenuItem addMenu(String optId, String name) {
		return this.addMenu(optId, name, null);
	}

	/**
	 * 增加一级菜单， 默认:不开新窗口, 图标：icon-home
	 * 
	 * @param optId
	 *            权限ID
	 * @param name
	 *            菜单名
	 * @param url
	 * @return
	 */
	public MenuItem addMenu(String optId, String name, String url) {
		return this.addMenu(optId, name, url, "icon-home", false);
	}

	/**
	 * 增加一级菜单, 默认：不打开新窗口
	 * 
	 * @param optId
	 *            权限ID
	 * @param name
	 *            菜单名
	 * @param url
	 * @param iconCssClass
	 *            图标CSS样式
	 * @return
	 */
	public MenuItem addMenu(String optId, String name, String url, String iconCssClass) {
		return this.addMenu(optId, name, url, iconCssClass, false);
	}

	/**
	 * 增加一级菜单
	 * 
	 * @param optId
	 *            权限ID
	 * @param name
	 *            菜单名
	 * @param url
	 * @param iconCssClass
	 *            图标CSS样式
	 * @param newWin
	 *            是否在新窗口打开
	 * @return
	 */
	public MenuItem addMenu(String optId, String name, String url, String iconCssClass, boolean newWin) {
		MenuItem menu = new MenuItem(null, optId, name, url, iconCssClass, newWin);
		this.menus.add(menu);
		return menu;
	}

	public List<MenuItem> getMenus() {
		return menus;
	}

	/**
	 * 根据id，设置激活菜单
	 * 
	 * @param optId
	 */
	public void setCurMenuById(String optId) {
		for (MenuItem menuItem : this.menus) {
			menuItem.setCurMenuById(optId);
		}
	}

	/**
	 * 根据url设置激活菜单
	 * 
	 * @param request
	 */
	public void setCurMenuByUrl(HttpServletRequest request) {
		String url = request.getRequestURI();
		for (MenuItem menuItem : this.menus) {
			menuItem.setCurMenuByUrl(url);
		}
	}

	/**
	 * 去除url中?后面的内容
	 * 
	 * @param url
	 *            可能带?的url
	 * @return 无？号的 uri
	 */
	private static String buildUri(String url) {
		if (url == null) {
			return null;
		} else {
			int index = url.indexOf("?");
			if (index <= 0) {
				return url;
			} else {
				return url.substring(0, index);
			}
		}
	}

	/**
	 * 根据用户的权限，生成新的菜单
	 * 
	 * <pre>
	 * 规则
	 * 1. 如果子菜单有权限，则主菜单也有权限
	 * 2. 如果对主菜单有权限，则对所有子菜单都有权限
	 * 
	 * </pre>
	 * 
	 * @param user
	 *            用户对象
	 * @return 经过权限过滤后的菜单
	 */
	public MenuVo createForUser(IWebUser user) {
		// 先对所有菜单都检查一把
		for (MenuItem item : this.menus) {
			item.checkRights(user);
		}

		MenuVo res = new MenuVo();
		for (MenuItem item : this.menus) {
			if (this.isHasRights(item)) {
				// 如果对主菜单有权限，就加进来
				MenuItem newItem = item.cloneMenu();
				res.menus.add(newItem);

				if (item.isHasSubMenu()) {
					// 如果有子菜单
					for (MenuItem sub : item.submenuList) {
						if (this.isHasRights(sub)) {
							// 如果对某子菜单有权限，就加到主菜单上
							newItem.submenuList.add(sub.cloneMenu());
						}
					}
				}
			}
		}

		return res;
	}

	public boolean isHasRights(MenuItem menu) {
		if (menu.hasRights) {
			// 如果当前菜单直接就有有权限，就返回有权限
			return true;
		}

		// 如果不是直接的权限，就看看是否有子菜单和父菜单了

		// 先检查子菜单的
		if (menu.isHasSubMenu()) {

			//
			for (MenuItem sub : menu.submenuList) {
				if (sub.hasRights) {
					// 只要某一个子菜单有权限，就等于有权限
					return true;
				}
			}
		}

		// 检查父菜单的
		if (menu.parent != null && menu.parent.hasRights) {
			// 有父菜单，并且父菜单有直接权限，则返回真
			return true;
		}

		// 默认是没有权限
		return false;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (MenuItem item : this.menus) {
			item.addDebugInfo(sb);
		}
		return sb.toString();
	}

	public String getHtmlClassForActive() {
		return htmlClassForActive;
	}

	public void setHtmlClassForActive(String htmlClassForActive) {
		this.htmlClassForActive = htmlClassForActive;
	}

	public String getHtmlClassForOpen() {
		return htmlClassForOpen;
	}

	public void setHtmlClassForOpen(String htmlClassForOpen) {
		this.htmlClassForOpen = htmlClassForOpen;
	}

	public String getHtmlNewWindow() {
		return htmlNewWindow;
	}

	public void setHtmlNewWindow(String htmlNewWindow) {
		this.htmlNewWindow = htmlNewWindow;
	}

}
