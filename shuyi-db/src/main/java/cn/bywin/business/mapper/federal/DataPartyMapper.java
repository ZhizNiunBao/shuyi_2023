package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FDataPartyDo;
import cn.bywin.business.bean.view.federal.DataOrderVo;
import cn.bywin.business.bean.view.federal.FDataPartyVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface DataPartyMapper extends Mapper<FDataPartyDo>, MySqlMapper<FDataPartyDo> {

    List<FDataPartyDo> selectByProjectId(@Param("ids") List<String> ids, @Param("projectId") String projectId);

    List<FDataPartyDo> selectByModelId(@Param("ids") List<String> ids, @Param("modelId") String modelId, @Param("types") Integer types);

    List<FDataPartyDo> findBeanList(FDataPartyDo bean);

//    List<DataOrderVo> findDataOrder(@Param("ids") List<String> ids, @Param("creatorId") String creatorId,@Param("name") String name);
//
//    long findDataOrderCnt(@Param("ids") List<String> ids, @Param("creatorId") String creatorId,@Param("name") String name);


    List<DataOrderVo> findDataOrder(FDataPartyVo bean);

    long findDataOrderCnt(FDataPartyVo bean);

    List<DataOrderVo> findDataOrderTree(FDataPartyVo bean);

    long findDataOrderTreeCnt(FDataPartyVo bean);

    long findBeanCnt(FDataPartyDo bean);

    List<FDataPartyVo> findBeanFlList(FDataPartyVo bean);

    long findBeanFlCnt(FDataPartyVo bean);

    long findBeanProjectCnt(FDataPartyVo bean);

    List<FDataPartyDo> findBeanProjectList(FDataPartyVo modelInfo);

    List<FDataPartyDo> findByAllIds(@Param("ids") List<String> ids);

    @Select("select  * from  fl_data_party where id =#{id} ")
    FDataPartyDo selectById(@Param("id") String id);

    @Select("select  data_id ,sum( types) approve , count(distinct project_id) use_cnt  from fl_data_approve  where data_id =#{id} and approve != 9 \n" +
            " \n" +
            " group by data_id  ")
    FDataPartyVo findUseCnt(@Param("id") String id);

    List<FDataPartyVo> findByAllIdsDetail(@Param("ids") List<String> ids);
}

