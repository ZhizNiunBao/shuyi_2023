package cn.bywin.business.bean.view.federal;

import cn.bywin.business.bean.federal.FDataNodeDo;
import cn.bywin.business.bean.federal.FDataPartyDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.federal.FlDataDescDo;
import lombok.Data;

import java.util.List;

@Data
public class FDataPartyVo extends FDataPartyDo {

    private List<FNodePartyDo> nodeDos;
    private List<FDataNodeDo> nodeList;
    private List<FDataNodeDo> delList;
    private List<FDataNodeDo> addList;
    private List<String> dataIds;
    private List<String> modelIds;
    private String nodes;
    private String nodeName;
    private String nodeInId;
    private String projectId;
    private String pdId;
    private Integer approve;
    private Integer types;
    //private String tzInfo;
    private List<FlDataDescDo> tzInfoList;
    private List<FlDataDescDo> tzAddList;
    private List<FlDataDescDo> tzDelList;
    private List<FlDataDescDo> tzModList;
}
