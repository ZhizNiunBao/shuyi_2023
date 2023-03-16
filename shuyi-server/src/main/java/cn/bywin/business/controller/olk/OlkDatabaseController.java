package cn.bywin.business.controller.olk;


import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.olk.TOlkCatalogTypeDo;
import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkDcServerDo;
import cn.bywin.business.bean.olk.TOlkFieldDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.olk.TOlkSchemaDo;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.view.federal.FDataApproveVo;
import cn.bywin.business.bean.view.olk.OlkObjectWithFieldsVo;
import cn.bywin.business.bean.view.olk.UserGrantDbSchema;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.Constants;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.hetu.CatalogInfo;
import cn.bywin.business.hetu.DynamicCatalogResult;
import cn.bywin.business.hetu.EncryptFlag;
import cn.bywin.business.hetu.HetuDynamicCatalogUtil;
import cn.bywin.business.hetu.HetuInfo;
import cn.bywin.business.hetu.HetuJdbcOperate;
import cn.bywin.business.hetu.HetuJdbcOperateComponent;
import cn.bywin.business.job.OlkDataNodeJob;
import cn.bywin.business.service.federal.DataSourceService;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.service.olk.OlkCatalogTypeService;
import cn.bywin.business.service.olk.OlkDatabaseService;
import cn.bywin.business.service.olk.OlkDcServerService;
import cn.bywin.business.service.olk.OlkFieldService;
import cn.bywin.business.service.olk.OlkModelObjectService;
import cn.bywin.business.service.olk.OlkObjectService;
import cn.bywin.business.service.olk.OlkSchemaService;
import cn.bywin.business.trumodel.ApiOlkDbService;
import cn.bywin.business.trumodel.ApiTruModelService;
import cn.bywin.cache.SysParamSetOp;
import cn.bywin.common.resp.ListResp;
import cn.bywin.common.resp.ObjectResp;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
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
@Api(tags = "olk-数据目录管理-olkdatabase")
@RequestMapping("/olkdatabase")
public class OlkDatabaseController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Autowired
    private OlkDatabaseService databaseService;

    @Autowired
    private OlkSchemaService schemaService;

    @Autowired
    private OlkObjectService objectService;

    @Autowired
    private OlkFieldService fieldService;

    //@Autowired
    //private OlkUdfService udfService;

    @Autowired
    private OlkCatalogTypeService catalogTypeService;

    @Autowired
    private NodePartyService nodePartyService;

    @Autowired
    private DataSourceService dbSourceService;

    @Autowired
    private ApiTruModelService apiTruModelService;

    @Autowired
    private ApiOlkDbService apiOlkDbService;

    @Autowired
    private OlkModelObjectService modelObjService;

