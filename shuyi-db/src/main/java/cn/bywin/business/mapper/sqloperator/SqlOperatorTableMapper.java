package cn.bywin.business.mapper.sqloperator;

import cn.bywin.business.bean.sqloperator.TSqlOperatorTableDo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface SqlOperatorTableMapper extends Mapper<TSqlOperatorTableDo> {
    @Select("select sche_full_name from t_olk_schema where id = #{id}")
    String getDatasourceFullName(String id);
}
