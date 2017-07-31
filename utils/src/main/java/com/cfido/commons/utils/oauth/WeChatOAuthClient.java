package com.cfido.commons.utils.oauth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.cfido.commons.beans.exceptions.WeChatApiException;
import com.cfido.commons.beans.oauth.UserTokenBean;
import com.cfido.commons.beans.oauth.WeChatUserInfoBean;
import com.cfido.commons.utils.utils.EncodeUtil;
import com.cfido.commons.utils.utils.HttpUtil;
import com.cfido.commons.utils.utils.HttpUtilException;

/**
 * <pre>
 * 微信相关
 * </pre>
 */
public class WeChatOAuthClient {

	public enum SCOPE {
		/**
		 * 不弹出授权页面，直接跳转，只能获取用户openid
		 */
		snsapi_base,

		/**
		 * 弹出授权页面，可通过openid拿到昵称、性别、所在地。并且，即使在未关注的情况下，只要用户授权，也能获取其信息
		 */
		snsapi_userinfo
	}

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WeChatOAuthClient.class);

	private static final String URL_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";

	private static final String URL_REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
	private static final String URL_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";

	/** 微信给的 appid */
	private final String appId;

	/** 微信给的 appSecert */
	private final String appSecert;

	/** 是否已经获得token了 */
	private boolean isTokenGot;

	/** 已经获得的 token */
	private UserTokenBean token;

	/** 成功获取/刷新 token 时的时间，用于判断是token超时 */
	private long tokenGotTime;

	/** 微信的用户信息 */
	private WeChatUserInfoBean userInfo;

	public WeChatOAuthClient(String appId, String appSecert) {
		Assert.hasText(appId,"appId不能为空");
		Assert.hasText(appSecert,"app秘钥不能为空");

		this.appId = appId;
		this.appSecert = appSecert;
	}

	/**
	 * 获取微信OAuth授权的URL
	 * 
	 * @param redirectUrl
	 *            授权后重定向的回调链接地址
	 * @param scope
	 *            应用授权作用域
	 * @param state
	 *            重定向后会带上state参数，开发者可以填写任意参数值
	 * 
	 * @param isWebChatRedirect
	 *            是否直接在微信打开链接。做页面302重定向时候，必须为真
	 * @return
	 */
	public String getAuthorizeUrl(String redirectUrl, SCOPE scope, String state, boolean isWebChatRedirect) {
		StringBuilder b = new StringBuilder();
		b.append("https://open.weixin.qq.com/connect/oauth2/authorize")
				.append("?")
				.append("appid=").append(this.appId)
				.append("&")
				.append("redirect_uri=").append(EncodeUtil.url(redirectUrl))
				.append("&")
				.append("response_type=code")
				.append("&")
				.append("scope=").append(scope.toString());

		if (StringUtils.hasText(state)) {
			b.append("&")
					.append("state=").append(EncodeUtil.url(state));
		}

		if (isWebChatRedirect) {
			b.append("#wechat_redirect");
		}

		return b.toString();
	}

	/**
	 * 返回已经获得的token
	 * 
	 * @return
	 */
	public UserTokenBean getToken() {
		return token;
	}

	/**
	 * 返回成功获取token时的时间
	 * 
	 * @return
	 */
	public long getTokenGotTime() {
		return tokenGotTime;
	}

	/**
	 * 获取微信用户信息
	 * 
	 * @return
	 * @throws HttpUtilException
	 * @throws IOException
	 */
	public synchronized WeChatUserInfoBean getUserInfoBean() throws HttpUtilException, IOException {
		Assert.isTrue(this.isTokenGot, "token尚未成功获取");

		// 参数设置
		Map<String, Object> param = new HashMap<>();
		param.put("access_token", this.token.getAccess_token());
		param.put("openid", this.token.getOpenid());

		this.userInfo = HttpUtil.requestJson(WeChatUserInfoBean.class, URL_USER_INFO, param, false, null);

		log.debug("成功获得用户信息 : {}", JSON.toJSONString(this.userInfo, true));

		return this.userInfo;
	}

	/**
	 * 返回是否已经成功获取token
	 * 
	 * @return
	 */
	public boolean isTokenGot() {
		return isTokenGot;
	}

	/**
	 * 判断依据获得的token是否还能用
	 * 
	 * @return
	 */
	public boolean isTokenNotExpire() {
		if (!this.isTokenGot) {
			// 如果根本就没有获得token，就直接返回不能用
			return false;
		} else {
			// 否则就根据 token的expire_in判断是否已经超时了
			long now = System.currentTimeMillis();

			return now - this.tokenGotTime < TimeUnit.SECONDS.toMillis(this.token.getExpires_in());
		}
	}

	/**
	 * 当授权回调的时候，传入code获得token
	 * 
	 * @param code
	 * @throws IOException
	 * @throws HttpUtilException
	 * @throws WeChatApiException
	 */
	public void onAuthorizeCallback(String code) throws HttpUtilException, IOException, WeChatApiException {
		Assert.notNull(code, "'code' 不能为空");

		// 参数设置
		Map<String, Object> param = new HashMap<>();
		param.put("grant_type", "authorization_code");

		param.put("appid", this.appId);
		param.put("secret", this.appSecert);
		param.put("code", code);

		this.beforeGetToken();

		this.token = HttpUtil.requestJson(UserTokenBean.class, URL_ACCESS_TOKEN, param, false, null);

		this.afterGetToken();

		log.info("获得 token 成功：{}", token.getOpenid());
		log.info("获得 token 成功：{}", token.getToken_type());
		log.info("获得 token 成功：{}", token.getRefresh_token());
		log.info("获得 token 成功：{}", token.getScope());
		
		log.info("获得 token 成功：{}", JSON.toJSONString(token, true));
	}

	/**
	 * 刷新token，貌似没啥用
	 * 
	 * @throws IOException
	 * @throws HttpUtilException
	 * @throws WeChatApiException
	 */
	public void refreshToken() throws HttpUtilException, IOException, WeChatApiException {
		Assert.isTrue(this.isTokenGot, "token尚未成功获取");

		// 参数设置
		Map<String, Object> param = new HashMap<>();
		param.put("grant_type", "refresh_token");

		param.put("appid", this.appId);
		param.put("refresh_token", this.token.getRefresh_token());

		this.beforeGetToken();

		this.token = HttpUtil.requestJson(UserTokenBean.class, URL_REFRESH_TOKEN, param, false, null);

		this.afterGetToken();

		log.info("获得 token 成功：{}", JSON.toJSONString(token, true));
	}

	private void afterGetToken() throws WeChatApiException {
		Assert.notNull(this.token, "token 不应该为null");

		this.isTokenGot = false;

		// 检查是否成功
		this.token.checkIsSuccess();

		this.isTokenGot = true;
		this.tokenGotTime = System.currentTimeMillis();
	}

	private void beforeGetToken() {
		this.isTokenGot = false;
		this.token = null;
	}

	/** token 可以从外面设置进来 */
	public void setToken(UserTokenBean token) {
		this.isTokenGot = (token != null);
		this.token = token;
	}

}
