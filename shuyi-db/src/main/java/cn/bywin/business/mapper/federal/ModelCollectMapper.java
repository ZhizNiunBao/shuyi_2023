package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FModelCollectDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface ModelCollectMapper extends Mapper<FModelCollectDo>, MySqlMapper<FModelCollectDo> {



    List<FModelCollectDo> findBeanList(FModelCollectDo bean);
    @Select("SELECT count(*) FROM fl_model_collect WHERE model_job_id = #{modelJobId} and user_id = #{userId} ")
    long findBeanCnt(@Param("userId") String userId,@Param("modelJobId") String modelJobId);

    @Select("DELETE  FROM fl_model_collect WHERE model_job_id = #{modelJobId}")
    void deleteByModelJobId(@Param("modelJobId") String modelJobId);

    @Select("DELETE  FROM fl_model_collect WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") String userId);


    @Select("DELETE  FROM fl_model_collect WHERE model_job_id = #{modelJobId} and user_id = #{userId}")
    void deleteById(@Param("userId") String userId,@Param("modelJobId") String modelJobId);
}