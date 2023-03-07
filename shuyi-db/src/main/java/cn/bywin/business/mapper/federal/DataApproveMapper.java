package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.view.federal.FDataApproveVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface DataApproveMapper extends Mapper<FDataApproveDo>, MySqlMapper<FDataApproveDo> {


    List<FDataApproveVo> findBeanList(FDataApproveVo bean);
    List<FDataApproveVo> findBydbBeanList(FDataApproveVo bean);
    List<FDataApproveVo> findOlkBeanList(FDataApproveVo bean);

    long findBeanCnt(FDataApproveVo bean);

    @Select("SELECT * FROM fl_data_approve   where approve!=9 and project_id= #{projectId} and node_id=#{nodeId} and types=#{types}")
    List<FDataApproveDo> selectByProjectDataId(@Param("projectId") String projectId, @Param("nodeId") String nodeId, @Param("types") Integer types);

    List<FDataApproveDo> selectByProjectDataIds(@Param("projectId") String projectId, @Param("nodeId") String nodeId, @Param("ids") List<String> ids);

    @Select("SELECT * FROM fl_data_approve   where  approve!=9 and project_id= #{projectId} and node_id=#{nodeId} and data_id=#{dataId}")
    FDataApproveDo selectByProjectNodeDataId(@Param("projectId") String projectId, @Param("nodeId") String nodeId, @Param("dataId") String dataId);

    //    @Select("DELETE  FROM fl_data_approve WHERE  project_id = #{projectId}")
    @Update("Update fl_data_approve set approve=9 WHERE  project_id = #{projectId}")
    void deleteByProjectId(@Param("projectId") String projectId);

    //    @Select("DELETE  FROM fl_data_approve WHERE project_id = #{projectId} and data_id=#{dataId}")
    @Update("Update fl_data_approve set approve=9 WHERE project_id = #{projectId} and data_id=#{dataId}")
    void deleteByProjectDataId(@Param("projectId") String projectId, @Param("dataId") String dataId);

    @Select("select x.* from fl_data_approve x, (\n" +
            "  select  max(created_time) ct,creator_id ,data_id  from fl_data_approve where approve != 9 group by creator_id  ,data_id  ) y\n" +
            "  where x.data_id  =y.data_id and x.creator_id  =y.creator_id and x.created_time  = y.ct\n" +
            "  and x.creator_id  =#{userId} and x.data_id in( ${dataIds} ) and approve != 9 ")
    List<FDataApproveDo> selectApproveByUserDataId(@Param("userId") String userId, @Param("dataIds") String dataIds);

}