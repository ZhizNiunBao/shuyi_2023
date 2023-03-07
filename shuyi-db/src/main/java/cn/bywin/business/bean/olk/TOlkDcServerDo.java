package cn.bywin.business.bean.olk;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_olk_dc_server")
public class TOlkDcServerDo extends SidEntityDo {
    /**
     * 数据中心代码
     */
    @ApiModelProperty(required = true, value = "数据中心代码", hidden = true, example = "")
    @Column(name = "dc_code")
    private String dcCode;
    /**
     * 数据中心名称
     */
    @ApiModelProperty(required = true, value = "数据中心名称", hidden = true, example = "")
    @Column(name = "dc_name")
    private String dcName;
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
     * 部门编号
     */
//    @ApiModelProperty(required = true, value = "部门编号", hidden = true, example = "")
//    @Column(name = "dept_no")
//    private String deptNo;
    /**
     * 部门名称
     */
//    @ApiModelProperty(required = true, value = "部门名称", hidden = true, example = "")
//    @Column(name = "dept_name")
//    private String deptName;
    /**
     * 数据中心私钥
     */
    @ApiModelProperty(required = true, value = "数据中心私钥", hidden = true, example = "")
    @Column(name = "dc_priv")
    private String dcPriv;
    /**
     * 数据中心公钥
     */
    @ApiModelProperty(required = true, value = "数据中心公钥", hidden = true, example = "")
    @Column(name = "dc_pub")
    private String dcPub;
    /**
     * 是否dc服务 1是 0否
     */
    @ApiModelProperty(required = true, value = "是否dc服务 1是 0否", hidden = true, example = "")
    @Column(name = "dc_type")
    private Integer dcType;
    /**
     * jdbc地址
     */
    @ApiModelProperty(required = true, value = "jdbc地址", hidden = true, example = "")
    @Column(name = "jdbc_url")
    private String jdbcUrl;
    /**
     * olk服务地址
     */
    @ApiModelProperty(required = true, value = "olk服务地址", hidden = true, example = "")
    @Column(name = "connection_url")
    private String connectionUrl;
    /**
     * olk用户名
     */
    @ApiModelProperty(required = true, value = "olk用户名", hidden = true, example = "")
    @Column(name = "connection_user")
    private String connectionUser;
    /**
     * olk密码
     */
    @ApiModelProperty(required = true, value = "olk密码", hidden = true, example = "")
    @Column(name = "connection_pwd")
    private String connectionPwd;
    /**
     * 负责帐号
     */
//    @ApiModelProperty(required = true, value = "负责帐号", hidden = true, example = "")
//    @Column(name = "manage_account")
//    private String manageAccount;
    /**
     * 负责人姓名
     */
//    @ApiModelProperty(required = true, value = "负责人姓名", hidden = true, example = "")
//    @Column(name = "manage_name")
//    private String manageName;
    /**
     * 关联码
     */
    @ApiModelProperty(required = true, value = "关联码", hidden = true, example = "")
    @Column(name = "client_no")
    private String clientNo;
    /**
     * 缓存表模板
     */
    @ApiModelProperty(required = true, value = "缓存表模板", hidden = true, example = "")
    @Column(name = "cache_templete")
    private String cacheTemplete;
    /**
     * 缓存库配置
     */
    @ApiModelProperty(required = true, value = "缓存库配置", hidden = true, example = "")
    @Column(name = "cache_db_set")
    private String cacheDbSet;

    @ApiModelProperty(required = true, value = "流程审批key", hidden = false, example = "")
    @Column(name = "work_flow_key")
    private String workFlowKey;
    /**
     * 是否启用 1启用
     */
    @ApiModelProperty(required = true, value = "是否启用 1启用", hidden = true, example = "")
    @Column(name = "enable")
    private Integer enable;
    /**
     * 排序
     */
    @ApiModelProperty(required = true, value = "排序", hidden = true, example = "")
    @Column(name = "norder")
    private Integer norder;
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
     * 认证类型
     */
    @ApiModelProperty(required = true, value = "认证类型", hidden = true, example = "")
    @Column(name = "auth_type")
    private String authType;
    /**
     * 密钥密码
     */
    @ApiModelProperty(required = true, value = "密钥密码", hidden = true, example = "")
    @Column(name = "keystore_password")
    private String keystorePassword;
    /**
     * 密钥文件名
     */
    @ApiModelProperty(required = true, value = "密钥文件名", hidden = true, example = "")
    @Column(name = "keystore_file_name")
    private String keystoreFileName;
    /**
     * 密钥文件
     */
    @ApiModelProperty(required = true, value = "密钥文件", hidden = true, example = "")
    @Column(name = "keystore_file")
    private byte[] keystoreFile;
    /**
     * krb5配置文件名
     */
    @ApiModelProperty(required = true, value = "krb5配置文件名", hidden = true, example = "")
    @Column(name = "krb5_config_file_name")
    private String krb5ConfigFileName;
    /**
     * krb5配置文件
     */
    @ApiModelProperty(required = true, value = "krb5配置文件", hidden = true, example = "")
    @Column(name = "krb5_config_file")
    private byte[] krb5ConfigFile;
    /**
     * kerberos 认证主体
     */
    @ApiModelProperty(required = true, value = "kerberos 认证主体", hidden = true, example = "")
    @Column(name = "principal")
    private String principal;
    /**
     * kerberos keytab 配置文件名
     */
    @ApiModelProperty(required = true, value = "kerberos keytab 配置文件名", hidden = true, example = "")
    @Column(name = "keytab_file_name")
    private String keytabFileName;
    /**
     * kerberos keytab 配置文件
     */
    @ApiModelProperty(required = true, value = "kerberos keytab 配置文件", hidden = true, example = "")
    @Column(name = "keytab_file")
    private byte[] keytabFile;
    /**
     * kerberos 服务名
     */
    @ApiModelProperty(required = true, value = "kerberos 服务名", hidden = true, example = "")
    @Column(name = "remote_service_name")
    private String remoteServiceName;
    /**
     * 是否对密码加密, 0不加密, 1 加密
     */
    @ApiModelProperty(required = true, value = "是否对密码加密, 0不加密, 1 加密", hidden = true, example = "")
    @Column(name = "encrypt_flag")
    private Integer encryptFlag;
}
