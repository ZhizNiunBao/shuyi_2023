package cn.bywin.business.bean.bydb;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "t_tru_discuss_object")
public class TTruDiscussObjectDo extends SidEntityDo {
    /**
     * 关联虚拟id
     */
    @ApiModelProperty(required = true, value = "关联虚拟id", hidden = true, example = "")
    @Column(name = "rel_id")
    private String relId;
    /**
     * 对象分类 db表 ds数据集
     */
    @ApiModelProperty(required = true, value = "对象分类 db表 ds数据集", hidden = true, example = "")
    @Column(name = "stype")
    private String stype;
    /**
     * 目录表id
     */
    @ApiModelProperty(required = true, value = "目录表id", hidden = true, example = "")
    @Column(name = "object_id")
    private String objectId;
    /**
     * 数据集id
     */
    @ApiModelProperty(required = true, value = "数据集id", hidden = true, example = "")
    @Column(name = "dataset_id")
    private String datasetId;
    /**
     * 
     */
//    @ApiModelProperty(required = true, value = "", hidden = true, example = "")
//    @Column(name = "dc_id")
//    private String dcId;

    /**
     * 节点id
     */
    @ApiModelProperty(required = false, value = "节点id", hidden = false, example = "")
    @Column(name = "node_party_id")
    private String nodePartyId;
    /**
     * 归属账号
     */
    @ApiModelProperty(required = false, value = "归属账号", hidden = false, example = "")
    @Column(name = "user_account")
    private String userAccount;
    /**
     * 归属用户id
     */
    @ApiModelProperty(required = false, value = "归属用户id", hidden = false, example = "")
    @Column(name = "user_id")
    private String userId;
    /**
     * 归属用户名
     */
    @ApiModelProperty(required = false, value = "归属用户名", hidden = false, example = "")
    @Column(name = "user_name")
    private String userName;

    /**
     * 表名称
     */
    @ApiModelProperty(required = true, value = "表名称", hidden = true, example = "")
    @Column(name = "obj_name")
    private String objName;
    /**
     * 表完整名称
     */
    @ApiModelProperty(required = true, value = "表完整名称", hidden = true, example = "")
    @Column(name = "obj_full_name")
    private String objFullName;
    /**
     * 中文名称
     */
    @ApiModelProperty(required = true, value = "中文名称", hidden = true, example = "")
    @Column(name = "obj_chn_name")
    private String objChnName;
    /**
     * 申请帐号
     */
    @ApiModelProperty(required = true, value = "申请帐号", hidden = true, example = "")
    @Column(name = "discuss_account")
    private String discussAccount;
    /**
     * 申请时间
     */
    @ApiModelProperty(required = true, value = "申请时间", hidden = true, example = "")
    @Column(name = "discuss_time")
    private Date discussTime;
    /**
     * 审批说明
     */
    @ApiModelProperty(required = true, value = "审批说明", hidden = true, example = "")
    @Column(name = "discuss_note")
    private String discussNote;
    /**
     * 状态 1申请 0不通过 2通过
     */
    @ApiModelProperty(required = true, value = "状态 1申请 0不通过 2通过", hidden = true, example = "")
    @Column(name = "status")
    private Integer status;
    /**
     * 审批帐号
     */
    @ApiModelProperty(required = true, value = "审批帐号", hidden = true, example = "")
    @Column(name = "check_account")
    private String checkAccount;
    /**
     * 审批说明
     */
    @ApiModelProperty(required = true, value = "审批说明", hidden = true, example = "")
    @Column(name = "check_remark")
    private String checkRemark;
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
