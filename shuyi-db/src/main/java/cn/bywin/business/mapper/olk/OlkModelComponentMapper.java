package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkModelComponentDo;
import cn.bywin.business.bean.view.olk.TOlkModelComponentVo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkModelComponentMapper extends Mapper<TOlkModelComponentDo>, MySqlMapper<TOlkModelComponentDo> {

    List<TOlkModelComponentVo> findBeanList( TOlkModelComponentDo bean);

    long findBeanCnt( TOlkModelComponentDo bean);


}

