package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TBydbDatabaseDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

@Repository
public interface BydbDatabaseMapper extends Mapper<TBydbDatabaseDo>, MySqlMapper<TBydbDatabaseDo> {

    List<TBydbDatabaseDo> findBeanList( TBydbDatabaseDo bean );

    long findBeanCnt( TBydbDatabaseDo bean );

    /**
     * @description: 根据sql获取sql 数据
     */
    @Select(value = " ${sql} ")
    List<Map<String, Object>> selectData( @Param("sql") String sql );

    @Select(value = " select count(*) cnt from  t_bydb_database where db_name= #{dbName} " +
            " and id != #{id} ")
    long findSameNameCount( TBydbDatabaseDo bean );

    @Select(value = " select * from  t_bydb_database where enable=1 and dc_id=#{dcId} " +
            " order by db_type,db_name ")
    List<TBydbDatabaseDo> findUserDbList( TBydbDatabaseDo bean );

    @Update(value = " update t_bydb_database set syn_flag = 0, enable=#{enable}  where id=#{id} and (enable is null or enable !=#{enable})  ")
    long updateEnable( TBydbDatabaseDo bean );

    @Select(value = " select max(norder) norder from  t_bydb_database" )
    Integer findMaxOrder();
//    @Select(value = " select max(norder) norder from  t_bydb_database ")
//    Integer findMaxOrder();

}