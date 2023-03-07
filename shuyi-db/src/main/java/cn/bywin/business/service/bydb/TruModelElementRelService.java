package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.TTruModelElementDo;
import cn.bywin.business.bean.bydb.TTruModelElementRelDo;
import cn.bywin.business.bean.bydb.TTruModelFieldDo;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.mapper.bydb.TruModelComponentMapper;
import cn.bywin.business.mapper.bydb.TruModelElementRelMapper;
import cn.bywin.business.mapper.bydb.TruModelElementMapper;
import cn.bywin.business.mapper.bydb.TruModelFieldMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.sql.Timestamp;
import java.util.List;


@Service
public class TruModelElementRelService extends BaseServiceImpl<TTruModelElementRelDo, String> {


    @Autowired
    private TruModelFieldMapper bydbModelFieldMapper;
    @Autowired
    private TruModelElementRelMapper truModelElementRelMapper;

    @Autowired
    private TruModelElementMapper truModelElementMapper;
    @Autowired
    private TruModelComponentMapper bydbModelComponentMapper;

    @Override
    public Mapper<TTruModelElementRelDo> getMapper() {
        return truModelElementRelMapper;
    }

    public List<TTruModelElementRelDo> selectByModelId(String modelId) {
        return truModelElementRelMapper.selectByModelId(modelId);
    }

    public List<TTruModelElementRelDo> selectByExist(String startId, String endId) {
        return truModelElementRelMapper.selectByExist(startId, endId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteDetail(String id,List<TTruModelElementDo> updateList ) throws Exception {

        //TTruModelElementRelDo tBydbModelElementRelDo = truModelElementRelMapper.selectById(id);
        //清空配置
        //TTruModelElementDo elementDo=   truModelElementMapper.selectById(tBydbModelElementRelDo.getEndElementId());
        //elementDo.setConfig(null);
       // bydbModelFieldMapper.deleteByExtendsId(tBydbModelElementRelDo.getStartElementId());
        //truModelElementMapper.updateByPrimaryKey(elementDo);
        truModelElementRelMapper.deleteByPrimaryKey(id);
        if( updateList != null ){
            for ( TTruModelElementDo elementDo : updateList ) {
                truModelElementMapper.updateByPrimaryKeySelective( elementDo );
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertBeanDetail(TTruModelElementRelDo elementInfo, List<TTruModelFieldDo> fieldDos,List<TTruModelElementDo> updateList) throws Exception {
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
            for ( TTruModelElementDo elementDo : updateList ) {
                truModelElementMapper.updateByPrimaryKeySelective( elementDo );
            }
        }

    }
}
