package cn.bywin.business.common.enums;

/**
 * Created by Administrator on 2018/6/29 0029.
 */
public enum TemplateBootResultCodeEnum {
    SUCCESS("0", "操作成功"),
    ERROR("500", "操作失败"),
    SYSTEM_ERROR("CR1001", "系统错误"),
    NOLOGIN("904", "用户未登录"),;
    private String code;
    private String message;

    private TemplateBootResultCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
