package cn.bywin.business.bean.view.security;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zzm
 */
@Data
public class DataSecurityResult {

    private String message;

    private boolean hasPermission;

    private String authType;

    /**
     * 字段脱敏列表
     */
    private List<Map<String, String>> columnMasks;

    /**
     * 行过滤列表
     */
    private List<Map<String, String>> rowFilters;

    public static DataSecurityResult noPermissionResult(String message) {
        return new DataSecurityResult(false, message);
    }

    public DataSecurityResult(boolean hasPermission, String message) {
        this.hasPermission = hasPermission;
        this.message = message;
    }
}
