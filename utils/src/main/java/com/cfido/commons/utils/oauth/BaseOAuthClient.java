package com.cfido.commons.utils.oauth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.cfido.commons.beans.appServer.UserResponse;
import com.cfido.commons.beans.oauth.UserTokenBean;
import com.cfido.commons.utils.utils.HttpUtil;

/**
 * <pre>
 * OAuth Client的java实现，其实看了原理后，同样也可以做其他语言的实现
 * </pre>
 * 
 * @author 梁韦江 2016年7月19日
 */
public abstract class BaseOAuthClient {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BaseOAuthClient.class);

	private UserTokenBean token;

	private boolean isTokenGot;

	private String code;

	/**
	 * 获得oauth服务器的url
	 * 
	 * @return
	 */
	protected abstract String getOAuthServerUrl();

	protected abstract String getClientId();

	protected abstract String getClientSecret();

	protected abstract String getRedirectUri();

	/**
	 * 使用用户名和密码获得token
	 * 
	 * <pre>
	 * 在服务器上必须设置了允许password登陆方式。
	 * 就是 authorizedGrantTypes 中必须有 password
	 * </pre>
	 * 
	 * @param username
	 * @param password
	 * @param scope
	 *            可为null
	 * @throws IOException
	 */
	public void createTokenByUsername(String username, String password, String scope) throws IOException {
		Assert.notNull(username, "'username' 不能为 null");
		Assert.notNull(password, "'password' 不能为 null");

		// 根据code获得token的 url
		String url = this.getBaseUrl() + "/oauth/token";

		// 参数设置
		Map<String, Object> param = new HashMap<>();
		param.put("grant_type", "password");
		param.put("client_id", this.getClientId());
		param.put("client_secret", this.getClientSecret());
		param.put("password", password);
		param.put("username", username);

		if (StringUtils.hasText(scope)) {
			param.put("scope", scope);
		}

		// Set the Content-Type header
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		this.isTokenGot = false;
		this.token = HttpUtil.requestJson(UserTokenBean.class, url, param, true, null);
		this.isTokenGot = true;

		log.info("获得 token 成功：{}", token.getAccess_token());

	}

	private String getBaseUrl() {
		String url = this.getOAuthServerUrl().trim();
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}

	public void createToken(String code) throws IOException {
		this.createToken(code, null);
	}

	/**
	 * 根据code获得token
	 * 
	 * @param code
	 * @param redirectUri
	 *            重定向的url，如果为空，则服务器会自动从配置中获取
	 * @throws IOException
	 */
	public void createToken(String code, String redirectUri) throws IOException {

		this.code = code;

		Assert.notNull(code, "'code' must not be null");

		// 根据code获得token的 url
		String url = this.getBaseUrl() + "/oauth/token";

		// 参数设置
		Map<String, Object> param = new HashMap<>();
		param.put("grant_type", "authorization_code");
		param.put("client_id", this.getClientId());
		param.put("client_secret", this.getClientSecret());
		param.put("code", code);
		param.put("redirect_uri", this.getRedirectUri());
		
		

		log.info("client_id：{}", this.getClientId());
		log.info("client_secret：{}", this.getClientSecret());
		log.info("code：{}", code);
		log.info("redirect_uri：{}", this.getRedirectUri());

		// Set the Content-Type header
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		this.isTokenGot = false;
		this.token = HttpUtil.requestJson(UserTokenBean.class, url, param, true, null);
		this.isTokenGot = true;
		
		

		log.info("获得 token 成功：{}", token.getAccess_token());

	}

	/**
	 * 在获得token后，请求该服务器的其他资源
	 * 
	 * @param responseClass
	 * @param url
	 * @param paramMap
	 * @param postMethod
	 * @return
	 * @throws OAuthClientException
	 */
	public <T> T getResource(Class<T> responseClass, String url, Map<String, Object> paramMap, boolean postMethod)
			throws OAuthClientException, IOException {
		if (!this.isTokenGot) {
			throw new OAuthClientException(OAuthClientExceptionEnum.MISS_TOKEN);
		}

		Map<String, String> header = new HashMap<>();
		header.put("Authorization", String.format("%s %s", this.token.getToken_type(), this.token.getAccess_token()));

		String fullUrl = this.getBaseUrl() + url;

		return HttpUtil.requestJson(responseClass, fullUrl, paramMap, postMethod, header);
	}

	public UserTokenBean getToken() {
		return token;
	}

	public String getCode() {
		return code;
	}

	/**
	 * 根据code，获得token，然后去获取用户信息
	 * 
	 * @param code
	 *            appserver传过来的code
	 */
	public UserResponse getUserInfo(String code) throws IOException, OAuthClientException {
		Assert.notNull(code,"授权码不能为空");

		// 先获得token
		this.createToken(code);

		// 然后根据token去获得用户信息
		UserResponse userBean = this.getResource(UserResponse.class, "/resources/customers/userInfo", null, false);
		return userBean;
	}

}
