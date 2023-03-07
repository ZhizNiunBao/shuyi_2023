package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkModelElementRelMapper extends Mapper<TOlkModelElementRelDo>, MySqlMapper<TOlkModelElementRelDo> {

    @Select("SELECT * FROM t_olk_model_element_rel WHERE model_id = #{modelId}")
    List<TOlkModelElementRelDo> selectByModelId(@Param("modelId") String modelId);

    @Select("SELECT * FROM t_olk_model_element_rel WHERE start_element_id=#{startId} AND end_element_id=#{endId}")
    List<TOlkModelElementRelDo> selectByExist(@Param("startId") String startId,@Param("endId") String endId);

    @Select("SELECT * FROM t_olk_model_element_rel WHERE id = #{id}")
    TOlkModelElementRelDo selectById(@Param("id") String id);

   @Select("SELECT * FROM t_olk_model_element_rel WHERE start_element_id=#{vertexId} OR end_element_id=#{vertexId}")
   List<TOlkModelElementRelDo> selectByVertexId(@Param("vertexId") String vertexId);
}
