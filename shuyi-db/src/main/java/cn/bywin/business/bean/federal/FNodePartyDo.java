package cn.bywin.business.bean.federal;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-30
 */
@Data
@Entity
@Table( name ="fl_node_party" )
public class FNodePartyDo extends SidEntityDo {

    /**
     * 节点名称
     */
    @Column(name = "name" )
    @ApiModelProperty( value = "节点名称", hidden = false, example = "")
    private String name;
    /**
     * 服务类型
     */
    @Column(name = "server_config")
    @ApiModelProperty( value = "服务类型", hidden = false, example = "")
    private String serverConfig;

    /**
     * 节点id
     */
    @Column(name = "party_id")
    @ApiModelProperty( value = "节点识别码", hidden = false, example = "")
    private Integer partyId;

    /**
     * ip 不显示
     */
    @Column(name = "ip" )
    @ApiModelProperty( value = "节点ip", hidden = false, example = "")
    private   String ip;
    /**
     * 端口 不显示
     */
    @Column(name = "port" )
    @ApiModelProperty( value = "节点端口", hidden = false, example = "")
    private   Integer port;
    /**
     * 状态
     */
    @Column(name = "status" )
    @ApiModelProperty( value = "状态", hidden = true, example = "")
    private Integer status;

    /**
     * 运行模式 0 为 eggroll 1 为spark rabbitmq 2 为 spark pulsar
     */
    @Column(name = "backend" )
    @ApiModelProperty(required = true, value = "运行模式 0 为 eggroll 1 为spark rabbitmq 2 为 spark pulsar", hidden = false, example = "")
    private Integer backend;

    /**
     * 存储类型 1为 mysql
     */
    @Column(name = "work_mode" )
    @ApiModelProperty(required = true, value = "存储类型 1为 mysql", hidden = false, example = "")
    private Integer workMode;


    /**
     * 前缀符号
     */
    @Column(name = "prefix_symbol" )
    @ApiModelProperty(required = true, value = "前缀符号", hidden = false, example = "A")
    private String prefixSymbol;


    /**
     * spark executor进程的cpu core数量
     */
    @Column(name = "executor_cores" )
    @ApiModelProperty(required = true, value = " spark executor进程的cpu core数量", hidden = false, example = "")
    private Integer executorCores;

    /**
     * spark 执行任务的并发数
     */
    @Column(name = "num_executors" )
    @ApiModelProperty(required = true, value = "spark 执行任务的并发数", hidden = false, example = "")
    private Integer numExecutors;

    /**
     *  spark  应用可分配的内存大小
     */
    @Column(name = "driver_memory" )
    @ApiModelProperty(required = true, value = "spark  应用可分配的内存大小", hidden = false, example = "")
    private String driverMemory;

    /**
     * spark executor进程的内存大小
     */
    @Column(name = "executor_memory" )
    @ApiModelProperty(required = true, value = "spark executor进程的内存大小", hidden = false, example = "")
    private String executorMemory;

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
     * storageAddress
     */
    @Column(name = "storage_address")
    @ApiModelProperty( value = "存储服务资源地址", hidden = false, example = "")
    private String storageAddress;

    /**
     * flowAddress
     */
    @Column(name = "flow_address")
    @ApiModelProperty( value = "流程服务资源地址", hidden = false, example = "")
    private String flowAddress;
    /**
     * pmsAddress
     */
    @Column(name = "pms_address")
    @ApiModelProperty( value = "pms服务资源地址", hidden = false, example = "")
    private String pmsAddress;

    /**
     * pmsAddress
     */
    @Column(name = "exchange_address")
    @ApiModelProperty( value = "exchange服务资源地址", hidden = false, example = "")
    private String exchangeAddress;
    /**
     * eggrollAddress
     */
    @Column(name = "eggroll_address")
    @ApiModelProperty( value = "eggroll服务资源地址", hidden = false, example = "")
    private String eggrollAddress;

    /**
     * sparkAddress
     */
    @Column(name = "spark_address")
    @ApiModelProperty( value = "存储服务资源地址", hidden = false, example = "")
    private String sparkAddress;

    /**
     * mqAddress
     */
    @Column(name = "mq_address")
    @ApiModelProperty( value = "mq服务资源地址", hidden = false, example = "")
    private String mqAddress;
    /**
     * pulsarAddress
     */
    @Column(name = "pulsar_address")
    @ApiModelProperty( value = "pulsar服务资源地址", hidden = false, example = "")
    private String pulsarAddress;
    /**
     * 图标
     */
    @ApiModelProperty("图标")
    @Column(name = "icon")
    private String icon;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    @Column(name = "email")
    private String email;


    /**
     * 电话
     */
    @ApiModelProperty("电话")
    @Column(name = "mobile")
    private String mobile;
    /**
     * 是否在线
     */
    @ApiModelProperty("是否在线")
    @Column(name = "is_status")
    private Integer isStatus;
    /**
     * 是否开放基础数据
     */
    @ApiModelProperty("是否开放基础数据")
    @Column(name = "is_open")
    private Integer isOpen;
}
