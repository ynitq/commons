<!-- 左边栏 -->
<div id="div_left_attach">
	<!-- 搜索 pannel -->
	<div class="panel panel-default">
		<div class="panel-body">

			<div class="attach_div" v-for="(vo,index) in fileList">
				<template v-if="vo.row.imageFile"> <a href="javascript:" @click="fileView(index)" class="thumbnail">
					<div class="imgDiv">
						<img :alt="vo.row.memo" :src="vo.fullThumbPath" class="img-responsive center-block">
					</div>
				</a>
				<p>{{vo.row.key}}.{{vo.row.extName}}</p>
				<p>{{vo.row.imageWidth}} X {{vo.row.imageHeight}}</p>
				</template>

				<template v-else> <a href="javascript:" @click="fileView(index)" class="thumbnail">
					<div class="other-file">{{vo.row.extName}}</div>
				</a>

				<p>名字:{{vo.row.key}}.{{vo.row.extName}}</p>
				</template>
			</div>
		</div>
	</div>
</div>
<!-- /左边栏 -->

<!-- 右边栏 -->
<div id="div_main_attach">

	<!-- 编辑查看区 -->
	<div class="panel panel-default">
		<div class="panel-heading">{{fileFormPanelTitle}}</div>
		<div class="panel-body">
			<form id="form_edit_attach" role="form">

				<div class="form-group">

					<template v-if="fileFormEditMode"> <!-- 编辑模式 --> <label>当前Key</label> <input type="hidden" name="key"
						v-model="fileFormKey" />{{fileFormKey}} </template>

					<template v-else> <!-- 新建模式 --> <label>新增Key<span class="glyphicon glyphicon-asterisk required"></span></label> <input
						required="required" type="text" name="key" class="form-control" placeholder="请输入key" v-model="fileFormKey" />
					<p class="help-block">上传后，key就是文件名</p>
					</template>
				</div>

				<div class="form-group" v-show="fileFormEditMode">
					<label>文件路径</label> <a target="_blank" :href="fileFormUrl">点击查看</a>
					<pre>{{fileFormUrl}}</pre>
				</div>

				<div class="form-group">
					<label>请选择附件文件</label>
					<input type="file" name="file" id="input_attach_file" data-height="100" />
					<p class="help-block" v-if="fileFormEditMode">如果选择了新的文件，原来的文件将会被替换</p>
					<p class="help-block" v-else>必须选择一个文件上传</p>
				</div>

				<div class="form-group">
					<label>备注</label>
					<textarea class="form-control" rows="2" name="memo" placeholder="请输入备注" v-model="fileFormMemo"></textarea>
				</div>

				<template v-if="fileFormEditMode"> <!-- 编辑模式 -->
				<div class="form-group">
					<button type="submit" class="btn btn-success w100">保存</button>
					<button type="button" class="btn btn-danger w100" @click="fileDelete(fileFormKey)">删除</button>
				</div>
				<hr />
				<div class="form-group">
					<button type="button" @click="fileChangeToNewMode" class="btn btn-warning w100">新建Key</button>
				</template>

				<template v-else> <!-- 新增模式 -->
				<div class="form-group">
					<button type="submit" class="btn btn-success w100">新增</button>
				</div>
				</template>

			</form>
		</div>
	</div>
	<!-- /编辑查看区 -->
</div>