//    @Autowired
//    private OlkDataNodeService dataNodeService;

    @Autowired
    private OlkDcServerService dcService;


    String connectChar = "^";
    String splitChar = "\\^";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private String olkRef = "olk_ref_";

    @Autowired
    private HetuJdbcOperate hetuJdbcOperate;

    @Autowired
    private HetuJdbcOperateComponent hetuJdbcOperateComponent;

    @ApiOperation(value = "按配置新建olk数据目录", notes = "按配置新建olk数据目录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chName", value = "备注名称", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "catalogName", value = "英文名称", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "connectorName", value = "类型", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "properties", value = "配置参数", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "catalogType", value = "目录类型", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dcId", value = "节点id", dataType = "String", required = true, paramType = "query")

    })
    @RequestMapping(value = "/newdb", method = {RequestMethod.POST})
    @ResponseBody
    public Object newDb( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser( request );
            if ( user == null || StringUtils.isBlank( user.getUserName() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }

            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );

            String catalogName = hru.getNvlPara( "catalogName" );
            String chName = hru.getNvlPara( "chName" );
            String connectorName = hru.getNvlPara( "connectorName" );
            String propertiesJson = hru.getNvlPara( "properties" );
            String propSet = hru.getNvlPara( "propSet" );
            String catalogType = hru.getNvlPara( "catalogType" );
            String dbsourceId = hru.getNvlPara( "dbsourceId" );
            String dcId = hru.getNvlPara( "dcId" );

            TOlkDatabaseDo info = new TOlkDatabaseDo();

            if ( StringUtils.isBlank( chName ) ) {
                return resMap.setErr( "中文名称不能为空,只能为2-30个字符,字符可为中文大小写字母数字" ).getResultMap();
            }
            else {
                String reg = "^[a-zA-Z0-9\\\u4e00-\\\u9fa5]{2,30}$";
                Pattern pat = Pattern.compile( reg, Pattern.CASE_INSENSITIVE );
                Matcher matcher = pat.matcher( chName );
                if ( !matcher.find() ) {
                    return resMap.setErr( "中文名称只能为2-30个字符,字符可为中文大小写字母数字" ).getResultMap();
                }
            }

            if ( StringUtils.isBlank( catalogName ) ) {
                return resMap.setErr( "英文名称不能为空,只能为大小写字母开头3-20个字符，其他字符可为大小写字母数字和下划线" ).getResultMap();
            }
            else {
                String reg = "^[a-zA-Z]{1}[a-zA-Z0-9_]{2,20}$";
                Pattern pat = Pattern.compile( reg, Pattern.CASE_INSENSITIVE );
                Matcher matcher = pat.matcher( catalogName );
                if ( !matcher.find() ) {
                    return resMap.setErr( "英文名称只能为大小写字母开头3-20个字符，其他字符可为大小写字母数字和下划线" ).getResultMap();
                }
            }
            if ( "system".equalsIgnoreCase( catalogName ) ) {
                return resMap.setErr( "名称不能为system" ).getResultMap();
            }
            if ( StringUtils.isBlank( connectorName ) ) {
                return resMap.setErr( "连接类型不能为空" ).getResultMap();
            }
            if ( StringUtils.isBlank( propertiesJson ) && StringUtils.isBlank( propSet ) ) {
                return resMap.setErr( "配置不能为空" ).getResultMap();
            }
            if ( StringUtils.isNotBlank( catalogType ) ) {
                TOlkCatalogTypeDo ctype = catalogTypeService.findById( catalogType );
                if ( ctype == null ) {
                    return resMap.setErr( "目录类型不存在" ).getResultMap();
                }
                info.setCatalogType( ctype.getId() );
            }

            TOlkDcServerDo dcDo = dcService.findById( dcId );
            if ( dcDo == null ) {
                return resMap.setErr( "节点不存在" ).getResultMap();
            }
            if ( dcDo.getDcType() == null || dcDo.getDcType() != 1 ) {
                return resMap.setErr( "不能在此节点添加目录" ).getResultMap();
            }
            if ( dcDo.getEnable() == null || dcDo.getEnable() != 1 ) {
                return resMap.setErr( "节点未启用" ).getResultMap();
            }

            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            if( StringUtils.isBlank(  nodePartyDo.getPrefixSymbol() )){
                return resMap.setErr( "节点前缀符号未配置" ).getResultMap();
            }
            info.setNodePartyId( nodePartyDo.getId() );
            info.setDbsourceId( Constants.dchetu );
            info.setId( nodePartyDo.getPrefixSymbol()+"odb"+ ComUtil.genId() );
            info.setDbChnName( chName );
            info.setDcCode( dcDo.getDcCode() );
            info.setUserId( user.getUserId() );
            info.setUserName( user.getChnName() );
            info.setUserAccount( user.getUserName() );
            info.setSynFlag( 0 );

//            catalogName = dcDo.getDcCode() + "_" + catalogName;
//            info.setDbName(catalogName);
//            info.setDcDbName(catalogName);

            info.setDbName( dcDo.getDcCode() + "." + catalogName );
            info.setDcDbName( catalogName );

            info.setDcId( dcDo.getId() );

            info.setDbType( connectorName );
            LoginUtil.setBeanInsertUserInfo( info, user );

            final long sameNameCount = databaseService.findSameNameCount( info );
            if ( sameNameCount > 0 ) {
                return resMap.setErr( "名称已使用" ).getResultMap();
            }

            Map<String, String> properties = null;
            if ( StringUtils.isNotBlank( propertiesJson ) ) {
                try {
                    properties = JsonUtil.deserialize( propertiesJson, Map.class );
                }
                catch ( Exception ee ) {

                }
                if ( properties == null ) {
                    Properties prop = new Properties();
                    prop.load( new StringReader( propertiesJson ) );
                    properties = new HashMap<>( (Map) prop );
                }
            }
            else {
                properties = new HashMap<>();
                Type jsonType = new TypeToken<List<HashMap>>() {
                }.getType();
                List<Map> list = JsonUtil.deserialize( propSet, jsonType );
                for ( Map map : list ) {
                    String name = map.get( "name" ) == null ? "" : map.get( "name" ).toString().trim();
                    String value = map.get( "value" ) == null ? "" : map.get( "value" ).toString().trim();
                    if ( StringUtils.isNotBlank( name ) && StringUtils.isNotBlank( value ) ) {
                        properties.put( name, value );
                    }
                }
            }

            CatalogInfo catalogInfo = hetuJdbcOperateComponent.genCatalogInfo( EncryptFlag.isEncrypt( dcDo.getEncryptFlag() ),
                    catalogName, connectorName, properties );

            //String ddl = JsonUtil.toJson(catalogInfo);
            //info.setDdlContent(ddl);

            Integer maxOrder = databaseService.findMaxOrder( dcDo.getId() );
            int norder = 10;
            if ( maxOrder != null ) {
                norder = maxOrder.intValue() + 10;
            }
            info.setNorder( norder );
            info.setEnable( 1 );

            OlkDataNodeJob.addDb( info.getId() );

            HetuInfo hetuInfo = hetuJdbcOperateComponent.genHetuInfo( dcDo );
            boolean bexists = HetuDynamicCatalogUtil.checkCatalogExist( hetuInfo, info.getDbName() );
            if ( bexists ) {
                resMap.setSingleOk( info, "目录已存在" );
            }
            DynamicCatalogResult dynamicCatalogResult = HetuDynamicCatalogUtil.addCatalog( hetuInfo, catalogInfo );
            if ( dynamicCatalogResult.isSuccessful() ) {
                //success
                databaseService.insertBean( info );
                String redKey = olkRef + info.getId();
                loadCatalogSchemaOnly( info, dcDo, nodePartyDo, redKey, user, request );
                new Thread( () -> {
                    loadOlkCatalogMeta( info, dcDo, null, null, nodePartyDo, redKey, user, request );
                } ).start();
                //new LogActionOp( SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).addLog(user, info, "新增-按配新增olk数据目录");
                resMap.setSingleOk( info, "保存成功" );
            }
            else {
                String codeMap = "401 UNAUTHORIZED 没有权限添加目录 没有权限修改目录 没有权限修改目录 \n" +
                        "302 FOUND 目录已存在 - - \n" +
                        "404 NOT_FOUND 动态目录已停用 目录不存在或动态目录已停用 目录不存在或动态目录已停用 \n" +
                        "400 BAD_REQUEST 请求不正确 请求不正确 请求不正确 \n" +
                        "409 CONFLICT 另一个会话正在操作目录 另一个会话正在操作目录 另一个会话正在操作目录 \n" +
                        "500 INTERNAL_SERVER_ERROR 协调节点内部发生错误 协调节点内部发生错误 协调节点内部发生错误 \n" +
                        "201 CREATED 成功 成功 - \n" +
                        "204 NO_CONTENT - - 成功 \n";
                String[] split = codeMap.split( "\\n" );
                String errMsg = "";
                int responseCode = dynamicCatalogResult.getResponseCode();
                for ( String s : split ) {
                    if ( s.indexOf( "" + responseCode ) >= 0 ) {
                        errMsg = s.split( "\\s+" )[2];
                    }
                }
                resMap.setErr( String.format( "提交失败，返回:%s %s", responseCode, errMsg ) );
            }

        }
        catch ( Exception ex ) {
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }

    private void loadCatalogSchemaOnly( TOlkDatabaseDo dbInfo, TOlkDcServerDo dcDo,FNodePartyDo nodePartyDo, String redKey, UserDo user, HttpServletRequest request ) {

        List<String> schemaNameList = new ArrayList<>();
        //List<TOlkSchemaDo> delSchemaList = null;

        List<TOlkSchemaDo> allSchemaList = new ArrayList<>();

        BoundValueOperations<String, Object> bvo = redisTemplate.boundValueOps( redKey );
        //HashMap<String, String> schemaMap = new HashMap<>();

        try ( HetuJdbcOperate jdbcOp = hetuJdbcOperateComponent.genHetuJdbcOperate( dcDo, user ) ) {

            JsonObject noDealSchema = JsonUtil.toJsonObject( SysParamSetOp.readValue( "OlkNoDealSchema", "{}" ) );
            TOlkSchemaDo schTmp = new TOlkSchemaDo();
            schTmp.setDbId( dbInfo.getId() );
            List<TOlkSchemaDo> schemaList = schemaService.find( schTmp );

            JsonElement jsonElement = noDealSchema.get( dbInfo.getDbType() );
            logger.debug( "dbType:{},set:{} , noDealSchema:{}", dbInfo.getDbType(), jsonElement, noDealSchema );

            List<String> noDealList = new ArrayList<>();
            noDealList.add( "information_schema" );
            noDealList.add( "sys" );
            noDealList.add( "performance_schema" );
            if ( jsonElement != null && !jsonElement.isJsonNull() && StringUtils.isNotBlank( jsonElement.getAsString() ) ) {

                String[] split = jsonElement.getAsString().split( "\\\r|\\\n|," );
                for ( String s : split ) {
                    if ( StringUtils.isNotBlank( s ) ) {
                        noDealList.add( s.toLowerCase() );
                    }
                }
                logger.info( "不处理模式:{},noDealList:{}", jsonElement.getAsString(), noDealList );
            }

            bvo.expire( 3, TimeUnit.MINUTES );
            String showSchema = String.format( "SHOW SCHEMAS from %s", dbInfo.getDcDbName() );
            logger.info( showSchema );
            List<Map<String, Object>> list1 = jdbcOp.selectData( showSchema );
            logger.debug( "schema:{}", list1 );
            bvo.expire( 3, TimeUnit.MINUTES );
            for ( Map<String, Object> dat : list1 ) {
                Iterator<Object> iterator = dat.values().iterator();
                if ( iterator.hasNext() ) {
                    String sn = iterator.next().toString();
                    if ( noDealList.indexOf( sn.toLowerCase() ) < 0 && sn.indexOf( " " ) < 0 ) {
                        schemaNameList.add( sn );
                    }
                    else {
                        logger.info( "忽略schema:{}", sn );
                    }
                }
            }
            List<TOlkSchemaDo> delList = new ArrayList<>();
            for ( TOlkSchemaDo sch : schemaList ) {
                if ( schemaNameList.indexOf( sch.getSchemaName() ) < 0 ) {
                    delList.add( sch );
                }
            }
            if ( delList.size() > 0 ) {
                List<String> deIdList = delList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                apiOlkDbService.delOlkSchema(deIdList,"0", user.getTokenId() );
                schemaService.deleteWhithRel( delList );
//                for (TOlkSchemaDo delObj : delList) {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(user, delObj, "删除-刷新olk模式与对象");
//                }
            }
            int norder = 10;
            schemaList = schemaService.find( schTmp );
            for ( String schemaName : schemaNameList ) {
                boolean bfound = false;
                for ( TOlkSchemaDo schemaDo : schemaList ) {
                    if ( schemaDo.getSchemaName().equals( schemaName ) ) {
                        bfound = true;
                        schemaDo.setNorder( norder );
                        schemaService.updateBean( schemaDo );
                        allSchemaList.add( schemaDo );
                        break;
                    }
                }
                if ( !bfound ) {
                    TOlkSchemaDo schemaDo = new TOlkSchemaDo();
                    schemaDo.setNorder( norder );
                    schemaDo.setEnable( 1 );
                    schemaDo.setDcId( dbInfo.getDcId() );
                    schemaDo.setNodePartyId( dbInfo.getNodePartyId() );
                    schemaDo.setUserId( dbInfo.getUserId() );
                    schemaDo.setUserAccount( dbInfo.getUserAccount() );
                    schemaDo.setUserName( dbInfo.getUserName() );
                    schemaDo.setSchemaName( schemaName );
                    schemaDo.setScheFullName( dbInfo.getDbName() + "." + schemaName );
                    schemaDo.setDbId( dbInfo.getId() );
                    schemaDo.setId( nodePartyDo.getPrefixSymbol() + "odb" + ComUtil.genId() );
                    schemaDo.setSynFlag( 0 );
                    LoginUtil.setBeanInsertUserInfo( schemaDo, user );
                    schemaService.insertBean( schemaDo );
                    allSchemaList.add( schemaDo );
                    schemaList.add( schemaDo );
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).addLog(user, schemaDo, "新增-刷新olk模式与对象");
                }
                norder += 10;
            }
        }
        catch ( Exception e ) {
            logger.error( "刷新节点" + dcDo.getDcName() + "的" + dbInfo.getDcDbName() + "元数据数据失败", e );
        }
        redisTemplate.delete( redKey );
        OlkDataNodeJob.reInit();

    }

    private void loadOlkCatalogMeta( TOlkDatabaseDo dbInfo, TOlkDcServerDo dcDo, TOlkSchemaDo schemaInfo, TOlkObjectDo objectInfo, FNodePartyDo nodePartyDo, String redKey, UserDo user, HttpServletRequest request ) {

        List<TOlkObjectDo> allObjectList = new ArrayList<>();
        List<String> schemaNameList = new ArrayList<>();

        List<TOlkSchemaDo> allSchemaList = new ArrayList<>();

        BoundValueOperations<String, Object> bvo = redisTemplate.boundValueOps( redKey );
        HashMap<String, String> schemaMap = new HashMap<>();
        try ( HetuJdbcOperate jdbcOp = hetuJdbcOperateComponent.genHetuJdbcOperate( dcDo, user ) ) {

            //某张表
            if ( objectInfo != null ) {
                TOlkSchemaDo schemaDo = schemaService.findById( objectInfo.getSchemaId() );
                schemaMap.put( schemaDo.getId(), schemaDo.getSchemaName() );
                allObjectList.add( objectInfo );
            }
            //整个shcema
            else if ( schemaInfo != null ) {
                allSchemaList.add( schemaInfo );
            }
            else {  //整个catalog
                JsonObject noDealSchema = JsonUtil.toJsonObject( SysParamSetOp.readValue( "OlkNoDealSchema", "{}" ) );
                TOlkSchemaDo schTmp = new TOlkSchemaDo();
                schTmp.setDbId( dbInfo.getId() );
                List<TOlkSchemaDo> oldList = schemaService.find( schTmp );

                JsonElement jsonElement = noDealSchema.get( dbInfo.getDbType() );
                logger.debug( "dbType:{},set:{} , noDealSchema:{}", dbInfo.getDbType(), jsonElement, noDealSchema );

                List<String> noDealList = new ArrayList<>();
                noDealList.add( "information_schema" );
                noDealList.add( "sys" );
                noDealList.add( "performance_schema" );
                if ( jsonElement != null && !jsonElement.isJsonNull() && StringUtils.isNotBlank( jsonElement.getAsString() ) ) {

                    String[] split = jsonElement.getAsString().split( "\\\r|\\\n|," );
                    for ( String s : split ) {
                        if ( StringUtils.isNotBlank( s ) ) {
                            noDealList.add( s.toLowerCase() );
                        }
                    }
                    logger.info( "不处理模式:{},noDealList:{}", jsonElement.getAsString(), noDealList );
                }

                bvo.expire( 3, TimeUnit.MINUTES );
                String showSchema = String.format( "SHOW SCHEMAS from %s", dbInfo.getDcDbName() );
                logger.info( showSchema );
                List<Map<String, Object>> list1 = jdbcOp.selectData( showSchema );
                logger.debug( "schema:{}", list1 );
                bvo.expire( 3, TimeUnit.MINUTES );
                for ( Map<String, Object> dat : list1 ) {
                    Iterator<Object> iterator = dat.values().iterator();
                    if ( iterator.hasNext() ) {
                        String sn = iterator.next().toString();
                        if ( noDealList.indexOf( sn.toLowerCase() ) < 0 && sn.indexOf( " " ) < 0 ) {
                            schemaNameList.add( sn );
                        }
                        else {
                            logger.info( "忽略schema:{}", sn );
                        }
                    }
                }
                List<TOlkSchemaDo> delList = new ArrayList<>();
                for ( TOlkSchemaDo sch : oldList ) {
                    if ( schemaNameList.indexOf( sch.getSchemaName() ) < 0 ) {
                        delList.add( sch );
                    }
                }
                if ( delList.size() > 0 ) {
                    List<String> deIdList = delList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                    apiOlkDbService.delOlkSchema(deIdList,"0", user.getTokenId() );
                    schemaService.deleteWhithRel( delList );
//                    for (TOlkSchemaDo delObj : delList) {
//                        new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(user, delObj, "删除-刷新olk模式与对象");
//                    }
                }
                int norder = 10;
                oldList = schemaService.find( schTmp );
                for ( String schemaName : schemaNameList ) {
                    boolean bfound = false;
                    for ( TOlkSchemaDo schemaDo : oldList ) {
                        if ( schemaDo.getSchemaName().equals( schemaName ) ) {
                            bfound = true;
                            schemaDo.setNorder( norder );
                            schemaService.updateBean( schemaDo );
                            allSchemaList.add( schemaDo );
                            break;
                        }
                    }
                    if ( !bfound ) {
                        TOlkSchemaDo schemaDo = new TOlkSchemaDo();
                        schemaDo.setNorder( norder );
                        schemaDo.setEnable( 1 );
                        schemaDo.setNodePartyId( dbInfo.getNodePartyId() );
                        schemaDo.setDcId( dbInfo.getDcId() );
                        schemaDo.setUserId( dbInfo.getUserId() );
                        schemaDo.setUserName( dbInfo.getUserName() );
                        schemaDo.setUserAccount( dbInfo.getUserAccount() );
                        schemaDo.setSynFlag( 0 );
                        schemaDo.setSchemaName( schemaName );
                        schemaDo.setScheFullName( dbInfo.getDbName() + "." + schemaName );
                        schemaDo.setDbId( dbInfo.getId() );
                        schemaDo.setId( nodePartyDo.getPrefixSymbol() + "odb" + ComUtil.genId() );
                        LoginUtil.setBeanInsertUserInfo( schemaDo, user );
                        schemaService.insertBean( schemaDo );
                        allSchemaList.add( schemaDo );
                        delList.add( schemaDo );
//                        new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).addLog(user, schemaDo, "新增-刷新olk模式与对象");
                    }
                    norder += 10;
                }

            }

            for ( TOlkSchemaDo schemaDo : allSchemaList ) {
                if ( schemaDo.getEnable() == null || schemaDo.getEnable() != 1 ) {
                    continue;
                }
                schemaMap.put( schemaDo.getId(), schemaDo.getSchemaName() );
                //db 所有表
                TOlkObjectDo objTmp = new TOlkObjectDo();
                objTmp.setSchemaId( schemaDo.getId() );
                List<TOlkObjectDo> delObjectList = objectService.find( objTmp );
                int objorder = 10;
                bvo.expire( 3, TimeUnit.MINUTES );
                //String showTable = String.format( "SHOW TABLES from %s.\"%s\"", dbInfo.getDcDbName(), schemaDo.getSchemaName() );
                //logger.info( showTable );
                String sql ="select table_name as tablename,remarks as comment ,concat( table_cat,'.', table_schem) as schemaname from \"system\".jdbc.tables where  1=1  ";
                sql = String.format( " %s and table_cat ='%s' and table_schem ='%s' ", sql, dbInfo.getDcDbName() ,schemaDo.getSchemaName() );
                logger.info( sql );
                List<Map<String, Object>> list2 = jdbcOp.selectData( sql );
                logger.debug( "table:{}", list2 );
                bvo.expire( 3, TimeUnit.MINUTES );
                for ( Map<String, Object> dat : list2 ) {
                    //Iterator<Object> iterator = dat.values().iterator();
                    //if ( iterator.hasNext() ) {
                        String tabName = (String) dat.get( "tablename" );
                        boolean bobj = false;
                        for ( TOlkObjectDo objectDo : delObjectList ) {
                            if ( objectDo.getObjectName().equals( tabName ) ) {
                                bobj = true;
                                if(StringUtils.isBlank( objectDo.getObjChnName() )) {
                                    objectDo.setObjChnName( (String) dat.get( "comment" ) );
                                }
                                objectDo.setNorder( objorder );
                                objectService.updateBean( objectDo );
                                delObjectList.remove( objectDo );
                                allObjectList.add( objectDo );
                                break;
                            }
                        }
                        if ( !bobj ) {
                            TOlkObjectDo objectDo = new TOlkObjectDo();
                            objectDo.setDcId( dbInfo.getDcId() );
                            objectDo.setNodePartyId( dbInfo.getNodePartyId() );
                            objectDo.setUserId( dbInfo.getUserId() );
                            objectDo.setUserName( dbInfo.getUserName() );
                            objectDo.setUserAccount( dbInfo.getUserAccount() );
                            objectDo.setSynFlag( 0 );
                            objectDo.setDbId( dbInfo.getId() );
                            objectDo.setSchemaId( schemaDo.getId() );
                            objectDo.setObjChnName( (String)dat.get( "comment" ) );
                            objectDo.setNorder( objorder );
                            objectDo.setObjectName( tabName );
                            objectDo.setObjFullName( schemaDo.getScheFullName() + "." + objectDo.getObjectName() );
                            objectDo.setStype( "table" );
                            objectDo.setEnable( 1 );
                            objectDo.setShareFlag( 0 );
                            objectDo.setSynFlag( 0 );

                            objectDo.setId( nodePartyDo.getPrefixSymbol() + "odb" + ComUtil.genId() );
                            LoginUtil.setBeanInsertUserInfo( objectDo, user );
                            objectService.insertBean( objectDo );
                            allObjectList.add( objectDo );
//                            new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).addLog(user, objectDo, "新增-刷新olk模式与对象");
                        }
                        objorder += 10;


                }
                if ( delObjectList.size() > 0 ) {
                    List<String> deIdList = delObjectList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                    apiOlkDbService.delOlkTable(delObjectList,user.getTokenId() );
                    objectService.deleteWhithOthers( delObjectList );
//                    for (TOlkObjectDo objectDo : delObjectList) {
//                        new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(user, objectDo, "删除-刷新olk模式与对象");
//                    }
                }
            }
            for ( TOlkObjectDo objectDo : allObjectList ) {
                if ( objectDo.getEnable() == null || objectDo.getEnable() != 1 ) {
                    continue;
                }
                List<TOlkFieldDo> addFieldList = new ArrayList<>();
                List<TOlkFieldDo> modFieldList = new ArrayList<>();

                //db 所有字段
                TOlkFieldDo fieldTmp = new TOlkFieldDo();
                fieldTmp.setObjectId( objectDo.getId() );
                List<TOlkFieldDo> delFieldList = fieldService.find( fieldTmp );
                HashMap<String, TOlkFieldDo> modMap = new HashMap<>();

                String schemaName = schemaMap.get( objectDo.getSchemaId() );
                int fieldorder = 10;
                bvo.expire( 3, TimeUnit.MINUTES );
                //String showField = String.format( "SHOW COLUMNS FROM %s.\"%s\".\"%s\"", dbInfo.getDcDbName(), schemaName, objectDo.getObjectName() );
                String sql = "select TABLE_SCHEMA schemaname,TABLE_NAME tablename, COLUMN_NAME columnname,ORDINAL_POSITION ordinalposition,\n" +
                        "data_type as datatype,data_type as columntype,comment columncomment from "+dbInfo.getDcDbName()+".information_schema.COLUMNS a where 1=1 ";
                sql = String.format( " %s and TABLE_SCHEMA ='%s' ", sql, schemaName );
                sql = String.format( " %s and TABLE_NAME ='%s' ", sql, objectDo.getObjectName() );

                logger.debug( sql );
                List<Map<String, Object>> list3 = jdbcOp.selectData( sql );
                logger.debug( "field:{}", list3 );
                bvo.expire( 3, TimeUnit.MINUTES );
                for ( Map<String, Object> dat3 : list3 ) {
                    TOlkFieldDo fieldDo = new TOlkFieldDo();
                    fieldDo.setFieldName( (String) dat3.get( "columnname" ) );
                    fieldDo.setFieldType( (String) dat3.get( "columntype" ) );
                    fieldDo.setChnName( (String) dat3.get( "columncomment" ) );
                    fieldDo.setNodePartyId( dbInfo.getNodePartyId() );
                    fieldDo.setDcId( dbInfo.getDcId() );
                    fieldDo.setUserId( dbInfo.getUserId() );
                    fieldDo.setUserName( dbInfo.getUserName() );
                    fieldDo.setUserAccount( dbInfo.getUserAccount() );
                    fieldDo.setDbId( dbInfo.getId() );
                    fieldDo.setNodePartyId( dbInfo.getNodePartyId() );
                    fieldDo.setSchemaId( objectDo.getSchemaId() );
                    fieldDo.setObjectId( objectDo.getId() );
                    fieldDo.setFieldFullName( objectDo.getObjFullName() + "." + fieldDo.getFieldName() );
                    fieldDo.setNorder( fieldorder );
                    fieldDo.setEnable( 1 );
                    fieldorder += 10;
                    fieldDo.setId(nodePartyDo.getPrefixSymbol() + "odb" +  ComUtil.genId() );
                    LoginUtil.setBeanInsertUserInfo( fieldDo, user );
                    boolean bfield = false;
                    for ( TOlkFieldDo fd : delFieldList ) {
                        if ( fieldDo.getObjectId().equals( fd.getObjectId() ) && StringUtils.equalsIgnoreCase( fieldDo.getFieldName(), fd.getFieldName() ) ) {
                            bfield = true;
                            TOlkFieldDo old = new TOlkFieldDo();
                            MyBeanUtils.copyBeanNotNull2Bean( fd, old );
                            modMap.put( fd.getId(), old );
                            modFieldList.add( fd );
                            if(StringUtils.isBlank( fd.getChnName() )){
                                fd.setChnName( fieldDo.getChnName() );
                            }
                            fd.setFieldName( fieldDo.getFieldName() );
                            fd.setFieldFullName( fieldDo.getFieldFullName() );
                            fd.setFieldType( fieldDo.getFieldType() );
                            fd.setNorder( fieldDo.getNorder() );
                            delFieldList.remove( fd );
                            break;
                        }
                    }
                    if ( !bfield ) {
                        addFieldList.add( fieldDo );
                    }
                }
                TOlkObjectDo objTmp = new TOlkObjectDo();
                objTmp.setId( objectDo.getId() );
                objTmp.setSynFlag( 0 );
                objectService.updateNoNull( objTmp );
                fieldService.saveOneObject( addFieldList, modFieldList, delFieldList );
                OlkDataNodeJob.reInit();
//                for (TOlkFieldDo fieldDo : addFieldList) {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).addLog(user, fieldDo, "新增-刷新olk模式与对象");
//                }
//                for (TOlkFieldDo fieldDo : modFieldList) {
//                    TOlkFieldDo old = modMap.get(fieldDo.getId());
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(user, old, fieldDo, "修改-刷新olk模式与对象");
//                }
//                for (TOlkFieldDo fieldDo : addFieldList) {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(user, fieldDo, "删除-刷新olk模式与对象");
//                }
            }
        }
        catch ( Exception e ) {
            logger.error( "刷新节点" + dcDo.getDcName() + "的" + dbInfo.getDcDbName() + "元数据数据失败", e );
        }
        redisTemplate.delete( redKey );

    }

    @ApiOperation(value = "修改olk数据目录", notes = "修改olk数据目录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", example = "c2e9e8a5ce0e4a1b8be389dd1a7d5871", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dbChnName", value = "中文名称", dataType = "String", example = "mysql实例", required = false, paramType = "query"),
            @ApiImplicitParam(name = "catalogType", value = "目录分组ID", dataType = "String", example = "", required = false, paramType = "query"),
            @ApiImplicitParam(name = "enable", value = "是否启用 0未启用 1部分启用", dataType = "Integer", example = "1", required = false, paramType = "query")
    })
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }

            //@RequestBody TOlkDatabaseDo modelVo,
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            logger.debug( "{}", hru.getAllParaData() );
            TOlkDatabaseDo info = databaseService.findById( hru.getNvlPara( "id" ) );

            if ( info == null ) {
                return resMap.setErr( "目录不存在" ).getResultMap();
            }

            TOlkDatabaseDo oldData = new TOlkDatabaseDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, oldData );

            new PageBeanWrapper( info, hru, "" );
            info.setDbName( oldData.getDbName() );
            if ( StringUtils.isBlank( info.getDbName() ) ) {
                return resMap.setErr( "英文名称不能为空" ).getResultMap();
            }
            if ( oldData.getEnable() != null ) {
                info.setEnable( oldData.getEnable() );
            }
            else {
                if ( info.getEnable() == null ) {
                    info.setEnable( 1 );
                }
            }
