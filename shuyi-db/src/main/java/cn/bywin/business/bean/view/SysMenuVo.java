package cn.bywin.business.bean.view;

import cn.bywin.business.bean.system.SysMenuDo;
import lombok.Data;

import java.util.List;

@Data
public class SysMenuVo  extends SysMenuDo {
    private Integer status;
    private Integer hasNext;
    private String menuUrl;
    private List<SysMenuVo> children;
}
