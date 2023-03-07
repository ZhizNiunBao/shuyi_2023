package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkFieldDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkFieldMapper extends Mapper<TOlkFieldDo>, MySqlMapper<TOlkFieldDo> {

    List<TOlkFieldDo> findBeanList(TOlkFieldDo bean);
    long findBeanCnt(TOlkFieldDo bean);

    @Delete(value = " delete from t_olk_field where db_id=#{dbId} " )
    long deleteByDatabaseId(@Param("dbId") String dbId);

    @Delete(value = " delete from t_olk_field where schema_id=#{schemaId} " )
    long deleteBySchemaId(@Param("schemaId") String schemaId);

    @Delete(value = " delete from t_olk_field where object_id=#{objectId} " )
    long deleteByObjectId(@Param("objectId") String objectId);

    @Update(value = " update t_olk_field set enable= (select enable from t_olk_database where  id=#{dbId}) where db_id=#{dbId} " +
            "and (enable is null or enable != (select enable from t_olk_database where  id=#{dbId}) ) " )
    long updateEnableByDbId(@Param("dbId") String dbId);

    @Update(value = " update t_olk_field set enable= (select enable from t_olk_schema where  id=#{schemaId}) where schema_id=#{schemaId} " +
            "and (enable is null or enable != (select enable from t_olk_schema where  id=#{schemaId}) )  " )
    long updateEnableBySchemeId(@Param("schemaId") String schemaId);

    @Update(value = " update t_olk_field set enable= (select enable from t_olk_object where  id=#{objectId}) where object_id=#{objectId}  " +
            "and (enable is null or enable != (select enable from t_olk_object where  id=#{objectId}) )  " )
    long updateEnableByObjectId(@Param("objectId") String objectId);

    @Update(value = " update t_olk_field set enable=#{enable}  where id=#{id} and ( enable is null or enable != #{enable} ) " )
    long updateEnable(TOlkFieldDo bean);

    @Select(value = " select * from  t_olk_field where object_id = #{objectId} order by norder " )
    List<TOlkFieldDo> selectByObjectId(@Param("objectId") String objectId);


}