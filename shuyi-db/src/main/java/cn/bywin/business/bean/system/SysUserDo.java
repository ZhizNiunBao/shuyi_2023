package cn.bywin.business.bean.system;

import cn.bywin.business.common.base.SidEntityDo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table( name ="sys_user" )
@Data
public class SysUserDo extends SidEntityDo {

    /**
     * 用户
     */
    @ApiModelProperty("用户")
    @Column(name = "username")
    private String username;
    /**
     * 昵称
     */
    @ApiModelProperty("密码")
    @Column(name = "password")
    private String password;
    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    @Column(name = "chnname")
    private String chnname;

    /**
     * 电话
     */
    @ApiModelProperty("电话")
    @Column(name = "mobile")
    private String mobile;
    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    @Column(name = "email")
    private String email;
    /**
     * 是否启用
     */
    @ApiModelProperty("是否启用")
    @Column(name = "is_lock")
    private Integer isLock;

    /**
     * 节点id
     */
    @ApiModelProperty(required = false, value = "节点id", hidden = false, example = "")
    @Column(name = "node_party_id")
    private String nodePartyId;

    /**
     * 创建人ID
     */
    @ApiModelProperty("创建人ID")
    @Column(name = "creator_id")
    private String creatorId;

    /**
     * 创建人帐号
     */
    @ApiModelProperty("创建人帐号")
    @Column(name = "creator_account")
    private String creatorAccount;

    /**
     * 创建人姓名
     */
    @ApiModelProperty("创建人姓名")
    @Column(name = "creator_name")
    private String creatorName;


    /**
     * 注册时间
     */
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "reg_time" )
    @ApiModelProperty(value = "注册时间",hidden = true)
    protected Timestamp regTime;

}
