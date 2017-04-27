/** 对应mbeanInfo.ftl */

$(function() {
	mbeanInfo.init();// 初始化控件
});

var mbeanInfo = {
	dateFormat : 'yyyy-mm-dd hh:ii',

	apiUrlGetMBeanInfo : " /api/jmxInWeb/getMBeanInfo",// 获取的mbean信息
	apiUrlChangeAttr : " /api/jmxInWeb/changeAttr",// 更改属性的值
	apiUrlInvokeOpt : " /api/jmxInWeb/invokeOpt",// 调用一个方法

	attrListVm : {},// 属性列表的model

	objectName : "",// 当前MBean的object name;

	/** 初始化 */
	init : function() {
		this.initAttrPanel(); // 属性部分

		this.initOptForm(); // opt部分

		this.loadAttr(); // 加载属性列表
	},

	/** 初始化 输入各类操作参数的表单 */
	initOptForm : function() {
		var that = this;

		$(".js_form_datetime").datetimepicker({
			// autoclose: true,
			format : that.dateFormat,
		// pickerPosition: "bottom-left"
		});

		$('.js_boolean_checkbox').click(function(e) {
			var name = "#" + $(this).attr('data-name');
			var checked = $(this).is(':checked');
			console.log(name + ":" + checked);

			var value = "" + checked;

			$(name + "_txt").text(value);
			$(name + "_input").val(value);
		});

		/**
		 * 执行opt
		 */
		$('.js_invoke_opt').click(function(e) {
			var formName = "#" + $(this).attr('data-form-name');

			var form = $(formName);
			var param = form.serialize();
			lzUtil.ajax(that.apiUrlInvokeOpt, param, function(res) {
				lzUtil.showMsg("执行成功");

				if (res.hasReturn) {
					$("#invodeResult_body").text(res.returnData);
					$("#invokeResult_modal").modal();
				}
				that.loadAttr();
			});

		});
	},

	initAttrPanel : function() {
		var that = this;

		/** 修改属性的链接 */
		var attrValueComp = {
			// 声明 props
			props : {
				'attrVo' : { // 注意：外面传进来时，要用attr-vo
					type : Object,// 类型是对象
					required : true, // 这个属性是必须的
				},
			},

			template : '#attr_value-template',

			mounted : function() {
				// 当这个属性链接对象创建成功时，我们需要激活 bootstrap-editable
				// console.log("mounted", this.$el, this.attrVo);
				that.bindEditable($(this.$el), this.attrVo);
			},

			updated : function() {
				// 在更新时，需要重新绑一下editable,否则页面上的值不刷新
				that.bindEditable($(this.$el), this.attrVo);
			},
		};

		this.attrListVm = new Vue({
			el : '#list_table_attr',

			components : {
				'attr-value' : attrValueComp, // 修改属性的链接
			},

			methods : {
				reload : function() {
					console.log("刷新属性")
					that.loadAttr();
					lzUtil.showMsg("刷新属性");
				},
			},

			data : {
				attrs : [],// 属性列表
			},
		});

	},

	/** 为链接增加 editable */
	bindEditable : function(el, attrVo) {
		var that = this;
		var option = {
			url : function(params) {
				var d = new $.Deferred();

				params.objectName = that.objectName;

				console.log("属性被修改了", params);

				lzUtil.ajax(that.apiUrlChangeAttr, params, function(res) {
					d.resolve(); // 变成非等待状态
					lzUtil.showMsg("修改完成");
				});

				return d.promise();
			},
			success : function() {
				//修改完成后，刷新所有的属性
				that.loadAttr();
			},
			type : 'textarea',
			mode : 'inline',
			value : '', // 初始值为空
			pk: that.objectName + attrVo.info.name,
			display : false,// 默认修改完成后不修改界面上的值
		}

		var valueType = attrVo.info.type;
		if (valueType == 'boolean' || valueType == 'java.lang.Boolean') {
			option.type = 'select';
			option.source = [ {
				value : 'false',
				text : 'false'
			}, {
				value : 'true',
				text : 'true'
			} ];
			option.showbuttons = false;
		} else if (valueType == 'java.util.Date') {
			option.type = 'datetime';
			option.format = 'yyyy-mm-dd hh:ii';
			option.viewformat = 'yyyy-mm-dd hh:ii';
			option.escape = true;
		}

		if (attrVo.info.readable) {
			// 如果该属性可读
			option.value = attrVo.value;
			option.display = null;
		}

		el.editable(option);
	},

	/** 初始化时，加载该项目列表 */
	loadAttr : function() {
		var that = this;
		console.log("加载属性列表 objectName=", that.objectName);

		var param = {
			objectName : that.objectName
		};

		lzUtil.ajax(that.apiUrlGetMBeanInfo, param, function(res) {
			that.attrListVm.attrs = res.info.attrs;
		});
	},

};
