package cn.bywin.cache;

import cn.bywin.business.common.encrypt.RSAPage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RsaCache {
    @Value("${redisRsaName:kg_rsa_di}")
    private String redisRsaName ;

    @Autowired
    UserRedisCache userRedisCache;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public RSAPage genRsa() throws Exception{

        RSAPage rsa = null;

        final Map<Object, Object> entries = redisTemplate.opsForHash().entries(redisRsaName);
        try {
            if (!entries.isEmpty()) {
                rsa =  new RSAPage();
                rsa.setPublicKey(entries.get("pub").toString());
                rsa.setPrivateKey(entries.get("priv").toString());
            }
        }
        catch ( Exception ex) {
            ex.printStackTrace();
            rsa = null;
        }
        if( rsa == null ){
            rsa =  new RSAPage();
            rsa.generateKeyPair();
            HashMap<String, String> map = new HashMap<>();
            map.put("pub", rsa.getPublicKey());
            map.put("priv", rsa.getPrivateKey());
            redisTemplate.opsForHash().putAll(redisRsaName, map);
        }
        redisTemplate.expire(redisRsaName,30, TimeUnit.MINUTES);
        return rsa;
    }
}
