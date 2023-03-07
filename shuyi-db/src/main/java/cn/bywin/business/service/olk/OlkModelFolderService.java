package cn.bywin.business.service.olk;


import cn.bywin.business.bean.olk.TOlkModelFolderDo;
import cn.bywin.business.mapper.olk.OlkModelFolderMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class OlkModelFolderService extends BaseServiceImpl<TOlkModelFolderDo, String> {

	@Autowired
	private OlkModelFolderMapper commMapper;

	@Override
	public Mapper<TOlkModelFolderDo> getMapper() {
		return commMapper;
	}

	public List<TOlkModelFolderDo> findBeanList( TOlkModelFolderDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt( TOlkModelFolderDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public long findSameNameCount( TOlkModelFolderDo bean){
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
