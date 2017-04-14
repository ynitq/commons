/**
 * 通用工具集合
 */
$(document).ready(function() {
	lzUtil.checkBrowerVersion();
	lzUtil.initToastr();
	lzUtil.initMsgFromPrePage();
});

/**
 * 自定义js类
 */
var lzUtil =
		{

			/** “加载中”的div的id */
			loadingDivId : "div_loading",

			isEmpty : function(str) {
				return str == null || (typeof (str) == undefined)|| (typeof (str) == 'undefined') || (str == 'undefined')|| str == '';
			},

			/**
			 * 初始化文件上传空间
			 */
			initDropify : function(inputId) {
				var obj = $("#" + inputId).dropify({
					messages : {
						'default' : '点击或拖拽文件到这里',
						'replace' : '点击或拖拽文件到这里来替换文件',
						'remove' : '移除文件',
						'error' : '对不起，有错误发生了'
					},
					error : {
						'fileSize' : '只能上传 {{ value }} 的文件',
						'minWidth' : 'The image width is too small ({{ value }}}px min).',
						'maxWidth' : 'The image width is too big ({{ value }}}px max).',
						'minHeight' : 'The image height is too small ({{ value }}}px min).',
						'maxHeight' : 'The image height is too big ({{ value }}px max).',
						'imageFormat' : '只能上传  {{ value }} 格式的图片。',
						'fileExtension' : '请选择  {{ value }} 文件上传。'
					},
				});

				return obj.data("dropify");

			},

			/**
			 * 用低版本的IE就烦死他
			 */
			checkBrowerVersion : function() {
				var isIE = !!window.ActiveXObject;
				var isIE6 = isIE && !window.XMLHttpRequest;
				var isIE8 = isIE && !!document.documentMode;
				var isIE7 = isIE && !isIE6 && !isIE8;

				if ((isIE6 || isIE7 || isIE8) && navigator.userAgent.indexOf("MSIE") != -1) {
					alert("您的浏览器版本太老！不符合新的web标准，我们强烈建议您使用谷歌浏览器进行操作！");
				}
			},

			// 为baseJsonResponse显示参数错误
			showErrorForJsonResponse : function(res) {
				this.showErrorMsg(res.message);
			},

			// 显示错误信息
			showErrorMsg : function(msg) {
				toastr.error(msg);
			},

			// 正常的显示信息
			showMsg : function(msg) {
				toastr.success(msg);
			},

			/**
			 * 重新封装的ajax
			 * 
			 * @param url
			 *            ajax访问url
			 * @param param
			 *            ajax参数
			 * @param callback
			 *            回调函数，参数是 result, status, xhr
			 * @param onBeforeAjax
			 *            ajax调用前，调用该方法
			 * @param onAfterAjax
			 *            ajax调用后，调用该方法
			 */
			ajax : function(url, param, callback, onBeforeAjax, onAfterAjax) {
				// 打开loading图标的方法
				var that = this;

				var beforeFn = that.onBeforeAjax;
				if (onBeforeAjax) {
					beforeFn = onBeforeAjax;
				}

				// 关闭loading图标的方法
				var afterFn = that.onAfterAjax;
				if (onAfterAjax) {
					afterFn = onAfterAjax;
				}

				var options = {
					type : "post", // post get
					url : url,
					data : param,
					dataType : "json",
					// success : callback,
					success : function(result, status, xhr) {
						afterFn();// 关闭图标
						if (!result.success) {
							// 如果失败
							that.showErrorForJsonResponse(result);
						} else {
							// 如果成功
							callback(result, status, xhr);
						}
					},
					error : function(e) {
						afterFn();// 关闭图标
						if (e.readyState == 4) {
							// 服务器有返回内容
							var result = eval("(" + e.responseText + ")");
							that.showErrorForJsonResponse(result);
						} else {
							that.showErrorMsg("出错了! 状态码:" + e.readyState);
						}
					}
				};

				beforeFn();// 显示loading
				$.ajax(options);
			},

			/** 默认的 ajax开始前调用的方法 */
			onBeforeAjax : function() {
				$("#" + lzUtil.loadingDivId).show();
			},

			/** 默认的 ajax 结束后调用的方法 */
			onAfterAjax : function() {
				$("#" + lzUtil.loadingDivId).hide();
			},

			/**
			 * 初始化 bootstrap-toastr
			 */
			initToastr : function() {
				console.log("初始化 bootstrap-toastr");
				toastr.options = {
					"closeButton" : true,
					"debug" : false,
					"positionClass" : "toast-top-right",
					"onclick" : null,
					"showDuration" : "1000",
					"hideDuration" : "1000",
					"timeOut" : "3000",
					"extendedTimeOut" : "1000",
					"showEasing" : "swing",
					"hideEasing" : "linear",
					"showMethod" : "fadeIn",
					"hideMethod" : "fadeOut"
				}
			},

			initMsgFromPrePage : function() {
				var tipInfo = $("#tipInfo").val();
				if (tipInfo != null && tipInfo != "") {
					lzUtil.showMsg(tipInfo);
				}
			},

			/**
			 * 弹出确认框
			 * 
			 * @param title
			 *            标题
			 * @param msg
			 *            消息
			 * @param onOk
			 *            点击确认是的回调函数
			 * @param onCancel
			 *            点击取消时的回调函数
			 */
			confirm : function(title, msg, onOk, onCancel) {
				// 避免缓存，每次都清空
				var htmlId = "lzUtilConfirmModal";
				var jqueryId = "#" + htmlId;

				var ui = $(jqueryId);
				if (ui.length == 0) {
					// 如果这个id的模式窗不存在，就创建

					var html =
							"<div id='lzUtilConfirmModal' class='modal fade' tabindex='-1' role='dialog' aria-labelledby='lzUtilConfirmModal_label'>"
									+ "	<div class='modal-dialog'>" + "		<div class='modal-content'>"
									+ "			<div class='modal-header'>"
									+ "				<button type='button' class='close' data-dismiss='modal' aria-hidden='true'></button>"
									+ "				<h4 class='modal-title' id='lzUtilConfirmModal_label'></h4>" + "			</div>"
									+ "			<div class='modal-body'>" + "				<p id='lzUtilConfirmModal_body'></p>" + "			</div>"
									+ "			<div class='modal-footer'>"
									+ "				<button data-dismiss='modal' class='btn btn-primary js_ok'>确定</button>"
									+ "				<button data-dismiss='modal' class='btn btn-default'>取消</button>" + "			</div>"
									+ "		</div>" + "	</div>" + "</div>";

					ui = $(html);
					$(document.body).append(ui);
				}

				$("#lzUtilConfirmModal_label").text(title);
				$("#lzUtilConfirmModal_body").text(msg);

				// 添加ok的回调函数到按钮上
				var okBtn = $(ui.find(".js_ok")[0]);
				okBtn.unbind();// 重新绑定click之前，需要取消原来的所有绑定
				okBtn.click(function() {
					try {
						if (onOk) {
							onOk();
						}
					} catch (e) {
						console.log("出错了:" + e)
					}

					ui.modal('hide');

				});

				// 添加cancel回调函数到事件上
				ui.on('hide.bs.modal', function() {
					if (onCancel) {
						onCancel();
					}
				});

				ui.modal('show');
			},

			/**
			 * 模态框的 Alert
			 * 
			 * @param msg
			 */
			alert : function(msg) {
				// 避免缓存，每次都清空
				var jqueryId = "#lzUtilAlertModal";

				console.log("lzUtil.alert msg=" + msg);
				// 避免缓存，如果这个id的模式窗已经存在，则删除
				var obj = $(jqueryId);
				if (obj == null || obj.length == 0) {
					var html =
							"<div id='lzUtilAlertModal' class='modal fade' tabindex='-1' role='dialog' aria-labelledby='myModalLabel2'>"
									+ "	<div class='modal-dialog'>" + "		<div class='modal-content'>"
									+ "			<div class='modal-header'>"
									+ "				<button type='button' class='close' data-dismiss='modal' aria-hidden='true'></button>"
									+ "				<h4 class='modal-title' id='myModalLabel2'>系统提示</h4>" + "			</div>"
									+ "			<div class='modal-body'>" + "				<p id='lzUtilAlertModal_body'></p>" + "			</div>"
									+ "			<div class='modal-footer'>"
									+ "				<button data-dismiss='modal' class='btn btn-primary'>确定</button>" + "			</div>"
									+ "		</div>" + "	</div>" + "</div>";

					obj = $(html);
					$(document.body).append(obj);
				}

				$("#lzUtilAlertModal_body").text(msg);
				obj.modal('show');
			},

			ajaxSubmit : function(formId, url, successHandle) {
				console.log(formId + ".ajaxSubmit()");

				var that = this;

				var form = $("#" + formId);
				that.onBeforeAjax();
				form.ajaxSubmit({
					type : "post", // 提交方式
					dataType : "html", // 如果是要上传文件,只能用html
					url : url, // 请求url
					success : function(data) { // 提交成功的回调函数
						that.onAfterAjax();
						var res = eval("(" + data + ")");
						if (!res.success) {
							// 失败就显示失败信息
							that.showErrorForJsonResponse(res);
						} else {
							if (successHandle) {
								successHandle(res);
							}
						}
					},
					error : function(e) {
						that.onAfterAjax();
						if (e.readyState == 4) {
							// 服务器有返回内容
							var result = eval("(" + e.responseText + ")");
							that.showErrorForJsonResponse(result);
						} else {
							that.showErrorMsg("出错了! 状态码:" + e.readyState);
						}
					}
				});
			}
		};
