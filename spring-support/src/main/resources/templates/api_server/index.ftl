<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta http-equiv="Cache-Control" content="must-revalidate" />
<title>${pageTitle} - API测试</title>
<link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.bootcss.com/Dropify/0.2.2/css/dropify.min.css" rel="stylesheet">
<link href="https://cdn.bootcss.com/toastr.js/2.1.3/toastr.min.css" rel="stylesheet">

<script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/Dropify/0.2.2/js/dropify.min.js"></script>
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

</head>
<style>
.msgWin {
	width: 200px;
}

#testFrame {
	margin: 0xp;
	padding: 0px;
	border: 0px;
	width: 100%;
	height: 100%;
	overflow: scroll;
}

#testDiv {
	position: absolute;
	top: 55px;
	left: 835px;
	bottom: 10px;
	margin: 10px;
	right: 5px;
	min-width: 200px;
	overflow: auto;
}

#leftDiv {
	position: absolute;
	top: 55px;
	left: 5px;
	bottom: 10px;
	margin: 5px;
	width: 320px;
	overflow: auto;
}

#descDiv {
	position: absolute;
	top: 55px;
	left: 330px;
	bottom: 10px;
	margin: 5px;
	width: 500px;
	overflow: auto;
}

div.myFrom {
	margin: 5px;
}

td {
	padding-top: 2px;
}

.w-m100 {
	min-width: 100px;
}

.high {
	color: #0000ff;
}

pre {
	overflow: auto;
	white-space: pre-wrap;
	white-space: -moz-pre-wrap;
	white-space: -pre-wrap;
	white-space: -o-pre-wrap;
	word-wrap: break-word;
}



.my-panel-heading {
	background-color: #e6e6e6;
	border-bottom: 1px solid #ddd;
	line-height: 20px;
	padding-bottom: 10px;
	padding-left: 15px;
	padding-right: 15px;
	padding-top: 10px;
	display: block;
}

a:HOVER {
	text-decoration: none;
}

a:LINK {
	text-decoration: none;
}

a:VISITED {
	text-decoration: none;
}
</style>
<script>
	var lastObj = null;
	function show(id) {
		var obj = document.getElementById(id);

		if (obj != null) {
			if (lastObj != null) {
				lastObj.style.display = "none";
			}

			if (obj.style.display == "none") {
				obj.style.display = "block";
				lastObj = obj;
			} else {
				obj.style.display = "none";
			}
		}
	};

	$(document).ready(function() {
		$(".js_form").submit(function() {
			try {
				var that = $(this);
				var url = $(this).attr("data-url")
				console.log("post to " + url);

				var formData = new FormData(that[0]);

				$.ajax({
					type : "post", //提交方式  
					dataType : "json", //如果是要上传文件,只能用html
					data : formData,
					url : url, //请求url
					contentType : false, // 当有文件要上传时，此项是必须的，否则后台无法识别文件流的起始位置(详见：#1)
					processData : false, // 是否序列化data属性，默认true(注意：false时type必须是post，详见：#2)
					success : function(data) { //提交成功的回调函数
						//var res = eval("(" + data + ")");
						var res = data;
						if (!res.success) {
							console.log("错误信息:" + res.message);
						}
						$("#testResult").text("url:" + url + "\n返回结果:\n" + JSON.stringify(data, null, 4));
					},
					error : function(xhr, status, error) {
						$("#testResult").text("错误码:" + xhr.status + "\n" + xhr.responseText);
					},
				});
			} catch (ex) {
				console.log("ajax 错误", ex)
			}
			return false;
		});

		$(".js_dropify").dropify({
			messages : {
				'default' : '点击或拖拽文件到这里',
				'replace' : '点击或拖拽文件到这里来替换文件',
				'remove' : '移除文件',
				'error' : '对不起，你上传的文件太大了'
			}
		});

		$("#js_clear").click(function() {
			$("#testResult").text('')
		})

		$('[data-toggle="tooltip"]').tooltip()

		$(".js_add_input").click(function() {
			var div = $(this).parent().clone();

			var icon = div.find(".glyphicon-plus")
			icon.removeClass("glyphicon-plus");
			icon.addClass("glyphicon-minus");

			div.find("a").click(function() {
				$(this).parent().remove();
			})

			var parent = $(this).parent().parent();
			console.debug('点击增加')
			parent.append(div);
		})
	});
