package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FModelJobDo;
import cn.bywin.business.bean.view.federal.FModelJobVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface ModelJobMapper extends Mapper<FModelJobDo>, MySqlMapper<FModelJobDo> {

    List<FModelJobDo> findBeanList(FModelJobDo bean);

    long findBeanCnt(FModelJobDo bean);

    @Select("SELECT * FROM fl_model_job WHERE model_id = #{modelId}")
    List<FModelJobDo> selectByModelId(@Param("modelId") String modelId);


    List<FModelJobVo> findBeanAllList(FModelJobDo modelDo);

    long findBeanAllCnt(FModelJobDo modelDo);



    List<FModelJobVo> findBeanCollectList(FModelJobVo modelDo);

    long findBeanCollectCnt(FModelJobVo modelDo);
}

