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
 * @Date 2021-07-27
 */
@Data
@Entity
@Table(name = "fl_data_party")
public class FDataPartyDo extends SidEntityDo {
    /**
     * 1 为 csv 0 为 联邦分析sql源
     */
    @ApiModelProperty(required = true, value = "1 为 csv 0 为 联邦分析sql源", hidden = false, example = "")
    @Column(name = "data_type")
    private Integer dataType;
    /**
     * 数据集名称
     */
    @ApiModelProperty(required = true, value = "数据集名称", hidden = false, example = "")
    @Column(name = "name")
    private String name;
    /**
     * 标签
     */
    @ApiModelProperty(required = true, value = "标签", hidden = false, example = "")
    @Column(name = "tags")
    private String tags;
    /**
     * 数据集英文名
     */
    @ApiModelProperty(required = true, value = "数据集英文名称", hidden = false, example = "")
    @Column(name = "table_name")
    private String tableName;
    /**
     * 数据集详情
     */
    @ApiModelProperty(required = false, value = "数据集详情", hidden = false, example = "")
    @Column(name = "description")
    private String description;
    /**
     * 数据集空间 英文
     */
    @ApiModelProperty(required = true, value = "数据集空间 英文", hidden = false, example = "")
    @Column(name = "namespace")
    private String namespace;
    /**
     * 数据集是否有标题
     */
    @ApiModelProperty(required = true, value = "数据集是否有标题", hidden = false, example = "")
    @Column(name = "head")
    private Integer head;
    /**
     * 特征字段数量
     */
    @ApiModelProperty(required = true, value = "特征字段数量", hidden = true, example = "")
    @Column(name = "featrue_num")
    private Integer featrueNum;
    /**
     * 特征数据总量
     */
    @ApiModelProperty(required = true, value = "特征数据总量", hidden = true, example = "")
    @Column(name = "data_total")
    private Integer dataTotal;
    /**
     * 数据集引用数量
     */
    @ApiModelProperty(required = true, value = "数据集引用数量", hidden = false, example = "")
    @Column(name = "use_cnt")
    private Integer useCnt;
    /**
     * 特征id
     */
    @ApiModelProperty(required = true, value = "特征id", hidden = false, example = "")
    @Column(name = "sid")
    private String sid;
    /**
     * 特征名
     */
    @ApiModelProperty(required = true, value = "特征名", hidden = true, example = "")
    @Column(name = "feature_column")
    private String featureColumn;
    /**
     * 数据集状态 1为启用 0 为下线
     */
    @ApiModelProperty(required = true, value = "数据集状态 1为启用 0 为下线", hidden = true, example = "")
    @Column(name = "status")
    private Integer status;
    /**
     * 0 为 私有 1 为 公有 2 为指定节点
     */
    @ApiModelProperty(required = true, value = "0 为 私有 1 为 公有 2 为指定节点", hidden = true, example = "")
    @Column(name = "is_show")
    private Integer isShow;
    /**
     * 上传数据集任务id
     */
    @ApiModelProperty(required = false, value = "上传数据集任务id", hidden = true, example = "")
    @Column(name = "job_id")
    private String jobId;
    /**
     * 数据集是否有标签 1为有 0 为没有
     */
    @ApiModelProperty(required = false, value = "数据集是否有标签 1为有 0 为没有", hidden = false, example = "")
    @Column(name = "with_label")
    private Integer withLabel;
    /**
     * 标签名称
     */
    @ApiModelProperty(required = false, value = "标签名称", hidden = false, example = "")
    @Column(name = "label_name")
    private String labelName;
    /**
     * 标签类型
     */
    @ApiModelProperty(required = false, value = "标签类型", hidden = false, example = "")
    @Column(name = "label_type")
    private String labelType;
    /**
     * 分区
     */
    @ApiModelProperty(required = true, value = "分区", hidden = true, example = "")
    @Column(name = "part")
    private Integer part;
    /**
     * 数据源id
     */
    @ApiModelProperty(required = false, value = "数据源id", hidden = false, example = "")
    @Column(name = "db_source_id")
    private String dbSourceId;
    /**
     * 语句内容
     */
    @ApiModelProperty(required = false, value = "语句内容", hidden = false, example = "")
    @Column(name = "sql_content")
    private String sqlContent;
    /**
     * 节点id
     */
    @ApiModelProperty(required = true, value = "节点id", hidden = false, example = "")
    @Column(name = "node_id")
    private String nodeId;
    /**
     * eda html 文佳地址
     */
    @ApiModelProperty(required = false, value = "eda html 文佳地址", hidden = true, example = "")
    @Column(name = "eda_html")
    private String edaHtml;
    /**
     * 创建人id
     */
    @ApiModelProperty(required = false, value = "创建人id", hidden = true, example = "")
    @Column(name = "creator_id")
    private String creatorId;
    /**
     * 创建人账号
     */
    @ApiModelProperty(required = false, value = "创建人账号", hidden = true, example = "")
    @Column(name = "creator_account")
    private String creatorAccount;
    /**
     * 创建者名称
     */
    @ApiModelProperty(required = false, value = "创建者名称", hidden = true, example = "")
    @Column(name = "creator_name")
    private String creatorName;
}
