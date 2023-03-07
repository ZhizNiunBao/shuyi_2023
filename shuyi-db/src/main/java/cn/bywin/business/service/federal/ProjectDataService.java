//package cn.bywin.business.service.federal;
//
//import cn.bywin.business.bean.federal.FProjectDataDo;
//import cn.bywin.business.mapper.federal.ProjectDataMapper;
//import cn.service.impl.BaseServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import tk.mybatis.mapper.common.Mapper;
//
//import java.util.List;
//
//@Service
//public class ProjectDataService extends BaseServiceImpl<FProjectDataDo, String> {
//
//    @Autowired
//    private ProjectDataMapper projectDataMapper;
//
//    @Override
//    public Mapper<FProjectDataDo> getMapper() {
//        return projectDataMapper;
//    }
//
//    public List<FProjectDataDo> findBeanList(FProjectDataDo modelInfo) {
//        return projectDataMapper.findBeanList(modelInfo);
//    }
//
//    List<FProjectDataDo> selectByProjectId(String projectId) {
//        return projectDataMapper.selectByProjectId(projectId);
//    }
//
//    public long findBeanCnt(FProjectDataDo bean) {
//        return projectDataMapper.findBeanCnt(bean);
//    }
//
//    public List<FProjectDataDo> selectByProjectDataId(String projectId,String nodeId) {
//        return projectDataMapper.selectByProjectDataId(projectId,nodeId);
//    }
//    public FProjectDataDo selectByProjectNodeDataId(String projectId,String nodeId,String dataId) {
//        return projectDataMapper.selectByProjectNodeDataId(projectId,nodeId,dataId);
//    }
//
//}
