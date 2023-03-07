package cn.bywin.business.bean.view.federal;

import cn.bywin.business.bean.federal.FDataApproveDo;
import lombok.Data;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
public class FDataApproveVo extends FDataApproveDo {

    private String createNodePartyId;
    private String createNodePartyName;
    private Integer createUserLock;
    protected Date createUserRegTime;

    private String nodeName;
    private String dataName;
    private String projectName;
    private List<String> dataIds;
    private  Integer type;
}
