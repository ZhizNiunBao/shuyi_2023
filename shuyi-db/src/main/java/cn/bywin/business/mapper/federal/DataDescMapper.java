package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FDataPartyDo;
import cn.bywin.business.bean.federal.FlDataDescDo;
import cn.bywin.business.bean.view.federal.FDataPartyVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface DataDescMapper extends Mapper<FlDataDescDo>, MySqlMapper<FlDataDescDo> {


    @Select( "select * from fl_data_desc where data_id =#{dataId} order by eda_order")
    List<FlDataDescDo> selectByDataId(@Param("dataId") String dataId);

    List<FlDataDescDo> findBeanList(FlDataDescDo bean);

    long findBeanCnt(FlDataDescDo bean);

    @Select("DELETE  FROM fl_data_desc WHERE data_id = #{dataId}")
    void deleteByDataId(@Param("dataId") String dataId);

}

