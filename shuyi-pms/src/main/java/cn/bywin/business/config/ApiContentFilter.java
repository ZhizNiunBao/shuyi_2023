/*
package cn.bywin.business.config;


import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.encrypt.JwtHs;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.Constants;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
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
public class ApiContentFilter implements Filter, InitializingBean {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${urlIgnorePattern:}")
    private String urlIgnoreString;

    private static Pattern urlIgnorePattern;

    @Override
    public void afterPropertiesSet() {
        urlIgnorePattern = Pattern.compile(urlIgnoreString);
    }

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
            type = "AUTHORIZATION";
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
        if (!urlIgnorePattern.matcher(uri).find()) {
            Gson gson = new Gson();
            String token = readToken(request);
            try {
                if (token == null || JwtHs.vaildToken(token) == null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("statusCode", "1010");
                    map.put("statusInfo", "无效的token");
                    map.put("data", "");
                    map.put("success", false);
                    map.put("msg", "无效的token");
                    String resultStr = gson.toJson(map);
                    logger.info(resultStr);
                    response.setCharacterEncoding("utf-8");
                    response.setContentType("application/json; charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(gson.toJson(map));
                    writer.close();
                    return;
                } else {
                    arg2.doFilter(arg0, arg1);
                    return;
                }
            } catch (Exception e) {
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter writer = response.getWriter();
                writer.write("无效的token");
                writer.close();
                logger.info("无效的token");
            }
        }
        arg2.doFilter(arg0, arg1);
    }
}

*/
