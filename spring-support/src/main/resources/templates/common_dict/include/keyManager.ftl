<!-- 左边栏 -->
<div id="div_left">
	<!-- 搜索 pannel -->
	<div class="panel panel-default searchPanel">
		<div class="panel-heading">
			<form role="form" id="form_seach">
				<div class="input-group">
					<input type="text" class="form-control" name="key" placeholder="请输入要搜索的key" v-model="pageBar.searchKey">
					<span class="input-group-btn">
						<input type="submit" name="submit" class="btn btn-default" value="搜索" />
					</span>
					<span class="input-group-btn">
						<button id="btn_refresh" class="btn btn-default" title="刷新">
							<i class="glyphicon glyphicon-refresh"></i>
						</button>
					</span>
				</div>
			</form>
		</div>
		<div class="panel-body">
			<table class="table table-bordered table-hover">
				<thead>
					<tr>
						<th width="200">KEY</th>
						<th>内容</th>
						<th width="50">使用</th>
					</tr>
				</thead>
				<tbody>
					<tr class="link" v-for="(row,index) in list" @click="view(index)">
						<td><i class="glyphicon glyphicon-info-sign text-danger" v-if="row.todo" title="这个是页面上新出现的key，需要填写内容"></i> <span
								v-html="row.keyWithMark"></span></td>
						<td>{{row.valueSummary}}</td>
						<td>{{row.usedCount}}</td>
					</tr>
				</tbody>
			</table>
		</div>

		<div class="panel-footer">
			<nav v-show="pageBar.show">
				<ul class="pagination pagination-sm">
					<li :class="{disabled : !pageBar.hasPrev}"><a @click="prevPage">&laquo;</a></li>
					<li><span>{{pageBar.pageNo}}/{{pageBar.pageTotal}}</span></li>
					<li :class="{disabled : !pageBar.hasNext}"><a @click="nextPage">&raquo;</a></li>
				</ul>
			</nav>
		</div>
	</div>
	<!-- 搜索 pannel -->

</div>
<!-- /左边栏 -->

