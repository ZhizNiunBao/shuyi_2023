package cn.bywin.business.bean.olk;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "t_olk_model")
public class TOlkModelDo extends SidEntityDo {
    /**
     * 可视化组件类型 1 为 纵向 0 为横向
     */
    @ApiModelProperty(required = true, value = "可视化组件类型 1 为 纵向 0 为横向", hidden = false, example = "1")
    @Column(name = "model_type")
    private Integer modelType;

    /**
     * sql语句
     */
    @ApiModelProperty(required = false, value = "sql语句", hidden = true, example = "")
    @Column(name = "run_sql")
    private String runSql;
    /**
     * 构建类型1 为可视化组件 0 为编写sql
     */
    @ApiModelProperty(required = false, value = "构建类型1 为可视化组件 0 为编写sql", hidden = false, example = "1")
    @Column(name = "build_type")
    private Integer buildType;
    /**
     * 模型详情
     */
    @ApiModelProperty(required = false, value = "模型详情", hidden = false, example = "")
    @Column(name = "description")
    private String description;
    /**
     * 标签
     */
    @ApiModelProperty(required = false, value = "标签", hidden = false, example = "")
    @Column(name = "tags")
    private String tags;
    /**
     * 模型名称
     */
    @ApiModelProperty(required = true, value = "模型名称", hidden = false, example = "test")
    @Column(name = "name")
    private String name;
    /**
     * 模型数据输出大小值
     */
    @ApiModelProperty(required = false, value = "模型数据输出大小值", hidden = true, example = "")
    @Column(name = "total")
    private Integer total;
    /**
     * 路径id
     */
    @ApiModelProperty(required = false, value = "路径id", hidden = false, example = "")
    @Column(name = "folder_id")
    private String folderId;
    /**
     * 1 为离线 0 为实时
     */
    @ApiModelProperty(required = false, value = "1 为离线 0 为实时", hidden = false, example = "1")
    @Column(name = "types")
    private Integer types;
    /**
     * 是否显示
     */
    @ApiModelProperty(required = false, value = "是否显示", hidden = true, example = "")
    @Column(name = "disable")
    private Integer disable;
    /**
     * 任务状态 0 失败 1 成功 2 正在运行 3 等待 4 其他 9 删除
     */
    @ApiModelProperty(required = false, value = "任务状态 0 失败 1 成功 2 正在运行 3 等待 4 其他 9 删除", hidden = true, example = "")
    @Column(name = "status")
    private Integer status;

    /**
     * 节点id
     */
    @ApiModelProperty(required = false, value = "节点id", hidden = false, example = "")
    @Column(name = "node_party_id")
    private String nodePartyId;
    /**
     * 上传标志 1已上传 3待上传
     */
    @ApiModelProperty(required = false, value = "上传标志 1已上传 3待上传", hidden = false, example = "")
    @Column(name = "syn_flag")
    private Integer synFlag;

    /**
     * 授权账号
     */
//    @ApiModelProperty(required = false, value = "授权账号", hidden = true, example = "")
//    @Column(name = "admin_id")
//    private String adminId;
    /**
     * 模型类型 olk spark flink
     */
    @ApiModelProperty(required = false, value = "模型类型 olk spark flink", hidden = true, example = "")
    @Column(name = "config")
    private String config;

    /**
     * 节点id
     */
    @ApiModelProperty(required = true, value = "节点id", hidden = true, example = "")
    @Column(name = "dc_id")
    private String dcId;

    /**
     * 是否缓存 1缓存 0不缓存
     */
    @ApiModelProperty(required = false, value = "是否缓存 1缓存 0不缓存", hidden = true, example = "")
    @Column(name = "cache_flag")
    private Integer cacheFlag;
    /**
     * 运行类型 ；1 为 手动运行 0 为 调度运行
     */
    @ApiModelProperty(required = false, value = "运行类型 ；1 为 手动运行 0 为 调度运行", hidden = false, example = "1")
    @Column(name = "run_type")
    private Integer runType;
    /**
     * 运行频率
     */
    @ApiModelProperty(required = false, value = "运行频率", hidden = true, example = "")
    @Column(name = "run_corn")
    private String runCorn;
    /**
     * 最后运行时间
     */
    @ApiModelProperty(required = false, value = "最后运行时间", hidden = true, example = "")
    @Column(name = "last_run_time")
    private Date lastRunTime;
    /**
     * 视图名称
     */
    @ApiModelProperty(required = false, value = "视图名称", hidden = true, example = "")
    @Column(name = "view_name")
    private String viewName;
    /**
     * 缓存编号
     */
    @ApiModelProperty(required = false, value = "缓存编号", hidden = true, example = "")
    @Column(name = "cache_task_no")
    private String cacheTaskNo;
    /**
     * 缓存表
     */
    @ApiModelProperty(required = true, value = "缓存表", hidden = true, example = "")
    @Column(name = "cache_table_name")
    private String cacheTableName;
    /**
     * 创建者id
     */
    @ApiModelProperty(required = true, value = "创建者id", hidden = true, example = "")
    @Column(name = "creator_id")
    private String creatorId;
    /**
     * 创建者账号
     */
    @ApiModelProperty(required = true, value = "创建者账号", hidden = true, example = "")
    @Column(name = "creator_account")
    private String creatorAccount;
    /**
     * 创建人姓名
     */
    @ApiModelProperty(required = true, value = "创建人姓名", hidden = true, example = "")
    @Column(name = "creator_name")
    private String creatorName;
    /**
     * 归属部门编号
     */
    @ApiModelProperty(required = true, value = "归属部门编号", hidden = true, example = "")
    @Column(name = "create_dept_no")
    private String createDeptNo;
    /**
     * 归属部门名称
     */
    @ApiModelProperty(required = true, value = "归属部门名称", hidden = true, example = "")
    @Column(name = "create_dept_na")
    private String createDeptNa;
    /**
     * 输出组件类型
     */
    @ApiModelProperty(required = false, value = "输出组件类型", hidden = true, example = "")
    @Column(name = "output_type")
    private Integer outputType;
    /**
     * 输出组件id
     */
    @ApiModelProperty(required = false, value = "输出组件id", hidden = true, example = "")
    @Column(name = "output_id")
    private String outputId;
    /**
     * sql语句字段
     */
    @ApiModelProperty(required = false, value = "sql语句字段", hidden = true, example = "")
    @Column(name = "param_config")
    private String paramConfig;
}
