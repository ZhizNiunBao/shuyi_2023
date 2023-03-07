package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkUdfDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkUdfMapper extends Mapper<TOlkUdfDo>, MySqlMapper<TOlkUdfDo> {

    List<TOlkUdfDo> findBeanList(TOlkUdfDo bean);
    long findBeanCnt(TOlkUdfDo bean);

    @Select(value = " select count(*) cnt from  t_olk_udf where ( pid is null and '${pid}' = '#NULL#'  or pid = '${pid}' ) and type_name= #{typeName} " +
            "and id != #{id} " )
    long findSameNameCount(TOlkUdfDo bean);

    @Delete(value = " delete from t_olk_udf where dc_id= #{dcId} " )
    long deleteByDcId(@Param("dcId") String dcId);

}