package cn.bywin.business.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 处理text/plain类型请求
 * @author Administrator
 *
 */
public class HttpRequestUtil {
	
	private HttpServletRequest req;
	private JsonObject json;

	private HttpRequestUtil() {
		
	}
	
	private HttpRequestUtil(HttpServletRequest request)  {
		this.req=request;
		json = readHttpBody(req);
	}
	
	public static HttpRequestUtil parseHttpRequest( HttpServletRequest request ) {
		return new HttpRequestUtil( request );
	}
	
	private JsonObject readHttpBody(HttpServletRequest request) {
    	try {
    		String header =  request.getHeader("content-type");
    		if( header == null ) 
    			header = "";
    		else 
    			header = header.toLowerCase();
    		if( header.indexOf( "application/json")<0 
    				&& header.indexOf("text/plain") <0 ) {
    			return null;
    		}
	    	StringBuilder sb = new StringBuilder();
	        try(BufferedReader reader = request.getReader();) {
	                 char[]buff = new char[10240];
	                 int len;
	                 while((len = reader.read(buff)) != -1) {
	                          sb.append(buff,0, len);
	                 }
	        }catch (IOException e) {
	                 e.printStackTrace();
	        }
			if( sb.length()>0 ) {

				//System.out.println( sb.toString() );
				JsonObject jsonObject =	JsonUtil.deserialize(sb.toString(), JsonObject.class);
				return jsonObject;
			}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
	public String getNvlPara(String name) {
    	String val =  req.getParameter(name);
    	if( val == null )
    		val = "";
    	if( val.length() == 0 && json != null && json.keySet().contains(name) && json.get(name) != null && !json.get(name).isJsonNull() ) {
    		val = json.get(name).getAsString();
    	}
    	return val;
    		
    }
	public JsonObject getJsonObject(String name) {
		if( json != null && json.keySet().contains(name) && json.get(name).isJsonObject() ) {
			return json.get(name).getAsJsonObject();
		}
		return null;
	}
	public JsonArray getJsonArray(String name) {
		if( json != null && json.keySet().contains(name) && json.get(name).isJsonArray() ) {
			return json.get(name).getAsJsonArray();
		}
		return null;
	}

	/**
	 * 取参数值 为null时转成""
	 * @param name
	 * @return
	 */
	public String[] getNvlParaArray(String name ) {
		String[] val =  req.getParameterValues(name);
		if( val != null && val.length>0 )
			return val;
		if( json != null && json.keySet().contains(name) && json.get(name) != null ) {
			JsonArray ja = json.get(name).getAsJsonArray();
			if( ja == null || ja.size()==0)
				return  null;
			val  =new String[ja.size()];
			for (int i = 0; i < ja.size(); i++) {
				//System.out.println(ja.get(i).getAsString());
				val[i] = ja.get(i).getAsString();
			}
			return val;
		}
		return null;

	}
	
	public Map<String, Object> getAllParaData() {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String[]> requestParams = req.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		if( json != null ) {
			Iterator<Map.Entry<String, JsonElement>> iterator = json.entrySet().iterator();
			while( iterator.hasNext()){
				Map.Entry<String, JsonElement> next = iterator.next();
				String key = next.getKey();
				JsonElement kv = next.getValue();
				if( kv == null ||kv.isJsonNull() ){
					params.put(key, null);
				}
				else if( kv.isJsonArray() ){
					params.put(key, kv.getAsJsonArray());
				}
				else if( kv.isJsonArray() ){
					params.put(key, kv.getAsJsonObject());
				}
				else{
					params.put(key, kv.getAsString());
				}
			}
		}
		return params;
	}

	public HttpServletRequest getReq() {
		return req;
	}

	public JsonObject getJson() {
		return json;
	}


	/**
	 * 取body流数据
	 * @param request
	 * @return
	 */
	public static JsonObject httpBody(HttpServletRequest request) {
		try {
			String header = ComUtil.trsEmpty( request.getHeader("content-type")).toLowerCase();
			if( header.indexOf( "application/json")<0
					&& header.indexOf("text/plain") <0 ) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			try(BufferedReader reader = request.getReader();) {
				char[]buff = new char[10240];
				int len;
				while((len = reader.read(buff)) != -1) {
					sb.append(buff,0, len);
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
			if( sb.length()>0 ) {
				JsonObject jsonObject =	JsonUtil.deserialize(sb.toString(),JsonObject.class);
				return jsonObject;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 取参数值 为null时转成""
	 * @param name
	 * @param request
	 * @param json
	 * @return
	 */
	public static String getNvlPara(String name, HttpServletRequest request,JsonObject json) {
		String val =  request.getParameter(name);
		if( val == null )
			val = "";
		if( val.length() == 0 && json != null && json.keySet().contains(name) && json.get(name) != null ) {
			val = json.get(name).toString();
		}
		return val;

	}

	/**
	 * 取参数值 为null时转成""
	 * @param name
	 * @param request
	 * @param json
	 * @return
	 */
	public static String[] getNvlParaArray(String name, HttpServletRequest request,JsonObject json) {
		String[] val =  request.getParameterValues(name);
		if( val != null && val.length>0 )
			return val;
		if( json != null && json.keySet().contains(name) && json.get(name) != null ) {
			JsonArray ja = json.get(name).getAsJsonArray();
			if( ja == null || ja.size()==0)
				return  null;
			val  =new String[ja.size()];
			for (int i = ja.size() - 1; i >= 0; i--) {
				val[i] = ja.get(i).getAsString();
			}
			return val;
		}
		return null;

	}

	/**
	 * 取参数map
	 * @param request
	 * @return
	 */
	public static Map<String, String> getParaData(HttpServletRequest request ) {
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		return params;
	}

	/**
	 * 取ip
	 * @param request
	 * @return
	 */

	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("Proxy-Client-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("WL-Proxy-Client-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("HTTP_CLIENT_IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getRemoteAddr();
		if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException unknownhostexception) {
			}
		}
		if (!StringUtils.isEmpty(ip) && ip.length() > 15) {
			ip = ip.substring(0, ip.indexOf(","));
		}
		return ip;
	}

	/**
	 * 取ip
	 * @param request
	 * @return
	 */

	public static String getAllIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("Proxy-Client-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("WL-Proxy-Client-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("HTTP_CLIENT_IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getRemoteAddr();
		if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException unknownhostexception) {
			}
		}
//		if (!StringUtils.isEmpty(ip) && ip.length() > 15) {
//			ip = ip.substring(0, ip.indexOf(","));
//		}
		return ip;
	}
/*
	public static Object sendPostForObject(String url, Object param, String token) {
		RestTemplate restTemplate = getRestTemplate("utf-8" );
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", token);
		HttpEntity entity = new HttpEntity(param, headers);
		return restTemplate.postForObject(url, entity, JSONObject.class);
	}
	public static Object sendPostForObject(String url, Object param) {
		RestTemplate restTemplate = getRestTemplate("utf-8" );
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity entity = new HttpEntity(param, headers);
		return restTemplate.postForObject(url, entity, JSONObject.class);
	}

	public static Object sendPostRequest(String url, Map<String, Object> params) {

		url = buildUrl(url, params);

		RestTemplate restTemplate = getRestTemplate("utf-8" );

		HttpHeaders headers = new HttpHeaders();
		MimeType mimeType = MimeTypeUtils.parseMimeType(MediaType.APPLICATION_JSON_VALUE);
		MediaType mediaType = new MediaType(mimeType.getType(), mimeType.getSubtype());
		headers.setContentType(mediaType);
		HttpEntity entity = new HttpEntity(headers);

		return restTemplate.postForObject(url, entity, JSONObject.class);
	}

	public static Object sendPostRequest(String url, Map<String, Object> params,boolean bjson,String charset ) {


		//"application/json; charset=UTF-8"
		String mediaTypeAndcharset = "application/json; charset=UTF-8";
		if(StringUtils.isBlank( charset ) )
			charset = "UTF-8";
		RestTemplate restTemplate = getRestTemplate( charset );
		if( bjson ){
			mediaTypeAndcharset = "application/json; charset=" + charset;
			HttpHeaders headers = new HttpHeaders();
			//MimeType mimeType = MimeTypeUtils.parseMimeType(MediaType.APPLICATION_JSON_VALUE);//
			//MediaType mediaType = new MediaType(mimeType.getType(), mimeType.getSubtype());
			MediaType type = MediaType.parseMediaType( mediaTypeAndcharset );
			headers.setContentType(type);
			HttpEntity entity = null;
			if( params == null || params.isEmpty())
				entity = new HttpEntity( headers);
			else
				entity = new HttpEntity( params,headers);

			return restTemplate.postForObject(url, entity, String.class);
		}
		else{
			mediaTypeAndcharset = "application/x-www-form-urlencoded; charset=" + charset;
			HttpHeaders headers = new HttpHeaders();
			//MimeType mimeType = MimeTypeUtils.parseMimeType(MediaType.APPLICATION_JSON_VALUE);//
			//MediaType mediaType = new MediaType(mimeType.getType(), mimeType.getSubtype());
			MediaType type = MediaType.parseMediaType( mediaTypeAndcharset );
			headers.setContentType(type);
			//HttpEntity entity = new HttpEntity( headers);

			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, Object> map= new LinkedMultiValueMap<String, Object>();
			if( params != null && !params.isEmpty()) {
				Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, Object> next = iterator.next();
					map.add(next.getKey(), next.getValue());
				}
			}

			HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);

			return restTemplate.postForObject( url, request , String.class );
		}

	}

	public static Object sendPostRequestWithToken(String url, Map<String, Object> params, HttpServletRequest request) {
		UserDo userDo = LoginUtil.getUser(request);
		if (userDo == null)
			throw new IllegalArgumentException("用户未登录");

		url = buildUrl(url, params);
		RestTemplate restTemplate = getRestTemplate("utf-8" );

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", userDo.getTokenId());
		HttpEntity<JSONObject> httpEntity = new HttpEntity<>(headers);

		return restTemplate.postForObject(url, httpEntity, JSONObject.class);
	}

	public static Object sendGetRequestWithToken(String url, Map<String, Object> params, HttpServletRequest request) {
		UserDo userDo = LoginUtil.getUser(request);
		if (userDo == null)
			throw new IllegalArgumentException("用户未登录");

		RestTemplate restTemplate = getRestTemplate("utf-8" );
		url = buildUrl(url, params);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", userDo.getTokenId());
		HttpEntity<JSONObject> httpEntity = new HttpEntity<>(headers);

		return restTemplate.getForObject(url, JSONObject.class, httpEntity);
	}

	public static Object sendGetRequest(String url, Map<String, Object> params, boolean bjson ) {
		RestTemplate restTemplate = getRestTemplate("utf-8" );
		url = buildUrl(url, params);
		if (bjson)
			return restTemplate.getForObject(url, JSONObject.class);
		return restTemplate.getForObject( url, String.class );
	}

	public static void sendDeleteRequest(String url, Map<String, Object> params ) {
		RestTemplate restTemplate = getRestTemplate("utf-8" );
		url = buildUrl(url, params);
		restTemplate.delete( url );
	}

	public static void sendPutRequest(String url, Map<String, Object> params ) {
		RestTemplate restTemplate = getRestTemplate("utf-8" );
		url = buildUrl(url, params);
		restTemplate.put( url, String.class );
	}

	private static String buildUrl(String url, Map<String, Object> params) {
		if( params != null && !params.isEmpty()) {
			StringBuilder param = new StringBuilder();
			Iterator<String> it = params.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				param.append(key).append("=").append( params.get(key)).append("&");
			}
			if(url.indexOf("?") > 0)
				url = url + "&" + param.toString();
			else
				url = url + "?" + param.toString();
		}
		return url;
	}

	private static RestTemplate getRestTemplate(String charset) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(100000);
		requestFactory.setReadTimeout(100000);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
		for (HttpMessageConverter<?> httpMessageConverter : list) {
			if(httpMessageConverter instanceof StringHttpMessageConverter) {
				((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(Charset.forName(charset));
				break;
			}
		}
		return restTemplate;
	}*/

//	public static void main(String[] args) {
//		HashMap<String,Object> para = new HashMap<>();
//		para.put("id","ssdeede");
//		para.put("name","的的是分色\r\n的是额是给");
//		//"text/plain; charset=UTF-8"
//		//"application/json; charset=UTF-8"
//		HttpRequestUtil.sendPutRequest( "http://localhost:9096/task/bwjob/test",para);
//	}

}
