package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkModelTaskLogDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkModelTaskLogMapper extends Mapper<TOlkModelTaskLogDo>, MySqlMapper<TOlkModelTaskLogDo> {

    List<TOlkModelTaskLogDo> findBeanList(TOlkModelTaskLogDo bean);
    long findBeanCnt(TOlkModelTaskLogDo bean);

    @Select(value = " select * from  t_tru_model_task_log where model_id= #{modelId} and end_time is null  order by start_time desc "  )
    List<TOlkModelTaskLogDo> findUnfinished(@Param( "modelId") String modelId );

    @Delete(value = " delete from  t_tru_model_task_log where model_id= #{modelId} "  )
    long deleteByModelId(@Param( "modelId") String modelId );

}