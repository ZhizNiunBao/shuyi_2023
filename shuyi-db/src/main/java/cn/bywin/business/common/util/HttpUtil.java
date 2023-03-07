package cn.bywin.business.common.util;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class HttpUtil {

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            if(StringUtils.isNoneBlank(param)){
                url=url + "?" + param;

            }
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            // 建立实际的连接
            connection.connect();
//            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 向指定 URL 发送POST方法的请求
     * @param strURL 发送请求的 URL
     * @param json 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String strURL, String json) {
        String result = "";
        BufferedReader reader = null;
        try {
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Accept", "*/*"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.connect();
            if (json != null && json.trim().length()>0) {
//                byte[] writebytes = params.getBytes();
                // 设置文件长度
                //   connection.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                OutputStream outwritestream = connection.getOutputStream();
                outwritestream.write(json.getBytes("UTF-8"));
                outwritestream.flush();
                outwritestream.close();
            }
            //if (connection.getResponseCode() == 200) {
                reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(),"UTF-8"));
                result = reader.readLine();
            //}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /*public static void main(String[] args) {
        //发送 GET 请求
        String s=HttpUtil.sendGet("http://localhost:8066/taskInstance/countByType", "projectId=9af99d4bfe714fcc968a5d4a69c54d1c");
        System.out.println(s);
        Map<String,String> map=new HashMap<>();
        map.put("username","admin");
        map.put("pwd","123456");
        //发送 POST 请求
        String sr=HttpUtil.sendPost("http://192.168.96.91:8081/login", JsonUtil.serialize(map));
        System.out.println(sr);
        String sql="select ad,e,ewrew,d,sd from where   select  adsadad from ";
        sql=sql.substring(sql.indexOf("select"),sql.indexOf("from"));
        System.out.println(sql);
    }*/


}
