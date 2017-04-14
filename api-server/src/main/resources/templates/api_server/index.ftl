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
<link rel="stylesheet" type="text/css" href="/webjars/bootstrap/3.3.6/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="/webjars/dropify/0.2.1/dist/css/dropify.css">
<script src="/webjars/jquery/1.11.1/jquery.min.js" type="text/javascript"></script>
<script src="/webjars/jquery-form/3.51/jquery.form.js" type="text/javascript"></script>
<script src="/webjars/dropify/0.2.1/dist/js/dropify.js" type="text/javascript"></script>
<script src="/webjars/bootstrap/3.3.6/js/bootstrap.min.js"></script>
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
	background-color:#e6e6e6;
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
			var that = $(this);
			var url = $(this).attr("action")
			console.log("post to " + url);

			that.ajaxSubmit({
				type : "post", //提交方式  
				dataType : "html", //如果是要上传文件,只能用html
				url : url, //请求url  
				success : function(data) { //提交成功的回调函数
					var res = eval("(" + data + ")");
					if (!res.success) {
						console.log("错误信息:" + res.message);
					}
					$("#testResult").text(data);
				},
				error : function(xhr, status, error) {
					$("#testResult").text("错误码:" + xhr.status + "\n" + xhr.responseText);
				},
			});
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
	});
</script>
</head>
<body>
	<nav class="navbar navbar-default" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand">API测试 ${apiServerUrl}</a>
			</div>

			<div class="navbar-collapse">
				<ul class="nav navbar-nav">
					<li><a href="/dict/manager" target="_blank">字典管理</a></li>
				</ul>
			</div>
		</div>
	</nav>
	<div id="testDiv">
		测试结果
		<pre id="testResult"></pre>
	</div>
	<div id="leftDiv">
		<div class="panel-group" role="tablist" aria-multiselectable="true">

			<#list vo.infGroup as group>
			<div class="panel panel-default">
				<a class="my-panel-heading btn-default" data-toggle="collapse" href="#coll-${group.infKey}" aria-expanded="false"
					aria-controls="#coll-${group.infKey}">${group.memo} ${group.infKey}
				</a>
				<div id="coll-${group.infKey}" class="panel-collapse collapse" role="tabpanel">
					<div class="list-group">
						<#list group.methods as m> <a href="#" onclick="show('${m.key}Div');return false" class="list-group-item">
							${m.methodKey} <#if !m.needLogin><span class="label label-danger">open</span></#if>
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
					<span class="text-info">${apiServerUrl}${vo.apiUrlPrefix}/${m.url}</span>
				</h4>
				<p>
					<#if m.needLogin> 登录用户 :
					<span class="label label-success"> ${m.webUserClasses}</span>
					${m.optId} <#else>
					<span class="label label-danger">无需登录</span>
					</#if>
				</p>
				<form method="post" action="${apiServerUrl}${vo.apiUrlPrefix}/${m.url}"
					<#if m.uploadFile>enctype="multipart/form-data"</#if> class="js_form">
					<input name="linzi_ri_token" type="hidden" value="${token}"/>
						<table width="100%" class="table">
<#list m.paramVoList as pp>
								<tr>
									<td width="120" valign="top">${pp.name}:</td>
									<td>
									<#if pp.uploadFile>
										<input name="${pp.name}" type="file" class="js_dropify" data-show-remove="false"/>
									<#else>
										<#if pp.className == "boolean">
											<input name="${pp.name}" type="checkbox" value="true" />
										<#else>
											<input name="${pp.name}" type="text" value="${pp.value}" />
										</#if>
									</#if>
										<div class="text-muted">${pp.memo}</div></td>
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
${m.defaultMockData}
		  </pre>

			</div>
		</div>
		</#list>
	</div>
</body>
</html>