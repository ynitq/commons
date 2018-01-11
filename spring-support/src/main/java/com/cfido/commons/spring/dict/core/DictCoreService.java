package com.cfido.commons.spring.dict.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.bind.JAXBException;

import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.cfido.commons.beans.apiExceptions.IdNotFoundException;
import com.cfido.commons.beans.apiExceptions.MissFieldException;
import com.cfido.commons.beans.apiExceptions.SimpleApiException;
import com.cfido.commons.beans.apiExceptions.SystemErrorException;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.spring.debugMode.DebugModeProperties;
import com.cfido.commons.spring.dict.DictProperties;
import com.cfido.commons.spring.dict.constants.DictValueTypeConstant;
import com.cfido.commons.spring.dict.inf.form.DictAttachmentEditForm;
import com.cfido.commons.spring.dict.inf.form.DictRowEditForm;
import com.cfido.commons.spring.dict.inf.form.KeySearchPageForm;
import com.cfido.commons.spring.dict.inf.responses.DictAttachmentVo;
import com.cfido.commons.spring.dict.inf.responses.DictVo;
import com.cfido.commons.spring.dict.schema.DictXml;
import com.cfido.commons.spring.dict.schema.DictXml.DictAttachmentRow;
import com.cfido.commons.spring.dict.schema.DictXml.DictXmlRow;
import com.cfido.commons.spring.imageUpload.ImageUploadProperties;
import com.cfido.commons.spring.imageUpload.ImageUploadService;
import com.cfido.commons.spring.imageUpload.ImageUploadService.SaveResult;
import com.cfido.commons.spring.security.LoginCheckInterceptor;
import com.cfido.commons.spring.utils.WebContextHolderHelper;
import com.cfido.commons.utils.db.PageQueryResult;
import com.cfido.commons.utils.utils.DateUtil;
import com.cfido.commons.utils.utils.EncodeUtil;
import com.cfido.commons.utils.utils.JaxbUtil;
import com.cfido.commons.utils.utils.LogUtil;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * <pre>
 * 字典项目的核心服务
 * </pre>
 * 
 * @author 梁韦江 2016年11月15日
 */
@Service
public class DictCoreService {

	/**
	 * <pre>
	 * freemarker引擎用的模板加载器，我们这里是从字典中根据key获取模板
	 * </pre>
	 * 
	 * @author 梁韦江 2017年5月25日
	 * 
	 */
	private class DictTemplateLoader implements TemplateLoader {

		@Override
		public void closeTemplateSource(Object templateSource) throws IOException {
		}

		@Override
		public Object findTemplateSource(String name) throws IOException {
			DictXmlRow row = DictCoreService.this.map.get(name);
			if (row != null) {
				return row.getValue();
			} else {
				return null;
			}
		}

		@Override
		public long getLastModified(Object templateSource) {
			return 0;
		}

		@Override
		public Reader getReader(Object templateSource, String encoding) throws IOException {
			return new StringReader((String) templateSource);
		}

	}

	public class DictHtmlModel {
		public String get(String key) {
			// 转码
			return DictCoreService.this.getStringByKey(key, true, false, null);
		}
	}

