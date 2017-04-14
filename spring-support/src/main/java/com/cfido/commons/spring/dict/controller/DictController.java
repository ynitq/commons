package com.cfido.commons.spring.dict.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cfido.commons.spring.debugMode.DebugModeProperties;
import com.cfido.commons.spring.dict.DictProperties;
import com.cfido.commons.spring.dict.core.DictAdminWebUser;
import com.cfido.commons.spring.dict.core.DictCoreService;
import com.cfido.commons.spring.dict.core.DictTemplateService;
import com.cfido.commons.spring.dict.inf.impl.DictAdminUserImpl;
import com.cfido.commons.spring.dict.schema.DictXml.DictXmlRow;
import com.cfido.commons.spring.utils.WebContextHolderHelper;
import com.cfido.commons.utils.utils.EncodeUtil;
import com.cfido.commons.utils.utils.FileUtil;
import com.linzi.common.loginCheck.ANeedCheckLogin;

import freemarker.template.TemplateException;

/**
 * <pre>
 * 导出xml
 * </pre>
 * 
 * @author 梁韦江 2016年11月17日
 */
@Controller
public class DictController {

	@Autowired
	private DictProperties prop;

	@Autowired
	private DictCoreService coreService;

	@Autowired
	private DictTemplateService templateService;

	@Autowired
	private DictAdminUserImpl adminUserImpl;

	@Autowired
	private DebugModeProperties debugMode;

	@RequestMapping(value = DictProperties.EXPORT_URL)
	@ResponseBody
	@ANeedCheckLogin(userClass = DictAdminWebUser.class)
	public String export() throws IOException {

		// 导出前，先保存一次文件
		this.coreService.doSaveAll();

		// xml文件路径
		File file = new File(this.prop.getXmlFullPath());

		return FileUtil.readFile(file);
	}

	/**
	 * 管理界面，这是一个单页应用，所有东西都在一个页面中
	 * 
	 * @throws IOException
	 * @throws TemplateException
	 */
	@RequestMapping(value = DictProperties.MANAGER_URL)
	@ResponseBody
	public String manager() throws TemplateException, IOException {
		Map<String, Object> model = new HashMap<>();
		model.put("pageTitle", this.prop.getAdmin().getPageTitle());
		model.put("adminInProp", this.adminUserImpl.isAdminInPorp());

		String basePath = WebContextHolderHelper.getAttachmentFullPath(null);
		model.put("basePath", basePath);

		return this.templateService.process("index", model);
	}
	
	/**
	 * 用于js的数据
	 * @return String
	 * @throws TemplateException
	 * @throws IOException
	 * 
	 */
	@RequestMapping(value = DictProperties.DICT_JS)
	@ResponseBody
	public String js() throws TemplateException, IOException {
		

		// 获得所有数据
		List<DictXmlRow> srcList = this.coreService.getAllFromMap();

		// 将数据转化成为map,其实就是js中的object
		Map<String, String> jsonMap = new HashMap<>();
		for (DictXmlRow row : srcList) {
			jsonMap.put(row.getKey(), row.isHtml() ? row.getValue() : EncodeUtil.html(row.getValue(), false));
		}
		String jsonStr = JSON.toJSONString(jsonMap, true);

		Map<String, Object> model = new HashMap<>();
		model.put("jsonStr", jsonStr);
		model.put("debugMode", String.valueOf(this.debugMode.isDebugMode()));
		return this.templateService.process("dictJs", model);
	}

}
