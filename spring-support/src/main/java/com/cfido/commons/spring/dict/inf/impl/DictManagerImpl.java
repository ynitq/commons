package com.cfido.commons.spring.dict.inf.impl;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cfido.commons.annotation.api.AApiServerImpl;
import com.cfido.commons.beans.apiExceptions.SimpleApiException;
import com.cfido.commons.beans.apiExceptions.SystemErrorException;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.apiServer.impl.CommonSuccessResponse;
import com.cfido.commons.spring.dict.core.DictAdminWebUser;
import com.cfido.commons.spring.dict.core.DictCoreService;
import com.cfido.commons.spring.dict.inf.IDictManager;
import com.cfido.commons.spring.dict.inf.form.DictImportForm;
import com.cfido.commons.spring.dict.inf.form.DictKeyForm;
import com.cfido.commons.spring.dict.inf.form.DictRowEditForm;
import com.cfido.commons.spring.dict.inf.form.KeySearchPageForm;
import com.cfido.commons.spring.dict.inf.responses.DictKeySearchResponse;
import com.cfido.commons.spring.dict.inf.responses.DictVo;
import com.cfido.commons.spring.dict.schema.DictXml;
import com.cfido.commons.spring.utils.WebContextHolderHelper;
import com.cfido.commons.utils.db.PageQueryResult;
import com.cfido.commons.utils.utils.FileUtil;
import com.cfido.commons.utils.utils.JaxbUtil;
import com.cfido.commons.utils.web.BinderUtil;
import com.linzi.common.loginCheck.ANeedCheckLogin;

/**
 * <pre>
 * 字典管理的实现类
 * </pre>
 * 
 * @author 梁韦江 2016年11月16日
 */
@Service
@ANeedCheckLogin(userClass = DictAdminWebUser.class)
@AApiServerImpl
public class DictManagerImpl implements IDictManager {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DictManagerImpl.class);

	private final String sessionName = this.getClass().getName();

	@Autowired
	private DictCoreService coreService;

	@Override
	public DictKeySearchResponse search(KeySearchPageForm form) {

		PageQueryResult<DictVo> page = this.coreService.findInPage(form);

		DictKeySearchResponse res = new DictKeySearchResponse();

		res.setItemTotal(page.getItemTotal());
		res.setList(page.getList());
		res.setPageNo(page.getPageNo());
		res.setPageSize(page.getPageSize());
		res.setPageTotal(page.getPageTotal());

		return res;
	}

	protected DictKeySearchResponse getListFromSessionForm() throws BaseApiException {
		KeySearchPageForm form = BinderUtil.getFormFromSession(
				WebContextHolderHelper.getRequest(), KeySearchPageForm.class, sessionName);

		return this.search(form);
	}

	@Override
	@ANeedCheckLogin(userClass = DictAdminWebUser.class)
	public DictKeySearchResponse save(DictRowEditForm form) throws BaseApiException {

		if (!StringUtils.hasText(form.getKey())) {
			throw new SimpleApiException("键值不能为空");
		}

		// 保存
		this.coreService.saveRow(form);

		// 返回列表结果，用于更新页面的列表页
		return this.getListFromSessionForm();
	}

	@Override
	@ANeedCheckLogin(userClass = DictAdminWebUser.class)
	public CommonSuccessResponse importXml(DictImportForm form) throws BaseApiException {

		MultipartFile multipartFile = form.getFile();

		if (multipartFile == null || multipartFile.isEmpty()) {
			throw new SimpleApiException("请上传文件");
		}

		try {
			String xmlStr = FileUtil.readFile(multipartFile.getInputStream());

			DictXml xml = JaxbUtil.parserXml(DictXml.class, xmlStr);

			if (xml.getDictXmlRow().isEmpty()) {
				throw new SimpleApiException("上传的xml文件中没有数据");
			}

			log.debug("收到上传的xml文件, 含{}条记录 ", xml.getDictXmlRow().size());

			this.coreService.importXml(xml, form.isCleanOld());

		} catch (IOException e) {
			throw new SystemErrorException(e);
		} catch (JAXBException e) {
			throw new SimpleApiException("xml文件解析错误，请上传正确格式的文件");
		}

		return CommonSuccessResponse.DEFAULT;
	}

	@Override
	public DictKeySearchResponse delete(DictKeyForm form) throws BaseApiException {
		this.coreService.deleteRow(form.getKey());
		return this.getListFromSessionForm();
	}

}
