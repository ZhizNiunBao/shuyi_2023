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
@Table(name = "fl_project_guest")
public class FProjectGuestDo extends SidEntityDo {


    /**
     * 项目id
     */
    @Column(name = "project_id")
    @ApiModelProperty(required = true, value = "项目id", hidden = true, example = "")
    private String projectId;

    /**
     * 节点id
     */
    @ApiModelProperty(required = true, value = "协助方节点id", hidden = true, example = "")
    private String nodeId;

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
