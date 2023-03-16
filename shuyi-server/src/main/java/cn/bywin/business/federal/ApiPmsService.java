package cn.bywin.business.federal;

import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.federal.FDataPartyDo;
import cn.bywin.business.bean.federal.FModelDataDo;
import cn.bywin.business.bean.federal.FModelDo;
import cn.bywin.business.bean.federal.FModelJobDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.federal.FProjectDataDo;
import cn.bywin.business.bean.federal.FProjectDo;
import cn.bywin.business.bean.federal.FProjectGuestDo;
import cn.bywin.business.bean.federal.PmsResult;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.view.federal.DataOrderVo;
import cn.bywin.business.bean.view.federal.FDataApproveVo;
import cn.bywin.business.bean.view.federal.FDataPartyVo;
import cn.bywin.business.bean.view.federal.FNodePartyVo;
import cn.bywin.business.bean.view.federal.FProjectVo;
import cn.bywin.business.bean.view.federal.ProjectVo;
import cn.bywin.config.FeignConfiguration;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description
 * @Author wangh
 * @Date 2022-01-15
 */
@FeignClient(name = "pmsService", url = "${pmServerUrl}", configuration = FeignConfiguration.class)
public interface ApiPmsService {

    /**
     * 节点同步到pms服务
     *
     * @param fNodePartyDo 节点信息
     * @return
     */
    @PostMapping("syncnode")
    PmsResult syncNode(@RequestBody FNodePartyDo fNodePartyDo);

    /**
     * 用户信息同步到pms服务
     *
     * @param userDo 用户信息
     * @return
     */
    @PostMapping("syncuser")
    PmsResult syncUser(@RequestBody SysUserDo userDo, @RequestParam("token") String token);

    /**
     * 数据集同步到pms
     *
     * @param info 数据集信息
     */
    @PostMapping("syncdata")
    PmsResult syncData(@RequestBody FDataPartyVo info, @RequestParam("token") String token);

    /**
     * 项目同步到pms
     *
     * @param info 项目信息
     */
    @PostMapping("syncproject")
    PmsResult syncProject(@RequestBody ProjectVo info);

    /**
     * 项目删除同步到pms
     *
     * @param info
     */
    @PostMapping("delproject")
    PmsResult delProject(@RequestBody FProjectDo info);

    /**
     * 项目合作方同步到pms
     *
     * @param info 项目信息
     */
    @PostMapping("syncprojectguest")
    PmsResult syncProjectGuest(@RequestBody FProjectGuestDo info);

    /**
     * 删除项目协助方同步到pms
     *
     * @param info 项目信息
     */
    @PostMapping("delprojectguest")
    PmsResult delProjectGuest(@RequestBody FProjectGuestDo info);

    /**
     * 模型同步到pms
     *
     * @param info 模型信息
     */
    @PostMapping("syncmodel")
    PmsResult syncModel(@RequestBody FModelDo info, @RequestParam("token") String token);

    /**
     * 模型删除同步到pms
     *
     * @param info 模型
     */
    @PostMapping("delmodel")
    PmsResult delModel(@RequestBody FModelDo info);


    /**
     * 模型记录同步到pms
     *
     * @param info 项目信息
     */
    @PostMapping("syncmodeljob")
    PmsResult syncModelJob(FModelJobDo info, @RequestParam("token") String token);


    /**
     * 模型记录删除同步到pms
     *
     * @param info 项目信息
     */
    @PostMapping("delmodeljob")
    PmsResult delModelJob(FModelJobDo info);


    /**
     * 审批同步到pms
     *
     * @param fDataApproveDo 审批信息
     */
    @PostMapping("syncapprove")
    PmsResult syncApprove(FDataApproveDo fDataApproveDo);

    /**
     * 批量添加审批数据同步到pms
     *
     * @param fDataApproveDo 审批信息
     */
    @PostMapping("syncapproves")
    PmsResult syncApproves(FDataApproveVo fDataApproveDo);

    /**
     * 审批删除同步到pms服务
     *
     * @param bean 审批
     */
    @PostMapping("delapproveid")
    PmsResult delApprove(FDataApproveDo bean);

    /**
     * 移除项目数据同步到pms服务
     *
     * @param info
     */
    @PostMapping("delapprove")
    PmsResult delApproveByDataProjectId(@RequestBody FProjectDataDo info);

    /**
     * 数据集删除同步到pms服务
     *
     * @param info 数据信息
     */
    @PostMapping("deldata")
    PmsResult delData(@RequestBody FDataPartyDo info);

