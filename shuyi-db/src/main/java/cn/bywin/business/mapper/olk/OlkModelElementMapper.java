package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkModelElementDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkModelElementMapper extends Mapper<TOlkModelElementDo>, MySqlMapper<TOlkModelElementDo> {
    @Select("SELECT * FROM t_olk_model_element WHERE model_id = #{modelId}")
    List<TOlkModelElementDo> selectByModelId(@Param("modelId") String modelId);

    @Select("SELECT * FROM t_olk_model_element WHERE id = #{id}")
    TOlkModelElementDo selectById(@Param("id") String id);
    @Select("SELECT e.*,g.types as types " +
            "FROM t_olk_model_element e JOIN t_olk_model_component g " +
            "ON e.model_id = #{modelId} AND e.tc_id = g.id")
    List<TOlkModelElementDo> selectByModelIdWithDetail(@Param("modelId") String modelId);
    @Select("SELECT e.* FROM t_olk_model_element e join  t_olk_model_element_rel  rel on e.id=rel.start_element_id and end_element_id = #{vertexId}")
    List<TOlkModelElementDo> selectStartId(String vertexId);
    @Select("SELECT e.* FROM t_olk_model_element e join  t_olk_model_element_rel  rel on e.id=rel.end_element_id and start_element_id = #{vertexId}")
    List<TOlkModelElementDo> selectEndId(String vertexId);
    @Select("SELECT count(*) FROM t_olk_model_element WHERE model_id = #{modelId}")
    long countByModelId(@Param("modelId") String modelId);
    @Select("SELECT count(*) FROM t_olk_model_element WHERE model_id = #{modelId} and name like  '${name}%' ")
    long countNameByModelId(@Param("modelId") String modelId,@Param("name") String name);
}

