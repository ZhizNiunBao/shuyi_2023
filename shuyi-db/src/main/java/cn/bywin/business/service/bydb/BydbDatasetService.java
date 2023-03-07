package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.TBydbDatasetDo;
import cn.bywin.business.mapper.bydb.BydbDatasetMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;


@Service
public class BydbDatasetService extends BaseServiceImpl<TBydbDatasetDo, String> {

	@Autowired
	private BydbDatasetMapper commMapper;

//	@Autowired
//	private BydbDsContentMapper dsContentMapper;
//
//	@Autowired
//	private BydbDsColumnMapper columnMapper;
//
//	@Autowired
//	private BydbDsEntityMapper entityMapper;
//
//	@Autowired
//	private BydbDsEntityContentMapper entityContentMapper;
//
//	@Autowired
//	private BydbDsRelMapper relMapper;
//
//	@Autowired
//	private BydbDsRelOnMapper relOnMapper;
//
//	@Autowired
//	private BydbDsTaskLogMapper logMapper;
//
	@Override
	public Mapper<TBydbDatasetDo> getMapper() {
		return commMapper;
	}
//
//	public List<TBydbDatasetDo> findBeanList(TBydbDatasetDo bean){
//		return  commMapper.findBeanList(bean);
//	}
//
//	public long findBeanCnt(TBydbDatasetDo bean){
//		return  commMapper.findBeanCnt(bean);
//	}
//
//	public long findSameNameCount(TBydbDatasetDo bean){
//		return commMapper.findSameNameCount( bean );
//	}
//
//	public long findSameCodeCount(TBydbDatasetDo bean){
//		return commMapper.findSameCodeCount( bean );
//	}
//
//	public BydbDatasetVo findVoById(String id ){
//		return commMapper.findVoById( id );
//	}
//
//	public List<TBydbDatasetDo> findByFullName(String viewName){
//		return  commMapper.findByFullName(viewName);
//	}
//
//	@Transactional(rollbackFor=Exception.class)
//	public long saveWithColumn(TBydbDatasetDo bean, TBydbDsContentDo contentDo, List<TBydbDsColumnDo> addColList){
//
//		commMapper.insert( bean );
//		dsContentMapper.insert( contentDo );
//
//		if( addColList != null){
//			for (TBydbDsColumnDo columnDo : addColList) {
//				columnMapper.insert( columnDo );
//			}
//		}
//		return 1;
//	}
//
//	@Transactional(rollbackFor=Exception.class)
//	public long updateWithColumn(TBydbDatasetDo bean,TBydbDsContentDo contentDo, List<TBydbDsColumnDo> addColList, List<TBydbDsColumnDo> modColList, List<TBydbDsColumnDo> delColList){
//		commMapper.updateByPrimaryKey( bean );
//		if( contentDo != null)
//			dsContentMapper.updateByPrimaryKey( contentDo );
//		if( delColList != null){
//			for (TBydbDsColumnDo columnDo : delColList) {
//				columnMapper.deleteByPrimaryKey( columnDo );
//			}
//		}
//		if( modColList != null){
//			for (TBydbDsColumnDo columnDo : modColList) {
//				columnMapper.updateByPrimaryKey( columnDo );
//			}
//		}
//		if( addColList != null){
//			for (TBydbDsColumnDo columnDo : addColList) {
//				columnMapper.insert( columnDo );
//			}
//		}
//		return 0;
//	}
//
//	@Transactional(rollbackFor=Exception.class)
//	public long deleteWhithRel(List<TBydbDatasetDo> delList){
//		if( delList != null){
//			for (TBydbDatasetDo datasetDo : delList) {
//				columnMapper.deleteByDatasetId( datasetDo.getId() );
//				relOnMapper.deleteByDatasetId( datasetDo.getId() );
//				relMapper.deleteByDatasetId( datasetDo.getId() );
//				entityContentMapper.deleteByDatasetId( datasetDo.getId() );
//				entityMapper.deleteByDatasetId( datasetDo.getId() );
//				dsContentMapper.deleteByDatasetId( datasetDo.getId() );
//				commMapper.deleteByPrimaryKey( datasetDo );
//			}
//			return delList.size();
//		}
//		return 0;
//	}
//
//	@Transactional(rollbackFor=Exception.class)
//	public long updateBeanWithLog(TBydbDatasetDo bean, TBydbDsTaskLogDo addLog, List<TBydbDsTaskLogDo> updList){
//		commMapper.updateByPrimaryKeySelective( bean );
//		if( addLog != null)
//			logMapper.insert( addLog );
//		if( updList != null){
//			for (TBydbDsTaskLogDo logDo : updList) {
//				logMapper.updateByPrimaryKey( logDo );
//			}
//		}
//		return 1;
//	}

}
