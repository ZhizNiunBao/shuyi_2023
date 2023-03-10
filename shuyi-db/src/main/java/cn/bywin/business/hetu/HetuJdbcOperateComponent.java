package cn.bywin.business.hetu;

import cn.bywin.business.bean.olk.TOlkDcServerConfigMapDo;
import cn.bywin.business.bean.olk.TOlkDcServerDo;
import cn.bywin.business.bean.view.security.ScheduleResult;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.HttpOperaterUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.service.olk.OlkDcServerConfigMapService;
import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static cn.bywin.business.hetu.HetuPropertyField.AUTH_TYPE;
import static cn.bywin.business.hetu.HetuPropertyField.DC_CODE;
import static cn.bywin.business.hetu.HetuPropertyField.EXTRA_CREDENTIALS;
import static cn.bywin.business.hetu.HetuPropertyField.KEYSTORE_PASSWORD;
import static cn.bywin.business.hetu.HetuPropertyField.KEYSTORE_PATH;
import static cn.bywin.business.hetu.HetuPropertyField.KEYTAB_PATH;
import static cn.bywin.business.hetu.HetuPropertyField.KRB5_CONFIG_PATH;
import static cn.bywin.business.hetu.HetuPropertyField.PASSWORD;
import static cn.bywin.business.hetu.HetuPropertyField.PRINCIPAL;
import static cn.bywin.business.hetu.HetuPropertyField.REMOTE_SERVICE_NAME;
import static cn.bywin.business.hetu.HetuPropertyField.TOKEN;
import static cn.bywin.business.hetu.HetuPropertyField.USERNAME;
import static cn.bywin.business.hetu.HetuPropertyField.USE_SSL;

/**
 * hetu ?????????????????????????????????????????????
 * @author firepation
 */
@Slf4j
@Component
public class HetuJdbcOperateComponent {

    public static final String CONFIG_PATH = "config/";

    public static final String CONFIG_TMP_PATH = "config/tmp/";

    private static final String PASSWORD_FIELD = "password";

    private static final String ENCRYPTED_PROPERTIES_FIELD = "encrypted-properties";

    private final ConcurrentHashMap<String, Lock> serverLockMap = new ConcurrentHashMap<>();

    @Autowired
    private OlkDcServerConfigMapService dcServerConfigMapService;

    @Autowired
    private HttpServletRequest request;

    private Type updateResultType = new TypeToken<ScheduleResult<Integer>>() {

    }.getType();

    private Type uploadResultType = new TypeToken<ScheduleResult<Integer>>() {

    }.getType();

    /**
     * ??????????????????????????????????????????
     * @param dcServerConfig ????????????
     */
    public HetuInfo genHetuInfo(TOlkDcServerDo dcServerConfig) throws Exception {
        Properties properties = genHetuProperties(dcServerConfig);
        setAuthInfo(properties, LoginUtil.getTokenId(request), dcServerConfig);
        HetuInfo hetuInfo = new HetuInfo();
        hetuInfo.setHetuProperties(properties);
        hetuInfo.setConnectionUrl(dcServerConfig.getConnectionUrl());
        hetuInfo.setAuthType(dcServerConfig.getAuthType());
        return hetuInfo;
    }

