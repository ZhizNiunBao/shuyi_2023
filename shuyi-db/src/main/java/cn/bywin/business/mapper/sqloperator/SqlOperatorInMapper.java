package cn.bywin.business.mapper.sqloperator;

import cn.bywin.business.bean.response.sqloperator.OlkModelOperatorElementRelVo;
import cn.bywin.business.bean.sqloperator.TSqlOperatorInDo;
import java.util.List;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface SqlOperatorInMapper extends Mapper<TSqlOperatorInDo> {
    @Select("select distinct in_code from t_sql_operator_in where operator_id = #{id}")
    List<OlkModelOperatorElementRelVo> getDistinctInCode(String id);
}
