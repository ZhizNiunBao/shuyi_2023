package cn.bywin.business.bean.view.federal;


import lombok.Data;

import java.util.List;

@Data
public class FDatasourceVo {

    private String id;
    private String name;
    private String desc;
    private String type;
    List<FDatasourceVo> children;
}
