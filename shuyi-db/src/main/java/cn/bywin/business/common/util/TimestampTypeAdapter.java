package cn.bywin.business.common.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;

public class TimestampTypeAdapter implements JsonDeserializer<Timestamp>{

    public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)  {
        if( json == null ||  json.isJsonNull()  )
            return null;
//        if (!(json instanceof JsonPrimitive)) {
//            throw new JsonParseException("The date should be a string value");
//        }

        String strdt = json.getAsJsonPrimitive().getAsString();
        if( ComUtil.isNumeric( strdt ) && strdt.length() ==13 ){
            return new Timestamp( Long.parseLong( strdt ) );
        }
        Date date = ComUtil.strToDate(strdt);
        return new Timestamp(date.getTime());

    }

}
