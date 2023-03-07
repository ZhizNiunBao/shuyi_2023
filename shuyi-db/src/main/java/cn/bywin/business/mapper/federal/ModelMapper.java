package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FModelDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface ModelMapper extends Mapper<FModelDo>, MySqlMapper<FModelDo> {

    List<FModelDo> findBeanList(FModelDo bean);

    long findBeanCnt(FModelDo bean);


    @Select("SELECT * FROM fl_model where project_id = #{projectId} order by created_time desc")
    List<FModelDo> selectByProjectId(@Param("projectId") String projectId);

    List<FModelDo> selectByProjectDataId(@Param("projectId") String projectId,@Param("dataId") String dataId
            ,@Param("ids") List<String>  ids,
                                         @Param("types") Integer types);

}

