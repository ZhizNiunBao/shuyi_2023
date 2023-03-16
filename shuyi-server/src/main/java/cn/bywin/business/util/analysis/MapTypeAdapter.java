package cn.bywin.business.util.analysis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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



}
