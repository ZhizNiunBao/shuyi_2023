package cn.bywin.business.bean.view.federal;

import cn.bywin.business.bean.federal.FDataPartyDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.common.util.MyBeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author wangh
 * @Date 2021-07-27
 */
public class ProjectDetail {

    private FNodePartyDo node;
    private List<FDataPartyVo> dList;


    public FNodePartyDo getNode() {
        return node;
    }

    public void setNode(FNodePartyDo node) {
        this.node = node;
    }

    public List<FDataPartyVo> getDList() {
        return dList;
    }

    public void setDList(List<FDataPartyDo> dList, Map<String, Integer> idApproveMap, Map<String, String> idDataMap) throws Exception {
        List<FDataPartyVo> dataPartyVos = new ArrayList<>();
        if (dList != null && dList.size() > 0) {

            for (FDataPartyDo fDataPartyDo:dList){
                FDataPartyVo info = new FDataPartyVo();
                MyBeanUtils.copyBean2Bean(info, fDataPartyDo);
                if (idApproveMap != null&&idApproveMap.keySet().contains(fDataPartyDo.getId())){
                    info.setPdId(idDataMap.get(fDataPartyDo.getId()));
                    info.setApprove(idApproveMap.get(fDataPartyDo.getId()));
                }else {
                    info.setApprove(1);
                }
                dataPartyVos.add(info);
            }
        }
        this.dList = dataPartyVos;
    }
}
