package cn.bywin.business.service.olk;


import cn.bywin.business.bean.olk.TOlkDataNodeDo;
import cn.bywin.business.mapper.olk.OlkDataNodeMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class OlkDataNodeService extends BaseServiceImpl<TOlkDataNodeDo, String> {

    @Autowired
    private OlkDataNodeMapper commMapper;

    @Override
    public Mapper<TOlkDataNodeDo> getMapper() {
        return commMapper;
    }

    public List<TOlkDataNodeDo> findBeanList( TOlkDataNodeDo bean ) {
        return commMapper.findBeanList( bean );
    }

    public long findBeanCnt( TOlkDataNodeDo bean ) {
        return commMapper.findBeanCnt( bean );
    }

    public List<TOlkDataNodeDo> findByDataId( String dataId ) {
        return commMapper.findByDataId( dataId );
    }

    @Transactional(rollbackFor = Exception.class)
    public long delByDataId( String dataId ) {
        return commMapper.delByDataId( dataId );
    }

}