//            if (StringUtils.isBlank(oldData.getDbName()) || !oldData.getDbName().equals(info.getDbName())) {
//                return resMap.setErr("英文名称不能为空且不能修改").getResultMap();
//            } else {
//            String reg = "^[a-zA-Z]{1}[a-zA-Z0-9_]{2,20}$";
//            Pattern pat = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
//            Matcher matcher = pat.matcher(info.getDbName());
//            if (!matcher.find()) {
//                return resMap.setErr("英文名称只能为大小写字母开头3-20个字符，其他字符可为大小写字母数字和下划线").getResultMap();
//            }
//            }

            if ( StringUtils.isBlank( info.getDbChnName() ) ) {
                return resMap.setErr( "中文名称不能为空,只能为2-30个字符,字符可为中文大小写字母数字" ).getResultMap();
            }
            else {
                String reg = "^[a-zA-Z0-9\\\u4e00-\\\u9fa5]{2,30}$";
                Pattern pat = Pattern.compile( reg, Pattern.CASE_INSENSITIVE );
                Matcher matcher = pat.matcher( info.getDbChnName() );
                if ( !matcher.find() ) {
                    return resMap.setErr( "中文名称只能为2-30个字符,字符可为中文大小写字母数字" ).getResultMap();
                }
            }

            if ( StringUtils.isBlank( info.getDbType() ) ) {
                return resMap.setErr( "连接类型不能为空" ).getResultMap();
            }

            //info.setDdlContent(oldData.getDdlContent());

            final long sameNameCount = databaseService.findSameNameCount( info );
            if ( sameNameCount > 0 ) {
                return resMap.setErr( "名称已使用" ).getResultMap();
            }
            info.setSynFlag( 0 );

            if ( oldData.getEnable() == null || !oldData.getEnable().equals( info.getEnable() ) ) {
                List<TOlkDatabaseDo> updList = new ArrayList<>();
                updList.add( info );
                databaseService.updateBeanWithFlag( updList );
                OlkDataNodeJob.reInit();
            }
            else {
                databaseService.updateBean( info );
            }
            //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(ud, oldData, info, "修改-olk数据目录");

            resMap.setSingleOk( info, "保存成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "批量启用或禁用olk数据目录", notes = "批量启用或禁用olk数据目录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "目录id", dataType = "String", required = true, paramType = "query"),
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
            Example exp = new Example( TOlkDatabaseDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", split );

            if ( enable == 0 ) {
                actType = "禁用";
            }
            else {
                actType = "启用";
            }

            String act = "修改-" + actType + "olk目录" + System.currentTimeMillis();
            if ( enable == 1 ) {
                criteria.andCondition( " (enable =0  or enable is null )" );
            }
            else {
                criteria.andEqualTo( "enable", 1 );
            }
            List<TOlkDatabaseDo> dbList = databaseService.findByExample( exp );
            if ( dbList.size() != split.size() ) {
                return resMap.setErr( " 数据已变化不能操作" ).getResultMap();
            }
            List<String> userIdList = dbList.stream().map( x -> x.getUserId() ).distinct().collect( Collectors.toList() );
            if(userIdList.size()!=1 ){
                return resMap.setErr( "不能同时操作不同用户数据" ).getResultMap();
            }
            String userId = userIdList.get( 0 );

            if ( enable == 0 ) {
                String ids = "'" + StringUtils.join( split, "','" ) + "'";
                exp = new Example( TOlkModelObjectDo.class );
                criteria = exp.createCriteria();
                criteria.andCondition( " real_obj_id in (select a.id from t_olk_object a, t_olk_database b where a.db_id =b.id  and b.id in( " + ids + ") )" );
                criteria.andEqualTo( "userId",userId);
                int cnt = modelObjService.findCountByExample( exp );
                if ( cnt > 0 ) {
                    return resMap.setErr( "数据被使用，不能"+actType ).getResultMap();
                }
            }

            List<TOlkDatabaseDo> oldList = new ArrayList<>();
            if ( enable == 1 ) {
                for ( TOlkDatabaseDo info : dbList ) {
                    TOlkDatabaseDo old = new TOlkDatabaseDo();
                    MyBeanUtils.copyBeanNotNull2Bean( info, old );
                    oldList.add( old );
                    info.setEnable( 1 );
                    info.setSynFlag( 0 );
                }
            }
            else {
                for ( TOlkDatabaseDo info : dbList ) {
                    TOlkDatabaseDo old = new TOlkDatabaseDo();
                    MyBeanUtils.copyBeanNotNull2Bean( info, old );
                    oldList.add( old );
                    info.setEnable( 0 );
                    info.setSynFlag( 0 );
                }
            }

            databaseService.updateBeanWithFlag( dbList );
            OlkDataNodeJob.reInit();

//            for (int i = 0; i < dbList.size(); i++) {
//                TOlkDatabaseDo info = dbList.get(i);
//                TOlkDatabaseDo old = oldList.get(i);
//                new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(ud, old, info, act);
//            }
            if ( enable == 0 ) {
                resMap.setOk( actType + "成功" );
            }
            else {
                resMap.setOk( actType + "启用成功" );
            }

        }
        catch ( Exception ex ) {
            resMap.setErr( actType + "失败" );
            logger.error( actType + "异常:", ex );
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "olk数据目录内容", notes = "olk数据目录内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "olk数据目录 id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    @ResponseBody
    public Object info( String id ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( StringUtils.isBlank( id ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }
            TOlkDatabaseDo modelVo = databaseService.findById( id );
            resMap.setSingleOk( modelVo, "成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "查询失败" );
            logger.error( "查询异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除olk数据目录", notes = "删除olk数据目录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "olk数据目录 id", dataType = "String", required = true, paramType = "query")
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

            List<String> idList = Arrays.asList( id.split( "(,|\\s)+" ) ).stream().filter( StringUtils::isNoneBlank ).distinct().collect( Collectors.toList() );

            if ( idList.size() != 1 ) {
                return resMap.setErr( "只能删除1个目录" ).getResultMap();
            }

            TOlkDatabaseDo info = databaseService.findById( idList.get( 0 ) );
            if ( info == null ) {
                return resMap.setErr( "数据目录不存在" ).getResultMap();
            }

            Example exp = new Example( TOlkDatabaseDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andCondition( " real_obj_id in (select a.id from t_olk_object a, t_olk_database b where a.db_id =b.id  and b.id in( '" + info.getId() + "') )" );
            criteria.andEqualTo( "userId",info.getUserId());
            int cnt = modelObjService.findCountByExample( exp );
            if ( cnt > 0 ) {
                return resMap.setErr( "数据被使用，不能删除" ).getResultMap();
            }

            ObjectResp<String> retVal = apiOlkDbService.delOlkDatabase( info.getId(), user.getTokenId() );
            if ( retVal.isSuccess() ) {
                if ( Constants.dchetu.equals( info.getDbsourceId() ) ) {
                    TOlkDcServerDo dcDo = dcService.findById( info.getDcId() );
                    HetuInfo hetuInfo = hetuJdbcOperateComponent.genHetuInfo( dcDo );
                    if ( HetuDynamicCatalogUtil.checkCatalogExist( hetuInfo, info.getDcDbName() ) ) {
                        DynamicCatalogResult dynamicCatalogResult = HetuDynamicCatalogUtil.deleteCatalog( hetuInfo, info.getDcDbName() );
                        if ( !dynamicCatalogResult.isSuccessful() ) {
                            return resMap.setErr( "删除服务端目录失败" + dynamicCatalogResult.getMessage() ).getResultMap();
                        }
                    }
                }
                databaseService.deleteWithOther( info );
                resMap.setOk( "删除成功" );
            }
            else {
                return retVal;
            }
        }
        catch ( Exception ex ) {
            resMap.setErr( "删除失败" );
            logger.error( "删除异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "数据目录分页", notes = "数据目录分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qryCond", value = "模糊条件", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dbsourceId", value = "数据源类型", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dcId", value = "节点id", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "catalogType", value = "目录分组ID", dataType = "String", example = "", required = false, paramType = "query"),
            @ApiImplicitParam(name = "catalogType", value = "目录分组ID", dataType = "String", example = "", required = false, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "页数", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", required = false, paramType = "query")
    })
    @RequestMapping(value = "/page", method = {RequestMethod.GET})
    @ResponseBody
    public Object page( HttpServletRequest request ) {
        UserDo user = LoginUtil.getUser( request );
        TOlkDatabaseDo modelVo = new TOlkDatabaseDo();
        HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
        logger.debug( "{}", hru.getAllParaData() );
        new PageBeanWrapper( modelVo, hru );

        ResponeMap resMap = this.genResponeMap();
        try {

//            TOlkDcServerDo dcTmp = new TOlkDcServerDo();
//            dcTmp.setManageAccount(user.getUserName());
//            List<TOlkDcServerDo> dcList = dcserverService.find(dcTmp);
//            if (dcList.size() == 0) {
//                return resMap.setErr("用户未关联节点").getResultMap();
//            } else if (dcList.size() > 0) {
//                modelVo.setDcId(dcList.get(0).getId());
//            }
            if ( StringUtils.isBlank( modelVo.getUserAccount() ) ) {
                modelVo.setUserAccount( user.getUserName() );
            }

            modelVo.setQryCond( ComUtil.chgLikeStr( modelVo.getQryCond() ) );
            modelVo.setDbName( ComUtil.chgLikeStr( modelVo.getDbName() ) );

            long findCnt = databaseService.findBeanCnt( modelVo );
            modelVo.genPage( findCnt );

            List<TOlkDatabaseDo> list = databaseService.findBeanList( modelVo );

            Map< String, TOlkDcServerDo> dcMap = dcService.findAll().stream().collect( Collectors.toMap( x->x.getId(), x->x ));

            List<Object> dbList =new ArrayList<>();
            for ( TOlkDatabaseDo tOlkDatabaseDo : list ) {
                //tOlkDatabaseDo.setDcDbName( ComUtil.trsEmpty( tOlkDatabaseDo.getDcDbName(), tOlkDatabaseDo.getDbChnName() ) );
                HashMap<String,Object> datMap = new HashMap<>();
                MyBeanUtils.copyBean2Map( datMap,tOlkDatabaseDo );
                TOlkDcServerDo tOlkDcServerDo = dcMap.get( tOlkDatabaseDo.getDcId() );
                if( tOlkDcServerDo == null ){
                    tOlkDcServerDo = new TOlkDcServerDo();
                }
                datMap.put( "dcName", tOlkDcServerDo.getDcName() );
                dbList.add( datMap );
            }
            resMap.setPageInfo( modelVo.getPageSize(), modelVo.getCurrentPage() );
            resMap.setOk( findCnt, dbList, "获取数据目录列表成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取数据目录列表失败" );
            logger.error( "获取数据目录列表失败:", ex );
        }
        return resMap.getResultMap();
    }

    /*
    @ApiOperation(value = "获取数据目录选择列表", notes = "获取数据目录选择列表")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/option", method = {RequestMethod.GET})
    @ResponseBody
    public Object option() {
        ResponeMap resMap = this.genResponeMap();
        try {
            TOlkDatabaseDo modelVo = new TOlkDatabaseDo();
            List<TOlkDatabaseDo> list = databaseService.findBeanList(modelVo);
            resMap.setSingleOk(list, "获取数据目录选择列表成功");
        } catch (Exception ex) {
            resMap.setErr("获取数据目录选择列表失败");
            logger.error("获取数据目录选择列表失败:", ex);
        }
        return resMap.getResultMap();
    }
     */

    /*@ApiOperation(value = "获取系统数据目录列表", notes = "获取系统数据目录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "unused", value = "未使用标志", dataType = "String", required = false, paramType = "query")
    })
    @RequestMapping(value = "/listdbsouce", method = {RequestMethod.GET})
    @ResponseBody
    public Object listDbSource(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            final HttpRequestUtil httpRequestUtil = HttpRequestUtil.parseHttpRequest(request);
            boolean unused = "1".equalsIgnoreCase(httpRequestUtil.getNvlPara("unused")) ||
                    "true".equalsIgnoreCase(httpRequestUtil.getNvlPara("unused"));
            List<TOlkDatabaseDo> list = new ArrayList<>();
            try( HetuJdbcOperate dbop = this.hetuJdbcOperate ) {

                String sql = "select table_cat as name  from system.jdbc.catalogs where table_cat!='system' ";
                final List<Map<String, Object>> list1 = dbop.selectData(sql);

                List<TOlkDatabaseDo> dbAll = databaseService.findAll();

                int norder = dbAll.size() * 10 + 10;

                for (Map<String, Object> map1 : list1) {
                    TOlkDatabaseDo db = new TOlkDatabaseDo();
                    db.setDbName(map1.get("name").toString());
                    boolean bfound = false;
                    if (unused) {
                        for (TOlkDatabaseDo tOlkDatabaseDo : dbAll) {
                            if (tOlkDatabaseDo.getDbName().equals(db.getDbName())) {
                                //list.add(tOlkDatabaseDo);
                                bfound = true;
                                break;
                            }
                        }
                    }
                    if (!bfound) {
                        db.setNorder(norder);
                        norder += 10;
                        //db.setId(UUID.randomUUID().toString().replaceAll("-",""));
                        list.add(db);
                    }
                }
            }

            resMap.setSingleOk(list, "获取系统数据目录列表");
        } catch (Exception ex) {
            resMap.setErr("获取系统数据目录列表失败");
            logger.error("获取系统数据目录列表失败:", ex);
        }
        return resMap.getResultMap();
    }*/

    private List<Object> catalogTypeSubTreeNode( String dcId, String ptypeId, final List<TOlkCatalogTypeDo> typeList, final List<TOlkDatabaseDo> dbList, final List<TOlkSchemaDo> schemaList, final List<TOlkObjectDo> tabList ) {

        List<Object> list = new ArrayList<>();
        int norder = 10;
        if ( StringUtils.isBlank( ptypeId ) ) {
            List<Object> subList2 = catalogSchemTreeNode( dcId, "", dbList, schemaList, tabList );
            list.addAll( subList2 );
        }
        List<TOlkCatalogTypeDo> tmpList = typeList.stream().filter( x -> ComUtil.trsEmpty( dcId ).equals( ComUtil.trsEmpty( x.getDcId() ) ) &&
                ComUtil.trsEmpty( ptypeId ).equals( ComUtil.trsEmpty( x.getPid() ) ) ).collect( Collectors.toList() );
        for ( TOlkCatalogTypeDo ctDo : tmpList ) {
//            if (ComUtil.trsEmpty( dcId ).equals( ComUtil.trsEmpty( ctDo.getDcId() ) ) &&
//                    ComUtil.trsEmpty( ptypeId ).equals( ComUtil.trsEmpty( ctDo.getPid() ) ) ) {
            HashMap<String, Object> node1 = new HashMap<>();
            node1.put( "type", "catalog" );
            node1.put( "dataType", "catalog" );
            node1.put( "id", "catalogtype" + connectChar + dcId + connectChar + ctDo.getId() );
            node1.put( "relId", ctDo.getId() );
            node1.put( "name", ComUtil.trsEmpty( ctDo.getTypeName() ) );
            node1.put( "tips", ComUtil.trsEmpty( ctDo.getTypeName() ) );
            //node.setDbName(temp.getDcName());
            //node.setDbId(temp.getId());
            node1.put( "hasLeaf", true );
            node1.put( "norder", norder++ );
            node1.put( "dcId", dcId );
            node1.put( "catalogType",ctDo.getId() );
            List<Object> subList1 = catalogSchemTreeNode( dcId, ctDo.getId(), dbList, schemaList, tabList );
            List<Object> subList2 = catalogTypeSubTreeNode( dcId, ctDo.getId(), typeList, dbList, schemaList, tabList );
            subList1.addAll( subList2 );
            node1.put( "children", subList1 );
            list.add( node1 );
//            }
        }
        return list;
    }

    private List<Object> catalogSchemTreeNode( String dcId, String ptypeId, final List<TOlkDatabaseDo> dbList, final List<TOlkSchemaDo> schemaList, final List<TOlkObjectDo> tabList ) {

        List<Object> list = new ArrayList<>();
        int norder = 10;
        List<TOlkDatabaseDo> tmpList = dbList.stream().filter( x -> ComUtil.trsEmpty( dcId ).equals( ComUtil.trsEmpty( x.getDcId() ) )
                && ComUtil.trsEmpty( ptypeId ).equals( ComUtil.trsEmpty( x.getCatalogType() ) ) ).collect( Collectors.toList() );
        for ( TOlkDatabaseDo db : tmpList ) {
            HashMap<String, Object> node1 = new HashMap<>();
            node1.put( "dataType", "db" );
            node1.put( "type", "schema" );
            node1.put( "dbsourceId", db.getDbsourceId() );
            node1.put( "dbType", ComUtil.trsEmpty( db.getDbType() ).toUpperCase() );
            node1.put( "id", "catalog" + connectChar + db.getId() );
            node1.put( "relId", db.getId() );
            node1.put( "name", db.getDcDbName() );
            node1.put( "tips", StringUtils.isNotBlank( db.getDbChnName() ) ? db.getDbChnName() : db.getDcDbName() );
            node1.put( "dbName", db.getDbName() );
            node1.put( "dbId", db.getId() );
            node1.put( "dcId", dcId );
            node1.put( "catalogType",ptypeId );
            node1.put( "hasLeaf", true );
            node1.put( "norder", norder++ );
            int norder2 = 10;
            List<Object> scSubList = new ArrayList<>();
            for ( TOlkSchemaDo schemaDo : schemaList ) {
                if ( db.getId().equals( schemaDo.getDbId() ) ) {
                    HashMap<String, Object> node2 = new HashMap<>();
                    node2.put( "dataType", "schema" );
                    node2.put( "type", "table" );
                    node2.put( "id", "schema" + connectChar + schemaDo.getId() );
                    node2.put( "relId", schemaDo.getId() );
                    node2.put( "name", schemaDo.getSchemaName() );
                    node2.put( "tips", StringUtils.isNotBlank( schemaDo.getSchemaChnName() ) ? schemaDo.getSchemaChnName() : schemaDo.getSchemaName() );
                    node2.put( "schemaName", schemaDo.getSchemaName() );
                    node2.put( "schemaId", schemaDo.getId() );
                    node2.put( "dbName", db.getDbName() );
                    node2.put( "dbId", db.getId() );
                    node2.put( "dcId", dcId );
                    node2.put( "hasLeaf", true );
                    node2.put( "norder", norder2++ );

                    List<Object> ttSubList = new ArrayList<>();
                    for ( TOlkObjectDo objectDo : tabList ) {
                        if ( schemaDo.getId().equals( objectDo.getSchemaId() ) ) {
                            HashMap<String, Object> node3 = new HashMap<>();
                            node3.put( "dataType", "table" );
                            node3.put( "type", "field" );
                            node3.put( "id", "table" + connectChar + objectDo.getId() );
                            node3.put( "relId", objectDo.getId() );
                            node3.put( "name", objectDo.getObjectName() );
                            node3.put( "tips", StringUtils.isNotBlank( objectDo.getObjChnName() ) ? objectDo.getObjChnName() : objectDo.getObjectName() );
                            node3.put( "schemaName", objectDo.getObjectName() );
                            node3.put( "schemaId", schemaDo.getId() );
                            node3.put( "dbName", db.getDbName() );
                            node3.put( "dbId", db.getId() );
                            node3.put( "dcId", dcId );
                            node3.put( "hasLeaf", false );
                            node3.put( "norder", norder2++ );

                            ttSubList.add( node3 );
                        }
                    }
                    //if ( ttSubList.size() > 0 ) {
                    node2.put( "children", ttSubList );
                    //}
                    scSubList.add( node2 );
                }
            }
            //if ( scSubList.size() > 0 ) {
            node1.put( "children", scSubList );
            //}
            list.add( node1 );
//            }
        }
        return list;
    }

    @ApiOperation(value = "节点元数据树", notes = "节点元数据树")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dbsourceId", value = "数据源类型", dataType = "String", required = false, paramType = "query"),
            //@ApiImplicitParam(name = "id", value = "上级id", dataType = "String", required = false, paramType = "query")
    })
    @RequestMapping(value = "/dcmetatree", method = {RequestMethod.GET})
    @ResponseBody
    public Object dcMetaTree( HttpServletRequest request ) {
        UserDo user = LoginUtil.getUser( request );

        HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
        String type = hru.getNvlPara( "type" );
        if ( type == null || "".equals( type ) ) {
            type = "root";
        }
        String dbsourceId = hru.getNvlPara( "dbsourceId" );

        ResponeMap resMap = this.genResponeMap();
        try {
//            String connectChar = "^";
//            String splitChar = "\\^";

            //String sql = "";
            List<Object> dataList = new ArrayList<>();

            FNodePartyDo fNodePartyDo = nodePartyService.findFirst();

            TOlkCatalogTypeDo typeTmp = new TOlkCatalogTypeDo();
            typeTmp.setUserAccount( user.getUserName() );

            //typeTmp.setPid( "#NULL#" );
            final List<TOlkCatalogTypeDo> typeList = catalogTypeService.findBeanList( typeTmp );

            TOlkDatabaseDo dbTmp = new TOlkDatabaseDo();
            dbTmp.setDbsourceId( dbsourceId );
            //dbTmp.setDcId( user.getUserName() );
            dbTmp.setUserId( user.getUserId() );
            final List<TOlkDatabaseDo> userDbList = databaseService.findBeanList( dbTmp );

            //List<String> dcId = userDbList.stream().map( x -> x.getDcId() ).filter( x -> StringUtils.isNotBlank( x ) ).distinct().collect( Collectors.toList() );

            List<TOlkDcServerDo> dcList = new ArrayList<>();
            List<TOlkSchemaDo> schemaList = new ArrayList<>();
            List<TOlkObjectDo> tabList = new ArrayList<>();
            //if(dcId.size()>0 ) {
            Example exp = new Example( TOlkDcServerDo.class );
            Example.Criteria criteria = exp.createCriteria();
            //criteria.andIn( "id", dbId );
            exp.orderBy( "dcCode" );
            dcList = dcService.findByExample( exp );

            exp = new Example( TOlkSchemaDo.class );
            criteria = exp.createCriteria();
            criteria.andEqualTo( "userId", user.getUserId() );
            exp.orderBy( "schemaName" );
            schemaList = schemaService.findByExample( exp );

            exp = new Example( TOlkObjectDo.class );
            criteria = exp.createCriteria();
            criteria.andEqualTo( "userId", user.getUserId() );
            exp.orderBy( "objectName" );
            tabList = objectService.findByExample( exp );
            //}

            HashMap<String, Object> node = new HashMap<>();
            //"root", "root", dcDo.getId(), dcDo.getDcName(), true, 1);
            node.put( "type", "root" );
            node.put( "id", "root" );
            node.put( "relId", "root" );
            node.put( "value", "root" );
            node.put( "name", fNodePartyDo.getName() );
            node.put( "title", fNodePartyDo.getName() );
            node.put( "itemId", null );
            node.put( "pid", null );
            node.put( "dcId","" );
            node.put( "catalogType","" );
            node.put( "dataType", "nodeParty" );
            //node.put("tips",String.format("%s(%s)", dcDo.getDcName(), dcDo.getDcCode()));
            List<Object> s1List = new ArrayList<>();
            for ( TOlkDcServerDo dcDo : dcList ) {
                HashMap<String, Object> dcnode = new HashMap<>();
                //"root", "root", dcDo.getId(), dcDo.getDcName(), true, 1);
                dcnode.put( "type", "dc" );
                dcnode.put( "id", dcDo.getId() );
                dcnode.put( "relId", dcDo.getId() );
                dcnode.put( "value", dcDo.getId() );
                dcnode.put( "name", dcDo.getDcName() );
                dcnode.put( "title", dcDo.getDcName() );
                dcnode.put( "itemId", null );
                dcnode.put( "pid", null );
                dcnode.put( "dcId", dcDo.getId() );
                dcnode.put( "catalogType","" );
                dcnode.put( "dataType", "dcServer" );

                List<Object> subList = catalogTypeSubTreeNode( dcDo.getId(), "", typeList, userDbList, schemaList, tabList );
                dcnode.put( "children", subList );
                s1List.add( dcnode );
            }

            s1List.addAll( catalogTypeSubTreeNode( "", "", typeList, userDbList, schemaList, tabList ) );
            //if ( s1List.size() > 0 ) {
            node.put( "children", s1List );
            //}
            dataList.add( node );
            resMap.setSingleOk( dataList, "获取树结构成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取树结构失败" );
            resMap.setDebugeInfo( ComUtil.getErrorInfoFromException( ex ) );
            logger.error( "获取树结构异常:", ex );
        }
        return resMap.getResultMap();
    }

    /*private List<Object> makeCatalogTree( List<TOlkCatalogTypeDo> list, String pid, String connectChar,
                                          List<TOlkDatabaseDo> dbList, List<TOlkSchemaDo> schemaList, List<TOlkObjectDo> objList ) {
        List<Object> retList = new ArrayList<>();
        pid = ComUtil.trsEmpty( pid );
        for ( TOlkCatalogTypeDo temp : list ) {
            if ( pid.equals( ComUtil.trsEmpty( temp.getPid() ) ) ) {
                HashMap<String, Object> node = new HashMap<String, Object>();
                node.put( "type", "item" );
                node.put( "Id", "item" + connectChar + temp.getId() );
                node.put( "relId", temp.getId() );
                node.put( "value", temp.getId() );
                node.put( "name", temp.getTypeName() );
                node.put( "title", temp.getTypeName() );
                node.put( "itemId", temp.getId() );
                node.put( "pid", temp.getPid() );

                //node.setNorder(norder++);
                List<Object> subList = makeCatalogTree( list, temp.getId(), connectChar, dbList, schemaList, objList );
                if ( subList.size() > 0 ) {
                    node.put( "hasLeaf", true );
                    node.put( "children", subList );
                }
                else {
                    node.put( "hasLeaf", false );
                }
                retList.add( node );
            }
        }
        for ( TOlkDatabaseDo dbDo : dbList ) {
            if ( pid.equals( ComUtil.trsEmpty( dbDo.getCatalogType() ) ) ) {
                HashMap<String, Object> node = new HashMap<String, Object>();
                node.put( "type", "dbname" );
                node.put( "dbsourceId", dbDo.getDbsourceId() );
                node.put( "dbType", ComUtil.trsEmpty( dbDo.getDbType() ).toUpperCase() );
                node.put( "Id", "db" + connectChar + dbDo.getId() );
                node.put( "relId", dbDo.getId() );
                node.put( "value", dbDo.getId() );
                node.put( "name", ComUtil.trsEmpty( dbDo.getDbName(), dbDo.getDcDbName() ) );
                node.put( "title", dbDo.getDbChnName() );
                //node.put("name",ComUtil.trsEmpty(dbDo.getDbChnName())+"(" + dbDo.getDbName()+")");
                //node.put("title",ComUtil.trsEmpty(dbDo.getDbChnName())+"(" + dbDo.getDbName()+")");
                node.put( "dbId", dbDo.getId() );
                node.put( "typeId", pid );

                //node.setNorder(norder++);
                final String pid1 = pid;
                List<Object> subList = schemaList.stream().filter( x -> dbDo.getId().equals( x.getDbId() ) ).map(
                        x -> {
                            HashMap<String, Object> subnode = new HashMap<String, Object>();
                            subnode.put( "type", "dbschema" );
                            subnode.put( "Id", "schema" + connectChar + x.getId() );
                            subnode.put( "relId", x.getId() );
                            subnode.put( "value", x.getId() );
                            subnode.put( "name", x.getSchemaName() );
                            subnode.put( "title", x.getSchemaChnName() );
                            subnode.put( "dbId", dbDo.getId() );
                            subnode.put( "typeId", pid1 );
                            {
                                subnode.put( "hasLeaf", false );
                            }
                            return subnode;
                        }
                ).collect( Collectors.toList() );
                if ( subList.size() > 0 ) {
                    node.put( "children", subList );
                }
                retList.add( node );
            }
        }

        return retList;
    }*/

    @ApiOperation(value = "刷新模式或表信息", notes = "刷新模式或表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dbId", value = "目录id", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "schemaId", value = "模式id", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "objectId", value = "对象id", dataType = "String", required = false, paramType = "query")
    })
    @RequestMapping(value = "/refreshschematable", method = {RequestMethod.POST})
    @ResponseBody
    public Object refreshSchemaTable( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser( request );
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            String dbId = hru.getNvlPara( "dbId" );
            String schemaId = hru.getNvlPara( "schemaId" );
            String objectId = hru.getNvlPara( "objectId" );
            logger.debug( "{}", hru.getAllParaData() );

            TOlkDatabaseDo olkDb = null;
            TOlkSchemaDo olkSchema = null;
            TOlkObjectDo olkObject = null;

            if ( StringUtils.isNotBlank( objectId ) ) {

                olkObject = objectService.findById( objectId );
                if ( olkObject == null ) {
                    return resMap.setErr( "指定对象不存在" ).getResultMap();
                }
                olkDb = databaseService.findById( olkObject.getDbId() );
                olkSchema = schemaService.findById( olkObject.getSchemaId() );
                logger.debug( "从{}对象{}.{}.{}刷新", olkDb.getDcCode(), olkDb.getDcDbName(), olkSchema.getSchemaName(), olkObject.getObjectName() );
            }
            else if ( StringUtils.isNotBlank( schemaId ) ) {
                olkSchema = schemaService.findById( schemaId );
                if ( olkSchema == null ) {
                    return resMap.setErr( "指定模式不存在" ).getResultMap();
                }
                olkDb = databaseService.findById( olkSchema.getDbId() );
                logger.debug( "从{}模式{}.{}刷新", olkDb.getDcCode(), olkDb.getDcDbName(), olkSchema.getSchemaName() );
            }
            else if ( StringUtils.isNotBlank( dbId ) ) {
                olkDb = databaseService.findById( dbId );
                if ( olkDb == null ) {
                    return resMap.setErr( "指定数据目录不存在" ).getResultMap();
                }
                logger.debug( "从{}目录{}刷新", olkDb.getDcCode(), olkDb.getDcDbName() );
            }
            else {
                return resMap.setErr( "数据目录、模式id与对象id不能同时为空" ).getResultMap();
            }

            String redKey = olkRef + olkDb.getId();
            boolean bset = redisTemplate.opsForValue().setIfAbsent( redKey, "1", 3, TimeUnit.MINUTES );
            if ( !bset ) {
                return resMap.setErr( olkDb.getDcDbName() + " 数据正在处理" ).getResultMap();
            }
            final TOlkDatabaseDo dbInfo = olkDb;
            final TOlkSchemaDo schemaInfo = olkSchema;
            final TOlkObjectDo objectInfo = olkObject;
            TOlkDcServerDo dcDo = dcService.findById( olkDb.getDcId() );
            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            new Thread( new Runnable() {
                @Override
                public void run() {
                    loadOlkCatalogMeta( dbInfo, dcDo, schemaInfo, objectInfo, nodePartyDo, redKey, user, request );
                }
            }, "处理元数据" ).start();

            resMap.setOk( "数据开始处理" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "数据处理失败" );
            logger.error( "数据处理异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "对用户授权", notes = "对用户授权")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stype", value = "类型 database,schema,table ", dataType = "String",example = "database,schema,table", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dataId", value = "数据id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户Id", dataType = "String", required = true, paramType = "query")

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




                Example exp = new Example( TOlkDatabaseDo.class );
                Example.Criteria criteria = exp.createCriteria();
                criteria.andIn( "id", dataIdList ).andEqualTo( "enable", 1 );
                List<TOlkDatabaseDo> objList = databaseService.findByExample( exp );
                if ( objList.size() != dataIdList.size() ) {
                    return resMap.setErr( "部分对象已变化或不存在" ).getResultMap();
                }

                List<FDataApproveDo> list = new ArrayList<>();
                for ( TOlkDatabaseDo objDo : objList ) {
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
                        da.setDataCatalog( "database" );
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


    @ApiOperation(value = "表有权限用户", notes = "表有权限用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "表id", dataType = "String", required = true, paramType = "query"),

    })
    @RequestMapping(value = "/tableprivuser", method = {RequestMethod.POST})
    @ResponseBody
    public Object tablePrivUser( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            String id = hru.getNvlPara( "id" );

            UserDo user = LoginUtil.getUser( request );

            TOlkObjectDo objectDo = objectService.findById( id );
            if( objectDo == null){
                return resMap.setErr( "表不存在" ).getResultMap();
            }

            ObjectResp<OlkObjectWithFieldsVo> retVal = apiOlkDbService.olkTableWithSubInfo( objectDo.getId(), user.getTokenId() );
            if ( !retVal.isSuccess() ) {
                return resMap.setErr( "获取失败," + retVal.getMsg() ).getResultMap();
            }
            List<FDataApproveVo> approveList = retVal.getData().getApproveList();

            resMap.setSingleOk(approveList, "获取成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取失败" );
            logger.error( "获取失败:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "用户有权限表", notes = "用户有权限表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dbId", value = "目录id", dataType = "", required = false, paramType = "query"),
            @ApiImplicitParam(name = "schemaId", value = "库id", dataType = "", required = false, paramType = "query"),
            @ApiImplicitParam(name = "objectId", value = "表id", dataType = "", required = false, paramType = "query"),

    })
    @RequestMapping(value = "/userprivtable", method = {RequestMethod.POST})
    @ResponseBody
    public Object userPrivTable( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            OlkObjectWithFieldsVo obj = new OlkObjectWithFieldsVo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            obj.setGrantUserId(   hru.getNvlPara( "userId" )  );
            obj.setDbId( ComUtil.trsEmpty( hru.getNvlPara( "dbId" ),null) );
            obj.setSchemaId( ComUtil.trsEmpty( hru.getNvlPara( "schemaId" ),null) );
            obj.setId( ComUtil.trsEmpty( hru.getNvlPara( "objectId" ),null) );
            UserDo user = LoginUtil.getUser( request );
            if( StringUtils.isBlank( obj.getGrantUserId() )){
                return resMap.setErr( "用户id不能为空" ).getResultMap();
            }

            ListResp<OlkObjectWithFieldsVo> retVal = apiOlkDbService.userOlkTableWithInfo( obj, user.getTokenId() );
            if ( !retVal.isSuccess() ) {
                return resMap.setErr( "获取失败," + retVal.getMsg() ).getResultMap();
            }
            List<OlkObjectWithFieldsVo> dataList = retVal.getData();

            resMap.setSingleOk(dataList, "获取成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取失败" );
            logger.error( "获取失败:", ex );
        }
        return resMap.getResultMap();
    }

    /*@ApiOperation(value = "上报表", notes = "上报表")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "modelVo", value = "数据源", dataType = "TDatasourceDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/synobject", method = {RequestMethod.POST})
    @ResponseBody
    public Object synObject(String dbId,Integer flag,String nodeId, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
            if( StringUtils.isBlank( dbId ) ){
                return resMap.setErr( "目录id不能为空" ).getResultMap();
            }
            List<String> split = Arrays.asList( dbId.split( "(,|\\s)+" ) );

            Example exp = new Example( TOlkObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "dbId", split);

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

                        Map<String, Object> retMap = a.synDatabase( dbDo, ud.getTokenId() );
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
    }

     */

}
