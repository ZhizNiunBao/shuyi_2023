package cn.bywin.business.bean.view.federal;

import cn.bywin.business.common.base.SidEntityDo;
import lombok.Data;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-30
 */
@Data

public class FNodePartyVo extends SidEntityDo {


    private String name;

    private String ids;

    private String partyId;

    private Integer status;

    private String creatorId;


    private String creatorAccount;

    private String creatorName;


}