    /**
     * @return 从pms获取当前节点信息
     */
    @PostMapping("pmsinfo")
    Object pmsinfo(@RequestBody FNodePartyDo fNodePartyDo);

    @PostMapping("details")
    Object details(@RequestBody FProjectDo info);

    /**
     * @return 从pms获取当前项目协助方数据
     */
    @PostMapping("memberpage")
    Object memberpage(@RequestBody FProjectVo info);

    /**
     * @return 从pms获取当前项目协助方模型流程数据
     */
    @PostMapping("membermodel")
    Object memberModel(@RequestBody FModelDo info);


    /**
     * @return 从pms获取当前项目协助方模型流程数据
     */
    @PostMapping("membermodeljob")
    Object memberModelJob(@RequestBody FModelJobDo info);

    /**
     * 从pms获取节点列表
     *
     * @param info
     * @return Object
     */
    @PostMapping("nodelist")
    Object nodelist(@RequestBody FNodePartyDo info);

    /**
     * 从pms获取数据集列表
     *
     * @param info 数据集信息
     * @return Object
     */
    @PostMapping("federaldata")
    Object federalData(@RequestBody FDataPartyVo info);

    /**
     * 根据id从pms获取节点
     *
     * @param info
     * @return List<FNodePartyDo>
     */
    @PostMapping("nodebyids")
    List<FNodePartyDo> nodeByIds(@RequestBody FNodePartyVo info);

    /**
     * 根据id从pms获取节点标识码
     *
     * @param info
     * @return List<Integer>
     */
    @PostMapping("partybyids")
    List<Integer> partyByIds(@RequestBody FNodePartyVo info);

    /**
     * 根据id从pms获取数据集
     *
     * @param info
     * @return List<FDataPartyDo>
     */
    @PostMapping("databyids")
    List<FDataPartyDo> dataByIds(@RequestBody FNodePartyVo info);


    @PostMapping("databyidsdetail")
    List<FDataPartyVo> dataByIdsDetail(@RequestBody FNodePartyVo dataPartyVo);

    /**
     * 根据id从pms获取数据集和特征
     *
     * @param info
     * @return FDataPartyVo
     */
    @PostMapping("dataPartyById")
    FDataPartyVo dataPartyById(@RequestBody FDataPartyVo info);

    /**
     * 根据id从pms获取数据集
     *
     * @param info
     * @return FModelJobDo
     */
    @PostMapping("modeljobbyid")
    FModelJobDo modelJobById(@RequestBody FModelJobDo info);

    /**
     * 根据id从pms获取数据集
     *
     * @param bean
     * @return FDataApproveDo
     */
    @PostMapping("byprojectnodedataid")
    FDataApproveDo byProjectNodeDataId(@RequestBody FDataApproveDo bean);

    /**
     * 根据id从pms获取数据集
     *
     * @param bean
     * @return List<FDataApproveDo>
     */
    @PostMapping("byprojectnodedataids")
    List<FDataApproveDo> byProjectNodeDataIds(@RequestBody FDataApproveVo bean);

    /**
     * 根据id从pms获取协助项目信息
     *
     * @param info
     * @return FProjectDo
     */
    @PostMapping("byprojectid")
    FProjectDo byProjectIid(@RequestBody FProjectDo info);

    /**
     * 根据id从pms获取协助流程信息
     *
     * @param info
     * @return FModelDo
     */
    @PostMapping("bymodelid")
    FModelDo byModelIid(@RequestBody FModelDo info);

    /**
     * 从pms获取项目审批数据
     *
     * @param info
     * @return List<FDataApproveDo>
     */
    @PostMapping("byprojectnodeid")
    List<FDataApproveDo> byProjectNodeId(@RequestBody FDataPartyVo info);

    /**
     * 从pms获取审批列表
     *
     * @param info
     * @return Object
     */
    @PostMapping("getapprove")
    Object getApprove(@RequestBody FDataApproveVo info);

    @PostMapping("dataorder")
    Object findDataOrder(@RequestBody FDataPartyVo partyDo);

    @PostMapping("dataordertree")
    Object findDataOrderTree(@RequestBody FDataPartyVo partyDo);

    @PostMapping("modelordertree")
    Object findModelOrderTree(@RequestBody DataOrderVo partyDo);

    @PostMapping("syncmodeldata")
    PmsResult syncModelData(@RequestBody List<FModelDataDo> addModelData);
}
