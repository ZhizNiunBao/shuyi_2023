package cn.bywin.business.service.federal;

import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.view.federal.FDataApproveVo;
import cn.bywin.business.common.enums.ApproveStatus;
import cn.bywin.business.mapper.federal.DataApproveMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class DataApproveService extends BaseServiceImpl<FDataApproveDo, String> {

    @Autowired
    private DataApproveMapper dataApproveMapper;

    @Override
    public Mapper<FDataApproveDo> getMapper() {
        return dataApproveMapper;
    }

    public List<FDataApproveVo> findBeanList(FDataApproveVo bean) {
        return dataApproveMapper.findBeanList(bean);
    }
    public List<FDataApproveVo> findBydbBeanList(FDataApproveVo bean) {
        return dataApproveMapper.findBydbBeanList(bean);
    }
    public List<FDataApproveVo> findOlkBeanList(FDataApproveVo bean) {
        return dataApproveMapper.findOlkBeanList(bean);
    }
    public long findBeanCnt(FDataApproveVo bean) {
        return dataApproveMapper.findBeanCnt(bean);
    }


    public List<FDataApproveDo> selectByProjectDataId(String projectId, String nodeId,Integer types){
        return dataApproveMapper.selectByProjectDataId(projectId,nodeId,types);
    }
    public  FDataApproveDo selectByProjectNodeDataId( String projectId, String nodeId,  String dataId){
        return dataApproveMapper.selectByProjectNodeDataId(projectId,nodeId,dataId);
    }


    public void deleteByProjectId(String projectId) {
         dataApproveMapper.deleteByProjectId(projectId);
    }

    public void deleteByProjectDataId(String projectId, String dataId) {
        dataApproveMapper.deleteByProjectDataId(projectId,dataId);
    }

    public List<FDataApproveDo> selectByProjectNodeDataIds(String projectId, String nodeId, List<String> dataIds) {
        return dataApproveMapper.selectByProjectDataIds(projectId,nodeId,dataIds);
    }

    public  List<FDataApproveDo> selectApproveByUserDataId( String userId,  String dataIds){
        return dataApproveMapper.selectApproveByUserDataId(userId,dataIds);
    }

    @Transactional(rollbackFor = Exception.class)
    public  Integer saveAndUpdateBeans( List<FDataApproveDo> addList,  List<FDataApproveDo> modList){
        if( modList != null ){
            for ( FDataApproveDo approveDo : modList ) {
                dataApproveMapper.updateByPrimaryKey( approveDo );
            }
        }
        if( addList != null ){
            for ( FDataApproveDo approveDo : addList ) {
                dataApproveMapper.insert( approveDo );
            }
        }
        return 1;
    }

    /**
     * 判断用户是否申请过指定资源
     * @param userId   用户Id
     * @param dataId   资源Id
     * @return         是否申请过
     */
    public boolean hasApprove(String userId, String dataId) {
        FDataApproveDo queryInfo = new FDataApproveDo();
        queryInfo.setCreatorId(userId);
        queryInfo.setDataId(dataId);
        queryInfo.setApprove(ApproveStatus.PASS);
        int count = dataApproveMapper.selectCount(queryInfo);
        return count > 0;
    }
}
