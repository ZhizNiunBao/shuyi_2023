package cn.bywin.business.service.federal;

import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FModelDataDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.bean.federal.FModelElementRelDo;
import cn.bywin.business.bean.view.federal.FModelElementVo;
import cn.bywin.business.mapper.federal.ModelDataMapper;
import cn.bywin.business.mapper.federal.ModelElementMapper;
import cn.bywin.business.mapper.federal.ModelElementRelMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class ModelElementService extends BaseServiceImpl<FModelElementDo, String> {


    @Autowired
    private ModelElementMapper componentElementMapper;

    @Autowired
    private ModelElementRelMapper componentElementRelMapper;
    @Autowired
    private ModelDataMapper modelDataMapper;


    @Override
    public Mapper<FModelElementDo> getMapper() {
        return componentElementMapper;
    }

    public List<FModelElementVo> selectByModelIdWithDetail(String modelId) {
        return componentElementMapper.selectByModelIdWithDetail(modelId);
    }
    public  List<FModelElementDo> selectStartId(String vertexId) {

        return componentElementMapper.selectStartId(vertexId);
    }
    public List<FModelElementDo> selectByModelId(String modelId) {
        return componentElementMapper.selectByModelId(modelId);
    }
    public List<FModelElementVo> selectByModelIdDeTail(String modelId) {
        return componentElementMapper.selectByModelIdDeTail(modelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteById(String id) {
     //   FModelElementDo fModelElementDo = componentElementMapper.selectById(id);

        List<FModelElementRelDo> elementRels = componentElementRelMapper.selectByVertexId(id);
        elementRels.stream().forEach(element -> componentElementRelMapper.deleteByPrimaryKey(element.getId()));
        componentElementMapper.deleteByPrimaryKey(id);
        return getMapper().deleteByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBeanWithData(FModelElementDo elementInfo, List<FModelDataDo> addModelData) {

        componentElementMapper.updateByPrimaryKey(elementInfo);
        modelDataMapper.deleteByModelId(elementInfo.getModelId());
        for (FModelDataDo fDataPartyDo : addModelData) {
            modelDataMapper.insert(fDataPartyDo);
        }

    }

    public FComponentDo selectComponentByModelId(String modelId) {
        return componentElementMapper.selectComponentByModelId(modelId);
    }
}
