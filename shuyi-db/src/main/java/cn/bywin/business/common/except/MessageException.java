package cn.bywin.business.common.except;

public class MessageException extends Exception{

    public MessageException(String msg){
        super(msg);
    }
    public MessageException(String msg,Throwable throwable){
        super(msg,throwable);
    }
}
