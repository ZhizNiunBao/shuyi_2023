package cn.bywin.business.service.olk;


import cn.bywin.business.bean.olk.TOlkUdfDo;
import cn.bywin.business.mapper.olk.OlkUdfMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class OlkUdfService extends BaseServiceImpl<TOlkUdfDo, String> {

	@Autowired
	private OlkUdfMapper commMapper;

	@Override
	public Mapper<TOlkUdfDo> getMapper() {
		return commMapper;
	}

	public List<TOlkUdfDo> findBeanList(TOlkUdfDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TOlkUdfDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public long findSameNameCount(TOlkUdfDo bean){
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
