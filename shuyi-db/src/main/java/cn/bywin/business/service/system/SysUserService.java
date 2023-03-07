package cn.bywin.business.service.system;

import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.system.SysRoleDo;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.system.SysUserRoleDo;
import cn.bywin.business.bean.view.UserVo;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.mapper.federal.NodePartyMapper;
import cn.bywin.business.mapper.system.SysUserMapper;
import cn.bywin.business.mapper.system.SysUserRoleMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

import static cn.bywin.business.common.util.Constants.FLSYSTEM;


@Service("SysUserService")
public class SysUserService extends BaseServiceImpl<SysUserDo, String> {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private NodePartyMapper nodePartyMapper;

    @Override
    public Mapper<SysUserDo> getMapper() {
        return sysUserMapper;
    }

    public List<SysUserDo> findBeanList(SysUserDo bean) {
        return sysUserMapper.findBeanList(bean);
    }

    public long findBeanCnt(SysUserDo bean) {
        return sysUserMapper.findBeanCnt(bean);
    }


    public long findAllByRoleCnt(SysUserDo sysUserDo) {
        return sysUserMapper.findAllByRoleCnt(sysUserDo);
    }

    public int checkName(SysUserDo bean) {
        return sysUserMapper.checkName(bean);
    }


    @Transactional
    public Integer insertUser(SysUserDo bean) {
        SysUserDo sysUserDo = new SysUserDo();
        sysUserDo.setMobile(bean.getMobile());
        long cnt = sysUserMapper.findBeanCnt(sysUserDo);
        if (cnt > 0) {
            return 0;
        }
        String id = ComUtil.genId();
        bean.setId(id);
        List<SysUserDo> all = sysUserMapper.selectAll();
        bean.setCreatedTime(ComUtil.getCurTimestamp());
        bean.setModifiedTime(ComUtil.getCurTimestamp());
        bean.setRegTime(ComUtil.getCurTimestamp());
        SysUserRoleDo sysUserRoleDo = new SysUserRoleDo();
        if (all == null || all.size() == 0) {
            FNodePartyDo fNodePartyDo=new FNodePartyDo();
            fNodePartyDo.setId(ComUtil.genId());
            fNodePartyDo.setIsOpen(1);
            fNodePartyDo.setIcon("flhub");
            fNodePartyDo.setIsStatus(0);
            fNodePartyDo.setName(bean.getUsername());
            fNodePartyDo.setMobile(bean.getMobile());
            fNodePartyDo.setEmail(bean.getEmail());
            nodePartyMapper.insert(fNodePartyDo);
            bean.setIsLock(1);
            sysUserRoleDo.setId(ComUtil.genId());
            sysUserRoleDo.setUserId(id);
            sysUserRoleDo.setRoleId(FLSYSTEM);
        } else {
            bean.setIsLock(0);
            sysUserRoleDo.setId(ComUtil.genId());
            sysUserRoleDo.setUserId(id);
            sysUserRoleDo.setRoleId("normal");
        }
        sysUserMapper.insert(bean);
        sysUserRoleMapper.insert(sysUserRoleDo);
        return 1;
    }

    public SysRoleDo getRole(String userId) {
        return sysUserRoleMapper.getRole(userId);
    }

    public SysUserRoleDo getSystemRole(String userId) {
        return sysUserRoleMapper.getSystemRole(userId);
    }

    public List<SysUserDo> getSuperAdminUser() {
        return sysUserRoleMapper.getSuperAdminUser();
    }

    public List<SysUserDo> getCommonUser() {
        return sysUserRoleMapper.getCommonUser();
    }

    @Transactional
    public void updateSystemPower(String systemId, String userId) {
        SysUserRoleDo old = sysUserRoleMapper.getSystemRole(systemId);
        SysUserRoleDo news = sysUserRoleMapper.getSystemRole(userId);
        news.setUserId(systemId);
        old.setUserId(userId);
        sysUserRoleMapper.updateByPrimaryKey(news);
        sysUserRoleMapper.updateByPrimaryKey(old);
    }

//    @Transactional
//    public void updateRoleAdmin(String userId, String roleId) {
//        SysUserRoleDo info = sysUserRoleMapper.getSystemRole(userId);
//        info.setRoleId(roleId);
//        sysUserRoleMapper.updateByPrimaryKey(info);
//    }

    @Transactional
    public Integer deleteById(String userId) {
        sysUserRoleMapper.deleteByUserId(userId);

        return sysUserMapper.deleteByPrimaryKey(userId);
    }

    public List<SysRoleDo> findRoleList() {
        return sysUserMapper.findRoleList();
    }

    public SysUserDo getSystemUser(String roleId) {
        return sysUserMapper.getSystemUser(roleId);
    }


    public List<UserVo> findAllByRole(SysUserDo sysUserDo) {

        return sysUserMapper.findAllByRole(sysUserDo);
    }
}
