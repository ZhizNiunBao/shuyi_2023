package cn.bywin.business.bean.federal;

import java.util.List;
import lombok.Data;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
@Data
public class NodePartyVo {

    private FNodePartyDo node;
    private List<FDataPartyDo> dList;
}
