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

    @Value("${olk.encryptPassword}")
    private Integer encryptPassword;

    @Autowired
    private HetuJdbcOperateComponent hetuJdbcOperateComponent;

    @Bean
    public TOlkDcServerDo createMasterDcServerInfo() {
        TOlkDcServerDo dcServerInfo = new TOlkDcServerDo();
        dcServerInfo.setJdbcUrl(url);
        dcServerInfo.setEncryptFlag(encryptPassword);
        dcServerInfo.setConnectionUrl(connectionUrl);
        dcServerInfo.setConnectionUser(user);
        dcServerInfo.setConnectionPwd(password);
        return dcServerInfo;
    }
}
