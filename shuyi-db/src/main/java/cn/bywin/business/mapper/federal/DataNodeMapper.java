package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FDataNodeDo;
import cn.bywin.business.bean.federal.FDataPartyDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface DataNodeMapper extends Mapper<FDataNodeDo>, MySqlMapper<FDataNodeDo> {


    List<FDataNodeDo> findBeanList(FDataPartyDo bean);

    long findBeanCnt(FDataNodeDo bean);


    @Select("DELETE  FROM fl_data_node WHERE data_id = #{dataId}")
    void deleteByDataId(@Param("dataId") String dataId);


    @Select(value = "select count(*)  from  fl_data_node where node_id=#{nodeId} and data_id=#{dataId}")
    long checkId(@Param("nodeId")String nodeId, @Param("dataId")String dataId);

    @Select("select *   FROM fl_data_node WHERE data_id = #{dataId}")
    List<FDataNodeDo> findByDataId(@Param("dataId") String dataId);


}

