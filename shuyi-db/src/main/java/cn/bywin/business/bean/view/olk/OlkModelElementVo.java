package cn.bywin.business.bean.view.olk;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OlkModelElementVo {
    private String Id;
    private String tcId;

    private String name;

    private String element;

    private String modelId;

    private String x;

    private String y;

    private Integer total;

    private String shape;

    private Integer elementType;

    private Integer runStatus;

    private String icon;

    private String creatorId;

    private String creatorAccount;

    private String creatorName;

    private String ports;

    private List field;
    private  List<Map<String,Object>> data;
}
