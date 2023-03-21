package cn.bywin.business.hetu;

import static cn.bywin.business.hetu.HetuPropertyField.USERNAME;

import cn.bywin.business.bean.olk.TOlkDcServerDo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * hetu 连接组件，用于生成配置或者连接
 * @author firepation
 */
@Slf4j
@Component
public class HetuJdbcOperateComponent {

    @Autowired
    private TOlkDcServerDo dcServerConfig;

    private static final String PASSWORD_FIELD = "password";

    private static final String ENCRYPTED_PROPERTIES_FIELD = "encrypted-properties";

    /**
     * 获取动态目录配置
     */
    public HetuInfo genHetuInfo() {
        Properties properties = genHetuProperties(dcServerConfig);
        HetuInfo hetuInfo = new HetuInfo();
        hetuInfo.setHetuProperties(properties);
        hetuInfo.setConnectionUrl(dcServerConfig.getConnectionUrl());
        return hetuInfo;
    }

    /**
     * 生成数据源(除hetu类型)动态目录配置
     * @param isEncryptPassword 是否需要密码加密
     * @param catalogName 目录名称
     * @param connectorName 数据源类型
     * @param properties 数据源配置
     * @return 动态目录配置
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
     * 根据节点配置、用户信息获取连接
     * @return hetu 操作类
     */
    public HetuJdbcOperate genHetuJdbcOperate() {
        Properties properties = genHetuProperties(dcServerConfig);
        HetuJdbcOperate hetuJdbcOperate = new HetuJdbcOperate();
        hetuJdbcOperate.init( dcServerConfig.getJdbcUrl(), properties );
        return hetuJdbcOperate;
    }

    /**
     * 生成 hetu 连接配置
     * @param dcServerConfig 节点配置信息
     * @return hetu 连接配置
     */
    public Properties genHetuProperties(TOlkDcServerDo dcServerConfig) {
        Properties properties = new Properties();
        properties.setProperty(USERNAME, dcServerConfig.getConnectionUser());
        return properties;
    }
}
