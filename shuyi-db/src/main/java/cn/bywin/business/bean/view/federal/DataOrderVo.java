package cn.bywin.business.bean.view.federal;

import cn.bywin.business.bean.federal.FModelJobDo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class DataOrderVo {

    private String dataName;
    private String nodeName;
    private String keyName;
    private String projectId;
    private String dataId;
    private String projectName;
    private String modelName;
    private String modelId;
    private Integer status;
    private Integer types;
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private Timestamp projectTime;
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private Timestamp modelTime;
    private Integer approve;
    private String creatorName;
    private List<String> dataIds;
    private List<FModelJobDo> children;

}
