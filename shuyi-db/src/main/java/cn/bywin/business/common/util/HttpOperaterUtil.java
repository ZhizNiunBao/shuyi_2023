package cn.bywin.business.common.util;


import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 处理text/plain类型请求
 *
 * @author Administrator
 */
public class HttpOperaterUtil {


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
        this.dataMap = new HashMap<>();
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
        this.headMap = new HashMap<>();
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


//	public <T> T sendPostForObject(String url, Object param, Class<T> cls) {
//        RestTemplate restTemplate = getRestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity entity = new HttpEntity(param, headers);
//        return restTemplate.postForObject(url, entity, cls);
//    }

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

    public <T> T sendPostRequest(Class<T> responseType) {

        String mediaTypeAndcharset = "application/json; charset=UTF-8";

        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        if (this.appJson) {
            mediaTypeAndcharset = "application/json; charset=" + charset;
            //MimeType mimeType = MimeTypeUtils.parseMimeType(MediaType.APPLICATION_JSON_VALUE);//
            //MediaType mediaType = new MediaType(mimeType.getType(), mimeType.getSubtype());
            MediaType type = MediaType.parseMediaType(mediaTypeAndcharset);
            headers.setContentType(type);
        } else {

            //headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            mediaTypeAndcharset = "application/x-www-form-urlencoded; charset=" + charset;

            //MimeType mimeType = MimeTypeUtils.parseMimeType(MediaType.APPLICATION_JSON_VALUE);//
            //MediaType mediaType = new MediaType(mimeType.getType(), mimeType.getSubtype());
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
        //headers.setContentType(MediaType.MULTIPART_FORM_DATA);
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

    public String sendAsFile(String fileParaName, String filePath) {

        String mediaTypeAndcharset = "application/json; charset=UTF-8";
        //if(StringUtils.isBlank( charset ) )
        //	charset = "UTF-8";
        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("multipart/form-data; charset=" + this.charset);
        headers.setContentType(type);
        //headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        //ByteArrayResource fileSystemResource = new ByteArrayResource (data.getBytes());

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        if (fileParaName != null && fileParaName.length() > 0) {
            FileSystemResource fileSystemResource = new FileSystemResource(filePath);
            map.add(fileParaName, fileSystemResource);
        }
        if (dataMap != null && !dataMap.isEmpty()) {
            Iterator<Map.Entry<String, Object>> iterator = dataMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                map.add(next.getKey(), next.getValue());
            }
        }

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);

        return restTemplate.postForObject(this.strUrl, request, String.class);
    }

    /*public String sendPostRequest(String url, Map<String, Object> params, Map<String, String> headers) {
        HttpPost method = new HttpPost(url);
        List<NameValuePair> pairs = buildParams(params);
        if (pairs.size() > 0) {
            HttpEntity entity = null;
            try {
                entity = new UrlEncodedFormEntity(pairs, Charsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                throw new RestClientException(e.getMessage(), e);
            }
            method.setEntity(entity);
        }
        return send(method, headers);
    }*/

    /*private static String send(HttpUriRequest method, Map<String, String> headers) {
        HttpClient client = HttpClients.createDefault();
        if (headers != null) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                method.setHeader(key, headers.get(key));
            }
        }

        try {
            HttpResponse response = client.execute(method);
            HttpEntity entity = response.getEntity();
            String body = EntityUtils.toString(entity, Charsets.UTF_8);

            return body;
        } catch (IOException e) {
            throw new RestClientException(e.getMessage(), e);
        }
    }*/

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

