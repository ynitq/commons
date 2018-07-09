<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>登录JMX-${pageTitle}</title>
<meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no, minimal-ui">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<#include "include/style"/>
</head>

<body>
	<nav class="navbar navbar-default" role="navigation">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand"><label class="system-name">${pageTitle}-JMX</label></a>
			</div>
			<!-- /.navbar-collapse -->
		</div>
		<!-- /.container-fluid -->
	</nav>

	<div class="container-fluid">
		<div class="row">
			<div class="col-md-4 col-md-offset-4">
				<!-- 登录 pannel -->
				<div class="panel panel-default">
					<div class="panel-heading">用户登录</div>
					<div class="panel-body">
						<form id="login_form" method="post">
							<div class="form-group">
								<label>账号</label>
								<input type="text" required="required" name="account" class="form-control" placeholder="请输入用户名">
							</div>
							<div class="form-group">
								<label>密码</label>
								<input type="password" required="required" name="password" class="form-control" placeholder="请输入密码">
							</div>
							<div class="form-group">
								<label>保持登录 <input type="checkbox" name="rememberMe" value="yes" checked="checked"></label>
							</div>
							<input type="submit" name="submit" class="btn btn-info" value="登录" />
						</form>
					</div>
				</div>
				<!-- /登录 pannel -->
			</div>


		</div>
	</div>

	<#include "include/js"/>
	<!-- 自己的js -->
	<script>
		$(document).ready(function() {
			// 页面初始化
			console.log("初始化 登录表单");
			$("#login_form").submit(function() {

				var param = $(this).serialize();
				var url = "/api/jmxInWeb/login";

				console.log("登录", param);

				lzUtil.ajax(url, param, function(res) {
					window.location.href = "/jmxInWeb/";
				});

				return false;
			});
		});
	</script>
</body>
</html>
