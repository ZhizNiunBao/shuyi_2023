package cn.bywin.business.controller.bydb;


import cn.bywin.business.bean.bydb.TBydbDatabaseDo;
import cn.bywin.business.bean.bydb.TBydbSchemaDo;
import cn.bywin.business.bean.bydb.TTruModelObjectDo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.service.bydb.BydbDataNodeService;
import cn.bywin.business.service.bydb.BydbDatabaseService;
import cn.bywin.business.service.bydb.BydbFieldService;
import cn.bywin.business.service.bydb.BydbObjectService;
import cn.bywin.business.service.bydb.BydbSchemaService;
import cn.bywin.business.service.bydb.TruModelObjectService;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.trumodel.ApiTruModelService;
import cn.bywin.common.resp.ObjectResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
@Api(tags = "联邦学习-库管理-bydbschema")
@RequestMapping("/bydbschema")
public class BydbSchemaController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Autowired
    private BydbDatabaseService databaseService;

    @Autowired
    private BydbSchemaService schemaService;

    @Autowired
    private BydbObjectService objectService;

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

    //@Autowired
    //private BydbDcServerService dcserverService;

    //String connectChar = "^";
    //String splitChar = "\\^";


    /*@ApiOperation(value = "新增bydb库", notes = "新增bydb库")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "info", value = "bydb库", dataType = "TBydbSchemaDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public Object add(@RequestBody TBydbSchemaDo info, HttpServletRequest request) {
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

            final TBydbDatabaseDo dbDo = databaseService.findById(info.getDbId());
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

    @ApiOperation(value = "修改bydb库", notes = "修改bydb库")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelVo", value = "bydb库", dataType = "TBydbSchemaDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update( @RequestBody TBydbSchemaDo bean, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
//            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
//            logger.debug( "{}",hru.getAllParaData() );
//            TBydbSchemaDo info = schemaService.findById(hru.getNvlPara("id"));
            TBydbSchemaDo info = schemaService.findById( bean.getId() );

            if ( info == null ) {
                return resMap.setErr( "内容不存在" ).getResultMap();
            }

            TBydbSchemaDo oldData = new TBydbSchemaDo();
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

            final TBydbDatabaseDo dbDo = databaseService.findById( info.getDbId() );
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
            List<TBydbSchemaDo> updList = new ArrayList<>();
            updList.add( info );
            List<TBydbDatabaseDo> dbList = new ArrayList<>();
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
//            HashMap<String, Object> map = new HashMap<>();
//            if ( dbList.size() > 0 ) {
//                map.put( "db", dbList.get( 0 ) );
//            }
//            String msg = JsonUtil.toJson( map );

//            new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(ud, oldData, info, msg,"修改-bydb数据库");

            resMap.setSingleOk( info, "保存成功" );

        }
        catch ( Exception ex ) {
            ex.printStackTrace();
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "批量启用或禁用bydb库", notes = "批量启用或禁用bydb库")
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
            Example exp = new Example( TBydbSchemaDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", split );

            if ( enable == 0 ) {
                actType = "禁用";
            }
            else {
                actType = "启用";
            }

            String act = "修改-" + actType + "bydb库" + System.currentTimeMillis();
            if ( enable == 1 ) {
                criteria.andCondition( " (enable =0  or enable is null )" );
            }
            else {
                criteria.andEqualTo( "enable", 1 );
            }
            List<TBydbSchemaDo> chgList = schemaService.findByExample( exp );
            if ( chgList.size() != split.size() ) {
                return resMap.setErr( " 数据已变化不能操作" ).getResultMap();
            }
            if ( enable == 0 ) {
                String ids = "'" + StringUtils.join( split, "','" ) + "'";
                exp = new Example( TTruModelObjectDo.class );
                criteria = exp.createCriteria();
                criteria.andCondition( " real_obj_id in (select a.id from t_bydb_object a, t_bydb_schema b where a.schema_id =b.id  and b.id in( " + ids + ") )" );
                int cnt = modelObjService.findCountByExample( exp );
                if ( cnt > 0 ) {
                    return resMap.setErr( "数据被使用，不能".concat( actType ) ).getResultMap();
                }
            }
            List<TBydbSchemaDo> oldList = new ArrayList<>();
            List<TBydbDatabaseDo> dbList = null;
            if ( enable == 1 ) {
                for ( TBydbSchemaDo info : chgList ) {
                    TBydbSchemaDo old = new TBydbSchemaDo();
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
            }
            else {
                for ( TBydbSchemaDo info : chgList ) {
                    TBydbSchemaDo old = new TBydbSchemaDo();
                    MyBeanUtils.copyBeanNotNull2Bean( info, old );
                    oldList.add( old );
                    info.setEnable( 0 );
                    info.setSynFlag( 0 );
                }
            }

            schemaService.updateBeanWithFlag( chgList, dbList );

            for ( int i = 0; i < chgList.size(); i++ ) {
                TBydbSchemaDo info = chgList.get( i );
                TBydbSchemaDo old = oldList.get( i );
                HashMap<String, Object> map = new HashMap<>();
                if ( dbList != null ) {
                    for ( TBydbDatabaseDo tBydbDatabaseDo : dbList ) {
                        if ( tBydbDatabaseDo.getId().equals( info.getDbId() ) ) {
                            map.put( "db", tBydbDatabaseDo );
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


    @ApiOperation(value = "bydb库内容", notes = "bydb库内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "bydb库 id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    @ResponseBody
    public Object info( String id ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( StringUtils.isBlank( id ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }
            TBydbSchemaDo modelVo = schemaService.findById( id );
            resMap.setSingleOk( modelVo, "成功" );

        }
        catch ( Exception ex ) {
            ex.printStackTrace();
            resMap.setErr( "查询失败" );
            logger.error( "查询异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除bydb库", notes = "删除bydb库")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "bydb库 id", dataType = "String", required = true, paramType = "query")
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

//            Example exp = new Example( TBydbObjectDo.class );
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andIn( "schemaId", idList );
//            //criteria.andCondition( " id in(select table_id from t_bydb_ds_entity where table_id is not null ) ");
//            int cnt = objectService.findCountByExample( exp );
//            if ( cnt > 0 ) {
//                return resMap.setErr( "库有下级对象被使用，不能删除" ).getResultMap();
//            }

            Example exp = new Example( TBydbSchemaDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", idList );
            criteria.andEqualTo( "userId", user.getUserId() );
            List<TBydbSchemaDo> list = schemaService.findByExample( exp );

//            if (list.size()==0) {
//                return resMap.setErr("没有数据可删除").getResultMap();
//            }

//            if ( list.size() != idList.size() ) {
//                return resMap.setErr( "数据已变化，删除失败" ).getResultMap();
//            }
//            String ids = "'" + StringUtils.join( idList, "','" ) + "'";
//            exp = new Example( TTruModelObjectDo.class );
//            criteria = exp.createCriteria();
//            criteria.andCondition( " real_obj_id in (select a.id from t_bydb_object a, t_bydb_schema b where a.schema_id =b.id  and b.id in( " + ids + ") )" );
//            cnt = modelObjService.findCountByExample( exp );
//            if ( cnt > 0 ) {
//                return resMap.setErr( "数据被使用，不能删除" ).getResultMap();
//            }

            ObjectResp<String> retVal = apiTruModelService.delSchema( idList,"0", user.getTokenId() );
            if( retVal.isSuccess() ) {
                schemaService.deleteWhithRel( list );
            }
            else{
                return retVal;
            }
            resMap.setOk( "删除成功" );
//            String times = String.valueOf(System.currentTimeMillis());
//            for (TBydbSchemaDo info : list) {
//                try {
//
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(user, info, "删除-bydb库" + times);
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
            TBydbSchemaDo modelVo = new TBydbSchemaDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo, hru );
            modelVo.setQryCond( ComUtil.chgLikeStr( modelVo.getQryCond() ) );
            modelVo.setSchemaName( ComUtil.chgLikeStr( modelVo.getSchemaName() ) );

            long findCnt = schemaService.findBeanCnt( modelVo );
            //modelVo.genPage();
            modelVo.genPage( findCnt );

            List<TBydbSchemaDo> list = schemaService.findBeanList( modelVo );

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

            Example exp = new Example( TBydbObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "schemaId", split);

            List<TBydbObjectDo> list = objectService.findByExample(exp);
            int cnt  = list.size();
            if( cnt == 0 ){
                return resMap.setErr("没有数据可操作").getResultMap();
            }
            if( list.size() != split.size() ){
                return resMap.setErr("数据已变化，操作失败").getResultMap();
            }

            List<String> nodeIdList = Arrays.asList( nodeId.split( "(,|\\s)+" ) );

            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            Map<String,TBydbDatabaseDo> dbMap = new HashMap<>();
            Map<String,TBydbSchemaDo> schemaMap = new HashMap<>();

            for ( TBydbObjectDo info : list ) {
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
            }

            for ( TBydbObjectDo info : list ) {
                if ( StringUtils.isBlank( info.getNodePartyId() ) ) {
                    info.setNodePartyId( nodePartyDo.getId() );
                }
                List<TBydbDataNodeDo> tabNodeList = new ArrayList<>();
                List<TBydbDataNodeDo> list1 = dataNodeService.findByDataId( info.getId() );
                List<TBydbDataNodeDo> delDnList = new ArrayList<>();
                List<TBydbDataNodeDo> addDnList = new ArrayList<>();
                List<TBydbDataNodeDo> modDnList = new ArrayList<>();
                for ( TBydbDataNodeDo dn : list1 ) {
                    if( !nodeIdList.contains( dn.getId() ) ){
                        delDnList.add( dn );
                    }
                    else{
                        modDnList.add( dn );
                    }
                }
                for ( String s : nodeIdList ) {
                    boolean bfound = false;
                    for ( TBydbDataNodeDo dn : list1 ) {
                        if( dn.getNodeId().equals( s )) {
                            bfound = true;
                        }
                    }
                    if(!bfound) {
                        TBydbDataNodeDo dataNodeDo = new TBydbDataNodeDo();
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
                List<TBydbFieldDo> fieldList = fieldService.selectByObjectId( info.getId() );
                BydbObjectFieldsVo objFld = new BydbObjectFieldsVo();
                MyBeanUtils.copyBeanNotNull2Bean( info,objFld );
                objFld.setFieldList( fieldList );
                objFld.setDataNodeList(  tabNodeList );
                List<BydbObjectFieldsVo> bofList = new ArrayList<>();
                bofList.add( objFld );
                Map<String, Object> retMap = apiTruModelService.synTable( bofList, ud.getTokenId() );
                if ( retMap.containsKey( "success" ) && !(boolean) retMap.get( "success" ) ) {
                    return resMap.setErr( "保存完成，更新同步信息失败" ).getResultMap();
                }
                TBydbObjectDo tmp = new TBydbObjectDo();
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
//                    TBydbObjectDo tmp = new TBydbObjectDo();
//                    tmp.setId( info.getId() );
//                    tmp.setNodePartyId( info.getNodePartyId() );
//                    tmp.setShareTime( info.getShareTime() );
//                    tmp.setShareFlag( info.getShareFlag() );
//                    bydbObjectService.updateNoNull( tmp );
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
