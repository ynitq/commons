<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>${pageTitle} - 字典管理</title>
<meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no, minimal-ui">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.bootcss.com/toastr.js/2.1.3/toastr.min.css" rel="stylesheet">
<link href="https://cdn.bootcss.com/Dropify/0.2.2/css/dropify.min.css" rel="stylesheet">
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

<script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/jquery.form/4.2.1/jquery.form.min.js"></script>
<script src="https://cdn.bootcss.com/Dropify/0.2.2/js/dropify.min.js"></script>
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script src="https://cdn.bootcss.com/toastr.js/latest/toastr.min.js"></script>
<script src="https://cdn.bootcss.com/vue/2.2.6/vue.min.js"></script>

<!-- 自己的js -->
<script src="/dict/static/linzi-util.js" type="text/javascript"></script>
<script src="/dict/static/dict.js" type="text/javascript"></script>

</html>