package cn.bywin.business.util;

import cn.bywin.business.common.util.JsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

public class TxtToMap {
    public static List<String> toList(String setInfo) throws IOException {
        List<String> list = new ArrayList<>();
        if( StringUtils.isBlank(  setInfo ) ) {
            return list;
        }
        String info = setInfo.trim();
        if( info.startsWith( "{" )){
            JsonObject jsonObject = JsonUtil.toJsonObject( info );
            for ( Map.Entry<String, JsonElement> dat : jsonObject.entrySet() ) {
                list.add( String.format( "%s=%s", dat.getKey(),dat.getValue().getAsString() ) );
            }
        }
        else{
            Properties pro = new Properties();
            pro.load( new ByteArrayInputStream( info.getBytes() ) );
            for ( String name : pro.stringPropertyNames() ) {
                list.add( String.format( "%s=%s", name,pro.getProperty( name ) ) );
            }
        }
        return list;
    }

    public static Map<String,String> toMap( String setInfo) throws IOException {
        Map<String,String> map = new HashMap<>();
        if( StringUtils.isBlank(  setInfo ) ) {
            return map;
        }
        String info = setInfo.trim();
        if( info.startsWith( "{" )){
            JsonObject jsonObject = JsonUtil.toJsonObject( info );
            for ( Map.Entry<String, JsonElement> dat : jsonObject.entrySet() ) {
                map.put( dat.getKey(),dat.getValue().getAsString() );
            }
        }
        else{
            Properties pro = new Properties();
            pro.load( new ByteArrayInputStream( info.getBytes() ) );
            for ( String name : pro.stringPropertyNames() ) {
                map.put( name,pro.getProperty( name ) );
            }
        }
        return map;
    }
}
