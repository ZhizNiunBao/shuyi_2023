package cn.bywin.business.hetu;

import cn.bywin.business.bean.olk.TOlkDcServerDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.service.olk.OlkDcServerConfigMapService;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import static cn.bywin.business.hetu.HetuPropertyField.AUTH_TYPE;
import static cn.bywin.business.hetu.HetuPropertyField.EXTRA_CREDENTIALS;
import static cn.bywin.business.hetu.HetuPropertyField.KRB5_CONFIG_PATH;
import static cn.bywin.business.hetu.HetuPropertyField.KEYTAB_PATH;
import static cn.bywin.business.hetu.HetuPropertyField.PRINCIPAL;
import static cn.bywin.business.hetu.HetuPropertyField.REMOTE_SERVICE_NAME;
import static cn.bywin.business.hetu.HetuPropertyField.KEYSTORE_PASSWORD;
import static cn.bywin.business.hetu.HetuPropertyField.KEYSTORE_PATH;
import static cn.bywin.business.hetu.HetuPropertyField.PASSWORD;
import static cn.bywin.business.hetu.HetuPropertyField.TOKEN;
import static cn.bywin.business.hetu.HetuPropertyField.USERNAME;
import static cn.bywin.business.hetu.HetuPropertyField.USE_SSL;

/**
 * 主节点连接配置
 * @author firepation
 */
@Configuration
@Data
public class DataHubJdbcOperateConfigurate {

    public static final String MASTER_CODE = "dc0";

    public static final String SYNC_FLAG = "syncFlag";

    @Value("${olk.driver}")
    private String driver;

    @Value("${olk.url}")
    private String url;

    @Value("${olk.connectionUrl}")
    private String connectionUrl;

    @Value("${olk.authType}")
    private String authType;

    @Value("${olk.user}")
    private String user;

    @Value("${olk.password:}")
    private String password;

    @Value("${olk.keyStorePassword:}")
    private String keyStorePassword;

    @Value("${olk.keyStorePath:}")
    private String keyStorePath;

    @Value("${olk.kerberosConfigPath:}")
    private String kerberosConfigPath;

    @Value("${olk.kerberosPrincipal:}")
    private String kerberosPrincipal;

    @Value("${olk.kerberosKeytabPath:}")
    private String kerberosKeytabPath;

    @Value("${olk.kerberosRemoteServiceName:}")
    private String kerberosRemoteServiceName;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private OlkDcServerConfigMapService dcServerConfigMapService;

    @Autowired
    private HetuJdbcOperateComponent hetuJdbcOperateComponent;

    @Bean(name = "dataHubProperties")
    public Properties createMasterProperties() {
        Properties properties = new Properties();
        properties.setProperty(USERNAME, user);
        if (!AuthType.NONE.equals(authType)) {
            properties.setProperty(USE_SSL, "true");
            properties.setProperty(KEYSTORE_PATH, keyStorePath);
            properties.setProperty(KEYSTORE_PASSWORD, keyStorePassword);
        }

        if (AuthType.LDAP.equals(authType)) {
            properties.setProperty(PASSWORD, password);
        } else if (AuthType.KERBEROS.equals(authType)) {
            properties.setProperty(PRINCIPAL, kerberosPrincipal);
            properties.setProperty(REMOTE_SERVICE_NAME, kerberosRemoteServiceName);

            properties.setProperty(KRB5_CONFIG_PATH, kerberosConfigPath);
            properties.setProperty(KEYTAB_PATH, kerberosKeytabPath);
        }
        return properties;
    }

//    @Bean(name = "dataHubScheduleProperties")
//    public Properties createMasterScheduleProperties() throws Exception {
//        TOlkDcServerDo masterDcServerInfo = createMasterDcServerInfo();
//        // 系统启动时同步文件到调度平台，失败之后后续使用时会进行重试
//        boolean syncFlag;
//        try {
//            syncFlag = hetuJdbcOperateComponent.syncConfigFile(masterDcServerInfo);
//        } catch (Exception ignore) {
//            syncFlag = false;
//        }
//        Properties properties = hetuJdbcOperateComponent.genHetuPropertiesForSchedule(masterDcServerInfo);
//        properties.setProperty(SYNC_FLAG, String.valueOf(syncFlag));
//        return properties;
//    }

    @Bean
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public HetuJdbcOperate createMasterJdbcOperate(@Qualifier("dataHubProperties") Properties masterProperties) throws SQLException {
        Properties newProperties = new Properties();
        newProperties.putAll(masterProperties);

        // 设置执行用户信息
        String token = LoginUtil.getTokenId(request);
        newProperties.setProperty(EXTRA_CREDENTIALS, TOKEN.concat(":").concat(token).concat(";")
                                .concat(AUTH_TYPE).concat(":").concat(AuthType.TOKEN));
        return HetuJdbcOperateLogProxy.getInstance(url, newProperties);
    }

    @Bean(name = "dataHubHetuInfo")
    public HetuInfo createMasterHetuInfo(@Qualifier("dataHubProperties") Properties masterProperties) {
        HetuInfo masterHetuInfo = new HetuInfo();
        masterHetuInfo.setAuthType(authType);
        masterHetuInfo.setConnectionUrl(connectionUrl);
        masterHetuInfo.setHetuProperties(masterProperties);
        return masterHetuInfo;
    }

    @Bean
    public TOlkDcServerDo createMasterDcServerInfo() throws IOException {
        TOlkDcServerDo dcServerInfo = new TOlkDcServerDo();
        dcServerInfo.setDcCode(MASTER_CODE);
        dcServerInfo.setJdbcUrl(url);
        dcServerInfo.setConnectionUrl(connectionUrl);
        dcServerInfo.setAuthType(authType);
        dcServerInfo.setConnectionUser(user);
        dcServerInfo.setConnectionPwd(password);
        dcServerInfo.setKeystorePassword(keyStorePassword);
        dcServerInfo.setPrincipal(kerberosPrincipal);
        dcServerInfo.setRemoteServiceName(kerberosRemoteServiceName);

        if (StringUtils.isNotBlank(keyStorePath)) {
            dcServerInfo.setKeystoreFile(IOUtils.resourceToByteArray(keyStorePath));
        }

        if (StringUtils.isNotBlank(kerberosConfigPath)) {
            dcServerInfo.setKrb5ConfigFile(IOUtils.resourceToByteArray(kerberosConfigPath));
        }

        if (StringUtils.isNotBlank(kerberosKeytabPath)) {
            dcServerInfo.setKeytabFile(IOUtils.resourceToByteArray(kerberosKeytabPath));
        }
        return dcServerInfo;
    }
}
