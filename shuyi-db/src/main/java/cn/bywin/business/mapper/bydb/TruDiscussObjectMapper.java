package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TTruDiscussObjectDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface TruDiscussObjectMapper extends Mapper<TTruDiscussObjectDo>, MySqlMapper<TTruDiscussObjectDo> {

    List<TTruDiscussObjectDo> findBeanList(TTruDiscussObjectDo bean);
    long findBeanCnt(TTruDiscussObjectDo bean);

    @Select(value = " select * from  t_tru_discuss_object where ds_id= #{datasetId} and end_time is null  order by start_time desc "  )
    List<TTruDiscussObjectDo> findUnfinished(@Param( "datasetId") String datasetId );

    @Delete(value = " delete from  t_tru_favourite_object where ds_id= #{datasetId} "  )
    long deleteByDatasetId(@Param( "datasetId") String datasetId );

}