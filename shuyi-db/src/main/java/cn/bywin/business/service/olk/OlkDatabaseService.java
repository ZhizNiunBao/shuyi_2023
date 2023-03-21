package cn.bywin.business.service.olk;


import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkFieldDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.olk.TOlkSchemaDo;
import cn.bywin.business.mapper.olk.*;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.beans.PropertyDescriptor;
import java.util.*;


@Service
public class OlkDatabaseService extends BaseServiceImpl<TOlkDatabaseDo, String> {

	@Autowired
	private OlkDatabaseMapper commMapper;

	@Autowired
	private OlkSchemaMapper schemaMapper;

	@Autowired
	private OlkObjectMapper objMapper;

	@Autowired
	private OlkFieldMapper fieldMapper;

	@Autowired
	private OlkDataNodeMapper dataNodeMapper;

	@Override
	public Mapper<TOlkDatabaseDo> getMapper() {
		return commMapper;
	}

	public List<TOlkDatabaseDo> findBeanList(TOlkDatabaseDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TOlkDatabaseDo bean){
		return  commMapper.findBeanCnt(bean);
	}


	public Integer findMaxOrder(){
		return commMapper.findMaxOrder();
	}

	/**
	 * @description: 根据sql获取sql 数据
	 * @author: me
	 * @date: 2019-06-03
	 */
	public List<Map<String,Object>> selectData(String sql) {
		return commMapper.selectData(sql);
	}

	public <T> List<T> selectData(String sql,Class<T> cls)throws Exception{
		List<Map<String,Object>> dataList = commMapper.selectData(sql);
		if( dataList == null )
			return null;
		List<T> retList  =new ArrayList<>();
//		List<Field> fieldList = new ArrayList<>();
//		Class cls1 = TOlkDatabaseDo.class;
//		while( true ) {
//			Field[] fields = cls1.getDeclaredFields();
//			fieldList.addAll(Arrays.asList( fields ) );
//			cls1 = cls1.getSuperclass();
//			if( cls1.getSimpleName().equalsIgnoreCase("Object")){
//				break;
//			}
//		}


		for( Map<String,Object> map: dataList) {
			Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
			T obj = cls.newInstance();
			BeanWrapper bw = new BeanWrapperImpl(obj);
			PropertyDescriptor[] props = bw.getPropertyDescriptors();
			HashMap<String,PropertyDescriptor> nameMap = new HashMap<>();
			for (PropertyDescriptor prop : props) {
				nameMap.put( prop.getName().toLowerCase(), prop );
			}
			while ( iterator.hasNext()) {
				Map.Entry<String, Object> data = iterator.next();
				String labName = data.getKey().replaceAll("_", "").toLowerCase();
				PropertyDescriptor prop = nameMap.get(labName);
				Object dataVal = data.getValue();
				if( prop.getName() != null && dataVal != null){
					if( prop.getPropertyType().getSimpleName().equalsIgnoreCase("string")) {
						bw.setPropertyValue(prop.getName(), dataVal.toString());
					}
					else{
						bw.setPropertyValue(prop.getName(), dataVal);
					}
				}
//				for (Field f : fieldList) {
//					if (f.getName().equalsIgnoreCase(labName)) {
//						boolean flag = f.isAccessible();
//						f.setAccessible(true);
//						f.set(obj, DbDataToObject.toEffVal( dataVal, f.getType().getSimpleName()));
//						f.setAccessible(flag);
//						break;
//					}
//				}
			}
			retList.add(obj);
		}
		return retList;
	}

	public long findSameNameCount(TOlkDatabaseDo bean){
		return commMapper.findSameNameCount( bean );
	}

	public List<TOlkDatabaseDo> findUserDbList(TOlkDatabaseDo bean){
		return  commMapper.findUserDbList(bean);
	}

	@Transactional(rollbackFor=Exception.class)
	public long addNewWithSubMetaData(TOlkDatabaseDo bean, List<TOlkSchemaDo> schemaList, List<TOlkObjectDo> objList, List<TOlkFieldDo> fieldList){
		commMapper.insert( bean );
		if( schemaList != null){
			for (TOlkSchemaDo schemaDo : schemaList) {
				schemaMapper.insert( schemaDo );
			}
		}
		if( objList != null){
			for (TOlkObjectDo objectDo : objList) {
				objMapper.insert( objectDo );
			}
		}
		if( fieldList != null){
			for (TOlkFieldDo fieldDo : fieldList) {
				fieldMapper.insert( fieldDo );
			}
		}
		return 1;
	}

	@Transactional(rollbackFor=Exception.class)
	public long addNewAndDelOthers(List<TOlkDatabaseDo> addDbList, List<TOlkDatabaseDo> disDbList){

		if( disDbList != null){
			for (TOlkDatabaseDo db : disDbList) {
				commMapper.updateByPrimaryKey( db );
				fieldMapper.updateEnableByDbId( db.getId() );
				objMapper.updateEnableByDbId( db.getId() );
				schemaMapper.updateEnableByDbId( db.getId() );
			}
		}
		if( addDbList != null){
			for (TOlkDatabaseDo db : addDbList) {
				commMapper.insert( db );
			}
		}
		return 1;
	}


	@Transactional(rollbackFor=Exception.class)
	public long updateBeanWithFlag(List<TOlkDatabaseDo> updateList){
		if( updateList != null){
			for (TOlkDatabaseDo obj : updateList) {
				commMapper.updateByPrimaryKey( obj );
				//if( obj.getEnable() == 0 ) {
					schemaMapper.updateEnableByDbId(obj.getId());
					objMapper.updateEnableByDbId(obj.getId());
					fieldMapper.updateEnableByDbId( obj.getId() );
				//}
			}
			return updateList.size();
		}
		return 0;
	}

	@Transactional(rollbackFor=Exception.class)
	public long deleteWithOther(TOlkDatabaseDo bean){
		dataNodeMapper.delByDbId( bean.getId() );
		fieldMapper.deleteByDatabaseId( bean.getId() );
		objMapper.deleteByDatabaseId( bean.getId() );
		schemaMapper.deleteByDatabaseId( bean.getId() );
		commMapper.deleteByPrimaryKey( bean.getId() );
		return 1;
	}

}
