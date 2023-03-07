package cn.bywin.business.service.olk;

import cn.bywin.business.bean.olk.TOlkModelElementJobDo;
import cn.bywin.business.mapper.olk.OlkModelElementJobMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class OlkModelElementJobService extends BaseServiceImpl<TOlkModelElementJobDo, String> {

    @Autowired
    private OlkModelElementJobMapper bydbModelElementFieldMapper;

    @Override
    public Mapper<TOlkModelElementJobDo> getMapper() {
        return bydbModelElementFieldMapper;
    }

    public List<TOlkModelElementJobDo> findBeanList( TOlkModelElementJobDo modelInfo) {
        return bydbModelElementFieldMapper.findBeanList(modelInfo);
    }

    public long findBeanCnt( TOlkModelElementJobDo bean) {
        return bydbModelElementFieldMapper.findBeanCnt(bean);
    }

    public List<TOlkModelElementJobDo> selectByElementId( String id) {
        return bydbModelElementFieldMapper.selectByModelId(id);
    }

    public long deleteByElementId(String elementId) {
        return bydbModelElementFieldMapper.deleteByModelId(elementId);
    }
//	public List<TOlkModelTaskLogDo> findUnfinished( String modelId){
//		return bydbModelElementFieldMapper.findUnfinished( modelId );
//	}

}
