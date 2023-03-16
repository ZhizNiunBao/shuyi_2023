package cn.bywin.business.controller.olk;


import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.service.olk.OlkDatabaseService;
import cn.bywin.business.service.olk.OlkFieldService;
import cn.bywin.business.service.olk.OlkObjectService;
import cn.bywin.business.service.olk.OlkSchemaService;
import cn.bywin.business.trumodel.ApiOlkDbService;
import cn.bywin.common.resp.ObjectResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        maxAge = 3600)
@RestController
@Api(tags = "olk- 申请管理-olkapplyobject")
@RequestMapping("/olkapplyobject")
public class OlkApplyObjectController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OlkDatabaseService databaseService;

    @Autowired
    private OlkSchemaService shcmeaService;

    @Autowired
    private OlkObjectService objectService;

    @Autowired
    private OlkFieldService fieldService;

    @Autowired
    private NodePartyService nodePartyService;

    @Autowired
    private ApiOlkDbService apiOlkDbService;

    @ApiOperation(value = "新增申请对象", notes = "新增申请对象")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "info", value = "申请对象", dataType = "TOlkFieldDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public Object add(@RequestBody FDataApproveDo info, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null || StringUtils.isBlank(ud.getUserId())) {
                return resMap.setErr("请先登录").getResultMap();
            }
            if (info == null || StringUtils.isBlank(info.getDataId())) {
                return resMap.setErr("内容不能为空").getResultMap();
            }

            if (StringUtils.isBlank(info.getContent())) {
                return resMap.setErr("申请原因不能为空").getResultMap();
            }

            logger.info( "{}", JsonUtil.toSimpleJson( info ) );

            FNodePartyDo nodePartyDo = nodePartyService.findFirst();

            //info.setApproval( bean.getApplyNote() );
            //info.setUserId(ud.getUserId());
            //info.setUserName( ud.getUserName());
            //info.setDataId(bean.getRelId());
            //info.setDataId( bean.getRelId() );
            info.setApprove(2);
            info.setTypes( 1 );
            info.setId( ComUtil.genId());
            info.setNodeId( nodePartyDo.getId() );
            LoginUtil.setBeanInsertUserInfo(info, ud);

//            String relId = info.getRelId();
//            if (StringUtils.isBlank(relId)) {
//                return resMap.setErr("申请对象id不能为空").getResultMap();
//            } else if (relId.startsWith("db")) {
//                info.setObjectId(relId.substring(2));
//                info.setStype("db");
//                TOlkObjectDo objectDo = objectService.findById(info.getObjectId());
//                if (objectDo == null) {
//                    return resMap.setErr("申请对象表不存在").getResultMap();
//                }
//                //info.setDcId(objectDo.getDcId());
//                info.setDbId(objectDo.getDbId());
//                info.setSchemaId(objectDo.getSchemaId());
//                info.setObjName(objectDo.getObjectName());
//
//                info.setObjFullName(objectDo.getObjFullName());
//                info.setObjChnName(ComUtil.trsEmpty( objectDo.getObjChnName(),objectDo.getObjectName()));
//
//            } else if (relId.startsWith("ds")) {
//                info.setDatasetId(relId.substring(2));
//                info.setStype("ds");
//                TOlkDatasetDo datasetDo = datasetService.findById(info.getDatasetId());
//                if (datasetDo == null) {
//                    return resMap.setErr("申请数据集不存在").getResultMap();
//                }
//                info.setObjName(datasetDo.getSetCode());
//                info.setObjFullName(datasetDo.getViewName());
//                info.setObjChnName(ComUtil.trsEmpty( datasetDo.getSetChnName(),datasetDo.getSetCode()));
//            }

