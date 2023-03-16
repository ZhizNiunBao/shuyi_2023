package cn.bywin.config;

import cn.bywin.business.common.util.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 设置feign client 全局请求头部
 * feign client 需要设置configuration属性
 * 使用@configuration 注解
 */
@Slf4j
@Configuration
public class FeignConfiguration implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        if (httpServletRequest!=null){
            Map<String,String> headers = getHeaders(httpServletRequest);
            requestTemplate.header(Constants.AUTHORIZATION, StringUtils.isBlank(headers.get(Constants.AUTHORIZATION))?
                    headers.get(Constants.AUTHORIZATION.toLowerCase()):headers.get(Constants.AUTHORIZATION));
        }
    }
    private HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes!=null){
                return attributes.getRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}

