package cn.bywin.business.util;

import cn.bywin.business.common.util.MyBeanUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @Description
 * @Author wangh
 * @Date 2021-07-27
 */
public class HttpOperaterUtil {
    final String URL_CHAR = "?";
    Integer successCode = 200;
    String charset = "UTF-8";
    Integer readTimeOut = 120000;
    Integer connectTimeout = 30000;

    boolean headerUrlEncode = true;
    boolean dataUrlEncode = false;

    String strUrl;
    Map<String, Object> dataMap = new HashMap<>();
    Map<String, Object> headMap = new HashMap<>();

    boolean appJson = true;

    public String getCharset() {
        return charset;
    }

    public HttpOperaterUtil setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public Integer getReadTimeOut() {
        return readTimeOut;
    }

    public HttpOperaterUtil setReadTimeOut(Integer readTimeOut) {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public HttpOperaterUtil setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public boolean isHeaderUrlEncode() {
        return headerUrlEncode;
    }

    public HttpOperaterUtil setHeaderUrlEncode(boolean headerUrlEncode) {
        this.headerUrlEncode = headerUrlEncode;
        return this;
    }

    public boolean isDataUrlEncode() {
        return dataUrlEncode;
    }

    public HttpOperaterUtil setDataUrlEncode(boolean dataUrlEncode) {
        this.dataUrlEncode = dataUrlEncode;
        return this;
    }

    public String getStrUrl() {
        return strUrl;
    }

    public HttpOperaterUtil setStrUrl(String strUrl) {
        this.strUrl = strUrl;
        return this;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public boolean isAppJson() {
        return appJson;
    }

    public HttpOperaterUtil setAppJson(boolean appJson) {
        this.appJson = appJson;
        return this;
    }

    public HttpOperaterUtil setDataMap(Map<String, ? extends Object> dataMap) {
        this.dataMap = new HashMap<>(8);
        if (dataMap != null && !dataMap.isEmpty()) {
            Iterator<? extends Map.Entry<String, ?>> iterator = dataMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ?> next = iterator.next();
                this.dataMap.put(next.getKey(), next.getValue());
            }
        }

        return this;
    }

    public Map<String, Object> getHeadMap() {
        return headMap;
    }

    public HttpOperaterUtil setHeadMap(Map<String, ? extends Object> headMap) {
        this.headMap = new HashMap<>(8);
        if (headMap != null && !headMap.isEmpty()) {
            Iterator<? extends Map.Entry<String, ?>> iterator = headMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ?> next = iterator.next();
                this.headMap.put(next.getKey(), next.getValue());
            }
        }
        return this;
    }

    public HttpOperaterUtil() {

    }

    public HttpOperaterUtil(String charset, Integer readTimeOut, Integer connectTimeout) {
        this.charset = charset;
        this.readTimeOut = readTimeOut;
        this.connectTimeout = connectTimeout;
    }


    public String sendPostequest(String url, String params) {
        return sendHttp(url, params, "POST");
    }

    public void sendPostequest(String url, String params,String filename, HttpServletResponse response) {
        sendDownPostHttp(url, params, filename,"POST", response);
    }

    public String sendGetRequest(String url, String params) {
        return sendHttp(url, params, "GET");
    }

    public String sendHttpRequest(String url, InputStream inputStream, String fileName) {
        return sendHttp(url, inputStream, fileName);
    }

    public String sendHttpRequest(String url, byte[] fileData, String fileName) {
        return sendHttp(url, fileData, fileName);
    }

    public void sendHttpRequest(String actionUrl, String params, String filename, HttpServletResponse response) {
        sendDownHttp(actionUrl, params, filename, response);
    }

