package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TTruUdfDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface TruUdfMapper extends Mapper<TTruUdfDo>, MySqlMapper<TTruUdfDo> {

    List<TTruUdfDo> findBeanList(TTruUdfDo bean);
    long findBeanCnt(TTruUdfDo bean);

    @Select(value = " select count(*) cnt from  t_tru_udf where ( pid is null and '${pid}' = '#NULL#'  or pid = '${pid}' ) and type_name= #{typeName} " +
            "and id != #{id} " )
    long findSameNameCount(TTruUdfDo bean);

    @Delete(value = " delete from t_tru_udf where dc_id= #{dcId} " )
    long deleteByDcId(@Param("dcId") String dcId);

}