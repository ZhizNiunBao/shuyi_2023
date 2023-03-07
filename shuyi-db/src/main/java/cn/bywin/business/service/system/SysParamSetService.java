package cn.bywin.business.service.system;

import cn.bywin.business.bean.system.SysParamSetDo;
import cn.bywin.business.mapper.system.SysParamSetMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service("sysParamSetService")
public class SysParamSetService extends BaseServiceImpl<SysParamSetDo, String> {

	@Autowired
	private SysParamSetMapper commMapper;

	@Override
	public Mapper<SysParamSetDo> getMapper() {
		return commMapper;
	}

	public List<SysParamSetDo> findBeanList(SysParamSetDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(SysParamSetDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public int checkCode( SysParamSetDo bean ){
		return commMapper.checkCode( bean );
	}
	public int checkName( SysParamSetDo bean ){
		return commMapper.checkName( bean );
	}
	public SysParamSetDo findByCode(String code){
		return commMapper.findByCode( code );
	}
	public String findValueByCode(String code,String def){
		SysParamSetDo set= commMapper.findByCode( code );
		if( set == null ) {
			return def;
		}
		if( set.getParaValue() == null ){
			return def;
		}
		return set.getParaValue();
	}

	public List<SysParamSetDo> findByIds( String ids ){
		return commMapper.findByIds( ids );
	}

	public long deleteByIds( String ids ){
		return commMapper.deleteByIds( ids );
	}
	
}
