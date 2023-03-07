package cn.bywin.business.hetu;

import cn.bywin.business.hetu.AuthType;
import lombok.Data;

import java.util.Properties;

/**
 * hetu 动态目录连接和配置信息
 * @author firepation
 */
@Data
public class HetuInfo {

    /**
     * 认证类型
     */
    private String authType;

    /**
     * 连接地址
     */
    private String connectionUrl;

    /**
     * hetu 配置信息
     */
    private Properties hetuProperties;

    /**
     * kerberos 主体格式
     */
    private String servicePrincipalPattern = "${SERVICE}@${HOST}";

    public boolean useSsl() {
        if (null == authType) {
            return false;
        }
        return !AuthType.NONE.equals(authType);
    }
}
