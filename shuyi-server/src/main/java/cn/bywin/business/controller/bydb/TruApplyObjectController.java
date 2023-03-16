package cn.bywin.business.controller.bydb;


//import cn.bywin.api.workflow.common.WfTaskMeta;

import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.service.bydb.BydbDatabaseService;
import cn.bywin.business.service.bydb.BydbDatasetService;
import cn.bywin.business.service.bydb.BydbFieldService;
import cn.bywin.business.service.bydb.BydbObjectService;
import cn.bywin.business.service.bydb.BydbSchemaService;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.trumodel.ApiTruModelService;
import cn.bywin.common.resp.BaseRespone;
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
@Api(tags = "可信建模- 申请管理-truapplyobject")
@RequestMapping("/truapplyobject")
public class TruApplyObjectController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BydbDatabaseService databaseService;

    @Autowired
    private BydbSchemaService shcmeaService;

    @Autowired
    private BydbObjectService objectService;

    @Autowired
    private BydbFieldService fieldService;

    @Autowired
    private BydbDatasetService datasetService;

//    @Autowired
//    private BydbFavouriteObjectService favouriteObjectService;

//    @Autowired
//    private TruApplyObjectService applyObjectService;
//
//    @Autowired
//    private TruApplyCheckService applyCheckService;
//
//    @Autowired
//    private TruGrantObjectService grantObjectService;

    @Autowired
    private NodePartyService nodePartyService;

    @Autowired
    private ApiTruModelService apiTruModelService;

//    @Autowired
//    private BydbDcServerService dcserverService;

