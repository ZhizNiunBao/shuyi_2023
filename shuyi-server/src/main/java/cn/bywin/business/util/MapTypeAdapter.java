package cn.bywin.business.util;

import cn.bywin.business.common.util.ComUtil;
import com.csvreader.CsvWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description
 * @Author wangh
 * @Date 2021-07-27
 */
public class MapTypeAdapter extends TypeAdapter<Object> {

    private final TypeAdapter<Object> delegate = new Gson().getAdapter(Object.class);

    public static Map<String, Object> gsonToMap(String strJson) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                }.getType(), new MapTypeAdapter()).create();
        return gson.fromJson(strJson, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    @Override
    public Object read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        switch (token) {
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList<>();
                in.beginArray();
                while (in.hasNext()) {
                    list.add(read(in));
                }
                in.endArray();
                return list;

            case BEGIN_OBJECT:
                Map<String, Object> map = new LinkedTreeMap<>();
                in.beginObject();
                while (in.hasNext()) {
                    map.put(in.nextName(), read(in));
                }
                in.endObject();
                return map;

            case STRING:
                return in.nextString();

            case NUMBER:
                /**
                 * 改写数字的处理逻辑，将数字值分为整型与浮点型。
                 */
                double dbNum = in.nextDouble();

                // 数字超过long的最大值，返回浮点类型
                if (dbNum > Long.MAX_VALUE) {
                    return String.valueOf(dbNum);
                }

                // 判断数字是否为整数值
                long lngNum = (long) dbNum;
                if (dbNum == lngNum) {
                    return lngNum;
                } else {
                    return dbNum;
                }

            case BOOLEAN:
                return in.nextBoolean();

            case NULL:
                in.nextNull();
                return null;

            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        delegate.write(out, value);
    }

    public static Map<String, Object> checkParams(Map<String, Object> oldParams) {
        Map<String, Object> params = new HashMap<>();
        for (String key : oldParams.keySet()) {
            if (null == oldParams.get(key) || "".equals(oldParams.get(key))) {
                continue;
            } else {
                params.put(key, oldParams.get(key));
            }
        }
        return params;
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String chgTypeCom(String type) {
        String stype = type.toLowerCase();
        if (stype.indexOf("bool") >= 0) {
            return "String";
        }

        if (stype.equals("string") || stype.indexOf("char") >= 0 || stype.indexOf("text") >= 0) {
            return "String";
        }
        if (stype.startsWith("double") || stype.startsWith("decimal") || stype.startsWith("float")) {
            return "Integer";
        }

//        if( stype.indexOf("datetime")>=0){
//            return "Timestamp";
//        }
        if (stype.indexOf("datetime") >= 0 || stype.indexOf("timestamp") >= 0) {
            return "Date";
        }
        if (stype.indexOf("date") >= 0) {
            return "Date";
        }
        if (stype.indexOf("bigint") >= 0) {
            return "Integer";
        }
        if (stype.indexOf("int") >= 0) {
            return "Integer";
        }
        return type.substring(0, 1).toUpperCase() + type.substring(1);
    }

    /**
     * 创建临时的csv文件
     *
     * @return
     * @throws IOException
     */
    public static File createTempFile(List<Map<String, Object>> datas) throws IOException {

        String saveName = ComUtil.dateToStr(new Date(), "yyyyMMhh") + "_" + ComUtil.genId() + ".csv";
        String path = ComUtil.mergePaths(System.getProperty("user.dir"), "data");//ComUtil.mergePaths(tempPath);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File tempFile = new File(ComUtil.mergePaths(path, saveName));
        CsvWriter csvWriter = new CsvWriter(tempFile.getCanonicalPath(), ',', Charset.forName("UTF-8"));
        Arrays.asList(datas.get(0).keySet());
        // 写表头
        String[] headers = String.join(",", datas.get(0).keySet()).split(",");
        csvWriter.writeRecord(headers);
        for (Map<String, Object> data : datas) {
            for (String key : data.keySet()) {
                csvWriter.write(String.valueOf(data.get(key)));
            }
            csvWriter.endRecord();
        }
        csvWriter.close();
        return tempFile;
    }

    /**
     * 删除单个文件
     *
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(File file) {
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public static String makeExeSql(String field, String fieldExp, String sql) {
        String tableName=sql.toLowerCase().split("from")[1].split("limit")[0].trim();
        String fieldType=fieldExp.split("--")[0];
        String fieldSchema=String.format("select column_comment from \n" +
                "INFORMATION_SCHEMA.Columns where table_name='%s' " +
                " and COLUMN_NAME='%s'",tableName,field);
        String intSql = "SELECT '%s' AS \"fieldName\" ,\n" +
                "(%s) AS  \"fieldExpr\",\n" +
                "'%s' AS  \"fieldType\",\n" +
                "COUNT(DISTINCT %s) AS \"distinct_count\",\n" +
                "COUNT(*) AS \"count\" ,\n" +
                "SUM(\n" +
                "CASE\n" +
                " WHEN %s IS NULL THEN 1\n" +
                " ELSE 0\n" +
                " END) \"null_count\" ";
        if ("Integer".equals(fieldType)) {
            intSql = intSql.concat(String.format(",MIN(%s) \"min\",\n" +
                    "MAX(%s) \"max\",\n" +
                    "AVG(%s) \"avg\",\n" +
                    "STDDEV(%s) \"std\"\n", field, field, field, field));
        } else if ("Date".equals(fieldType)) {
            intSql = intSql.concat(String.format(",MIN(%s) \"min\",\n" +
                    "MAX(%s) \"max\"", field, field));
        }
        intSql = intSql.concat(" FROM (%s) as t;");
        String result = String.format(intSql, field, fieldSchema, fieldType, field,
                field, sql);
        return result;

    }

    public static boolean isHostConnectable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (Exception e) {
          //  e.printStackTrace();
            return false;
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    public static boolean isUrlConnect(String url) {
        String ip = "";
        Integer port = 0;
        Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)");
        Matcher m = p.matcher(url);
        while (m.find()) {
            ip = m.group(1);
            port = Integer.parseInt(m.group(2));
        }
        return isHostConnectable(ip, port);
    }
}
