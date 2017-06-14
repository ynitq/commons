package com.cfido.commons.beans.oauth;

/**
 * <pre>
 * 用于用户访问资源时，需要的授权token
 * 通过 oauth/token请求授权时，返回的结果
 * 
 * 遵循 oauth 2.0 规范
 * 
 * 授权请求参数的例子 
 * grant_type=authorization_code 这个是常量，就是说通过code获得token
 * client_id=ios_app : 这是oauth客户端的id，该id的配置必须在oauth服务端有。
 * client_secret=112233 : 和 client_id是一对的配置
 * code=sln9h5 ： 这个是通过访问oauth/authorize请求授权时，得到的code
 * 
 * 发出请求后，就会得到该bean描述的内容。最主要的内容就是access_token
 * 例如 ：
 * {
 * 	"access_token":"93b1801f-1276-4430-820c-f43d382d84c2",
 * 	"refresh_token":"e45b3e6b-ba11-4439-8e05-26742b2903d9",
 * 	"expires_in":43199,
 * 	"scope":"read"
 * 	"token_type":"bearer", //oauth 2.0 有这个，微信没有
 *  "openid": "oLVPpjqs9BhvzwPj5A-vTYAX3GLc", 微信、
 * }
 * 
 * 以后访问该oauth的资源服务器时，都需要发送这个access_token，发送方式是放在文件头
 * 
 * </pre>
 * 
 * @author 梁韦江 2016年7月12日
 */
public class UserTokenBean extends AccessTokenBean {

	/** 例如 ： bearer, 微信的TokenBaen 无此属性 */
	private String token_type;

	private String refresh_token;

	private String scope;

	private String openid;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
