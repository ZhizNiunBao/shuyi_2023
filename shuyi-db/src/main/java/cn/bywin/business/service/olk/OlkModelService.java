package cn.bywin.business.service.olk;


import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.olk.TOlkModelTaskLogDo;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.mapper.olk.OlkModelElementJobMapper;
import cn.bywin.business.mapper.olk.OlkModelElementMapper;
import cn.bywin.business.mapper.olk.OlkModelElementRelMapper;
import cn.bywin.business.mapper.olk.OlkModelFieldMapper;
import cn.bywin.business.mapper.olk.OlkModelMapper;
import cn.bywin.business.mapper.olk.OlkModelObjectMapper;
import cn.bywin.business.mapper.olk.OlkModelTaskLogMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;


@Service
public class OlkModelService extends BaseServiceImpl<TOlkModelDo, String> {

	@Autowired
	private OlkModelMapper commMapper;
	    @Autowired
    private OlkModelElementMapper bydbModelElementMapper;

    @Autowired
    private OlkModelElementRelMapper bydbModelElementRelMapper;

    @Autowired
    private OlkModelObjectMapper bydbModelObjectMapper;

    @Autowired
    private OlkModelElementJobMapper bydbModelElementJobMapper;

    @Autowired
    private OlkModelFieldMapper bydbModelFieldMapper;

    @Autowired
    private OlkModelTaskLogMapper logMapper;


	@Override
	public Mapper<TOlkModelDo> getMapper() {
		return commMapper;
	}

	public List<TOlkModelDo> findBeanList( TOlkModelDo bean){
		return  commMapper.findBeanList(bean);
	}

	public long findBeanCnt( TOlkModelDo bean){
		return  commMapper.findBeanCnt(bean);
	}
	public List<TOlkModelDo> findByUser( String creatorAccount){
        return commMapper.findByUser(creatorAccount);
    }
    public List<Map<String,Object>> statsByUser( TOlkModelDo bean){
        return commMapper.statsByUser(bean);
    }

    public List<TOlkModelDo> findByName(String name) {
        return commMapper.findByName(name);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteById(String id) {
        List<TOlkModelObjectDo> objectList =bydbModelObjectMapper.selectByModelId(id);
        objectList.stream().forEach(element -> bydbModelObjectMapper.deleteByPrimaryKey(element.getId()));
        List<TOlkModelElementRelDo> fModelElementRelDos = bydbModelElementRelMapper.selectByModelId(id);
        fModelElementRelDos.stream().forEach(element -> bydbModelElementRelMapper.deleteByPrimaryKey(element.getId()));
        List<TOlkModelElementDo> fModelElementDos = bydbModelElementMapper.selectByModelId(id);
        bydbModelElementJobMapper.deleteByModelId(id);
        fModelElementDos.stream().forEach(e->{
            bydbModelFieldMapper.deleteByElementId(e.getId());
            bydbModelElementMapper.deleteByPrimaryKey(e.getId());
        });
        return getMapper().deleteByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertBeanDetail(TOlkModelDo modelInfo) {
        String uuid = ComUtil.genId();
        modelInfo.setId(uuid);
        commMapper.insert(modelInfo);
    }

    @Transactional(rollbackFor=Exception.class)
    public long updateBeanWithLog( TOlkModelDo bean, TOlkModelTaskLogDo addLog, List<TOlkModelTaskLogDo> updList){
        commMapper.updateByPrimaryKeySelective( bean );
        if( addLog != null)
            logMapper.insert( addLog );
        if( updList != null){
            for (TOlkModelTaskLogDo logDo : updList) {
                logMapper.updateByPrimaryKey( logDo );
            }
        }
        return 1;
    }

    @Transactional(rollbackFor=Exception.class)
    public long copyModel( TOlkModelDo bean, List<TOlkModelElementDo> eleList,List<TOlkModelElementRelDo> relList,List<TOlkModelFieldDo> fieldList,List<TOlkModelObjectDo> objList){
        commMapper.insert( bean );

        if( objList != null){
            for (TOlkModelObjectDo obj : objList) {
                bydbModelObjectMapper.insert( obj );
            }
        }

        if( eleList != null){
            for (TOlkModelElementDo ele : eleList) {
                bydbModelElementMapper.insert( ele );
            }
        }

        if( relList != null){
            for (TOlkModelElementRelDo rel : relList) {
                bydbModelElementRelMapper.insert( rel );
            }
        }

        if( fieldList != null){
            for (TOlkModelFieldDo field : fieldList) {
                bydbModelFieldMapper.insert( field );
            }
        }
        return 1;
    }
}
