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
@Table(name = "t_tru_ds_task_log")
public class TTruDsTaskLogDo extends SidEntityDo {
    /**
     * 节点id
     */
    @ApiModelProperty(required = true, value = "节点id", hidden = true, example = "")
    @Column(name = "dc_id")
    private String dcId;
    /**
     * 数据集id
     */
    @ApiModelProperty(required = true, value = "数据集id", hidden = true, example = "")
    @Column(name = "ds_id")
    private String dsId;
    /**
     * 数据集名称
     */
    @ApiModelProperty(required = true, value = "数据集名称", hidden = true, example = "")
    @Column(name = "ds_code")
    private String dsCode;
    /**
     * 任务名称
     */
    @ApiModelProperty(required = true, value = "任务名称", hidden = true, example = "")
    @Column(name = "task_name")
    private String taskName;
    /**
     * 视图名称
     */
    @ApiModelProperty(required = true, value = "视图名称", hidden = true, example = "")
    @Column(name = "from_table_name")
    private String fromTableName;
    /**
     * 缓存表
     */
    @ApiModelProperty(required = true, value = "缓存表", hidden = true, example = "")
    @Column(name = "to_table_name")
    private String toTableName;
    /**
     * 缓存标志 1正在缓存 2成功 3失败
     */
    @ApiModelProperty(required = true, value = "缓存标志 1正在缓存 2成功 3失败", hidden = true, example = "")
    @Column(name = "cache_flag")
    private Integer cacheFlag;
    /**
     * 开始时间
     */
    @ApiModelProperty(required = true, value = "开始时间", hidden = true, example = "")
    @Column(name = "start_time")
    private Date startTime;
    /**
     * 结束时间
     */
    @ApiModelProperty(required = true, value = "结束时间", hidden = true, example = "")
    @Column(name = "end_time")
    private Date endTime;
    /**
     * 备注
     */
    @ApiModelProperty(required = true, value = "备注", hidden = true, example = "")
    @Column(name = "remark")
    private String remark;
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
