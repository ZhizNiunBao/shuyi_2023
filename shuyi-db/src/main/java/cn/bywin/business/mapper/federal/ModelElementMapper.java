package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.bean.view.federal.FModelElementVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface ModelElementMapper extends Mapper<FModelElementDo>, MySqlMapper<FModelElementDo> {
    @Select("SELECT * FROM fl_model_element WHERE model_id = #{modelId}")
    List<FModelElementDo> selectByModelId(@Param("modelId") String modelId);


    @Select("SELECT e.*,g.types as types,g.component_en as  componentType " +
            " FROM fl_model_element e  JOIN fl_component g on e.component_id = g.id and e.model_id = #{modelId}")
    List<FModelElementVo> selectByModelIdDeTail(@Param("modelId") String modelId);

    @Select("SELECT * FROM fl_model_element WHERE id = #{id}")
    FModelElementDo selectById(@Param("id") String id);
    @Select("SELECT e.*,g.types as types,g.component_en as  componentType " +
            "FROM fl_model_element e JOIN fl_component g " +
            "ON e.model_id = #{modelId} AND e.component_id = g.id")
    List<FModelElementVo> selectByModelIdWithDetail(@Param("modelId") String modelId);
    @Select("SELECT e.* FROM fl_model_element_rel rel join  fl_model_element  e on e.id=rel.start_element_id and end_element_id = #{vertexId}")
    List<FModelElementDo> selectStartId(String vertexId);

    @Select("SELECT g.* FROM fl_model_element e JOIN fl_component g " +
            " ON   e.component_id = g.id AND  e.model_id = #{modelId} AND types=4")
    FComponentDo selectComponentByModelId(@Param("modelId")String modelId);
}

