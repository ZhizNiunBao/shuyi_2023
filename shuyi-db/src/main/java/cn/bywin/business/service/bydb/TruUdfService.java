package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.TTruUdfDo;
import cn.bywin.business.mapper.bydb.TruUdfMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class TruUdfService extends BaseServiceImpl<TTruUdfDo, String> {

	@Autowired
	private TruUdfMapper commMapper;

	@Override
	public Mapper<TTruUdfDo> getMapper() {
		return commMapper;
	}

	public List<TTruUdfDo> findBeanList(TTruUdfDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TTruUdfDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public long findSameNameCount(TTruUdfDo bean){
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
