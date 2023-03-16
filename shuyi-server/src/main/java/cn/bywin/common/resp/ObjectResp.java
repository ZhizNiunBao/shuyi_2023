package cn.bywin.common.resp;

import java.io.Serializable;
import lombok.Data;

@Data
public class ObjectResp<T> extends BaseRespone implements Serializable {
    private T data;
}
