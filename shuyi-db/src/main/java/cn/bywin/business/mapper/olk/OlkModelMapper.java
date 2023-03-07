package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkModelDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

@Repository
public interface OlkModelMapper extends Mapper<TOlkModelDo>, MySqlMapper<TOlkModelDo> {


    List<TOlkModelDo> findBeanList(TOlkModelDo bean);

    long findBeanCnt(TOlkModelDo bean);

    @Select("SELECT * FROM t_olk_model WHERE id = #{id}")
    TOlkModelDo findById(@Param("id") String id);

    @Select("SELECT * FROM t_olk_model WHERE name = #{name}")
    List<TOlkModelDo> findByName(@Param("name") String name);

    @Select("SELECT * FROM t_olk_model WHERE creator_account = #{creatorAccount} and status!=9 ")
    List<TOlkModelDo> findByUser(@Param("creatorAccount") String creatorAccount);

   // @Select("SELECT cache_flag cacheflag,count(*) cnt FROM t_tru_model WHERE creator_account = #{creatorAccount} and status!=9 group by cache_flag ")
    List<Map<String,Object>> statsByUser( TOlkModelDo bean);

}