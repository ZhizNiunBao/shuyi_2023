package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TBydbObjectDo;
import cn.bywin.business.bean.view.VBydbObjectVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface BydbObjectMapper extends Mapper<TBydbObjectDo>, MySqlMapper<TBydbObjectDo> {

    List<TBydbObjectDo> findBeanList(TBydbObjectDo bean);

    long findBeanCnt(TBydbObjectDo bean);


    List<VBydbObjectVo> findNodeBeanList(VBydbObjectVo bean);

    long findNodeBeanCnt(VBydbObjectVo bean);


    @Select(value = " select count(*) cnt from  t_bydb_object where object_name= #{objectName} " +
            "and id != #{id} and schema_id = #{schemaId} ")
    long findSameNameCount(TBydbObjectDo bean);

//    @Select(value = " select * from  t_bydb_object c,(select a.id as ds_db_id, db_name, b.id as ds_schema_id,schema_name from t_bydb_database a, t_bydb_schema b where a.id =b.db_id) ds where c.visible=1 and schema_id=#{schemaId} " +
//            " and ds.ds_schema_id =c.schema_id order by norder,object_name ")
//    List<VBydbObjectVo> findUserObjectList(TBydbObjectDo bean);

//    @Select(value = " select * from  t_bydb_object c,(select a.id as ds_db_id, db_name, b.id as ds_schema_id,schema_name from t_bydb_database a, t_bydb_schema b where a.id =b.db_id) ds where  c.visible=1 and " +
//            " ds.ds_schema_id =c.schema_id and id=#{id} ")
//    VBydbObjectVo findViewObjectById(@Param("id") String id);

    @Update(value = " update t_bydb_object set syn_flag =0, enable= (select enable from t_bydb_database where  id=#{dbId}) where db_id=#{dbId} " +
            "and (enable is null or enable != (select enable from t_bydb_database where  id=#{dbId}) ) ")
    long updateEnableByDbId(@Param("dbId") String dbId);

    @Update(value = " update t_bydb_object set syn_flag =0,enable= (select enable from t_bydb_schema where  id=#{schemaId}) where schema_id=#{schemaId}" +
            "and (enable is null or enable != (select enable from t_bydb_schema where  id=#{schemaId})  )  ")
    long updateEnableBySchemaId(@Param("schemaId") String schemaId);

    @Delete(value = "delete from t_bydb_object where db_id = #{dbId} ")
    long deleteByDatabaseId(@Param("dbId") String dbId);

    @Delete(value = "delete from t_bydb_object where schema_id = #{schemaId} ")
    long deleteBySchemaId(@Param("schemaId") String schemaId);

    @Update(value = "update t_bydb_object set syn_flag =0,enable=#{enable}  where id=#{id} and (enable is null or enable !=#{enable}) ")
    long updateEnable(TBydbObjectDo bean);

    @Select(value = " select * from  t_bydb_object where object_name  like '%${name}%'  or obj_chn_name like '%${name}%' ")
    List<TBydbObjectDo> findLikeName(@Param("name") String name);

    @Select(value = " select * from  t_bydb_object where obj_full_name =#{fullName}  ")
    List<TBydbObjectDo> findByFullName(@Param("fullName") String fullName);
}