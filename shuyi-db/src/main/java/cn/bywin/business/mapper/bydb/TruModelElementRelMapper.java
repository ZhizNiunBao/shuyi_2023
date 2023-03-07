package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TTruModelElementRelDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface TruModelElementRelMapper extends Mapper<TTruModelElementRelDo>, MySqlMapper<TTruModelElementRelDo> {

    @Select("SELECT * FROM t_tru_model_element_rel WHERE model_id = #{modelId}")
    List<TTruModelElementRelDo> selectByModelId(@Param("modelId") String modelId);

    @Select("SELECT * FROM t_tru_model_element_rel WHERE start_element_id=#{startId} AND end_element_id=#{endId}")
    List<TTruModelElementRelDo> selectByExist(@Param("startId") String startId,@Param("endId") String endId);

    @Select("SELECT * FROM t_tru_model_element_rel WHERE id = #{id}")
    TTruModelElementRelDo selectById(@Param("id") String id);

   @Select("SELECT * FROM t_tru_model_element_rel WHERE start_element_id=#{vertexId} OR end_element_id=#{vertexId}")
   List<TTruModelElementRelDo> selectByVertexId(@Param("vertexId") String vertexId);
}
