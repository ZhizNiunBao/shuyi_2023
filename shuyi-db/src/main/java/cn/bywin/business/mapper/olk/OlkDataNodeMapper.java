package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkDataNodeDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkDataNodeMapper extends Mapper<TOlkDataNodeDo>, MySqlMapper<TOlkDataNodeDo> {

    List<TOlkDataNodeDo> findBeanList(TOlkDataNodeDo bean);
    long findBeanCnt(TOlkDataNodeDo bean);

    @Select(value = " select * from  t_olk_data_node where (  data_id = #{dataId} ) " )
    List<TOlkDataNodeDo> findByDataId(@Param("dataId") String dataId );

    @Delete(value = " delete from  t_olk_data_node where (  data_id = #{dataId} )  " )
    long delByDataId(@Param("dataId") String dataId);

    @Delete(value = " delete from  t_olk_data_node where (  data_id in ( select id from t_olk_object where schema_id = #{schemaId} ) )  " )
    long delBySchemaId(@Param("schemaId") String schemaId);

    @Delete(value = " delete from  t_olk_data_node where (  data_id in ( select id from t_olk_object where db_id = #{dbId} ) )  " )
    long delByDbId(@Param("dbId") String dbId);

}