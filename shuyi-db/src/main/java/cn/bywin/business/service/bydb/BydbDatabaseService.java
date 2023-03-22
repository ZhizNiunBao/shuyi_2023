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
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Service
public class BydbDatabaseService extends BaseServiceImpl<TBydbDatabaseDo, String> {

	@Autowired
	private BydbDatabaseMapper commMapper;

	@Autowired
	private BydbSchemaMapper schemaMapper;

	@Autowired
	private BydbObjectMapper objMapper;

	@Autowired
	private BydbFieldMapper fieldMapper;

	@Autowired
	private BydbDataNodeMapper dataNodeMapper;

	@Override
	public Mapper<TBydbDatabaseDo> getMapper() {
		return commMapper;
	}

	public List<TBydbDatabaseDo> findBeanList(TBydbDatabaseDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TBydbDatabaseDo bean){
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
//		Class cls1 = TBydbDatabaseDo.class;
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

	public long findSameNameCount(TBydbDatabaseDo bean){
		return commMapper.findSameNameCount( bean );
	}

	public List<TBydbDatabaseDo> findUserDbList(TBydbDatabaseDo bean){
		return  commMapper.findUserDbList(bean);
	}

	@Transactional(rollbackFor=Exception.class)
	public long addNewWithSubMetaData(TBydbDatabaseDo bean, List<TBydbSchemaDo> schemaList, List<TBydbObjectDo> objList, List<TBydbFieldDo> fieldList){
		commMapper.insert( bean );
		if( schemaList != null){
			for (TBydbSchemaDo schemaDo : schemaList) {
				schemaMapper.insert( schemaDo );
			}
		}
		if( objList != null){
			for (TBydbObjectDo objectDo : objList) {
				objMapper.insert( objectDo );
			}
		}
		if( fieldList != null){
			for (TBydbFieldDo fieldDo : fieldList) {
				fieldMapper.insert( fieldDo );
			}
		}
		return 1;
	}

	@Transactional(rollbackFor=Exception.class)
	public long addNewAndDelOthers(List<TBydbDatabaseDo> addDbList, List<TBydbDatabaseDo> disDbList){

		if( disDbList != null){
			for (TBydbDatabaseDo db : disDbList) {
				commMapper.updateByPrimaryKey( db );
				fieldMapper.updateEnableByDbId( db.getId() );
				objMapper.updateEnableByDbId( db.getId() );
				schemaMapper.updateEnableByDbId( db.getId() );
			}
		}
		if( addDbList != null){
			for (TBydbDatabaseDo db : addDbList) {
				commMapper.insert( db );
			}
		}
		return 1;
	}


	@Transactional(rollbackFor=Exception.class)
	public long updateBeanWithFlag(List<TBydbDatabaseDo> updateList){
		if( updateList != null){
			for (TBydbDatabaseDo obj : updateList) {
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
	public long deleteWithOther(TBydbDatabaseDo bean){
		dataNodeMapper.delByDbId( bean.getId() );
		fieldMapper.deleteByDatabaseId( bean.getId() );
		objMapper.deleteByDatabaseId( bean.getId() );
		schemaMapper.deleteByDatabaseId( bean.getId() );
		commMapper.deleteByPrimaryKey( bean.getId() );
		return 1;
	}

}
