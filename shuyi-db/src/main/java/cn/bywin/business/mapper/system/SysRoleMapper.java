package cn.bywin.business.mapper.system;

import cn.bywin.business.bean.system.SysMenuDo;
import cn.bywin.business.bean.system.SysRoleDo;
import cn.bywin.business.bean.system.SysUserRoleDo;
import cn.bywin.business.bean.view.UserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface SysRoleMapper extends Mapper<SysRoleDo>,MySqlMapper<SysRoleDo> {


        List<SysRoleDo> findBeanList(SysRoleDo bean);

        long findBeanCnt(SysRoleDo bean);

        List<UserVo> findAllByRole(SysUserRoleDo roleId);

        long findAllByRoleCnt(SysUserRoleDo roleId);

        @Select( "SELECT MAX(role_order) AS role_order FROM sys_role WHERE role_order IS NOT NULL" )
        Integer findMaxOrder();
}