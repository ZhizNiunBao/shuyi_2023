package cn.bywin.business.hetu;

import lombok.ToString;

/**
 * 动态目录操作结果
 * @author firepation
 */
@ToString
public class DynamicCatalogResult {

    private static final int SUCCESS_CODE = 200;

    private boolean success;

    private Integer responseCode;

    private String message;

    public DynamicCatalogResult(Integer responseCode, String message) {
        this.success = (responseCode >= 200 && responseCode < 300);
        this.responseCode = responseCode;
        this.message = message;
    }

    public boolean isSuccessful() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public static DynamicCatalogResult successResult() {
        return new DynamicCatalogResult(SUCCESS_CODE, "");
    }
}