//            TTruGrantObjectDo gtemp = new TTruGrantObjectDo();
//            gtemp.setRelId( info.getRelId() );
//            gtemp.setToAccount( info.getApplyAccount() );
//
//            List<TTruGrantObjectDo> grantList = grantObjectService.find(gtemp);
//            if( grantList.size()>0 ){
//                return resMap.setErr("已授权无需再申请").getResultMap();
//            }
//            applyObjectService.insertBean(info);

            ObjectResp<FDataApproveDo>  retVal = apiOlkDbService.synOlkApplyObject( info, ud.getTokenId() );
            if ( !retVal.isSuccess() ) {
                String msg = String.format( "保存失败" );
                if ( StringUtils.isNotBlank( retVal.getMsg() ) ) {
                    msg = retVal.getMsg();
                }
                //applyObjectService.deleteById( info.getId() );
                return resMap.setErr( msg ).getResultMap();
            }

            //TOlkDcServerDo dcDo = dcserverService.findById(info.getDcId());

            //String start = workflow.start(dcDo.getWorkFlowKey(),info);
//            if( "".equals( start) ){
//                info.setCandidateUser(String.format(",%s,", info.getCandidateUser() ));
//                applyObjectService.updateBean( info );
//            }
//            else{
//                return  resMap.setErr("提交审批失败").getResultMap();
//            }

            //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).addLog(ud, info, "新增-申请对象");

            resMap.setOk("保存成功");

        } catch (Exception ex) {
            resMap.setErr("保存失败");
            logger.error("保存异常:", ex);
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "取消申请对象", notes = "取消申请对象")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "申请id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "relId", value = "关联对象id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/cancel", method = {RequestMethod.POST})
    @ResponseBody
    public Object cancel(String id, String relId, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if (StringUtils.isBlank(id) && StringUtils.isBlank(relId)) {
                return resMap.setErr("id和关联id不能同时为空").getResultMap();
            }
            UserDo user = LoginUtil.getUser(request);
//            List<String> ids = null;
//
//            if (StringUtils.isNotBlank(id)) {
//                ids = Arrays.asList(id.split(",|\\s+"));
//            }
//            List<String> rels = null;
//            if (StringUtils.isNotBlank(relId)) {
//                rels = Arrays.asList(relId.split(",|\\s+"));
//            }

            //ids = list.stream().map(x -> x.getId()).collect(Collectors.toList());

//            List<TTruApplyObjectDo> oldList = new ArrayList<>();
//
//            for (TTruApplyObjectDo info : list) {
//                TTruApplyObjectDo old = new TTruApplyObjectDo();
//                MyBeanUtils.copyBeanNotNull2Bean( info, old );
//                oldList.add( old );
//                info.setSynFlag( 1 );
//                info.setStatus( 3 );
//                try{
////                    if( StringUtils.isNotBlank( info.getProcessInstanceId() ) ) {
////                        Resp<WfTaskMeta> result = workflow.delete(info.getProcessInstanceId());
////                    }
//                }
//                catch (Exception e1){
//                    //resMap.setErr("删除流程异常");
//                    logger.error("删除异常:", e1);
//                }
//            }

            //applyObjectService.batchUpdateByPrimaryKey( list );

            ObjectResp<String> retVal = apiOlkDbService.cancelOlkApplyObject( id, relId, user.getUserId(), user.getTokenId() );
            if ( !retVal.isSuccess() ) {
                String msg = String.format( "取消失败" );
                if ( StringUtils.isNotBlank( retVal.getMsg() ) ) {
                    msg = retVal.getMsg();
                }
                return resMap.setErr( msg ).getResultMap();
            }

            //applyObjectService.deleteByIds(ids);

//            String times = String.valueOf(System.currentTimeMillis());
//            for (int i = 0; i < list.size(); i++) {
//                TTruApplyObjectDo info = list.get(i);
//                TTruApplyObjectDo old = oldList.get(i);
//                try {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(user, old, info, "修改-取消申请" + times);
//                } catch (Exception e1) {
//                    resMap.setErr("取消失败");
//                    logger.error("取消异常:", e1);
//                }
//            }
            resMap.setOk("取消成功");

        } catch (Exception ex) {
            resMap.setErr("取消失败");
            logger.error("取消异常:", ex);
        }
        return resMap.getResultMap();
    }

}
