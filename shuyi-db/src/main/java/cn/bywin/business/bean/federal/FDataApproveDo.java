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
@Table(name = "fl_data_approve")
public class FDataApproveDo extends SidEntityDo {


    /**
     * pd_id
     */
    @Column(name = "project_id")
    @ApiModelProperty(required = true, value = "项目数据id", hidden = true, example = "")
    private String projectId;

    /**
     * data_id
     */
    @Column(name = "data_id")
    @ApiModelProperty(required = true, value = "数据集id", hidden = true, example = "")
    private String dataId;
    /**
     * 数据归类
     */
    @Column(name = "data_catalog")
    @ApiModelProperty(required = true, value = "数据归类", hidden = true, example = "")
    private String dataCatalog;
    /**
     * content
     */
    @Column(name = "content")
    @ApiModelProperty(required = true, value = "申请内容", hidden = true, example = "")
    private String content;

    /**
     * content
     */
    @Column(name = "approval")
    @ApiModelProperty(required = true, value = "审批理由", hidden = true, example = "")
    private String approval;
    /**
     * node_id
     */
    @Column(name = "node_id")
    @ApiModelProperty(required = true, value = "节点id", hidden = true, example = "")
    private String nodeId;


    /**
     * user_id
     */
    @Column(name = "user_id")
    @ApiModelProperty(required = true, value = "用户id", hidden = true, example = "")
    private String userId;

    /**
     * user_name
     */
    @Column(name = "user_name")
    @ApiModelProperty(required = true, value = "用户名称", hidden = true, example = "")
    private String userName;
    /**
     * 审批状态
     */
    @Column(name = "approve")
    @ApiModelProperty(required = true, value = "审批状态", hidden = true, example = "")
    private Integer approve;

    /**
     * 类型
     */
    @Column(name = "types")
    @ApiModelProperty(required = true, value = "类型", hidden = true, example = "")
    private Integer types;
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
