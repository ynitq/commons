
<!-- 导航部分 -->
<nav class="navbar navbar-default  navbar-fixed-top" role="navigation">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>

			<a class="navbar-brand"><label class="system-name">${pageTitle}-JMX</label></a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<!-- 功能菜单区域   开始  -->
			<ul class="nav navbar-nav ">
				<#list menuVo.menus as menu>
				<li${menu.classStr}><a href="${menu.url}"${menu.targetStr}>${menu.name}</a></li> </#list>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">您好：${user.username} <span
							class="caret"></span></a>
					<ul class="dropdown-menu" role="menu">
						<li><a href="logout">退出登陆</a></li>
					</ul></li>
			</ul>
		</div>
		<!--/.nav-collapse -->
	</div>
</nav>
<!-- /. 导航部分End -->

