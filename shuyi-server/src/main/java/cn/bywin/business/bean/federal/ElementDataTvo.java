package cn.bywin.business.bean.federal;

import java.util.List;
import lombok.Data;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
@Data
public class ElementDataTvo {
    private String nodeId;
    private String id;
    private List<FDataPartyDo> host;
    private List<FDataPartyDo> guest;

}
