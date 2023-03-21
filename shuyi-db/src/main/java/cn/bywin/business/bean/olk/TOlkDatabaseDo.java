package cn.bywin.business.bean.olk;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_olk_database")
public class TOlkDatabaseDo extends SidEntityDo {

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
     * 数据源id
     */
    @ApiModelProperty(required = true, value = "数据源id", hidden = true, example = "")
    @Column(name = "dbsource_id")
    private String dbsourceId;
    /**
     * 完整目录名称
     */
    @ApiModelProperty(required = true, value = " 完整目录名称，最大长度(20)", hidden = true, example = "")
    @Column(name = "db_name")
    private String dbName;
    /**
     * 节点目录名称
     */
    @ApiModelProperty(required = true, value = "节点目录名称，最大长度(50)", hidden = false, example = "erju")
    @Column(name = "dc_db_name")
    private String dcDbName;

    /**
     * 中文名称
     */
    @ApiModelProperty(required = true, value = "中文名称，最大长度(200)", hidden = true, example = "委办二局数据库")
    @Column(name = "db_chn_name")
    private String dbChnName;
    /**
     * 类型
     */
    @ApiModelProperty(required = true, value = "类型", hidden = true, example = "")
    @Column(name = "db_type")
    private String dbType;
    /**
     * 目录分组ID
     */
    @ApiModelProperty(required = true, value = "目录分组ID，最大长度(32)", hidden = true, example = "")
    @Column(name = "catalog_type")
    private String catalogType;
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
     * 是否启用 0未启用 1启用
     */
    @ApiModelProperty(required = true, value = "是否启用 0未启用 1启用", hidden = true, example = "1")
    @Column(name = "enable")
    private Integer enable;
    /**
     * 删除标志 1已删除
     */
    @ApiModelProperty(required = false, value = "删除标志 1已删除", hidden = true, example = "0")
    @Column(name = "del_flag")
    private Integer delFlag;
    /**
     * 创建语句
     */
    //@ApiModelProperty(required = true, value = "创建语句", hidden = true, example = "")
    //@Column(name = "ddl_content")
    //private String ddlContent;

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
