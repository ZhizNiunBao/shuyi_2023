package cn.bywin.business.controller.olk;


import cn.bywin.business.bean.bydb.TTruModelObjectDo;
import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.olk.TOlkSchemaDo;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.view.olk.UserGrantDbSchema;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.service.olk.OlkDataNodeService;
import cn.bywin.business.service.olk.OlkDatabaseService;
import cn.bywin.business.service.olk.OlkFieldService;
import cn.bywin.business.service.olk.OlkModelObjectService;
import cn.bywin.business.service.olk.OlkObjectService;
import cn.bywin.business.service.olk.OlkSchemaService;
import cn.bywin.business.trumodel.ApiOlkDbService;
import cn.bywin.business.trumodel.ApiTruModelService;
import cn.bywin.common.resp.ListResp;
import cn.bywin.common.resp.ObjectResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@Api(tags = "olk-库管理-olkschema")
@RequestMapping("/olkschema")
public class OlkSchemaController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Autowired
    private OlkDatabaseService databaseService;

    @Autowired
    private OlkSchemaService schemaService;

    @Autowired
    private OlkObjectService objectService;

    @Autowired
    private OlkFieldService fieldService;

    @Autowired
    private OlkModelObjectService modelObjService;

    @Autowired
    private ApiTruModelService apiTruModelService;

    @Autowired
    private ApiOlkDbService apiOlkDbService;

    @Autowired
    private NodePartyService nodePartyService;

    @Autowired
    private OlkDataNodeService dataNodeService;

    //@Autowired
    //private OlkDcServerService dcserverService;

    //String connectChar = "^";
    //String splitChar = "\\^";


    /*@ApiOperation(value = "新增olk库", notes = "新增olk库")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "info", value = "olk库", dataType = "TOlkSchemaDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public Object add(@RequestBody TOlkSchemaDo info, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null || StringUtils.isBlank(ud.getUserId())) {
                return resMap.setErr("请先登录").getResultMap();
            }
            if (StringUtils.isBlank(info.getSchemaName())) {
                return resMap.setErr("名称不能为空").getResultMap();
            }

            if (StringUtils.isBlank(info.getDbId())) {
                return resMap.setErr("数据库不能为空").getResultMap();
            }

            final TOlkDatabaseDo dbDo = databaseService.findById(info.getDbId());
            if( dbDo == null ){
                return resMap.setErr("数据库不存在").getResultMap();
            }

            info.setId(UUID.randomUUID().toString().replaceAll("-",""));
            LoginUtil.setBeanInsertUserInfo( info,ud );

            final long sameNameCount = schemaService.findSameNameCount( info );
            if( sameNameCount >0 ){
                return resMap.setErr("名称已使用").getResultMap();
            }

            schemaService.insertBean(info);

            resMap.setSingleOk(info, "保存成功");

        } catch (Exception ex) {
            ex.printStackTrace();
            resMap.setErr("保存失败");
            logger.error("保存异常:", ex);
        }
        return resMap.getResultMap();
    }*/

    @ApiOperation(value = "修改olk库", notes = "修改olk库")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelVo", value = "olk库", dataType = "TOlkSchemaDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update( @RequestBody TOlkSchemaDo bean, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
//            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
//            logger.debug( "{}",hru.getAllParaData() );
//            TOlkSchemaDo info = schemaService.findById(hru.getNvlPara("id"));
            TOlkSchemaDo info = schemaService.findById( bean.getId() );

            if ( info == null ) {
                return resMap.setErr( "内容不存在" ).getResultMap();
            }

            TOlkSchemaDo oldData = new TOlkSchemaDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, oldData );
            MyBeanUtils.copyBeanNotNull2Bean( bean, info );

            //new PageBeanWrapper( info,hru,"");
            info.setSchemaName( oldData.getSchemaName() );
            info.setDbId( oldData.getDbId() );

            if ( StringUtils.isBlank( info.getSchemaName() ) ) {
                return resMap.setErr( "名称不能为空" ).getResultMap();
            }

            if ( StringUtils.isBlank( info.getDbId() ) ) {
                return resMap.setErr( "数据目录不能为空" ).getResultMap();
            }

            final TOlkDatabaseDo dbDo = databaseService.findById( info.getDbId() );
            if ( dbDo == null ) {
                return resMap.setErr( "数据目录不存在" ).getResultMap();
            }

            final long sameNameCount = schemaService.findSameNameCount( info );
            if ( sameNameCount > 0 ) {
                return resMap.setErr( "名称已使用" ).getResultMap();
            }
            if( oldData.getEnable() != null ) {
                info.setEnable( oldData.getEnable() );
            }
            else{
                if( info.getEnable() == null) {
                    info.setEnable( 1 );
                }
            }

            info.setSynFlag( 0 );
            List<TOlkSchemaDo> updList = new ArrayList<>();
            updList.add( info );
            List<TOlkDatabaseDo> dbList = new ArrayList<>();
            if(oldData.getEnable() == null || !oldData.getEnable().equals( info.getEnable() )) {
                if ( info.getEnable() == 1 && dbDo.getEnable() == 0 ) {
                    dbDo.setEnable( 1 );
                    dbList.add( dbDo );
                }
                schemaService.updateBeanWithFlag( updList, dbList );
            }
            else {
                schemaService.updateBean( info );
            }
            resMap.setSingleOk( info, "保存成功" );

        }
        catch ( Exception ex ) {
            ex.printStackTrace();
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "批量启用或禁用olk库", notes = "批量启用或禁用olk库")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "库id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "enable", value = "启用1，禁用0", dataType = "Integer", required = true, paramType = "query")
    })
    @RequestMapping(value = "/enabledata", method = {RequestMethod.POST})
    @ResponseBody
    public Object enableData( String id, Integer enable, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        String actType = "操作";
        try {
            logger.debug( "id:{},enable:{}", id, enable );
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
            if ( enable == null || !(enable == 0 || enable == 1) ) {
                return resMap.setErr( "标记不正确" ).getResultMap();
            }
            List<String> split = Arrays.asList( id.split( "(,|\\s)+" ) );
            Example exp = new Example( TOlkSchemaDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", split );

            if ( enable == 0 ) {
                actType = "禁用";
            }
            else {
                actType = "启用";
            }

            String act = "修改-" + actType + "olk库" + System.currentTimeMillis();
            if ( enable == 1 ) {
                criteria.andCondition( " (enable =0  or enable is null )" );
            }
            else {
                criteria.andEqualTo( "enable", 1 );
            }
            List<TOlkSchemaDo> chgList = schemaService.findByExample( exp );
            if ( chgList.size() != split.size() ) {
                return resMap.setErr( " 数据已变化不能操作" ).getResultMap();
            }
            List<String> userIdList = chgList.stream().map( x -> x.getUserId() ).distinct().collect( Collectors.toList() );
            if(userIdList.size()!=1 ){
                return resMap.setErr( "不能同时操作不同用户数据" ).getResultMap();
            }
            if ( enable == 0 ) {
                String ids = "'" + StringUtils.join( split, "','" ) + "'";
                exp = new Example( TOlkModelObjectDo.class );
                criteria = exp.createCriteria();
                criteria.andCondition( " real_obj_id in (select a.id from t_olk_object a, t_olk_schema b where a.schema_id =b.id  and b.id in( " + ids + ") )" );
                criteria.andEqualTo("userId" ,userIdList.get( 0 ));
                int cnt = modelObjService.findCountByExample( exp );
                if ( cnt > 0 ) {
                    return resMap.setErr( "数据被使用，不能".concat( actType ) ).getResultMap();
                }
            }
            List<TOlkSchemaDo> oldList = new ArrayList<>();
            List<TOlkDatabaseDo> dbList = null;
            if ( enable == 1 ) {
                for ( TOlkSchemaDo info : chgList ) {
                    TOlkSchemaDo old = new TOlkSchemaDo();
                    MyBeanUtils.copyBeanNotNull2Bean( info, old );
                    oldList.add( old );
                    info.setEnable( 1 );
                    info.setSynFlag( 0 );
                }
                List<String> idList = chgList.stream().map( x -> x.getDbId() ).distinct().collect( Collectors.toList() );
                exp = new Example( TOlkDatabaseDo.class );
                criteria = exp.createCriteria();
                criteria.andIn( "id", idList );
                criteria.andCondition( " (enable =0  or enable is null )" );
                dbList = databaseService.findByExample( exp );
                for ( TOlkDatabaseDo tOlkDatabaseDo : dbList ) {
                    tOlkDatabaseDo.setEnable( enable );
                }
            }
            else {
                for ( TOlkSchemaDo info : chgList ) {
                    TOlkSchemaDo old = new TOlkSchemaDo();
                    MyBeanUtils.copyBeanNotNull2Bean( info, old );
                    oldList.add( old );
                    info.setEnable( 0 );
                    info.setSynFlag( 0 );
                }
            }

            schemaService.updateBeanWithFlag( chgList, dbList );

            for ( int i = 0; i < chgList.size(); i++ ) {
                TOlkSchemaDo info = chgList.get( i );
                TOlkSchemaDo old = oldList.get( i );
                HashMap<String, Object> map = new HashMap<>();
                if ( dbList != null ) {
                    for ( TOlkDatabaseDo tOlkDatabaseDo : dbList ) {
                        if ( tOlkDatabaseDo.getId().equals( info.getDbId() ) ) {
                            map.put( "db", tOlkDatabaseDo );
                            break;
                        }
                    }
                }
                String msg = JsonUtil.toJson( map );
                //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(ud, old, info,msg, act);
            }
            if ( enable == 0 ) {
                resMap.setOk( actType + "成功" );
            }
            else {
                resMap.setOk( actType + "成功" );
            }

        }
        catch ( Exception ex ) {
            resMap.setErr( actType + "失败" );
            logger.error( actType + "异常:", ex );
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "olk库内容", notes = "olk库内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "olk库 id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    @ResponseBody
    public Object info( String id ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( StringUtils.isBlank( id ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }
            TOlkSchemaDo modelVo = schemaService.findById( id );
            resMap.setSingleOk( modelVo, "成功" );

        }
        catch ( Exception ex ) {
            ex.printStackTrace();
            resMap.setErr( "查询失败" );
            logger.error( "查询异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除olk库", notes = "删除olk库")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "olk库 id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public Object delete( String id, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( StringUtils.isBlank( id ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }
            UserDo user = LoginUtil.getUser( request );
            List<String> idList = Arrays.asList( id.split( "(,|\\s)+" ) ).stream().distinct().filter( StringUtils::isNotBlank ).collect( Collectors.toList());
            if( idList.size()==0){
                return resMap.setErr( "id不能为空" ).getResultMap();
            }

//            Example exp = new Example( TOlkObjectDo.class );
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andIn( "schemaId", idList );
//            //criteria.andCondition( " id in(select table_id from t_olk_ds_entity where table_id is not null ) ");
//            int cnt = objectService.findCountByExample( exp );
//            if ( cnt > 0 ) {
//                return resMap.setErr( "库有下级对象被使用，不能删除" ).getResultMap();
//            }

            Example exp = new Example( TOlkSchemaDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", idList );
            criteria.andEqualTo( "userId", user.getUserId() );
            List<TOlkSchemaDo> list = schemaService.findByExample( exp );

            if (list.size()==0) {
                return resMap.setErr("没有数据可删除").getResultMap();
            }

            if ( list.size() != idList.size() ) {
                return resMap.setErr( "数据已变化，删除失败" ).getResultMap();
            }

            if ( list.size() != idList.size() ) {
                return resMap.setErr( "数据已变化，删除失败" ).getResultMap();
            }
            List<String> userIdList = list.stream().map( x -> x.getUserId() ).distinct().collect( Collectors.toList() );
            String userId = "";
            if( userIdList.size()>1){
                return resMap.setErr( "不能同时删除不同用户数据" ).getResultMap();
            }
            else if(userIdList.size()==1){
                userId = userIdList.get( 0 );
            }

            if(StringUtils.isNotBlank( userId )) {
                String ids = "'" + StringUtils.join( idList, "','" ) + "'";
                exp = new Example( TTruModelObjectDo.class );
                criteria = exp.createCriteria();
                criteria.andCondition( " real_obj_id in (select a.id from t_olk_object a, t_olk_schema b where a.schema_id =b.id  and b.id in( " + ids + ") )" );
                criteria.andEqualTo( "userId", userId );
                int cnt = modelObjService.findCountByExample( exp );
                if ( cnt > 0 ) {
                    return resMap.setErr( "数据被使用，不能删除" ).getResultMap();
                }
            }

            ObjectResp<String> retVal = apiOlkDbService.delOlkSchema( idList,"0", user.getTokenId() );
            if( retVal.isSuccess() ) {
                schemaService.deleteWhithRel( list );
            }
            else{
                return retVal;
            }
            resMap.setOk( "删除成功" );
//            String times = String.valueOf(System.currentTimeMillis());
//            for (TOlkSchemaDo info : list) {
//                try {
//
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(user, info, "删除-olk库" + times);
//                } catch (Exception e1) {
//                    logger.error("登记删除库{}异常:",info.getId(), e1);
//                }
//            }
        }
        catch ( Exception ex ) {
            resMap.setErr( "删除失败" );
            logger.error( "删除异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取库列表", notes = "获取库列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qryCond", value = "模糊条件", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "页数", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", required = false, paramType = "query")
    })
    @RequestMapping(value = "/page", method = {RequestMethod.GET})
    @ResponseBody
    public Object page( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            TOlkSchemaDo modelVo = new TOlkSchemaDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo, hru );
            modelVo.setQryCond( ComUtil.chgLikeStr( modelVo.getQryCond() ) );
            modelVo.setSchemaName( ComUtil.chgLikeStr( modelVo.getSchemaName() ) );

            long findCnt = schemaService.findBeanCnt( modelVo );
            //modelVo.genPage();
            modelVo.genPage( findCnt );

            List<TOlkSchemaDo> list = schemaService.findBeanList( modelVo );

            resMap.setPageInfo( modelVo.getPageSize(), modelVo.getCurrentPage() );
            resMap.setOk( findCnt, list, "获取库列表成功" );
        }
        catch ( Exception ex ) {
            ex.printStackTrace();
            resMap.setErr( "获取库列表失败" );
            logger.error( "获取库列表失败:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "对用户授权", notes = "对用户授权")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "stype", value = "类型 database,schema,table ", dataType = "String",example = "database,schema,table", required = true, paramType = "query"),
            //@ApiImplicitParam(name = "dataId", value = "数据id", dataType = "String", required = true, paramType = "query"),
            //@ApiImplicitParam(name = "userId", value = "用户Id", dataType = "String", required = true, paramType = "query")

    })
    @RequestMapping(value = "/grantouser", method = {RequestMethod.POST})
    @ResponseBody
    public Object grantoUser( @RequestBody UserGrantDbSchema bean, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {

            if ( bean == null) {
                return resMap.setErr( "数据不能为空" ).getResultMap();
            }
            List<String> userIdList = bean.getUserId();
            List<String> dataIdList = bean.getDataId();

            if (dataIdList == null || dataIdList.size() == 0 ) {
                return resMap.setErr( "数据id不能为空" ).getResultMap();
            }

            if ( userIdList == null || userIdList.size() == 0 ) {
                return resMap.setErr( "用户id不能为空" ).getResultMap();
            }

            UserDo user = LoginUtil.getUser( request );

            SysUserDo qryUser = new SysUserDo();
            qryUser.setId( String.join( ",", userIdList ) );
            qryUser.setIsLock( 1 );
            ListResp<SysUserDo> retUser = apiTruModelService.nodeUserList( qryUser, user.getTokenId() );
            if ( !retUser.isSuccess() ) {
                return resMap.setErr( "验证用户失败，" + retUser.getMsg() ).getResultMap();
            }
            if ( retUser.getData().size() != userIdList.size() ) {
                return resMap.setErr( "部分用户不存在" ).getResultMap();
            }
            Map<String, SysUserDo> userMap = retUser.getData().stream().collect( Collectors.toMap( x -> x.getId(), x -> x ) );


            Example exp = new Example( TOlkSchemaDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", dataIdList ).andEqualTo( "enable", 1 );
            List<TOlkSchemaDo> objList = schemaService.findByExample( exp );
            if ( objList.size() != dataIdList.size() ) {
                return resMap.setErr( "部分对象已变化或不存在" ).getResultMap();
            }

            List<FDataApproveDo> list = new ArrayList<>();
            for ( TOlkSchemaDo objDo : objList ) {
                for ( String s : userIdList ) {
                    if ( s.equals( objDo.getUserId() ) ) {
                        return resMap.setErr( "部分为自己对象无需授权" ).getResultMap();
                    }
                    FDataApproveDo da = new FDataApproveDo();
                    da.setDataId( objDo.getId() );
                    da.setNodeId( objDo.getNodePartyId() );
                    da.setUserId( objDo.getUserId() );
                    da.setUserName( objDo.getUserName() );
                    da.setNodeId( objDo.getNodePartyId() );
                    da.setDataCatalog( "schema" );
                    da.setTypes( 1 );
                    da.setApprove( 1 );
                    da.setApproval( "直接授权" );
                    da.setId( ComUtil.genId() );
                    SysUserDo userDo = userMap.get( s );
                    da.setCreatorId( userDo.getId() );
                    da.setCreatorAccount( userDo.getMobile() );
                    da.setCreatorName( userDo.getUsername() );
                    da.setCreatedTime( ComUtil.getCurTimestamp() );
                    list.add( da );
                }
            }
            ListResp<FDataApproveDo> retVal = apiOlkDbService.saveGrantObject( list, user.getTokenId() );
            if ( !retVal.isSuccess() ) {
                return resMap.setErr( "授权失败," + retVal.getMsg() ).getResultMap();
            }

            resMap.setOk( "授权成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "授权失败" );
            logger.error( "授权失败:", ex );
        }
        return resMap.getResultMap();
    }

    /*@ApiOperation(value = "上报表", notes = "上报表")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "modelVo", value = "数据源", dataType = "TDatasourceDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/synschema", method = {RequestMethod.POST})
    @ResponseBody
    public Object synSchema(String schemaId,Integer flag,String nodeId, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
            if( StringUtils.isBlank( schemaId ) ){
                return resMap.setErr( "库id不能为空" ).getResultMap();
            }
            List<String> split = Arrays.asList( schemaId.split( "(,|\\s)+" ) );

            Example exp = new Example( TOlkObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "schemaId", split);

            List<TOlkObjectDo> list = objectService.findByExample(exp);
            int cnt  = list.size();
            if( cnt == 0 ){
                return resMap.setErr("没有数据可操作").getResultMap();
            }
            if( list.size() != split.size() ){
                return resMap.setErr("数据已变化，操作失败").getResultMap();
            }

            List<String> nodeIdList = Arrays.asList( nodeId.split( "(,|\\s)+" ) );

            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            Map<String,TOlkDatabaseDo> dbMap = new HashMap<>();
            Map<String,TOlkSchemaDo> schemaMap = new HashMap<>();

            for ( TOlkObjectDo info : list ) {
                if( !dbMap.containsKey( info.getDbId() )) {
                    TOlkDatabaseDo dbDo = databaseService.findById( info.getDbId() );
                    if( dbDo.getShareFlag() == null || dbDo.getShareFlag() !=1 ){
                        dbDo.setShareFlag( 1 );
                        dbDo.setNodePartyId( nodePartyDo.getId() );
                        dbDo.setShareTime( ComUtil.getCurTimestamp() );

                        Map<String, Object> retMap = apiTruModelService.synDatabase( dbDo, ud.getTokenId() );
                        if ( retMap.containsKey( "success" ) && !(boolean) retMap.get( "success" ) ) {
                            return resMap.setErr( "保存完成，同步目录信息失败" ).getResultMap();
                        }
                        TOlkDatabaseDo tmp = new TOlkDatabaseDo();
                        tmp.setId( dbDo.getId() );
                        tmp.setNodePartyId( dbDo.getNodePartyId() );
                        tmp.setShareTime( dbDo.getShareTime() );
                        tmp.setShareFlag( dbDo.getShareFlag() );
                        databaseService.updateNoNull( tmp );
                        dbMap.put( dbDo.getId(),dbDo );
                    }
                }

                if( !schemaMap.containsKey( info.getSchemaId() )) {
                    TOlkSchemaDo schemaDo = schemaService.findById( info.getSchemaId() );
                    if( schemaDo.getShareFlag() == null || schemaDo.getShareFlag() !=1 ){
                        schemaDo.setShareFlag( 1 );
                        schemaDo.setNodePartyId( nodePartyDo.getId() );
                        schemaDo.setShareTime( ComUtil.getCurTimestamp() );

                        Map<String, Object> retMap = apiTruModelService.synSchema( schemaDo, ud.getTokenId() );
                        if ( retMap.containsKey( "success" ) && !(boolean) retMap.get( "success" ) ) {
                            return resMap.setErr( "保存完成，同步目录信息失败" ).getResultMap();
                        }
                        TOlkSchemaDo tmp = new TOlkSchemaDo();
                        tmp.setId( schemaDo.getId() );
                        tmp.setNodePartyId( schemaDo.getNodePartyId() );
                        tmp.setShareTime( schemaDo.getShareTime() );
                        tmp.setShareFlag( schemaDo.getShareFlag() );
                        schemaService.updateNoNull( tmp );
                        schemaMap.put( schemaDo.getId(),schemaDo );
                    }
                }
            }

            for ( TOlkObjectDo info : list ) {
                if ( StringUtils.isBlank( info.getNodePartyId() ) ) {
                    info.setNodePartyId( nodePartyDo.getId() );
                }
                List<TOlkDataNodeDo> tabNodeList = new ArrayList<>();
                List<TOlkDataNodeDo> list1 = dataNodeService.findByDataId( info.getId() );
                List<TOlkDataNodeDo> delDnList = new ArrayList<>();
                List<TOlkDataNodeDo> addDnList = new ArrayList<>();
                List<TOlkDataNodeDo> modDnList = new ArrayList<>();
                for ( TOlkDataNodeDo dn : list1 ) {
                    if( !nodeIdList.contains( dn.getId() ) ){
                        delDnList.add( dn );
                    }
                    else{
                        modDnList.add( dn );
                    }
                }
                for ( String s : nodeIdList ) {
                    boolean bfound = false;
                    for ( TOlkDataNodeDo dn : list1 ) {
                        if( dn.getNodeId().equals( s )) {
                            bfound = true;
                        }
                    }
                    if(!bfound) {
                        TOlkDataNodeDo dataNodeDo = new TOlkDataNodeDo();
                        dataNodeDo.setNodeId( s );
                        dataNodeDo.setDataId( info.getId() );
                        dataNodeDo.setDataType( "db" );
                        dataNodeDo.setId( ComUtil.genId() );
                        LoginUtil.setBeanInsertUserInfo( dataNodeDo, ud );
                        addDnList.add( dataNodeDo );
                    }
                }

                tabNodeList.addAll( addDnList );
                tabNodeList.addAll( modDnList );

                info.setShareFlag( flag );
                info.setShareTime( ComUtil.getCurTimestamp() );
                List<TOlkFieldDo> fieldList = fieldService.selectByObjectId( info.getId() );
                OlkObjectFieldsVo objFld = new OlkObjectFieldsVo();
                MyBeanUtils.copyBeanNotNull2Bean( info,objFld );
                objFld.setFieldList( fieldList );
                objFld.setDataNodeList(  tabNodeList );
                List<OlkObjectFieldsVo> bofList = new ArrayList<>();
                bofList.add( objFld );
                Map<String, Object> retMap = apiTruModelService.synTable( bofList, ud.getTokenId() );
                if ( retMap.containsKey( "success" ) && !(boolean) retMap.get( "success" ) ) {
                    return resMap.setErr( "保存完成，更新同步信息失败" ).getResultMap();
                }
                TOlkObjectDo tmp = new TOlkObjectDo();
                tmp.setId( info.getId() );
                tmp.setNodePartyId( info.getNodePartyId() );
                tmp.setShareTime( info.getShareTime() );
                tmp.setShareFlag( info.getShareFlag() );
                objectService.updateWithNodes( tmp,addDnList,null,delDnList );

//                else {
//                    Map<String, Object> retMap = apiTruModelService.delTable( info.getId(), ud.getTokenId() );
//                    if ( !retMap.containsKey( "success" ) ){
//                        return resMap.setErr( "保存完成，取消同步信息失败" ).getResultMap();
//                    }
//                    if(!(boolean) retMap.get( "success" ) ) {
//                        return resMap.setErr( "保存完成，取消同步信息失败,".concat(  (String)retMap.get( "msg" ) ) ).getResultMap();
//                    }
//                    info.setShareFlag( 0 );
//                    info.setShareTime( ComUtil.getCurTimestamp() );
//                    TOlkObjectDo tmp = new TOlkObjectDo();
//                    tmp.setId( info.getId() );
//                    tmp.setNodePartyId( info.getNodePartyId() );
//                    tmp.setShareTime( info.getShareTime() );
//                    tmp.setShareFlag( info.getShareFlag() );
//                    olkObjectService.updateNoNull( tmp );
//                }
            }

//            if( info.getShareFlag() == 1){
//                apiTruModelService.synDbsource( info );
//            }

            //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(ud, old, info, "修改-数据源");

            resMap.setOk( "保存成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }*/

}
