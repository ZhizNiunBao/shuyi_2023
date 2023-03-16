package cn.bywin.business.bean.federal.graph;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;
import lombok.Data;

/**
 * @Description
 * @Author wangh
 * @Date 2021-07-27
 */
@Data
public class ComponentVos {
    private String componentName;
    private String componentId;
    private String status;
    private String component;
    private String partyId;
    private String jobId;
    private String role;
    private Integer types;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp endTime;
    private String[] name;
    private String namespace;
    private long times;
}
