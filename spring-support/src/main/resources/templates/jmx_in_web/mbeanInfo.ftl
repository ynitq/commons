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

		<!-- 属性列表 vue-->
		<div class="attr_container">
			<div class="panel panel-default" id="list_table_attr">
				<div class="panel-heading">
					属性
					<button class="btn btn-sm btn-default" @click="allColumn">{{showAllColText}}</button>
					<button class="btn btn-sm pull-right btn-default" @click="reload">刷新</button>
				</div>
				<div class="panel-body">
					<table class="table table-light table-hover">
						<thead>
							<tr>
								<th width="250" :class="{'hide':!showAllCol}">名字</th>
								<th width="150">说明</th>
								<th>值</th>
							</tr>
						</thead>
						<tbody>
							<template v-for="attrVo in attrs">
							<tr>
								<td :class="{'hide':!showAllCol}"><span class="caption">{{attrVo.info.name}}</span> <span class="label label-warning"
										v-if="attrVo.info.writable && !attrVo.info.readable">只写</span> <span class="label label-success"
										v-if="!attrVo.info.writable">只读</span> <span class="text-muted" align="right">{{attrVo.info.type}}</span></td>
								<td>{{attrVo.desc}}</td>
								<td class="attr_value">
									<div v-if="!attrVo.info.writable || !attrVo.inputable">
										<pre v-if="attrVo.jsonValue" v-html="attrVo.value"></pre>
										<span class="text-primary" v-if="!attrVo.jsonValue" v-html="attrVo.value"></span>
									</div> <attr-value :attr-vo="attrVo" v-if="attrVo.info.writable && attrVo.inputable"></attr-value>
								</td>
							</tr>
							</template>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<!-- /属性列表 -->

		<!-- opt列表 -->
		<div class="opt-container">
			<!-- MBean 基础信息 -->
			<div class="panel panel-default">
				<div class="panel-heading">
					<b>说明: </b> ${mbean.desc} <b>类:</b> ${mbean.className}
				</div>
				<div class="panel-body">
					<b>Object name: </b>${mbean.objectName}
				</div>
			</div>
			<!-- /MBean 基础信息 -->

			<div class="opt-nav">
				<ul class="nav nav-pills nav-stacked">
					<#assign i=0> <#list mbean.opts as optVo>
					<li role="presentation"><a href="#opt_form_${i}" role="tab" data-toggle="tab" aria-controls="#opt_form_${i}">${optVo.info.name} <span class="text-muted">${optVo.info.description}</span></a></li>
					<#assign i=i+1></#list>
				</ul>
			</div>

			<div class="tab-content opt-forms">
				<#assign i=0> <#list mbean.opts as optVo>
				<div role="tabpanel" class="tab-pane fade opt-panel" id="opt_form_${i}">
					<div class="panel panel-default">
						<div class="panel-heading">
							<span class="label label-sm label-success">${optVo.info.returnType}</span>
							<span class="caption">${optVo.info.name}</span>
							<span class="text-muted">${optVo.info.description}</span>
						</div>
						<div class="panel-body">
							<form id="form_invoke_${i}">
								<input type="hidden" name="optName" value="${optVo.info.name}" />
								<input type="hidden" name="objectName" value="${mbean.objectName}" />
								<#if !optVo.noParam>
								<table class="table table-bordered ">
									<thead>
										<tr role="row" class="heading">
											<th width="250px">名字</th>
											<th>说明</th>
										</tr>
									</thead>
									<tbody>
										<#list optVo.params as paramVo>
										<tr role="row" class="filter">
											<input type="hidden" name="paramType" value="${paramVo.info.type}" />
											<td>${paramVo.info.name}
											<span class="label label-success" > ${paramVo.info.type}</span>
											</td>
											<td><#if paramVo.info.type=='boolean'> 
													<label> 
														<input type="checkbox" data-name="p_${i}_${paramVo.id}" class="js_boolean_checkbox">
														<span id="p_${i}_${paramVo.id}_txt">false</span>
													</label> <input type="hidden" name="paramValue" value="false" id="p_${i}_${paramVo.id}_input"> 
												<#elseif paramVo.info.type=='java.util.Date'>
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
												<#else> 
													<textarea rows="2" class="form-control form-filter input-sm" name="paramValue">${paramVo.defaultValue}</textarea>
												</#if>

												<div class="text-muted">
													${paramVo.info.description}
												</div></td>
										</tr>
										</#list>
									</tbody>

								</table>
								</#if>
								<a class="btn btn-info w100 js_invoke_opt" data-form-name="form_invoke_${i}">执行</a>
							</form>
						</div>
					</div>
				</div>
				<#assign i=i+1></#list>
			</div>

			<#assign i=0>
			<div class="row"></div>
		</div>
		<!-- /opt列表 -->

	</div>
	<!-- /main-content -->

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
				<div class="modal-body">
					<pre id="invodeResult_body"></pre>
				</div>
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