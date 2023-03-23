package cn.bywin.business.mapper.federal;

import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.view.federal.NodePartyView;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface NodePartyMapper extends Mapper<FNodePartyDo>, MySqlMapper<FNodePartyDo> {

    List<FNodePartyDo> findBeanList(FNodePartyDo bean);

    long findBeanCnt(FNodePartyDo bean);

    List<NodePartyView> findNodePartyViewList( FNodePartyDo bean);

    @Select("SELECT * FROM fl_node_party WHERE party_id = #{partyId}")
    FNodePartyDo findByPartId(@Param("partyId") String partyId);

//    @Select("SELECT * FROM fl_node_party n JOIN fl_user_party u " +
//            "           ON n.id = u.node_id AND  u.user_name = #{userName}")
//    FNodePartyDo findBUserName(@Param("userName") String userName);


    @Select("SELECT n.* FROM fl_node_party n JOIN fl_project_guest p " +
            "           ON n.id = p.node_id AND  p.project_id = #{projectId}")
    List<FNodePartyDo> findByProjectId(@Param("projectId") String projectId);

    List<FNodePartyDo> findByAllIds(@Param("ids") List<String> ids);
}

