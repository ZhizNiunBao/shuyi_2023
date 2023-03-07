package cn.bywin.business.bean.olk;

import cn.bywin.business.common.base.SidEntityDo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@Entity
@Table( name ="t_olk_model_element_job" )
public class TOlkModelElementJobDo extends SidEntityDo {
    /**
     * 模型ID
     */
    @ApiModelProperty(required = true, value = "模型ID", hidden = true, example = "")
    @Column(name = "model_id" )
    private String modelId;

    /**
     * 任务名称
     */
    @ApiModelProperty(required = true, value = "任务名称", hidden = false, example = "")
    @Column(name = "name" )
    private String name;

    /**
     * 运行状态
     */
    @ApiModelProperty(required = true, value = "运行状态;0 失败 1 成功 2 正在运行 3 等待 4 其他 9 删", hidden = false, example = "")
    @Column(name = "job_status" )
    private Integer jobStatus;
    /**
     * 任务开始时间
     */
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_time" )
    @ApiModelProperty(value = "创建时间",hidden = false)
    protected Timestamp startTime;

    /**
     * 任务结束时间
     */
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "end_time" )
    @ApiModelProperty(value = "修改时间",hidden = false)
    protected Timestamp endTime;
    /**
     * 任务配置
     */
    @ApiModelProperty(required = true, value = "任务配置", hidden = true, example = "")
    @Column(name = "config" )
    private String config;
    /**
     * 任务类型
     */
    @ApiModelProperty(required = true, value = "任务类型除", hidden = true, example = "")
    @Column(name = "job_type" )
    private Integer jobType;
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
