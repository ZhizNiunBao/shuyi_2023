package cn.bywin.business.mapper.system;

import cn.bywin.business.bean.system.SysRoleDo;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.view.UserVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

import static cn.bywin.business.common.util.Constants.FLSYSTEM;

@Repository
public interface SysUserMapper extends Mapper<SysUserDo>, MySqlMapper<SysUserDo> {


    List<SysUserDo> findBeanList(SysUserDo bean);

    long findBeanCnt(SysUserDo bean);


    @Select(value = "select count(*)  from  sys_user where username=#{username}  ")
    int checkName(SysUserDo bean);

    @Select(value = "insert into sys_user_role where username=#{username}  ")
    int initRole(SysUserDo bean);

    @Delete(value = "delete from sys_user where id in ( ${ids} ) ")
    long deleteByIds(@Param("ids") String ids);

    @Select(value = "select * from  sys_role where id!='" + FLSYSTEM + "' order by role_order ")
    List<SysRoleDo> findRoleList();

    //    @Select(value = "select su.*,sr.role_name as roleName  from  sys_role sr ,sys_user_role sur,sys_user su  " +
//            " where sr.id=sur.role_id and  sur.user_id=su.id   order by sr.role_order")
    List<UserVo> findAllByRole(SysUserDo sysUserDo);

    long findAllByRoleCnt(SysUserDo sysUserDo);

    @Select(value = "select su.*  from  sys_user su, sys_user_role sur  where su.id=sur.user_id and sur.role_id=#{roleId} ")
    SysUserDo getSystemUser(@Param("roleId") String roleId);
}