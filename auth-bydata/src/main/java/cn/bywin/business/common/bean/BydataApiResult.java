package cn.bywin.business.common.bean;

import lombok.Data;

/**
 * 中台接口返回格式
 * @author zzm
 */
@Data
public class BydataApiResult<T> {

    private String code;

    private T data;

    private String message;

    private boolean success;

}
