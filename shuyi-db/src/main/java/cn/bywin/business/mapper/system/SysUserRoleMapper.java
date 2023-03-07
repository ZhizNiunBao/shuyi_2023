package cn.bywin.business.mapper.system;

import cn.bywin.business.bean.system.SysRoleDo;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.system.SysUserRoleDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface SysUserRoleMapper extends Mapper<SysUserRoleDo>, MySqlMapper<SysUserRoleDo> {


    List<SysUserRoleDo> findBeanList(SysUserDo bean);

    long findBeanCnt(SysUserRoleDo bean);

    @Select(value = "select *  from  sys_role sr ,sys_user_role sur  " +
            " where sr.id=sur.role_id and  sur.user_id=#{userId} limit 1 ")
    SysRoleDo getRole(@Param("userId") String userId);

    @Select(value = "select *  from  sys_user_role   where user_id=#{userId} ")
    SysUserRoleDo getSystemRole(@Param("userId") String userId);


    @Delete(value = "delete from sys_user_role where user_id =#{userId} ")
    long deleteByUserId(@Param("userId") String userId);

    @Update(value = "update sys_user_role set role_id='normal' where role_id =#{roleId} ")
    long updateToNormalRoleId(@Param("roleId") String roleId);

    @Select(value = " select * from sys_user where id in( select user_id from sys_user_role where role_id ='flsystem' ) ")
    List<SysUserDo> getSuperAdminUser();

    @Select(value = " select * from sys_user where id not in( select user_id from sys_user_role where role_id ='flsystem' ) order by mobile ")
    List<SysUserDo> getCommonUser();

}