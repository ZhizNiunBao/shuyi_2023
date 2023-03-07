package cn.bywin.business.service.federal;

import cn.bywin.business.bean.federal.*;
import cn.bywin.business.bean.view.federal.FProjectVo;
import cn.bywin.business.bean.view.federal.ProjectVo;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.mapper.federal.*;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class ProjectService extends BaseServiceImpl<FProjectDo, String> {

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectDataMapper projectDataMapper;

    @Autowired
    private ProjectGuestMapper projectGuestMapper;

    @Autowired
    private DataPartyService dataPartyService;

    @Autowired
    private DataPartyMapper dataPartyMapper;
    @Autowired
    private DataApproveMapper dataApproveMapper;
    @Override
    public Mapper<FProjectDo> getMapper() {
        return projectMapper;
    }

    public List<FProjectDo> findBeanList(FProjectVo modelInfo) {
        return projectMapper.findBeanList(modelInfo);
    }

    public long findBeanCnt(FProjectVo bean) {
        return projectMapper.findBeanCnt(bean);
    }
    public List<FProjectDo> findByMemberBeanList(FProjectVo modelInfo) {
        return projectMapper.findByMemberBeanList(modelInfo);
    }

    public long findByMemberBeanCnt(FProjectVo bean) {
        return projectMapper.findByMemberBeanCnt(bean);
    }
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteByAll(String id) {
        List<FProjectDataDo> elementRels = projectDataMapper.selectByProjectId(id);
        List<FProjectGuestDo> fProjectGuestDos = projectGuestMapper.selectByProjectId(id);
        elementRels.stream().forEach(element -> projectDataMapper.deleteByPrimaryKey(element.getId()));
        fProjectGuestDos.stream().forEach(element -> projectGuestMapper.deleteByPrimaryKey(element.getId()));
        projectMapper.deleteByPrimaryKey(id);
        return getMapper().deleteByPrimaryKey(id);
    }


    @Transactional(rollbackFor = Exception.class)
    public void insertBeanByData(ProjectVo modelInfo,List<FProjectDataDo> fProjectDataHost,
                                 List<FProjectDataDo> fProjectDataGuest,List<FProjectGuestDo> fProjectGuestDos) throws Exception {

        //FNodePartyDo fNodePartyDo = nodePartyMapper.findByUserId(modelInfo.getProject().getHost());
        modelInfo.getProject().setHost(modelInfo.getProject().getHost());
        projectMapper.insert(modelInfo.getProject());
        //host
        for (FProjectDataDo host : fProjectDataHost) {
            projectDataMapper.insert(host);
        }
        //guest
        for (FProjectDataDo guest : fProjectDataGuest) {
            projectDataMapper.insert(guest);
        }
        for (FProjectGuestDo projectGuestDo : fProjectGuestDos) {
            projectGuestMapper.insert(projectGuestDo);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void insertBeanByData(ProjectVo modelInfo,List<FProjectGuestDo> fProjectGuestDos) throws Exception {

        //FNodePartyDo fNodePartyDo = nodePartyMapper.findByUserId(modelInfo.getProject().getHost());
        modelInfo.getProject().setHost(modelInfo.getProject().getHost());
        projectMapper.insert(modelInfo.getProject());
        for (FProjectGuestDo projectGuestDo : fProjectGuestDos) {
            projectGuestMapper.insert(projectGuestDo);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertBeanPms(ProjectVo modelInfo) {

        //FNodePartyDo fNodePartyDo = nodePartyMapper.findByUserId(modelInfo.getProject().getHost());
        modelInfo.getProject().setHost(modelInfo.getProject().getHost());
        projectMapper.insert(modelInfo.getProject());
        //guest
        for (FProjectGuestDo fProjectGuestDo: modelInfo.getProjectGuest()) {
            projectGuestMapper.insert(fProjectGuestDo);
        }

        //DataApprove
        for (FDataApproveDo fDataApproveDo: modelInfo.getDataApproveDos()) {
            fDataApproveDo.setId(ComUtil.genId());
            FDataPartyDo info = dataPartyMapper.selectById(fDataApproveDo.getDataId());
            if (info!=null){
                fDataApproveDo.setUserId(info.getCreatorId());
                fDataApproveDo.setUserName(info.getCreatorName());
                info.setUseCnt((info.getUseCnt() == null || info.getUseCnt() == 0) ? 1 : info.getUseCnt() + 1);
                dataPartyMapper.updateByPrimaryKey(info);
            }
            dataApproveMapper.insert(fDataApproveDo);
        }
    }
}
