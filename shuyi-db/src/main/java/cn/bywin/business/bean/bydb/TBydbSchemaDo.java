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
@Table(name = "t_bydb_schema")
public class TBydbSchemaDo extends SidEntityDo {
    /**
     * 节点id
     */
    @ApiModelProperty(value = "节点id，最大长度(32)", hidden = true, example = "")
    @Column(name = "dc_id")
    private String dcId;
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
     * 上传标志 1已上传 3待上传
     */
    @ApiModelProperty(required = false, value = "上传标志 1已上传 3待上传", hidden = false, example = "")
    @Column(name = "syn_flag")
    private Integer synFlag;
    /**
     * 同步标志 0未同步 1已同步 2待同步
     */
//    @ApiModelProperty(required = false, value = "同步标志 0未同步 1已同步 2待同步", hidden = false, example = "")
//    @Column(name = "share_flag")
//    private Integer shareFlag;
    /**
     * 同步操作时间
     */
//    @ApiModelProperty(required = false, value = "同步操作时间", hidden = false, example = "")
//    @Column(name = "share_time")
//    private Date shareTime;
    /**
     * 数据库id
     */
    @ApiModelProperty(required = true, value = "数据库id", hidden = true, example = "")
    @Column(name = "db_id")
    private String dbId;
    /**
     * 库名称
     */
    @ApiModelProperty(required = true, value = "库名称，最大长度(30)", example = "employees")
    @Column(name = "schema_name")
    private String schemaName;
    /**
     * 库完整名称
     */
    @ApiModelProperty(required = true, value = "库完整名称", hidden = true, example = "")
    @Column(name = "sche_full_name")
    private String scheFullName;
    /**
     * 中文名称
     */
    @ApiModelProperty(required = true, value = "中文名称，最大长度(50)", example = "雇员")
    @Column(name = "schema_chn_name")
    private String schemaChnName;
    /**
     * 图标
     */
    @ApiModelProperty(required = true, value = "图标", hidden = true, example = "")
    @Column(name = "icon")
    private String icon;
    /**
     * 排序
     */
    @ApiModelProperty(required = true, value = "排序", hidden = true, example = "")
    @Column(name = "norder")
    private Integer norder;
    /**
     * 是否启用 0未启用 1部分启用 2全部启用
     */
    @ApiModelProperty(required = true, value = "是否启用 0未启用 1启用", example = "1")
    @Column(name = "enable")
    private Integer enable;

    /**
     * 删除标志 1已删除
     */
    @ApiModelProperty(required = false, value = "删除标志 1已删除", hidden = true, example = "0")
    @Column(name = "del_flag")
    private Integer delFlag;

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
