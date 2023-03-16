package cn.bywin.business.controller.bydb;


import cn.bywin.business.bean.bydb.TBydbDataNodeDo;
import cn.bywin.business.bean.bydb.TBydbDatabaseDo;
import cn.bywin.business.bean.bydb.TBydbFieldDo;
import cn.bywin.business.bean.bydb.TBydbObjectDo;
import cn.bywin.business.bean.bydb.TBydbSchemaDo;
import cn.bywin.business.bean.bydb.TTruModelObjectDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.view.SynObjDataVo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.service.bydb.BydbDataNodeService;
import cn.bywin.business.service.bydb.BydbDatabaseService;
import cn.bywin.business.service.bydb.BydbFieldService;
import cn.bywin.business.service.bydb.BydbObjectService;
import cn.bywin.business.service.bydb.BydbSchemaService;
import cn.bywin.business.service.bydb.TruModelObjectService;
import cn.bywin.business.service.federal.DataSourceService;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.trumodel.ApiTruModelService;
import cn.bywin.common.resp.ObjectResp;
import cn.jdbc.IJdbcOp;
import cn.jdbc.JdbcOpBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
@Api(tags = "联邦学习-对象管理-bydbobject")
@RequestMapping("/bydobject")
public class BydbObjectController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Autowired
    private DataSourceService dbSourceService;

    @Autowired
    private BydbDatabaseService databaseService;

    @Autowired
    private BydbSchemaService schemaService;

    @Autowired
    private BydbObjectService bydbObjectService;

    @Autowired
    private BydbFieldService fieldService;

    @Autowired
    private TruModelObjectService modelObjService;

    @Autowired
    private ApiTruModelService apiTruModelService;

    @Autowired
    private NodePartyService nodePartyService;

    @Autowired
    private BydbDataNodeService dataNodeService;

//    @Autowired
//    private BydbDsEntityService dsEntityService;

    @ApiOperation(value = "修改bydb对象", notes = "修改bydb对象")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelVo", value = "bydb对象", dataType = "TBydbObjectDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update( @RequestBody TBydbObjectDo bean, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
//            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
//            logger.debug( "{}",hru.getAllParaData() );
//            TBydbObjectDo info = objectService.findById(hru.getNvlPara("id"));

            TBydbObjectDo info = bydbObjectService.findById( bean.getId() );

            if ( info == null ) {
                return resMap.setErr( "内容不存在" ).getResultMap();
            }

            TBydbObjectDo oldData = new TBydbObjectDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, oldData );

            MyBeanUtils.copyBeanNotNull2Bean( bean, info );

            //new PageBeanWrapper( info,hru,"");
            info.setDbId( oldData.getDbId() );
            info.setSchemaId( oldData.getSchemaId() );
            info.setObjectName( oldData.getObjectName() );
            info.setUserId( oldData.getUserId() );
            info.setUserAccount( oldData.getUserAccount() );
            info.setUserName( oldData.getUserName() );
            info.setSynFlag( 0 );

            if ( StringUtils.isBlank( info.getObjectName() ) ) {
                return resMap.setErr( "名称不能为空" ).getResultMap();
            }

//            if (StringUtils.isBlank(info.getStype())) {
//                return resMap.setErr("类型不能为空").getResultMap();
//            }

            if ( StringUtils.isBlank( info.getSchemaId() ) ) {
                return resMap.setErr( "模式不能为空" ).getResultMap();
            }

            final TBydbSchemaDo schemaDo = schemaService.findById( info.getSchemaId() );
            if ( schemaDo == null ) {
                return resMap.setErr( "数据库不存在" ).getResultMap();
            }

            if ( StringUtils.isBlank( info.getDbId() ) ) {
                info.setDbId( schemaDo.getDbId() );
            }
            if ( StringUtils.isBlank( info.getUserAccount() ) ) {
                info.setUserAccount( schemaDo.getUserAccount() );
            }

            TBydbDatabaseDo dbDo = databaseService.findById( info.getDbId() );
            if ( dbDo == null ) {
                return resMap.setErr( "数据目录不存在" ).getResultMap();
            }
            if ( !info.getDbId().equals( schemaDo.getDbId() ) ) {
                return resMap.setErr( "数据库与目录不一致" ).getResultMap();
            }

            final long sameNameCount = bydbObjectService.findSameNameCount( info );
            if ( sameNameCount > 0 ) {
                return resMap.setErr( "名称已使用" ).getResultMap();
            }
            List<TBydbDatabaseDo> dbList = new ArrayList<>();
            List<TBydbSchemaDo> schemaList = new ArrayList<>();

            if ( info.getEnable() == 1 && dbDo.getEnable() == 0 ) {
                dbDo.setEnable( 1 );
                dbList.add( dbDo );
            }
            if ( info.getEnable() == 1 && schemaDo.getEnable() == 0 ) {
                schemaDo.setEnable( 1 );
                schemaList.add( schemaDo );
            }
            if( oldData.getEnable() != null ) {
                info.setEnable( oldData.getEnable() );
            }
            else{
                if( info.getEnable() == null) {
                    info.setEnable( 1 );
                }
            }

            if( oldData.getEnable() == null || !oldData.getEnable().equals(  info.getEnable() )) {
                List<TBydbObjectDo> updList = new ArrayList<>();
                updList.add( info );
                info.setSynFlag( 0 );

                bydbObjectService.updateBeanWithFlag( updList, dbList, schemaList );
            }
            else{
                bydbObjectService.updateBean( info );
            }

