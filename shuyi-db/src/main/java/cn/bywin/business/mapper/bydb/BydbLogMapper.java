package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TBydbLogDo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface BydbLogMapper extends Mapper<TBydbLogDo>, MySqlMapper<TBydbLogDo> {

    List<TBydbLogDo> findBeanList(TBydbLogDo bean);

    long findBeanCnt(TBydbLogDo bean);


}