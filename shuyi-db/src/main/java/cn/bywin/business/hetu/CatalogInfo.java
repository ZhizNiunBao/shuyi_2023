package cn.bywin.business.hetu;

import com.google.common.collect.ImmutableMap;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 动态添加目录数据格式
 * @author firepation
 */
@Data
public class CatalogInfo {

    Map<String, String> properties;
    private String catalogName;
    private String connectorName;
    private String securityKey;

    private transient List<String> configFilePaths;

    public CatalogInfo(String catalogName,
                       String connectorName,
                       String securityKey,
                       Map<String, String> properties,
                       List<String> configFilePaths) {
        this.catalogName = catalogName;
        this.connectorName = connectorName;
        this.securityKey = securityKey;
        this.properties = properties == null ? ImmutableMap.of() : properties;
        this.configFilePaths = configFilePaths;
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public String toString() {
        return "CatalogInfo{" +
                "properties=" + properties +
                ", catalogName='" + catalogName + '\'' +
                ", connectorName='" + connectorName + '\'' +
                ", securityKey='" + securityKey + '\'' +
                '}';
    }
}
