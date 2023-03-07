package cn.bywin.business.service.federal;

import cn.bywin.business.bean.federal.FModelCollectDo;
import cn.bywin.business.bean.federal.FModelJobDo;
import cn.bywin.business.bean.view.federal.FModelJobVo;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.mapper.federal.ModelCollectMapper;
import cn.bywin.business.mapper.federal.ModelJobMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class ModelJobService extends BaseServiceImpl<FModelJobDo, String> {

    @Autowired
    private ModelJobMapper modelJobMapper;


    @Autowired
    private ModelCollectMapper modelCollectMapper;

    @Override
    public Mapper<FModelJobDo> getMapper() {
        return modelJobMapper;
    }

    public List<FModelJobDo> findBeanList(FModelJobDo modelInfo) {
        return modelJobMapper.findBeanList(modelInfo);
    }

    public List<FModelJobDo> selectByModelId(String modelId) {
        return modelJobMapper.selectByModelId(modelId);
    }

    public long findBeanCnt(FModelJobDo bean) {
        return modelJobMapper.findBeanCnt(bean);
    }


    public List<FModelJobVo> findBeanAllList(FModelJobDo modelDo) {
        return modelJobMapper.findBeanAllList(modelDo);
    }

    public long findBeanAllCnt(FModelJobDo modelDo) {
        return modelJobMapper.findBeanAllCnt(modelDo);
    }

    public List<FModelJobVo> findBeanCollectList(FModelJobVo modelDo) {
        return modelJobMapper.findBeanCollectList(modelDo);
    }

    public long findBeanCollectCnt(FModelJobVo modelDo) {
        return modelJobMapper.findBeanCollectCnt(modelDo);
    }

    @Transactional
    public void deleteModelCollect(FModelCollectDo fModelCollectDo) {
        modelCollectMapper.deleteById(fModelCollectDo.getUserId(),fModelCollectDo.getModelJoBId());
    }

    @Transactional
    public long addModelCollect(FModelCollectDo fModelCollectDo) {
        long cnt = modelCollectMapper.findBeanCnt(fModelCollectDo.getUserId(),fModelCollectDo.getModelJoBId());
        if (cnt>0){
            return cnt;
        }
        fModelCollectDo.setId(ComUtil.genId());
        modelCollectMapper.insert(fModelCollectDo);
        return 0;
    }
}
