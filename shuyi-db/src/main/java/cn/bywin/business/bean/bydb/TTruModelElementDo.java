package cn.bywin.business.bean.bydb;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_tru_model_element")
public class TTruModelElementDo extends SidEntityDo {
    /**
     * 组件配置
     */
    @ApiModelProperty(required = false, value = "组件配置", hidden = false, example = "")
    @Column(name = "config")
    private String config;
    /**
     * 创建人账号
     */
    @ApiModelProperty(required = false, value = "创建人账号", hidden = false, example = "")
    @Column(name = "creator_account")
    private String creatorAccount;
    /**
     * 创建人ID
     */
    @ApiModelProperty(required = false, value = "创建人ID", hidden = false, example = "")
    @Column(name = "creator_id")
    private String creatorId;
    /**
     * 创建人姓名
     */
    @ApiModelProperty(required = false, value = "创建人姓名", hidden = false, example = "")
    @Column(name = "creator_name")
    private String creatorName;
    /**
     * 模型编码
     */
    @ApiModelProperty(required = false, value = "模型编码", hidden = false, example = "")
    @Column(name = "element")
    private String element;
    /**
     * 组件类型 1为表 0 为 系统组件
     */
    @ApiModelProperty(required = false, value = "组件类型 1为表 0 为 系统组件", hidden = false, example = "")
    @Column(name = "element_type")
    private Integer elementType;
    /**
     * 图标
     */
    @ApiModelProperty(required = false, value = "图标", hidden = false, example = "")
    @Column(name = "icon")
    private String icon;
    /**
     * 项目id
     */
    @ApiModelProperty(required = false, value = "项目id", hidden = false, example = "")
    @Column(name = "model_id")
    private String modelId;
    /**
     * 组件名称
     */
    @ApiModelProperty(required = false, value = "组件名称", hidden = false, example = "")
    @Column(name = "name")
    private String name;
    /**
     * 原名称
     */
    @ApiModelProperty(required = false, value = "原名称", hidden = false, example = "")
    @Column(name = "orig_name")
    private String origName;
    /**
     *
     */
//    @ApiModelProperty(required = false, value = "", hidden = false, example = "")
//    @Column(name = "param_sql")
//    private String paramSql;
    /**
     * 瞄点列表
     */
//    @ApiModelProperty(required = false, value = "瞄点列表", hidden = false, example = "")
//    @Column(name = "ports")
//    private String ports;
    /**
     *
     */
    @ApiModelProperty(required = false, value = "", hidden = false, example = "")
    @Column(name = "run_sql")
    private String runSql;
    /**
     * 备注
     */
    @ApiModelProperty(required = false, value = "", hidden = false, example = "")
    @Column(name = "remark")
    private String remark;
    /**
     * 运行顺序
     */
    @ApiModelProperty(required = false, value = "", hidden = false, example = "")
    @Column(name = "run_order")
    private Integer runOrder;
    /**
     * flink数据源语句
     */
    @ApiModelProperty(required = false, value = "flink数据源语句", hidden = false, example = "")
    @Column(name = "table_sql")
    private String tableSql;
    /**
     * 运行状态 -1 代表配置未保存 无法运行，0 配置保存 未审核 1 可以运行
     */
    @ApiModelProperty(required = false, value = "运行状态 -1 代表配置未保存 无法运行，0 配置保存 未审核 1 可以运行", hidden = false, example = "")
    @Column(name = "run_status")
    private Integer runStatus;
    /**
     * 图标形状
     */
    @ApiModelProperty(required = false, value = "图标形状", hidden = false, example = "")
    @Column(name = "shape")
    private String shape;
    /**
     * 实例化表或者组件ID
     */
    @ApiModelProperty(required = false, value = "实例化表或者组件ID", hidden = false, example = "")
    @Column(name = "tc_id")
    private String tcId;
    /**
     * 限制条数
     */
    @ApiModelProperty(required = false, value = "限制条数", hidden = false, example = "")
    @Column(name = "total")
    private Integer total;
    /**
     * x坐标
     */
    @ApiModelProperty(required = false, value = "x坐标", hidden = false, example = "")
    @Column(name = "x")
    private String x;
    /**
     * y坐标
     */
    @ApiModelProperty(required = false, value = "y坐标", hidden = false, example = "")
    @Column(name = "y")
    private String y;
}
