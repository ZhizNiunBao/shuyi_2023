package cn.bywin.business.bean.view.federal;

import cn.bywin.business.bean.federal.FProjectDataDo;
import lombok.Data;

@Data
public class FProjectDataVo extends FProjectDataDo {

    private  String userId;
    private  String pdId;
    private  Integer type;
    private  String content;
}
