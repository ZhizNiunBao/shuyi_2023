package cn.bywin.business.service.federal;

import cn.bywin.business.bean.federal.FProjectDataDo;
import cn.bywin.business.bean.federal.FProjectGuestDo;
import cn.bywin.business.mapper.federal.ProjectDataMapper;
import cn.bywin.business.mapper.federal.ProjectGuestMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class ProjectGuestService extends BaseServiceImpl<FProjectGuestDo, String> {

    @Autowired
    private ProjectGuestMapper projectGuestMapper;

    @Autowired
    private ProjectDataMapper projectDataMapper;

    @Override
    public Mapper<FProjectGuestDo> getMapper() {
        return projectGuestMapper;
    }

    public List<FProjectGuestDo> findBeanList(FProjectGuestDo modelInfo) {
        return projectGuestMapper.findBeanList(modelInfo);
    }

    public List<FProjectGuestDo> selectByProjectId(String projectId) {
        return projectGuestMapper.selectByProjectId(projectId);
    }

    public void deleteByProjectDataId(String projectId,String nodeId) {
         projectGuestMapper.deleteByProjectDataId(projectId,nodeId);
    }

    public long findBeanCnt(FProjectGuestDo bean) {
        return projectGuestMapper.findBeanCnt(bean);
    }

    public List<String> findByIds(String projectId) {
        return projectGuestMapper.findByIds(projectId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAndDataById(FProjectGuestDo info) {

        List<FProjectDataDo> fProjectDataDoList = projectDataMapper.selectByProjectAndNodeId(info.getProjectId(),info.getNodeId());
        for (FProjectDataDo fProjectDataDo : fProjectDataDoList) {
            projectDataMapper.deleteByPrimaryKey(fProjectDataDo.getId());
        }
        projectGuestMapper.deleteByPrimaryKey(info.getId());
    }
}