    /**
     * ???????????????(???hetu??????)??????????????????
     * @param isEncryptPassword ????????????????????????
     * @param catalogName ????????????
     * @param connectorName ???????????????
     * @param properties ???????????????
     * @return ??????????????????
     */
    public CatalogInfo genCatalogInfo(boolean isEncryptPassword,
                                      String catalogName,
                                      String connectorName,
                                      Map<String, String> properties) throws Exception {
        List<String> configFilePaths = new ArrayList<>();
        List<String> encryptProperties = new ArrayList<>();
        String securityKey = "";
        if (isEncryptPassword) {
            RsaKeyPair rsaKeyPair = RsaUtil.generateKeyPair();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                String key = entry.getKey();
                if (key.contains(PASSWORD_FIELD)) {
                    String encryptValue = RsaUtil.encryptByPublicKey(rsaKeyPair.getPublicKey(), entry.getKey());
                    properties.put(key, encryptValue);
                    encryptProperties.add(key);
                }
            }
            securityKey = rsaKeyPair.getPrivateKey();
            properties.put(ENCRYPTED_PROPERTIES_FIELD, StringUtils.join(encryptProperties, ","));
        }
        return new CatalogInfo(catalogName, connectorName, securityKey, properties, configFilePaths);
    }

    /**
     * ????????????????????????????????????????????????
     * @param dcServerConfig hetu ????????????
     * @return ????????????????????????
     */
    public CatalogInfo genAgentCatalogInfo(boolean isEncryptPassword,
                                           TOlkDcServerDo dcServerConfig) throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put("connection-url", dcServerConfig.getConnectionUrl());
        properties.put("connection-user", dcServerConfig.getConnectionUser());

        RsaKeyPair rsaKeyPair = RsaUtil.generateKeyPair();
        dcServerConfig.setDcPriv(rsaKeyPair.getPrivateKey());
        dcServerConfig.setDcPub(rsaKeyPair.getPublicKey());
        // ?????????????????????????????????
        StringBuilder extraCredentials = new StringBuilder();
        String encryptDcCode = RsaUtil.encryptByPublicKey(rsaKeyPair.getPublicKey(), dcServerConfig.getDcCode());
        extraCredentials.append("authType:").append(AuthType.KEY).append(";")
                .append("key:").append(encryptDcCode).append(";")
                .append("dcCode:").append(dcServerConfig.getDcCode());
        properties.put("dc.extra.credentials", extraCredentials.toString());


        // ????????????????????????????????????????????????????????????????????????????????? encrypted-properties
        String securityKey = "";
        if (isEncryptPassword){
            List<String> encryptProperties = new ArrayList<>();

            if (StringUtils.isNotEmpty(dcServerConfig.getKeystorePassword())) {
                String keystorePassword = RsaUtil.encryptByPublicKey(rsaKeyPair.getPublicKey(),
                        dcServerConfig.getKeystorePassword());
                dcServerConfig.setKeystorePassword(keystorePassword);
                encryptProperties.add("dc.ssl.keystore.password");
            }

            if (StringUtils.isNotEmpty(dcServerConfig.getConnectionPwd())) {
                String connectionPwd = RsaUtil.encryptByPublicKey(rsaKeyPair.getPublicKey(),
                        dcServerConfig.getConnectionPwd());
                dcServerConfig.setConnectionPwd(connectionPwd);
                encryptProperties.add("connection-password");
            }

            securityKey = rsaKeyPair.getPrivateKey();
            properties .put(ENCRYPTED_PROPERTIES_FIELD, StringUtils.join(encryptProperties, ","));
        }

        String authType = dcServerConfig.getAuthType();

        // ???????????????????????????????????????
        List<String> configFilePaths = new ArrayList<>();
        String configDirectory = createTmpConfigPath(dcServerConfig.getDcCode());
        if (!AuthType.NONE.equals(authType)) {
            properties.put("dc.ssl.keystore.password", dcServerConfig.getKeystorePassword());
            properties.put("dc.ssl", "true");

            String keystoreFilePath = configDirectory.concat(dcServerConfig.getKeystoreFileName());
            Files.write(dcServerConfig.getKeystoreFile(), new File(keystoreFilePath));
            configFilePaths.add(keystoreFilePath);
            properties.put("dc.ssl.keystore.path", dcServerConfig.getKeystoreFileName());
        }

        if (AuthType.LDAP.equals(authType)) {
            properties .put("connection-password", dcServerConfig.getConnectionPwd());
        } else if (AuthType.KERBEROS.equals(authType)) {
            properties.put("dc.kerberos.principal", dcServerConfig.getPrincipal());
            properties .put("dc.kerberos.remote.service.name", dcServerConfig.getRemoteServiceName());

            String krb5ConfigPath = configDirectory.concat(dcServerConfig.getKrb5ConfigFileName());
            Files.write(dcServerConfig.getKrb5ConfigFile(), new File(krb5ConfigPath));
            configFilePaths.add(krb5ConfigPath);
            properties.put("dc.kerberos.config.path", dcServerConfig.getKrb5ConfigFileName());

            String keytabFilePath = configDirectory.concat(dcServerConfig.getKeytabFileName());
            Files.write(dcServerConfig.getKeytabFile(), new File(keytabFilePath));
            configFilePaths.add(keytabFilePath);
            properties.put("dc.kerberos.keytab.path", dcServerConfig.getKeytabFileName());
        }
        return new CatalogInfo(dcServerConfig.getDcCode(),
                "dc", securityKey, properties, configFilePaths);
    }

    /**
     * ?????????????????????????????????????????????
     * @param dcServerConfig ????????????
     * @param userInfo ????????????
     * @return hetu ?????????
     */
    public HetuJdbcOperate genHetuJdbcOperate(TOlkDcServerDo dcServerConfig, UserDo userInfo) throws Exception {
        if (userInfo == null) {
            throw new IllegalArgumentException("?????????????????????");
        }
        Properties properties = genHetuProperties(dcServerConfig);
        setAuthInfo(properties, userInfo.getTokenId(), dcServerConfig);
        HetuJdbcOperate hetuJdbcOperate = new HetuJdbcOperate();
        hetuJdbcOperate.init( dcServerConfig.getJdbcUrl(), properties );
        return hetuJdbcOperate;
    }

    /**
     * ??????????????????????????????
     * @param dcServerConfig ????????????
     * @return hetu ?????????
     */
    public HetuJdbcOperate genHetuJdbcOperate(TOlkDcServerDo dcServerConfig) throws Exception {
        Properties properties = genHetuProperties(dcServerConfig);
        setAuthInfo(properties, LoginUtil.getTokenId(request), dcServerConfig);
        return HetuJdbcOperateLogProxy.getInstance(dcServerConfig.getJdbcUrl(), properties);
    }

    /**
     * ?????? hetu ????????????
     * @param dcServerConfig ??????????????????
     * @return hetu ????????????
     */
    public Properties genHetuProperties(TOlkDcServerDo dcServerConfig) throws Exception {
        Properties properties = new Properties();

        properties.setProperty(USERNAME, dcServerConfig.getConnectionUser());

        // ?????????????????????????????????????????????????????????????????????????????????
        // ??????????????????????????????
        String dcCode = dcServerConfig.getDcCode();
        Lock serverLock = serverLockMap.get(dcCode);
        synchronized (serverLockMap) {
            if (serverLock == null) {
                serverLock = new ReentrantLock();
                serverLockMap.put(dcCode, serverLock);
            }
        }
        serverLock.lock();
        try {
            String authType = dcServerConfig.getAuthType();
            String configDirectory = CONFIG_PATH.concat(dcServerConfig.getDcCode()).concat("/");

            String privateKey = dcServerConfig.getDcPriv();
            boolean isEncryptPassword = EncryptFlag.isEncrypt(dcServerConfig.getEncryptFlag());

            // ????????????????????????, ???????????????????????????
            if (!AuthType.NONE.equals(authType)) {
                File configPath = new File(configDirectory);
                if (!configPath.exists()) {
                    configPath.mkdirs();
                }

                properties.setProperty(USE_SSL, "true");

                String keystorePassword = dcServerConfig.getKeystorePassword();
                if (isEncryptPassword) {
                    keystorePassword = RsaUtil.decryptByPrivateKey(privateKey, keystorePassword);
                }
                properties.setProperty(KEYSTORE_PASSWORD, keystorePassword);

                String keystoreFileName = configDirectory.concat(dcServerConfig.getKeystoreFileName());
                File keystoreFile = new File(keystoreFileName);
                if (!keystoreFile.exists()) {
                    Files.write(dcServerConfig.getKeystoreFile(), keystoreFile);
                }
                properties.setProperty(KEYSTORE_PATH, keystoreFileName);
            }

            // ?????????????????????????????????????????????
            if (AuthType.LDAP.equals(authType)) {
                String connectionPassword = dcServerConfig.getConnectionPwd();
                if (isEncryptPassword) {
                    connectionPassword = RsaUtil.decryptByPrivateKey(privateKey, connectionPassword);
                }
                properties.setProperty(PASSWORD, connectionPassword);
            } else if (AuthType.KERBEROS.equals(authType)) {
                properties.setProperty(PRINCIPAL, dcServerConfig.getPrincipal());
                properties.setProperty(REMOTE_SERVICE_NAME, dcServerConfig.getRemoteServiceName());

                String krb5ConfigPath = configDirectory.concat(dcServerConfig.getKrb5ConfigFileName());
                File krb5ConfigFile = new File(krb5ConfigPath);
                if (!krb5ConfigFile.exists()) {
                    Files.write(dcServerConfig.getKrb5ConfigFile(), krb5ConfigFile);
                }
                properties.setProperty(KRB5_CONFIG_PATH, krb5ConfigPath);

                String keytabFileName = configDirectory.concat(dcServerConfig.getKeytabFileName());
                File keytabFile = new File(keytabFileName);
                if (!keytabFile.exists()) {
                    Files.write(dcServerConfig.getKeytabFile(), keytabFile);
                }
                properties.setProperty(KEYTAB_PATH, keytabFileName);
            }
        } finally {
            serverLock.unlock();
        }
        return properties;
    }

    private void setAuthInfo(Properties properties, String authInfo, TOlkDcServerDo dcServerInfo) {
        properties.setProperty(EXTRA_CREDENTIALS, TOKEN.concat(":").concat(authInfo).concat(";")
                                                .concat(DC_CODE).concat(":").concat(dcServerInfo.getDcCode()).concat(";")
                                                .concat(AUTH_TYPE).concat(":").concat(AuthType.TOKEN));
    }

    /**
     * ?????????????????????????????????
     * @param dcServerConfig  ????????????
     * @return                ????????????
     */
    public Boolean syncConfigFile(TOlkDcServerDo dcServerConfig) throws IOException {
        String dcCode = dcServerConfig.getDcCode();
        log.info("???????????? {} ????????????", dcServerConfig.getDcCode());
        HttpOperaterUtil httpOperate = new HttpOperaterUtil();

        String uploadUrl = null;
        String updateUrl = null;

        Map<String, File> fileMap = getFileMap(dcServerConfig);
        log.info("{} ?????? {} ???????????????", dcCode, fileMap.size());
        try {
            for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                String fileCode = String.format("%s.%s", dcCode, entry.getKey());
                TOlkDcServerConfigMapDo dcServerConfigMapInfo = dcServerConfigMapService.findById(fileCode);
                if (dcServerConfigMapInfo == null) {
                    String resultString = httpOperate.sendPostFile(uploadUrl, new HashMap<>(), "file", entry.getValue().getPath());
                    ScheduleResult<Integer> resultData = JsonUtil.deserialize(resultString, uploadResultType);

                    dcServerConfigMapInfo = new TOlkDcServerConfigMapDo();
                    dcServerConfigMapInfo.setId(fileCode);
                    if (resultData.isSuccess()) {
                        Integer resourceId = resultData.getData();

                        dcServerConfigMapInfo.setSuccess(true);
                        dcServerConfigMapInfo.setResourceId(resourceId);
                        dcServerConfigMapInfo.setFileType(entry.getKey());
                        dcServerConfigMapInfo.setDcServerCode(dcServerConfig.getDcCode());
                    }
                    dcServerConfigMapService.insertBean(dcServerConfigMapInfo);
                } else {
                    Map<String, Object> params = new HashMap<>();
                    params.put("id", dcServerConfigMapInfo.getResourceId());
                    String resultString = httpOperate.sendPostFile(updateUrl, params, "file", entry.getValue().getPath());
                    ScheduleResult<Integer> resultData = JsonUtil.deserialize(resultString, updateResultType);
                    dcServerConfigMapInfo.setSuccess(resultData.isSuccess());
                    dcServerConfigMapService.updateBean(dcServerConfigMapInfo);
                }
            }
        } finally {
            File dcConfigTmpPath = new File(HetuJdbcOperateComponent.CONFIG_TMP_PATH.concat(dcServerConfig.getDcCode()));
            try {
                FileUtils.deleteDirectory(dcConfigTmpPath);
            } catch (IOException ignore) {

            }
        }
        return true;
    }

    /**
     * ???????????????????????????????????????????????????
     * @param dcServerConfig   ????????????
     * @return                 ?????????????????????????????????
     */
    private Map<String, File> getFileMap(TOlkDcServerDo dcServerConfig) throws IOException {
        String tmpConfigPath = createTmpConfigPath(dcServerConfig.getDcCode());
        Map<String, File> fileMap = new HashMap<>();

        if (!AuthType.NONE.equals(dcServerConfig.getAuthType())) {
            String keystoreFilePath = tmpConfigPath.concat(dcServerConfig.getKeystoreFileName());
            File keystoreFile = new File(keystoreFilePath);
            Files.write(dcServerConfig.getKeystoreFile(), keystoreFile);
            fileMap.put(HetuPropertyField.KEYSTORE_PATH, keystoreFile);
        }

        if (AuthType.KERBEROS.equals(dcServerConfig.getAuthType())) {
            String keytabFilePath = tmpConfigPath.concat(dcServerConfig.getKeytabFileName());
            File keytabFile = new File(keytabFilePath);
            Files.write(dcServerConfig.getKeystoreFile(), keytabFile);
            fileMap.put(HetuPropertyField.KEYTAB_PATH, keytabFile);

            String krb5ConfigFilePath = tmpConfigPath.concat(dcServerConfig.getKrb5ConfigFileName());
            File krb5ConfigFile = new File(krb5ConfigFilePath);
            Files.write(dcServerConfig.getKeystoreFile(), krb5ConfigFile);
            fileMap.put(HetuPropertyField.KRB5_CONFIG_PATH, krb5ConfigFile);
        }
        return fileMap;
    }

    /**
     * ??????????????????????????????????????????
     * @param dcCode   ????????????
     * @return         ????????????
     */
    private String createTmpConfigPath(String dcCode) {
        String configDirectory = CONFIG_TMP_PATH.concat(dcCode).concat("/");
        File configPath = new File(configDirectory);
        if (!configPath.exists()) {
            configPath.mkdirs();
        }
        return configDirectory;
    }
}
