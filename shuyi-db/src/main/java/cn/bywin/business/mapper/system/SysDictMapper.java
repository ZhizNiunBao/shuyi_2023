package cn.bywin.business.mapper.system;

import cn.bywin.business.bean.system.SysDictDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface SysDictMapper extends Mapper<SysDictDo>,MySqlMapper<SysDictDo> {


        List<SysDictDo> findBeanList(SysDictDo bean);

        long findBeanCnt(SysDictDo bean);

        @Select(value = "select count(*)  from  sys_dict where dict_code=#{dictCode} and id != #{id} and ('' = '${pid}' and pid is null or pid = #{pid}) ")
        int checkCode(SysDictDo bean);
        @Select(value = "select count(*)  from  sys_dict where dict_name=#{dictName} and id != #{id} and ('' = '${pid}' and pid is null or pid = #{pid}) ")
        int checkName(SysDictDo bean);
//        @Select(value = "select * from  sys_dict where pid is null or pid ='0' order by dict_order ")
//        List<SysDictDo> findTopMenu(@Param("appCode") String appCode);
//        @Select(value = "select * from  sys_menu where pid  in (select id from sys_menu where menu_code= #{memuCode}  and (menu_type='menu' or menu_type='link') ) order by menu_order ")
//        List<SysDictDo> findUserSubMenu(@Param("appCode") String appCode, @Param("memuCode") String memuCode, @Param("userId") String userId);
//        @Select(value = "select * from  sys_menu order by menu_order ")
//        List<SysDictDo> findUserAllMenu(@Param("appCode") String appCode, @Param("userId") String userId);
        @Select(value = "select * from sys_dict where pid =(\n" +
                "select id from sys_dict where dict_code =#{topCode} and pid is null ) order by dict_order ,dict_code  ")
        List<SysDictDo> findLevel1DictByTopCode(@Param("topCode") String topCode);

        @Select(value = "select * from sys_dict where pid =(\n" +
                "select id from sys_dict where dict_code =#{topCode} and pid is null ) and display='1' order by dict_order ,dict_code  ")
        List<SysDictDo> findVisibleLevel1DictByTopCode(@Param("topCode") String topCode);

        @Select(value = "select * from sys_dict where  pid is null  order by dict_order ,dict_code  ")
        List<SysDictDo> findAllType();

        @Select(value = "select * from sys_dict where id in ( ${ids} ) order by pid, dict_order ,dict_code  ")
        List<SysDictDo> findDictByIds(@Param("ids") String ids);

        @Select(value = "select * from sys_dict where pid =#{pid} order by dict_order ,dict_code  ")
        List<SysDictDo> findSubDict(@Param("pid") String pid);

        @Select(value = "select count(*) cnt from sys_dict where pid in ( ${pids} ) ")
        long findSubDictCnt(@Param("pids") String pids );

        @Delete(value = "delete from sys_dict where id in ( ${ids} ) ")
        long deleteByIds(@Param("ids") String ids );

        @Update(value = "update sys_dict set top_code =#{dictCode} where top_id = #{id}  ")
        long updateDictTopCode(SysDictDo topDict );


}