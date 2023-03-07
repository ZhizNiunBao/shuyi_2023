package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TBydbDataNodeDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface BydbDataNodeMapper extends Mapper<TBydbDataNodeDo>, MySqlMapper<TBydbDataNodeDo> {

    List<TBydbDataNodeDo> findBeanList(TBydbDataNodeDo bean);
    long findBeanCnt(TBydbDataNodeDo bean);

    @Select(value = " select * from  t_bydb_data_node where (  data_id = #{dataId} ) " )
    List<TBydbDataNodeDo> findByDataId(@Param("dataId") String dataId );

    @Delete(value = " delete from  t_bydb_data_node where (  data_id = #{dataId} )  " )
    long delByDataId(@Param("dataId") String dataId);

    @Delete(value = " delete from  t_bydb_data_node where (  data_id in ( select id from t_bydb_object where schema_id = #{schemaId} ) )  " )
    long delBySchemaId(@Param("schemaId") String schemaId);

    @Delete(value = " delete from  t_bydb_data_node where (  data_id in ( select id from t_bydb_object where db_id = #{dbId} ) )  " )
    long delByDbId(@Param("dbId") String dbId);

}