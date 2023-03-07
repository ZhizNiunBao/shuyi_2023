package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TBydbCatalogTypeDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface BydbCatalogTypeMapper extends Mapper<TBydbCatalogTypeDo>, MySqlMapper<TBydbCatalogTypeDo> {

    List<TBydbCatalogTypeDo> findBeanList(TBydbCatalogTypeDo bean);
    long findBeanCnt(TBydbCatalogTypeDo bean);

    @Select(value = " select count(*) cnt from  t_bydb_catalog_type where ( pid is null and '${pid}' = '#NULL#'  or pid = '${pid}' ) and type_name= #{typeName} " +
            "and id != #{id} " )
    long findSameNameCount(TBydbCatalogTypeDo bean);

    @Delete(value = " delete from t_bydb_catalog_type where dc_id= #{dcId} " )
    long deleteByDcId(@Param("dcId") String dcId);

}