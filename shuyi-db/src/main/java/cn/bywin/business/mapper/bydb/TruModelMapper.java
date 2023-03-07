package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TTruModelDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

@Repository
public interface TruModelMapper extends Mapper<TTruModelDo>, MySqlMapper<TTruModelDo> {


    List<TTruModelDo> findBeanList(TTruModelDo bean);

    long findBeanCnt(TTruModelDo bean);

    @Select("SELECT * FROM t_tru_model WHERE id = #{id}")
    TTruModelDo findById(@Param("id") String id);

    @Select("SELECT * FROM t_tru_model WHERE name = #{name}")
    List<TTruModelDo> findByName(@Param("name") String name);

    @Select("SELECT * FROM t_tru_model WHERE creator_account = #{creatorAccount} and status!=9 ")
    List<TTruModelDo> findByUser(@Param("creatorAccount") String creatorAccount);

   // @Select("SELECT cache_flag cacheflag,count(*) cnt FROM t_tru_model WHERE creator_account = #{creatorAccount} and status!=9 group by cache_flag ")
    List<Map<String,Object>> statsByUser( TTruModelDo bean);

}