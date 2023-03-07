package cn.bywin.business.service.federal;

import cn.bywin.business.bean.bydb.TBydbDatabaseDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.view.bydb.BydbDatabaseSourceVo;
import cn.bywin.business.mapper.bydb.BydbDatabaseMapper;
import cn.bywin.business.mapper.federal.DataSourceMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class DataSourceService extends BaseServiceImpl<FDatasourceDo, String> {

    @Autowired
    private DataSourceMapper dataSourceMapper;

	@Autowired
	private BydbDatabaseMapper databaseMapper;
    @Override
    public Mapper<FDatasourceDo> getMapper() {
        return dataSourceMapper;
    }


    public List<FDatasourceDo> findBeanList(FDatasourceDo modelInfo){
        return dataSourceMapper.findBeanList(modelInfo);
    }

    public long findBeanCnt(FDatasourceDo bean){
        return dataSourceMapper.findBeanCnt(bean);
    }
	public long findSameNameCount(FDatasourceDo bean){
		return dataSourceMapper.findSameNameCount( bean );
	}

	public FDatasourceDo findByDatabaseId(String databaseId){
		return dataSourceMapper.findByDatabaseId( databaseId );
	}

	public List<FDatasourceDo> findUnUseDbSource(FDatasourceDo bean){
		return dataSourceMapper.findUnUseDbSource( bean );
	}

	public List<BydbDatabaseSourceVo> findBeanWithUsedFlag( FDatasourceDo bean){
		return dataSourceMapper.findBeanWithUsedFlag( bean );
	}

	@Transactional(rollbackFor=Exception.class)
	public long insertWithDatabase(FDatasourceDo dbSourceDo, TBydbDatabaseDo databaseDo){

		dataSourceMapper.insert( dbSourceDo );
		if( databaseDo != null ) {
			databaseMapper.insert(databaseDo);
		}
		return 1;
	}

	@Transactional(rollbackFor=Exception.class)
	public long updateWithDatabase(FDatasourceDo dbSourceDo, TBydbDatabaseDo databaseDo){

		dataSourceMapper.updateByPrimaryKey( dbSourceDo );
		if( databaseDo != null ) {
			databaseMapper.updateByPrimaryKey(databaseDo);
		}
		return 1;
	}

	@Transactional(rollbackFor=Exception.class)
	public long deleteList(List<FDatasourceDo> delList){

		if( delList != null){
			for (FDatasourceDo adminDo : delList) {
				dataSourceMapper.deleteByPrimaryKey( adminDo.getId() );
			}
		}
		return 1;
	}


}
