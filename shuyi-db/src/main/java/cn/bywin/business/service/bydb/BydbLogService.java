package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.TBydbLogDo;
import cn.bywin.business.mapper.bydb.BydbLogMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class BydbLogService extends BaseServiceImpl<TBydbLogDo, String> {

	@Autowired
	private BydbLogMapper commMapper;

	@Override
	public Mapper<TBydbLogDo> getMapper() {
		return commMapper;
	}

	public List<TBydbLogDo> findBeanList(TBydbLogDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TBydbLogDo bean){
		return  commMapper.findBeanCnt(bean);
	}


}
