package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.view.bydb.BydbDatabaseSourceVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface DataSourceMapper extends Mapper<FDatasourceDo>, MySqlMapper<FDatasourceDo> {


    List<FDatasourceDo> findBeanList(FDatasourceDo bean);

    long findBeanCnt(FDatasourceDo bean);
	
    @Select(value = " select count(*) cnt from  fl_datasource where ds_name= #{dsName} and id != #{id} and creator_account = #{creatorAccount}" )
    long findSameNameCount(FDatasourceDo bean);

    @Select(value = " select * from  fl_datasource where id not in (select dbsource_id from t_bydb_database WHERE dbsource_id IS NOT NULL  ) and enable in( 1,2 )  and creator_account = #{creatorAccount} " )
    List<FDatasourceDo> findUnUseDbSource(FDatasourceDo bean);

    @Select(value = " select a.*,b.dbsource_id from  fl_datasource a left join ( select DISTINCT dbsource_id from t_bydb_database  )  b on a.id = b.dbsource_id  where creator_account = #{creatorAccount}" )
    List<BydbDatabaseSourceVo> findBeanWithUsedFlag( FDatasourceDo bean);

    @Select(value = "select a.* from  fl_datasource a, t_bydb_database b where a.id = b.dbsource_id and b.id=#{databaseId}" )
    FDatasourceDo findByDatabaseId( @Param ( "databaseId" ) String databaseId);

}