<!-- 右边栏 -->
<div id="div_main">

	<!-- 编辑查看区 -->
	<div class="container-fluid">

		<!-- 标签导航 -->
		<ul class="nav nav-tabs" role="tablist" id="tab_nav">
			<li role="presentation" class="active"><a href="#div_tab_import" role="tab" data-toggle="tab">导入/导出</a></li>
			<li role="presentation"><a href="#div_tab_edit" role="tab" data-toggle="tab">{{tabTitle}}</a></li>
			<li role="presentation"><a href="#div_tab_preview" role="tab" data-toggle="tab">预览</a></li>
		</ul>
		<!-- /标签导航 -->

		<!-- 所有标签的容器 -->
		<div class="tab-content">

			<!-- 编辑标签页 -->
			<div role="tabpanel" class="tab-pane fade" id="div_tab_edit">

				<div class="panel panel-default">
					<div class="panel-heading">编辑Key:</div>
					<div class="panel-body">
						<form class="form-horizontal" id="form_edit" role="form">

							<div class="form-group">

								<template v-if="editMode"> <!-- 编辑模式 --> <label class="col-sm-2 control-label">当前Key</label>
								<div class="col-sm-8">
									<p class="form-control-static">{{editRow.key}}</p>
									<input type="hidden" name="key" v-model="editRow.key" />
									<p class="help-block">已经使用过 {{editRow.usedCount}} 次</p>
								</div>
								</template>

								<template v-if="!editMode"> <!-- 新建模式 --> <label class="col-sm-2 control-label">新增Key<span
										class="glyphicon glyphicon-asterisk required"></span></label>
								<div class="col-sm-9">
									<input type="text" name="key" class="form-control" placeholder="请输入key" v-model="editRow.key" />
								</div>
								</template>

							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label">Key的内容<span class="glyphicon glyphicon-asterisk required"></span>
								</label>
								<div class="col-sm-9">
									<textarea class="form-control" rows="4" name="value" placeholder="请输入key的内容" v-model="editRow.value" required="required"></textarea>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">备注</label>
								<div class="col-sm-9">
									<textarea class="form-control" rows="2" name="memo" placeholder="请输入备注" v-model="editRow.memo"></textarea>
								</div>
							</div>
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-9">
									<div class="checkbox">
										<label> <input type="radio" name="type" v-model="editRow.type" value="2" checked="{{editRow.type === 2}}"/> 
										内容中有html代码
										</label>
										<label style="color:red;">&nbsp;&nbsp;&nbsp;&nbsp;如果希望在内容中自己写HTML代码，请勾选该选项，一般情况下，我们不建议内容中带有HTML代码</label>
									</div>
									
									<div class="checkbox">
										<label> <input type="radio" name="type" v-model="editRow.type" value="3" checked="{{editRow.type === 3}}"/> 内容中有MarkDown代码
										</label>
									</div>
									
									<div class="checkbox">
										<label> <input type="radio" name="type" v-model="editRow.type" value="1" checked="{{editRow.type === 1 || editRow.type === 0}}"/> 内容中只有文本
										</label>
									</div>
								</div>
							</div>

							<template v-if="editMode"> <!-- 编辑模式 -->
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-9">
									<button type="submit" class="btn btn-success w100">保存</button>
									<button type="button" class="btn btn-danger w100" @click="deleteKey(editRow.key)">删除</button>
								</div>
							</div>
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-9">
									<p class="help-block">当程序员在页面中有key时，即使我们删除了，也会被自动增加。所以如果程序员没有修改页面，删除也是白删除。但我们可以删除那些使用次数为0的。</p>
								</div>
							</div>
							<hr />
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10">
									<button type="button" @click="newRow" class="btn btn-warning w100">新建Key</button>
								</div>
							</div>
							</template>

							<template v-if="!editMode"> <!-- 新增模式 -->
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10">
									<button type="submit" class="btn btn-success w100">新增</button>
								</div>
							</div>
							</template>

						</form>
					</div>
				</div>
			</div>
			<!-- /编辑标签页 -->

			<!-- 导入标签页 默认展现的是这个标签-->
			<div role="tabpanel" class="tab-pane fade active in" id="div_tab_import">
				<div class="panel panel-default">
					<div class="panel-heading">
						导入和导出 <a href="/dict/dictJs.js" class="pull-right" target="_blank">用于客户端的js文件</a>
					</div>
					<div class="panel-body">
						<form class="form-horizontal" id="form_import" role="form">
							<div class="form-group">
								<label for="input_import_file" class="col-sm-2 control-label">Xml文件<span
										class="glyphicon glyphicon-asterisk required"></span></label>
								<div class="col-sm-8">
									<input required="required" type="file" name="file" id="input_import_file" data-allowed-file-extensions="xml"
										data-height="100" />
								</div>
							</div>

							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10">
									<div class="checkbox">
										<label> <input type="checkbox" name="cleanOld" />导入时清除原来的所有内容
										</label>
									</div>
								</div>
							</div>
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10">
									<button type="submit" class="btn btn-success">从XML文件导入</button>
								</div>
							</div>
							<hr />
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10">
									<p>从上传的文件导入数据之前，建议先导出一次作为备份。请用"右键另存为"来保存文件。</p>
									<a href="/dict/export/dict.xml" target="_blank" class="btn btn-info">导出xml文件</a>
								</div>
							</div>
							<hr />
							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10">
									<p>当程序在页面中添加了Key以后，只要打开过一次那个页面，这个Key就会被自动添加，所以我们一般不需要自己手动添加，但我们也可以先添加了，然后通知程序员使用。</p>
									<button type="button" @click="newRow" class="btn btn-warning w100">新建Key</button>
								</div>
							</div>

						</form>
					</div>
				</div>


			</div>
			<!-- /导入标签页 -->
			
			<!-- 预览标签页 -->
			<div role="tabpanel" class="tab-pane fade" id="div_tab_preview" v-html="editRow.preview">
			</div>
			<!-- /导入标签页 -->

		</div>
		<!-- /所有标签的容器 -->

	</div>
	<!-- /编辑查看区 -->
</div>
