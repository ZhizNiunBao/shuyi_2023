package cn.bywin.business.service.federal;

import cn.bywin.business.bean.federal.FModelElementRelDo;
import cn.bywin.business.mapper.federal.ModelElementRelMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class ModelElementRelService extends BaseServiceImpl<FModelElementRelDo, String> {

    @Autowired
    private ModelElementRelMapper componentElementRelMapper;

    @Override
    public Mapper<FModelElementRelDo> getMapper() {
        return componentElementRelMapper;
    }

    public List<FModelElementRelDo> selectByModelId(String modelId) {
        return componentElementRelMapper.selectByModelId(modelId);
    }
    public List<FModelElementRelDo> selectByVertexId(String vertexId) {
        return componentElementRelMapper.selectByVertexId(vertexId);
    }


}
