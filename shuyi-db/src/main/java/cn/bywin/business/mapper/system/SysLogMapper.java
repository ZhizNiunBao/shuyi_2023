package cn.bywin.business.mapper.system;

import cn.bywin.business.bean.system.SysLogDo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface SysLogMapper extends Mapper<SysLogDo>,MySqlMapper<SysLogDo> {

        List<SysLogDo> findBeanList(SysLogDo bean);
        long findBeanCnt(SysLogDo bean);

}