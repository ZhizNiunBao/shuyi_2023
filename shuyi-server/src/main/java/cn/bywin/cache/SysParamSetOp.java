package cn.bywin.cache;

import cn.bywin.business.bean.system.SysParamSetDo;
import cn.bywin.business.service.system.SysParamSetService;
import cn.bywin.tools.SpringContextUtil;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SysParamSetOp implements ISysParamSetOp{
    static Logger logger = LoggerFactory.getLogger(SysParamSetOp.class);
    private static boolean breset = true;

    @Value("${paraSetRedisPrefixName:flhub_}")
    private String systemPre="flhub_";
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    SysParamSetService paramSetService;

    public static void reInit() {
        SysParamSetOp.breset = true;
    }

    public static String readValue(String code, String def) {
        ISysParamSetOp setOp = (ISysParamSetOp)SpringContextUtil.getBean("sysParamSetOp");
        return  setOp.readParaSetValue( code,def );
    }

    @Override
    public String readParaSetValue(String code, String def) {
        final  String redisKey = systemPre+ "para_set";
        final Boolean binit = redisTemplate.opsForHash().hasKey(redisKey, "init");
        if( !binit  || breset ){
            logger.info( "参数需要重置缓存,key:{},binit:{},breset:{}",redisKey,binit,breset );
            final List<SysParamSetDo> list = paramSetService.findAll();
            HashMap<String,String> map = new HashMap<>(100);
            for (SysParamSetDo sysParamSetDo : list) {
                map.put(sysParamSetDo.getParaCode(),sysParamSetDo.getParaValue());
            }
            map.put("init","true");
            redisTemplate.opsForHash().putAll( redisKey,map );
            redisTemplate.expire(redisKey,6, TimeUnit.HOURS);
            breset = false;
        }
        if( !breset ){
            final String data =(String) redisTemplate.opsForHash().get(redisKey, code);
            if(data == null || data.length() ==0 ) {
                return def;
            }
            else
            {
                return data;
            }
        }
        return paramSetService.findValueByCode(code,def);
    }

    //    public static SysParamSetDo readSet(String code) {
//        SysParamSetService serv = (SysParamSetService)SpringContextUtil.getBean("sysParamSetService");
//        return serv.findByCode(code);
//    }
}
