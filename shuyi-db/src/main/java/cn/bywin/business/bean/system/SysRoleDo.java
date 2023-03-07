package cn.bywin.business.bean.system;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table( name ="sys_role" )
@Data
public class SysRoleDo extends SidEntityDo {


    /**
     * 角色名
     */
    @ApiModelProperty("角色名")
    @Column(name = "role_name")
    private String roleName;
    /**
     * 角色描述
     */
    @ApiModelProperty("角色描述")
    @Column(name = "role_note")
    private String roleNote;
    /**
     * 排序
     */
    @ApiModelProperty("排序")
    @Column(name = "role_order")
    private Integer roleOrder;
    /**
     * 状态
     */
    @ApiModelProperty("状态")
    @Column(name = "is_status")
    private Integer isStatus;
    /**
     * 是否启用
     */
    @ApiModelProperty("是否启用")
    @Column(name = "is_open")
    private Integer isOpen;


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
