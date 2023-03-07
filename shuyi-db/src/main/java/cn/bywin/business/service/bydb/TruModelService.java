package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.bydb.TTruModelElementDo;
import cn.bywin.business.bean.bydb.TTruModelElementRelDo;
import cn.bywin.business.bean.bydb.TTruModelFieldDo;
import cn.bywin.business.bean.bydb.TTruModelObjectDo;
import cn.bywin.business.bean.bydb.TTruModelTaskLogDo;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.mapper.bydb.TruModelElementJobMapper;
import cn.bywin.business.mapper.bydb.TruModelElementMapper;
import cn.bywin.business.mapper.bydb.TruModelElementRelMapper;
import cn.bywin.business.mapper.bydb.TruModelFieldMapper;
import cn.bywin.business.mapper.bydb.TruModelMapper;
import cn.bywin.business.mapper.bydb.TruModelObjectMapper;
import cn.bywin.business.mapper.bydb.TruModelTaskLogMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;


@Service
public class TruModelService extends BaseServiceImpl<TTruModelDo, String> {

	@Autowired
	private TruModelMapper commMapper;
	    @Autowired
    private TruModelElementMapper bydbModelElementMapper;

    @Autowired
    private TruModelElementRelMapper bydbModelElementRelMapper;

    @Autowired
    private TruModelObjectMapper bydbModelObjectMapper;

    @Autowired
    private TruModelElementJobMapper bydbModelElementJobMapper;

    @Autowired
    private TruModelFieldMapper bydbModelFieldMapper;

    @Autowired
    private TruModelTaskLogMapper logMapper;


	@Override
	public Mapper<TTruModelDo> getMapper() {
		return commMapper;
	}

	public List<TTruModelDo> findBeanList( TTruModelDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt( TTruModelDo bean){
		return  commMapper.findBeanCnt(bean);
	}
	public List<TTruModelDo> findByUser( String creatorAccount){
        return commMapper.findByUser(creatorAccount);
    }
    public List<Map<String,Object>> statsByUser( TTruModelDo bean){
        return commMapper.statsByUser(bean);
    }

    public List<TTruModelDo> findByName(String name) {
        return commMapper.findByName(name);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteById(String id) {
        List<TTruModelObjectDo> objectList =bydbModelObjectMapper.selectByModelId(id);
        objectList.stream().forEach(element -> bydbModelObjectMapper.deleteByPrimaryKey(element.getId()));
        List<TTruModelElementRelDo> fModelElementRelDos = bydbModelElementRelMapper.selectByModelId(id);
        fModelElementRelDos.stream().forEach(element -> bydbModelElementRelMapper.deleteByPrimaryKey(element.getId()));
        List<TTruModelElementDo> fModelElementDos = bydbModelElementMapper.selectByModelId(id);
        bydbModelElementJobMapper.deleteByModelId(id);
        fModelElementDos.stream().forEach(e->{
            bydbModelFieldMapper.deleteByElementId(e.getId());
            bydbModelElementMapper.deleteByPrimaryKey(e.getId());
        });
        return getMapper().deleteByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertBeanDetail(TTruModelDo modelInfo) {
        String uuid = ComUtil.genId();
        modelInfo.setId(uuid);
        commMapper.insert(modelInfo);
    }

    @Transactional(rollbackFor=Exception.class)
    public long updateBeanWithLog( TTruModelDo bean, TTruModelTaskLogDo addLog, List<TTruModelTaskLogDo> updList){
        commMapper.updateByPrimaryKeySelective( bean );
        if( addLog != null)
            logMapper.insert( addLog );
        if( updList != null){
            for (TTruModelTaskLogDo logDo : updList) {
                logMapper.updateByPrimaryKey( logDo );
            }
        }
        return 1;
    }

    @Transactional(rollbackFor=Exception.class)
    public long copyModel( TTruModelDo bean, List<TTruModelElementDo> eleList,List<TTruModelElementRelDo> relList,List<TTruModelFieldDo> fieldList,List<TTruModelObjectDo> objList){
        commMapper.insert( bean );

        if( objList != null){
            for (TTruModelObjectDo obj : objList) {
                bydbModelObjectMapper.insert( obj );
            }
        }

        if( eleList != null){
            for (TTruModelElementDo ele : eleList) {
                bydbModelElementMapper.insert( ele );
            }
        }

        if( relList != null){
            for (TTruModelElementRelDo rel : relList) {
                bydbModelElementRelMapper.insert( rel );
            }
        }

        if( fieldList != null){
            for (TTruModelFieldDo field : fieldList) {
                bydbModelFieldMapper.insert( field );
            }
        }
        return 1;
    }
}
