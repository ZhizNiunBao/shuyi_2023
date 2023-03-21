package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkCatalogTypeDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkCatalogTypeMapper extends Mapper<TOlkCatalogTypeDo>, MySqlMapper<TOlkCatalogTypeDo> {

    List<TOlkCatalogTypeDo> findBeanList(TOlkCatalogTypeDo bean);
    long findBeanCnt(TOlkCatalogTypeDo bean);

    @Select(value = " select count(*) cnt from  t_olk_catalog_type where ( pid is null and '${pid}' = ''  or pid = '${pid}' ) and type_name= #{typeName} " +
            "and id != #{id} " )
    long findSameNameCount(TOlkCatalogTypeDo bean);

    @Delete(value = " delete from t_olk_catalog_type where dc_id= #{dcId} " )
    long deleteByDcId(@Param("dcId") String dcId);

}