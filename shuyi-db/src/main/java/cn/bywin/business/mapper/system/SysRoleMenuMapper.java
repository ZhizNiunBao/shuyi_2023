package cn.bywin.business.mapper.system;

import cn.bywin.business.bean.system.SysMenuDo;
import cn.bywin.business.bean.system.SysRoleDo;
import cn.bywin.business.bean.system.SysRoleMenuDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface SysRoleMenuMapper extends Mapper<SysRoleMenuDo>,MySqlMapper<SysRoleMenuDo> {


        List<SysRoleMenuDo> findBeanList(SysMenuDo bean);

        long findBeanCnt(SysRoleDo bean);

        @Select(value = "select count(*)  from  sys_role_menu where role_id=#{roleId} and menu_id=#{menuId}")
        long checkRoleId(String roleId, String menuId);

        @Select(value = "select *  from  sys_role_menu where role_id=#{roleId}")
        List<SysRoleMenuDo> findByRoleId(String roleId);

        @Delete(value = "delete from sys_role_menu where role_id =#{roleId} ")
        long deleteByRoleId(@Param("roleId") String roleId);
}