package cn.bywin.business.hetu;

import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import io.prestosql.jdbc.$internal.okhttp3.OkHttpClient;

import java.io.File;
import java.util.Optional;
import java.util.Properties;

import static cn.bywin.business.hetu.HetuPropertyField.KEYSTORE_PASSWORD;
import static cn.bywin.business.hetu.HetuPropertyField.KEYSTORE_PATH;
import static cn.bywin.business.hetu.HetuPropertyField.KEYTAB_PATH;
import static cn.bywin.business.hetu.HetuPropertyField.KRB5_CONFIG_PATH;
import static cn.bywin.business.hetu.HetuPropertyField.PASSWORD;
import static cn.bywin.business.hetu.HetuPropertyField.PRINCIPAL;
import static cn.bywin.business.hetu.HetuPropertyField.REMOTE_SERVICE_NAME;
import static cn.bywin.business.hetu.HetuPropertyField.TOKEN;
import static cn.bywin.business.hetu.HetuPropertyField.USERNAME;
import static io.prestosql.jdbc.$internal.client.OkHttpUtil.basicAuth;
import static io.prestosql.jdbc.$internal.client.OkHttpUtil.setupKerberos;
import static io.prestosql.jdbc.$internal.client.OkHttpUtil.setupSsl;
import static io.prestosql.jdbc.$internal.client.PrestoHeaders.PRESTO_EXTRA_CREDENTIAL;
import static io.prestosql.jdbc.$internal.client.PrestoHeaders.PRESTO_USER;

/**
 * @author zzm
 */
public class HetuBaseUtil {

    private static final String DEFAULT_USERNAME = "admin";

    /**
     * 一个 OkHttpClient 包含了线程池和连接池，重复创建会导致线程数过多，
     * 所有和 hetu 的请求共用一个 OkHttpClient，不同的配置通过 newBuilder 方法创建
     */
    private static final OkHttpClient COMMON_HTTP_CLIENT = new OkHttpClient();

    /**
     * 根据 hetu 配置创建对应的 http(s) 连接
     * @param hetuInfo hetu 配置
     * @return http连接
     */
    protected static OkHttpClient createHttpClient(HetuInfo hetuInfo) {
        OkHttpClient.Builder newClientBuilder = COMMON_HTTP_CLIENT.newBuilder();

        Properties hetuProperties = hetuInfo.getHetuProperties();

        // 添加用户认证信息
        UserDo userInfo = LoginUtil.getUser();
        if (userInfo != null) {
            newClientBuilder.addInterceptor(
                    chain -> chain.proceed(chain.request().newBuilder()
                            .addHeader(PRESTO_EXTRA_CREDENTIAL, TOKEN.concat("=").concat(userInfo.getTokenId()))
                            .build())
            );
        }

        // 判断是否加密
        if (hetuInfo.useSsl()) {
            setupSsl(newClientBuilder,
                    Optional.of(hetuProperties.getProperty(KEYSTORE_PATH)),
                    Optional.of(hetuProperties.getProperty(KEYSTORE_PASSWORD)),
                    Optional.empty(),
                    Optional.empty());
        } else {
            newClientBuilder.addInterceptor(
                    chain -> chain.proceed(chain.request().newBuilder()
                            .addHeader(PRESTO_USER, DEFAULT_USERNAME)
                            .build()));
        }

        String authType = hetuInfo.getAuthType();
        // 开启 kerberos 认证
        if (AuthType.KERBEROS.equals(authType)) {
            setupKerberos(newClientBuilder,
                    hetuInfo.getServicePrincipalPattern(),
                    hetuProperties.getProperty(REMOTE_SERVICE_NAME),
                    false,
                    Optional.of(hetuProperties.getProperty(PRINCIPAL)),
                    Optional.of(new File(hetuProperties.getProperty(KRB5_CONFIG_PATH))),
                    Optional.of(new File(hetuProperties.getProperty(KEYTAB_PATH))),
                    Optional.empty());
        }
        // 开启 ldap 认证
        else if (AuthType.LDAP.equals(authType)) {
            newClientBuilder.addInterceptor(basicAuth(hetuProperties.getProperty(USERNAME),
                    hetuProperties.getProperty(PASSWORD)));
        }
        return newClientBuilder.build();
    }
}
