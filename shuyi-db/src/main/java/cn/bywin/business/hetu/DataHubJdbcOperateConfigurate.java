package cn.bywin.business.hetu;

import static cn.bywin.business.hetu.HetuPropertyField.USERNAME;

import cn.bywin.business.bean.olk.TOlkDcServerDo;
import java.sql.SQLException;
import java.util.Properties;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * 主节点连接配置
 * @author firepation
 */
@Configuration
@Data
public class DataHubJdbcOperateConfigurate {

    @Value("${olk.driver}")
    private String driver;

    @Value("${olk.url}")
    private String url;

    @Value("${olk.connectionUrl}")
    private String connectionUrl;

    @Value("${olk.user}")
    private String user;

    @Value("${olk.password:}")
    private String password;

    @Autowired
    private HetuJdbcOperateComponent hetuJdbcOperateComponent;

    @Bean(name = "dataHubProperties")
    public Properties createMasterProperties() {
        Properties properties = new Properties();
        properties.setProperty(USERNAME, user);
        return properties;
    }

    @Bean
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public HetuJdbcOperate createMasterJdbcOperate(@Qualifier("dataHubProperties") Properties masterProperties) throws SQLException {
        Properties newProperties = new Properties();
        newProperties.putAll(masterProperties);

        HetuJdbcOperate hetuJdbcOperate = new HetuJdbcOperate();
        hetuJdbcOperate.init(url, newProperties);
        return hetuJdbcOperate;
    }

    @Bean(name = "dataHubHetuInfo")
    public HetuInfo createMasterHetuInfo(@Qualifier("dataHubProperties") Properties masterProperties) {
        HetuInfo masterHetuInfo = new HetuInfo();
        masterHetuInfo.setConnectionUrl(connectionUrl);
        masterHetuInfo.setHetuProperties(masterProperties);
        return masterHetuInfo;
    }

    @Bean
    public TOlkDcServerDo createMasterDcServerInfo() {
        TOlkDcServerDo dcServerInfo = new TOlkDcServerDo();
        dcServerInfo.setJdbcUrl(url);
        dcServerInfo.setConnectionUrl(connectionUrl);
        dcServerInfo.setConnectionUser(user);
        dcServerInfo.setConnectionPwd(password);
        return dcServerInfo;
    }
}
