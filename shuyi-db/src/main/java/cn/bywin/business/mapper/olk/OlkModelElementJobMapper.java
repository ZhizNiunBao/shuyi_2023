package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkModelElementJobDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkModelElementJobMapper extends Mapper<TOlkModelElementJobDo>, MySqlMapper<OlkModelElementJobMapper> {
    @Select("SELECT * FROM t_olk_model_element_job WHERE model_id = #{modelId} ")
    List<TOlkModelElementJobDo> selectByModelId( @Param("modelId") String modelId);

    @Delete("delete from t_olk_model_element_job WHERE model_id = #{modelId} ")
    long deleteByModelId(@Param("modelId") String modelId);

    @Select("SELECT * FROM t_olk_model_element_job WHERE id = #{id}")
    TOlkModelElementJobDo selectById( @Param("id") String id);

    List<TOlkModelElementJobDo> findBeanList( TOlkModelElementJobDo bean);

    long findBeanCnt( TOlkModelElementJobDo bean);
}

