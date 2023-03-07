package cn.bywin.business.bean.system;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table( name ="sys_user_role" )
@Data
public class SysUserRoleDo extends SidEntityDo {

    /**
     * role_id
     */
    @ApiModelProperty("角色id")
    @Column(name = "role_id")
    private String roleId;
    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @Column(name = "user_id")
    private String userId;


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
}
