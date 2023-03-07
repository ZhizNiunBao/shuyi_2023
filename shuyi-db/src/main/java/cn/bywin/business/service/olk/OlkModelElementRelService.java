package cn.bywin.business.service.olk;


import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.mapper.bydb.TruModelComponentMapper;
import cn.bywin.business.mapper.bydb.TruModelElementMapper;
import cn.bywin.business.mapper.bydb.TruModelElementRelMapper;
import cn.bywin.business.mapper.bydb.TruModelFieldMapper;
import cn.bywin.business.mapper.olk.OlkModelComponentMapper;
import cn.bywin.business.mapper.olk.OlkModelElementMapper;
import cn.bywin.business.mapper.olk.OlkModelElementRelMapper;
import cn.bywin.business.mapper.olk.OlkModelFieldMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.sql.Timestamp;
import java.util.List;


@Service
public class OlkModelElementRelService extends BaseServiceImpl<TOlkModelElementRelDo, String> {


    @Autowired
    private OlkModelFieldMapper bydbModelFieldMapper;
    @Autowired
    private OlkModelElementRelMapper truModelElementRelMapper;

    @Autowired
    private OlkModelElementMapper truModelElementMapper;
    @Autowired
    private OlkModelComponentMapper bydbModelComponentMapper;

    @Override
    public Mapper<TOlkModelElementRelDo> getMapper() {
        return truModelElementRelMapper;
    }

    public List<TOlkModelElementRelDo> selectByModelId(String modelId) {
        return truModelElementRelMapper.selectByModelId(modelId);
    }

    public List<TOlkModelElementRelDo> selectByExist(String startId, String endId) {
        return truModelElementRelMapper.selectByExist(startId, endId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteDetail(String id,List<TOlkModelElementDo> updateList ) throws Exception {

        //TOlkModelElementRelDo tBydbModelElementRelDo = truModelElementRelMapper.selectById(id);
        //清空配置
        //TOlkModelElementDo elementDo=   truModelElementMapper.selectById(tBydbModelElementRelDo.getEndElementId());
        //elementDo.setConfig(null);
       // bydbModelFieldMapper.deleteByExtendsId(tBydbModelElementRelDo.getStartElementId());
        //truModelElementMapper.updateByPrimaryKey(elementDo);
        truModelElementRelMapper.deleteByPrimaryKey(id);
        if( updateList != null ){
            for ( TOlkModelElementDo elementDo : updateList ) {
                truModelElementMapper.updateByPrimaryKeySelective( elementDo );
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertBeanDetail(TOlkModelElementRelDo elementInfo, List<TOlkModelFieldDo> fieldDos,List<TOlkModelElementDo> updateList) throws Exception {
        String uuid = ComUtil.genId();
        elementInfo.setId(uuid);
        elementInfo.setCreatedTime((new Timestamp(System.currentTimeMillis())));
        elementInfo.setModifiedTime((new Timestamp(System.currentTimeMillis())));
        truModelElementRelMapper.insert(elementInfo);
        fieldDos.stream().forEach(e ->{
            e.setCreatedTime(ComUtil.getCurTimestamp());
            bydbModelFieldMapper.insert(e);
        });
        if( updateList != null ){
            for ( TOlkModelElementDo elementDo : updateList ) {
                truModelElementMapper.updateByPrimaryKeySelective( elementDo );
            }
        }

    }
}
