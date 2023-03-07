package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FProjectDataDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface ProjectDataMapper extends Mapper<FProjectDataDo>, MySqlMapper<FProjectDataDo> {


    @Select("SELECT * FROM fl_project_data   where project_id= #{projectId} and node_id=#{nodeId}")
    List<FProjectDataDo> selectByProjectDataId(@Param("projectId") String projectId, @Param("nodeId") String nodeId);

    @Select("SELECT * FROM fl_project_data WHERE project_id = #{projectId}")
    List<FProjectDataDo> selectByProjectId(@Param("projectId") String projectId);

    @Select("SELECT * FROM fl_project_data pd join fl_data_party dp on pd.data_id=dp.id and dp.node_id=#{nodeId} and pd.project_id = #{projectId}")
    List<FProjectDataDo> selectByProjectAndNodeId(@Param("projectId") String projectId, @Param("nodeId") String nodeId);

    List<FProjectDataDo> findBeanList(FProjectDataDo bean);

    long findBeanCnt(FProjectDataDo bean);

    @Select("SELECT * FROM fl_project_data   where project_id= #{projectId} and node_id=#{nodeId} and data_id=#{dataId}")
    FProjectDataDo selectByProjectNodeDataId(@Param("projectId") String projectId,  @Param("nodeId")String nodeId, @Param("dataId") String dataId);
}