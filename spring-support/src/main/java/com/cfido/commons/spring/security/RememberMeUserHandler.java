package com.cfido.commons.spring.security;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.cfido.commons.utils.utils.BitUtils;
import com.cfido.commons.utils.utils.EncryptUtil;
import com.cfido.commons.utils.web.CookieUtils;
import com.linzi.common.loginCheck.IWebUser;

/**
 * <pre>
 * 将用户放到session中的处理器
 * </pre>
 * 
 * @author 梁韦江 2016年8月23日
 */
@Component
public class RememberMeUserHandler {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RememberMeUserHandler.class);
	private static final String DELIMITER = ":";

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private LoginCheckProperties prop;

	@Autowired
	private ApplicationContext applicationContext;

	private final Map<String, IUserServiceForRememberMe> userProviderMap = new HashMap<>();

	protected static class CookieValue {
		String username;
		long expireTime;
		String signatureValue;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(username)
					.append(DELIMITER)
					.append(expireTime)
					.append(DELIMITER)
					.append(signatureValue);
			return sb.toString();
		}
	}

	@PostConstruct
	protected void init() {
		Map<String, IUserServiceForRememberMe> map = this.applicationContext.getBeansOfType(IUserServiceForRememberMe.class);

		for (IUserServiceForRememberMe userProvider : map.values()) {
			this.addUserProvider(userProvider);
		}

		if (this.userProviderMap.isEmpty()) {
			log.warn("没有找到任何提供实现了 IUserServiceForRememberMe接口的类，无法将用户信息记录到cookie");
		}
	}

	private boolean isEnable() {
		return !this.userProviderMap.isEmpty();
	}

	/**
	 * 检查 出来的的用户是否合法
	 * 
	 * @param clazz
	 * @param user
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T extends IWebUser> T check(Class<? extends IWebUser> clazz, IWebUser user, CookieValue value) {

		if (user == null || clazz == null || value == null || !this.isEnable()) {
			return null;
		}

		long time = value.expireTime - System.currentTimeMillis();

		if (time < 0) {
			log.debug("remember me 用户验证。超时了");
			return null;
		}

		String sign = this.makeTokenSignature(value.expireTime, value.username, user.getPassword());
		if (!sign.equals(value.signatureValue)) {
			log.debug("remember me 用户验证。 签名错误， 实际={}, 期待={}", value.signatureValue, sign);
			return null;
		}

		if (!clazz.isAssignableFrom(user.getClass())) {
			log.debug("remember me 用户验证。 class错误， 实际={}, 期待={}",
					user.getClass().getSimpleName(), clazz.getSimpleName());
		}

		log.debug("remember me 用户验证成功。username={}, 有效期还剩 {}小时",
				user.getUsername(), TimeUnit.MILLISECONDS.toHours(time));

		return (T) user;
	}

	private String extractRememberMeCookie(HttpServletRequest request, Class<? extends IWebUser> clazz) {
		Cookie[] cookies = request.getCookies();

		if ((cookies == null) || (cookies.length == 0)) {
			return null;
		}

		String cookieName = this.prop.getRememberMe().getCookieName(clazz);

		return CookieUtils.getCookieValue(request, cookieName, true);
	}

	/**
	 * 根据 cookie的值，找回用户
	 * 
	 * @param request
	 * @param response
	 * @param clazz
	 * @param userDetailService
	 * @return
	 */
	public <T extends IWebUser> T getUser(HttpServletRequest request, HttpServletResponse response,
			Class<? extends IWebUser> clazz) {

		if (!this.isEnable()) {
			return null;
		}
		
		Assert.notNull(request, "request 不能为空 ");
		Assert.notNull(clazz, "clazz 不能为空 ");

		String rememberMeCookieString = this.extractRememberMeCookie(request, clazz);
		if (!StringUtils.hasLength(rememberMeCookieString)) {
			return null;
		}

		log.debug("remember me Cookie解码，发现cookie:{}", rememberMeCookieString);

		T user = null;

		CookieValue value = decodeCookie(rememberMeCookieString);

		if (value != null) {
			// 根据用户class寻找这个用户类型的数据提供者
			IUserServiceForRememberMe userProvider = this.userProviderMap.get(clazz.getName());
			if (userProvider != null) {
				// 如果能找到
				IWebUser tmpUser = userProvider.loadUserByUsername(value.username);
				user = this.check(clazz, tmpUser, value);
			}
		}

		if (user == null) {
			this.onLogout(request, response, clazz);
		} else {
			log.debug("成功找回用户 {}", user.getUsername());
		}
		return user;
	}

	public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, IWebUser user) {
		if (!this.isEnable()) {
			return;
		}

		Assert.notNull(user, "user 不能为空");

		int maxAge = this.prop.getRememberMe().getCookieAge();
		String cookieName = this.prop.getRememberMe().getCookieName(user.getClass());

		// 合成要保存到cookie的内容
		CookieValue value = new CookieValue();
		value.username = user.getUsername();
		value.expireTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(maxAge);
		value.signatureValue = this.makeTokenSignature(value.expireTime, value.username, user.getPassword());
		String cookieValue = encodeCookie(value);

		CookieUtils.addEncryptCookie(response, "/", cookieName, cookieValue, "", maxAge);

		if (log.isDebugEnabled()) {
			Date expire = new Date(value.expireTime);
			log.debug("为用户生成 RememberMe的cookie: name={}, value={} , 到期时间:{}",
					cookieName, cookieValue, this.dateFormat.format(expire));
		}
	}

	public void onLogout(HttpServletRequest request, HttpServletResponse response, Class<? extends IWebUser> clazz) {
		if (!this.isEnable()) {
			return;
		}

		String cookieName = this.prop.getRememberMe().getCookieName(clazz);

		log.debug("删除 cookie:{}", cookieName);

		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		cookie.setPath(getCookiePath(request));

		response.addCookie(cookie);
	}

	/**
	 * 加密cookie
	 *
	 * @param value
	 *            要加密的内容
	 * @return 加密后的字符串
	 */
	protected String encodeCookie(CookieValue value) {
		String str = value.toString();

		byte[] byteAry = str.getBytes();
		BitUtils.shiftRight(byteAry, 4);// 右移4位

		return EncryptUtil.byteArray2String(byteAry);
	}

	private String getCookiePath(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		return contextPath.length() > 0 ? contextPath : "/";
	}

	/**
	 * 计算签名
	 */
	private String makeTokenSignature(long tokenExpireTime, String username,
			String password) {

		String data = username + DELIMITER + tokenExpireTime + DELIMITER + password + DELIMITER
				+ this.prop.getRememberMe().getKey();

		return EncryptUtil.md5(data);
	}

	/**
	 * 将cookie的值解码
	 * 
	 * @param cookieValue
	 *            原始值
	 * @return 解密后的值
	 */
	protected CookieValue decodeCookie(String cookieValue) {

		byte[] byteAry = EncryptUtil.byteString2Array(cookieValue);

		// 左移动4位
		BitUtils.shiftLeft(byteAry, 4);

		String cookieAsPlainText = new String(byteAry);

		String[] tokens = StringUtils.delimitedListToStringArray(cookieAsPlainText,
				DELIMITER);

		if (tokens.length != 3) {
			log.debug("remember me Cookie解码，但解出来的长度有错 ， str={}", cookieAsPlainText);
			return null;
		}

		CookieValue value = new CookieValue();
		value.username = tokens[0];
		value.signatureValue = tokens[2];
		try {
			value.expireTime = Long.parseLong(tokens[1]);
		} catch (NumberFormatException ex) {
			log.debug("remember me Cookie解码，但解出来的时间错 ， str={}", cookieAsPlainText);
			return null;
		}

		if (!StringUtils.hasLength(value.username) || !StringUtils.hasLength(value.signatureValue)) {
			log.debug("remember me Cookie解码，但内容无法解析来 ， str={}", cookieAsPlainText);
			return null;
		}

		log.debug("remember me Cookie解码成功，username={}, expire={} , sign={}",
				value.username, value.expireTime, value.signatureValue);

		return value;
	}

	/**
	 * 判断是否已经存在了一种用户类型的属性提供者
	 */
	public IUserServiceForRememberMe getUserProvider(Class<? extends IWebUser> clazz) {
		return this.userProviderMap.get(clazz.getName());
	}

	public void addUserProvider(IUserServiceForRememberMe userProvider) {
		String[] supportUserClassNames = userProvider.getSupportUserClassNames();
		if (supportUserClassNames != null) {
			for (String name : supportUserClassNames) {
				this.userProviderMap.put(name, userProvider);
				log.debug("找到类型:{} 的用户信息提供者 {}",
						name, userProvider.getClass().getName());
			}
		}
	}
}
