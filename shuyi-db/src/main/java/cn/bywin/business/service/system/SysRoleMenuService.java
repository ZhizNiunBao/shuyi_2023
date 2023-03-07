package cn.bywin.business.service.system;

import cn.bywin.business.bean.system.SysRoleDo;
import cn.bywin.business.bean.system.SysRoleMenuDo;
import cn.bywin.business.bean.system.SysUserRoleDo;
import cn.bywin.business.bean.view.UserVo;
import cn.bywin.business.mapper.system.SysRoleMapper;
import cn.bywin.business.mapper.system.SysRoleMenuMapper;
import cn.bywin.business.mapper.system.SysUserRoleMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class SysRoleMenuService extends BaseServiceImpl<SysRoleMenuDo, String> {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Override
    public Mapper<SysRoleMenuDo> getMapper() {
        return sysRoleMenuMapper;
    }

//    public List<SysRoleMenuDo> findBeanList(SysRoleMenuDo bean) {
//        return sysRoleMenuMapper.findBeanList(bean);
//    }
//
//    public long findBeanCnt(SysRoleMenuDo bean) {
//        return sysRoleMenuMapper.findBeanCnt(bean);
//    }


    @Transactional
    public Integer saveRoleMenus(List<SysRoleMenuDo> addList,List<SysRoleMenuDo> delList) {

        if( addList!=null){
            for ( SysRoleMenuDo sysRoleMenuDo : addList ) {
                sysRoleMenuMapper.insert( sysRoleMenuDo );
            }
        }
        if( delList!=null){
            for ( SysRoleMenuDo sysRoleMenuDo : delList ) {
                sysRoleMenuMapper.deleteByPrimaryKey( sysRoleMenuDo.getId() );
            }
        }
        return 1;
    }

    public List<UserVo> findAllByRole(SysUserRoleDo roleId) {
       return sysRoleMapper.findAllByRole(roleId);
    }
    public List<SysRoleMenuDo> findByRoleId(String roleId) {
        return sysRoleMenuMapper.findByRoleId(roleId);
    }


    public long findAllByRoleCnt(SysUserRoleDo info) {
        return sysRoleMapper.findAllByRoleCnt(info);
    }
}