    public static String sendHttp(String url, byte[] fileData, String fileName) {
        CloseableHttpClient client = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(String.valueOf(url));
            MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.setCharset(Charset.forName("UTF-8")).addBinaryBody("file", fileData, ContentType.MULTIPART_FORM_DATA, fileName);
            org.apache.http.HttpEntity httpEntity = builder.build();
            httpPost.setEntity(httpEntity);
            HttpResponse response = client.execute(httpPost);
            org.apache.http.HttpEntity responseEntity = response.getEntity();
            // 将响应内容转换为字符串
            if (responseEntity != null) {
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String sendHttp(String url, InputStream inputStream, String fileName) {
        CloseableHttpClient client = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(String.valueOf(url));
            MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.setCharset(Charset.forName("UTF-8")).addBinaryBody("file", inputStream, ContentType.MULTIPART_FORM_DATA, fileName);
            org.apache.http.HttpEntity httpEntity = builder.build();
            httpPost.setEntity(httpEntity);
            HttpResponse response = client.execute(httpPost);
            org.apache.http.HttpEntity responseEntity = response.getEntity();
            // 将响应内容转换为字符串
            if (responseEntity != null) {
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String sendHttp(String actionUrl, String params, String type) {
        String serverURL = actionUrl;
        StringBuffer sbf = new StringBuffer();
        String strRead = null;
        try {
            URL url = new URL(serverURL);
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) url.openConnection();
            //请求方式
            connection.setRequestMethod(type);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            //header内的的参数在这里set
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            //body参数放这里
            writer.write(params);
            writer.flush();
            if (connection.getResponseCode() != successCode) {
                return null;
            }
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String results = sbf.toString();
        return results;

    }

    public void sendDownPostHttp(String actionUrl, String params,String filename, String type, HttpServletResponse response) {
        String serverURL = actionUrl;
        StringBuffer sbf = new StringBuffer();
        String strRead = null;
        try {
            URL url = new URL(serverURL);
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) url.openConnection();
            //请求方式
            connection.setRequestMethod(type);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            //header内的的参数在这里set
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            //body参数放这里
            writer.write(params);
            writer.flush();
            if (connection.getResponseCode() != successCode) {
            }
            if (response.getStatus() == successCode) {
                response.setHeader("Content-disposition", String.format("attachment;filename=%s.tar.gz", filename));

                InputStream in = connection.getInputStream();
                int bytes = 0;
                byte[] buffer = new byte[1024];
                while ((bytes = in.read(buffer)) != -1) {
                    response.getOutputStream().write(buffer, 0, bytes);
                }
                //  response.getOutputStream().write(is);
//                InputStream is = connection.getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//                while ((strRead = reader.readLine()) != null) {
//                    sbf.append(strRead);
//                    sbf.append("\r\n");
//                }
//                reader.close();
//                connection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDownHttp(String url, String params, String filename, HttpServletResponse response) {
        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(params, headers);
        //get方式 传人boby 需要添加 此方法  //或者在注入之前set该requestfactory
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestWithBodyFactory());
        ResponseEntity<byte[]> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        if (response.getStatus() == successCode) {

            try {
                response.setHeader("Content-disposition", String.format("attachment;filename=%s.tar.gz", filename));
                //getOutputStream引擎创建的字节输出流对象
                response.getOutputStream().write(exchange.getBody());
            } catch (IOException e) {
                throw new IllegalArgumentException("下载失败");
            }
        }
    }

    private static final class HttpComponentsClientHttpRequestWithBodyFactory extends HttpComponentsClientHttpRequestFactory {
        @Override
        protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
            if (httpMethod == HttpMethod.GET) {
                return new HttpGetRequestWithEntity(uri);
            }
            return super.createHttpUriRequest(httpMethod, uri);
        }
    }

    private static final class HttpGetRequestWithEntity extends HttpEntityEnclosingRequestBase {
        public HttpGetRequestWithEntity(final URI uri) {
            super.setURI(uri);
        }

        @Override
        public String getMethod() {
            return HttpMethod.GET.name();
        }
    }

    public <T> T sendPostRequest(String url, Map<String, ? extends Object> params, Class<T> responseType) {
        setStrUrl(url);
        setDataMap(params);
        return sendPostRequest(responseType);
        /*
		//"application/json; charset=UTF-8"
		String mediaTypeAndcharset = "application/json; charset=UTF-8";
		//if(StringUtils.isBlank( charset ) )
		//	charset = "UTF-8";
		RestTemplate restTemplate = getRestTemplate( );
		if( bjson ){
			mediaTypeAndcharset = "application/json; charset=" + charset;
			HttpHeaders headers = new HttpHeaders();
			//MimeType mimeType = MimeTypeUtils.parseMimeType(MediaType.APPLICATION_JSON_VALUE);//
			//MediaType mediaType = new MediaType(mimeType.getType(), mimeType.getSubtype());
			MediaType type = MediaType.parseMediaType( mediaTypeAndcharset );
			headers.setContentType(type);
			HttpEntity entity = null;
			if( params == null || params.isEmpty()) {
				entity = new HttpEntity(headers);
			}
			else {
				entity = new HttpEntity(params, headers);
			}

			return restTemplate.postForObject(url, entity, responseType);
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

			return restTemplate.postForObject( url, request , responseType );
		}*/

    }

    public String sendPostRequest() {
        return sendPostRequest(String.class);
    }

    public <T> T sendPostRequest(Class<T> responseType, String param) {

        String mediaTypeAndcharset = "application/json; charset=UTF-8";

        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        if (this.appJson) {
            mediaTypeAndcharset = "application/json; charset=" + charset;
            //MimeType mimeType = MimeTypeUtils.parseMimeType(MediaType.APPLICATION_JSON_VALUE);//
            MediaType type = MediaType.parseMediaType(mediaTypeAndcharset);
            headers.setContentType(type);
        } else {


            mediaTypeAndcharset = "application/x-www-form-urlencoded; charset=" + charset;

            MediaType type = MediaType.parseMediaType(mediaTypeAndcharset);
            headers.setContentType(type);
        }
        if (this.headMap != null && !headMap.isEmpty()) {
            headMap.forEach((x, y) -> {
                if (y != null) {
                    if (headerUrlEncode) {
                        try {
                            headers.add(x, URLEncoder.encode(y.toString(), charset));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        headers.add(x, y.toString());
                    }
                } else {
                    headers.add(x, "");
                }
            });
        }

        StringEntity entity = null;
        try {
            entity = new StringEntity(param, "UTF-8");
            entity.setContentType("application/json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restTemplate.postForObject(strUrl, entity, responseType);

    }

    public <T> T sendPostRequest(Class<T> responseType) {

        String mediaTypeAndcharset = "application/json; charset=UTF-8";

        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        if (this.appJson) {
            mediaTypeAndcharset = "application/json; charset=" + charset;
            MediaType type = MediaType.parseMediaType(mediaTypeAndcharset);
            headers.setContentType(type);
        } else {

            mediaTypeAndcharset = "application/x-www-form-urlencoded; charset=" + charset;

            MediaType type = MediaType.parseMediaType(mediaTypeAndcharset);
            headers.setContentType(type);
        }
        if (this.headMap != null && !headMap.isEmpty()) {
            headMap.forEach((x, y) -> {
                if (y != null) {
                    if (headerUrlEncode) {
                        try {
                            headers.add(x, URLEncoder.encode(y.toString(), charset));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        headers.add(x, y.toString());
                    }
                } else {
                    headers.add(x, "");
                }
            });
        }
        if (appJson) {
            HttpEntity entity = null;
            if (dataMap == null || dataMap.isEmpty()) {
                entity = new HttpEntity(headers);
            } else {
                entity = new HttpEntity(dataMap, headers);
            }
            return restTemplate.postForObject(strUrl, entity, responseType);
        } else {

            MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
            if (dataMap != null && !dataMap.isEmpty()) {
                Iterator<? extends Map.Entry<String, ?>> iterator = dataMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, ? extends Object> next = iterator.next();
                    map.add(next.getKey(), next.getValue());
                }
            }
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);
            return restTemplate.postForObject(strUrl, request, responseType);
        }
    }

    public String sendPostFile(String url, Map<String, Object> params, String fileParaName, String filePath) {

        String mediaTypeAndcharset = "application/json; charset=UTF-8";
        //if(StringUtils.isBlank( charset ) )
        //	charset = "UTF-8";
        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("multipart/form-data; charset=" + this.charset);
        headers.setContentType(type);
        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add(fileParaName, fileSystemResource);
        if (params != null && !params.isEmpty()) {
            Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                map.add(next.getKey(), next.getValue());
            }
        }

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);

        return restTemplate.postForObject(url, request, String.class);
    }


    public String sendGetRequest() {
        return sendGetRequest(String.class);
    }

    public String sendGetRequest(String url, Map<String, ? extends Object> params) {
        return sendGetRequest(url, params, String.class);
    }

    public <T> T sendGetRequest(String url, Map<String, ? extends Object> params, Class<T> cls) {
        setStrUrl(url);
        setDataMap(params);
        return sendRequest(HttpMethod.GET, cls);
    }

    public <T> T sendGetRequest(Class<T> cls) {
        return sendRequest(HttpMethod.GET, cls);
    }

    public <T> T sendDeleteRequest(Class<T> cls) {
        return sendRequest(HttpMethod.DELETE, cls);
    }

    public String sendDeleteRequest() {
        return sendRequest(HttpMethod.DELETE, String.class);
    }

    public <T> T sendDeleteRequest(String url, Map<String, Object> params, Class<T> cls) {
        setStrUrl(url);
        setDataMap(params);
        return sendRequest(HttpMethod.DELETE, cls);
    }

    public <T> T sendPutRequest(Class<T> cls) {
        return sendRequest(HttpMethod.PUT, cls);
    }

    public String sendPutRequest() {
        return sendRequest(HttpMethod.PUT, String.class);
    }

    public <T> T sendPutRequest(String url, Map<String, Object> params, Class<T> cls) {
        setStrUrl(url);
        setDataMap(params);
        return sendRequest(HttpMethod.PUT, cls);
    }


    public <T> T sendRequest(HttpMethod method, Class<T> cls, Map<String, Object> maps) {

        String url = buildUrl();

        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        if (dataMap != null && !dataMap.isEmpty()) {
            Iterator<? extends Map.Entry<String, ?>> iterator = dataMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ? extends Object> next = iterator.next();
                map.add(next.getKey(), next.getValue());
            }
        }

        if (appJson) {
            String mediaTypeAndcharset = "application/json; charset=" + charset;
            MediaType type = MediaType.parseMediaType(mediaTypeAndcharset);
            headers.setContentType(type);

            String contentLength = "Content-Length";
            MediaType type1 = MediaType.parseMediaType(contentLength);
        } else {
            String mediaTypeAndcharset = "application/x-www-form-urlencoded; charset=" + charset;
            MediaType type = MediaType.parseMediaType(mediaTypeAndcharset);
            headers.setContentType(type);
        }


        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(map, headers);
        return restTemplate.exchange(url, method, entity, cls, dataMap).getBody();
        //}
        //else{
        //    restTemplate.exchange(url, method, entity ,cls,dataMap);
        //    return null;
        //}
    }

    public <T> T sendRequest(HttpMethod method, Class<T> cls) {

        String url = buildUrl();

        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders headers = new HttpHeaders();

        if (appJson) {
            String mediaTypeAndcharset = "application/json; charset=" + charset;
            MediaType type = MediaType.parseMediaType(mediaTypeAndcharset);
            headers.setContentType(type);
        } else {
            String mediaTypeAndcharset = "application/x-www-form-urlencoded; charset=" + charset;
            MediaType type = MediaType.parseMediaType(mediaTypeAndcharset);
            headers.setContentType(type);
        }

        if (this.headMap != null && !headMap.isEmpty()) {
            headMap.forEach((x, y) -> {
                if (y != null) {
                    if (headerUrlEncode) {
                        try {
                            headers.add(x, URLEncoder.encode(y.toString(), charset));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        headers.add(x, y.toString());
                    }
                } else {
                    headers.add(x, "");
                }
            });
        }

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(null, headers);
        return restTemplate.exchange(url, method, entity, cls, dataMap).getBody();

    }


//    private List<NameValuePair> buildParams(Map<String, Object> params) {
//        List<NameValuePair> pairs = new ArrayList<>();
//        if (params != null) {
//            Set<String> keys = params.keySet();
//            for (String key : keys) {
//                pairs.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
//            }
//        }
//        return pairs;
//    }

//    private String buildUrl1() {
//        String url = strUrl;
//        if (dataMap != null && !dataMap.isEmpty()) {
//            StringBuilder param = new StringBuilder();
//            Iterator<Map.Entry<String, Object>> iterator = dataMap.entrySet().iterator();
//            //Iterator<String> it = params.keySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry<String, Object> next = iterator.next();
//                if (next.getValue() == null) {
//                    param.append(next.getKey()).append("=").append("").append("&");
//                } else {
//                    if (dataUrlEncode) {
//                        try {
//                            param.append(next.getKey()).append("=").append(URLEncoder.encode(next.getValue().toString(), this.charset)).append("&");
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        param.append(next.getKey()).append("=").append(next.getValue()).append("&");
//                    }
//                }
//            }
//            if (url.indexOf("?") > 0) {
//                url = url + "&" + param.toString();
//            } else {
//                url = url + "?" + param.toString();
//            }
//        }
//        System.out.println(url);
//        return url;
//    }

    private String buildUrl() {
        String url = strUrl;
        if (dataMap != null && !dataMap.isEmpty()) {
            StringBuilder param = new StringBuilder();
            Iterator<Map.Entry<String, Object>> iterator = dataMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                if (next.getValue() == null) {
                    param.append(next.getKey()).append("=").append("").append("&");
                } else {
                    param.append(next.getKey()).append("=").append("{").append(next.getKey()).append("}").append("&");
                }

            }
            if (url.indexOf(URL_CHAR) > 0) {
                url = url + "&" + param.toString();
            } else {
                url = url + URL_CHAR + param.toString();
            }
        }
        return url;
    }

    private RestTemplate getRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        if (this.connectTimeout != null) {
            requestFactory.setConnectTimeout(this.connectTimeout);
        }
        if (this.readTimeOut != null) {
            requestFactory.setReadTimeout(this.readTimeOut);
        }
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> httpMessageConverter : list) {
            if (httpMessageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(Charset.forName(charset));
                break;
            }
        }
        return restTemplate;
    }

