<!doctype html>
<html lang="zh">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>MBean-${pageTitle}</title>
<#include "include/style"/>
</head>
<body>

	<#include "include/header"/>

	<div class="main-content" style="overflow: auto">
		<div class="panel panel-default">
			<div class="panel-heading">MBean列表</div>
		<!-- List group -->
		<div class="panel-body">
			<table class="table table-light table-hover" id="list_table">
				<thead>
					<tr>
						<th width="33.3%">ObjectName</th>
						<th width="33.3%">说明</th>
						<th width="33.3%">Class</th>
					</tr>
				</thead>
				<tbody>
<#list list as domainVo>
					<tr>
						<td colspan="3">
							<div class="caption">
								<i class="glyphicon glyphicon-th-list"></i> ${domainVo.name}
							</div>
						</td>
					</tr>
<#list domainVo.beans as mbeanVo>
                    <tr>
			          <td><a href="mbeanInfo?objectName=${mbeanVo.objectName?url('UTF-8')}">${mbeanVo.displayName}</a></td>
			          <td>${mbeanVo.desc}</td>
			          <td>${mbeanVo.className}</td>
                    </tr>
</#list>
</#list>
				</tbody>
			</table>
		</div>
	</div>
	</div>

	<#include "include/js"/>
</body>

</html>