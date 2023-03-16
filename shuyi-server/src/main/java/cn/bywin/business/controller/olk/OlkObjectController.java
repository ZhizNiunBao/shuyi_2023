package cn.bywin.business.controller.olk;


import cn.bywin.business.bean.bydb.TTruModelObjectDo;
import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.olk.TOlkDataNodeDo;
import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkFieldDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.olk.TOlkSchemaDo;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.view.SynObjDataVo;
import cn.bywin.business.bean.view.olk.OlkObjectWithFieldsVo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.service.federal.DataSourceService;
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
@Api(tags = "olk-对象管理-olkobject")
@RequestMapping("/olkobject")
public class OlkObjectController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Autowired
    private DataSourceService dbSourceService;

    @Autowired
    private OlkDatabaseService databaseService;

    @Autowired
    private OlkSchemaService schemaService;

    @Autowired
    private OlkObjectService olkObjectService;

    @Autowired
    private OlkFieldService fieldService;

    @Autowired
    private OlkModelObjectService modelObjService;

    @Autowired
    private ApiOlkDbService apiOlkDbService;

    @Autowired
    private ApiTruModelService apiTruModelService;

    @Autowired
    private NodePartyService nodePartyService;

    @Autowired
    private OlkDataNodeService dataNodeService;

//    @Autowired
//    private OlkDsEntityService dsEntityService;

    @ApiOperation(value = "修改olk对象", notes = "修改olk对象")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelVo", value = "olk对象", dataType = "TOlkObjectDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update( @RequestBody TOlkObjectDo bean, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
//            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
//            logger.debug( "{}",hru.getAllParaData() );
//            TOlkObjectDo info = objectService.findById(hru.getNvlPara("id"));

            TOlkObjectDo info = olkObjectService.findById( bean.getId() );

            if ( info == null ) {
                return resMap.setErr( "内容不存在" ).getResultMap();
            }

            TOlkObjectDo oldData = new TOlkObjectDo();
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

            final TOlkSchemaDo schemaDo = schemaService.findById( info.getSchemaId() );
            if ( schemaDo == null ) {
                return resMap.setErr( "数据库不存在" ).getResultMap();
            }

            if ( StringUtils.isBlank( info.getDbId() ) ) {
                info.setDbId( schemaDo.getDbId() );
            }
            if ( StringUtils.isBlank( info.getUserAccount() ) ) {
                info.setUserAccount( schemaDo.getUserAccount() );
            }

            TOlkDatabaseDo dbDo = databaseService.findById( info.getDbId() );
            if ( dbDo == null ) {
                return resMap.setErr( "数据目录不存在" ).getResultMap();
            }
            if ( !info.getDbId().equals( schemaDo.getDbId() ) ) {
                return resMap.setErr( "数据库与目录不一致" ).getResultMap();
            }

            final long sameNameCount = olkObjectService.findSameNameCount( info );
            if ( sameNameCount > 0 ) {
                return resMap.setErr( "名称已使用" ).getResultMap();
            }
            List<TOlkDatabaseDo> dbList = new ArrayList<>();
            List<TOlkSchemaDo> schemaList = new ArrayList<>();

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
                List<TOlkObjectDo> updList = new ArrayList<>();
                updList.add( info );
                info.setSynFlag( 0 );

                olkObjectService.updateBeanWithFlag( updList, dbList, schemaList );
            }
            else{
                olkObjectService.updateBean( info );
            }
            resMap.setSingleOk( info, "保存成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "批量启用或禁用olk对象", notes = "批量启用或禁用olk对象")
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
            Example exp = new Example( TOlkObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", split );

            if ( enable == 0 ) {
                actType = "禁用";
            }
            else {
                actType = "启用";
            }

            String act = "修改-" + actType + "olk对象" + System.currentTimeMillis();
            if ( enable == 1 ) {
                criteria.andCondition( " (enable =0  or enable is null )" );
            }
            else {
                criteria.andEqualTo( "enable", 1 );
            }
            List<TOlkObjectDo> chgList = olkObjectService.findByExample( exp );
            if ( chgList.size() != split.size() ) {
                return resMap.setErr( " 数据已变化不能操作" ).getResultMap();
            }
            List<String> userIdList = chgList.stream().map( x -> x.getUserId() ).distinct().collect( Collectors.toList() );
            if(userIdList.size()!=1 ){
                return resMap.setErr( "不能同时操作不同用户数据" ).getResultMap();
            }

            if ( enable == 0 ) {
                exp = new Example( TOlkModelObjectDo.class );
                criteria = exp.createCriteria();
                criteria.andIn( "realObjId", split ).andEqualTo( "userId", userIdList.get( 0 ));

                int cnt = modelObjService.findCountByExample( exp );
                if ( cnt > 0 ) {
                    return resMap.setErr( "数据被使用，不能".concat( actType ) ).getResultMap();
                }
            }

            List<TOlkObjectDo> oldList = new ArrayList<>();
            List<TOlkDatabaseDo> dbList = null;
            List<TOlkSchemaDo> schemaList = null;
            if ( enable == 1 ) {
                for ( TOlkObjectDo info : chgList ) {
                    TOlkObjectDo old = new TOlkObjectDo();
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

                idList = chgList.stream().map( x -> x.getSchemaId() ).distinct().collect( Collectors.toList() );
                exp = new Example( TOlkSchemaDo.class );
                criteria = exp.createCriteria();
                criteria.andIn( "id", idList );
                criteria.andCondition( " (enable =0  or enable is null )" );
                schemaList = schemaService.findByExample( exp );
                for ( TOlkSchemaDo schemaDo : schemaList ) {
                    schemaDo.setEnable( enable );
                }
            }
            else {
                for ( TOlkObjectDo info : chgList ) {
                    TOlkObjectDo old = new TOlkObjectDo();
                    MyBeanUtils.copyBeanNotNull2Bean( info, old );
                    oldList.add( old );
                    info.setEnable( 0 );
                    info.setSynFlag( 0 );
                }
            }

            olkObjectService.updateBeanWithFlag( chgList, dbList, schemaList );
            resMap.setOk( actType + "成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( actType + "失败" );
            logger.error( actType + "异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "olk对象内容", notes = "olk对象内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "olk对象 id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    @ResponseBody
    public Object info( String id ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( StringUtils.isBlank( id ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }
            TOlkObjectDo modelVo = olkObjectService.findById( id );
            resMap.setSingleOk( modelVo, "成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "查询失败" );
            logger.error( "查询异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除olk对象", notes = "删除olk对象")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "olk对象 id", dataType = "String", required = true, paramType = "query")
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
            Example exp = new Example( TOlkObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", idList );

            List<TOlkObjectDo> list = olkObjectService.findByExample( exp );
            int cnt = list.size();
            if ( cnt == 0 ) {
                return resMap.setErr( "没有数据可删除" ).getResultMap();
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

            if( StringUtils.isNotBlank( userId )){
            exp = new Example( TTruModelObjectDo.class );
            criteria = exp.createCriteria();
            criteria.andIn( "realObjId", idList );
                criteria.andEqualTo( "userId", userId );
                cnt = modelObjService.findCountByExample( exp );
                if ( cnt > 0 ) {
                    return resMap.setErr( "数据被使用，不能删除" ).getResultMap();
                }
            }

//            exp = new Example( TOlkItemObjectDo.class );
//            criteria = exp.createCriteria();
//            criteria.andIn( "objectId", split);
//            List<TOlkItemObjectDo> itemObjlist = itemObjectService.findByExample(exp);
//
//            exp = new Example( TOlkGroupObjectDo.class );
//            criteria = exp.createCriteria();
//            criteria.andIn( "objectId", split);
//            List<TOlkGroupObjectDo> groupObjlist = groupObjectService.findByExample(exp);

            ObjectResp<String> retVal = apiOlkDbService.delOlkTable( list, user.getTokenId() );
            if( retVal.isSuccess() ){
                olkObjectService.deleteWhithOthers( list );
            }
            else{
                return retVal;
            }

//            String times = String.valueOf( System.currentTimeMillis() );
//            for (TOlkObjectDo info : list) {
//                try {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(user, info, "删除-olk对象"+times);
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
            TOlkObjectDo modelVo = new TOlkObjectDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo, hru );
            modelVo.setQryCond( ComUtil.chgLikeStr( modelVo.getQryCond() ) );
            modelVo.setObjectName( ComUtil.chgLikeStr( modelVo.getObjectName() ) );

            long findCnt = olkObjectService.findBeanCnt( modelVo );
            modelVo.genPage( findCnt );

            List<TOlkObjectDo> list = olkObjectService.findBeanList( modelVo );

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
            TOlkObjectDo modelVo =new TOlkObjectDo();
            if( StringUtils.isNotBlank( dbId ) ){
                modelVo.setDbId( dbId );
            }
            if( StringUtils.isNotBlank( schemaId ) ){
                modelVo.setSchemaId( schemaId );
            }
            List<TOlkObjectDo> list = objectService.findBeanList(modelVo);
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

            Example exp = new Example( TOlkObjectDo.class );
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

            List<TOlkObjectDo> list = olkObjectService.findByExample( exp );
            int cnt = list.size();
            if ( cnt == 0 ) {
                return resMap.setErr( "没有数据可操作" ).getResultMap();
            }
//            if( list.size() != split.size() ){
//                return resMap.setErr("数据已变化，操作失败").getResultMap();
//            }


            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            //Map<String,TOlkDatabaseDo> dbMap = new HashMap<>();
            //Map<String,TOlkSchemaDo> schemaMap = new HashMap<>();

            /*for ( TOlkObjectDo info : list ) {
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
            }*/

            List<String> idList = new ArrayList<>();
            for ( TOlkObjectDo info : list ) {
                idList.add( info.getId() );
                if ( StringUtils.isBlank( info.getNodePartyId() ) ) {
                    info.setNodePartyId( nodePartyDo.getId() );
                }

                List<TOlkDataNodeDo> oldList = dataNodeService.findByDataId( info.getId() );
                List<TOlkDataNodeDo> delDnList = new ArrayList<>();
                List<TOlkDataNodeDo> addDnList = new ArrayList<>();
                List<TOlkDataNodeDo> modDnList = new ArrayList<>();

                for ( TOlkDataNodeDo dn : oldList ) {
                    if ( !nodeIdList.contains( dn.getNodeId() ) ) {
                        delDnList.add( dn );
                    }
                    else {
                        modDnList.add( dn );
                    }
                }
                for ( String s : nodeIdList ) {
                    boolean bfound = false;
                    for ( TOlkDataNodeDo dn : oldList ) {
                        if ( dn.getNodeId().equals( s ) ) {
                            bfound = true;
                        }
                    }
                    if ( !bfound ) {
                        TOlkDataNodeDo dataNodeDo = new TOlkDataNodeDo();
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

                TOlkObjectDo tmp = new TOlkObjectDo();
                tmp.setId( info.getId() );
                tmp.setSynFlag( 0 );
                tmp.setNodePartyId( info.getNodePartyId() );
                tmp.setShareTime( info.getShareTime() );
                tmp.setShareFlag( info.getShareFlag() );
                olkObjectService.updateWithNodes( tmp, addDnList, null, delDnList );
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
            TOlkObjectDo modelVo = new TOlkObjectDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo, hru );
            if ( StringUtils.isBlank( modelVo.getId() ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }

            TOlkObjectDo objectDo = olkObjectService.findById( modelVo.getId() );
            if ( objectDo == null ) {
                return resMap.setErr( "对象不存在" ).getResultMap();
            }
            if ( objectDo.getEnable() == null || objectDo.getEnable() != 1 ) {
                return resMap.setErr( "对象未启用" ).getResultMap();
            }
            TOlkDatabaseDo dbDo = databaseService.findById( objectDo.getDbId() );
            if ( dbDo == null ) {
                return resMap.setErr( "目录不存在" ).getResultMap();
            }

            FDatasourceDo sourceDo = dbSourceService.findById( dbDo.getDbsourceId() );
            if ( sourceDo == null ) {
                return resMap.setErr( "数据源不存在" ).getResultMap();
            }
            List<TOlkFieldDo> colList = fieldService.selectByObjectId( objectDo.getId() );
            for ( TOlkFieldDo fieldDo : colList ) {
                if(StringUtils.isBlank( fieldDo.getChnName() )){
                    fieldDo.setChnName( fieldDo.getFieldName() );
                }
            }

            resMap.put( "colList" ,colList );

            long count = 0;
            List<Map<String, Object>> list = new ArrayList<>();

            JdbcOpBuilder jdbcOpBuilder = new JdbcOpBuilder().withSet( sourceDo.getDsType(), sourceDo.getDsDriver(), sourceDo.getJdbcUrl(), sourceDo.getUsername(), sourceDo.getPassword() );

            try ( IJdbcOp dbop = jdbcOpBuilder.build() ) {

                String sql = String.format( "select count("+colList.get( 0 ).getFieldName()+") cnt from  %s ", objectDo.getObjFullName() );
                logger.debug( sql );
                count = dbop.selectTableCount( sql, null );

                modelVo.genPage( count );

                //sql = String.format("select %s from %s %s limit %d", fields, datasetDo.getCacheTableName(), order, modelVo.getPageSize());
                sql = String.format( "select * from %s ", objectDo.getObjFullName() );
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
                                dat.put( key, ComUtil.dateToStr( (Date) value ) );
                            }
                            else if ( value instanceof Timestamp ) {
                                dat.put( key, ComUtil.dateToLongStr( (Timestamp) value ) );
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

    @ApiOperation(value = "对用户授权", notes = "对用户授权")
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "stype", value = "类型 database,schema,table ", dataType = "String",example = "database,schema,table", required = true, paramType = "query"),
//            @ApiImplicitParam(name = "dataId", value = "数据id", dataType = "String", required = true, paramType = "query"),
//            @ApiImplicitParam(name = "userId", value = "用户Id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/grantouser", method = {RequestMethod.POST})
    @ResponseBody
    public Object grantoUser(@RequestBody List<OlkObjectWithFieldsVo> beanList,  HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
//            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
//            String dataId = hru.getNvlPara( "dataId" );
//            String userId = hru.getNvlPara( "userId" );
//            String stype = hru.getNvlPara( "stype" );

            if ( beanList ==null || beanList.size()==0 ) {
                return resMap.setErr( "数据不能为空" ).getResultMap();
            }

            List<String> userIdList = new ArrayList<>();
            List<String> dataIdList = new ArrayList<>();
            List<String> sameIdList = new ArrayList<>();
            for ( OlkObjectWithFieldsVo tmp : beanList ) {
                if( StringUtils.isBlank( tmp.getGrantUserId() ) ){
                    return resMap.setErr( "授权用户id不能为空" ).getResultMap();
                }
                if( StringUtils.isBlank( tmp.getId() ) ){
                    return resMap.setErr( "数据id不能为空" ).getResultMap();
                }

                String tmpId = tmp.getId()+"_"+tmp.getGrantUserId();
                if( sameIdList.indexOf( tmpId )>=0 ){
                    return resMap.setErr( "数据授权重复" ).getResultMap();
                }
                if( userIdList.indexOf( tmp.getGrantUserId() )< 0 ){
                    userIdList.add( tmp.getGrantUserId() );
                }
                if( dataIdList.indexOf( tmp.getId() ) < 0 ){
                    dataIdList.add( tmp.getId() );
                }
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

            Example exp = new Example( TOlkObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", dataIdList ).andEqualTo( "enable", 1 );
            List<TOlkObjectDo> objList = olkObjectService.findByExample( exp );
            if ( objList.size() != dataIdList.size() ) {
                return resMap.setErr( "部分对象已变化或不存在" ).getResultMap();
            }

            List<FDataApproveDo> list = new ArrayList<>();
            for ( TOlkObjectDo objDo : objList ) {
                for ( String s : userIdList ) {
                    if ( s.equals( objDo.getUserId() ) ) {
                        return resMap.setErr( "部分为自己对象无需授权" ).getResultMap();
                    }
                    FDataApproveDo da = new FDataApproveDo();
                    da.setDataId( objDo.getId() );
                    da.setNodeId( objDo.getNodePartyId() );
                    da.setUserId( objDo.getUserId() );
                    da.setUserName( objDo.getUserName() );
                    da.setDataCatalog( "table" );
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

}
