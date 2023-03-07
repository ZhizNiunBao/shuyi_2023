package cn.bywin.business.service.bydb;

import cn.bywin.business.bean.bydb.TTruModelElementJobDo;
import cn.bywin.business.bean.bydb.TTruModelTaskLogDo;
import cn.bywin.business.mapper.bydb.TruModelElementJobMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class BydbModelElementJobService extends BaseServiceImpl<TTruModelElementJobDo, String> {

    @Autowired
    private TruModelElementJobMapper bydbModelElementFieldMapper;

    @Override
    public Mapper<TTruModelElementJobDo> getMapper() {
        return bydbModelElementFieldMapper;
    }

    public List<TTruModelElementJobDo> findBeanList( TTruModelElementJobDo modelInfo) {
        return bydbModelElementFieldMapper.findBeanList(modelInfo);
    }

    public long findBeanCnt( TTruModelElementJobDo bean) {
        return bydbModelElementFieldMapper.findBeanCnt(bean);
    }

    public List<TTruModelElementJobDo> selectByElementId( String id) {
        return bydbModelElementFieldMapper.selectByModelId(id);
    }

    public long deleteByElementId(String elementId) {
        return bydbModelElementFieldMapper.deleteByModelId(elementId);
    }
//	public List<TTruModelTaskLogDo> findUnfinished( String modelId){
//		return bydbModelElementFieldMapper.findUnfinished( modelId );
//	}

}