    public <T> T sendPostForObject(String url, Object param, Class<T> clz) {
        setStrUrl(url);
        MyBeanUtils.copyBean2Map(dataMap, param);
        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();

        String mediaTypeAndcharset = "application/json; charset=" + charset;
        MediaType type = MediaType.parseMediaType(mediaTypeAndcharset);
        headers.setContentType(type);

        if (this.headMap != null && !headMap.isEmpty()) {
            headMap.forEach((x, y) -> {
                if (y != null) {
                    if (headerUrlEncode) {
                        try {
                            headers.add(x, URLEncoder.encode(y.toString(), charset));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        headers.add(x, y.toString());
                    }
                } else {
                    headers.add(x, "");
                }
            });
        }

        HttpEntity entity = null;
        if (param == null) {
            entity = new HttpEntity(headers);
        } else {
            entity = new HttpEntity(dataMap, headers);
        }

        return restTemplate.postForObject(strUrl, entity, clz);

    }
//    public <T> T sendPostForObject(String url, Object param, HttpServletRequest request, Class<T> clz) {
//        UserDo userDo = LoginUtil.getUser(request);
//        if (userDo == null) {
//            throw new IllegalArgumentException("用户未登录");
//        }
//        RestTemplate restTemplate = getRestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("Authorization", userDo.getTokenId());
//        HttpEntity entity = new HttpEntity(param, headers);
//        return restTemplate.postForObject(url, entity, clz);
//    }

//    public <T> T sendPostRequestWithToken1(String url, Map<String, Object> params, HttpServletRequest request,Class<T> clz) {
//        UserDo userDo = LoginUtil.getUser(request);
//        if (userDo == null) {
//            throw new IllegalArgumentException("用户未登录");
//        }
//
//        HashMap<String,String> head = new HashMap<>();
//        head.put("Authorization",userDo.getTokenId());
//        setHeadMap( head );
//        return sendPostRequest( url,params,clz);
//
//    }

//    public Object sendGetRequestWithToken2(String url, Map<String, Object> params, HttpServletRequest request) {
//        UserDo userDo = LoginUtil.getUser(request);
//        if (userDo == null) {
//            throw new IllegalArgumentException("用户未登录");
//        }
//
//        HashMap<String,String> head = new HashMap<>();
//        head.put("Authorization",userDo.getTokenId());
//        setHeadMap( head );
//        return sendGetRequest( url,params,String.class );
//    }

}
