package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.TBydbDcServerDo;
import cn.bywin.business.common.except.MessageException;
import cn.bywin.business.mapper.bydb.BydbCatalogTypeMapper;
import cn.bywin.business.mapper.bydb.BydbDcServerMapper;
import cn.service.impl.BaseServiceImpl;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class BydbDcServerService extends BaseServiceImpl<TBydbDcServerDo, String> {

	@Autowired
	private BydbDcServerMapper commMapper;
	@Autowired
	private BydbCatalogTypeMapper cataTypeMapper;

	@Override
	public Mapper<TBydbDcServerDo> getMapper() {
		return commMapper;
	}

	public List<TBydbDcServerDo> findBeanList(TBydbDcServerDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(TBydbDcServerDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public List<TBydbDcServerDo>  findBaseList(TBydbDcServerDo bean){
		if( bean == null)
			bean = new TBydbDcServerDo();
		return  commMapper.findBaseList(bean);
	}

	public TBydbDcServerDo findSimpleBean(String id){
		return  commMapper.findSimpleBean( id );
	}

	public long findSameCodeCount(TBydbDcServerDo bean){
		return commMapper.findSameCodeCount( bean );
	}

	public long findSameNameCount(TBydbDcServerDo bean){
		return commMapper.findSameNameCount( bean );
	}

	public long findSameDeptCount(TBydbDcServerDo bean){
		return commMapper.findSameDeptCount( bean );
	}

	public long findSameManageUserCount(TBydbDcServerDo bean){
		return commMapper.findSameManageUserCount( bean );
	}

	public long findSameUrlCount(TBydbDcServerDo bean){
		return commMapper.findSameUrlCount( bean );
	}

	public TBydbDcServerDo findCenter() throws MessageException{
		TBydbDcServerDo bean = new TBydbDcServerDo();
		bean.setDcType(  0 );
		List<TBydbDcServerDo> list = commMapper.select( bean );
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
	public long deleteWithOther(List<TBydbDcServerDo> delList){

		if( delList != null){
			for (TBydbDcServerDo dcDo : delList) {
				cataTypeMapper.deleteByDcId( dcDo.getId() );
				commMapper.deleteByPrimaryKey( dcDo.getId() );
			}
		}
		return 1;
	}

}
