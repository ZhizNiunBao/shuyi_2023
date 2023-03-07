package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TBydbDatasetDo;
import cn.bywin.business.bean.view.bydb.BydbDatasetVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface BydbDatasetMapper extends Mapper<TBydbDatasetDo>, MySqlMapper<TBydbDatasetDo> {

    List<TBydbDatasetDo> findBeanList(TBydbDatasetDo bean);
    long findBeanCnt(TBydbDatasetDo bean);

    @Select(value = " select count(*) cnt from  t_bydb_dataset where set_chn_name= #{setChnName} " +
            "and id != #{id} and dc_id = #{dcId} " )
    long findSameNameCount(TBydbDatasetDo bean);

    @Select(value = " select count(*) cnt from  t_bydb_dataset where ( set_code= #{setCode} or dc_set_code = #{dcSetCode}) " +
            "and id != #{id} and dc_id = #{dcId} " )
    long findSameCodeCount(TBydbDatasetDo bean);

    @Select(value = " select a.*, b.id contentId, b.centre_sql,b.dc_sql, b.where_cond  from  t_bydb_dataset a left join t_bydb_ds_content b on a.id =b.ds_id where a.id=#{id} " )
    BydbDatasetVo findVoById(@Param("id") String id);

    @Update(value = " update t_bydb_dataset set enable =2 where id=#{id} " )
    long updateChgEnable(@Param("id") String id);

    @Select(value = " select * from  t_bydb_dataset where view_name =#{viewName}  ")
    List<TBydbDatasetDo> findByFullName(@Param("viewName") String viewName);

}