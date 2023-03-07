package cn.bywin.business.bean.federal;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "fl_datasource")
public class FDatasourceDo extends SidEntityDo {
    /**
     * 数据源名称
     */
    @ApiModelProperty(required = true, value = "数据源名称", hidden = false, example = "")
    @Column(name = "ds_name")
    private String dsName;
    /**
     * 数据库类型 MYSQL,ORACLE,CLICKHOUSE, HIVE, SPARK,POSTGRESQL
     */
    @ApiModelProperty(required = true, value = "数据库类型 MYSQL,ORACLE,CLICKHOUSE, HIVE, SPARK,POSTGRESQL", hidden = false, example = "")
    @Column(name = "ds_type")
    private String dsType;
    /**
     * 数据源描述
     */
    @ApiModelProperty(required = false, value = "数据源描述", hidden = false, example = "")
    @Column(name = "ds_desc")
    private String dsDesc;
    /**
     * 集群地址
     */
    @ApiModelProperty(required = true, value = "集群地址", hidden = true, example = "")
    @Column(name = "ds_hosts")
    private String dsHosts;
    /**
     * 主机IP
     */
    @ApiModelProperty(required = true, value = "主机IP", hidden = false, example = "")
    @Column(name = "ds_ip")
    private String dsIp;
    /**
     * 端口
     */
    @ApiModelProperty(required = true, value = "端口", hidden = false, example = "")
    @Column(name = "ds_port")
    private Integer dsPort;
    /**
     * 数据库名
     */
    @ApiModelProperty(required = false, value = "数据库名", hidden = false, example = "")
    @Column(name = "ds_database")
    private String dsDatabase;
    /**
     * 数据模式名
     */
    @ApiModelProperty(required = false, value = "数据模式名", hidden = false, example = "")
    @Column(name = "ds_schema")
    private String dsSchema;
    /**
     * 用户名
     */
    @ApiModelProperty(required = true, value = "用户名", hidden = false, example = "")
    @Column(name = "username")
    private String username;
    /**
     * 密码
     */
    @ApiModelProperty(required = true, value = "密码", hidden = false, example = "")
    @Column(name = "password")
    private String password;
    /**
     * 数据库连接串
     */
    @ApiModelProperty(required = true, value = "数据库连接串", hidden = true, example = "")
    @Column(name = "jdbc_url")
    private String jdbcUrl;
    /**
     * 驱动
     */
    @ApiModelProperty(required = true, value = "驱动", hidden = true, example = "")
    @Column(name = "ds_driver")
    private String dsDriver;
    /**
     * 0不可用 1有效 2失效
     */
    @ApiModelProperty(required = true, value = "0不可用 1有效 2失效", hidden = true, example = "")
    @Column(name = "enable")
    private Integer enable;
    /**
     * 其他参数
     */
    @ApiModelProperty(required = false, value = "其他参数", hidden = false, example = "")
    @Column(name = "paraset")
    private String paraset;
    /**
     * sink配置
     */
    @ApiModelProperty(required = false, value = "sink配置", hidden = false, example = "")
    @Column(name = "sink_set")
    private String sinkSet;
    /**
     * source配置
     */
    @ApiModelProperty(required = false, value = "source配置", hidden = false, example = "")
    @Column(name = "source_set")
    private String sourceSet;
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
     * 消息类型
     */
    @ApiModelProperty(required = true, value = "消息类型", hidden = true, example = "")
    @Column(name = "message_type")
    private String messageType;
    /**
     * 消息格式
     */
    @ApiModelProperty(required = true, value = "消息格式", hidden = true, example = "")
    @Column(name = "message_format")
    private String messageFormat;
    /**
     * 消息主题
     */
    @ApiModelProperty(required = true, value = "消息主题", hidden = true, example = "")
    @Column(name = "topic")
    private String topic;
}
