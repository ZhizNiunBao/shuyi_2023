package cn.bywin.config;


import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.encrypt.JwtHs;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.Constants;
import cn.bywin.cache.cache.IUserRedisCache;
import cn.bywin.tools.SpringContextUtil;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@WebFilter(filterName = "sessionFilter", urlPatterns = {"/*"})
@Order(1)
public class LoginContentFilter implements Filter {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    Gson gson = new Gson();

    @Value("${urlIgnorePattern:}")
    private  String urlIgnorePattern;

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    private String readToken(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String token = request.getParameter("token");
        String type = "token";

        if (StringUtils.isBlank(token) || token.length() < 10) {
            type = "ticket";
            token = request.getParameter("ticket");
        }

        if (StringUtils.isBlank(token) || token.length() < 10) {
            token = request.getHeader(Constants.AUTHORIZATION);
            type = Constants.AUTHORIZATION.toUpperCase();
        }
        if (StringUtils.isBlank(token) || token.length() < 10) {
            UserDo tempu = LoginUtil.getUser(request);
            if (tempu != null) {
                type = "session User tokenId: ";
                token = tempu.getTokenId();
            }
        }
        if (StringUtils.isBlank(token) || token.length() < 10) {
            token = "";
        }
        logger.debug("uri:{},{} token:{},para:{},session id:{}", uri, type, token, request.getQueryString(), request.getSession().getId());
        return token;
    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1,
                         FilterChain arg2) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) arg0;
        HttpServletResponse response = (HttpServletResponse) arg1;
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String token = readToken(request);
        UserDo userDo = getUserByToken(token);
        if (userDo != null) {
            userDo.resetLastAct();
            LoginUtil.setUser(request, userDo);
        }
        if (!"OPTIONS".equals(method) && !"/".equals(uri) && !allowRequest(uri)) {
            String result = JwtHs.vaildToken(token);
            if (userDo == null || result == null) {
                Map<String, Object> map = new HashMap<>();
                map.put("statusCode", "1011");
                map.put("statusInfo", "未登录");
                map.put("url", "./index.html");
                map.put("logoutUrl", "./index.html");
                map.put("data", "");
                String resultStr = gson.toJson(map);
                logger.debug(resultStr);
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(gson.toJson(map));
                writer.close();
                return;
            }
        }
        arg2.doFilter(arg0, arg1);
    }

    private Boolean allowRequest(String uri) {

        logger.debug(urlIgnorePattern);
        if (StringUtils.isBlank(urlIgnorePattern)) {
            return false;
        }
        Pattern pattern = Pattern.compile(urlIgnorePattern);
        if (pattern.matcher(uri).find()) {
            logger.debug("{} 验证通过", uri);
            return true;
        }
        return false;
    }

    private UserDo getUserByToken(String token) {
        if (StringUtils.isNotBlank(token) && token.length() > 10) {
            IUserRedisCache userRedisCache = (IUserRedisCache) SpringContextUtil.getBean("userRedisCache");
            UserDo userDo = userRedisCache.getUser(token);
            if (userDo != null && StringUtils.isNotBlank(userDo.getTokenId())) {
                userDo.resetLastAct();
                userRedisCache.setUser(userDo);
                return userDo;
            } else {
                return null;
            }
        }
        return null;
    }

}

