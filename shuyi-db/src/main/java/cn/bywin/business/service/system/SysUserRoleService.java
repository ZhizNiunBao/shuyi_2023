package cn.bywin.business.service.system;

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
public class SysUserRoleService extends BaseServiceImpl<SysUserRoleDo, String> {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public Mapper<SysUserRoleDo> getMapper() {
        return sysUserRoleMapper;
    }

//    public List<SysRoleMenuDo> findBeanList(SysRoleMenuDo bean) {
//        return sysRoleMenuMapper.findBeanList(bean);
//    }
//
//    public long findBeanCnt(SysRoleMenuDo bean) {
//        return sysRoleMenuMapper.findBeanCnt(bean);
//    }


    @Transactional
    public Integer saveUserRoles(List<SysUserRoleDo> addList,List<SysUserRoleDo> modList) {

        if( addList!=null){
            for ( SysUserRoleDo sysUserRoleDo : addList ) {
                sysUserRoleMapper.insert( sysUserRoleDo );
            }
        }
        if( modList!=null){
            for ( SysUserRoleDo sysRoleMenuDo : modList ) {
                sysUserRoleMapper.updateByPrimaryKey( sysRoleMenuDo );
            }
        }
        return 1;
    }

}
