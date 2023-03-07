package cn.bywin.business.service.olk;


import cn.bywin.business.bean.olk.TOlkCatalogTypeDo;
import cn.bywin.business.mapper.olk.OlkCatalogTypeMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class OlkCatalogTypeService extends BaseServiceImpl<TOlkCatalogTypeDo, String> {

	@Autowired
	private OlkCatalogTypeMapper commMapper;

	@Override
	public Mapper<TOlkCatalogTypeDo> getMapper() {
		return commMapper;
	}

	public List<TOlkCatalogTypeDo> findBeanList(TOlkCatalogTypeDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TOlkCatalogTypeDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public long findSameNameCount(TOlkCatalogTypeDo bean){
		return commMapper.findSameNameCount( bean );
	}

//	@Transactional(rollbackFor=Exception.class)
//	public long insertBeans(List<TOlkCatalogTypeDo> addList){
//
//		if( addList != null){
//			for (TOlkCatalogTypeDo db : addList) {
//				commMapper.insert( db );
//			}
//			return addList.size();
//		}
//		return 0;
//	}

}
