package cn.bywin.business.common.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.gson.stream.JsonToken.BOOLEAN;
import static com.google.gson.stream.JsonToken.NULL;

public class JsonUtil {
    public static class GsonTypeAdapter extends TypeAdapter<Object> {
        @Override
        public Object read(JsonReader in) throws IOException {
            // 反序列化
            JsonToken token = in.peek();
            switch (token) {
                case BEGIN_ARRAY:

                    List<Object> list = new ArrayList<Object>();
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(read(in));
                    }
                    in.endArray();
                    return list;

                case BEGIN_OBJECT:

                    Map<String, Object> map = new HashMap<String, Object>();
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
                    if (dbNum > Long.MAX_VALUE)
                    {
                        return dbNum;
                    }
                    // 判断数字是否为整数值
                    long lngNum = (long) dbNum;
                    if (dbNum == lngNum)
                    {
                        return lngNum;
                    } else
                    {
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
            // 序列化不处理
        }
    }

    public static Gson gson() {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter())
                .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                }.getType(), new GsonTypeAdapter())
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .disableHtmlEscaping()
                .create();
        return gson;
    }

    public static JsonObject toJsonObject(String json) {
        if (StringUtils.isBlank(json))
            return null;
        return gson().fromJson(json, JsonObject.class);
    }

    public static JsonArray toArray(String json) {
        if (StringUtils.isBlank(json))
            return null;
        return gson().fromJson(json, JsonArray.class);
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json))
            return null;

        return gson().fromJson(json, clazz);
    }

    public static <T> T deserialize(String json, Type clazz) {
        // Type jsonType = new TypeToken<List<T>>() { }.getType();
        if (StringUtils.isBlank(json))
            return null;
        return gson().fromJson(json, clazz);
    }

    public static <T> T deserializeAsList(JsonArray json, Type clazz) {
        if (json == null)
            return null;
        return gson().fromJson(json, clazz);
    }

    public static <T> T deserializeAsList(JsonElement json, Type clazz) {
        if (json == null)
            return null;
        return gson().fromJson(json, clazz);
    }

    public static String toJson(Object obj) {
        if (obj == null)
            return null;
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .disableHtmlEscaping()
                .create();
        return gson.toJson(obj);
    }

    public static String toNotPrettyJson(Object obj) {
        if (obj == null)
            return null;
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();
        return gson.toJson(obj);
    }

    public static String toSimpleJson(Object obj) {
        if (obj == null)
            return null;
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();
        return gson.toJson(obj);
    }


}
