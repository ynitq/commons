package com.cfido.commons.utils.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

/**
 * <pre>
 * 带cookie保存的httpclient封装，可用于模拟登陆，并抓取网站数据
 * </pre>
 * 
 * @author 梁韦江 2017年3月14日
 */
public class CookieSavedHttpClient {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CookieSavedHttpClient.class);

	private static final int TIMEOUT = 15000;

//	/**
//	 * 将map装成为 httpclient接受的NameValuePair参数
//	 * 
//	 * @param paramMap
//	 * @return
//	 */
//	private static List<NameValuePair> mapToList(Map<String, Object> paramMap) {
//		if (paramMap != null) {
//			List<NameValuePair> list = new LinkedList<>();
//			Set<Map.Entry<String, Object>> set = paramMap.entrySet();
//			for (Entry<String, Object> en : set) {
//				list.add(new BasicNameValuePair(en.getKey(), en.getValue() == null ? "" : en.getValue().toString()));
//			}
//			return list;
//		} else {
//			return null;
//		}
//	}

	/**
	 * HTTP request 的配置，主要是设置各类timeout
	 */
	private RequestConfig requestConfig;

	private SSLConnectionSocketFactory sslConnectionSocketFactory;

	/** 创建一个本地上下文信息 */
	private final HttpContext localContext = new BasicHttpContext();
	/** 创建一个本地Cookie存储的实例 */
	private final CookieStore cookieStore = new BasicCookieStore();

	/** 每个实例随机使用一个浏览器的类型 */
	private final String userAgent = HttpHeaderMocker.randomUserAgent();

	private final String charset;

	private HttpResponse lastHttpResponse;
	private String lastContent;
	private boolean autoRedirect = false;

	/** 默认是认为目标网站是utf-8编码的 */
	public CookieSavedHttpClient() {
		this("UTF-8");
	}

	public CookieSavedHttpClient(String charSet) {

		this.charset = charSet;

		// 在本地上下文中绑定这个本地存储，用于每次请求
		localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

		// 设置默认的timeout时间
		this.requestConfig = RequestConfig.custom()
				.setSocketTimeout(TIMEOUT)
				.setConnectTimeout(TIMEOUT)
				.setConnectionRequestTimeout(TIMEOUT)
				.build();
	}

	/**
	 * 获得一个httpClient， 自动识别是否是https模式
	 * 
	 * @param url
	 * @return
	 */
	private CloseableHttpClient createClient(String url) {
		this.clearLast();

		if (StringUtils.isEmpty(url)) {
			return null;
		}

		CloseableHttpClient client = null;

		// 判断是否是https
		if (url.toLowerCase().startsWith("https://")) {
			SSLConnectionSocketFactory sslsf = this.getSSLConnectionSocketFactory();
			if (sslsf != null) {
				client = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			} else {
				log.warn("无法使用https模式");
			}
		}

		if (client == null) {
			client = HttpClients.createDefault();
		}

		return client;
	}

	/**
	 * 执行一个 http request，并将结果视作json字符串，并解析到一个bean里面
	 * 
	 * @param responseClass
	 * @param url
	 * @param paramMap
	 * @param postMethod
	 * @param header
	 * @return
	 * @throws HttpUtilException
	 * @throws IOException
	 */
	public <T> T executeJson(Class<T> responseClass, String url, List<NameValuePair> paramList, boolean postMethod,
			Map<String, String> header) throws HttpUtilException, IOException {
		String res = this.execute(url, paramList, postMethod, header, null);

		log.debug("请求 {} 获得的内容为：\n{}", url, res);

		return JSON.parseObject(res, responseClass);
	}

	public String execute(String url, List<NameValuePair> paramList, boolean postMethod) throws HttpUtilException, IOException {
		return this.execute(url, paramList, postMethod, null, null);
	}

	private void clearLast() {
		this.lastHttpResponse = null;
		this.lastContent = null;
	}

	/**
	 * 执行http请求
	 * 
	 * @param url
	 *            url
	 * @param paramMap
	 *            参数
	 * @param postMethod
	 *            是否post
	 * @param header
	 *            额外的header
	 * @param referer
	 *            来源
	 * @return
	 * @throws HttpUtilException
	 * @throws IOException
	 */
	public String execute(String url, List<NameValuePair> paramList, boolean postMethod,
			Map<String, String> header, String referer) throws HttpUtilException, IOException {

		log.debug("执行 {}:{}", postMethod ? "POST" : "GET", url);
		this.clearLast();
		
//		List<NameValuePair> paramList = mapToList(paramMap);

		CloseableHttpClient httpClient = this.createClient(url);

		CloseableHttpResponse response = null;
		// 配置URI
		try {

			HttpRequestBase method;
			if (!postMethod) {

				if (paramList != null) {
					URIBuilder builder = new URIBuilder(url);
					builder.setParameters(paramList);
					method = new HttpGet(builder.build());
				} else {
					method = new HttpGet(url);
				}

			} else {
				HttpPost post = new HttpPost(url);
				if (paramList != null) {
					post.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
				}
				method = post;
			}

			// 增加header
			this.addHeaderInfo(method, header, referer);

			method.setConfig(requestConfig);

			response = httpClient.execute(method, this.localContext);

			return getResult(response);
		} catch (URISyntaxException e) {
			throw new HttpUtilException(e);
		} catch (UnsupportedEncodingException e) {
			throw new HttpUtilException(e);
		} finally {
			// 关闭连接,释放资源
			if (response != null) {
				response.close();
			}
			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

	/**
	 * 增加header
	 */
	private void addHeaderInfo(HttpRequestBase method, Map<String, String> header, String referer) {

		HttpHeaderMocker.addCommonHeaders(method);

		if (header != null) {
			Set<Map.Entry<String, String>> set = header.entrySet();
			for (Entry<String, String> en : set) {
				method.addHeader(en.getKey(), en.getValue());
			}
		}
		method.addHeader("User-Agent", this.userAgent);

		if (StringUtils.hasText(referer)) {
			method.addHeader("Referer", referer);
		}
	}

	/**
	 * 通过httppost传输文件
	 * 
	 * @param url
	 *            推送url
	 * @param fieldName
	 *            文件参数名
	 * @param file
	 *            文件
	 * @param paramMap
	 *            其他参数对
	 * @return 服务器返回的内容
	 * @throws IOException
	 * @throws HttpUtilException
	 */
	public String executePostFile(String url, String fieldName, File file, Map<String, Object> paramMap,
			Map<String, String> header)
			throws IOException, HttpUtilException {
		this.clearLast();

		CloseableHttpClient httpClient = this.createClient(url);

		CloseableHttpResponse response = null;
		// 配置URI
		try {

			HttpPost method = new HttpPost(url);

			// 增加header信息
			this.addHeaderInfo(method, header, null);

			// 增加文件
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addBinaryBody(fieldName, file, ContentType.DEFAULT_BINARY, file.getName());

			// 增加 各项参数
			if (paramMap != null) {
				for (Map.Entry<String, Object> en : paramMap.entrySet()) {
					builder.addTextBody(en.getKey(), en.getValue().toString(), ContentType.DEFAULT_BINARY);
				}
			}

			method.setEntity(builder.build());

			method.setConfig(requestConfig);
			response = httpClient.execute(method, this.localContext);
			return this.getResult(response);
		} finally {
			// 关闭连接,释放资源
			if (response != null) {
				response.close();
			}
			if (httpClient != null) {
				httpClient.close();
			}

		}
	}

	/** 可以另外设置 timeout的时间 */
	public void setRequestConfig(RequestConfig requestConfig) {
		this.requestConfig = requestConfig;
	}

	/**
	 * 获取结果
	 *
	 * @param response
	 *            响应
	 * @return 响应结果
	 * @throws ParseException
	 * @throws IOException
	 * @throws HttpUtilException
	 */
	private String getResult(HttpResponse response) throws HttpUtilException, IOException {

		this.lastHttpResponse = response;

		int statusCode = response.getStatusLine().getStatusCode();

		if (log.isDebugEnabled()) {
			log.debug("Response: statusCode:{}, \n{}, \n{} ",
					statusCode, this.getCookieInfo(), this.getHeaderInfo(response));
		}

		// 判断是有重定向，
		Header location = response.getFirstHeader("Location");
		if (this.autoRedirect && location != null && StringUtils.hasText(location.getValue())) {
			// 如果有，需要抓取重定向的内容

			log.debug("抓取页面后发现该页面定向到 {} , 继续抓取重定向后的页面", location.getValue());

			return this.execute(location.getValue(), null, false, null, null);
		} else {
			// 否则就返回内容
			String content;
			try {
				HttpEntity httpEntity = response.getEntity();
				content = EntityUtils.toString(httpEntity, this.charset);
				this.lastContent = content;
			} catch (ParseException e) {
				throw new HttpUtilException(e);
			}

			if (statusCode < 400) {
				return content;
			} else {
				throw new HttpUtilException(response.getStatusLine(), content);
			}
		}
	}

	/**
	 * 信任所有证书的ssl链接工厂
	 * 
	 * @return
	 */
	private SSLConnectionSocketFactory getSSLConnectionSocketFactory() {
		if (this.sslConnectionSocketFactory == null) {
			try {

				TrustStrategy trustStrategy = new TrustStrategy() {

					@Override
					public boolean isTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
						// 信任所有
						return true;
					}

				};

				SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, trustStrategy).build();
				this.sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
			} catch (Exception e) {
				log.warn("创建ssl链接工厂时发生了错误", e);
			}
		}
		return this.sslConnectionSocketFactory;
	}

	/**
	 * 获得本地存储的cookie信息，方便调试
	 */
	private String getCookieInfo() {
		StringBuffer sb = new StringBuffer("本地Cookie:");
		List<Cookie> cookies = cookieStore.getCookies();
		for (Cookie cookie : cookies) {
			sb.append("\n\t").append(cookie.toString());
		}
		return sb.toString();
	}

	/**
	 * 获取返回的response中的所有头信息，方便调试
	 * 
	 * @param response
	 * @return
	 */
	private String getHeaderInfo(HttpResponse response) {
		StringBuffer sb = new StringBuffer("返回的Header:");
		// 获取消息头的信息
		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {
			sb.append("\n\t").append(header.toString());
		}
		return sb.toString();
	}

	/**
	 * 设置是否自动重定向
	 */
	public void setAutoRedirect(boolean autoRedirect) {
		this.autoRedirect = autoRedirect;
	}

	/**
	 * 返回最后一次请求的Response
	 */
	public HttpResponse getLastHttpResponse() {
		return lastHttpResponse;
	}

	/**
	 * 返回最后一次请求时获得的内容
	 */
	public String getLastContent() {
		return lastContent;
	}

	/**
	 * 获取最后一次请求返回的Header中的Location，用于判断是否有重定向
	 */
	public String getLastLocation() {
		if (this.lastHttpResponse != null) {
			Header location = lastHttpResponse.getFirstHeader("Location");
			if (location != null) {
				String value = location.getValue();
				if (value != null) {
					return value.trim();
				}
			}
		}
		return null;
	}

	/**
	 * 获取最后一次访问时获得的状态码
	 */
	public int getLastStatusCode() {
		if (this.lastHttpResponse != null) {
			return this.getLastHttpResponse().getStatusLine().getStatusCode();
		} else {
			return -1;
		}
	}

}
