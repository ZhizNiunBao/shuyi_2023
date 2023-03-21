package cn.bywin.business.mapper.sqloperator;

import cn.bywin.business.bean.sqloperator.TSqlOperatorDo;
import java.util.List;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface SqlOperatorMapper extends Mapper<TSqlOperatorDo> {

    @Select("select id from t_sql_operator where name = #{name} ")
    List<TSqlOperatorDo> selectByName(String name);

    @Select({"<script>",
        "select * from t_sql_operator where creator_id = #{userId}",
        " <when test='name!=null'> ",
        " AND name LIKE CONCAT('%',#{name},'%') ",
        " </when> ",

        " <when test='type!=null'> ",
        " AND operator_type = #{type} ",
        " </when> ",

        " </script> "})
    List<TSqlOperatorDo> queryPage(String name, String type, String userId);
}