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
@Table(name = "t_bydb_dataset")
public class TBydbDatasetDo extends SidEntityDo {
    /**
     * 节点id
     */
//    @ApiModelProperty(required = false, value = "节点id，最大长度(32)", hidden = true, example = "")
//    @Column(name = "dc_id")
//    private String dcId;
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
     * 同步标志 0未同步 1已同步 2待同步
     */
    @ApiModelProperty(required = false, value = "同步标志 0未同步 1已同步 2待同步", hidden = false, example = "")
    @Column(name = "share_flag")
    private Integer shareFlag;
    /**
     * 同步操作时间
     */
    @ApiModelProperty(required = false, value = "同步操作时间", hidden = false, example = "")
    @Column(name = "share_time")
    private Date shareTime;
    /**
     * 前缀编码
     */
    @ApiModelProperty(required = true, value = "前缀编码，最大长度(5)", hidden = false, example = "ads_")
    @Column(name = "code1")
    private String code1;
    /**
     * 输入编码
     */
    @ApiModelProperty(required = true, value = "输入编码，最大长度(15)", hidden = false, example = "szsszt")
    @Column(name = "code2")
    private String code2;
    /**
     * 完整编码
     */
    @ApiModelProperty(required = false, value = "完整编码", hidden = true, example = "")
    @Column(name = "set_code")
    private String setCode;
    /**
     * dc完整编码
     */
    @ApiModelProperty(required = false, value = "dc完整编码", hidden = true, example = "")
    @Column(name = "dc_set_code")
    private String dcSetCode;
    /**
     * 视图名称
     */
    @ApiModelProperty(required = false, value = "视图名称", hidden = true, example = "")
    @Column(name = "view_name")
    private String viewName;
    /**
     * dc端视图名称
     */
    @ApiModelProperty(required = false, value = "dc端视图名称", hidden = true, example = "")
    @Column(name = "dc_view_name")
    private String dcViewName;
    /**
     * 中文名称
     */
    @ApiModelProperty(required = false, value = "中文名称，最大长度(30)", hidden = false, example = "按天统计")
    @Column(name = "set_chn_name")
    private String setChnName;
    /**
     * 数据级别
     */
    @ApiModelProperty(required = false, value = "数据级别", hidden = true, example = "")
    @Column(name = "data_level")
    private Integer dataLevel;
    /**
     * 分类
     */
    @ApiModelProperty(required = true, value = "分类", hidden = false, example = "ide")
    @Column(name = "stype")
    private String stype;
    /**
     * 数据源id
     */
    @ApiModelProperty(required = true, value = "数据源id", hidden = false, example ="")
    @Column(name = "datasource_id")
    private String datasourceId;
    /**
     * 数据目录id
     */
    @ApiModelProperty(required = false, value = "数据目录id", hidden = true, example = "")
    @Column(name = "database_id")
    private String databaseId;
    /**
     * 路径id
     */
    @ApiModelProperty(required = false, value = "路径id", hidden = true, example = "")
    @Column(name = "folder_id")
    private String folderId;
    /**
     * 是否私有 1是 0公开
     */
    @ApiModelProperty(required = true, value = "是否私有 1是 0公开", hidden = false, example = "0")
    @Column(name = "priv_flag")
    private Integer privFlag;
    /**
     * 归属账号
     */
    @ApiModelProperty(required = false, value = "归属账号", hidden = true, example = "")
    @Column(name = "account")
    private String account;
    /**
     * 范围条数
     */
    @ApiModelProperty(required = false, value = "范围条数", hidden = false, example = "100")
    @Column(name = "result_limit")
    private Integer resultLimit;
    /**
     * 是否去重
     */
    @ApiModelProperty(required = false, value = "是否去重", hidden = false, example = "1")
    @Column(name = "distinct_flag")
    private Integer distinctFlag;
    /**
     * 标签
     */
    @ApiModelProperty(required = false, value = "标签", hidden = false, example = "标签")
    @Column(name = "ds_label")
    private String dsLabel;
    /**
     * 是否起用 1是 0否 2需要重建
     */
    @ApiModelProperty(required = false, value = "是否起用 1是 0否 2需要重建", hidden = true, example = "")
    @Column(name = "enable")
    private Integer enable;
    /**
     * 是否缓存 1缓存 0不缓存
     */
    @ApiModelProperty(required = false, value = "是否缓存 1缓存 0不缓存", hidden = true, example = "")
    @Column(name = "cache_flag")
    private Integer cacheFlag;
    /**
     * 缓存表
     */
    @ApiModelProperty(required = false, value = "缓存表", hidden = true, example = "")
    @Column(name = "cache_table_name")
    private String cacheTableName;
    /**
     * 缓存编号
     */
    @ApiModelProperty(required = false, value = "缓存编号", hidden = true, example = "")
    @Column(name = "cache_task_no")
    private String cacheTaskNo;
    /**
     * 锁定标志 1锁定 0非锁定
     */
    @ApiModelProperty(required = false, value = "锁定标志 1锁定 0非锁定", hidden = true, example = "0")
    @Column(name = "lock_flag")
    private Integer lockFlag;
    /**
     * 删除标志 1已删除
     */
    @ApiModelProperty(required = false, value = "删除标志 1已删除", hidden = true, example = "0")
    @Column(name = "del_flag")
    private Integer delFlag;

    /**
     * 备注
     */
    @ApiModelProperty(required = false, value = "备注", hidden = false, example = "备注")
    @Column(name = "remark")
    private String remark;
    /**
     * 创建人ID
     */
    @ApiModelProperty(required = false, value = "创建人ID", hidden = true, example = "")
    @Column(name = "creator_id")
    private String creatorId;
    /**
     * 创建人帐号
     */
    @ApiModelProperty(required = false, value = "创建人帐号", hidden = true, example = "")
    @Column(name = "creator_account")
    private String creatorAccount;
    /**
     * 创建人姓名
     */
    @ApiModelProperty(required = false, value = "创建人姓名", hidden = true, example = "")
    @Column(name = "creator_name")
    private String creatorName;
}
