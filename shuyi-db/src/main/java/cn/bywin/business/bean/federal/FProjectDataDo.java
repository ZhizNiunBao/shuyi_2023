package cn.bywin.business.bean.federal;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description
 * @Author wangh
 * @Date 2021-07-27
 */
@Data
@Entity
@Table(name = "fl_project_data")
public class FProjectDataDo extends SidEntityDo {


    /**
     * project_id
     */
    @Column(name = "project_id")
    @ApiModelProperty(required = true, value = "项目id", hidden = false, example = "")
    private String projectId;

    /**
     * data_id
     */
    @Column(name = "data_id")
    @ApiModelProperty(required = true, value = "数据集id", hidden = false, example = "")
    private String dataId;

    /**
     * node_id
     */
    @Column(name = "node_id")
    @ApiModelProperty(required = true, value = "节点id", hidden = true, example = "")
    private String nodeId;
    /**
     * 审批状态
     */
    @Column(name = "approve")
    @ApiModelProperty(required = true, value = "审批状态", hidden = true, example = "")
    private Integer approve;


    /**
     * 创建人ID
     */
    @ApiModelProperty(required = true, value = "创建人ID", hidden = true, example = "")
    @Column(name = "creator_id")
    private String creatorId;
    /**
     * 创建人帐号
     */
    @ApiModelProperty(required = true, value = "创建人帐号", hidden = true, example = "")
    @Column(name = "creator_account")
    private String creatorAccount;
    /**
     * 创建人姓名
     */
    @ApiModelProperty(required = true, value = "创建人姓名", hidden = true, example = "")
    @Column(name = "creator_name")
    private String creatorName;

}
