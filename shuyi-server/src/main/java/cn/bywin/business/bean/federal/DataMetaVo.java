package cn.bywin.business.bean.federal;

import java.util.Map;
import lombok.Data;

@Data
public class DataMetaVo {

    private String id;
    private String sql;
    private Map<String, Object> meta;
}
