<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="initial-scale=1, maximum-scale=1">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<title>出错了</title>
<link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
</head>
<style>
.errorPage {
	margin-top: 100px;
	font-size: 24px;
}

.error-code {
	line-height: 176px;
	font-size: 160px;
	font-weight: 500px;
}

.error-message {
	font-size: 18px;
	color: #565656;
}

.stackTrace {
	overflow: auto;
	white-space: pre-wrap;
	white-space: -moz-pre-wrap;
	white-space: -pre-wrap;
	white-space: -o-pre-wrap;
	word-wrap: break-word;
}

.showErrorBtn {
	position: absolute;
	top: 5px;
	right: 5px;
}

#errorDiv {
	position: absolute;
	top: 40px;
	bottom: 5px;
	left: 5px;
	right: 5px;
	overflow: auto;
}
</style>
<script type="text/javascript">
	function swapErrorDiv() {
		var obj = document.getElementById("errorDiv");
		if (obj.style.display == "none") {
			obj.style.display = "block";
		} else {
			obj.style.display = "none";
		}
	}
</script>
</head>
<body>

	<section class="container">
		<div class="row errorPage">
			<div class="col-md-6 col-md-offset-3">
				<div class="text-center">
					<header>
						<p class="text-warning">出错了!</p>
						<h1 class="error-code">${errorInfo.statusCode}</h1>
						<p class="error-message">
							<#if errorInfo.statusCode==404> 世界上最遥远的距离，不是生与死的距离，而是我在深情地望着电脑屏幕，却再也寻找不到你的身影。 <#else> <#if
								errorInfo.apiException> ${errorInfo.message} <#else> 抱歉，系统发生了内部错误，我们的工程师正在努力排查中，请稍后再试。 </#if></#if>
						</p>
					</header>
					<div class="row">
						<div class="col-md-6 col-md-offset-3">
							<button class="btn btn-success" onclick="history.back();">返回上一页</button>
						</div>
					</div>
				</div>
			</div>

		</div>
	</section>

	<#if errorInfo.canShowException>
	<div class="showErrorBtn">
		<button class="btn btn-default btn-sm" type="button" onclick="swapErrorDiv()">
			显示/隐藏 错误信息
			<span class="caret"></span>
		</button>
	</div>
	<div class="container-fluid" id="errorDiv" style="display: none">
		<pre class="stackTrace">${errorInfo.stackTrace}</pre>
	</div>
	</#if>
</body>
</html>