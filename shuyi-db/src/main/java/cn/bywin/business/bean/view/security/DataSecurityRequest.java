package cn.bywin.business.bean.view.security;

import lombok.Data;

import java.util.Set;

/**
 * @author zzm
 */
@Data
public class DataSecurityRequest {

    /**
     * 资源全名称: datacenter.database.table
     */
    public String resourceName;

    /**
     * 用户认证信息
     */
    private String token;

    /**
     * 认证类型
     */
    private String authType;

    /**
     * 认证 key
     */
    private String key;

    /**
     * 列集合
     */
    private Set<String> columns;
}
