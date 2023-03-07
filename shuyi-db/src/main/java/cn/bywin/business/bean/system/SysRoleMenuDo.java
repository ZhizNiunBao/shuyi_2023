package cn.bywin.business.bean.system;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table( name ="sys_role_menu" )
@Data
public class SysRoleMenuDo extends SidEntityDo {

    /**
     * role_id
     */
    @ApiModelProperty("角色id")
    @Column(name = "role_id")
    private String roleId;
    /**
     * 菜单id
     */
    @ApiModelProperty("菜单id")
    @Column(name = "menu_id")
    private String menuId;


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
