package cn.bywin.business.service.federal;

import cn.bywin.business.bean.federal.FDataNodeDo;
import cn.bywin.business.bean.federal.FDataPartyDo;
import cn.bywin.business.bean.federal.FModelDataDo;
import cn.bywin.business.bean.federal.FlDataDescDo;
import cn.bywin.business.bean.view.federal.DataOrderVo;
import cn.bywin.business.bean.view.federal.FDataPartyVo;
import cn.bywin.business.mapper.federal.DataDescMapper;
import cn.bywin.business.mapper.federal.DataNodeMapper;
import cn.bywin.business.mapper.federal.DataPartyMapper;
import cn.bywin.business.mapper.federal.ModelDataMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class DataPartyService extends BaseServiceImpl<FDataPartyDo, String> {

    @Autowired
    private DataPartyMapper dataPartyMapper;
    @Autowired
    private ModelDataMapper modelDataMapper;
    @Autowired
    private DataNodeMapper dataNodeMapper;
    @Autowired
    private DataDescMapper dataDescMapper;

    @Override
    public Mapper<FDataPartyDo> getMapper() {
        return dataPartyMapper;
    }


    public List<FModelDataDo> selectByModelNoDeId(String modelId,String nodeId,Integer types) {
        return modelDataMapper.selectByModelNoDeId(modelId,nodeId,types);
    }

//    public List<FDataPartyDo> selectByProjectId(List<String> ids, String projectId, String model) {
//        if (model == null) {
//            return dataPartyMapper.selectByProjectId(ids, projectId);
//        }
//        List<FModelDataDo> list = modelDataMapper.selectByModelId(model);
//        if (list != null && list.size() > 0) {
//            return dataPartyMapper.selectByModelId(ids, model);
//        } else {
//            return dataPartyMapper.selectByProjectId(ids, projectId);
//        }
//    }
    public List<FDataPartyDo> selectByModelId(List<String> ids, String model,Integer types ) {
        return dataPartyMapper.selectByModelId(ids, model,types);
    }
    public List<FDataPartyDo> findBeanList(FDataPartyDo modelInfo) {
        return dataPartyMapper.findBeanList(modelInfo);
    }

    public long findBeanCnt(FDataPartyDo bean) {
        return dataPartyMapper.findBeanCnt(bean);
    }


    public List<FDataPartyVo> findBeanFlList(FDataPartyVo modelInfo) {
        return dataPartyMapper.findBeanFlList(modelInfo);
    }

    public long findBeanFlCnt(FDataPartyVo bean) {
        return dataPartyMapper.findBeanFlCnt(bean);
    }

    public List<FDataPartyDo> findBeanProjectList(FDataPartyVo modelInfo) {
        return dataPartyMapper.findBeanProjectList(modelInfo);
    }

    public long findBeanProjectCnt(FDataPartyVo bean) {
        return dataPartyMapper.findBeanProjectCnt(bean);
    }

    public FDataPartyVo findUseCnt(String id) {
        return dataPartyMapper.findUseCnt(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteById(String id) {
        modelDataMapper.deleteByDataId(id);
        dataNodeMapper.deleteByDataId(id);
        dataDescMapper.deleteByDataId(id);

        return getMapper().deleteByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer deleteByPmsId(String id) {
        dataNodeMapper.deleteByDataId(id);
        return getMapper().deleteByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertBeanDetail(FDataPartyDo fDataPartyDo, List<FDataNodeDo> nodes,List<FlDataDescDo> flDataDescDoList) {
        dataPartyMapper.insert(fDataPartyDo);
        for (FDataNodeDo dataNodeDo : nodes) {
            dataNodeMapper.insert(dataNodeDo);
        }
        if( flDataDescDoList != null){
            for ( FlDataDescDo flDataDescDo : flDataDescDoList ) {
                dataDescMapper.insert(flDataDescDo);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBeanDetail(FDataPartyDo info,List<FDataNodeDo> delList, List<FDataNodeDo> addList,List<FlDataDescDo> tzAddList,List<FlDataDescDo> tzModList,List<FlDataDescDo> tzDelList) {


        dataPartyMapper.updateByPrimaryKey(info);
        if (delList != null) {
            for (FDataNodeDo fDataNodeDo : delList) {
                dataNodeMapper.deleteByPrimaryKey(fDataNodeDo.getId());
            }
        }
        if (addList != null) {
            for (FDataNodeDo fDataNodeDo : addList) {
                dataNodeMapper.insert(fDataNodeDo);
            }
        }
        if(tzDelList!=null && tzDelList.size()>0){
            for ( FlDataDescDo flDataDescDo : tzDelList ) {
                dataDescMapper.deleteByDataId( flDataDescDo.getId() );
            }
        }
        if(tzModList!=null && tzModList.size()>0){
            for ( FlDataDescDo flDataDescDo : tzModList ) {
                dataDescMapper.updateByPrimaryKey( flDataDescDo );
            }
        }
        if(tzAddList!=null && tzAddList.size()>0){
            for ( FlDataDescDo flDataDescDo : tzAddList ) {
                dataDescMapper.insert( flDataDescDo );
            }
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void insertBeanDetail(FDataPartyVo fDataPartyDo) {
        dataPartyMapper.insert(fDataPartyDo);
        if (fDataPartyDo.getNodeList() != null && fDataPartyDo.getNodeList().size() > 0) {
            for (FDataNodeDo fDataNodeDo : fDataPartyDo.getNodeList()) {
                dataNodeMapper.insert(fDataNodeDo);
            }
        }
        List<FlDataDescDo> tzAddList = fDataPartyDo.getTzAddList();
        if(tzAddList!=null && tzAddList.size()>0){
            for ( FlDataDescDo flDataDescDo : tzAddList ) {
                dataDescMapper.insert( flDataDescDo );
            }
        }
    }


    public List<FDataPartyDo> findByAllIds(List<String> ids) {
        return dataPartyMapper.findByAllIds(ids);
    }
    public List<FDataPartyVo> findByAllIdsDetail(List<String> ids) {
        return dataPartyMapper.findByAllIdsDetail(ids);
    }
    public List<FDataNodeDo> findByDataId(String dataId) {
        return dataNodeMapper.findByDataId(dataId);
    }

    public List<DataOrderVo> findDataOrder(FDataPartyVo bean){
        return dataPartyMapper.findDataOrder(bean);
    }

    public long findDataOrderCnt(FDataPartyVo bean) {
        return dataPartyMapper.findDataOrderCnt(bean);
    }


    public List<DataOrderVo> findDataOrderTree(FDataPartyVo bean){
        return dataPartyMapper.findDataOrderTree(bean);
    }

    public long findDataOrderTreeCnt(FDataPartyVo bean) {
        return dataPartyMapper.findDataOrderTreeCnt(bean);
    }
}
