package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.TTruFavouriteObjectDo;
//import cn.bywin.business.mapper.bydb.BydbFavouriteObjectHisMapper;
import cn.bywin.business.mapper.bydb.TruFavouriteObjectMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class TruFavouriteObjectService extends BaseServiceImpl<TTruFavouriteObjectDo, String> {

	@Autowired
	private TruFavouriteObjectMapper commMapper;

//	@Autowired
//	private BydbFavouriteObjectHisMapper hisMapper;

	@Override
	public Mapper<TTruFavouriteObjectDo> getMapper() {
		return commMapper;
	}

	public List<TTruFavouriteObjectDo> findBeanList( TTruFavouriteObjectDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt( TTruFavouriteObjectDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public List<TTruFavouriteObjectDo> findUnfinished( String datasetId){
		return commMapper.findUnfinished( datasetId );
	}

	public long deleteByDatasetId(String datasetId ){
		return commMapper.deleteByDatasetId( datasetId );
	}

}
