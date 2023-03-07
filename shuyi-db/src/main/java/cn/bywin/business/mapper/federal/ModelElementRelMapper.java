package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FModelElementRelDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface ModelElementRelMapper extends Mapper<FModelElementRelDo>, MySqlMapper<FModelElementRelDo> {

    @Select("SELECT * FROM fl_model_element_rel WHERE model_id = #{modelId}")
    List<FModelElementRelDo> selectByModelId(@Param("modelId") String modelId);

    @Select("SELECT * FROM fl_model_element_rel WHERE start_element_id=#{vertexId} OR end_element_id=#{vertexId}")
    List<FModelElementRelDo> selectByVertexId(@Param("vertexId") String vertexId);
}