	public class DictRawModel {
		public String get(String key) {
			// 原文
			return DictCoreService.this.getRawText(key);
		}
	}

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DictCoreService.class);

	/* PegDown 解析 Markdown 格式的内容 */
	private final PegDownProcessor pdp = new PegDownProcessor(Extensions.ALL_WITH_OPTIONALS);

	public static final String VO_NAME_HTML = "dict";
	public static final String VO_NAME_RAW = "dictRaw";

	/** debug模式下的 输出格式 */
	public static final String DEBUG_FORMAT = "<span title=\"key=%s\">%s</span>";

	@Autowired
	private DebugModeProperties debugMode;

	@Autowired
	private DictProperties prop;

	@Autowired
	private ImageUploadService imageUploadService;

	@Autowired(required = false)
	private LoginCheckInterceptor loginCheckInterceptor;

	/** 缓存所有帮助文字的map */
	private final Map<String, DictXmlRow> map = new HashMap<>();

	/** 缓存所有上传的附件的map */
	private final Map<String, DictAttachmentRow> attahcmentMap = new HashMap<>();

	/** 对操作map时的锁 */
	private final Lock lockForMap = new ReentrantLock();

	private boolean isNeedSave = false;

	/** 主线程池，处理游戏的各类事件、聊天、发送系统信息等 */
	private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

	private final Configuration freemarkerCfg;

	/** 根据属性转码为html、markdown格式的 model */
	private final DictHtmlModel dictHtmlModel = new DictHtmlModel();

	/** 不转码，只输出原文的 model */
	private final DictRawModel dictRawModel = new DictRawModel();

	public DictCoreService() {
		freemarkerCfg = new Configuration(Configuration.getVersion());
		// 防止freemarker渲染时对value=null的key 解析出错
		freemarkerCfg.setClassicCompatible(true);
		freemarkerCfg.setTemplateLoader(new DictTemplateLoader());
	}

	/**
	 * 将当前对象放入freemark的model用于非request请求
	 */
	public void addToModel(ModelMap model) {
		model.addAttribute(VO_NAME_RAW, this.dictRawModel);
		model.addAttribute(VO_NAME_HTML, this.dictHtmlModel);
	}

	/**
	 * 删除一个附件
	 * 
	 * @throws BaseApiException
	 * @throws IOException
	 */
	public void deleteAttachmengRow(String key) throws BaseApiException, IOException {
		if (!StringUtils.hasText(key)) {
			throw new SimpleApiException("请输入关键词");
		}

		log.debug("删除附件键值:{}", key);

		lockForMap.lock();
		try {
			DictAttachmentRow deletedRow = this.attahcmentMap.remove(key);

			if (deletedRow == null) {
				throw new IdNotFoundException("无此附件", key);
			} else {
				this.imageUploadService.deleteOldFile(deletedRow.getPathPrefix() + deletedRow.getKey(),
						deletedRow.getExtName());
			}
		} finally {
			lockForMap.unlock();
		}

		this.isNeedSave = true;
	}

	/**
	 * 删除一个键值
	 * 
	 * @throws BaseApiException
	 */
	public void deleteRow(String key) throws BaseApiException {

		if (!StringUtils.hasText(key)) {
			throw new SimpleApiException("请输入关键词");
		}

		log.debug("删除字典键值:{}", key);

		lockForMap.lock();
		try {
			DictXmlRow deletedRow = this.map.remove(key);

			if (deletedRow == null) {
				throw new IdNotFoundException("无此键值", key);
			}
		} finally {
			lockForMap.unlock();
		}

		this.isNeedSave = true;
	}

	/**
	 * 实际的保存数据
	 */
	public void doSaveAll() {

		if (!this.isNeedSave) {
			return;
		}

		log.debug("有字典数据发生了变化，需要保存到文件");

		// 执行保存时，取消需要保存的标志位
		this.isNeedSave = false;

		DictXml xml = new DictXml();

		this.lockForMap.lock();
		try {
			// 从map中获取数据的时候，需要锁一下
			xml.getDictXmlRow().addAll(this.map.values());
			xml.getDictAttachmentRow().addAll(this.attahcmentMap.values());
		} finally {
			this.lockForMap.unlock();
		}

		try {
			this.saveXml(xml);
		} catch (JAXBException e) {
			log.error("保存字典数据到xml文件的时候出错了", e);
		}
	}

	/**
	 * 根据表单查询数据库，并返回封装好的分页结果集
	 * 
	 * @param form
	 * @return
	 */
	public PageQueryResult<DictVo> findInPage(KeySearchPageForm form) {
		log.debug("搜索字典关键字: [{}] , pageNo={} pageSize={}", form.getKey(), form.getPageNo(), form.getPageSize());

		List<DictXmlRow> all = this.getAllFromMap();

		form.verifyPageNo();
		int start = (form.getPageNo() - 1) * form.getPageSize();// 分页时的起点
		int point = 0;// 当前指针
		int totalItem = 0;// 符合条件的总记录数
		List<DictVo> list = new LinkedList<>();
		for (DictXmlRow row : all) {
			if (form.isMatch(row)) {
				totalItem++;
				if (point >= start && list.size() < form.getPageSize()) {
					// 如果到达了某页的起点，并且结果集不到页长度，就添加到结果集中
					DictVo vo = this.rowToVo(row);
					vo.updateSearchKeyword(form.getKey());
					list.add(vo);
				} else {
					point++;
				}
			}
		}

		PageQueryResult<DictVo> page = new PageQueryResult<>(totalItem, list, form);
		return page;
	}

	/**
	 * 根据key返回key对于的html代码。这个方法是在模板中调用的。例如 dict["hi"]
	 */
	public String get(String key) {
		return this.getStringByKey(key, true, false, null);
	}

	/**
	 * 获取所有附件
	 */
	public List<DictAttachmentVo> getAllAttechmentsFromMap() {
		List<DictAttachmentRow> listAll = new LinkedList<>();
		this.lockForMap.lock();
		try {
			listAll.addAll(this.attahcmentMap.values());
		} finally {
			this.lockForMap.unlock();
		}

		// 对两list进行按key排序
		Comparator<DictAttachmentRow> comparator = new Comparator<DictAttachmentRow>() {
			@Override
			public int compare(DictAttachmentRow row1, DictAttachmentRow row2) {
				return row1.getKey().compareTo(row2.getKey());
			}
		};
		Collections.sort(listAll, comparator);

		List<DictAttachmentVo> voList = new LinkedList<>();
		for (DictAttachmentRow row : listAll) {
			String basePath = WebContextHolderHelper.getFullPath(null);
			String thumbPostfix = this.imageUploadService.getThumbPostfix();

			DictAttachmentVo vo = new DictAttachmentVo();
			vo.updateFromXml(row, basePath, thumbPostfix);
			voList.add(vo);
		}

		return voList;
	}

	/**
	 * 获得所有的内容,并排好序
	 * 
	 * @return
	 */
	public List<DictXmlRow> getAllFromMap() {
		List<DictXmlRow> listAll = new LinkedList<>();
		this.lockForMap.lock();
		try {
			listAll.addAll(this.map.values());
		} finally {
			this.lockForMap.unlock();
		}

		// 对两list进行按key排序,先按todo排序，再按名字排序
		Comparator<DictXmlRow> comparator = new Comparator<DictXmlRow>() {
			@Override
			public int compare(DictXmlRow row1, DictXmlRow row2) {
				if (row1.isTodo() == row2.isTodo()) {
					return row1.getKey().compareTo(row2.getKey());

				} else {
					return row1.isTodo() ? -1 : 1;
				}
			}
		};
		Collections.sort(listAll, comparator);

		return listAll;
	}

	public String js() throws TemplateException, IOException {

		List<DictXmlRow> srcList = this.getAllFromMap();

		// 将数据转化成为map,其实就是js中的object
		Map<String, String> jsonMap = new HashMap<>();
		for (DictXmlRow row : srcList) {
			String value = row.getValue();
			int type = row.getType();

			if (type == DictValueTypeConstant.TEXT) {
				value = EncodeUtil.html(value, false);
			} else if (type == DictValueTypeConstant.MARK_DOWN) {
				value = pdp.markdownToHtml(value);
			}
			jsonMap.put(row.getKey(), value);
		}
		return JSON.toJSONString(jsonMap, true);
	}

	/**
	 * 根据key返回key对应的原始文本，不做html转化
	 * 
	 * @return
	 */
	public String getRawText(String key) {
		return this.getStringByKey(key, false, true, null);
	}

	/**
	 * 根据key返回key对应的原始文本，不做html转化
	 * 
	 * @return
	 */
	public String getRawText(String key, String defaultValue) {
		return this.getStringByKey(key, false, true, defaultValue);
	}

	/**
	 * 我们用一个key来保存当前应用的名字，这个名字用于字典页面、api测试页面等地方
	 */
	public String getSystemName() {
		return this.getRawText("system.name");
	}

	/**
	 * 从xml文件导入内容，并覆盖原来的数据
	 * 
	 * @throws BaseApiException
	 */
	public void importXml(DictXml xml, boolean cleanOld) throws BaseApiException {

		Assert.notNull(xml, "字典xml不能为空");
		Assert.notEmpty(xml.getDictXmlRow(), "字典必须有数据不能为空");

		// 导入前先备份
		try {
			this.backup();
		} catch (JAXBException e) {
			log.error("备份字典文件时，发生了错误", e);

			throw new SystemErrorException(e);
		}

		this.lockForMap.lock();
		try {
			if (cleanOld) {
				// 如果不保留原来的数据，就清空
				this.map.clear();
			}

			List<DictXmlRow> list = xml.getDictXmlRow();
			int todo = 0;
			for (DictXmlRow row : list) {
				this.map.put(row.getKey(), row);
				if (row.isTodo()) {
					todo++;
				}
			}
			log.info("导入数据完成，共 {} 条记录，其中 {} 条待处理中", list.size(), todo);

		} finally {
			this.lockForMap.unlock();
		}

		this.asyncSave();
	}

	/**
	 * 根据key寻找模板，并数据生成字符串
	 * 
	 * @param key
	 *            模板的key
	 * @param dataModel
	 *            数据
	 * @return
	 * @throws TemplateException
	 * @throws IOException
	 */
	public String processTemplate(String key, Map<String, Object> dataModel) {

		try {
			Template template = this.freemarkerCfg.getTemplate(key);
			StringWriter w = new StringWriter();
			PrintWriter out = new PrintWriter(w);
			template.process(dataModel, out);
			return w.toString();
		} catch (TemplateException | IOException e) {
			LogUtil.traceError(log, e, "解析模板时发生了错误");
			return e.getMessage();
		}
	}

	/**
	 * 保存附件
	 * 
	 * @param form
	 * @throws BaseApiException
	 * @throws IOException
	 */
	public void saveAttachmentRow(DictAttachmentEditForm form) throws BaseApiException, IOException {

		Assert.hasText(form.getKey(), "key不能为空");

		String pathPrefix = ImageUploadProperties.UPLOAD_DIR_PREFIX + "/" + "dict";

		SaveResult res = null;
		if (form.getFile() != null && !form.getFile().isEmpty()) {
			res = this.imageUploadService.save(form.getFile(), pathPrefix, form.getKey());
			log.debug("上传的文件保存在 {}", res.getFullPath());
		}

		this.lockForMap.lock();
		try {
			DictAttachmentRow row = this.attahcmentMap.get(form.getKey());

			if (row == null) {
				// 如果原来没有，就增加

				if (res == null) {
					throw new MissFieldException("请上传文件");
				}
				row = new DictAttachmentRow();

				row.setKey(form.getKey());

				this.attahcmentMap.put(form.getKey(), row);
			} else {
				// 如果原来已经存在，并且这次又上传了文件，就需要比较一下扩展名
				if (res != null && !row.getExtName().equals(res.getExtName())) {
					// 如果扩展名不同，就需要将原来的存在的文件删除
					this.imageUploadService.deleteOldFile(row.getPathPrefix() + "/" + row.getKey(), row.getExtName());
				}
			}

			// 只有一个备注字段需要更新
			row.setMemo(form.getMemo());

			if (res != null) {
				// 如果有上传文件，就保存上传文件内存，非空判断已经在上班做了
				row.setExtName(res.getExtName());
				row.setImageFile(res.isImage());
				row.setPathPrefix(pathPrefix);
				if (res.isImage()) {
					// 如果是图片，就需要填写和图片相关的参数
					row.setImageHeight(res.getImageProp().getImageHeight());
					row.setImageWidth(res.getImageProp().getImageWidth());
				}
			}
			this.asyncSave();

		} finally {
			this.lockForMap.unlock();
		}
	}

	/**
	 * 保存一对键值到数据库
	 */
	public DictVo saveRow(DictRowEditForm form) {

		Assert.hasText(form.getKey(), "key不能为空");

		log.debug("保存字典键值 {}", form.getKey());

		this.lockForMap.lock();
		try {
			DictXmlRow row = this.map.get(form.getKey());

			if (row == null) {
				// 手动添加的，todo可以为false
				row = this.newDefaultEntity(form.getKey(), form.getValue(), false);
				row.setType(DictValueTypeConstant.TEXT);
				this.map.put(form.getKey(), row);
			}

			// 记录一下原来的值，用于比较是否发生了变化
			String oldValue = row.getValue();

			// 根据表单填写数据
			row.setValue(form.getValue());
			// row.setHtml(form.getType() == DictValueTypeConstant.HTML ? true :
			// false);
			row.setType(form.getType());
			row.setMemo(form.getMemo());

			if (row.isTodo() && StringUtils.hasText(form.getValue())) {
				// 如果原来是待处理状态，并且表单中有值，就判断值是否发生了变化
				row.setTodo(form.getValue().equals(oldValue));
			}

			this.asyncSave();

			return this.rowToVo(row);

		} finally {
			this.lockForMap.unlock();
		}
	}

	/**
	 * 异步保存。其实啥都不用干，因为有个定时器在定期保存
	 */
	private void asyncSave() {
		this.isNeedSave = true;
	}

	private void backup() throws JAXBException {
		String backupFile = this.prop.getBackupFileFullPath();

		DictXml backupXml = new DictXml();
		backupXml.getDictXmlRow().addAll(this.getAllFromMap());

		File file = new File(backupFile);
		JaxbUtil.save(backupXml, file);
	}

	/**
	 * 获得输出的html字符串
	 */
	private String getRowOutputHtml(DictXmlRow row, boolean debugMode) {
		// 如果是html模式就直接输出value，否则就需要转码
		// String value = row.isHtml() ? row.getValue() :
		// EncodeUtil.html(row.getValue(), false);
		String value = row.getValue();
		if (row.getType() == DictValueTypeConstant.TEXT) {
			value = EncodeUtil.html(row.getValue(), false);
		} else if (row.getType() == DictValueTypeConstant.MARK_DOWN) {
			value = pdp.markdownToHtml(row.getValue());
		}

		if (debugMode) {
			// 如果是debug模式，就用debug的格式
			return String.format(DEBUG_FORMAT, row.getKey(), value);
		} else {
			// 否则就直接输出
			return value;
		}

	}

	/**
	 * 根据key返回key对于的html代码。这个方法是在模板中调用的。例如 dict["hi"]
	 * 
	 * @param key
	 *            键值
	 * @param incCounter
	 *            是否在计数器中+1
	 * @param onlyRawText
	 *            是否只返回原始文本
	 * @return
	 */
	private String getStringByKey(String key, boolean incCounter, boolean onlyRawText, String defaultValue) {

		if (!StringUtils.hasText(key)) {
			return null;
		}

		DictXmlRow row;

		this.lockForMap.lock();
		try {

			row = this.map.get(key);
			if (row == null) {

				log.debug("发现新的键值 {}", key);

				// 如果原来不存在这个key，就创建一条记录
				row = this.newDefaultEntity(key, defaultValue, true);

				// 同时将key放到map中
				this.map.put(row.getKey(), row);
			} else {
				if (defaultValue != null && row.getKey().equals(row.getValue())) {
					// 如果原来的键和值相等，就表示是自动添加的，如果碰巧传入的了默认值，就用默认值更新一下
					row.setValue(defaultValue);
					this.asyncSave();
				}
			}

			if (incCounter) {
				// 这个key的使用次数+1，
				row.setUsedCount(row.getUsedCount() + 1);
			}

		} finally {
			this.lockForMap.unlock();
		}

		if (incCounter) {
			// 每一次get，其实都导致了计数器发生了变化，所以都需要异步保存一下
			this.asyncSave();
		}

		if (onlyRawText) {
			return row.getValue();
		} else {
			return this.getRowOutputHtml(row, this.debugMode.isDebugMode());
		}
	}

	private DictXml getXml() throws JAXBException {
		DictXml xml;

		String fileName = this.prop.getXmlFullPath();

		File file = new File(fileName);
		if (!file.exists()) {
			// 如果文件不存在，就创建默认文件
			file.getParentFile().mkdirs();

			xml = new DictXml();
			JaxbUtil.save(xml, file);
		} else {
			xml = JaxbUtil.parserXml(DictXml.class, file);
		}
		return xml;
	}

	/**
	 * 新建PO时的各项默认值
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	private DictXmlRow newDefaultEntity(String key, String defaultValue, boolean todo) {

		DictXmlRow row = new DictXmlRow();

		row.setKey(key);
		// row.setHtml(false);
		row.setTodo(todo);
		row.setUsedCount(0);
		row.setType(DictValueTypeConstant.TEXT);

		if (StringUtils.hasText(defaultValue)) {
			// 如果有默认值，就用默认值
			row.setValue(defaultValue);
			row.setTodo(false); // 有默认值的，就一定不是TODO的
			row.setMemo("默认值自动添加");
		} else {
			// 如果没有默认值，就用key作为值
			row.setValue(key);

			// 在备注里面说明这个是自动添加的，需要处理
			row.setMemo(String.format("第一次发现于: %s (%s)", WebContextHolderHelper.getRequestURL(false),
					DateUtil.dateFormat(new Date())));
		}

		return row;
	}

	/**
	 * 将xml行对象转为vo
	 */
	private DictVo rowToVo(DictXmlRow row) {

		DictVo vo = new DictVo();

		// 通过row设置数据
		vo.updateFromXml(row);

		// 预览的html，根据类型对md格式的解析
		vo.setPreview(this.getRowOutputHtml(row, false));

		return vo;
	}

	private void saveXml(DictXml xml) throws JAXBException {
		String fileName = this.prop.getXmlFullPath();
		File file = new File(fileName);
		JaxbUtil.save(xml, file);
	}

	@PostConstruct
	protected void init() throws JAXBException {

		if (this.loginCheckInterceptor == null) {
			log.info("DictCoreService 初始化失败，需要和LoginCheck一起使用");
		} else {

			this.loginCheckInterceptor.addCommonModel(VO_NAME_HTML, this.dictHtmlModel);
			this.loginCheckInterceptor.addCommonModel(VO_NAME_RAW, this.dictRawModel);

			DictXml xmlDoc = this.getXml();

			// 初始化字典map
			this.map.clear();
			int todo = 0;
			for (DictXmlRow row : xmlDoc.getDictXmlRow()) {
				if (row.getType() == 0) {
					// 如果是0，表示还是旧的用isHtml的数据，新版本用type替换了html，这里是将type设置为非0，表示已经转换过了
					row.setType(row.isHtml() ? DictValueTypeConstant.HTML : DictValueTypeConstant.TEXT);
				}

				this.map.put(row.getKey(), row);
				if (row.isTodo()) {
					todo++;
				}
			}

			// 初始化附件map
			this.attahcmentMap.clear();
			for (DictAttachmentRow row : xmlDoc.getDictAttachmentRow()) {
				this.attahcmentMap.put(row.getKey(), row);
			}
			log.info("初始化 DictCoreService 字典，共 {} 条记录，其中 {} 条待处理中, {} 个附件", xmlDoc.getDictXmlRow().size(), todo,
					xmlDoc.getDictAttachmentRow().size());

			// 启动定时器，定时检查是否需要保存数据
			this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					DictCoreService.this.doSaveAll();
				}
			}, 0, this.prop.getSavePeriod(), TimeUnit.SECONDS);
		}
	}

	@PreDestroy
	protected void onShutdown() {
		// 先将定时器停止了
		this.scheduledExecutorService.shutdown();

		// shutdown前需要检查一下是否有数据需要保存
		this.doSaveAll();

	}
}
