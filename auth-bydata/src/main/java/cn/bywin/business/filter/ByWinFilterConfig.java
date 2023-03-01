package cn.bywin.business.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ByWinFilterConfig {

    @Value("${bydata.authServerUrl}")
    private String indexUrl;

    @Value("${auth.mode}")
    private String authMode;

    @Value("${urlIgnorePattern:}")
    private String urlIgnorePattern;

    @Bean
    public FilterRegistrationBean filterCORSFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new SimpleCORSFilter());
        registration.addUrlPatterns("/*");
        registration.setName("CORSFilter");
        registration.setOrder(0);
        return registration;
    }

    /**
     * 中台权限认证过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean bydataLoginFilter() {
         FilterRegistrationBean registration = new FilterRegistrationBean();
        BydataAuthenticationFilter bydataAuthenticationFilter = new BydataAuthenticationFilter();
        bydataAuthenticationFilter.setAuthMode(authMode);
        bydataAuthenticationFilter.setUrlIgnorePattern(urlIgnorePattern);
        bydataAuthenticationFilter.setIndexUrl(indexUrl);
        registration.setName("BydataAuthenticationFilter");
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        registration.setFilter(bydataAuthenticationFilter);
        return registration;
    }
}
