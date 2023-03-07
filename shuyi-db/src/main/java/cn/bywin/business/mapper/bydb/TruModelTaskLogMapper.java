package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TTruModelTaskLogDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface TruModelTaskLogMapper extends Mapper<TTruModelTaskLogDo>, MySqlMapper<TTruModelTaskLogDo> {

    List<TTruModelTaskLogDo> findBeanList(TTruModelTaskLogDo bean);
    long findBeanCnt(TTruModelTaskLogDo bean);

    @Select(value = " select * from  t_tru_model_task_log where model_id= #{modelId} and end_time is null  order by start_time desc "  )
    List<TTruModelTaskLogDo> findUnfinished(@Param( "modelId") String modelId );

    @Delete(value = " delete from  t_tru_model_task_log where model_id= #{modelId} "  )
    long deleteByModelId(@Param( "modelId") String modelId );

}