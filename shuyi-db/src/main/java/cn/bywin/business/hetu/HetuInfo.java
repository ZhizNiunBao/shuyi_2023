package cn.bywin.business.hetu;

import lombok.Data;

import java.util.Properties;

/**
 * hetu 动态目录连接和配置信息
 * @author firepation
 */
@Data
public class HetuInfo {

    /**
     * 连接地址
     */
    private String connectionUrl;

    /**
     * hetu 配置信息
     */
    private Properties hetuProperties;
}
