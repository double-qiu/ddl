package com.ddl.common.httpclient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.ddl.common.httpclient.model.HttpResult;
import com.ddl.common.httpclient.model.ResponseJSON;
import com.ddl.common.utils.ResultJSON;
import com.ddl.common.utils.ResultListJSON;

/**
 * HttpClient调用客户端
 */
public class HttpClientComponent {
	
	private HttpClientComponent() {
		// 初试加载启动线程，清除无效链接
		new IdleConnectionEvictor(HttpClientManager.httpClientConnectionManager).executeClearIdleConnection() ;
	}
	
	// 静态内部类创建单例  线程安全
	private static class SingletonHolder {
		private final static HttpClientComponent INSTANCE = new HttpClientComponent();
    }
	
	// 获取实例
	public static final HttpClientComponent getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	/**  
	 *  getResultListByGet:get方法获取数据
	 *  @return_type:void
	 *  @author DOUBLE
	 *  @param url
	 *  @param params
	 *  @param headers
	 *  @param retryTime
	 *  @throws ClientProtocolException
	 *  @throws IOException
	 *  @throws URISyntaxException  
	 */
	public static List<Object> getResultListByGet(String url, Map<String, String> params, Map<String, String> headers, int retryTime)
			throws Exception {
		HttpResult httpResult = HttpClientComponent.getInstance().doGet(url, params, headers, retryTime);
		List<Object> result = null;
		if(httpResult != null && httpResult.getStatus() == 200) {
			String resultData = httpResult.getData();
			ResultListJSON resultDataJson =  JSON.parseObject(resultData,ResultListJSON.class);  
			 result = resultDataJson.getResult();
		}
		return result;
	}
	
	public static Map<String, Object> getResultMapByGet(String url, Map<String, String> params, Map<String, String> headers, int retryTime)
			throws Exception {
		HttpResult httpResult = HttpClientComponent.getInstance().doGet(url, params, headers, retryTime);
		Map<String, Object>  result = null;
		if(httpResult != null && httpResult.getStatus() == 200) {
			String resultData = httpResult.getData();
			ResultJSON resultDataJson =  JSON.parseObject(resultData,ResultJSON.class);  
			 result = resultDataJson.getResult();
		}
		return result;
	}
	
	public static Map<String, Object> getResultMapByPost(String url, String body, String token)
			throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Content-Type", "application/json");
		httpPost.setHeader("Authorization", "Bearer "+ token);
		httpPost.setEntity(new StringEntity(body));

