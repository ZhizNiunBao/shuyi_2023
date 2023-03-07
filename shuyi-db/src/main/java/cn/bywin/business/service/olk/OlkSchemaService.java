package cn.bywin.business.service.olk;


import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkFieldDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.olk.TOlkSchemaDo;
import cn.bywin.business.mapper.olk.*;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class OlkSchemaService extends BaseServiceImpl<TOlkSchemaDo, String> {

	@Autowired
	private OlkSchemaMapper commMapper;

	@Autowired
	private OlkDatabaseMapper databaseMapper;

	@Autowired
	private OlkObjectMapper objMapper;

	@Autowired
	private OlkFieldMapper fieldMapper;

	@Autowired
	private OlkDataNodeMapper dataNodeMapper;

	@Override
	public Mapper<TOlkSchemaDo> getMapper() {
		return commMapper;
	}

	public List<TOlkSchemaDo> findBeanList(TOlkSchemaDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TOlkSchemaDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public long findSameNameCount(TOlkSchemaDo bean){
		return commMapper.findSameNameCount( bean );
	}

	public List<TOlkSchemaDo> findUserSchemaList(TOlkSchemaDo bean){
		return  commMapper.findUserSchemaList(bean);
	}


//	public long deleteFolder(String id){
//		return commMapper.deleteFolder(id);
//	}

	@Transactional(rollbackFor=Exception.class)
	public long updateWithObject(List<TOlkSchemaDo> addSchemaList, List<TOlkObjectDo> addObjectList, List<TOlkSchemaDo> delSchemaList
			, List<TOlkObjectDo> delObjectList, List<TOlkFieldDo> addFieldList, List<TOlkFieldDo> modFieldList, List<TOlkFieldDo> delFieldList){

		if( delFieldList != null){
			for (TOlkFieldDo fieldDo : delFieldList) {
				fieldMapper.deleteByPrimaryKey( fieldDo.getId() );
			}
		}

		if( modFieldList != null){
			for (TOlkFieldDo fieldDo : modFieldList) {
				fieldMapper.updateByPrimaryKey( fieldDo );
			}
		}

		if( delObjectList != null){
			for (TOlkObjectDo obj : delObjectList) {
				objMapper.deleteByPrimaryKey( obj.getId() );
			}
		}
		if( delSchemaList != null){
			for (TOlkSchemaDo schema : delSchemaList) {
				commMapper.deleteByPrimaryKey( schema.getId() );
			}
		}
		if( addSchemaList != null){
			for (TOlkSchemaDo schema : addSchemaList) {
				commMapper.insert( schema );
			}
		}

		if( addObjectList != null){
			for (TOlkObjectDo obj : addObjectList) {
				objMapper.insert( obj );
			}
		}

		if( addFieldList != null){
			for (TOlkFieldDo fieldDo : addFieldList) {
				fieldMapper.insert( fieldDo );
			}
		}
		return 1;
	}

	@Transactional(rollbackFor=Exception.class)
	public long updateBeanWithFlag(List<TOlkSchemaDo> updateList,List<TOlkDatabaseDo> dbList ){
		if( dbList != null){
			for (TOlkDatabaseDo obj : dbList) {
				databaseMapper.updateEnable( obj );
			}
		}
		if( updateList != null){
			for (TOlkSchemaDo obj : updateList) {
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
	public long deleteWhithRel(List<TOlkSchemaDo> delList){
		if( delList != null){
			for (TOlkSchemaDo schemaDo : delList) {
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
