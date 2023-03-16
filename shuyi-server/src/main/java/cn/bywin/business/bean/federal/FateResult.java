package cn.bywin.business.bean.federal;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;

@Data
public class FateResult implements Serializable {
    private String retcode;
    private String retmsg;

    private Map<String,Object> data;
}
