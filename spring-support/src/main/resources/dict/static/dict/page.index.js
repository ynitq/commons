$(function() {
	dictManager.init();
	lzUtil.initDropify("input_import_file");

	$("#my_page").show();
});

var dictManager = {

	/** 左边div的数据model */
	pageVm : {},

	urlGetCurUser : "/api/dictAdmin/getCurUser", // 获得当前是否登录的状态
	urlLogin : "/api/dictAdmin/login", // 登录的url
	urlLogout : "/api/dictAdmin/logout", // 登出
	urlSearch : "/api/dictManager/search", // 搜索
	urlImportXml : "/api/dictManager/importXml", // 导入
	urlSave : " /api/dictManager/save",// 保存
	urlDelete : "/api/dictManager/delete", // 删除
	urlPasswordDemo : "/api/dictAdmin/passwordDemo", // 创建密码密文样例

	urlAttachmentList : "/api/dictAttachments/list", // 附件列表
	urlAttachmentSave : "/api/dictAttachments/save",// 附件保存
	urlAttachmentDelete : "/api/dictAttachments/delete",// 附件删除

	fileUploadDropify : null,

	/** 页面model中的属性 * */
	pageModelData : {
		logined : false, // 是否已经登录
		encodedPwd : "", // 生成出来的密码密文

		curPage : "keyManager", // 当前菜单 keyManager,attachment

		account : "", // 已经登录的账号名

		/**
		 * ------------------- 和文字管理页相关，此处的变量名没有前缀是不对的，历史原因
		 */
		list : {}, // 列表

		editMode : false, // 是否是编辑模式
		tabTitle : "新增Key", // 标签页上的名字

		editRow : { // 编辑表单的数据
			key : "",
			value : "",
			memo : "",
			html : false,
			usedCount : 0,
		},

		pageBar : {// 翻页控制
			searchKey : "",

			pageNo : 1,
			pageTotal : 0,
			show : false,
			hasNext : false,
			hasPrev : false,
		},

		/**
		 * --- 和附件页相关,attachment这个词太长了，我们懒，用file代替
		 */
		fileList : {},// 列表

		fileFormPanelTitle : "新增附件",// 附件编辑panel的标题
		fileFormEditMode : false, // 附件编辑表单是否处于编辑模式，还是新建模式
		fileFormKey : "",// 附件编辑表单中的key
		fileFormMemo : "",// 附件编辑表单中的备注
		fileFormUrl : "",
	},

	init : function() {

		var that = this;

		console.log("初始化页面 init()");

		// 建立详情页和数据的绑定关系
		this.pageVm = new Vue({
			el : '#my_page',

			/** 绑定对象的属性 */
			data : that.pageModelData,

			/** 绑定对象的方法 */
			methods : {
				/** 登出 */
				logout : function(event) {
					// 登出 logout
					lzUtil.ajax(that.urlLogout, {}, function(result, status, xhr) {
						that.onUserInfoResponse(result);
					});
				},

				/** 查看 */
				view : function(index) {
					var cur = this.list[index];
					console.log("查看内容 row index=", index, this.editRow);

					this.editRow.key = cur.key;
					this.editRow.value = cur.value;
					this.editRow.html = cur.html;
					this.editRow.memo = cur.memo;
					this.editRow.usedCount = cur.usedCount;

					this.editMode = true;
					this.tabTitle = "编辑";

					that.switchTab("div_tab_edit");
				},

				/** 增加新key */
				newRow : function() {
					console.log("newRow:增加新的key");
					that.resetAddForm();

					this.editMode = false;
					this.tabTitle = "新增Key";

					that.switchTab("div_tab_edit");
				},

				deleteKey : function(key) {
					console.log("删除key", key);

					lzUtil.confirm("请确认", "确认要删除吗？key:" + key, function() {
						that.doDelete(key);
					});
				},

				nextPage : function() {
					console.log("点击 下一页");
					if (this.pageBar.pageNo < this.pageBar.pageTotal) {
						this.pageBar.pageNo++;
						that.search();
					}
				},

				prevPage : function() {
					console.log("点击 上一页");

					if (this.pageBar.pageNo > 1) {
						this.pageBar.pageNo--;
						that.search();
					}

				},

				swithToKey : function() {
					console.log("切换到文字管理")
					this.curPage = "keyManager";
					that.search();
				},

				swithToAttachment : function() {
					console.log("切换到附件管理")
					this.curPage = "attachment";
					that.fileList();
				},

				/** 查看附件 */
				fileView : function(index) {
					var cur = this.fileList[index];
					console.log("查看附件 index=", index, cur);

					that.resetFileEditForm();// 重置表单

					this.fileFormKey = cur.row.key;
					this.fileFormMemo = cur.row.memo;
					this.fileFormEditMode = true;
					this.fileFormPanelTitle = "编辑";
					this.fileFormUrl = cur.fullPath;

					if (cur.row.imageFile) {
						that.fileUploadDropify.setPreview(true, cur.fullThumbPath);
					}

				},
				/** 删除附件 */
				fileDelete : function(key) {
					console.log("删除key", key);

					lzUtil.confirm("请确认", "确认要删除附件吗？key:" + key, function() {
						that.doFileDelete(key);
					});
				},
				/** 切换到新建附件模式 */
				fileChangeToNewMode : function() {
					that.resetFileEditForm();// 重置表单

					this.fileFormKey = "";
					this.fileFormMemo = "";
					this.fileFormEditMode = false;
					this.fileFormPanelTitle = "新建";
				},
			},
		});

		// 初始化各类表单
		this.initSearchForm();
		this.initImportForm();
		this.initLoginForm();
		this.initEditForm();
		this.initCreatePasswordForm();
		this.initFileEditForm();

		// 初始化完成后，去获取一次用户状态
		lzUtil.ajax(this.urlGetCurUser, {}, function(result, status, xhr) {
			that.onUserInfoResponse(result);
		});
	},

	/** 初始化附件编辑form */
	initFileEditForm : function() {
		console.log("初始化附件编辑form");
		var that = this;
		$("#form_edit_attach").submit(function() {
			lzUtil.ajaxSubmit("form_edit_attach", that.urlAttachmentSave, function(result) {
				lzUtil.showMsg("保存成功");

				// 刷新列表页
				that.fileList();
			});
			return false;
		});
	},

	/** 初始化登录form */
	initLoginForm : function() {
		console.log("初始化登录页");
		var that = this;
		$("#form_login").submit(function() {
			var param = $(this).serialize();

			lzUtil.ajax(that.urlLogin, param, function(result, status, xhr) {
				that.onUserInfoResponse(result);
			});
			return false;
		});
	},

	/** 初始化编辑页 */
	initEditForm : function() {
		console.log("初始化编辑页");
		var that = this;

		this.fileUploadDropify = lzUtil.initDropify("input_attach_file");

		$("#form_edit").submit(function() {
			var param = $(this).serialize();

			console.log("保存", param);

			lzUtil.ajax(that.urlSave, param, function(result, status, xhr) {
				lzUtil.showMsg("保存成功");

				// 刷新列表页
				that.updateListPage(result);
			});
			return false;
		});
	},

	initCreatePasswordForm : function() {
		console.log("初始化 创建加密密码表单");
		var that = this;
		$("#form_create_password").submit(function() {
			var param = $(this).serialize();

			that.pageVm.encodedPwd = "";

			lzUtil.ajax(that.urlPasswordDemo, param, function(result, status, xhr) {
				that.pageVm.encodedPwd = result.message;
			});
			return false;
		});
	},

	/** 初始化导入文件form */
	initImportForm : function() {
		console.log("初始化 导入页");
		var that = this;
		$("#form_import").submit(function() {
			lzUtil.ajaxSubmit("form_import", that.urlImportXml, function() {
				lzUtil.showMsg("导出成功");

				// 从第一页开始刷新
				that.pageVm.pageBar.pageNo = 1;

				that.search();
			});
			return false;
		});
	},

	/** 重置附件编辑表单 */
	resetFileEditForm : function() {
		this.fileUploadDropify.clearElement();
		// this.setPreview
	},

	/** 重置新建表单 */
	resetAddForm : function() {
		this.pageVm.editRow.key = "";
		this.pageVm.editRow.value = "";
		this.pageVm.editRow.html = false;
		this.pageVm.editRow.memo = "";
	},

	/** 初始化搜索form */
	initSearchForm : function() {
		console.log("初始化搜索栏");
		var that = this;
		$("#form_seach").submit(function() {
			that.search();
			return false;
		});

		$("#btn_refresh").click(function() {
			that.search();
		});
	},

	/** 搜索 */
	search : function() {
		var that = this;

		var param = {
			key : this.pageVm.pageBar.searchKey,
			pageNo : this.pageVm.pageBar.pageNo,
			pageSize : 10,
		};

		console.log("search()", param);

		lzUtil.ajax(that.urlSearch, param, function(result, status, xhr) {
			that.updateListPage(result);
		});
	},

	/** 确认后，执行删除文字 */
	doDelete : function(key) {
		var that = this;
		var param = {
			key : key,
		};
		lzUtil.ajax(that.urlDelete, param, function(result, status, xhr) {
			lzUtil.showMsg("删除成功");

			// 删除成功后，切换到新增模式
			that.pageVm.newRow();

			// 刷新列表页
			that.updateListPage(result);
		});
	},

	/** 确认后，执行删除附件 */
	doFileDelete : function(key) {
		var that = this;
		var param = {
			key : key,
		};
		lzUtil.ajax(that.urlAttachmentDelete, param, function(result, status, xhr) {
			lzUtil.showMsg("删除成功");

			// 删除成功后，切换到新增模式
			that.pageVm.fileChangeToNewMode();

			// 刷新列表页
			that.pageVm.fileList = result.list;
		});
	},

	/** 根据DictKeySearchResponse的结果更新列表栏 */
	updateListPage : function(result) {
		console.log("更新列表数据 pageNo=%d, pageTotal=%d", result.pageNo, result.pageTotal);
		this.pageVm.list = result.list;

		var pageBar = this.pageVm.pageBar;

		pageBar.pageNo = result.pageNo;
		pageBar.pageTotal = result.pageTotal;

		if (result.pageTotal > 0) {
			pageBar.show = true;
			pageBar.hasNext = (result.pageNo < result.pageTotal);
			pageBar.hasPrev = (result.pageNo > 1);
		} else {
			pageBar.show = false;
		}
	},

	/** 切换标签页 * */
	switchTab : function(tabId) {
		$('#tab_nav a[href="#' + tabId + '"]').tab('show');
	},

	/** 获取当前用户状态，或者登录时，收到的格式是UserInfoResponse */
	onUserInfoResponse : function(res) {
		console.log("获得用户状态 res:", res);

		this.pageVm.logined = res.logined;
		this.pageVm.account = res.account;

		if (res.logined) {
			this.pageVm.swithToKey();
			// this.pageVm.swithToAttachment();
		}
	},

	/** 加载附件列表 */
	fileList : function() {
		var that = this;
		console.log("附件列表");

		lzUtil.ajax(that.urlAttachmentList, null, function(result, status, xhr) {
			that.pageVm.fileList = result.list;

			var curkey = that.pageVm.fileFormKey;
			if (!that.pageVm.fileFormEditMode && !lzUtil.isEmpty(curkey)) {
				// 如果是在新增模式时的保存,并且当前key不为空，就将右边变成正在变成这个key的状态
				var index = 0;
				for (; index < result.list.length; index++) {
					var vo = result.list[index];
					if (curkey == vo.row.key) {
						console.log(vo.row.key);
						that.pageVm.fileView(index);
						break;
					}
				}
			}

		});
	}

};