    public <T> T sendRequest(HttpMethod method, Class<T> cls) {

        String url = buildUrl();

        RestTemplate restTemplate = getRestTemplate();

        //URI url = restTemplate.getUriTemplateHandler().expand( url1,dataMap );
        //System.out.println( url );
        HttpHeaders headers = new HttpHeaders();

        if (appJson) {
            String mediaTypeAndcharset = "application/json; charset=" + charset;
            MediaType type = MediaType.parseMediaType(mediaTypeAndcharset);
            headers.setContentType(type);
        } else {
            String mediaTypeAndcharset = "application/x-www-form-urlencoded; charset=" + charset;
            MediaType type = MediaType.parseMediaType(mediaTypeAndcharset);
            headers.setContentType(type);
            //headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
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
        //return restTemplate.postForObject(strUrl, request, responseType);
        //if( HttpMethod.GET == method || HttpMethod.POST == method ) {
        return restTemplate.exchange(url, method, entity, cls, dataMap).getBody();
        //}
        //else{
        //    restTemplate.exchange(url, method, entity ,cls,dataMap);
        //    return null;
        //}
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

    private String buildUrl1() {
        String url = strUrl;
        if (dataMap != null && !dataMap.isEmpty()) {
            StringBuilder param = new StringBuilder();
            Iterator<Map.Entry<String, Object>> iterator = dataMap.entrySet().iterator();
            //Iterator<String> it = params.keySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                if (next.getValue() == null) {
                    param.append(next.getKey()).append("=").append("").append("&");
                } else {
                    if (dataUrlEncode) {
                        try {
                            param.append(next.getKey()).append("=").append(URLEncoder.encode(next.getValue().toString(), this.charset)).append("&");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        param.append(next.getKey()).append("=").append(next.getValue()).append("&");
                    }
                }
            }
            if (url.indexOf("?") > 0) {
                url = url + "&" + param.toString();
            } else {
                url = url + "?" + param.toString();
            }
        }
        System.out.println(url);
        return url;
    }

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
            if (url.indexOf("?") > 0) {
                url = url + "&" + param.toString();
            } else {
                url = url + "?" + param.toString();
            }
        }
        //System.out.println( url );
        return url;
    }

    private RestTemplate getRestTemplate() {
//        SimpleClientHttpRequestFactory requestFactory = getUnsafeClientHttpRequestFactory();//new SimpleClientHttpRequestFactory();
//        if (this.connectTimeout != null) {
//            requestFactory.setConnectTimeout(this.connectTimeout);
//        }
//        if (this.readTimeOut != null) {
//            requestFactory.setReadTimeout(this.readTimeOut);
//        }

        HttpComponentsClientHttpRequestFactory factory = new
                HttpComponentsClientHttpRequestFactory();
        //factory.setConnectionRequestTimeout(requestTimeout);
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeOut);
        // https
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, (X509Certificate[] x509Certificates, String s) -> true);

            SSLConnectionSocketFactory socketFactory = null;

            socketFactory = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", socketFactory).build();
            PoolingHttpClientConnectionManager phccm = new PoolingHttpClientConnectionManager(registry);
            phccm.setMaxTotal(200);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).setConnectionManager(phccm).setConnectionManagerShared(true).build();
            factory.setHttpClient(httpClient);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        RestTemplate restTemplate = new RestTemplate(factory);
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> httpMessageConverter : list) {
            if (httpMessageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(Charset.forName(charset));
                break;
            }
        }
        return restTemplate;
    }

    private static SimpleClientHttpRequestFactory getUnsafeClientHttpRequestFactory() {
        TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};
        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
            sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }

        return new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection,
                                             String httpMethod) throws IOException {
                super.prepareConnection(connection, httpMethod);
                if (connection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) connection).setSSLSocketFactory(
                            sslContext.getSocketFactory());
                }
            }
        };
    }

    public <T> T sendPostForObject(String url, Object param, Class<T> clz) {

        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();

        String mediaTypeAndcharset = "application/json; charset=" + charset;
        //MimeType mimeType = MimeTypeUtils.parseMimeType(MediaType.APPLICATION_JSON_VALUE);//
        //MediaType mediaType = new MediaType(mimeType.getType(), mimeType.getSubtype());
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
