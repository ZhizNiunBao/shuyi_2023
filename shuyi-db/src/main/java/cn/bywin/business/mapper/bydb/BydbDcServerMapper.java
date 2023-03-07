package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TBydbDcServerDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface BydbDcServerMapper extends Mapper<TBydbDcServerDo>, MySqlMapper<TBydbDcServerDo> {

    List<TBydbDcServerDo> findBeanList(TBydbDcServerDo bean);
    long findBeanCnt(TBydbDcServerDo bean);

    List<TBydbDcServerDo> findBaseList(TBydbDcServerDo bean);

    @Select(value = " select id, dc_code, dc_name, dept_no, dept_name, dc_type, jdbc_url, connection_url, connection_user, connection_pwd, manage_account, manage_name, client_no,  enable, work_flow_key, norder, auth_type from  t_bydb_dc_server where id = #{id} " )
    TBydbDcServerDo findSimpleBean(@Param("id") String id);

    @Select(value = " select count(*) cnt from  t_bydb_dc_server where dc_code= #{dcCode} " +
            "and id != #{id} " )
    long findSameCodeCount(TBydbDcServerDo bean);

    @Select(value = " select count(*) cnt from  t_bydb_dc_server where dc_name= #{dcName} " +
            "and id != #{id} " )
    long findSameNameCount(TBydbDcServerDo bean);

    @Select(value = " select count(*) cnt from  t_bydb_dc_server where dept_no= #{deptNo} " +
            "and id != #{id} " )
    long findSameDeptCount(TBydbDcServerDo bean);

    @Select(value = " select count(*) cnt from  t_bydb_dc_server where manage_account= #{manageAccount} " +
            "and id != #{id} " )
    long findSameManageUserCount(TBydbDcServerDo bean);

    @Select(value = " select count(*) cnt from  t_bydb_dc_server where connection_url= #{connectionUrl} " +
            "and id != #{id} " )
    long findSameUrlCount(TBydbDcServerDo bean);

}