</script>
</head>
<body>
	<nav class="navbar navbar-default" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand">${pageTitle} - API测试 </a>
			</div>

			<div class="navbar-collapse">
				<ul class="nav navbar-nav">
					<li><a href="/dict/manager" target="_blank">字典管理</a></li>
					<li><a href="/jmxInWeb/" target="_blank">JMX</a></li>
				</ul>
			</div>
		</div>
	</nav>

	<div id="testDiv">
		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#tab-test-result" aria-controls="tab-test-result" role="tab"
				data-toggle="tab">测试结果</a></li>
			<li role="presentation"><a href="#tab-all-json" aria-controls="tab-all-json" role="tab" data-toggle="tab">所有接口</a></li>
		</ul>

		<!-- Tab panes -->
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="tab-test-result">
				<div style="margin: 5px">
					<a class='btn btn-default btn-sm' id="js_clear" ><i class="glyphicon glyphicon-trash"></i> 清空</a>
				</div>
				<div style="clear: both;">
					<pre id="testResult"></pre>
				</div>
			</div>
			<div role="tabpanel" class="tab-pane" id="tab-all-json">
			<pre>
<#list vo.infGroup as group>
	/** ${group.memo} */
	${group.infKey}: {
<#list group.methods as m> 
		${m.methodKey}: '${vo.apiUrlPrefix}/${m.url}', //  ${m.memo}
</#list>
	},
</#list>
			</pre>
			</div>
		</div>
	</div>

	<div id="leftDiv">
		<div class="panel-group" role="tablist" aria-multiselectable="true">

			<#list vo.infGroup as group>
			<div class="panel panel-default">
				<a class="my-panel-heading btn-default" data-toggle="collapse" href="#coll-${group.infKey}" aria-expanded="false"
					aria-controls="#coll-${group.infKey}">${group.memo} ${group.infKey} </a>
				<div id="coll-${group.infKey}" class="panel-collapse collapse" role="tabpanel">
					<div class="list-group">
						<#list group.methods as m> <a href="#" onclick="show('${m.key}Div');return false" class="list-group-item">
							${m.methodKey} <#if !m.needLogin> <span class="label label-danger pull-right">open</span><#else><span class="label label-default pull-right">${m.optId}</span></#if>
						</a> </#list>
					</div>
				</div>
			</div>
			</#list>
		</div>
	</div>

	<div id="descDiv">
		<#list vo.methods as m>
		<div id="${m.key}Div" style="display: none" class="panel panel-default">
			<div class="panel-heading">${m.memo} ${m.url}</div>
			<div class="panel-body">
				<h4>
					URL :
					<span class="text-info">${vo.apiUrlPrefix}/${m.url}</span>
				</h4>
				<p>
					<#if m.needLogin> 登录用户 : <span class="label label-success"> ${m.webUserClasses}</span> ${m.optId} <#else> <span
						class="label label-danger">无需登录</span></#if>
				</p>
				<form method="post" data-url="${vo.apiUrlPrefix}/${m.url}"
					<#if m.uploadFile>enctype="multipart/form-data"</#if>
					class="js_form">
					<table width="100%" class="table">
						<#list m.paramVoList as pp>
						<tr>
							<td width="120" valign="top"><#if pp.notNull> <span class="text-danger">*</span></#if><a data-toggle="tooltip"
								data-placement="right" title="${pp.className}">${pp.name}</a> <#if pp.array> []</#if></td>
							<td><#if pp.uploadFile> <input name="${pp.name}" type="file" class="js_dropify" data-show-remove="false" /> <#else>
								<#if pp.checkBox> <input name="${pp.name}" type="checkbox" value="true" /> <#else>
								<div style="padding-bottom: 2px;">
									<input name="${pp.name}" type="text" value="${pp.value}"
									<#if pp.notNull>required="required"</#if>
									/>
									<#if pp.array> <a class="btn btn-xs btn-default js_add_input"><i class="glyphicon glyphicon-plus"></i></a></#if>
								</div></#if></#if> <span class="text-muted">${pp.memo}</span></td>
						</tr>
						</#list>
						<tr>
							<td>&nbsp;</td>
							<td>
								<button class="btn btn-primary w-m100" type="submit">测试</button>
							</td>
						</tr>
					</table>
				</form>

				<hr />
				<h4>返回的结果</h4>
				<span class="text-info">${m.returnClass.name}</span>
				<pre>
${m.returnClassDesc}
		  </pre>

			</div>
		</div>
		</#list>
	</div>

</body>
</html>