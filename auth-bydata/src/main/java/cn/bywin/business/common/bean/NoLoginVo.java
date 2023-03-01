package cn.bywin.business.common.bean;

import lombok.Data;

/**
 * 登录认证失效返回格式
 * @author zzm
 */
@Data
public class NoLoginVo {

    private String statusCode = "1011";

    private String statusInfo = "未登录";

    private String authMode;;

    private String url;

    private String logoutUrl;

    private Object data;

    public NoLoginVo(String authMode, String url , String logoutUrl) {
        this.authMode = authMode;
        this.url = url;
        this.logoutUrl = logoutUrl;
    }

}