//    @Autowired
//    IApplyObjectWorkflow workflow;


    @ApiOperation(value = "新增申请对象", notes = "新增申请对象")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "info", value = "申请对象", dataType = "TBydbFieldDo", required = true, paramType = "body")
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
//                TBydbObjectDo objectDo = objectService.findById(info.getObjectId());
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
//                TBydbDatasetDo datasetDo = datasetService.findById(info.getDatasetId());
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

            ObjectResp<FDataApproveDo>  retVal = apiTruModelService.synApplyObject( info, ud.getTokenId() );
            if ( !retVal.isSuccess() ) {
                String msg = String.format( "保存失败" );
                if ( StringUtils.isNotBlank( retVal.getMsg() ) ) {
                    msg = retVal.getMsg();
                }
                //applyObjectService.deleteById( info.getId() );
                return resMap.setErr( msg ).getResultMap();
            }

            //TBydbDcServerDo dcDo = dcserverService.findById(info.getDcId());

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

    /*@ApiOperation(value = "新增申请对象", notes = "新增申请对象")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "info", value = "申请对象", dataType = "TBydbFieldDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/listadd", method = {RequestMethod.POST})
    @ResponseBody
    public Object listAdd(@RequestBody List<TTruApplyObjectDo> beanList, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null || StringUtils.isBlank(ud.getUserId())) {
                return resMap.setErr("请先登录").getResultMap();
            }
            if (beanList == null || beanList.size() == 0) {
                return resMap.setErr("内容不能为空").getResultMap();
            }

            Date now = new Date();

            List<TTruApplyObjectDo> foList = new ArrayList<>();
            List<String> objIdList = new ArrayList<>();
            List<String> dsIdList = new ArrayList<>();

            for (TTruApplyObjectDo tmp : beanList) {
                TTruApplyObjectDo foDo = new TTruApplyObjectDo();
                foDo.setApplyAccount(ud.getUserName());
                foDo.setRelId(tmp.getRelId());
                foDo.setApplyTime(now);
                foDo.setStatus(21);
                foDo.setId(ComUtil.genId());
                foDo.setApplyNote(tmp.getApplyNote());
                LoginUtil.setBeanInsertUserInfo(foDo, ud);

                String relId = foDo.getRelId();
                if (StringUtils.isBlank(relId)) {
                    return resMap.setErr("申请对象id不能为空").getResultMap();
                } else if (relId.startsWith("db")) {
                    foDo.setObjectId(relId.substring(2));
                    foDo.setStype("db");
                    if (objIdList.contains(foDo.getObjectId())) {
                        return resMap.setErr("申请对象id不能重复").getResultMap();
                    }
                    objIdList.add(foDo.getObjectId());
                    TBydbObjectDo objectDo = objectService.findById(foDo.getObjectId());
                    if (objectDo == null) {
                        return resMap.setErr("申请对象表不存在").getResultMap();
                    }
                    //foDo.setDcId(objectDo.getDcId());
                    foDo.setDbId(objectDo.getDbId());
                    foDo.setSchemaId(objectDo.getSchemaId());
                    foDo.setObjName(objectDo.getObjectName());
                    foDo.setObjFullName(objectDo.getObjFullName());
                    foDo.setObjChnName(objectDo.getObjChnName());

                } else if (relId.startsWith("ds")) {
                    foDo.setDatasetId(relId.substring(2));
                    foDo.setStype("ds");
                    if (dsIdList.contains(foDo.getDatasetId())) {
                        return resMap.setErr("申请对象id不能重复").getResultMap();
                    }
                    dsIdList.add(foDo.getDatasetId());
                    TBydbDatasetDo datasetDo = datasetService.findById(foDo.getDatasetId());
                    if (datasetDo == null) {
                        return resMap.setErr("申请数据集不存在").getResultMap();
                    }
                    //foDo.setDcId(datasetDo.getDcId());
                    foDo.setObjName(datasetDo.getSetCode());
                    foDo.setObjFullName(datasetDo.getViewName());
                    foDo.setObjChnName(datasetDo.getSetChnName());
                }
                TTruGrantObjectDo gtemp = new TTruGrantObjectDo();
                gtemp.setRelId( foDo.getRelId() );
                gtemp.setToAccount( foDo.getApplyAccount() );
                List<TTruGrantObjectDo> grantList = grantObjectService.find(gtemp);
                if( grantList.size()>0 ){
                    return resMap.setErr("有资源已授权不能再申请").getResultMap();
                }
                foList.add(foDo);
            }
//            Example exp = new Example(TBydbObjectDo.class);
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andIn("id", objIdList);
//            List<TBydbObjectDo> list = objectService.findByExample(exp);
//
//            if ( list.size() != objIdList.size() ) {
//                return resMap.setErr("没有数据可删除").getResultMap();
//            }
//            if (list.size() != ids.size()) {
//                return resMap.setErr("有数据不存在").getResultMap();
//            }
            applyObjectService.batchAdd(foList);

//            String times = "" + System.currentTimeMillis();
//            for (TTruApplyObjectDo info : foList) {
//                try {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).addLog(ud, info, "新增-申请对象" + times);
//                } catch (Exception e1) {
//                    resMap.setErr("新增申请对象失败");
//                    logger.error("新增申请对象异常:", e1);
//                }
//            }

            resMap.setOk("保存成功");

        } catch (Exception ex) {
            resMap.setErr("保存失败");
            logger.error("保存异常:", ex);
        }
        return resMap.getResultMap();
    }
*/


    /*@ApiOperation(value = "修改申请对象", notes = "修改申请对象")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "modelVo", value = "申请对象", dataType = "TBydbFieldDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update(@RequestBody TBydbFieldDo bean, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null || StringUtils.isBlank(ud.getUserId())) {
                return resMap.setErr("请先登录").getResultMap();
            }

            TBydbFieldDo info = fieldService.findById(bean.getId());

            if (info == null) {
                return resMap.setErr("内容不存在").getResultMap();
            }

            TBydbFieldDo oldData = new TBydbFieldDo();
            MyBeanUtils.copyBeanNotNull2Bean(info, oldData);

            MyBeanUtils.copyBeanNotNull2Bean(bean, info);

            //new PageBeanWrapper(info, hru, "");

            info.setFieldName(oldData.getFieldName());

            if (StringUtils.isBlank(info.getFieldName())) {
                return resMap.setErr("名称不能为空").getResultMap();
            }

            if (StringUtils.isBlank(info.getFieldType())) {
                return resMap.setErr("类型不能为空").getResultMap();
            }

            if (StringUtils.isBlank(info.getObjectId())) {
                return resMap.setErr("对象不能为空").getResultMap();
            }

            TBydbObjectDo objectDo = objectService.findById(info.getObjectId());
            if (objectDo == null) {
                return resMap.setErr("对象不存在").getResultMap();
            }
            info.setSchemaId(objectDo.getSchemaId());
            info.setDbId(objectDo.getDbId());

            info.setReplaceStatement( chgStateField( info.getChgStatement() , info.getFieldName() ));

//            final long sameNameCount = fieldService.findSameNameCount( info );
//            if( sameNameCount >0 ){
//                return resMap.setErr("名称已使用").getResultMap();
//            }

            fieldService.updateBean(info);

            resMap.setSingleOk(info, "保存成功");

        } catch (Exception ex) {
            resMap.setErr("保存失败");
            logger.error("保存异常:", ex);
        }
        return resMap.getResultMap();
    }
    @ApiOperation(value = "申请对象内容", notes = "申请对象内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "语句元素id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    @ResponseBody
    public Object info(String id) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if (StringUtils.isBlank(id)) {
                return resMap.setErr("id不能为空").getResultMap();
            }
            TBydbFieldDo modelVo = fieldService.findById(id);
            resMap.setSingleOk(modelVo, "成功");

        } catch (Exception ex) {
            resMap.setErr("查询失败");
            logger.error("查询异常:", ex);
        }
        return resMap.getResultMap();
    }
     */

    /*@ApiOperation(value = "删除申请对象", notes = "删除申请对象")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "申请id", dataType = "String", required = true, paramType = "query"),
            //@ApiImplicitParam(name = "relId", value = "关联对象id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public Object delete(String id, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            //if (StringUtils.isBlank(id) && StringUtils.isBlank(relId)) {
            //    return resMap.setErr("id和关联id不能同时为空").getResultMap();
            //}
            if (StringUtils.isBlank(id) ) {
                return resMap.setErr("id不能同时为空").getResultMap();
            }
            UserDo user = LoginUtil.getUser(request);
//            if( !id.matches("^[a-zA-Z0-9\\-_,]*$") ){
//                return resMap.setErr("id有非法字符").getResultMap();
//            }
            List<String> ids = null;
            if (StringUtils.isNotBlank(id)) {
                ids = Arrays.asList(id.split(",|\\s+"));
            }
//            List<String> rels = null;
//            if (StringUtils.isNotBlank(relId)) {
//                rels = Arrays.asList(relId.split(",|\\s+"));
//            }
            Example exp = new Example(TTruApplyObjectDo.class);
            Example.Criteria criteria = exp.createCriteria();
            if (ids != null) {
                criteria.andIn("id", ids);
            }
//            if (rels != null) {
//                criteria.andIn("relId", rels);
//                criteria.andEqualTo("applyAccount", user.getUserName());
//            }
            List<TTruApplyObjectDo> list = applyObjectService.findByExample(exp);
            int cnt = list.size();
            if (cnt == 0) {
                return resMap.setErr("没有数据可删除").getResultMap();
            }
            if (ids != null && list.size() != ids.size()) {
                return resMap.setErr("有数据不存在").getResultMap();
            }
            ids = list.stream().map(x -> x.getId()).collect(Collectors.toList());

            exp = new Example(TTruApplyCheckDo.class);
            criteria = exp.createCriteria();
            criteria.andIn("applyId", ids);

            List<TTruApplyCheckDo> checkList = applyCheckService.findByExample(exp);

            applyObjectService.deleteWithOther(list,checkList,null);

            String times = String.valueOf(System.currentTimeMillis());
            for (TTruApplyObjectDo info : list) {
//                try{
//                    if( StringUtils.isNotBlank( info.getProcessInstanceId() ) ) {
//                        Resp<WfTaskMeta> result = workflow.delete(info.getProcessInstanceId());
//                    }
//                }
//                catch (Exception e1){
//                    //resMap.setErr("删除流程异常");
//                    logger.error("删除异常:", e1);
//                }
//                try {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(user, info, "删除-申请对象" + times);
//                } catch (Exception e1) {
//                    //resMap.setErr("删除失败");
//                    logger.error("删除异常:", e1);
//                }
            }
            resMap.setOk("删除成功");

        } catch (Exception ex) {
            resMap.setErr("删除失败");
            logger.error("删除异常:", ex);
        }
        return resMap.getResultMap();
    }*/


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

            BaseRespone retVal = apiTruModelService.cancelApplyObject( id, relId, user.getUserId(), user.getTokenId() );
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

   /* @ApiOperation(value = "获取申请对象列表", notes = "获取申请对象列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "状态 1审核通过 2审核拒绝 21申请 22审批中", dataType = "Integer", required = false, paramType = "query" ,dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "qryCond", value = "模糊条件", dataType = "String", required = false, paramType = "query",dataTypeClass = String.class),
            @ApiImplicitParam(name = "currentPage", value = "页数", dataType = "Integer", required = false, paramType = "query",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", required = false, paramType = "query",dataTypeClass = Integer.class)
    })
    @RequestMapping(value = "/page", method = {RequestMethod.GET})
    @ResponseBody
    public Object page(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            TTruApplyObjectDo modelVo = new TTruApplyObjectDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            new PageBeanWrapper(modelVo, hru);
            modelVo.setQryCond(ComUtil.chgLikeStr(modelVo.getQryCond()));

            UserDo user = LoginUtil.getUser(request);
            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            if(StringUtils.isBlank( modelVo.getNodePartyId() ) ){
                modelVo.setNodePartyId( nodePartyDo.getId() );
            }
            if(StringUtils.isBlank( modelVo.getApplyAccount() ) ){
                modelVo.setApplyAccount( user.getUserName() );
            }

            long findCnt = applyObjectService.findBeanCnt(modelVo);
            modelVo.genPage(findCnt);

            //Map<String, TBydbDcServerDo> dcMap = dcserverService.findBaseList(null).stream().collect(Collectors.toMap(x -> x.getId(), x -> x));

            List<TTruApplyObjectDo> list = applyObjectService.findBeanList(modelVo);

            List<Object> retList = new ArrayList<>();
            for (TTruApplyObjectDo applyObjectDo : list) {
                HashMap<String,Object> datMap = new HashMap<>();
                MyBeanUtils.copyBean2Map( datMap, applyObjectDo );
//                TBydbDcServerDo dcdo = dcMap.get(applyObjectDo.getDcId());
//                if( dcdo != null ){
//                    datMap.put("dcName", dcdo.getDcName() );
//                    datMap.put("dcManageName",dcdo.getManageName());
//                }
                if(StringUtils.isNotBlank( applyObjectDo.getCandidateUser()) && applyObjectDo.getCandidateUser().startsWith(","))
                {
                    String cuser = applyObjectDo.getCandidateUser().substring(1);
                    if( cuser.charAt(cuser.length()-1)==','){
                        cuser = cuser.substring(0,cuser.length()-1);
                    }
                    datMap.put("candidateUser",cuser );
                }
                retList.add( datMap );
            }

            resMap.setPageInfo(modelVo.getPageSize(), modelVo.getCurrentPage());
            resMap.setOk(findCnt, retList, "获取申请对象列表成功");
        } catch (Exception ex) {
            resMap.setErr("获取申请对象列表失败");
            logger.error("获取申请对象列表失败:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取表字段", notes = "获取表字段")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "objectId", value = "表id", dataType = "String", required = true, paramType = "query"),
    })
    @RequestMapping(value = "/option", method = {RequestMethod.GET})
    @ResponseBody
    public Object option(String objectId,HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            TBydbFieldDo modelVo = new TBydbFieldDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo,hru);
            modelVo.setObjectId( objectId );

            List<TBydbFieldDo> list = fieldService.findBeanList(modelVo);
            resMap.setPageInfo(modelVo.getPageSize(), modelVo.getCurrentPage());
            resMap.setSingleOk(list, "获取表字段");
        } catch (Exception ex) {
            resMap.setErr("获取表字段失败");
            logger.error("获取表字段失败:", ex);
        }
        return resMap.getResultMap();
    }*/

}
