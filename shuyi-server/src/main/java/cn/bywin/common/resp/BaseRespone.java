package cn.bywin.common.resp;

import java.io.Serializable;
import lombok.Data;

/**
 * @author me
 * @date 2022-04-21
 */
@Data
public class BaseRespone implements Serializable{

    private String statusInfo;
    private String statusCode;

    private Integer total;
    private Integer code;
    private boolean success;
    private String msg;
    private String a_spend_time_a;
    private Integer pageSize;
    private Integer page;
    private boolean isPage;

}
