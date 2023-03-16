package cn.bywin.business.controller.bydb;


import cn.bywin.business.bean.bydb.TBydbDatasetDo;
import cn.bywin.business.bean.bydb.TBydbObjectDo;
import cn.bywin.business.bean.bydb.TTruDiscussObjectDo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.service.bydb.BydbDatabaseService;
import cn.bywin.business.service.bydb.BydbDatasetService;
import cn.bywin.business.service.bydb.BydbFieldService;
import cn.bywin.business.service.bydb.BydbObjectService;
import cn.bywin.business.service.bydb.BydbSchemaService;
import cn.bywin.business.service.bydb.TruDiscussObjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
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
import tk.mybatis.mapper.entity.Example;


@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        maxAge = 3600)
@RestController
@Api(tags = "可信建模-评论管理-truiscussobject")
@RequestMapping("/truiscussobject")
public class BydbDiscussObjectController extends BaseController {
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

    @Autowired
    private TruDiscussObjectService discussObjectService;


    @ApiOperation(value = "新增评论对象", notes = "新增评论对象")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "info", value = "评论对象", dataType = "TBydbFieldDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public Object add(@RequestBody TTruDiscussObjectDo bean, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null || StringUtils.isBlank(ud.getUserId())) {
                return resMap.setErr("请先登录").getResultMap();
            }
            if ( bean == null || StringUtils.isBlank( bean.getRelId() )  ) {
                return resMap.setErr("内容不能为空").getResultMap();
            }

            Date now = new Date();

            //List<String> objIdList = new ArrayList<>();
            //List<String> dsIdList = new ArrayList<>();

            TTruDiscussObjectDo foDo = new TTruDiscussObjectDo();
            foDo.setDiscussAccount( ud.getUserName() );
            foDo.setRelId(bean.getRelId());
            foDo.setDiscussTime( now );
            foDo.setId( ComUtil.genId() );
            foDo.setDiscussNote( bean.getDiscussNote() );
            foDo.setDiscussTime( ComUtil.getCurTimestamp() );
            LoginUtil.setBeanInsertUserInfo( foDo, ud );

            String relId = foDo.getRelId();
            if( StringUtils.isBlank( relId ) ) {
                return resMap.setErr("评论对象id不能为空").getResultMap();
            }
            else if( relId.startsWith("db")){
                foDo.setObjectId( relId.substring(2));
                foDo.setStype("db");
//                if( objIdList.contains( foDo.getObjectId() )){
//                    return resMap.setErr("评论对象id不能重复").getResultMap();
//                }
//                objIdList.add( foDo.getObjectId() );
                TBydbObjectDo objectDo = objectService.findById(foDo.getObjectId());
                if( objectDo == null){
                    return resMap.setErr("评论对象表不存在").getResultMap();
                }
                //foDo.setDcId( objectDo.getDcId() );
                foDo.setUserId( objectDo.getUserId() );
                foDo.setUserAccount( objectDo.getUserAccount() );
                foDo.setUserName( objectDo.getUserName() );
                //foDo.setDbId( objectDo.getDbId() );
                //foDo.setSchemaId( objectDo.getSchemaId() );
                foDo.setObjName( objectDo.getObjectName());
                foDo.setObjFullName( objectDo.getObjFullName() );
                foDo.setObjChnName( objectDo.getObjChnName());

            }else if( relId.startsWith("ds") ){
                foDo.setDatasetId( relId.substring(2));
                foDo.setStype("ds");
//                if( dsIdList.contains( foDo.getDatasetId() )){
//                    return resMap.setErr("评论对象id不能重复").getResultMap();
//                }
//                dsIdList.add( foDo.getDatasetId() );
                TBydbDatasetDo datasetDo = datasetService.findById(foDo.getDatasetId());
                if( datasetDo == null){
                    return resMap.setErr("评论数据集不存在").getResultMap();
                }
                foDo.setUserId( datasetDo.getUserId() );
                foDo.setUserAccount(datasetDo.getUserAccount());
                foDo.setUserName( datasetDo.getUserName() );
                foDo.setObjName( datasetDo.getSetCode());
                foDo.setObjFullName( datasetDo.getViewName() );
                foDo.setObjChnName( datasetDo.getSetChnName());
            }

            discussObjectService.insertBean( foDo );

            //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).addLog(ud, foDo, "新增-评论对象" );

            resMap.setOk( "保存成功");

        } catch (Exception ex) {
            resMap.setErr("保存失败");
            logger.error("保存异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "修改评论对象", notes = "修改评论对象")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "modelVo", value = "评论对象", dataType = "TBydbFieldDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update(@RequestBody TTruDiscussObjectDo bean, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null || StringUtils.isBlank(ud.getUserId())) {
                return resMap.setErr("请先登录").getResultMap();
            }

            TTruDiscussObjectDo info = discussObjectService.findById(bean.getId());

            if (info == null) {
                return resMap.setErr("内容不存在").getResultMap();
            }

            TTruDiscussObjectDo oldData = new TTruDiscussObjectDo();
            MyBeanUtils.copyBeanNotNull2Bean(info, oldData);

            MyBeanUtils.copyBeanNotNull2Bean(bean, info);

            //new PageBeanWrapper(info, hru, "");

//            if (StringUtils.isBlank(info.getFieldName())) {
//                return resMap.setErr("名称不能为空").getResultMap();
//            }


//            TBydbObjectDo objectDo = objectService.findById(info.getObjectId());
//            if (objectDo == null) {
//                return resMap.setErr("对象不存在").getResultMap();
//            }
//            info.setSchemaId(objectDo.getSchemaId());
//            info.setDbId(objectDo.getDbId());
//
//            info.setReplaceStatement( chgStateField( info.getChgStatement() , info.getFieldName() ));

//            final long sameNameCount = fieldService.findSameNameCount( info );
//            if( sameNameCount >0 ){
//                return resMap.setErr("名称已使用").getResultMap();
//            }

            discussObjectService.updateBean(info);

            resMap.setSingleOk(info, "保存成功");

        } catch (Exception ex) {
            resMap.setErr("保存失败");
            logger.error("保存异常:", ex);
        }
        return resMap.getResultMap();
    }
    @ApiOperation(value = "评论对象内容", notes = "评论对象内容")
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
            TTruDiscussObjectDo info = discussObjectService.findById(id);
            resMap.setSingleOk(info, "成功");

        } catch (Exception ex) {
            resMap.setErr("查询失败");
            logger.error("查询异常:", ex);
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "删除评论对象", notes = "删除评论对象")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "评论对象id", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "relId", value = "评论对象关联id", dataType = "String", required = false, paramType = "query")
    })
    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public Object delete(String id,String relId, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if (StringUtils.isBlank(id) && StringUtils.isBlank(relId)) {
                return resMap.setErr("id和关联id不能同时为空").getResultMap();
            }
            UserDo user = LoginUtil.getUser(request);
//            if( !id.matches("^[a-zA-Z0-9\\-_,]*$") ){
//                return resMap.setErr("id有非法字符").getResultMap();
//            }
            List<String> ids = null;
            if( StringUtils.isNotBlank( id) ){
                ids = Arrays.asList(id.split(",|\\s+"));
            }
            List<String> rels = null;
            if( StringUtils.isNotBlank( relId ) ){
                rels = Arrays.asList(relId.split(",|\\s+"));
            }
            Example exp = new Example(TTruDiscussObjectDo.class);
            Example.Criteria criteria = exp.createCriteria();
            if( ids != null) {
                criteria.andIn("id", ids);
            }
            if( rels != null) {
                criteria.andIn("relId", rels);
            }
            List<TTruDiscussObjectDo> list = discussObjectService.findByExample(exp);
            int cnt = list.size();
            if (cnt == 0) {
                return resMap.setErr("没有数据可删除").getResultMap();
            }
            if (ids!= null && list.size() != ids.size()) {
                return resMap.setErr("有数据不存在").getResultMap();
            }
//            if (rels!= null && list.size() != rels.size()) {
//                return resMap.setErr("有数据不存在").getResultMap();
//            }
            ids = list.stream().map(x -> x.getId()).collect(Collectors.toList());

            discussObjectService.deleteByIds(ids);

            String times = String.valueOf(System.currentTimeMillis());
            for (TTruDiscussObjectDo info : list) {
                try {
//                    HashMap<String,Object> data = new HashMap<>();
//                    List<TBydbItemObjectDo> collect1 = itemObjlist.stream().filter(x -> info.getId().equals(x.getObjectId())).collect(Collectors.toList());
//
//                    data.put("delItemObject", collect1);
//                    List<TBydbGroupObjectDo> collect2 = groupObjlist.stream().filter(x -> info.getId().equals(x.getObjectId())).collect(Collectors.toList());
//                    data.put("delGroupObject", collect2);
//                    String msg = JsonUtil.toJson(data);
                    //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(user, info, "删除-评论对象" + times);
                } catch (Exception e1) {
                    resMap.setErr("删除失败");
                    logger.error("删除异常:", e1);
                }
            }
            resMap.setOk("删除成功");

        } catch (Exception ex) {
            resMap.setErr("删除失败");
            logger.error("删除异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取评论对象列表", notes = "获取评论对象列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qryCond", value = "模糊条件", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "页数", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", required = false, paramType = "query")
    })
    @RequestMapping(value = "/page", method = {RequestMethod.GET})
    @ResponseBody
    public Object page(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            TTruDiscussObjectDo modelVo = new TTruDiscussObjectDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo,hru);
            modelVo.setQryCond(ComUtil.chgLikeStr(modelVo.getQryCond()));

            long findCnt = discussObjectService.findBeanCnt(modelVo);
            modelVo.genPage(findCnt);

            List<TTruDiscussObjectDo> list = discussObjectService.findBeanList(modelVo);

            resMap.setPageInfo(modelVo.getPageSize(), modelVo.getCurrentPage());
            resMap.setOk(findCnt, list, "获取评论对象列表成功");
        } catch (Exception ex) {
            resMap.setErr("获取评论对象列表失败");
            logger.error("获取评论对象列表失败:", ex);
        }
        return resMap.getResultMap();
    }

    /*@ApiOperation(value = "获取表字段", notes = "获取表字段")
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
