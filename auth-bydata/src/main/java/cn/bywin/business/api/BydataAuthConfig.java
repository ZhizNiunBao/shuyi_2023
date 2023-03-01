package cn.bywin.business.api;


import static cn.bywin.business.common.login.LoginUtil.AUTHORIZATION;

import cn.bywin.business.common.login.LoginUtil;
import feign.Client;
import feign.Feign;
import feign.Retryer;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.spring.SpringContract;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * @author zzm
 */
@Configuration
public class BydataAuthConfig {

    @Value("${bydata.authServerUrl}")
    private String authServerUrl;

    @Bean
    public BydataAuthClient bydataAuthClient() {
        Feign.Builder builder = Feign.builder()
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .contract(new SpringContract())
                .retryer(Retryer.NEVER_RETRY)
                .requestInterceptor(template -> {
                    template.header("Content-Type", "application/json");
                    HttpServletRequest request = LoginUtil.getRequest();
                    template.header(AUTHORIZATION, request.getHeader(AUTHORIZATION));
                });
        if (authServerUrl.startsWith("https")) {
            builder.client(new Client.Default(socketFactory(), new NoopHostnameVerifier()));
        }
        return builder.target(BydataAuthClient.class, authServerUrl);
    }

    public static SSLSocketFactory socketFactory() {

        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustManagerImpl()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
        }

        return ssfFactory;
    }

    public static class HostnameVerifierImpl implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }

    }

    public static class TrustManagerImpl implements X509TrustManager, TrustManager {

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }
}
