package com.cfido.commons.codeGen.core;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <pre>
 * 用于方便使用的controller
 * </pre>
 * 
 * @author 梁韦江 2017年7月3日
 */
@Controller
public class IndexController {

	@RequestMapping("/")
	public String index() {
		return "redirect:/jmxInWeb/";
	}

}
