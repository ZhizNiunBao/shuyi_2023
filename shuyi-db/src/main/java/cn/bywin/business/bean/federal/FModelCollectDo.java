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
 * @Date 2022-01-16
 */
@Data
@Entity
@Table(name = "fl_model_collect")
public class FModelCollectDo extends SidEntityDo {


    /**
     * project_id
     */
    @Column(name = "model_job_id")
    @ApiModelProperty(required = true, value = "模型ID", hidden = true, example = "")
    private String modelJoBId;

    /**
     * user_id
     */
    @Column(name = "user_id")
    @ApiModelProperty(required = true, value = "用户id", hidden = true, example = "")
    private String userId;


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
