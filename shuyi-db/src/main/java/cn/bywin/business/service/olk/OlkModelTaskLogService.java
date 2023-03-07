package cn.bywin.business.service.olk;


import cn.bywin.business.bean.olk.TOlkModelTaskLogDo;
import cn.bywin.business.mapper.olk.OlkModelTaskLogMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class OlkModelTaskLogService extends BaseServiceImpl<TOlkModelTaskLogDo, String> {

	@Autowired
	private OlkModelTaskLogMapper commMapper;

	@Override
	public Mapper<TOlkModelTaskLogDo> getMapper() {
		return commMapper;
	}

	public List<TOlkModelTaskLogDo> findBeanList(TOlkModelTaskLogDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TOlkModelTaskLogDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public List<TOlkModelTaskLogDo> findUnfinished(String modelId){
		return commMapper.findUnfinished( modelId );
	}

	public long deleteByModelId(String modelId ){
		return commMapper.deleteByModelId( modelId );
	}

}
