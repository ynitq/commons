<!doctype html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>MBean信息-${pageTitle}</title>
<#include "include/style"/>
<link rel="stylesheet" type="text/css" href="/dict/static/lib/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" />
<link href="https://cdn.bootcss.com/x-editable/1.5.1/bootstrap3-editable/css/bootstrap-editable.css" rel="stylesheet">

<script type="text/x-template" id="attr_value-template">
		<a 	:data-name="attrVo.info.name" data-rows="4"
			:data-readable="attrVo.info.readable">
			<span v-if="attrVo.info.readable" v-text="attrVo.value"></span>
			<span v-else>修改</span>
		</a>
</script>

</head>
<body>

	<#include "include/header"/>

	<div class="main-content">
		<!-- MBean 基础信息 -->
		<div class="panel panel-default">
			<div class="panel-heading">类: ${mbean.className}</div>
			<div class="panel-body">
				<p>
					<b>Object name: </b>${mbean.objectName}
				</p>
				<p>
					<b>说明: </b> ${mbean.desc}
				</p>
			</div>
		</div>
		<!-- /MBean 基础信息 -->

		<!-- 属性列表 vue-->
		<div class="panel panel-default" id="list_table_attr">
			<div class="panel-heading">
				属性
				<button class="btn btn-sm pull-right btn-default" @click="reload">刷新</button>
			</div>
			<div class="panel-body">
				<table class="table table-light table-hover">
					<thead>
						<tr>
							<th width="250">名字</th>
							<th width="150">类型</th>
							<th width="300">说明</th>
							<th>值</th>
						</tr>
					</thead>
					<tbody>
						<tr v-for="attrVo in attrs">
							<td><span class="caption">{{attrVo.info.name}}</span> <span class="label label-warning"
									v-if="attrVo.info.writable && !attrVo.info.readable">只写</span> <span class="label label-success"
									v-if="!attrVo.info.writable">只读</span></td>
							<td>{{attrVo.info.type}}</td>
							<td>{{attrVo.desc}}</td>
							<td class="attr_value">
								<div v-if="!attrVo.info.writable || !attrVo.inputable">
									<pre v-if="attrVo.jsonValue" v-html="attrVo.value"></pre>
									<span v-if="!attrVo.jsonValue" v-html="attrVo.value"></span>
								</div> <attr-value :attr-vo="attrVo" v-if="attrVo.info.writable && attrVo.inputable"></attr-value>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		<!-- /属性列表 -->

		<!-- opt列表 -->
		<div class="panel panel-default">
			<div class="panel-heading">操作</div>
			<div class="panel-body">
				<table class="table  table-bordered">
					<thead>
						<tr>
							<th width="200">名字</th>
							<th width="150">返回类型</th>
							<th>说明</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<#assign i=1> <#list mbean.opts as optVo>
						<form id="form_invoke_${i}">
							<input type="hidden" name="optName" value="${optVo.info.name}" />
							<input type="hidden" name="objectName" value="${mbean.objectName}" />
							<#if optVo.noParam>
							<tr>
								<td><span class="caption">${optVo.info.name}</span></td>
								<td>${optVo.info.returnType}</td>
								<td>${optVo.info.description}</td>
								<td><btn class="btn btn-xs btn-info js_invoke_opt" data-form-name="form_invoke_${i}">执行</btn></td>
							</tr>
							<#else>
							<tr>
								<td rowspan="2"><span class="caption">${optVo.info.name}</span></td>
								<td>${optVo.info.returnType}</td>
								<td>${optVo.info.description}</td>
								<td></td>
							</tr>
							<tr>
								<td colspan="3"><table class="table table-bordered table-hover">
										<thead>
											<tr role="row" class="heading">
												<th>名字</th>
												<th>说明</th>
												<th>类型</th>
												<th></th>
											</tr>
										</thead>
										<tbody>
											<#list optVo.params as paramVo>
											<tr role="row" class="filter">
												<input type="hidden" name="paramType" value="${paramVo.info.type}" />
												<td>${paramVo.info.name}</td>
												<td>${paramVo.info.description}</td>
												<td><span class="label label-sm label-success label-mini"> ${paramVo.info.type}</span></td>
												<td><#if paramVo.info.type=='boolean'> <label> <input type="checkbox"
															data-name="p_${i}_${paramVo.id}" class="js_boolean_checkbox"> <span id="p_${i}_${paramVo.id}_txt">false</span>
													</label> <input type="hidden" name="paramValue" value="false" id="p_${i}_${paramVo.id}_input"> <#elseif
														paramVo.info.type=='java.util.Date'>
													<div class="input-group date js_form_datetime">
														<input type="text" size="16" readonly class="form-control" name="paramValue">
														<span class="input-group-btn">
															<button class="btn btn-default date-set" type="button">
																<i class="glyphicon glyphicon-calendar"></i>
															</button>
															<button class="btn btn-default date-reset" type="button">
																<i class="glyphicon glyphicon-remove"></i>
															</button>
														</span>
													</div>
													<#else> <input type="text" class="form-control form-filter input-sm" name="paramValue"
														value="${paramVo.defaultValue}"></#if></td>
											</tr>
											</#list>
											<tr>
												<td colspan="4"><btn class="btn btn-xs btn-info js_invoke_opt" data-form-name="form_invoke_${i}">执行</btn></td>
											</tr>
										</tbody>

									</table></td>
							</tr></#if>
							<#assign i=i+1>
						</form>
						</#list>
					</tbody>

				</table>
			</div>
		</div>
		<!-- /opt列表 -->

		<!-- /main-content -->
	</div>

	<!-- INVOKE RESULT MODAL BEGIN -->
	<div class="modal fade bs-modal-lg" id="invokeResult_modal" tabindex="-1" role="basic" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
						<i class="glyphicon glyphicon-remove"></i>
					</button>
					<h4 class="modal-title">执行返回的结果</h4>
				</div>
				<div class="modal-body"><pre id="invodeResult_body"></pre></div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- INVOKE RESULT MODAL END -->

	<#include "include/js"/>
	<script src="https://cdn.bootcss.com/moment.js/2.18.0/moment.min.js"></script>
	<script src="/dict/static/lib/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
	<script src="https://cdn.bootcss.com/x-editable/1.5.1/bootstrap3-editable/js/bootstrap-editable.js"></script>

	<script src="/dict/static/jmx_in_web/page.mbeanInfo.js"></script>
	<script type="text/javascript">
		mbeanInfo.objectName = "${mbean.objectName?j_string}";
	</script>
</body>

</html>