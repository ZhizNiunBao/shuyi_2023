package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.TTruDiscussObjectDo;
import cn.bywin.business.mapper.bydb.TruDiscussObjectMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class TruDiscussObjectService extends BaseServiceImpl<TTruDiscussObjectDo, String> {

	@Autowired
	private TruDiscussObjectMapper commMapper;

	@Override
	public Mapper<TTruDiscussObjectDo> getMapper() {
		return commMapper;
	}

	public List<TTruDiscussObjectDo> findBeanList(TTruDiscussObjectDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TTruDiscussObjectDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public List<TTruDiscussObjectDo> findUnfinished(String datasetId){
		return commMapper.findUnfinished( datasetId );
	}

	public long deleteByDatasetId(String datasetId ){
		return commMapper.deleteByDatasetId( datasetId );
	}

}
