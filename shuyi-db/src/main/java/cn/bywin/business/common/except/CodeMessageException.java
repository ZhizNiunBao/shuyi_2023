package cn.bywin.business.common.except;

import lombok.Data;

//@Data
public class CodeMessageException extends Exception{

    String msg;
    String code;
    public CodeMessageException(String code, String msg){
        super(String.format( "错误代码:%s,错误信息:%s",code,msg ));
        this.code =code;
        this.msg = msg;
    }
    public CodeMessageException(String code, String msg, Throwable throwable){
        super(String.format( "错误代码:%s,错误信息:%s",code,msg ),throwable);
        this.code =code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }
}
