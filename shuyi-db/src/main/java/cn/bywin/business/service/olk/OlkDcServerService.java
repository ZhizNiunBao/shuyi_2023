package cn.bywin.business.service.olk;


import cn.bywin.business.bean.olk.TOlkDcServerDo;
import cn.bywin.business.common.except.MessageException;
import cn.bywin.business.mapper.olk.OlkCatalogTypeMapper;
import cn.bywin.business.mapper.olk.OlkDcServerMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class OlkDcServerService extends BaseServiceImpl<TOlkDcServerDo, String> {

	@Autowired
	private OlkDcServerMapper commMapper;
	@Autowired
	private OlkCatalogTypeMapper cataTypeMapper;

	@Override
	public Mapper<TOlkDcServerDo> getMapper() {
		return commMapper;
	}

	public List<TOlkDcServerDo> findBeanList(TOlkDcServerDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TOlkDcServerDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public List<TOlkDcServerDo>  findBaseList(TOlkDcServerDo bean){
		if( bean == null)
			bean = new TOlkDcServerDo();
		return  commMapper.findBaseList(bean);
	}

	public TOlkDcServerDo findSimpleBean(String id){
		return  commMapper.findSimpleBean( id );
	}

	public long findSameCodeCount(TOlkDcServerDo bean){
		return commMapper.findSameCodeCount( bean );
	}

	public long findSameNameCount(TOlkDcServerDo bean){
		return commMapper.findSameNameCount( bean );
	}

	public long findSameDeptCount(TOlkDcServerDo bean){
		return commMapper.findSameDeptCount( bean );
	}

	public long findSameManageUserCount(TOlkDcServerDo bean){
		return commMapper.findSameManageUserCount( bean );
	}

	public long findSameUrlCount(TOlkDcServerDo bean){
		return commMapper.findSameUrlCount( bean );
	}

	public TOlkDcServerDo findCenter() throws MessageException{
		TOlkDcServerDo bean = new TOlkDcServerDo();
		bean.setDcType(  0 );
		List<TOlkDcServerDo> list = commMapper.select( bean );
		if( list.size()==0 ){
			return null;
		}
		else if(list.size()==1){
			return list.get( 0 );
		}
		else{
			throw new MessageException( "中心节点不唯一" );
		}
	}

	@Transactional(rollbackFor=Exception.class)
	public long deleteWithOther(List<TOlkDcServerDo> delList){

		if( delList != null){
			for (TOlkDcServerDo dcDo : delList) {
				cataTypeMapper.deleteByDcId( dcDo.getId() );
				commMapper.deleteByPrimaryKey( dcDo.getId() );
			}
		}
		return 1;
	}

}
