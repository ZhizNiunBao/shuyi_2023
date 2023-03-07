package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.TBydbDataNodeDo;
import cn.bywin.business.mapper.bydb.BydbDataNodeMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service
public class BydbDataNodeService extends BaseServiceImpl<TBydbDataNodeDo, String> {

    @Autowired
    private BydbDataNodeMapper commMapper;

    @Override
    public Mapper<TBydbDataNodeDo> getMapper() {
        return commMapper;
    }

    public List<TBydbDataNodeDo> findBeanList( TBydbDataNodeDo bean ) {
        return commMapper.findBeanList( bean );
    }

    public long findBeanCnt( TBydbDataNodeDo bean ) {
        return commMapper.findBeanCnt( bean );
    }

    public List<TBydbDataNodeDo> findByDataId( String dataId ) {
        return commMapper.findByDataId( dataId );
    }

    @Transactional(rollbackFor = Exception.class)
    public long delByDataId( String dataId ) {
        return commMapper.delByDataId( dataId );
    }

}
