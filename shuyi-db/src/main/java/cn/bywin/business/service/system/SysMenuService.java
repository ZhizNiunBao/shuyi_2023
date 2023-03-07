package cn.bywin.business.service.system;

import cn.bywin.business.bean.system.SysMenuDo;
import cn.bywin.business.bean.system.SysRoleMenuDo;
import cn.bywin.business.bean.view.SysMenuVo;
import cn.bywin.business.bean.view.SysRoleMenuVo;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.mapper.system.SysMenuMapper;
import cn.bywin.business.mapper.system.SysRoleMenuMapper;
import cn.service.impl.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.stream.Collectors;


@Service("SysMenuService")
public class SysMenuService extends BaseServiceImpl<SysMenuDo, String> {

    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public Mapper<SysMenuDo> getMapper() {
        return sysMenuMapper;
    }

    public List<SysMenuDo> findBeanList(SysMenuDo bean) {
        return sysMenuMapper.findBeanList(bean);
    }

    public long findBeanCnt(SysMenuDo bean) {
        return sysMenuMapper.findBeanCnt(bean);
    }

    public List<SysMenuDo> usertopmenu(String userId) {

        return sysMenuMapper.usertopmenu(userId);
    }
    public List<SysMenuDo> findBeanListAll(SysMenuDo info) {
        return sysMenuMapper.findBeanListAll(info);
    }
    public List<SysMenuDo> userAllMenu(String userId) {

        return sysMenuMapper.userAllMenu(userId);
    }

    public List<SysMenuDo> usersubmenu(String code, String userId) {
        return sysMenuMapper.usersubmenu(code, userId);
    }

    public List<SysMenuDo> userSubMenuId(String pid, String userId, String menuType) {
        return sysMenuMapper.userSubMenuId(pid, userId, menuType);
    }

    public List<SysMenuVo> menuTree(List<SysMenuVo> data) {

        List<SysMenuVo> result = data.stream()
                .filter(meun -> StringUtils.isBlank( meun.getPid() ) )
                .map(menu -> {
                    List<SysMenuVo> children = getChildren(menu, data);
                    menu.setMenuUrl( menu.getMenuName() );
                    menu.setChildren(children.size()>0?children:null);
                    return menu;
                })
                // 根据排序字段排序
                .sorted((menu1, menu2) -> {
                    return (menu1.getMenuOrder() == null ? 0 : menu1.getMenuOrder()) - (menu2.getMenuOrder() == null ?
                            0 : menu2.getMenuOrder());
                })
                .collect(Collectors.toList());

        return result;
    }


    public SysMenuDo getMenuByCode(String code) {
        return sysMenuMapper.getMenuByCode(code);
    }

    public SysMenuDo getMenuPid(String pid) {
        return sysMenuMapper.getMenuPid(pid);
    }

    private List<SysMenuVo> getChildren(SysMenuVo root, List<SysMenuVo> all) {
        List<SysMenuVo> children = all.stream()
                .filter(menu -> String.valueOf(menu.getPid()).equals(root.getId()))
                .map((menu) -> {
                    List<SysMenuVo> schildren = getChildren(menu, all);
                    menu.setChildren(schildren.size()>0?schildren:null);
                    return menu;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getMenuOrder() == null ? 0 : menu1.getMenuOrder()) - (menu2.getMenuOrder() == null ? 0 :
                            menu2.getMenuOrder());
                })
                .collect(Collectors.toList());
        return children;
    }

    @Transactional
    public void updateNoNullData(SysRoleMenuVo info, UserDo userDo) {

        List<SysRoleMenuDo> oldAll = sysRoleMenuMapper.findByRoleId(info.getRoleId());
        if (oldAll != null && oldAll.size() > 0) {
            oldAll.stream().forEach(e -> {
                if (!info.getMenuIds().contains(e.getMenuId())) {
                    sysRoleMenuMapper.deleteByPrimaryKey(e);
                }
            });
        }
        if (info.getMenuIds() != null && info.getMenuIds().size() > 0) {
            info.getMenuIds().stream().forEach(e -> {
                long cnt = sysRoleMenuMapper.checkRoleId(info.getRoleId(), e);
                if (cnt == 0) {
                    SysRoleMenuDo sysRoleMenuDo = new SysRoleMenuDo();

                    sysRoleMenuDo.setId(ComUtil.genId());
                    sysRoleMenuDo.setRoleId(info.getRoleId());
                    sysRoleMenuDo.setMenuId(e);
                    sysRoleMenuMapper.insert(sysRoleMenuDo);
                }
            });
        }
    }


}
