package cn.bywin.business.bean.view.federal;

import cn.bywin.business.bean.federal.FModelJobDo;
import lombok.Data;

@Data
public class FModelJobVo  extends FModelJobDo {

    private String modelName;

    private String projectId;

    private String aucSort;

    private String f1Sort;

    private String createTimeSort;

    private String userId;

    private Object algoConfig;
}
