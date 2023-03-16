package cn.bywin.business.bean.federal;

import java.io.Serializable;
import lombok.Data;

@Data
public class PmsResult implements Serializable {
    private boolean success;
    private String msg;
}
