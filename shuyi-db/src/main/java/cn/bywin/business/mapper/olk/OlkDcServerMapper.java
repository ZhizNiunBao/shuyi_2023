package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkDcServerDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkDcServerMapper extends Mapper<TOlkDcServerDo>, MySqlMapper<TOlkDcServerDo> {

    List<TOlkDcServerDo> findBeanList(TOlkDcServerDo bean);
    long findBeanCnt(TOlkDcServerDo bean);

    List<TOlkDcServerDo> findBaseList(TOlkDcServerDo bean);

    @Select(value = " select id, dc_code, dc_name, dept_no, dept_name, dc_type, jdbc_url, connection_url, connection_user, connection_pwd, manage_account, manage_name, client_no,  enable, work_flow_key, norder, auth_type from  t_olk_dc_server where id = #{id} " )
    TOlkDcServerDo findSimpleBean(@Param("id") String id);

    @Select(value = " select count(*) cnt from  t_olk_dc_server where dc_code= #{dcCode} " +
            "and id != #{id} " )
    long findSameCodeCount(TOlkDcServerDo bean);

    @Select(value = " select count(*) cnt from  t_olk_dc_server where dc_name= #{dcName} " +
            "and id != #{id} " )
    long findSameNameCount(TOlkDcServerDo bean);

    @Select(value = " select count(*) cnt from  t_olk_dc_server where dept_no= #{deptNo} " +
            "and id != #{id} " )
    long findSameDeptCount(TOlkDcServerDo bean);

    @Select(value = " select count(*) cnt from  t_olk_dc_server where manage_account= #{manageAccount} " +
            "and id != #{id} " )
    long findSameManageUserCount(TOlkDcServerDo bean);

    @Select(value = " select count(*) cnt from  t_olk_dc_server where connection_url= #{connectionUrl} " +
            "and id != #{id} " )
    long findSameUrlCount(TOlkDcServerDo bean);

}