package cn.bywin.business.common.result;

import cn.bywin.business.common.enums.ErrorCodeConstants;
import java.io.Serializable;

public class CommonResult implements Serializable {

    private static final long serialVersionUID = -6298703072199610379L;
    protected static final String SUCCESS_CODE;
    protected static final String SUCCESS_MESSAGE;
    private String code;
    private String msg;

    public CommonResult() {
        this.code = SUCCESS_CODE;
        this.msg = SUCCESS_MESSAGE;
    }

    public CommonResult(String code, String message) {
        this.code = code;
        this.msg = message;
    }

    public boolean isSuccess() {
        return null != this.code && SUCCESS_CODE.equals(this.code);
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String message) {
        this.msg = message;
    }

    static {
        SUCCESS_CODE = ErrorCodeConstants.SUCCESS.getCode();
        SUCCESS_MESSAGE = ErrorCodeConstants.SUCCESS.getMessage();
    }
}
