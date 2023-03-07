package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.TBydbDatabaseDo;
import cn.bywin.business.bean.bydb.TBydbFieldDo;
import cn.bywin.business.bean.bydb.TBydbObjectDo;
import cn.bywin.business.bean.bydb.TBydbSchemaDo;
import cn.bywin.business.mapper.bydb.BydbDataNodeMapper;
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
public class BydbSchemaService extends BaseServiceImpl<TBydbSchemaDo, String> {

	@Autowired
	private BydbSchemaMapper commMapper;

	@Autowired
	private BydbDatabaseMapper databaseMapper;

	@Autowired
	private BydbObjectMapper objMapper;

	@Autowired
	private BydbFieldMapper fieldMapper;

	@Autowired
	private BydbDataNodeMapper dataNodeMapper;

	@Override
	public Mapper<TBydbSchemaDo> getMapper() {
		return commMapper;
	}

	public List<TBydbSchemaDo> findBeanList(TBydbSchemaDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TBydbSchemaDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public long findSameNameCount(TBydbSchemaDo bean){
		return commMapper.findSameNameCount( bean );
	}

	public List<TBydbSchemaDo> findUserSchemaList(TBydbSchemaDo bean){
		return  commMapper.findUserSchemaList(bean);
	}


//	public long deleteFolder(String id){
//		return commMapper.deleteFolder(id);
//	}

	@Transactional(rollbackFor=Exception.class)
	public long updateWithObject(List<TBydbSchemaDo> addSchemaList, List<TBydbObjectDo> addObjectList, List<TBydbSchemaDo> delSchemaList
			, List<TBydbObjectDo> delObjectList, List<TBydbFieldDo> addFieldList, List<TBydbFieldDo> modFieldList, List<TBydbFieldDo> delFieldList){

		if( delFieldList != null){
			for (TBydbFieldDo fieldDo : delFieldList) {
				fieldMapper.deleteByPrimaryKey( fieldDo.getId() );
			}
		}

		if( modFieldList != null){
			for (TBydbFieldDo fieldDo : modFieldList) {
				fieldMapper.updateByPrimaryKey( fieldDo );
			}
		}

		if( delObjectList != null){
			for (TBydbObjectDo obj : delObjectList) {
				objMapper.deleteByPrimaryKey( obj.getId() );
			}
		}
		if( delSchemaList != null){
			for (TBydbSchemaDo schema : delSchemaList) {
				commMapper.deleteByPrimaryKey( schema.getId() );
			}
		}
		if( addSchemaList != null){
			for (TBydbSchemaDo schema : addSchemaList) {
				commMapper.insert( schema );
			}
		}

		if( addObjectList != null){
			for (TBydbObjectDo obj : addObjectList) {
				objMapper.insert( obj );
			}
		}

		if( addFieldList != null){
			for (TBydbFieldDo fieldDo : addFieldList) {
				fieldMapper.insert( fieldDo );
			}
		}
		return 1;
	}

	@Transactional(rollbackFor=Exception.class)
	public long updateBeanWithFlag(List<TBydbSchemaDo> updateList,List<TBydbDatabaseDo> dbList ){
		if( dbList != null){
			for (TBydbDatabaseDo obj : dbList) {
				databaseMapper.updateEnable( obj );
			}
		}
		if( updateList != null){
			for (TBydbSchemaDo obj : updateList) {
				commMapper.updateByPrimaryKey( obj );
				//if( obj.getEnable() == 0 ) {
					objMapper.updateEnableBySchemaId(obj.getId());
					fieldMapper.updateEnableBySchemeId(obj.getId());
				//}
			}
			return updateList.size();
		}
		return 0;
	}

	@Transactional(rollbackFor=Exception.class)
	public long deleteWhithRel(List<TBydbSchemaDo> delList){
		if( delList != null){
			for (TBydbSchemaDo schemaDo : delList) {
				dataNodeMapper.delBySchemaId( schemaDo.getId() );
				fieldMapper.deleteBySchemaId( schemaDo.getId() );
				//objMapper.deleteBySchemaId( schemaDo.getId() );
				objMapper.deleteBySchemaId(schemaDo.getId() );
				commMapper.deleteByPrimaryKey( schemaDo.getId() );
			}
			return delList.size();
		}
		return 0;
	}

}
