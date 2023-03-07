package cn.bywin.business.bean.view;

import lombok.Data;

import java.io.Serializable;

@Data
public class TokenVo implements Serializable {
    private String uuid;
    private String node;
    private String userId;
    private long ts;

}
