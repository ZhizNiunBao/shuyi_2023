package cn.bywin.business.service.system;

import cn.bywin.business.bean.system.SysDictDo;
import cn.bywin.business.mapper.system.SysDictMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.beans.Transient;
import java.util.List;


@Service("sysDictService")
public class SysDictService extends BaseServiceImpl<SysDictDo, String> {

	@Autowired
	private SysDictMapper commMapper;

	@Override
	public Mapper<SysDictDo> getMapper() {
		return commMapper;
	}

	public List<SysDictDo> findBeanList(SysDictDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt(SysDictDo bean){
		return  commMapper.findBeanCnt(bean);
	}

	public int checkCode( SysDictDo bean ){
		return commMapper.checkCode( bean );
	}
	public int checkName( SysDictDo bean ){
		return commMapper.checkName( bean );
	}
//	public List<SysDictDo> findTopMenu(String appCode){
//		return commMapper.findTopMenu( appCode );
//	}
//	public List<SysDictDo> findUserSubMenu(String appCode,String memuCode,String userId){
//		return commMapper.findUserSubMenu( appCode,memuCode,userId );
//	}
//	public List<SysDictDo> findUserAllMenu(String appCode,String userId){
//		return commMapper.findUserAllMenu( appCode,userId );
//	}
	public List<SysDictDo> findLevel1DictByTopCode(String topCode){
		return commMapper.findLevel1DictByTopCode( topCode );
	}

	public List<SysDictDo> findVisibleLevel1DictByTopCode(String topCode){
		return commMapper.findVisibleLevel1DictByTopCode( topCode );
	}

	@Transactional(rollbackFor=Exception.class)
	public int updateTop(SysDictDo bean){
		commMapper.updateByPrimaryKey( bean );
		commMapper.updateDictTopCode( bean );
		return 1;
	}

	public List<SysDictDo> findAllType(){
		return commMapper.findAllType( );
	}

	public List<SysDictDo> findDictByIds(String ids){
		return commMapper.findDictByIds( ids );
	}

	public List<SysDictDo> findSubDict(String pid){
		return commMapper.findSubDict( pid );
	}

	public long findSubDictCnt(String pids){
		return commMapper.findSubDictCnt( pids );
	}

	public long deleteByIds(String ids){
		return commMapper.deleteByIds( ids );
	}

}
