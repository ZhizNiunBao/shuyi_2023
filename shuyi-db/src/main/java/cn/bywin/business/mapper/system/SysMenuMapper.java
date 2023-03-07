package cn.bywin.business.mapper.system;

import cn.bywin.business.bean.system.SysMenuDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface SysMenuMapper extends Mapper<SysMenuDo>, MySqlMapper<SysMenuDo> {


    List<SysMenuDo> findBeanList(SysMenuDo bean);

    long findBeanCnt(SysMenuDo bean);


    @Select(value = "select * from sys_menu sm where sm.pid in (select id  from  sys_menu where menu_code=#{menuCode}) and sm.show_flag=1         \n" +
            "        and id in(         \n" +
            "        select menu_id  from sys_role_menu srm , sys_user_role sur where srm.role_id =sur.role_id and sur.user_id=#{userId} \n" +
            "        UNION \n" +
            "        select id from sys_menu where EXISTS ( select user_id  from sys_user_role sur where role_id ='flsystem' and user_id=#{userId} )\n" +
            "        )        \n" +
            "        order by sm.menu_order  ")
    List<SysMenuDo> usersubmenu(@Param("menuCode") String menuCode, @Param("userId") String userId);

    @Select(value = "select * from sys_menu sm where sm.level =1 and sm.show_flag=1         \n" +
            "        and id in(         \n" +
            "        select menu_id  from sys_role_menu srm , sys_user_role sur where srm.role_id =sur.role_id and sur.user_id=#{userId} \n" +
            "        UNION \n" +
            "        select id from sys_menu where EXISTS ( select user_id  from sys_user_role sur where role_id ='flsystem' and user_id=#{userId} )\n" +
            "        )        \n" +
            "        order by sm.menu_order  ")
    List<SysMenuDo> usertopmenu(@Param("userId") String userId);

    @Select(value = "select * from sys_menu sm where sm.level =1 and sm.show_flag=1 and menu_type='menu'         \n" +
            "        and id in(         \n" +
            "        select menu_id  from sys_role_menu srm , sys_user_role sur where srm.role_id =sur.role_id and sur.user_id=#{userId} \n" +
            "        UNION \n" +
            "        select id from sys_menu where EXISTS ( select user_id  from sys_user_role sur where role_id ='flsystem' and user_id=#{userId} )\n" +
            "        )        \n" +
            "        order by pid, sm.menu_order  ")
    List<SysMenuDo> userAllMenu(@Param("userId") String userId);

    @Select(value = "select * from sys_menu  where menu_code=#{menuCode}")
    SysMenuDo getMenuByCode(@Param("menuCode") String menuCode);

    @Select(value = "select * from sys_menu  where pid=#{pid}")
    SysMenuDo getMenuPid(@Param("pid")String pid);


    List<SysMenuDo> userSubMenuId(@Param("pid") String pid, @Param("userId") String userId,@Param("menuType")String menuType);

    List<SysMenuDo> findBeanListAll(SysMenuDo info);
}