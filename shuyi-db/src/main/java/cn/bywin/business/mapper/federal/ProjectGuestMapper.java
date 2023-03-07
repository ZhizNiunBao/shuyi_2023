package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FProjectGuestDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface ProjectGuestMapper extends Mapper<FProjectGuestDo>, MySqlMapper<FProjectGuestDo> {


    @Select("SELECT * FROM fl_project_guest WHERE project_id = #{projectId}")
    List<FProjectGuestDo> selectByProjectId(@Param("projectId") String projectId);

    List<FProjectGuestDo> findBeanList(FProjectGuestDo bean);

    long findBeanCnt(FProjectGuestDo bean);

    @Select("SELECT node_id FROM fl_project_guest WHERE project_id = #{projectId}")
    List<String> findByIds(@Param("projectId") String projectId);

    @Delete("DELETE  FROM fl_project_guest WHERE project_id=#{projectId} and node_id = #{nodeId}")
    void deleteByProjectDataId(@Param("projectId") String projectId, @Param("nodeId") String nodeId);


}