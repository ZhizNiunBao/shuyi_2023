package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.TTruModelTaskLogDo;
import cn.bywin.business.mapper.bydb.TruModelTaskLogMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class TruModelTaskLogService extends BaseServiceImpl<TTruModelTaskLogDo, String> {

	@Autowired
	private TruModelTaskLogMapper commMapper;

	@Override
	public Mapper<TTruModelTaskLogDo> getMapper() {
		return commMapper;
	}

	public List<TTruModelTaskLogDo> findBeanList(TTruModelTaskLogDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TTruModelTaskLogDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public List<TTruModelTaskLogDo> findUnfinished(String modelId){
		return commMapper.findUnfinished( modelId );
	}

	public long deleteByModelId(String modelId ){
		return commMapper.deleteByModelId( modelId );
	}

}
