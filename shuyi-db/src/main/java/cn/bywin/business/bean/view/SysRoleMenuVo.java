package cn.bywin.business.bean.view;

import cn.bywin.business.bean.system.SysMenuDo;
import lombok.Data;

import java.util.List;

@Data
public class SysRoleMenuVo extends SysMenuDo {
    private String roleId;
    private List<String> menuIds;
}
