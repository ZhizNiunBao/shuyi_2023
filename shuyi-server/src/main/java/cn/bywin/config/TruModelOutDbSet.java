package cn.bywin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "trumodeloutdbset")
@Data
public class TruModelOutDbSet {

    //@Value( "${truModelOutDbSet.dstype}" )
    private String dstype;
    //@Value( "${truModelOutDbSet.dbName}" )
    private String dbName;
    //@Value( "${truModelOutDbSet.driver}" )
    private String driver;
    //@Value( "${truModelOutDbSet.jdbcurl}" )
    private String jdbcUrl;
    //@Value( "${truModelOutDbSet.user}" )
    private String user;
    //@Value( "${truModelOutDbSet.loadurl:}" )
    private String loadUrl;
    //@Value( "${truModelOutDbSet.password}" )
    private String password;
}
