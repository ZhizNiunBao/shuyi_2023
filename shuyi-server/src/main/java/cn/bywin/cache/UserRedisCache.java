package cn.bywin.cache;

import cn.bywin.business.common.base.UserDo;
import cn.bywin.cache.cache.IUserRedisCache;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
@Component
public class UserRedisCache implements IUserRedisCache {
    @Value("${userLoginTimeOutMinutes:30}")
    private long minutes = 30L;
    @Value("${userLoginRedisPrefixName:byflhub_}")
    private String userLoginRedisPrefixName ="byflhub_" ;

    Gson gson = new Gson();
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public UserDo getUser(String tokenId ){
        if( StringUtils.isBlank(tokenId ) )
        {
            return null;
        }
        String data  = (String)redisTemplate.opsForValue().get(tokenId);
        if( data == null)
        {
            return null;
        }
        return gson.fromJson( data,UserDo.class);
    }
    @Override
    public void setUser( UserDo user ){
        if(StringUtils.isNotBlank(user.getTokenId())) {
            if( user.getCachSecond() <=0 ){
                user.setCachSecond( minutes *60 );
            }
            redisTemplate.opsForValue().set(user.getTokenId(), gson.toJson(user), user.getCachSecond(), TimeUnit.SECONDS);
        }
    }

    @Override
    public void removeUser(String tokenId) {
        if( StringUtils.isNotBlank(tokenId ) )
        {
            redisTemplate.delete(tokenId);
        }
    }

    public List<UserDo> getAllUsers( ){
        final Set<String> kvs = redisTemplate.keys(userLoginRedisPrefixName+"*");
        if( kvs == null) {
            return null;
        }
        List<UserDo> list = new ArrayList<>();
        for (String kv : kvs) {
            String data  = (String)redisTemplate.opsForValue().get(kv);
            if( StringUtils.isNotBlank( data ) ) {
                UserDo u = gson.fromJson( data,UserDo.class);
                list.add(u);
            }
        }
        return list;
    }

    public static void clear(){

    }
}
