package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FProjectDataDo;
import cn.bywin.business.bean.federal.FProjectDo;
import cn.bywin.business.bean.view.federal.FProjectVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface ProjectMapper extends Mapper<FProjectDo>, MySqlMapper<FProjectDo> {

    List<FProjectDo> findBeanList(FProjectVo bean);

    long findBeanCnt(FProjectVo bean);


    List<FProjectDo> findByMemberBeanList(FProjectVo bean);

    long findByMemberBeanCnt(FProjectVo bean);

    @Select("SELECT * FROM fl_project p JOIN fl_model m  ON p.id = m.project_id  " +
            "JOIN fl_model_job j  ON  m.id = j.model_id and j.id=#{jobId}")
    List<FProjectDataDo> selectByModelJobId(@Param("jobId") String jobId);




}

