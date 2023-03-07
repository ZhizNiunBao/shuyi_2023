package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FModelDataDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface ModelDataMapper extends Mapper<FModelDataDo>, MySqlMapper<FModelDataDo> {
    @Select("SELECT model_id FROM fl_model_data WHERE data_id = #{dataId} ")
    List<String>  selectByDataId(@Param("dataId") String dataId);

    @Select("SELECT * FROM fl_model_data WHERE model_id = #{modelId} ")
    List<FModelDataDo> selectByModelId(@Param("modelId") String modelId);

    @Select("SELECT * FROM fl_model_data WHERE model_id = #{modelId} and node_id=#{nodeId}  and types= #{types}")
    List<FModelDataDo> selectByModelNoDeId(@Param("modelId") String modelId,@Param("nodeId") String nodeId,@Param("types") Integer types);

    @Select("SELECT * FROM fl_project_data pd join fl_data_party dp on pd.data_id=dp.id and dp.node_id=#{nodeId} and pd.project_id = #{projectId}" +
            " WHERE  dp.status!=-1 ")
    List<FModelDataDo> selectByProjectAndNodeId(@Param("projectId") String projectId, @Param("nodeId") String nodeId);

    List<FModelDataDo> findBeanList(FModelDataDo bean);

    long findBeanCnt(FModelDataDo bean);

    @Select("SELECT * FROM fl_model_data WHERE model_id = #{modelId} and data_id = #{dataId}  ")
    FModelDataDo findModelAndData(@Param("modelId") String modelId, @Param("dataId") String dataId);

    @Delete("DELETE  FROM fl_model_data WHERE model_id = #{modelId}")
    void deleteByModelId(@Param("modelId") String modelId);

    @Delete("DELETE  FROM fl_model_data WHERE Data_id = #{DataId}")
    void deleteByDataId(@Param("DataId") String DataId);

    @Select("SELECT * FROM fl_model_data   WHERE creator_id = #{userId} ")
    List<FModelDataDo> selectByModelDataIds(@Param("userId") String userId);
}