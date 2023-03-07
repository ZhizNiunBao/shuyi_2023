package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TTruModelElementJobDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface TruModelElementJobMapper extends Mapper<TTruModelElementJobDo>, MySqlMapper<TruModelElementJobMapper> {
    @Select("SELECT * FROM t_tru_model_element_job WHERE model_id = #{modelId} ")
    List<TTruModelElementJobDo> selectByModelId( @Param("modelId") String modelId);

    @Delete("delete from t_tru_model_element_job WHERE model_id = #{modelId} ")
    long deleteByModelId(@Param("modelId") String modelId);

    @Select("SELECT * FROM t_tru_model_element_job WHERE id = #{id}")
    TTruModelElementJobDo selectById( @Param("id") String id);

    List<TTruModelElementJobDo> findBeanList( TTruModelElementJobDo bean);

    long findBeanCnt( TTruModelElementJobDo bean);
}