//            HashMap<String, Object> map = new HashMap<>();
//            if ( dbList.size() > 0 ) {
//                map.put( "db", dbList.get( 0 ) );
//            }
//            if ( schemaList.size() > 0 ) {
//                map.put( "schema", schemaList.get( 0 ) );
//            }
//            String msg = JsonUtil.toJson(map);
//            new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(ud, oldData, info, msg,"修改-bydb对象");

            resMap.setSingleOk( info, "保存成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "批量启用或禁用bydb对象", notes = "批量启用或禁用bydb对象")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "对象id", dataType = "String", required = true, paramType = "query"),
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
            Example exp = new Example( TBydbObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", split );

            if ( enable == 0 ) {
                actType = "禁用";
            }
            else {
                actType = "启用";
            }

            String act = "修改-" + actType + "bydb对象" + System.currentTimeMillis();
            if ( enable == 1 ) {
                criteria.andCondition( " (enable =0  or enable is null )" );
            }
            else {
                criteria.andEqualTo( "enable", 1 );
            }
            List<TBydbObjectDo> chgList = bydbObjectService.findByExample( exp );
            if ( chgList.size() != split.size() ) {
                return resMap.setErr( " 数据已变化不能操作" ).getResultMap();
            }

            if ( enable == 0 ) {
                exp = new Example( TTruModelObjectDo.class );
                criteria = exp.createCriteria();
                criteria.andIn( "realObjId", split );
                int cnt = modelObjService.findCountByExample( exp );
                if ( cnt > 0 ) {
                    return resMap.setErr( "数据被使用，不能".concat( actType ) ).getResultMap();
                }
            }

            List<TBydbObjectDo> oldList = new ArrayList<>();
            List<TBydbDatabaseDo> dbList = null;
            List<TBydbSchemaDo> schemaList = null;
            if ( enable == 1 ) {
                for ( TBydbObjectDo info : chgList ) {
                    TBydbObjectDo old = new TBydbObjectDo();
                    MyBeanUtils.copyBeanNotNull2Bean( info, old );
                    oldList.add( old );
                    info.setEnable( 1 );
                    info.setSynFlag( 0 );
                }
                List<String> idList = chgList.stream().map( x -> x.getDbId() ).distinct().collect( Collectors.toList() );
                exp = new Example( TBydbDatabaseDo.class );
                criteria = exp.createCriteria();
                criteria.andIn( "id", idList );
                criteria.andCondition( " (enable =0  or enable is null )" );
                dbList = databaseService.findByExample( exp );
                for ( TBydbDatabaseDo tBydbDatabaseDo : dbList ) {
                    tBydbDatabaseDo.setEnable( enable );
                }

                idList = chgList.stream().map( x -> x.getSchemaId() ).distinct().collect( Collectors.toList() );
                exp = new Example( TBydbSchemaDo.class );
                criteria = exp.createCriteria();
                criteria.andIn( "id", idList );
                criteria.andCondition( " (enable =0  or enable is null )" );
                schemaList = schemaService.findByExample( exp );
                for ( TBydbSchemaDo schemaDo : schemaList ) {
                    schemaDo.setEnable( enable );
                }
            }
            else {
                for ( TBydbObjectDo info : chgList ) {
                    TBydbObjectDo old = new TBydbObjectDo();
                    MyBeanUtils.copyBeanNotNull2Bean( info, old );
                    oldList.add( old );
                    info.setEnable( 0 );
                    info.setSynFlag( 0 );
                }
            }

            bydbObjectService.updateBeanWithFlag( chgList, dbList, schemaList );

//            for ( int i = 0; i < chgList.size(); i++ ) {
//                TBydbObjectDo info = chgList.get( i );
//                TBydbObjectDo old = oldList.get( i );
//                HashMap<String, Object> map = new HashMap<>();
//                if ( dbList != null ) {
//                    for ( TBydbDatabaseDo tBydbDatabaseDo : dbList ) {
//                        if ( tBydbDatabaseDo.getId().equals( info.getDbId() ) ) {
//                            map.put( "db", tBydbDatabaseDo );
//                            break;
//                        }
//                    }
//                }
//                if ( schemaList != null ) {
//                    for ( TBydbSchemaDo schemaDo : schemaList ) {
//                        if ( schemaDo.getId().equals( info.getSchemaId() ) ) {
//                            map.put( "schema", schemaDo );
//                            break;
//                        }
//                    }
//                }
//
////                String msg = JsonUtil.toJson(map);
////                new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(ud, old, info,msg, act);
//            }

            resMap.setOk( actType + "成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( actType + "失败" );
            logger.error( actType + "异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "bydb对象内容", notes = "bydb对象内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "bydb对象 id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    @ResponseBody
    public Object info( String id ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( StringUtils.isBlank( id ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }
            TBydbObjectDo modelVo = bydbObjectService.findById( id );
            resMap.setSingleOk( modelVo, "成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "查询失败" );
            logger.error( "查询异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除bydb对象", notes = "删除bydb对象")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "bydb对象 id", dataType = "String", required = true, paramType = "query")
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
            if ( idList.size() == 0 ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }
            Example exp = new Example( TBydbObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", idList );

            List<TBydbObjectDo> list = bydbObjectService.findByExample( exp );
//            int cnt = list.size();
//            if ( cnt == 0 ) {
//                return resMap.setErr( "没有数据可删除" ).getResultMap();
//            }
//            if ( list.size() != split.size() ) {
//                return resMap.setErr( "数据已变化，删除失败" ).getResultMap();
//            }
//
//            exp = new Example( TTruModelObjectDo.class );
//            criteria = exp.createCriteria();
//            criteria.andIn( "realObjId", split );
//            cnt = modelObjService.findCountByExample( exp );
//            if ( cnt > 0 ) {
//                return resMap.setErr( "数据被使用，不能删除" ).getResultMap();
//            }

//            exp = new Example( TBydbItemObjectDo.class );
//            criteria = exp.createCriteria();
//            criteria.andIn( "objectId", split);
//            List<TBydbItemObjectDo> itemObjlist = itemObjectService.findByExample(exp);
//
//            exp = new Example( TBydbGroupObjectDo.class );
//            criteria = exp.createCriteria();
//            criteria.andIn( "objectId", split);
//            List<TBydbGroupObjectDo> groupObjlist = groupObjectService.findByExample(exp);



            ObjectResp<String> retVal = apiTruModelService.delTable( idList, user.getTokenId() );
            if( retVal.isSuccess() ){
                bydbObjectService.deleteWhithOthers( list );
            }
            else{
                return retVal;
            }

//            String times = String.valueOf( System.currentTimeMillis() );
//            for (TBydbObjectDo info : list) {
//                try {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(user, info, "删除-bydb对象"+times);
//                }
//                catch (Exception e1) {
//                    resMap.setErr("删除失败");
//                    logger.error("删除异常:", e1);
//                }
//            }
            resMap.setOk( "删除成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "删除失败" );
            logger.error( "删除异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取对象列表", notes = "获取对象列表")
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
            TBydbObjectDo modelVo = new TBydbObjectDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo, hru );
            modelVo.setQryCond( ComUtil.chgLikeStr( modelVo.getQryCond() ) );
            modelVo.setObjectName( ComUtil.chgLikeStr( modelVo.getObjectName() ) );

            long findCnt = bydbObjectService.findBeanCnt( modelVo );
            modelVo.genPage( findCnt );

            List<TBydbObjectDo> list = bydbObjectService.findBeanList( modelVo );

            resMap.setPageInfo( modelVo.getPageSize(), modelVo.getCurrentPage() );
            resMap.setOk( findCnt, list, "获取对象列表成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取对象列表失败" );
            logger.error( "获取对象列表失败:", ex );
        }
        return resMap.getResultMap();
    }

    /*
    @ApiOperation(value = "获取对象选择列表", notes = "获取对象选择列表")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/option", method = {RequestMethod.GET})
    @ResponseBody
    public Object option( String dbId,String schemaId ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            TBydbObjectDo modelVo =new TBydbObjectDo();
            if( StringUtils.isNotBlank( dbId ) ){
                modelVo.setDbId( dbId );
            }
            if( StringUtils.isNotBlank( schemaId ) ){
                modelVo.setSchemaId( schemaId );
            }
            List<TBydbObjectDo> list = objectService.findBeanList(modelVo);
            resMap.setSingleOk(list, "获取对象选择列表成功");
        } catch (Exception ex) {
            resMap.setErr("获取对象选择列表失败");
            logger.error("获取对象选择列表失败:", ex);
        }
        return resMap.getResultMap();
    }*/

    @ApiOperation(value = "获取对象类型", notes = "获取对象类型")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/objecttype", method = {RequestMethod.GET})
    @ResponseBody
    public Object objectType() {
        ResponeMap resMap = this.genResponeMap();
        try {
            List<Object> list = new ArrayList<>();
            HashMap<String, String> data = new HashMap<>();
            data.put( "value", "table" );
            data.put( "name", "表" );
            list.add( data );
            resMap.setSingleOk( list, "获取对象类型" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取对象类型失败" );
            logger.error( "获取对象类型失败:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "上报表", notes = "上报表")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "modelVo", value = "数据源", dataType = "TDatasourceDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/synobject", method = {RequestMethod.POST})
    @ResponseBody
    public Object synObject( @RequestBody SynObjDataVo objDataVo, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
            if ( StringUtils.isBlank( objDataVo.getDataId() ) ) {
                return resMap.setErr( "对象id不能为空" ).getResultMap();
            }
            if ( objDataVo.getFlag() == null ) {
                return resMap.setErr( "可见性不能为空" ).getResultMap();
            }
            List<String> nodeIdList = Arrays.asList( objDataVo.getNodeId().split( "(,|\\s)+" ) ).stream().filter( x->StringUtils.isNotBlank( x ) ).distinct().collect( Collectors.toList());

            if ( objDataVo.getFlag() == 2 ) {
                if ( nodeIdList.size() == 0 ) {
                    return resMap.setErr( "对指定成员可见时成员不能为空" ).getResultMap();
                }
            }

            List<String> split = Arrays.asList( objDataVo.getDataId().split( "(,|\\s)+" ) );

            Example exp = new Example( TBydbObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            if ( "table".equalsIgnoreCase( objDataVo.getType() ) ) {
                criteria.andIn( "id", split );
            }
            else if ( "schema".equalsIgnoreCase( objDataVo.getType() ) ) {
                criteria.andIn( "schemaId", split );
            }
            else if ( "db".equalsIgnoreCase( objDataVo.getType() ) ) {
                criteria.andIn( "dbId", split );
            }

            List<TBydbObjectDo> list = bydbObjectService.findByExample( exp );
            int cnt = list.size();
            if ( cnt == 0 ) {
                return resMap.setErr( "没有数据可操作" ).getResultMap();
            }
//            if( list.size() != split.size() ){
//                return resMap.setErr("数据已变化，操作失败").getResultMap();
//            }


            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            //Map<String,TBydbDatabaseDo> dbMap = new HashMap<>();
            //Map<String,TBydbSchemaDo> schemaMap = new HashMap<>();

            /*for ( TBydbObjectDo info : list ) {
                if( !dbMap.containsKey( info.getDbId() )) {
                    TBydbDatabaseDo dbDo = databaseService.findById( info.getDbId() );
                    if( dbDo.getShareFlag() == null || dbDo.getShareFlag() !=1 ){
                        dbDo.setShareFlag( 1 );
                        dbDo.setNodePartyId( nodePartyDo.getId() );
                        dbDo.setShareTime( ComUtil.getCurTimestamp() );

                        Map<String, Object> retMap = apiTruModelService.synDatabase( dbDo, ud.getTokenId() );
                        if ( retMap.containsKey( "success" ) && !(boolean) retMap.get( "success" ) ) {
                            return resMap.setErr( "保存完成，同步目录信息失败" ).getResultMap();
                        }
                        TBydbDatabaseDo tmp = new TBydbDatabaseDo();
                        tmp.setId( dbDo.getId() );
                        tmp.setNodePartyId( dbDo.getNodePartyId() );
                        tmp.setShareTime( dbDo.getShareTime() );
                        tmp.setShareFlag( dbDo.getShareFlag() );
                        databaseService.updateNoNull( tmp );
                        dbMap.put( dbDo.getId(),dbDo );
                    }
                }

                if( !schemaMap.containsKey( info.getSchemaId() )) {
                    TBydbSchemaDo schemaDo = schemaService.findById( info.getSchemaId() );
                    if( schemaDo.getShareFlag() == null || schemaDo.getShareFlag() !=1 ){
                        schemaDo.setShareFlag( 1 );
                        schemaDo.setNodePartyId( nodePartyDo.getId() );
                        schemaDo.setShareTime( ComUtil.getCurTimestamp() );

                        Map<String, Object> retMap = apiTruModelService.synSchema( schemaDo, ud.getTokenId() );
                        if ( retMap.containsKey( "success" ) && !(boolean) retMap.get( "success" ) ) {
                            return resMap.setErr( "保存完成，同步目录信息失败" ).getResultMap();
                        }
                        TBydbSchemaDo tmp = new TBydbSchemaDo();
                        tmp.setId( schemaDo.getId() );
                        tmp.setNodePartyId( schemaDo.getNodePartyId() );
                        tmp.setShareTime( schemaDo.getShareTime() );
                        tmp.setShareFlag( schemaDo.getShareFlag() );
                        schemaService.updateNoNull( tmp );
                        schemaMap.put( schemaDo.getId(),schemaDo );
                    }
                }
            }*/

            List<String> idList = new ArrayList<>();
            for ( TBydbObjectDo info : list ) {
                idList.add( info.getId() );
                if ( StringUtils.isBlank( info.getNodePartyId() ) ) {
                    info.setNodePartyId( nodePartyDo.getId() );
                }

                List<TBydbDataNodeDo> oldList = dataNodeService.findByDataId( info.getId() );
                List<TBydbDataNodeDo> delDnList = new ArrayList<>();
                List<TBydbDataNodeDo> addDnList = new ArrayList<>();
                List<TBydbDataNodeDo> modDnList = new ArrayList<>();

                for ( TBydbDataNodeDo dn : oldList ) {
                    if ( !nodeIdList.contains( dn.getNodeId() ) ) {
                        delDnList.add( dn );
                    }
                    else {
                        modDnList.add( dn );
                    }
                }
                for ( String s : nodeIdList ) {
                    boolean bfound = false;
                    for ( TBydbDataNodeDo dn : oldList ) {
                        if ( dn.getNodeId().equals( s ) ) {
                            bfound = true;
                        }
                    }
                    if ( !bfound ) {
                        TBydbDataNodeDo dataNodeDo = new TBydbDataNodeDo();
                        dataNodeDo.setNodeId( s );
                        dataNodeDo.setDataId( info.getId() );
                        dataNodeDo.setDataType( "db" );
                        dataNodeDo.setId( ComUtil.genId() );
                        LoginUtil.setBeanInsertUserInfo( dataNodeDo, ud );
                        addDnList.add( dataNodeDo );
                    }
                }

                info.setShareFlag( objDataVo.getFlag() );
                info.setShareTime( ComUtil.getCurTimestamp() );

                TBydbObjectDo tmp = new TBydbObjectDo();
                tmp.setId( info.getId() );
                tmp.setSynFlag( 0 );
                tmp.setNodePartyId( info.getNodePartyId() );
                tmp.setShareTime( info.getShareTime() );
                tmp.setShareFlag( info.getShareFlag() );
                bydbObjectService.updateWithNodes( tmp, addDnList, null, delDnList );

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
//                    TBydbObjectDo tmp = new TBydbObjectDo();
//                    tmp.setId( info.getId() );
//                    tmp.setNodePartyId( info.getNodePartyId() );
//                    tmp.setShareTime( info.getShareTime() );
//                    tmp.setShareFlag( info.getShareFlag() );
//                    bydbObjectService.updateNoNull( tmp );
//                }
            }
            resMap.setOk( "保存成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "数据预览", notes = "数据预览")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "当前页", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "Integer", required = false, paramType = "query")

    })
    @RequestMapping(value = "/queryobjectdata", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryObjectData( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            TBydbObjectDo modelVo = new TBydbObjectDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo, hru );
            if ( StringUtils.isBlank( modelVo.getId() ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }

            TBydbObjectDo objectDo = bydbObjectService.findById( modelVo.getId() );
            if ( objectDo == null ) {
                return resMap.setErr( "对象不存在" ).getResultMap();
            }
            if ( objectDo.getEnable() == null || objectDo.getEnable() != 1 ) {
                return resMap.setErr( "对象未启用" ).getResultMap();
            }
            TBydbDatabaseDo dbDo = databaseService.findById( objectDo.getDbId() );
            if ( dbDo == null ) {
                return resMap.setErr( "目录不存在" ).getResultMap();
            }

            FDatasourceDo sourceDo = dbSourceService.findById( dbDo.getDbsourceId() );
            if ( sourceDo == null ) {
                return resMap.setErr( "数据源不存在" ).getResultMap();
            }
            List<TBydbFieldDo> colList = fieldService.selectByObjectId( objectDo.getId() ).stream().filter( x->x.getEnable()!=null && x.getEnable()==1 ).collect( Collectors.toList());

            List<String> fldList = new ArrayList<>();
            for ( TBydbFieldDo fieldDo : colList ) {
                if(StringUtils.isBlank( fieldDo.getChnName() )){
                    fieldDo.setChnName( fieldDo.getFieldName() );
                }
                fldList.add( fieldDo.getFieldName() );
            }

            resMap.put( "colList" ,colList );

            long count = 0;
            List<Map<String, Object>> list = new ArrayList<>();

            JdbcOpBuilder jdbcOpBuilder = new JdbcOpBuilder().withSet( sourceDo.getDsType(), sourceDo.getDsDriver(), sourceDo.getJdbcUrl(), sourceDo.getUsername(), sourceDo.getPassword() );

            try ( IJdbcOp dbop = jdbcOpBuilder.build() ) {

                String sql = String.format( "SELECT COUNT("+colList.get( 0 ).getFieldName()+") CNT FROM  %s ", objectDo.getObjFullName() );
                logger.debug( sql );
                count = dbop.selectTableCount( sql, null );

                modelVo.genPage( count );

                //sql = String.format("select %s from %s %s limit %d", fields, datasetDo.getCacheTableName(), order, modelVo.getPageSize());
                sql = String.format( "SELECT %s FROM %s ", String.join( " , " ,fldList), objectDo.getObjFullName() );
                logger.info( sql );
                list = dbop.selectData( sql, (modelVo.getCurrentPage() - 1) * modelVo.getPageSize(), modelVo.getPageSize() );
            }

            if ( list != null && list.size() > 0 ) {
                for ( Map<String, Object> dat : list ) {
                    final Iterator<Map.Entry<String, Object>> iterator = dat.entrySet().iterator();
                    while ( iterator.hasNext() ) {
                        final Map.Entry<String, Object> next = iterator.next();
                        final String key = next.getKey();
                        final Object value = next.getValue();
                        if ( value != null ) {
                            if ( value instanceof Date ) {
                                dat.put( key, ComUtil.dateToEffStr( (Date) value ) );
                            }
                            else if ( value instanceof Timestamp ) {
                                dat.put( key, ComUtil.dateToEffStr( (Timestamp) value ) );
                            }
                            else if ( value instanceof Boolean ) {
                                if ( (Boolean) value ) {
                                    dat.put( key, "是" );
                                }
                                else {
                                    dat.put( key, "否" );
                                }
                            }
                        }
                    }
                }
            }

//                if ( StringUtils.isNotBlank( modelDo.getParamConfig() ) ) {
//                    Type type = new TypeToken<List<Map<String, String>>>() {
//                    }.getType();
//                    List<Map<String, String>> tmpColList = JsonUtil.deserialize( modelDo.getParamConfig(), type );
//                    resMap.put( "colList", tmpColList );
//                }
//                else {
//                    resMap.put( "colList", new ArrayList<>() );
//                }

            resMap.setPageInfo( modelVo.getPageSize(), modelVo.getCurrentPage() );
            return resMap.setOk( count, list, "查询联邦分析成功" ).getResultMap();
        }
        catch ( Exception ex ) {
            resMap.setErr( "查询联邦分析失败" );
            logger.error( "查询联邦分析失败:", ex );
        }
        return resMap.getResultMap();
    }

}
