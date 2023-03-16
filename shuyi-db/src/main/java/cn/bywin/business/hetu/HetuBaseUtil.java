package cn.bywin.business.hetu;

import static io.prestosql.jdbc.$internal.client.PrestoHeaders.PRESTO_USER;

import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import io.prestosql.jdbc.$internal.okhttp3.OkHttpClient;
import java.util.Properties;

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
                            .build())
            );
        }

        newClientBuilder.addInterceptor(
                chain -> chain.proceed(chain.request().newBuilder()
                        .addHeader(PRESTO_USER, DEFAULT_USERNAME)
                        .build()));
        return newClientBuilder.build();
    }
}
