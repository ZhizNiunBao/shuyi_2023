package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkSchemaDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkSchemaMapper extends Mapper<TOlkSchemaDo>, MySqlMapper<TOlkSchemaDo> {

    List<TOlkSchemaDo> findBeanList(TOlkSchemaDo bean);
    long findBeanCnt(TOlkSchemaDo bean);

    @Select(value = " select count(*) cnt from  t_olk_schema where schema_name= #{schemaName} " +
            "and id != #{id} and db_id = #{dbId} " )
    long findSameNameCount(TOlkSchemaDo bean);

    @Select(value = " select * from  t_olk_schema where visible=1 and db_id=#{dbId}  " +
            "order by norder,schema_name " )
    List<TOlkSchemaDo> findUserSchemaList(TOlkSchemaDo bean);

    @Update(value = " update t_olk_schema set syn_flag =0,enable= (select enable from t_olk_database where  id=#{dbId}) where db_id=#{dbId} " +
            "and (enable is null or enable != (select enable from t_olk_database where  id=#{dbId}) ) " )
    long updateEnableByDbId(@Param("dbId") String dbId);

    @Update(value = " update t_olk_schema set syn_flag =0,enable=#{enable}  where id=#{id}  and (enable is null or  enable!=#{enable} ) " )
    long updateEnable(TOlkSchemaDo bean);

    @Delete(value = "delete from t_olk_schema where db_id = #{dbId} " )
    long deleteByDatabaseId(@Param("dbId") String dbId);

}