package cn.bywin.business.common.except;

import cn.bywin.business.common.enums.ErrorCodeConstants;

/**
 * 错误码对象
 */
public class ErrorCode {

    /**
     * 返回码
     * 所有返回码定义在 {@link ErrorCodeConstants}
     */
    private final String code;

    /**
     * 错误信息
     */
    private final String message;

    public ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
