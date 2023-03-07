package cn.bywin.business.bean.view.federal;

import cn.bywin.business.bean.federal.FModelDo;
import cn.bywin.business.common.base.SidEntityDo;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;


@Data
public class FProjectVo extends SidEntityDo {

    private String name;

    private String project;

    private String description;

    private Integer status;

    private String host;

    private Integer disable;

    private String guest;

    private Integer types;

    private String creatorId;

    private String creatorAccount;

    private String creatorName;
    private String icon;

    private List guests;

    private long run;

    private long success;

    private long models;
    private Timestamp startTime;
    private Timestamp endTime;

}