package com.cfido.commons.utils.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;

/**
 * 通过http协议抓起页面的工具类
 * 
 * @author liangwj
 * 
 */
public class HttpUtil implements IHttpUtil {
	private static HttpUtil instance = new HttpUtil();

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HttpUtil.class);

	private static final int TIMEOUT = 15000;

	/**
	 * 统一为 request()
	 */
	@Deprecated
	public static String getContentUseGet(String urlStr, Map<String, Object> paramMap) throws IOException, HttpUtilException {

		return instance.doExecute(urlStr, paramMap, false, null);
	}

	/**
	 * 统一为 request()
	 */
	@Deprecated
	public static String getContentUseHttpClient(String urlStr, Map<String, Object> paramMap, boolean postMethod)
			throws IOException, HttpUtilException {
		return instance.doExecute(urlStr, paramMap, postMethod, null);
	}

	/**
	 * 统一为 request()
	 */
	@Deprecated
	public static String getContentUseHttpClient(String urlStr, Map<String, Object> paramMap, boolean postMethod,
			Map<String, String> header) throws IOException, HttpUtilException {

		return instance.doExecute(urlStr, paramMap, postMethod, header);
	}

	/**
	 * 统一为 request()
	 */
	@Deprecated
	public static String getContentUseHttpClientForRESTFul(String urlStr, Map<String, Object> paramMap, boolean postMethod)
			throws IOException, HttpUtilException {

		return instance.doExecute(urlStr, paramMap, postMethod, null);
	}

	/**
	 * 执行一个http 请求
	 * 
	 * @param urlStr
	 * @param paramMap
	 *            存放参数的map， 可为null
	 * @param postMethod
	 *            是否post
	 * @param header
	 *            附加的header, 可为null
	 * @return
	 * @throws IOException
	 * @throws HttpUtilException
	 */
	public static String request(String urlStr, Map<String, Object> paramMap, boolean postMethod, Map<String, String> header)
			throws IOException, HttpUtilException {

		return instance.doExecute(urlStr, paramMap, postMethod, header);
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
	public static <T> T requestJson(Class<T> responseClass, String url, Map<String, Object> paramMap, boolean postMethod,
			Map<String, String> header) throws HttpUtilException, IOException {
		return instance.executeJson(responseClass, url, paramMap, postMethod, header);
	}

	/**
	 * 上传文件
	 * 
	 * @param url
	 * @param file
	 * @param fieldName
	 * @param paramMap
	 * @return
	 * @throws IOException
	 * @throws HttpUtilException
	 */
	public static String postFileUseHttpClient(String url, File file, String fieldName, Map<String, Object> paramMap)
			throws IOException, HttpUtilException {
		return instance.executePostFile(url, fieldName, file, paramMap, null);

	}

	public static HttpUtil getInstance() {
		return instance;
	}

	/**
	 * 将map装成为 httpclient接受的NameValuePair参数
	 * 
	 * @param paramMap
	 * @return
	 */
	private static List<NameValuePair> mapToList(Map<String, Object> paramMap) {
		List<NameValuePair> list = new LinkedList<>();
		if (paramMap != null) {
			Set<Map.Entry<String, Object>> set = paramMap.entrySet();
			for (Entry<String, Object> en : set) {
				list.add(new BasicNameValuePair(en.getKey(), en.getValue() == null ? "" : en.getValue().toString()));
			}
		}
		return list;
	}

	/**
	 * HTTP request 的配置，主要是设置各类timeout
	 */
	private RequestConfig requestConfig;

	private SSLConnectionSocketFactory sslConnectionSocketFactory;

	/**
	 * 禁止从外部new 实例
	 */
	private HttpUtil() {

	}

	/**
	 * 获得一个httpClient， 自动识别是否是https模式
	 * 
	 * @param url
	 * @return
	 */
	public CloseableHttpClient createClient(String url) {
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
	public <T> T executeJson(Class<T> responseClass, String url, Map<String, Object> paramMap, boolean postMethod,
			Map<String, String> header) throws HttpUtilException, IOException {
		String res = this.doExecute(url, paramMap, postMethod, header);

		log.debug("请求 {} 获得的内容为：\n{}", url, res);

		return JSON.parseObject(res, responseClass);

		// return this.objectMapper.readValue(res, responseClass);
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
	 *            header
	 * @return
	 * @throws HttpUtilException
	 * @throws IOException
	 */
	@Override
	public String doExecute(String url, Map<String, Object> paramMap, boolean postMethod,
			Map<String, String> header) throws HttpUtilException, IOException {

		List<NameValuePair> paramList = mapToList(paramMap);

		CloseableHttpClient httpClient = this.createClient(url);

		CloseableHttpResponse response = null;
		// 配置URI
		try {

			HttpRequestBase method;
			if (!postMethod) {

				URIBuilder builder = new URIBuilder(url);
				if (paramList != null) {
					builder.setParameters(paramList);
				}
				method = new HttpGet(builder.build());
			} else {
				HttpPost post = new HttpPost(url);
				if (paramList != null) {
					post.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
				}
				method = post;
			}

			if (header != null) {
				Set<Map.Entry<String, String>> set = header.entrySet();
				for (Entry<String, String> en : set) {
					method.addHeader(en.getKey(), en.getValue());
				}
			}

			method.setConfig(requestConfig);
			response = httpClient.execute(method);
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

		CloseableHttpClient httpClient = this.createClient(url);

		CloseableHttpResponse response = null;
		// 配置URI
		try {

			HttpPost method = new HttpPost(url);

			// 增加header信息
			if (header != null) {
				Set<Map.Entry<String, String>> set = header.entrySet();
				for (Entry<String, String> en : set) {
					method.addHeader(en.getKey(), en.getValue());
				}
			}

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
			response = httpClient.execute(method);
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

	public RequestConfig getRequestConfig() {
		if (this.requestConfig == null) {
			requestConfig = RequestConfig.custom()
					.setSocketTimeout(TIMEOUT)
					.setConnectTimeout(TIMEOUT)
					.setConnectionRequestTimeout(TIMEOUT)
					.build();
		}
		return this.requestConfig;
	}

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
		int statusCode = response.getStatusLine().getStatusCode();

		String content;
		try {
			HttpEntity httpEntity = response.getEntity();
			content = EntityUtils.toString(httpEntity, "UTF-8");
		} catch (ParseException e) {
			throw new HttpUtilException(e);
		}

		if (statusCode < 400) {
			return content;
		} else {
			throw new HttpUtilException(response.getStatusLine(), content);
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

}
