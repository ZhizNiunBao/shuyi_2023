package cn.bywin.business.service.olk;


import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkFieldDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.olk.TOlkSchemaDo;
import cn.bywin.business.mapper.olk.OlkDatabaseMapper;
import cn.bywin.business.mapper.olk.OlkFieldMapper;
import cn.bywin.business.mapper.olk.OlkObjectMapper;
import cn.bywin.business.mapper.olk.OlkSchemaMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class OlkFieldService extends BaseServiceImpl<TOlkFieldDo, String> {

	@Autowired
	private OlkFieldMapper commMapper;

	@Autowired
	private OlkDatabaseMapper databaseMapper;

	@Autowired
	private OlkSchemaMapper schemaMapper;

	@Autowired
	private OlkObjectMapper objectMapper;

//	@Autowired
//	private OlkGroupObjectMapper groupObjectMapper;

//	@Autowired
//	private OlkItemObjectMapper itemObjectMapper;

	@Override
	public Mapper<TOlkFieldDo> getMapper() {
		return commMapper;
	}

	public List<TOlkFieldDo> findBeanList(TOlkFieldDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TOlkFieldDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public List<TOlkFieldDo> selectByObjectId( String objectId ){
		return  commMapper.selectByObjectId( objectId );
	}

	@Transactional(rollbackFor=Exception.class)
	public long updateBeanWithFlag(List<TOlkFieldDo> updateList,List<TOlkDatabaseDo> dbList ,
			List<TOlkSchemaDo> schemaList ,List<TOlkObjectDo> objectList ){
		if( dbList != null){
			for (TOlkDatabaseDo obj : dbList) {
				databaseMapper.updateEnable( obj );
			}
		}

		if( schemaList != null){
			for (TOlkSchemaDo obj : schemaList) {
				schemaMapper.updateEnable( obj );
			}
		}

		if( objectList != null){
			for (TOlkObjectDo obj : objectList) {
				objectMapper.updateByPrimaryKeySelective( obj );
			}
		}

		if( updateList != null){
			for (TOlkFieldDo obj : updateList) {
				commMapper.updateEnable( obj );
			}
			return updateList.size();
		}
		return 0;
	}

	public long saveOneObject(List<TOlkFieldDo> addList,List<TOlkFieldDo> modList, List<TOlkFieldDo> delList){
		if( delList != null ){
			for (TOlkFieldDo fieldDo : delList) {
				commMapper.deleteByPrimaryKey(fieldDo.getId());
			}
		}
		if( modList != null ){
			for (TOlkFieldDo fieldDo : modList) {
				commMapper.updateByPrimaryKey( fieldDo );
			}
		}
		if( addList != null ){
			for (TOlkFieldDo fieldDo : addList) {
				commMapper.insert( fieldDo );
			}
		}
		return 1;
	}

	@Transactional(rollbackFor=Exception.class)
	public long delWithUpdate(List<TOlkFieldDo> delList,List<TOlkObjectDo> objList  ){
		if( delList != null){
			for (TOlkFieldDo fieldDo : delList) {
				commMapper.deleteByPrimaryKey( fieldDo.getId() );
			}
		}

		if( objList != null){
			for (TOlkObjectDo obj : objList) {
				objectMapper.updateByPrimaryKeySelective( obj );
			}
		}
		return 1;
	}

}
