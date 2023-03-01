package cn.bywin.business.filter;

import cn.bywin.business.api.BydataAuthClient;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.bean.BydataApiResult;
import cn.bywin.business.common.bean.NoLoginVo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.tools.SpringContextUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 中台认证过滤器
 *
 * @author zzm
 */
@Slf4j
public class BydataAuthenticationFilter implements Filter {

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String DEFAULT_CONTENT_TYPE = "application/json; charset=utf-8";

    private String indexUrl;

    private String authMode;

    private String urlIgnorePattern;

    private Pattern pattern;

    private Gson gson = new Gson();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        if (!allowRequest(uri)) {
            String token = LoginUtil.readToken(request);
            log.debug("请求地址: {}, 获取到的 token: {}", request.getRequestURL(), token);

            UserDo userInfo;
            try {
                 userInfo = getUserByToken(token);
            } catch (Exception e) {
                ResponeMap authErrorResult = new ResponeMap();
                authErrorResult.setErr("获取用户信息失败, 请稍后重试");
                writeData(response, authErrorResult.getResultMap());
                return;
            }
            if (userInfo != null) {
                userInfo.resetLastAct();
                LoginUtil.setUser(request, userInfo);
            } else {
                NoLoginVo noLoginVo = new NoLoginVo(authMode, indexUrl, indexUrl);
                writeData(response, noLoginVo);
                return;
            }
        }
        filterChain.doFilter(req, res);
    }

    private void writeData(HttpServletResponse response, Object data) throws IOException {
        String resultStr = gson.toJson(data);
        log.debug(resultStr);
        response.setCharacterEncoding(DEFAULT_CHARSET);
        response.setContentType(DEFAULT_CONTENT_TYPE);
        IOUtils.copy(new ByteArrayInputStream(resultStr.getBytes(DEFAULT_CHARSET)), response.getOutputStream());
    }

    private UserDo getUserByToken(String token) {
        BydataAuthClient authClient = SpringContextUtil.getBean(BydataAuthClient.class);
        BydataApiResult<Map> result = authClient.getUserByToken(token);
        if (!result.isSuccess()) {
            return null;
        }
        Map<String, Object> data = result.getData();
        UserDo userInfo = new UserDo();
        userInfo.setUserId(data.get("userId").toString());
        userInfo.setUserName(data.get("userName").toString());
        userInfo.setChnName(data.get("userName").toString());
        userInfo.setTokenId(data.get("token").toString());
        return userInfo;
    }

    /**
     * 判断 url 是否可以忽略
     * @param uri   uri
     * @return      是否可以忽略
     */
    private Boolean allowRequest(String uri) {
        log.debug(urlIgnorePattern);
        if (pattern == null) {
            return false;
        }
        if (pattern.matcher(uri).find()) {
            log.debug("{} 验证通过", uri);
            return true;
        }
        return false;
    }

    public void setUrlIgnorePattern(String urlIgnorePattern) {
        this.urlIgnorePattern = urlIgnorePattern;
        this.pattern = Pattern.compile(urlIgnorePattern);
    }

    public void setAuthMode(String authMode) {
        this.authMode = authMode;
    }

    public void setIndexUrl(String indexUrl) {
        this.indexUrl = indexUrl;
    }
}
