package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TTruModelComponentDo;
import cn.bywin.business.bean.view.bydb.TTruModelComponentVo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface TruModelComponentMapper extends Mapper<TTruModelComponentDo>, MySqlMapper<TTruModelComponentDo> {

    List<TTruModelComponentVo> findBeanList( TTruModelComponentDo bean);

    long findBeanCnt( TTruModelComponentDo bean);


}

