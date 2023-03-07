package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.TBydbDatabaseDo;
import cn.bywin.business.bean.bydb.TBydbFieldDo;
import cn.bywin.business.bean.bydb.TBydbObjectDo;
import cn.bywin.business.bean.bydb.TBydbSchemaDo;
import cn.bywin.business.mapper.bydb.BydbDatabaseMapper;
import cn.bywin.business.mapper.bydb.BydbFieldMapper;
import cn.bywin.business.mapper.bydb.BydbObjectMapper;
import cn.bywin.business.mapper.bydb.BydbSchemaMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class BydbFieldService extends BaseServiceImpl<TBydbFieldDo, String> {

	@Autowired
	private BydbFieldMapper commMapper;

	@Autowired
	private BydbDatabaseMapper databaseMapper;

	@Autowired
	private BydbSchemaMapper schemaMapper;

	@Autowired
	private BydbObjectMapper objectMapper;

//	@Autowired
//	private BydbGroupObjectMapper groupObjectMapper;

//	@Autowired
//	private BydbItemObjectMapper itemObjectMapper;

	@Override
	public Mapper<TBydbFieldDo> getMapper() {
		return commMapper;
	}

	public List<TBydbFieldDo> findBeanList(TBydbFieldDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TBydbFieldDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public List<TBydbFieldDo> selectByObjectId( String objectId ){
		return  commMapper.selectByObjectId( objectId );
	}

	@Transactional(rollbackFor=Exception.class)
	public long updateBeanWithFlag(List<TBydbFieldDo> updateList,List<TBydbDatabaseDo> dbList ,
			List<TBydbSchemaDo> schemaList ,List<TBydbObjectDo> objectList ){
		if( dbList != null){
			for (TBydbDatabaseDo obj : dbList) {
				databaseMapper.updateEnable( obj );
			}
		}

		if( schemaList != null){
			for (TBydbSchemaDo obj : schemaList) {
				schemaMapper.updateEnable( obj );
			}
		}

		if( objectList != null){
			for (TBydbObjectDo obj : objectList) {
				objectMapper.updateByPrimaryKeySelective( obj );
			}
		}

		if( updateList != null){
			for (TBydbFieldDo obj : updateList) {
				commMapper.updateEnable( obj );
			}
			return updateList.size();
		}
		return 0;
	}

	public long saveOneObject(List<TBydbFieldDo> addList,List<TBydbFieldDo> modList, List<TBydbFieldDo> delList){
		if( delList != null ){
			for (TBydbFieldDo fieldDo : delList) {
				commMapper.deleteByPrimaryKey(fieldDo.getId());
			}
		}
		if( modList != null ){
			for (TBydbFieldDo fieldDo : modList) {
				commMapper.updateByPrimaryKey( fieldDo );
			}
		}
		if( addList != null ){
			for (TBydbFieldDo fieldDo : addList) {
				commMapper.insert( fieldDo );
			}
		}
		return 1;
	}

	@Transactional(rollbackFor=Exception.class)
	public long delWithUpdate(List<TBydbFieldDo> delList,List<TBydbObjectDo> objList  ){
		if( delList != null){
			for (TBydbFieldDo fieldDo : delList) {
				commMapper.deleteByPrimaryKey( fieldDo.getId() );
			}
		}

		if( objList != null){
			for (TBydbObjectDo obj : objList) {
				objectMapper.updateByPrimaryKeySelective( obj );
			}
		}
		return 1;
	}

}
