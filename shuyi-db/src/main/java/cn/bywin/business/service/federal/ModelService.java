package cn.bywin.business.service.federal;

import cn.bywin.business.bean.federal.*;
import cn.bywin.business.mapper.federal.*;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class ModelService extends BaseServiceImpl<FModelDo, String> {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ModelElementMapper modelElementMapper;
    @Autowired
    private ModelElementRelMapper modelElementRelMapper;
    @Autowired
    private ModelDataMapper modelDataMapper;
    @Autowired
    private ModelJobMapper modelJobMapper;

    @Override
    public Mapper<FModelDo> getMapper() {
        return modelMapper;
    }

    public List<FModelDo> findBeanList(FModelDo modelInfo) {
        return modelMapper.findBeanList(modelInfo);
    }

    public long findBeanCnt(FModelDo bean) {
        return modelMapper.findBeanCnt(bean);
    }

    public List<FModelDo> selectByProjectId(String projectId) {
        return modelMapper.selectByProjectId(projectId);
    }


    public List<FModelDo> selectByProjectDataId(String projectId,String dataId,List<String> dataIds,Integer types) {
        return modelMapper.selectByProjectDataId(projectId,dataId,dataIds,types);
    }
    public List<String> selectByDataId(String dataId) {
        return modelDataMapper.selectByDataId(dataId);
    }


    @Transactional(rollbackFor = Exception.class)
    public void insertModelData(List<FModelDataDo> addModelData) {
        if (addModelData!=null&&addModelData.size()>0){
            modelDataMapper.deleteByModelId(addModelData.get(0).getModelId());
            for (FModelDataDo fModelDataDo:addModelData) {
                modelDataMapper.insert(fModelDataDo);
            }
        }

    }

    public List<FModelDataDo> selectByModelDataIds(String userId) {
        return modelDataMapper.selectByModelDataIds(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer deleteByAll(String id) {
        List<FModelElementRelDo> fModelElementRelDos = modelElementRelMapper.selectByModelId(id);


        fModelElementRelDos.stream().forEach(element -> modelElementRelMapper.deleteByPrimaryKey(element.getId()));
        List<FModelElementDo> fModelElementDos = modelElementMapper.selectByModelId(id);
        fModelElementDos.stream().forEach(element -> {
            List<FModelDataDo> fModelDataDos = modelDataMapper.selectByModelId(element.getModelId());
            if (fModelDataDos != null && fModelDataDos.size() > 0) {
                fModelDataDos.stream().forEach(c -> modelDataMapper.deleteByPrimaryKey(c.getId()));
            }
            modelElementMapper.deleteByPrimaryKey(element.getId());
        });
        List<FModelJobDo> fModelJobDoList = modelJobMapper.selectByModelId(id);
        fModelJobDoList.stream().forEach(element -> modelJobMapper.deleteByPrimaryKey(element.getId()));
        return getMapper().deleteByPrimaryKey(id);
    }

}
