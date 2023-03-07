package cn.bywin.business.service.system;

import cn.bywin.business.bean.system.SysLogDo;
import cn.bywin.business.bean.system.SysRoleDo;
import cn.bywin.business.bean.system.SysUserRoleDo;
import cn.bywin.business.bean.view.UserVo;
import cn.bywin.business.mapper.system.SysLogMapper;
import cn.bywin.business.mapper.system.SysRoleMapper;
import cn.bywin.business.mapper.system.SysUserRoleMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Service("SysLogService")
public class SysLogService extends BaseServiceImpl<SysLogDo, String> {

    @Autowired
    private SysLogMapper sysLogMapper;

    @Override
    public Mapper<SysLogDo> getMapper() {
        return sysLogMapper;
    }

    public List<SysLogDo> findBeanList(SysLogDo bean) {
        return sysLogMapper.findBeanList(bean);
    }

    public long findBeanCnt(SysLogDo bean) {
        return sysLogMapper.findBeanCnt(bean);
    }

}
