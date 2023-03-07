package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TBydbSchemaDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface BydbSchemaMapper extends Mapper<TBydbSchemaDo>, MySqlMapper<TBydbSchemaDo> {

    List<TBydbSchemaDo> findBeanList(TBydbSchemaDo bean);
    long findBeanCnt(TBydbSchemaDo bean);

    @Select(value = " select count(*) cnt from  t_bydb_schema where schema_name= #{schemaName} " +
            "and id != #{id} and db_id = #{dbId} " )
    long findSameNameCount(TBydbSchemaDo bean);

    @Select(value = " select * from  t_bydb_schema where visible=1 and db_id=#{dbId}  " +
            "order by norder,schema_name " )
    List<TBydbSchemaDo> findUserSchemaList(TBydbSchemaDo bean);

    @Update(value = " update t_bydb_schema set syn_flag =0,enable= (select enable from t_bydb_database where  id=#{dbId}) where db_id=#{dbId} " +
            "and (enable is null or enable != (select enable from t_bydb_database where  id=#{dbId}) ) " )
    long updateEnableByDbId(@Param("dbId") String dbId);

    @Update(value = " update t_bydb_schema set syn_flag =0,enable=#{enable}  where id=#{id}  and (enable is null or  enable!=#{enable} ) " )
    long updateEnable(TBydbSchemaDo bean);

    @Delete(value = "delete from t_bydb_schema where db_id = #{dbId} " )
    long deleteByDatabaseId(@Param("dbId") String dbId);

}