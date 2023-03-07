package cn.bywin.business.mapper.system;

import cn.bywin.business.bean.system.SysParamSetDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface SysParamSetMapper extends Mapper<SysParamSetDo>,MySqlMapper<SysParamSetDo> {


        List<SysParamSetDo> findBeanList(SysParamSetDo bean);

        long findBeanCnt(SysParamSetDo bean);

        @Select(value = "select count(*)  from  sys_param_set where para_code=#{paraCode} and id != #{id} ")
        int checkCode( SysParamSetDo bean );
        @Select(value = "select count(*)  from  sys_param_set where para_name=#{paraName} and id != #{id} ")
        int checkName( SysParamSetDo bean );
        @Select(value = "select * from  sys_param_set where para_code=#{code}")
        SysParamSetDo findByCode(@Param("code") String code);

        @Select(value = "select * from sys_param_set where id in ( ${ids} ) ")
        List<SysParamSetDo> findByIds(@Param("ids") String ids );

        @Delete(value = "delete from sys_param_set where id in ( ${ids} ) ")
        long deleteByIds(@Param("ids") String ids );
}