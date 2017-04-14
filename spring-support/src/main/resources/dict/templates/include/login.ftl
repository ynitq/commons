
<!-- 左边栏 -->
<div id="div_left">
	<!-- 登录 pannel -->
	<div class="panel panel-default">
		<div class="panel-heading">用户登录</div>
		<div class="panel-body">
			<form id="form_login">
				<div class="form-group">
					<label>手机号</label>
					<input type="text" name="account" class="form-control" placeholder="请输入用户名" value="admin">
				</div>
				<div class="form-group">
					<label>密码</label>
					<input type="password" name="password" class="form-control" placeholder="请输入密码">
				</div>
				<div class="form-group">
					<label>保持登录 <input type="checkbox" name="rememberMe" value="yes"></label>
				</div>
				<input type="submit" name="submit" class="btn btn-info" value="登录" />
			</form>
		</div>
	</div>
	<!-- /登录 pannel -->
</div>
<!-- /左边栏 -->

<!-- 右边栏 -->
<div id="div_main">
	<div class="container-fluid">
		<div class="panel panel-default">
			<div class="panel-heading">使用说明</div>
			<div class="panel-body">
				<div>
					<h4>关于这个系统</h4>
					<p class="text-muted">这个是管理系统中所有文字的独立管理系统，由林资产品部人员使用。</p>
				</div>
				<hr />

				<#if adminInProp> <!-- 用配置文件来管理账号 -->
				<div>
					<div>
						<h4>TO 程序员:</h4>
						<p class="text-muted">密码存储与配置文件中，请程序员修改以下两个参数配置管理员账号</p>
						<ul>
							<li>dict.admin.account</li>
							<li>dict.admin.password</li>
						</ul>
						<p class="text-muted">也可以用数据库管理，只需要一个实现了IUserServiceForRememberMe接口的类，这个类需要支持DictAdminWebUser这个类型用户的管理</p>
					</div>
					<hr />

					<div>
						<h4>TO 产品部:</h4>
						<p class="text-muted">请在下面的输入框中输入密码，生成加密后的密码，然后通知程序员将这个密文放到配置文件中，这样就可以用这个密码登陆了。默认的用户名是admin</p>
						<form id="form_create_password" class="form-horizontal">
							<div class="form-group">
								<label class="col-sm-2 control-label">密码明文<span class="glyphicon glyphicon-asterisk required"></span></label>
								<div class="col-sm-9">
									<input type="text" name="password" class="form-control" placeholder="请输入要生成密文的密码明文" />
								</div>
							</div>

							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10">
									<button type="submit" class="btn btn-warning w100">创建密文</button>
								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label">生成结果:</label>
								<div class="col-sm-9">
									<p class="form-control-static">{{encodedPwd}}</p>
								</div>
							</div>

						</form>
					</div>
				</div>
				<!-- /用配置文件来管理账号 --> <#else> <!-- 用类来管理账号 --> 请使用系统管理员账号登陆 <!-- /用类来管理账号 --></#if>
			</div>
		</div>
	</div>
</div>
<!-- 右边栏 -->