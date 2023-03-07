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
@Table(name = "t_bydb_ds_column")
public class TBydbDsColumnDo extends SidEntityDo {
    /**
     * 节点id
     */
    @ApiModelProperty(value = "节点id，最大长度(32)", hidden = true, example = "")
    @Column(name = "dc_id")
    private String dcId;
    /**
     * 数据集id
     */
    @ApiModelProperty(required = true, value = "数据集id", hidden = true, example = "")
    @Column(name = "ds_id")
    private String dsId;
    /**
     * 点id
     */
    @ApiModelProperty(required = true, value = "点id", hidden = true, example = "")
    @Column(name = "entity_id")
    private String entityId;
    /**
     * 归类 dataset entity
     */
    @ApiModelProperty(required = true, value = "归类 dataset entity", hidden = true, example = "")
    @Column(name = "etype")
    private String etype;
    /**
     * 来源id
     */
    @ApiModelProperty(required = true, value = "来源id", hidden = true, example = "")
    @Column(name = "from_column_id")
    private String fromColumnId;
    /**
     * 字段名称
     */
    @ApiModelProperty(required = true, value = "字段名称，最大长度(50)", example = "")
    @Column(name = "column_name")
    private String columnName;
    /**
     * 
     */
    @ApiModelProperty(required = true, value = "字段别名，最大长度(50)", example = "")
    @Column(name = "column_alias_name")
    private String columnAliasName;
    /**
     * 中文名称
     */
    @ApiModelProperty(required = true, value = "中文名称，最大长度(50)",example = "员工ID")
    @Column(name = "chn_name")
    private String chnName;
    /**
     * 原中文名称
     */
    @ApiModelProperty(required = true, value = "原中文名称", hidden = true, example = "")
    @Column(name = "org_chn_name")
    private String orgChnName;
    /**
     * 字段java类型
     */
    @ApiModelProperty(required = true, value = "字段java类型", example = "String")
    @Column(name = "column_type")
    private String columnType;
    /**
     * 原类型
     */
    @ApiModelProperty(required = true, value = "原类型", hidden = true, example = "")
    @Column(name = "org_type")
    private String orgType;
    /**
     * 原字段java类型
     */
    @ApiModelProperty(required = true, value = "原字段java类型", hidden = true, example = "")
    @Column(name = "org_column_type")
    private String orgColumnType;
    /**
     * 维度指标  维度0 指标1
     */
    @ApiModelProperty(required = true, value = "维度指标  维度0 指标1", example = "0")
    @Column(name = "dim_idx")
    private Integer dimIdx;
    /**
     * 原维度指标  维度0 指标1
     */
    @ApiModelProperty(required = true, value = "原维度指标  维度0 指标1", hidden = true, example = "")
    @Column(name = "org_dim_idx")
    private Integer orgDimIdx;
    /**
     * 默认转化
     */
    @ApiModelProperty(required = true, value = "默认转化", example = "")
    @Column(name = "chg_statement")
    private String chgStatement;
    /**
     * 聚合标志 1聚合
     */
    @ApiModelProperty(required = true, value = "聚合标志 1聚合", example = "")
    @Column(name = "group_flag")
    private Integer groupFlag;
    /**
     * 排序 1asc 2desc
     */
    @ApiModelProperty(required = true, value = "排序 1asc 2desc", example = "asc")
    @Column(name = "order_flag")
    private Integer orderFlag;
    /**
     * 排序顺序
     */
    @ApiModelProperty(required = true, value = "排序顺序", hidden = true, example = "")
    @Column(name = "order_num")
    private Integer orderNum;
    /**
     * 排序
     */
    @ApiModelProperty(required = true, value = "排序", hidden = true, example = "")
    @Column(name = "norder")
    private Integer norder;
    /**
     * 是否起用 1是 0否
     */
    @ApiModelProperty(required = true, value = "是否起用 1是 0否", example = "1")
    @Column(name = "enable")
    private Integer enable;
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