		CloseableHttpResponse response = httpClient.execute(httpPost);
		HttpResult httpResult = new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), "UTF-8"));
		Map<String, Object>  result = null;
		if(httpResult != null && httpResult.getStatus() == 200) {
			String resultData = httpResult.getData();
			ResultJSON resultDataJson =  JSON.parseObject(resultData,ResultJSON.class);  
			result = resultDataJson.getResult();
		}
		return result;
	}
	
	public static String getResultGet(String url, Map<String, String> params, Map<String, String> headers, int retryTime)
			throws Exception {
		HttpResult httpResult = HttpClientComponent.getInstance().doGet(url, params, headers, retryTime);
		String  result = null;
		if(httpResult != null && httpResult.getStatus() == 200) {
			String resultData = httpResult.getData();
			ResponseJSON resultDataJson =  JSON.parseObject(resultData,ResponseJSON.class);  
			result = resultDataJson.getData();
		}
		return result;
	}
	
	public static String getResultPost(String url, String body, String token,  int retryTime)  {
		CloseableHttpResponse response = null;
		String result = null;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Connection", "close"); 
		CloseableHttpClient httpClient = null;
		try {
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.setHeader("Authorization", "Bearer "+ token);
			httpPost.setEntity(new StringEntity(body));
			httpClient = HttpClientManager.createCloseableHttpClient(retryTime);
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String responseContent = EntityUtils.toString(entity, "UTF-8"); 
			int status = response.getStatusLine().getStatusCode();
			if(status == 200) {
				ResponseJSON responseJson = JSON.parseObject(responseContent,ResponseJSON.class);  
				if(responseJson != null &&  "200".equals(responseJson.getCode())) {
					result = responseJson.getData();
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			httpPost.releaseConnection();
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.SECONDS);
		}
		return result;
	}
	/**
	 * 执行GET请求--无参数
	 * @param url
	 * @param retryTime 重连次数
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public HttpResult doGet(String url, Map<String, String> headers, int retryTime) throws ClientProtocolException, IOException {
		// 创建http GET请求
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Connection", "close"); 
		httpGet.setConfig(HttpClientManager.getRequestConfig());
		// 构造头部信息
		if(headers != null) {
			for(Map.Entry<String, String> header : headers.entrySet()) {
				httpGet.setHeader(header.getKey(), header.getValue());
			}
		}

		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = null;
		try {
			// 执行请求
			httpClient = HttpClientManager.createCloseableHttpClient(retryTime);
			response = httpClient.execute(httpGet);
			return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), "UTF-8"));
		} finally {
			httpGet.releaseConnection();
			if (response != null) {
				response.close();
			}
			httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.SECONDS);
		}
	}

	/**
	 * 带有参数的GET请求
	 * @param url
	 * @param params
	 * @param retryTime 重连次数
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public HttpResult doGet(String url, Map<String, String> params, Map<String, String> headers, int retryTime)
			throws ClientProtocolException, IOException, URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder(url);
		for (String key : params.keySet()) {
			uriBuilder.addParameter(key, String.valueOf(params.get(key)));
		}
		return this.doGet(uriBuilder.build().toString(), headers, retryTime);
	}

	/**
	 * 执行POST请求
	 * @param url
	 * @param params
	 * @param retryTime 重连次数
	 * @return
	 * @throws IOException
	 */
	public HttpResult doPost(String url, Map<String, String> params, Map<String, String> headers, int retryTime) throws IOException {
		// 创建http POST请求
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(HttpClientManager.getRequestConfig());
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		// 构造头部信息
		if(headers != null) {
			for(Map.Entry<String, String> header : headers.entrySet()) {
				httpPost.setHeader(header.getKey(), header.getValue());
				//httpPost.setHeader("Authorization", "Bearer "+token);
				//httpPost.setHeader("Accept", "application/json");
			}
		}
		
		if (params != null) {
			// 设置2个post参数，一个是scope、一个是q
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				parameters.add(new BasicNameValuePair(key, params.get(key)));
			}
			// 构造一个form表单式的实体
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
			// 将请求实体设置到httpPost对象中
			httpPost.setEntity(formEntity);
		}
		CloseableHttpResponse response = null;
		try {
			// 执行请求
			response = HttpClientManager.createCloseableHttpClient(retryTime).execute(httpPost);
			return new HttpResult(response.getStatusLine().getStatusCode(),
					EntityUtils.toString(response.getEntity(), "UTF-8"));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * 执行POST请求--无参数
	 * @param url
	 * @param retryTime 重连次数
	 * @return
	 * @throws IOException
	 */
	public HttpResult doPost(String url, Map<String, String> headers, int retryTime) throws IOException {
		return this.doPost(url, null, headers, retryTime);
	}

	/**
	 * 提交json数据
	 * @param url
	 * @param json
	 * @param retryTime 重连次数
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpResult doPostJson(String url, String json, Map<String, String> headers, int retryTime) throws ClientProtocolException, IOException {
		// 创建http POST请求
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(HttpClientManager.getRequestConfig());
		if(headers != null) {
			for(Map.Entry<String, String> header : headers.entrySet()) {
				httpPost.setHeader(header.getKey(), header.getValue());
				//httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
				//httpPost.setHeader("Authorization", "Bearer "+token);
				//httpPost.setHeader("Accept", "application/json");
			}
		}
		
		if (json != null) {
			// 构造一个form表单式的实体
			/**
			 * 这里是json数据的请求头，所以服务端需要是下面的json请求头，否则无法接收到json串数据
			 *  @Path("demo")
				@POST
				@Produces("application/json")
				public String demo(String json)
			 */
			StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
			// 将请求实体设置到httpPost对象中
			httpPost.setEntity(stringEntity);
		}

		CloseableHttpResponse response = null;
		try {
			// 执行请求
			response = HttpClientManager.createCloseableHttpClient(retryTime).execute(httpPost);
			return new HttpResult(response.getStatusLine().getStatusCode(),
					EntityUtils.toString(response.getEntity(), "UTF-8"));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	
	// =========================== Put请求  ===========================
	/**
	 *  doPut:(无参传递). 
	 *  @return_type:HttpResult
	 *  @author DOUBLE 
	 *  @param url
	 *  @param headers
	 *  @param retryTime 重连次数
	 *  @return
	 *  @throws IOException
	 */
	public HttpResult doPut(String url, Map<String, String> headers, int retryTime) throws IOException {
		return this.doPut(url, null, headers, retryTime);
	}
	
	/**
	 *  doPut:(携带参数调用). 
	 *  @return_type:HttpResult
	 *  @author DOUBLE 
	 *  @param url
	 *  @param params
	 *  @param headers
	 *  @param retryTime 重连次数
	 *  @return
	 *  @throws IOException
	 */
	public HttpResult doPut(String url, Map<String, String> params, Map<String, String> headers, int retryTime) throws IOException {
		// 创建http Put请求
		HttpPut httpPut = new HttpPut(url) ;
		httpPut.setConfig(HttpClientManager.getRequestConfig());
		httpPut.setHeader("Content-Type", "application/x-www-form-urlencoded");
		// 构造头部信息
		if(headers != null) {
			for(Map.Entry<String, String> header : headers.entrySet()) {
				httpPut.setHeader(header.getKey(), header.getValue());
				//httpPost.setHeader("Authorization", "Bearer "+token);
				//httpPost.setHeader("Accept", "application/json");
			}
		}
		
		if (params != null) {
			// 设置2个post参数，一个是scope、一个是q
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				parameters.add(new BasicNameValuePair(key, params.get(key)));
			}
			// 构造一个form表单式的实体
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
			// 将请求实体设置到httpPost对象中
			httpPut.setEntity(formEntity);
		}
		
		CloseableHttpResponse response = null;
		try {
			// 执行请求
			response = HttpClientManager.createCloseableHttpClient(retryTime).execute(httpPut);
			return new HttpResult(response.getStatusLine().getStatusCode(),
					EntityUtils.toString(response.getEntity(), "UTF-8"));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	
	/**
	 *  doPutJson:(Put请求传输Json数据). 
	 *  @return_type:HttpResult
	 *  @author DOUBLE 
	 *  @param url
	 *  @param json
	 *  @param headers
	 *  @param retryTime 重连次数
	 *  @return
	 *  @throws ClientProtocolException
	 *  @throws IOException
	 */
	public HttpResult doPutJson(String url, String json, Map<String, String> headers, int retryTime) throws ClientProtocolException, IOException {
		// 创建http POST请求
		HttpPut httpPut = new HttpPut(url);
		httpPut.setConfig(HttpClientManager.getRequestConfig());
		if(headers != null) {
			for(Map.Entry<String, String> header : headers.entrySet()) {
				httpPut.setHeader(header.getKey(), header.getValue());
			}
		}
		
		if (json != null) {
			// 构造一个form表单式的实体
			/**
			 * 这里是json数据的请求头，所以服务端需要是下面的json请求头，否则无法接收到json串数据
			 *  @Path("demo")
				@POST
				@Produces("application/json")
				public String demo(String json)
			 */
			StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
			// 将请求实体设置到httpPost对象中
			httpPut.setEntity(stringEntity);
		}

		CloseableHttpResponse response = null;
		try {
			// 执行请求
			response = HttpClientManager.createCloseableHttpClient(retryTime).execute(httpPut);
			return new HttpResult(response.getStatusLine().getStatusCode(),
					EntityUtils.toString(response.getEntity(), "UTF-8"));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	
	// =========================== Delete请求  ===========================
	/**
	 *  doDelete:(删除数据). 
	 *  @return_type:String
	 *  @author DOUBLE 
	 *  @param url
	 *  @param headers
	 *  @param retryTime 重连次数
	 *  @return
	 *  @throws ClientProtocolException
	 *  @throws IOException
	 */
	public HttpResult doDelete(String url, Map<String, String> headers, int retryTime) throws ClientProtocolException, IOException {
		// 创建http GET请求
		HttpDelete httpDelete = new HttpDelete(url);
		httpDelete.setConfig(HttpClientManager.getRequestConfig());
		// 构造头部信息
		if(headers != null) {
			for(Map.Entry<String, String> header : headers.entrySet()) {
				httpDelete.setHeader(header.getKey(), header.getValue());
			}
		}

		CloseableHttpResponse response = null;
		try {
			// 执行请求
			response = HttpClientManager.createCloseableHttpClient(retryTime).execute(httpDelete);
			return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), "UTF-8"));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	
	/**
	 *  doDelete:(携带参数删除数据). 
	 *  @return_type:String
	 *  @author DOUBLE 
	 *  @param url
	 *  @param params
	 *  @param headers
	 *  @param retryTime 重连次数
	 *  @return
	 *  @throws ClientProtocolException
	 *  @throws IOException
	 *  @throws URISyntaxException
	 */
	public HttpResult doDelete(String url, Map<String, String> params, Map<String, String> headers, int retryTime)
			throws ClientProtocolException, IOException, URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder(url);
		for (String key : params.keySet()) {
			uriBuilder.addParameter(key, params.get(key));
		}
		return this.doDelete(uriBuilder.build().toString(), headers, retryTime);
	}
	
	/**
	 *  doUpload:(文件上传). 
	 *  @return_type:HttpResult
	 *  @author DOUBLE 
	 *  @param url
	 *  @param localFile
	 *  @param params
	 *  @param retryTime 重连次数 
	 *  @return
	 *  @throws IOException
	 */
	public HttpResult doUpload(String url, String uploadFile, String uploadFileName, Map<String, String> params, int retryTime) throws IOException{
		HttpPost httpPost = new HttpPost(url) ;
		httpPost.setConfig(HttpClientManager.getRequestConfig());
		// 把文件转换成流对象FileBody
		FileBody fileBody = null ;
		if(uploadFileName == null || "".equals(uploadFileName)) {
			fileBody = new FileBody(new File(uploadFile), ContentType.MULTIPART_FORM_DATA) ;
		} else {
			fileBody = new FileBody(new File(uploadFile), ContentType.MULTIPART_FORM_DATA, uploadFileName) ;
		}
		
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create() ;
		// 相当于<input type="file" name="file"/>
		multipartEntityBuilder.addPart("file", fileBody) ;
		
		if(params != null) {
			for(Map.Entry<String, String> par : params.entrySet()) {
				// 相当于<input type="text" name="userName" value=userName>
				multipartEntityBuilder.addPart(par.getKey(), new StringBody(par.getValue(), ContentType.create("text/plain", Consts.UTF_8))) ;
			}
		}
		HttpEntity httpEntity = multipartEntityBuilder.build() ;
		
		httpPost.setEntity(httpEntity);
		
		// 发起请求 并返回请求的响应
		CloseableHttpResponse response = null;
		HttpEntity resEntity = null ;
		try {
			// 执行请求
			response = HttpClientManager.createCloseableHttpClient(retryTime).execute(httpPost);
			// 获取响应对象
			resEntity = response.getEntity();
			return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(resEntity, Charset.forName("UTF-8"))) ;
		} finally {
			if (response != null) {
				response.close();
			}
			
			// 销毁
			if(resEntity != null) {
				EntityUtils.consume(resEntity);
			}
		}
	}
	
	/**
	 *  doDownload:(HttpClient文件下载). 
	 *  @return_type:HttpResult
	 *  @author DOUBLE 
	 *  @param url
	 *  @param headers
	 *  @param retryTime 重连次数
	 *  @param remoteFileName
	 *  @param localFileName
	 *  @return
	 *  @throws IOException
	 */
	public InputStream doDownload(String url, Map<String, String> headers, int retryTime) throws IOException {
		InputStream in = null;
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(HttpClientManager.getRequestConfig());
		/*
		 * httpGet.addHeader("userName", userName);
		   httpGet.addHeader("passwd", passwd);
	       httpGet.addHeader("fileName", remoteFileName);
		 */
		// 构造头部信息
		if(headers != null) {
			for(Map.Entry<String, String> header : headers.entrySet()) {
				httpGet.setHeader(header.getKey(), header.getValue());
			}
		}
		
		try {
			HttpResponse httpResponse = HttpClientManager.createCloseableHttpClient(retryTime).execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			in = httpEntity.getContent();
			
			long length = httpEntity.getContentLength();
			if (length <= 0) {
				throw new RuntimeException("文件不存在......") ;
			}
			
			return in;
		} finally {
			if(in != null) {
				in.close();
			}
		}
	}
}
