package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.TBydbCatalogTypeDo;
import cn.bywin.business.mapper.bydb.BydbCatalogTypeMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class BydbCatalogTypeService extends BaseServiceImpl<TBydbCatalogTypeDo, String> {

	@Autowired
	private BydbCatalogTypeMapper commMapper;

	@Override
	public Mapper<TBydbCatalogTypeDo> getMapper() {
		return commMapper;
	}

	public List<TBydbCatalogTypeDo> findBeanList(TBydbCatalogTypeDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TBydbCatalogTypeDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public long findSameNameCount(TBydbCatalogTypeDo bean){
		return commMapper.findSameNameCount( bean );
	}

//	@Transactional(rollbackFor=Exception.class)
//	public long insertBeans(List<TBydbCatalogTypeDo> addList){
//
//		if( addList != null){
//			for (TBydbCatalogTypeDo db : addList) {
//				commMapper.insert( db );
//			}
//			return addList.size();
//		}
//		return 0;
//	}

}
