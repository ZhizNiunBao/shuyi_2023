package cn.bywin.business.controller;

import cn.bywin.business.bean.federal.*;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.view.federal.*;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.service.federal.*;
import cn.bywin.business.service.system.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 联邦节点信息交换管理项目构建
 * @Author wangh
 * @Date 2022-01-05
 */
@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        maxAge = 3600)
@RestController
@Api(tags = "联邦节点信息交换理")
@RequestMapping("/transform")
public class TransformController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${workMode:}")
    private Integer WORKMODE = 0;
    @Value("${backend:}")
    private Integer BACKEND = 0;
    @Value("${server_config:}")
    private String SERVER_CONFIG = "";
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private NodePartyService nodePartyService;
    @Autowired
    private DataPartyService dataPartyService;
    @Autowired
    private DataDescService dataDescService;
    @Autowired
    private DataApproveService dataApproveService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectGuestService projectGuestService;
    @Autowired
    private ModelService modelService;
    @Autowired
    private ModelJobService modelJobService;

    @ApiOperation(value = "联邦数据集列表", notes = "联邦数据集列表")
    @RequestMapping(value = "/federaldata", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "type为 1 代表获取我的数据集", dataType = "int", required = true, paramType = "query", example = "")
    })
    public Object federalData(@RequestBody FDataPartyVo info) {
        ResponeMap result = genResponeMap();
        try {

            MyBeanUtils.chgBeanLikeProperties(info, "name", "qryCond");
            info.genPage();
            long cnt = dataPartyService.findBeanFlCnt(info);
            List<FDataPartyVo> data = dataPartyService.findBeanFlList(info);

            result.setPageInfo(info.getPageSize(), info.getCurrentPage());
            result.setOk(cnt, data, "查询联邦数据成功");
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "节点列表", notes = "节点列表")
    @RequestMapping(value = "/nodelist", method = {RequestMethod.POST})
    public Object nodeList(@RequestBody FNodePartyDo info, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {

            MyBeanUtils.chgBeanLikeProperties(info, "name", "qryCond");
            info.genPage();
            long cnt = nodePartyService.findBeanCnt(info);
            List<NodePartyView> data = nodePartyService.findVNodePartyList(info);
            result.setPageInfo(info.getPageSize(), info.getCurrentPage());
            data.stream().forEach(e -> {
                e.setIp("*");
                e.setPort(0);
            });
            result.setOk(cnt, data, "查询节点列表成功");
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "根据ids节点列表", notes = "根据ids节点列表")
    @RequestMapping(value = "/nodebyids", method = {RequestMethod.POST})
    public Object nodeByIds(@RequestBody FNodePartyVo info) {
        try {
            List<FNodePartyDo> data = nodePartyService.findByAllIds(Arrays.asList(info.getIds().split(",")));
            data.stream().forEach(e -> {
                e.setIp("***");
                e.setPort(0);
                e.setFlowAddress("***");
            });
            return data;
        } catch (Exception e) {
            logger.error("获取失败", e);
            return null;
        }
    }


    @ApiOperation(value = "数据审批列表", notes = "数据审批列表")
    @RequestMapping(value = "/byprojectnodedataid", method = {RequestMethod.POST})
    public Object byProjectNodeDataId(@RequestBody FDataApproveVo bean) {
        try {
            FDataApproveDo fDataApproveDos = dataApproveService.selectByProjectNodeDataId(bean.getProjectId(), bean.getNodeId(), bean.getDataId());
            return fDataApproveDos;
        } catch (Exception e) {
            logger.error("获取失败", e);
            return null;
        }
    }

    @ApiOperation(value = "数据审批列表", notes = "数据审批列表")
    @RequestMapping(value = "/byprojectnodedataids", method = {RequestMethod.POST})
    public Object byProjectNodeDataIds(@RequestBody FDataApproveVo bean) {
        try {
            List<FDataApproveDo> fDataApproveDos = dataApproveService.selectByProjectNodeDataIds(bean.getProjectId(), bean.getNodeId(), bean.getDataIds());
            return fDataApproveDos;
        } catch (Exception e) {
            logger.error("获取失败", e);
            return null;
        }
    }

    @ApiOperation(value = "数据审批列表", notes = "数据审批列表")
    @RequestMapping(value = "/byprojectnodeid", method = {RequestMethod.POST})
    public Object byProjectNodeId(@RequestBody FDataApproveDo bean) {
        try {
            List<FDataApproveDo> fDataApproveDos = dataApproveService.selectByProjectDataId(bean.getProjectId(), bean.getNodeId(), bean.getTypes());
            return fDataApproveDos;
        } catch (Exception e) {
            logger.error("获取失败", e);
            return null;
        }
    }


    @ApiOperation(value = "根据ids节点列表", notes = "根据ids节点列表")
    @RequestMapping(value = "/byprojectid", method = {RequestMethod.POST})
    public Object byProjectId(@RequestBody FProjectDo info) {
        try {
            FProjectDo data = projectService.findById(info.getId());
            return data;
        } catch (Exception e) {
            logger.error("获取失败", e);
            return null;
        }
    }

    @ApiOperation(value = "根据ids节点列表", notes = "根据ids节点列表")
    @RequestMapping(value = "/bymodelid", method = {RequestMethod.POST})
    public Object byModelId(@RequestBody FModelDo info) {
        try {
            FModelDo data = modelService.findById(info.getId());
            return data;
        } catch (Exception e) {
            logger.error("获取失败", e);
            return null;
        }
    }

    @ApiOperation(value = "根据ids节点列表", notes = "根据ids节点列表")
    @RequestMapping(value = "/partybyids", method = {RequestMethod.POST})
    public Object partyByIds(@RequestBody FNodePartyVo info) {
        try {
            List<String> data = nodePartyService.findByIds(Arrays.asList(info.getIds().split(",")));
            return data;
        } catch (Exception e) {
            logger.error("获取失败", e);
            return null;
        }
    }

    @ApiOperation(value = "根据ids节点列表", notes = "根据ids节点列表")
    @RequestMapping(value = "/databyids", method = {RequestMethod.POST})
    public Object dataByIds(@RequestBody FNodePartyVo info) {
        try {
            List<FDataPartyDo> data = dataPartyService.findByAllIds(Arrays.asList(info.getIds().split(",")));
            return data;
        } catch (Exception e) {
            logger.error("获取失败", e);
            return null;
        }
    }

    @ApiOperation(value = "根据ids节点列表详情", notes = "根据ids节点列表详情")
    @RequestMapping(value = "/databyidsdetail", method = {RequestMethod.POST})
    public Object dataByIdsDetail(@RequestBody FNodePartyVo info) {
        try {
            List<FDataPartyVo> data = dataPartyService.findByAllIdsDetail(Arrays.asList(info.getIds().split(",")));
            return data;
        } catch (Exception e) {
            logger.error("获取失败", e);
            return null;
        }
    }

    @ApiOperation(value = "根据id节点获取数据集明细", notes = "根据id节点获取数据集明细")
    @RequestMapping(value = "/dataPartyById", method = {RequestMethod.POST})
    public Object dataPartyById(@RequestBody FDataPartyDo bean) {
        try {
            FDataPartyDo data = dataPartyService.findById(bean.getId());
            FDataPartyVo info = new FDataPartyVo();
            MyBeanUtils.copyBeanNotNull2Bean(data, info);
            List<FlDataDescDo> ddList = dataDescService.selectByDataId(data.getId());
            FDataPartyVo tmpVo = dataPartyService.findUseCnt(bean.getId());
            if (tmpVo != null) {
                info.setApprove(tmpVo.getApprove());
                info.setUseCnt(tmpVo.getUseCnt());
            }
            info.setTzInfoList(ddList);
            return info;
        } catch (Exception e) {
            logger.error("获取失败", e);
            return null;
        }
    }

    @ApiOperation(value = "联邦信息", notes = "联邦信息")
    @RequestMapping(value = "/pmsinfo", method = {RequestMethod.POST})
    public Object pmServer(@RequestBody FNodePartyDo bean) {
        ResponeMap result = genResponeMap();
        try {
            FNodePartyDo info = nodePartyService.findById(bean.getId());
            result.setSingleOk(info, "查询联邦信息成功");
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "同步数据", notes = "同步数据")
    @RequestMapping(value = "/syncdata", method = {RequestMethod.POST})
    public Object syncData(@RequestBody FDataPartyVo info) {
        ResponeMap result = genResponeMap();
        try {
            logger.info("{}", info);
            Example exp = new Example(FlDataDescDo.class);
            Example.Criteria criteria = exp.createCriteria();
            criteria.andEqualTo("dataId", info.getId()).andEqualTo("edaType", "info");
            exp.orderBy("edaOrder");
            List<FlDataDescDo> delList = dataDescService.findByExample(exp);
            int norder = 1;
            List<FlDataDescDo> modList = new ArrayList<>();
            List<FlDataDescDo> addList = new ArrayList<>();
            if (info.getTzInfoList() != null && info.getTzInfoList().size() > 0) {
                List<FlDataDescDo> tzInfoList = info.getTzInfoList();
                for (FlDataDescDo tz : tzInfoList) {
                    boolean bfound = false;
                    for (FlDataDescDo flDataDescDo : delList) {
                        if (flDataDescDo.getFieldName().equalsIgnoreCase(tz.getFieldName())) {
                            flDataDescDo.setFieldName(tz.getFieldName());
                            flDataDescDo.setEdaValue(tz.getEdaValue());
                            flDataDescDo.setRemark(tz.getRemark());
                            flDataDescDo.setEdaOrder(norder * 10);
                            norder++;
                            delList.remove(flDataDescDo);
                            modList.add(flDataDescDo);
                            bfound = true;
                            break;
                        }
                    }
                    if (!bfound) {
                        FlDataDescDo flDataDescDo = new FlDataDescDo();
                        flDataDescDo.setFieldName(tz.getFieldName());
                        flDataDescDo.setEdaValue(tz.getEdaValue());
                        flDataDescDo.setRemark(tz.getRemark());
                        flDataDescDo.setId(ComUtil.genId());
                        flDataDescDo.setDataId(info.getId());
                        flDataDescDo.setEdaType("info");
                        flDataDescDo.setIndicator("Dtype");
                        flDataDescDo.setEdaOrder(norder * 10);
                        norder++;
                        addList.add(flDataDescDo);
                    }
                }
            } else {
                delList = null;
                addList = null;
                modList = null;
            }
            info.setTzAddList(addList);
            info.setTzModList(modList);
            info.setTzDelList(delList);
            if (dataPartyService.findById(info.getId()) == null) {
                dataPartyService.insertBeanDetail(info);
            } else {
                dataPartyService.updateBeanDetail(info, info.getDelList(), info.getAddList(), addList, modList, delList);
            }
            logger.info("同步数据:数据名称{}", info.getName());
            result.setOk("同步数据成功");
        } catch (Exception e) {
            logger.error("同步数据失败", e);
            result.setErr("同步数据失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "获取项目详情列表", notes = "获取项目详情列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "项目id", dataType = "String", required = true, paramType = "query", example = "")
    })
    @RequestMapping(value = "/details", method = {RequestMethod.POST})
    @ResponseBody
    public Object details(@RequestBody FProjectDo bean, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        if (StringUtils.isEmpty(bean.getId())) {
            return result.setErr("项目id为空").getResultMap();
        }
        try {
            List<FModelDo> modelDos = modelService.selectByProjectId(bean.getId());
            FProjectDo projectDo = projectService.findById(bean.getId());
            if (projectDo == null) {
                return result.setErr("项目不存在").getResultMap();
            }
            ProjectDetail hosts = new ProjectDetail();
            FNodePartyDo host = nodePartyService.findById(projectDo.getHost());

            List<FDataApproveDo> hdatalist = dataApproveService.selectByProjectDataId(projectDo.getId(), host.getId(), 1);
            if (hdatalist != null && hdatalist.size() > 0) {
                Map<String, String> idDataMap = hdatalist.stream().collect(Collectors.toMap(FDataApproveDo::getDataId, e -> e.getId(), (k1, k2) -> k1));
                Map<String, Integer> idApproveMap = hdatalist.stream().collect(Collectors.toMap(FDataApproveDo::getDataId, e -> e.getApprove(), (k1, k2) -> k1));
                List<FDataPartyDo> hlist = dataPartyService.findByAllIds(new ArrayList<>(idDataMap.keySet()));
                hosts.setDList(hlist, idApproveMap, idDataMap);
            }
            hosts.setNode(host);
            List<ProjectDetail> guests = new ArrayList<>();
            List<FProjectGuestDo> guestDos = projectGuestService.selectByProjectId(projectDo.getId());
            for (FProjectGuestDo guestDo : guestDos) {
                ProjectDetail projectDetail = new ProjectDetail();
                FNodePartyDo glists = nodePartyService.findById(guestDo.getNodeId());
                projectDetail.setNode(glists);
                List<FDataApproveDo> datalist = dataApproveService.selectByProjectDataId(projectDo.getId(), guestDo.getNodeId(), 0);
                if (datalist != null && datalist.size() > 0) {
                    Map<String, String> idDataMap = datalist.stream().collect(Collectors.toMap(FDataApproveDo::getDataId, e -> e.getId(), (k1, k2) -> k1));
                    Map<String, Integer> idApproveMap = datalist.stream().collect(Collectors.toMap(FDataApproveDo::getDataId, e -> e.getApprove(), (k1, k2) -> k1));
                    List<FDataPartyDo> glist = dataPartyService.findByAllIds(new ArrayList<>(idDataMap.keySet()));
                    projectDetail.setDList(glist, idApproveMap, idDataMap);
                } else {
                    projectDetail.setDList(new ArrayList<>(), null, null);
                }
                guests.add(projectDetail);
            }
            result.put("host", hosts);
            result.put("guest", guests);
            result.put("model", modelDos);
            result.setSingleOk(projectDo, "获取成功");
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "删除数据", notes = "删除数据")
    @RequestMapping(value = "/deldata", method = {RequestMethod.POST})
    public Object delData(@RequestBody FDataPartyDo info) {
        ResponeMap result = genResponeMap();
        try {

            FDataPartyDo fDataPartyDo = dataPartyService.findById(info.getId());
            if (fDataPartyDo != null) {
                if (fDataPartyDo.getNodeId().equals(info.getNodeId())) {
                    logger.info("删除数据:数据名称{}", info.getName());
                    dataPartyService.deleteByPmsId(fDataPartyDo.getId());
                }
            }
            result.setOk("删除数据成功");
        } catch (Exception e) {
            logger.error("删除数据失败", e);
            result.setErr("删除数据失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "同步节点", notes = "同步节点")
    @RequestMapping(value = "/syncnode", method = {RequestMethod.POST})
    public Object syncNode(@RequestBody FNodePartyDo info) {
        ResponeMap result = genResponeMap();
        try {
            FNodePartyDo nodePartyDo = nodePartyService.findById(info.getId());
            if (nodePartyDo == null) {
                info.setWorkMode(WORKMODE);
                info.setBackend(BACKEND);
                info.setServerConfig(SERVER_CONFIG);
                Integer partyId = Math.toIntExact(Math.round(Math.random() * (90000 - 9000)) + 9000);
                FNodePartyDo fNodePartyDo = nodePartyService.findByPartId(partyId.toString());
                while (fNodePartyDo != null) {
                    partyId = Math.toIntExact(Math.round(Math.random() * (90000 - 9000)) + 9000);
                    fNodePartyDo = nodePartyService.findByPartId(partyId.toString());
                }
                info.setPartyId(partyId);
                nodePartyService.insertBean(info);
            } else {
                info.setPartyId(nodePartyDo.getPartyId());
                nodePartyService.updateNoNull(info);
            }
            logger.info("同步节点:节点名称-{}", info.getName());
            result.setOk("同步节点成功");

        } catch (Exception e) {
            logger.error("同步节点失败", e);
            result.setErr("同步节点失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "同步用户", notes = "同步用户")
    @RequestMapping(value = "/syncuser", method = {RequestMethod.POST})
    public Object syncUser(@RequestBody SysUserDo info) {
        ResponeMap result = genResponeMap();
        try {
            if( info.getNodePartyId() == null){
                return result.setErr( "节点id不能为空" ).getResultMap();
            }
            if( info.getId() == null){
                return result.setErr( "用户id不能为空" ).getResultMap();
            }
            FNodePartyDo nodePartyDo = nodePartyService.findById(info.getNodePartyId());
            if (nodePartyDo == null) {
                return result.setErr( "节点不存在" ).getResultMap();
            }
            SysUserDo nodeUser = sysUserService.findById( info.getId() );
            if( nodeUser != null ) {
                if ( !info.getNodePartyId().equals( nodeUser.getNodePartyId() ) ) {
                    return result.setErr( "用户节点不正确" ).getResultMap();
                }
                sysUserService.updateBean(info);
            }
            else {
                sysUserService.insertBean(info);
            }
            logger.info("同步用户:{}-{}", info.getUsername(),info.getMobile());
            result.setOk("同步用户成功");

        } catch (Exception e) {
            logger.error("同步用户失败", e);
            result.setErr("同步用户失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "提交审批", notes = "提交审批")
    @RequestMapping(value = "/syncapprove", method = {RequestMethod.POST})
    public Object syncApprove(@RequestBody FDataApproveDo info) {
        ResponeMap result = genResponeMap();
        try {

            FDataApproveDo approveDo = dataApproveService.findById(info.getId());
            if (approveDo == null) {
                approveDo = dataApproveService.selectByProjectNodeDataId(info.getProjectId(), info.getNodeId(), info.getDataId());
            }
            if (approveDo == null) {
                FDataPartyDo byId = dataPartyService.findById(info.getDataId());
                if (byId != null) {
                    info.setUserId(byId.getCreatorId());
                    info.setUserName(byId.getCreatorName());
                    dataApproveService.insertBean(info);
                    byId.setUseCnt(byId.getUseCnt() + 1);
                    dataPartyService.updateNoNull(byId);
                }
            } else {
                if (StringUtils.isNotBlank(info.getApproval())) {
                    approveDo.setApproval(info.getApproval());
                }
                if (StringUtils.isNotBlank(info.getContent())) {
                    approveDo.setContent(info.getContent());
                }
                approveDo.setApprove(info.getApprove());
                dataApproveService.updateNoNull(approveDo);
            }
            logger.info("审批提交:用户名称-{}", info.getCreatorAccount());
            result.setOk("提交审批成功");
        } catch (Exception e) {
            logger.error("提交审批失败", e);
            result.setErr("提交审批失败");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "批量添加审批数据", notes = "批量添加审批数据")
    @RequestMapping(value = "/syncapproves", method = {RequestMethod.POST})
    public Object syncApproves(@RequestBody FDataApproveVo info) {
        ResponeMap result = genResponeMap();
        try {
            List<FDataApproveDo> approveDos = dataApproveService.selectByProjectDataId(info.getProjectId(), info.getNodeId(), info.getTypes());
            Map<String, Integer> idApproveMap = approveDos.stream().collect(Collectors.toMap(FDataApproveDo::getDataId, e -> e.getApprove(), (k1, k2) -> k1));
            for (String data : info.getDataIds()) {
                if (!idApproveMap.keySet().contains(data)) {
                    FDataPartyDo byId = dataPartyService.findById(data);
                    if (byId != null) {
                        FDataApproveDo fDataApproveDo = new FDataApproveDo();
                        MyBeanUtils.copyBean2Bean(fDataApproveDo, info);
                        fDataApproveDo.setDataId(data);
                        fDataApproveDo.setUserId(byId.getCreatorId());
                        fDataApproveDo.setUserName(byId.getCreatorName());
                        dataApproveService.insertBean(fDataApproveDo);
                        byId.setUseCnt((byId.getUseCnt() == null || byId.getUseCnt() == 0) ? 1 : byId.getUseCnt() + 1);
                        dataPartyService.updateNoNull(byId);
                    }
                }
            }
            logger.info("审批提交:用户名称-{}", info.getCreatorAccount());
            result.setOk("提交审批成功");
        } catch (Exception e) {
            logger.error("提交审批失败", e);
            result.setErr("提交审批失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "根据id删除审批", notes = "根据id删除审批")
    @RequestMapping(value = "/delapproveid", method = {RequestMethod.POST})
    public Object delApproveId(@RequestBody FDataApproveDo info) {
        ResponeMap result = genResponeMap();
        try {
            FDataApproveDo fDataApproveDo = dataApproveService.findById(info.getId());
            if (fDataApproveDo != null) {
                dataApproveService.deleteById(info.getId());
            }
            logger.info("审批删除:用户名称-{}", info.getCreatorAccount());
            result.setOk("删除审批成功");
        } catch (Exception e) {
            logger.error("删除审批失败", e);
            result.setErr("删除审批失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "删除审批", notes = "删除审批")
    @RequestMapping(value = "/delapprove", method = {RequestMethod.POST})
    public Object delApprove(@RequestBody FDataApproveVo info) {
        ResponeMap result = genResponeMap();
        try {
            long cnt = dataApproveService.findBeanCnt(info);
            if (cnt > 0) {
                logger.info("审批删除:用户名称-{}", info.getCreatorName());
                dataApproveService.deleteByProjectDataId(info.getProjectId(), info.getDataId());
            }
            result.setOk("删除审批成功");
        } catch (Exception e) {
            logger.error("删除审批失败", e);
            result.setErr("删除审批失败");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "查询审批", notes = "查询审批")
    @RequestMapping(value = "/getapprove", method = {RequestMethod.POST})
    public Object getApprove(@RequestBody FDataApproveVo info) {
        ResponeMap result = genResponeMap();
        try {
            MyBeanUtils.chgBeanLikeProperties(info, "dataName", "qryCond");
            info.genPage();
            logger.debug( "{}", JsonUtil.toSimpleJson( info ) );
            long cnt = dataApproveService.findBeanCnt(info);
            List<FDataApproveVo> data = dataApproveService.findBeanList(info);
            result.setPageInfo(info.getPageSize(), info.getCurrentPage());
            result.setOk(cnt, data, "查询审批列表成功");
        } catch (Exception e) {
            logger.error("查询审批失败", e);
            result.setErr("查询审批失败");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "协助方项目列表", notes = "协助方项目列表")
    @RequestMapping(value = "/memberpage", method = {RequestMethod.POST})
    public Object memberPage(@RequestBody FProjectVo modelInfo, HttpServletRequest request) {
        ResponeMap resMap = genResponeMap();
        try {
            MyBeanUtils.chgBeanLikeProperties(modelInfo, "name", "qryCond");
            modelInfo.genPage();
            modelInfo.setGuest(modelInfo.getGuest());
            List<FProjectDo> data = projectService.findByMemberBeanList(modelInfo);
            long cnt = projectService.findByMemberBeanCnt(modelInfo);
            List<FProjectVo> res = new ArrayList<>();
            for (FProjectDo fProjectDo : data) {
                FProjectVo fProjectVo = new FProjectVo();
                MyBeanUtils.copyBean2Bean(fProjectVo, fProjectDo);
                FNodePartyDo fNodePartyDo = nodePartyService.findById(fProjectDo.getHost());
                fProjectVo.setIcon(fNodePartyDo.getIcon());
                fProjectVo.setHost(fNodePartyDo.getName());
                List<String> guests = projectGuestService.findByIds(fProjectDo.getId());
                List<FNodePartyDo> list = nodePartyService.findByAllIds(guests);
                fProjectVo.setGuests(list);
                List<FModelDo> fModelDos = modelService.selectByProjectId(fProjectDo.getId());
                fProjectVo.setModels(modelService.selectByProjectId(fProjectDo.getId()).size());
                long running = 0;
                for (FModelDo fModelDo : fModelDos) {
                    running = running + modelJobService.selectByModelId(fModelDo.getId()).stream().filter(e -> e.getStatus().equals(2)).count();
                }
                fProjectVo.setRun(running);
                res.add(fProjectVo);
            }
            resMap.put("status", 0);
            resMap.setPageInfo(modelInfo.getPageSize(), modelInfo.getCurrentPage());
            resMap.setOk(cnt, res, "查询成功");
        } catch (Exception ex) {
            resMap.setErr("获取项目列表失败");
            logger.error("获取项目列表失败", ex);
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "协助方模型列表", notes = "协助方模型列表")
    @RequestMapping(value = "/membermodel", method = {RequestMethod.POST})
    public Map<String, Object> memberModel(@RequestBody FModelDo modelInfo, HttpServletRequest request) {
        ResponeMap resMap = genResponeMap();
        try {
            MyBeanUtils.chgBeanLikeProperties(modelInfo, "name", "qryCond");
            modelInfo.genPage();
            List<FModelDo> data = modelService.findBeanList(modelInfo);
            long cnt = modelService.findBeanCnt(modelInfo);
            if (data.size() > 0) {
                data.stream().forEach(e -> {
                    e.setDisable(0);
                });
            }
            resMap.setPageInfo(modelInfo.getPageSize(), modelInfo.getCurrentPage());
            resMap.setOk(cnt, data);
        } catch (Exception e) {
            logger.error("获取模型失败", e);
            resMap.setErr("获取模型失败");
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "协助方模型记录列表", notes = "协助方模型记录列表")
    @RequestMapping(value = "/membermodeljob", method = {RequestMethod.POST})
    public Map<String, Object> memberModelJob(@RequestBody FModelJobVo modelInfo, HttpServletRequest request) {
        ResponeMap resMap = genResponeMap();
        try {
            MyBeanUtils.chgBeanLikeProperties(modelInfo, "name", "qryCond");
            modelInfo.genPage();
            FModelDo modelDo = modelService.findById(modelInfo.getModelId());
            if (modelDo != null) {
                modelInfo.setTypes(modelDo.getTypes());
                List<FModelJobDo> data = modelJobService.findBeanList(modelInfo);
                if (data != null && data.size() > 0) {
                    data.stream().forEach((f -> f.setHost("协作方")));
                }
                long cnt = modelJobService.findBeanCnt(modelInfo);
                resMap.setPageInfo(modelInfo.getPageSize(), modelInfo.getCurrentPage());
                FProjectDo fProjectDo = projectService.findById(modelDo.getProjectId());
                resMap.put("info", fProjectDo);
                resMap.put("model", modelDo);
                resMap.setOk(cnt, data);
            }
        } catch (Exception e) {
            logger.error("获取模型记录失败", e);
            resMap.setErr("获取模型记录失败");
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "根据id获取模型记录", notes = "根据id获取模型记录")
    @RequestMapping(value = "/modeljobbyid", method = {RequestMethod.POST})
    public Object modelJobById(@RequestBody FModelJobDo bean, HttpServletRequest request) {
        ResponeMap resMap = genResponeMap();
        try {
            return modelJobService.findById(bean.getId());
        } catch (Exception e) {
            logger.error("获取模型记录失败", e);
            resMap.setErr("获取模型记录失败");
            return null;
        }
    }


    @ApiOperation(value = "同步项目", notes = "同步项目")
    @RequestMapping(value = "/syncproject", method = {RequestMethod.POST})
    public Map<String, Object> syncProject(@RequestBody ProjectVo modelInfo, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            if (projectService.findById(modelInfo.getProject().getId()) == null) {

                projectService.insertBeanPms(modelInfo);
            } else {
                projectService.updateNoNull(modelInfo.getProject());
            }
            result.setOk("新增项目成功");
        } catch (Exception e) {
            logger.error("新增项目失败", e);
            result.setErr("新增项目失败");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "删除项目", notes = "删除项目")
    @RequestMapping(value = "/delproject", method = {RequestMethod.POST})
    public Map<String, Object> delProject(@RequestBody FProjectDo fProjectDo, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            if (fProjectDo.getId() != null) {
                List<FProjectGuestDo> projectGuestDos = projectGuestService.selectByProjectId(fProjectDo.getId());
                if (projectGuestDos != null && projectGuestDos.size() > 0) {
                    projectGuestDos.stream().forEach(e -> {
                        projectGuestService.deleteById(e.getId());
                        dataApproveService.deleteByProjectId(e.getProjectId());
                    });
                }
                projectService.deleteById(fProjectDo.getId());
            }
            result.setOk("删除项目协助方成功");
        } catch (Exception e) {
            logger.error("删除项目协助方失败", e);
            result.setErr("删除项目协助方失败");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "同步项目参与方", notes = "同步项目参与方")
    @RequestMapping(value = "/syncprojectguest", method = {RequestMethod.POST})
    public Map<String, Object> syncProjectGuest(@RequestBody FProjectGuestDo fProjectGuestDo, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            FProjectGuestDo old = new FProjectGuestDo();
            old.setNodeId(fProjectGuestDo.getNodeId());
            old.setProjectId(fProjectGuestDo.getProjectId());
            FProjectGuestDo info = projectGuestService.findOne(old);
            if (info == null) {
                projectGuestService.insertBean(fProjectGuestDo);
            } else {
                projectGuestService.updateNoNull(fProjectGuestDo);
            }
            result.setOk("新增项目协助方成功");
        } catch (Exception e) {
            logger.error("新增项目协助方失败", e);
            result.setErr("新增项目协助方失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "删除项目参与方", notes = "删除项目参与方")
    @RequestMapping(value = "/delprojectguest", method = {RequestMethod.POST})
    public Map<String, Object> delProjectGuest(@RequestBody FProjectGuestDo fProjectGuestDo, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            projectGuestService.deleteByProjectDataId(fProjectGuestDo.getProjectId(), fProjectGuestDo.getNodeId());
            result.setOk("删除项目协助方成功");
        } catch (Exception e) {
            logger.error("删除项目协助方失败", e);
            result.setErr("删除项目协助方失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "同步模型", notes = "同步模型")
    @RequestMapping(value = "/syncmodel", method = {RequestMethod.POST})
    public Map<String, Object> syncModel(@RequestBody FModelDo modelDo, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            if (modelService.findById(modelDo.getId()) == null) {
                modelService.insertBean(modelDo);
            } else {
                modelService.updateNoNull(modelDo);
            }
            result.setOk("新增流程成功");
        } catch (Exception e) {
            logger.error("新增流程目失败", e);
            result.setErr("新增流程失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "同步模型引用数据", notes = "同步模型引用数据")
    @RequestMapping(value = "/syncmodeldata", method = {RequestMethod.POST})
    public Map<String, Object> syncModelData(@RequestBody List<FModelDataDo> addModelData, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            modelService.insertModelData(addModelData);
            result.setOk("新增模型数据成功");
        } catch (Exception e) {
            logger.error("新增模型数据失败", e);
            result.setErr("新增模型数据失败");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "删除模型", notes = "删除模型")
    @RequestMapping(value = "/delmodel", method = {RequestMethod.POST})
    public Map<String, Object> delModel(@RequestBody FModelDo modelDo, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            if (modelDo.getId() != null) {
                List<FModelJobDo> fModelJobDos = modelJobService.selectByModelId(modelDo.getId());
                if (fModelJobDos != null && fModelJobDos.size() > 0) {
                    fModelJobDos.stream().forEach(e -> {
                        modelJobService.deleteById(e.getId());
                    });
                }
                modelService.deleteById(modelDo.getId());
            }
            result.setOk("删除流程成功");
        } catch (Exception e) {
            logger.error("删除流程失败", e);
            result.setErr("删除流程失败");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "同步模型记录", notes = "同步模型记录")
    @RequestMapping(value = "/syncmodeljob", method = {RequestMethod.POST})
    public Map<String, Object> syncModelJob(@RequestBody FModelJobDo modelJobDo, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            if (modelJobService.findById(modelJobDo.getId()) == null) {
                modelJobService.insertBean(modelJobDo);
            } else {
                modelJobService.updateNoNull(modelJobDo);
            }
            result.setOk("新增模型记录成功");
        } catch (Exception e) {
            logger.error("新增模型记录失败", e);
            result.setErr("新增模型记录失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "删除模型记录", notes = "删除模型记录")
    @RequestMapping(value = "/delmodeljob", method = {RequestMethod.POST})
    public Object delModelJob(@RequestBody FModelJobDo info) {
        ResponeMap result = genResponeMap();
        try {
            if (info.getId() != null) {
                modelJobService.deleteById(info.getId());
            }
            result.setOk("删除模型记录成功");
        } catch (Exception e) {
            logger.error("删除模型记录失败", e);
            result.setErr("删除模型记录失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "联邦统计", notes = "联邦统计")
    @RequestMapping(value = "/dataorder", method = {RequestMethod.POST})
    public Object dataOrder(@RequestBody FDataPartyVo info) {
        ResponeMap result = genResponeMap();
        try {
            MyBeanUtils.chgBeanLikeProperties(info, "name", "qryCond");
            info.genPage();
            List<DataOrderVo> dataOrder = dataPartyService.findDataOrder(info);
            long orderCnt = dataPartyService.findDataOrderCnt(info);
            result.setPageInfo(info.getPageSize(), info.getCurrentPage());
            result.setOk(orderCnt, dataOrder, "查询联邦统计数据成功");
        } catch (Exception e) {
            logger.error("获取联邦统计失败", e);
            result.setErr("联邦统计失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "联邦统计", notes = "联邦统计")
    @RequestMapping(value = "/dataordertree", method = {RequestMethod.POST})
    public Object dataOrderTree(@RequestBody FDataPartyVo info) {
        ResponeMap result = genResponeMap();

        try {
            MyBeanUtils.chgBeanLikeProperties(info, "name", "qryCond");
            info.genPage();
            List<DataOrderVo> dataOrder = dataPartyService.findDataOrderTree(info);
            long orderCnt = dataPartyService.findDataOrderTreeCnt(info);
            result.setPageInfo(info.getPageSize(), info.getCurrentPage());
            result.setOk(orderCnt, dataOrder, "查询联邦统计数据成功");
        } catch (Exception e) {
            logger.error("获取联邦统计失败", e);
            result.setErr("联邦统计失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "联邦统计模型", notes = "联邦统计模型")
    @RequestMapping(value = "/modelordertree", method = {RequestMethod.POST})
    public Object modelOrderTree(@RequestBody DataOrderVo info) {
        ResponeMap result = genResponeMap();
        try {
            List<String> strings = modelService.selectByDataId(info.getDataId());
            List<FModelDo> fModelDos = modelService.selectByProjectDataId(info.getProjectId(), info.getDataId(), (strings==null||strings.size()==0)?null:strings, info.getTypes());
            result.setSingleOk(fModelDos, "获取联邦统计模型");
        } catch (Exception e) {
            logger.error("获取联邦统计模型失败", e);
            result.setErr("获取联邦统计模型失败");
        }
        return result.getResultMap();
    }
}
