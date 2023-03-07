package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.view.bydb.DigitalAssetVo;
import cn.bywin.business.mapper.bydb.DigitalAssetMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class DigitalAssetService extends BaseServiceImpl<DigitalAssetVo, String> {

	@Autowired
	private DigitalAssetMapper commMapper;

	@Override
	public Mapper<DigitalAssetVo> getMapper() {
		return commMapper;
	}

	public List<DigitalAssetVo> findBeanList(DigitalAssetVo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(DigitalAssetVo bean){
		return  commMapper.findBeanCnt(bean);
	}

}
