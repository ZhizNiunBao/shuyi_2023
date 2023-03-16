package cn.bywin.common.resp;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class ListResp<T> extends BaseRespone implements Serializable {
    private List<T> data;
}
