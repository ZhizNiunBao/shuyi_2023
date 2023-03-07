package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkDcServerDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.view.olk.OlkObjectWithFieldsVo;
import cn.bywin.business.bean.view.olk.VOlkObjectVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkObjectMapper extends Mapper<TOlkObjectDo>, MySqlMapper<TOlkObjectDo> {

    List<TOlkObjectDo> findBeanList(TOlkObjectDo bean);

    long findBeanCnt(TOlkObjectDo bean);


    List<VOlkObjectVo> findNodeBeanList(VOlkObjectVo bean);

    long findNodeBeanCnt(VOlkObjectVo bean);

    List<OlkObjectWithFieldsVo> findUserTable( OlkObjectWithFieldsVo bean );


    @Select(value = " select count(*) cnt from  t_olk_object where object_name= #{objectName} " +
            "and id != #{id} and schema_id = #{schemaId} ")
    long findSameNameCount(TOlkObjectDo bean);

//    @Select(value = " select * from  t_olk_object c,(select a.id as ds_db_id, db_name, b.id as ds_schema_id,schema_name from t_olk_database a, t_olk_schema b where a.id =b.db_id) ds where c.visible=1 and schema_id=#{schemaId} " +
//            " and ds.ds_schema_id =c.schema_id order by norder,object_name ")
//    List<VOlkObjectVo> findUserObjectList(TOlkObjectDo bean);

//    @Select(value = " select * from  t_olk_object c,(select a.id as ds_db_id, db_name, b.id as ds_schema_id,schema_name from t_olk_database a, t_olk_schema b where a.id =b.db_id) ds where  c.visible=1 and " +
//            " ds.ds_schema_id =c.schema_id and id=#{id} ")
//    VOlkObjectVo findViewObjectById(@Param("id") String id);

    @Update(value = " update t_olk_object set syn_flag =0, enable= (select enable from t_olk_database where  id=#{dbId}) where db_id=#{dbId} " +
            "and (enable is null or enable != (select enable from t_olk_database where  id=#{dbId}) ) ")
    long updateEnableByDbId(@Param("dbId") String dbId);

    @Update(value = " update t_olk_object set syn_flag =0,enable= (select enable from t_olk_schema where  id=#{schemaId}) where schema_id=#{schemaId}" +
            "and (enable is null or enable != (select enable from t_olk_schema where  id=#{schemaId})  )  ")
    long updateEnableBySchemaId(@Param("schemaId") String schemaId);

    @Delete(value = "delete from t_olk_object where db_id = #{dbId} ")
    long deleteByDatabaseId(@Param("dbId") String dbId);

    @Delete(value = "delete from t_olk_object where schema_id = #{schemaId} ")
    long deleteBySchemaId(@Param("schemaId") String schemaId);

    @Update(value = "update t_olk_object set syn_flag =0,enable=#{enable}  where id=#{id} and (enable is null or enable !=#{enable}) ")
    long updateEnable(TOlkObjectDo bean);

    @Select(value = " select * from  t_olk_object where object_name  like '%${name}%'  or obj_chn_name like '%${name}%' ")
    List<TOlkObjectDo> findLikeName(@Param("name") String name);

    @Select(value = " select * from  t_olk_object where obj_full_name =#{fullName}  ")
    TOlkObjectDo findByFullName(@Param("fullName") String fullName);

    @Select(value = "select d.* from t_olk_object o join t_olk_dc_server d on o.obj_full_name = #{fullName} and o.dc_id = d.id")
    TOlkDcServerDo findBelongDcCode(@Param("fullName") String fullName);
}