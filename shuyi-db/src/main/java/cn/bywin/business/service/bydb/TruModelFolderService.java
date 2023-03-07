package cn.bywin.business.service.bydb;



import cn.bywin.business.bean.bydb.TTruModelFolderDo;
import cn.bywin.business.mapper.bydb.TruModelFolderMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class TruModelFolderService extends BaseServiceImpl<TTruModelFolderDo, String> {

	@Autowired
	private TruModelFolderMapper commMapper;

	@Override
	public Mapper<TTruModelFolderDo> getMapper() {
		return commMapper;
	}

	public List<TTruModelFolderDo> findBeanList( TTruModelFolderDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt( TTruModelFolderDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public long findSameNameCount( TTruModelFolderDo bean){
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
