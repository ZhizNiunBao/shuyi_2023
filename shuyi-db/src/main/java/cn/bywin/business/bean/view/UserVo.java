package cn.bywin.business.bean.view;

import cn.bywin.business.bean.system.SysUserDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
public class UserVo extends SysUserDo {

    private String code;
    private String oldPassword;
    private String role;
    private String roleName;
    /**
     * 是否在线
     */
    private Integer isStatus;
    private Integer status;
    private String icon;
    private Integer isOpen;
}
