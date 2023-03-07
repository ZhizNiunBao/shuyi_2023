package cn.bywin.cache;


import cn.bywin.business.common.base.UserDo;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
public class UserCache {
    private static long lastClear = 0;
    private static long ENDClear = 60000L;
    static Map<String, UserDo> userMap = new ConcurrentHashMap();
    private static long remain = 30*60*1000L;
    public static UserDo getUser( String tokenId){
        if( userMap.containsKey(tokenId) )
        {
            UserDo u  = userMap.get(tokenId);
            if( u.getLastAct() + remain> System.currentTimeMillis()){
                return u;
            }
        }
        return null;
    }
    public static void setUser( UserDo user){
        userMap.put(user.getTokenId(),user );
    }

    public static void removeUser(String tokenId) {
        userMap.remove(tokenId);
    }

    public static void clear(){
        try{
            if(lastClear + ENDClear < System.currentTimeMillis()){
                Iterator<String> keys = userMap.keySet().iterator();
                while(keys.hasNext() ){
                    String k = keys.next();
                    UserDo u  = userMap.get(k);
                    if( u.getLastAct() + remain> System.currentTimeMillis()){

                    }
                    else{
                        userMap.remove(k);
                    }
                }
            }
            lastClear = System.currentTimeMillis() + remain;
        }
        catch (Exception e){

        }
    }
}
