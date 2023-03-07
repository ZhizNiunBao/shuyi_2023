package cn.bywin.business.service.system;

import cn.bywin.business.bean.system.SysRoleDo;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.system.SysUserRoleDo;
import cn.bywin.business.bean.view.UserVo;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.mapper.system.SysRoleMapper;
import cn.bywin.business.mapper.system.SysRoleMenuMapper;
import cn.bywin.business.mapper.system.SysUserMapper;
import cn.bywin.business.mapper.system.SysUserRoleMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

import static cn.bywin.business.common.util.Constants.FLSYSTEM;


@Service("SysRoleService")
public class SysRoleService extends BaseServiceImpl<SysRoleDo, String> {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public Mapper<SysRoleDo> getMapper() {
        return sysRoleMapper;
    }

    public List<SysRoleDo> findBeanList(SysRoleDo bean) {
        return sysRoleMapper.findBeanList(bean);
    }

    public long findBeanCnt(SysRoleDo bean) {
        return sysRoleMapper.findBeanCnt(bean);
    }

    public Integer findMaxOrder() {
        return sysRoleMapper.findMaxOrder();
    }

    @Transactional
    public Integer deleteRole(List<SysRoleDo> roleList) {
        for ( SysRoleDo sysRoleDo : roleList ) {
            sysRoleMenuMapper.deleteByRoleId(sysRoleDo.getId());
        }
        for ( SysRoleDo sysRoleDo : roleList ) {
            sysUserRoleMapper.updateToNormalRoleId(sysRoleDo.getId());
        }
        for ( SysRoleDo sysRoleDo : roleList ) {
            sysRoleMapper.deleteByPrimaryKey(sysRoleDo.getId());
        }
        return roleList.size();
    }

    public List<UserVo> findAllByRole(SysUserRoleDo roleId) {
       return sysRoleMapper.findAllByRole(roleId);
    }


    public long findAllByRoleCnt(SysUserRoleDo info) {
        return sysRoleMapper.findAllByRoleCnt(info);
    }
}
