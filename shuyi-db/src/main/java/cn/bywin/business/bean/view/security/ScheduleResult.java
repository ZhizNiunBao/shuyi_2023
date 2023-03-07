package cn.bywin.business.bean.view.security;

import lombok.Data;

/**
 * @author zzm
 */
@Data
public class ScheduleResult<T> {

    private Integer code;

    private String msg;

    private T data;

    public boolean isSuccess() {
        return code == 200;
    }
}
