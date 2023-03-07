package cn.bywin.business.bean.federal;

import cn.bywin.business.common.base.SidEntityDo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @Description
 * @Author wangh
 * @Date 2021-07-27
 */
@Data
@Entity
@Table(name = "fl_model_job")
public class FModelJobDo extends SidEntityDo {
    /**
     * 模型版本  配置
     */
    @ApiModelProperty(required = true, value = "模型版本配置", hidden = true, example = "")
    @Column(name = "model_config")
    private String modelConfig;


    /**
     * dsl  配置
     */
    @ApiModelProperty(required = true, value = "模型版本配置", hidden = true, example = "")
    @Column(name = "dsl_config")
    private String dslConfig;

    /**
     * 模型评分ks
     */
    @ApiModelProperty(required = true, value = "模型评分ks", hidden = true, example = "")
    @Column(name = "ks")
    private Double ks;

    /**
     * 模型评分auc
     */
    @ApiModelProperty(required = true, value = "模型评分auc", hidden = true, example = "")
    @Column(name = "auc")
    private Double auc;
    /**
     * 模型版本
     */
    @ApiModelProperty(required = true, value = "模型版本", hidden = true, example = "")
    @Column(name = "versions")
    private String versions;
    /**
     * 发布
     */
    @ApiModelProperty(required = true, value = "1 发布成功 0 发布失败 2 未发布", hidden = true, example = "")
    @Column(name = "deploy")
    private Integer deploy;
    /**
     * 模型
     */
    @ApiModelProperty(required = true, value = "模模型id", hidden = true, example = "")
    @Column(name = "model_id")
    private String modelId;
    /**
     * 描述
     */
    @Column(name = "job_id")
    @ApiModelProperty(required = true, value = "任务运行id", hidden = true, example = "")
    private String jobId;
    /**
     * 状态
     */
    @Column(name = "status")
    @ApiModelProperty(required = false, value = "运行状态 1 在线 0 下线", hidden = false, example = "")
    private Integer status;
    /**
     * host
     */
    @ApiModelProperty(required = false, value = "发起方id", hidden = false, example = "")
    @Column(name = "host")
    private String host;
    /**
     * host
     */
    @Column(name = "guest")
    @ApiModelProperty(required = false, value = "协助方id", hidden = false, example = "")
    private String guest;

    /**
     * 类型
     */
    @Column(name = "types")
    @ApiModelProperty(required = false, value = "类型", hidden = false, example = "")
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


    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间", hidden = true, example = "")
    @Column(name = "start_time")
    protected Timestamp startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间", hidden = true, example = "")
    @Column(name = "end_time")
    protected Timestamp endTime;

}
