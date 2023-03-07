package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.view.federal.FComponentVo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface ComponentMapper extends Mapper<FComponentDo>, MySqlMapper<FComponentDo> {

    List<FComponentDo> findBeanList(FComponentDo bean);

    List<FComponentVo> findTreeList(FComponentDo bean);

    long findBeanCnt(FComponentDo bean);


}

