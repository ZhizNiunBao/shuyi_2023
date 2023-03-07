package cn.bywin.business.bean.view.federal;

import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.federal.FProjectDo;
import cn.bywin.business.bean.federal.FProjectGuestDo;
import lombok.Data;

import java.util.List;

@Data
public class ProjectVo {

    private FProjectDo project;

    private NodeDataVo host;

    private List<NodeDataVo> guest;

    List<FProjectGuestDo> projectGuest;
    private List<FDataApproveDo> dataApproveDos;


}
