package cn.bywin.business.service.federal;

import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.view.federal.NodePartyView;
import cn.bywin.business.mapper.federal.NodePartyMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class NodePartyService extends BaseServiceImpl<FNodePartyDo, String> {

    @Autowired
    private NodePartyMapper nodePartyMapper;

    @Override
    public Mapper<FNodePartyDo> getMapper() {
        return nodePartyMapper;
    }

    public List<FNodePartyDo> findBeanList(FNodePartyDo modelInfo) {
        return nodePartyMapper.findBeanList(modelInfo);
    }

    public List<NodePartyView> findVNodePartyList( FNodePartyDo modelInfo) {
        return nodePartyMapper.findNodePartyViewList(modelInfo);
    }

    public List<FNodePartyDo> findByProjectId(String projectId) {
        return nodePartyMapper.findByProjectId(projectId);
    }

    public long findBeanCnt(FNodePartyDo bean) {
        return nodePartyMapper.findBeanCnt(bean);
    }

    public FNodePartyDo findByPartId(String partyId) {
        return nodePartyMapper.findByPartId(partyId);
    }

    public List<String> findByIds(List<String> userId) {
        return nodePartyMapper.findByIds(userId);
    }

    public FNodePartyDo findFirst() {
        List<FNodePartyDo> fNodePartyDos = nodePartyMapper.selectAll();
        if( fNodePartyDos== null)
            return null;
        return fNodePartyDos.get( 0 );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteById(String id) {
        nodePartyMapper.deleteByPrimaryKey(id);
        return getMapper().deleteByPrimaryKey(id);
    }

    public List<FNodePartyDo> findByAllIds(List<String> ids) {
        return nodePartyMapper.findByAllIds(ids);
    }
}
