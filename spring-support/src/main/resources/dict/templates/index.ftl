<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>${pageTitle} - 字典管理</title>
<meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no, minimal-ui">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<link rel="stylesheet" type="text/css" href="/webjars/bootstrap/3.3.6/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="/webjars/dropify/0.2.1/dist/css/dropify.css">
<link rel="stylesheet" type="text/css" href="/dict/static/lib/toastr/2.1.2/build/toastr.min.css">
<link rel="stylesheet" type="text/css" href="/dict/static/dict.css">
</head>

<body>
	<div id="my_page" style="display: none">

		<!-- 导航栏 -->
		<nav class="navbar navbar-default" role="navigation">
			<div class="container-fluid">
				<div class="navbar-header">
					<a class="navbar-brand">${pageTitle} 页面字典管理</a>
				</div>

				<!-- 登陆后才有的内容 -->
				<div v-show="logined">
					<!-- 菜单-->
					<ul class="nav navbar-nav">
						<li :class="{ active: curPage=='keyManager' }"><a href="javascript:" @click="swithToKey">文字管理</a></li>
						<li :class="{ active: curPage=='attachment' }"><a href="javascript:" @click="swithToAttachment">附件管理</a></li>
					</ul>
					<!-- /菜单 -->
					<div class="navbar-right">
						<p class="navbar-text ">当前用户: {{account}}</p>
						<button type="button" class="btn btn-default navbar-btn" v-on:click="logout">退出登录</button>
					</div>
				</div>
				<!-- /登陆后才有的内容 -->
			</div>
		</nav>
		<!-- /导航栏 -->

		<div class="main-box">
			<div v-show="!logined">
				<#include "include/login"/>
			</div>

			<div v-show="logined">
				<div v-show="curPage=='keyManager'">
					<!-- 文字管理 -->
					<#include "include/keyManager"/>
				</div>
				<div v-show="curPage=='attachment'">
					<!-- 附件管理 -->
					<#include "include/attachmentManager"/>
				</div>
			</div>
		</div>
	</div>

	<div id="div_loading" style="display: none;">
		<div class="loading_bg"></div>
		<div class="loading_text">加载数据中...</div>
	</div>
</body>

<!-- wabjar库 -->
<script src="/webjars/jquery/1.11.1/jquery.min.js" type="text/javascript"></script>
<script src="/webjars/jquery-form/3.51/jquery.form.js" type="text/javascript"></script>
<script src="/webjars/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<script src="/webjars/dropify/0.2.1/dist/js/dropify.js" type="text/javascript"></script>

<!-- 本地库 -->
<script src="/dict/static/lib/toastr/2.1.2/build/toastr.min.js"></script>
<script src='/dict/static/lib/vue/2.0.5/vue.min.js'></script>

<!-- 自己的js -->
<script src="/dict/static/linzi-util.js" type="text/javascript"></script>
<script src="/dict/static/dict.js" type="text/javascript"></script>

</html>