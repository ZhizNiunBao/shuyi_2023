package cn.bywin.business.controller.olk;

import cn.bywin.business.bean.analysis.olk.OlkBaseComponenT;
import cn.bywin.business.bean.analysis.olk.OlkComponentEnum;
import cn.bywin.business.bean.analysis.olk.template.OlkDataSourceOutPutComponent;
import cn.bywin.business.bean.analysis.olk.template.OlkTableComponent;
import cn.bywin.business.bean.bydb.TaskStatus;
import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.olk.TOlkDcServerDo;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.view.StatusCount;
import cn.bywin.business.bean.view.bydb.OlkModelVo;
import cn.bywin.business.bean.view.federal.FDataApproveVo;
import cn.bywin.business.bean.view.olk.OlkModelElementVo;
import cn.bywin.business.bean.view.olk.VOlkObjectVo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.except.MessageException;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.hetu.DataHubJdbcOperateConfigurate;
import cn.bywin.business.hetu.HetuJdbcOperate;
import cn.bywin.business.hetu.HetuJdbcOperateComponent;
import cn.bywin.business.modeltask.ModelTaskFlinkApiService;
import cn.bywin.business.modeltask.OlkTaskFlinkApiService;
import cn.bywin.business.service.bydb.BydbDatabaseService;
import cn.bywin.business.service.bydb.BydbDatasetService;
import cn.bywin.business.service.bydb.BydbObjectService;
import cn.bywin.business.service.federal.DataSourceService;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.service.olk.OlkDcServerService;
import cn.bywin.business.service.olk.OlkModelComponentService;
import cn.bywin.business.service.olk.OlkModelElementRelService;
import cn.bywin.business.service.olk.OlkModelElementService;
import cn.bywin.business.service.olk.OlkModelFieldService;
import cn.bywin.business.service.olk.OlkModelObjectService;
import cn.bywin.business.service.olk.OlkModelService;
import cn.bywin.business.service.olk.OlkObjectService;
import cn.bywin.business.trumodel.ApiOlkDbService;
import cn.bywin.business.trumodel.ApiTruModelService;
import cn.bywin.business.util.DbTypeToFlinkType;
import cn.bywin.business.util.HttpOperaterUtil;
import cn.bywin.business.util.JdbcTypeToJavaTypeUtil;
import cn.bywin.business.util.JdbcTypeTransformUtil;
import cn.bywin.business.util.analysis.AnalysisRunService;
import cn.bywin.cache.SysParamSetOp;
import cn.bywin.common.resp.ListResp;
import cn.bywin.config.OlkModelOutDbSet;
import cn.jdbc.IJdbcOp;
import cn.jdbc.JdbcColumnInfo;
import cn.jdbc.JdbcOpBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

@RestController
@RequestMapping({"/analyse/olkmodeltask"})
@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        maxAge = 3600)
@Api(tags = "olk建模任务-olkmodeltask")
public class OlkModelTaskController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Autowired
    private OlkModelService truModelService;
    @Autowired
    private OlkModelObjectService truModelObjectService;
    @Autowired
    private OlkModelComponentService truModelComponentService;
    @Autowired
    private OlkModelFieldService truModelFieldService;
    @Autowired
    private BydbObjectService bydbObjectService;
    @Autowired
    private OlkModelElementService truModelElementService;
    //    @Autowired
//    private BydbModelElementJobService truModelElementJobService;
    @Autowired
    private OlkModelElementRelService truModelElementRelService;
    @Autowired
    private BydbDatabaseService bydbDatabaseService;
    @Autowired
    private BydbDatasetService datasetService;

    @Autowired
    private DataSourceService dbSourceService;

    @Autowired
    private HetuJdbcOperate hetuJdbcOperate;

    @Autowired
    private HetuJdbcOperateComponent hetuJdbcOperateComponent;

    @Autowired
    private DataHubJdbcOperateConfigurate hutuConfig;

    @Autowired
    private AnalysisRunService analysisRunService;

    @Autowired
    private NodePartyService nodePartyService;

    @Autowired
    private OlkObjectService objectService;

    @Autowired
    private OlkDcServerService dcService;

    @Autowired
    private ApiTruModelService apiTruModelService;

        @Autowired
    private ApiOlkDbService apiOlkDbService;

//    @Autowired
//    private MenuUtil menuUtil;

//    @Autowired
//    private ISysParamSetOp setOp;

//    @Autowired
//    @Qualifier("dataHubScheduleProperties")
//    private Properties dataHubProperties;

//    @Autowired
//    private SystemParamHolder systemParamHolder;


    @Autowired
    OlkModelOutDbSet outDbSet;

    @Autowired
    ModelTaskFlinkApiService modelTaskFlinkApiService;

    @Autowired
    OlkTaskFlinkApiService olkTaskFlinkApiService;



    @ApiOperation(value = "处理联邦分析", notes = "处理联邦分析")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "联邦分析id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/dealmodeldata", method = {RequestMethod.GET})
    @ResponseBody
    public Object dealModelData( String id, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            resMap.put( "objPrvList" ,new ArrayList<>());
            UserDo user = LoginUtil.getUser( request );
            if ( user == null || StringUtils.isBlank( user.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
            if ( StringUtils.isBlank( id ) ) {
                return resMap.setErr( "模型id不能为空" ).getResultMap();
            }

            TOlkModelDo info = truModelService.findById( id );
            if ( info == null ) {
                return resMap.setErr( "模型不存在" ).getResultMap();
            }

            FNodePartyDo nodePartyDo = nodePartyService.findFirst();

            List<TOlkModelObjectDo> modObjList = truModelObjectService.selectByModelId( info.getId() );
            HashMap<String,TOlkModelObjectDo> modObjMap = new HashMap<>();
            for ( TOlkModelObjectDo tmp : modObjList ) {
                modObjMap.put( tmp.getRealObjId(),tmp );
            }
//            List<String> modObjIdList = modObjList.stream().map( x -> x.getObjectId() ).distinct().collect( Collectors.toList() );
//            Example exp = new Example( TBydbObjectDo.class);
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andIn("id", modObjIdList).andEqualTo( "userId", );
//            int cnt = databaseService.findCountByExample(exp);
//            if( cnt >0 ){
//                return resMap.setErr("目录分组已被使用不能删除").getResultMap();
//            }

            List<TOlkModelElementDo> eleList = truModelElementService.selectByModelId( info.getId() );
            List<String> tabIdList = eleList.stream().filter( x -> "table".equals( x.getIcon() ) && StringUtils.isNotBlank( x.getTcId() ) ).map( x -> x.getTcId() ).distinct().collect( Collectors.toList() );

            if( tabIdList.size()>0 ) {
                Example exp = new Example( TOlkObjectDo.class );
                Example.Criteria criteria = exp.createCriteria();
                criteria.andIn( "id", tabIdList ).andEqualTo( "userId", user.getUserId() );
                List<TOlkObjectDo> objList = objectService.findByExample( exp );
                for ( TOlkObjectDo objectDo : objList ) {
                    tabIdList.remove( objectDo.getId() );
                }
            }

            if( tabIdList.size()>0 ) {
                List<FDataApproveDo> daList = new ArrayList<>();
                for ( String objId : tabIdList ) {
                    FDataApproveDo da = new FDataApproveDo();
                    da.setCreatorId( user.getUserId() );
                    da.setDataId( objId );
                    daList.add( da );
                }
                VOlkObjectVo qryObj = new VOlkObjectVo();
                qryObj.setOwnerUserId( user.getUserId() );
                qryObj.setOwnerNodeId( nodePartyDo.getId() );
                qryObj.setId( String.join( "," , tabIdList) );
                //qryObj.genPage();
                qryObj.setPageSize( null );
                //qryObj.setIdList( tabIdList );
                //qryObj.setPageSize( 500 );
//                HashMap<String,Object> qryObjMap = new HashMap<>();
//                MyBeanUtils.copyBeanNotNull2Map( qryObj,qryObjMap );

                ListResp<VOlkObjectVo> objVal = apiOlkDbService.pmOlkTableList( qryObj, user.getTokenId() );
                if( !objVal.isSuccess() ){
                    logger.error( "{}",objVal.getMsg());
                    return resMap.setErr( "验证权限失败" ).getResultMap();
                }
                ListResp<FDataApproveVo> daVal = apiTruModelService.queryUserApprove( daList, user.getTokenId() );
                if ( !daVal.isSuccess() ) {
                    logger.error( "{}",daVal.getMsg());
                    return resMap.setErr( "验证权限失败" ).getResultMap();
                }
                //boolean bpriv = true;
                String[] objFields = "userObjPriv,dbChnName,dbId,dbName,delFlag,enable,id,nodePartyId,nodePartyName,objChnName,objFullName,objectName,schemaChnName,schemaId,schemaName,shareFlag,userAccount,userId,userName,enable".split( "," );
                String[] approveFields = "approval,approve,content,dataId".split( "," );

                String msg  = null;
                List<Object> objApproveList = new ArrayList<>();
                for ( String s : tabIdList ) {
                    HashMap<String,Object> objMap = new HashMap<>();
                    VOlkObjectVo datum = new VOlkObjectVo();

                    TOlkModelObjectDo modTmp = modObjMap.get( s );
                    if( modTmp != null ){
                        datum.setObjChnName( modTmp.getObjChnName() );
                        datum.setObjectName( modTmp.getObjectName() );
                    }

                    datum.setId( s );
                    for ( VOlkObjectVo tmp : objVal.getData() ) {
                        if( s.equals( tmp.getId() )){
                            datum  =  tmp;
                            break;
                        }
                    }
                    MyBeanUtils.copyBean2Map( objMap, datum ,objFields);
                    FDataApproveVo  da = new FDataApproveVo();
                    for ( FDataApproveVo tmp : daVal.getData() ) {
                        if( s.equals( tmp.getDataId() )){
                            da  =  tmp;
                            break;
                        }
                    }
                    MyBeanUtils.copyBean2Map( objMap, da,approveFields );

                    objMap.put( "userPrivGrant", da.getApprove() == null?10:da.getApprove() );

                    int approve = da.getApprove()==null?0: da.getApprove();

                    if( datum.getEnable() == null || datum.getEnable() !=1){
                        msg ="部分资源不存在或未授权";
                        objMap.put( "approve",9 );
                        objMap.put( "userPrivGrant", 9 );
                    }
                    else {
                        if ( approve == 1 ) {
                            continue;
                        }
                        if ( !info.getCreatorId().equals( datum.getUserId() ) && approve != 1 ) {
                            if ( msg == null ) {
                                msg = "部分资源未授权";
                            }
                        }
                        if ( datum.getUserObjPriv() == null || datum.getUserObjPriv() != 1 ) {
                            msg = "部分资源不存在或未授权";
                            objMap.put( "approve", 9 );
                            objMap.put( "userPrivGrant", 9 );
                        }
                    }
                    objApproveList.add( objMap );
                }
                resMap.put( "objPrvList" ,objApproveList);
                if( msg!= null ){
                    return resMap.setErr( msg ).getResultMap();
                }
//                StringBuilder sb = new StringBuilder();
//                for ( FDataApproveDo da : retVal.getData() ) {
//                    tabIdList.remove( da.getDataId() );
//                    if( da.getApprove() == null || da.getApprove()!=1 ){
//                        TOlkModelObjectDo table = modObjMap.get( da.getDataId() );
//                        sb.append( String.format( "%s(%s)\r\n" ,table.getObjChnName(),table.getObjectName() ) );
//                        //return resMap.setErr( String.format( "%s(%s)没有权限执行" , da.getCreatorName(),da.getCreatorAccount() ) ).getResultMap();
//                    }
//                }
//                for ( String s : tabIdList ) {
//                    TOlkModelObjectDo table = modObjMap.get( s );
//                    sb.append( String.format( "%s(%s)\r\n" ,table.getObjChnName(),table.getObjectName() ) );
//                }
//                if( sb.length()>0){
//                    sb.append( "没有权限" );
//                    resMap.put( "objPrvList" ,retVal.getData());
//                    return resMap.setErr( sb.toString() ).getResultMap();
//                }
            }

//            List<TOlkModelObjectDo> modObjList = truModelObjectService.selectByModelId( info.getId() );
//            List<String> modObjIdList = modObjList.stream().map( x -> x.getObjectId() ).distinct().collect( Collectors.toList() );
//            Example exp = new Example( TBydbObjectDo.class);
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andIn("id", modObjIdList).andEqualTo( "userId", );
//            int cnt = databaseService.findCountByExample(exp);
//            if( cnt >0 ){
//                return resMap.setErr("目录分组已被使用不能删除").getResultMap();
//            }


//            String bydbDealFlag =  systemParamHolder.getBydbDealFlag();
//            if ("0".equals(bydbDealFlag)) {
//                return resMap.setErr("系统禁用数据处理").getResultMap();
//            }

            if ( StringUtils.isBlank( info.getRunSql() ) ) {
                return resMap.setErr( "模型配置未完成请重新配置保存后再执行" ).getResultMap();
            }
            if ( info.getCacheFlag() != null && info.getCacheFlag() == 2 ) {
                return resMap.setErr( "处理任务正在处理" ).getResultMap();
            }

            TOlkModelDo old = new TOlkModelDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, old );

            info.setCacheFlag( 1 );
            info.setRunType( 1 );

            String olkVdmCatalogViewName = SysParamSetOp.readValue( "olkVdmCatalogViewName","" );

//            List<TBydbDsColumnDo> colList = dsColumnService.findByDatasetId(info.getId());
//            colList = colList.stream().filter( x-> x.getEnable() != null && x.getEnable() ==1 ).collect(Collectors.toList());
//            List<TBydbDsColumnDo> groupList = colList.stream().filter(x -> "group".equals(x.getEtype())).collect(Collectors.toList());
//            if (groupList.size() > 0) {
//                colList = groupList;
//            }
            if ( "flink".equalsIgnoreCase( info.getConfig() ) || "tee".equalsIgnoreCase( info.getConfig() ) ) {

                TOlkModelElementDo meTmp = new TOlkModelElementDo();
                meTmp.setModelId( info.getId() );
                meTmp.setIcon( "t_datasource" );
                List<TOlkModelElementDo> meList = truModelElementService.find( meTmp );
                String sql = info.getRunSql();
                if ( meList.size() == 0 ) {

                    List<TOlkModelElementRelDo> relList = truModelElementRelService.selectByModelId( info.getId() );
                    List<String> nodeList = new ArrayList<>();
                    for ( TOlkModelElementDo elementDo : eleList ) {
                        nodeList.add( elementDo.getId() );
                    }
                    for ( TOlkModelElementRelDo relDo : relList ) {
                        nodeList.remove( relDo.getStartElementId() );
                    }
                    if ( nodeList.size() != 1 ) {
                        return resMap.setErr( "结果只能为一个组件" ).getResultMap();
                    }
                    TOlkModelElementDo elementDo = eleList.stream().filter( x -> x.getId().equals( nodeList.get( 0 ) ) ).collect( Collectors.toList() ).get( 0 );
                    //info.setViewName( "tmp_" + info.getId() );
                    String tableName = info.getViewName();

//                    if ( JdbcOpBuilder.dbOpenLooKeng.equalsIgnoreCase( dstype ) ) {
//                        return resMap.setErr( String.format( "%s的数据源%s不能做为输出数据源", meData.getName(), dsDo.getDsName() ) ).getResultMap();
//                    }
                    boolean btab = false;
                    try ( IJdbcOp jdbcOp = new JdbcOpBuilder()//.withCatalog( dsDo.getDsDatabase() ).withSchema( dsDo.getDsSchema() )
                            .withSet( outDbSet.getDstype(), outDbSet.getDriver(), outDbSet.getJdbcUrl(), outDbSet.getUser(), outDbSet.getPassword() ).build() ) {

                        List<TOlkModelFieldDo> fieldList = truModelFieldService.selectByElementIdAll( elementDo.getId() ).stream().filter( x -> x.getIsSelect() != null && x.getIsSelect() == 1 ).collect( Collectors.toList() );
                        StringBuilder sb = new StringBuilder();
                        sb.append( "DROP TABLE IF EXISTS " ).append( tableName ).append( ";\r\n" );
                        sb.append( "CREATE TABLE " ).append( tableName ).append( "(\r\n" );
                        int size = fieldList.size();
                        List<String> field2List = new ArrayList<>();
                        for ( TOlkModelFieldDo field : fieldList ) {
                            field2List.add("".concat(field.getFieldAlias()).concat(" ").concat(DbTypeToFlinkType.chgType(field.getFieldType())));
                            String newType = JdbcTypeTransformUtil.chgType( field.getFieldType(), outDbSet.getDstype() );
                            sb.append( field.getFieldAlias() ).append( " " ).append( newType );
                            if ( JdbcOpBuilder.dbStarRocks.equalsIgnoreCase( outDbSet.getDstype() ) || JdbcOpBuilder.dbMySql.equalsIgnoreCase( outDbSet.getDstype() ) ) {
                                sb.append( " COMMENT \"" ).append( field.getFieldExpr() ).append( "\"" );
                            }
                            size--;
                            if ( size > 0 ) {
                                sb.append( ",\r\n" );
                            }
                        }
                        sb.append( ")\r\n" );
                        if ( JdbcOpBuilder.dbStarRocks.equalsIgnoreCase( outDbSet.getDstype() ) ) {
                            sb.append( "DUPLICATE KEY(`" ).append( fieldList.get( 0 ).getFieldAlias() ).append( "`)\n" );
                        }
                        if ( JdbcOpBuilder.dbStarRocks.equalsIgnoreCase( outDbSet.getDstype() ) || JdbcOpBuilder.dbMySql.equalsIgnoreCase( outDbSet.getDstype() ) ) {
                            sb.append( "COMMENT \"" ).append( info.getName() ).append( "\" \r\n" );
                        }
                        if ( JdbcOpBuilder.dbStarRocks.equalsIgnoreCase( outDbSet.getDstype() ) ) {
//                            sb.append( "DUPLICATE KEY(`").append( fieldList.get( 0 ).getFieldAlias() ).append( "`)\n" +
//                                    "COMMENT \"").append( "" ).append( "\"\n" +
//                                    "DISTRIBUTED BY HASH(`by_env_date`) BUCKETS 32 \n" +
//                                    "PROPERTIES (\n" +
//                                    "\"replication_num\" = \"1\",\n" +
//                                    "\"colocate_with\" = \"pclab\",\n" +
//                                    "\"in_memory\" = \"false\",\n" +
//                                    "\"storage_format\" = \"DEFAULT\"\n" +
//                                    ")" );
                            sb.append( "DISTRIBUTED BY HASH(`" ).append( fieldList.get( 0 ).getFieldAlias() ).append( "`) BUCKETS 4\r\n" +
                                    "PROPERTIES (\n" +
                                    "\"replication_num\" = \"1\",\n" +
                                    //"\"colocate_with\" = \"pclab\",\n" +
                                    "\"in_memory\" = \"false\",\n" +
                                    "\"storage_format\" = \"DEFAULT\"\n" +
                                    ")" );
                        }
                        logger.info( sb.toString() );
                        jdbcOp.execute( sb.toString() );

                        sb = new StringBuilder();

                        //生成语句
                    }
                }
                for ( TOlkModelElementDo meData : meList ) {
                    String config = meData.getConfig();
                    logger.debug( "{},{}:{}", info.getName(), meData.getName(), config );
                    if ( StringUtils.isBlank( config ) ) {
                        return resMap.setErr( String.format( "%s未配置输出", meData.getName() ) ).getResultMap();
                    }
                    JsonObject jsonObject = JsonUtil.toJsonObject( config );
                    //{  "datasourceId": "814791acd5d54b578c241fe53b752f0e",  "tableName": "test11"}
                    if ( !jsonObject.has( "datasourceId" ) || !jsonObject.has( "tableName" ) ) {
                        return resMap.setErr( String.format( "%s配置不完整", meData.getName() ) ).getResultMap();
                    }
                    String tableName = jsonObject.get( OlkDataSourceOutPutComponent.paraTableName ).getAsString();
                    String datasourceId = jsonObject.get( OlkDataSourceOutPutComponent.paraDsId ).getAsString();
                    String dbschema = jsonObject.get( OlkDataSourceOutPutComponent.paraSchemaName ).getAsString();
                    String sinkType = jsonObject.get( OlkDataSourceOutPutComponent.paraSinkType ) == null || jsonObject.get( OlkDataSourceOutPutComponent.paraSinkType ).isJsonNull() ? "1" : jsonObject.get( OlkDataSourceOutPutComponent.paraSinkType ).getAsString();
                    FDatasourceDo dsDo = dbSourceService.findById( datasourceId );
                    if ( dsDo == null ) {
                        return resMap.setErr( String.format( "%s数据源不存在", meData.getName() ) ).getResultMap();
                    }
                    if ( JdbcOpBuilder.dbOpenLooKeng.equalsIgnoreCase( dsDo.getDsType() ) ) {
                        return resMap.setErr( String.format( "%s的数据源%s不能做为输出数据源", meData.getName(), dsDo.getDsName() ) ).getResultMap();
                    }
                    boolean btab = false;
                    String jdbcUrl = JdbcOpBuilder.genUrl( dsDo.getDsType(), dsDo.getDsIp(), dsDo.getDsPort(), dbschema );
                    try ( IJdbcOp jdbcOp = new JdbcOpBuilder()//.withCatalog( dsDo.getDsDatabase() ).withSchema( dsDo.getDsSchema() )
                            .withSet( dsDo.getDsType(), dsDo.getDsDriver(), jdbcUrl, dsDo.getUsername(), dsDo.getPassword() ).build() ) {

                        try {
                            jdbcOp.setRaiseException( true );
                            List<JdbcColumnInfo> colList = jdbcOp.listColumn( dsDo.getDsDatabase(), dbschema, tableName );
                            if ( colList != null && colList.size() > 0 ) {
                                btab = true;
                            }
                        }
                        catch ( Exception ee ) {
                            logger.info( "{}表{}不存在", meData.getName(), tableName );
                            return resMap.setErr( String.format( "%s的数据源%s配置失败", meData.getName(), dsDo.getDsName() ) ).getResultMap();
                        }
                        //List<String>  comentList = new ArrayList<>();
                        //comentList.addAll( Arrays.asList("",""));
                        if ( !btab || "1".equalsIgnoreCase( sinkType ) ) {
                            List<TOlkModelFieldDo> fieldList = truModelFieldService.selectByElementIdAll( meData.getId() ).stream().filter( x -> x.getIsSelect() != null && x.getIsSelect() == 1 ).collect( Collectors.toList() );
                            StringBuilder sb = new StringBuilder();
                            sb.append( "DROP TABLE IF EXISTS " ).append( tableName ).append( ";\r\n" );
                            sb.append( "CREATE TABLE " ).append( tableName ).append( "(\r\n" );
                            int size = fieldList.size();
                            for ( TOlkModelFieldDo field : fieldList ) {
                                String newType = JdbcTypeTransformUtil.chgType( field.getFieldType(), dsDo.getDsType() );
                                sb.append( field.getFieldAlias() ).append( " " ).append( newType );
                                if ( JdbcOpBuilder.dbStarRocks.equalsIgnoreCase( dsDo.getDsType() ) || JdbcOpBuilder.dbMySql.equalsIgnoreCase( dsDo.getDsType() ) ) {
                                    sb.append( " COMMENT \"" ).append( field.getFieldExpr() ).append( "\"" );
                                }
                                size--;
                                if ( size > 0 ) {
                                    sb.append( ",\r\n" );
                                }
                            }
                            sb.append( ")\r\n" );
                            if ( JdbcOpBuilder.dbStarRocks.equalsIgnoreCase( dsDo.getDsType() ) ) {
                                sb.append( "DUPLICATE KEY(`" ).append( fieldList.get( 0 ).getFieldAlias() ).append( "`)\n" );
                            }
                            if ( JdbcOpBuilder.dbStarRocks.equalsIgnoreCase( dsDo.getDsType() ) || JdbcOpBuilder.dbMySql.equalsIgnoreCase( dsDo.getDsType() ) ) {
                                sb.append( "COMMENT \"" ).append( info.getName() ).append( "\" \r\n" );
                            }
                            if ( JdbcOpBuilder.dbStarRocks.equalsIgnoreCase( dsDo.getDsType() ) ) {
//                            sb.append( "DUPLICATE KEY(`").append( fieldList.get( 0 ).getFieldAlias() ).append( "`)\n" +
//                                    "COMMENT \"").append( "" ).append( "\"\n" +
//                                    "DISTRIBUTED BY HASH(`by_env_date`) BUCKETS 32 \n" +
//                                    "PROPERTIES (\n" +
//                                    "\"replication_num\" = \"1\",\n" +
//                                    "\"colocate_with\" = \"pclab\",\n" +
//                                    "\"in_memory\" = \"false\",\n" +
//                                    "\"storage_format\" = \"DEFAULT\"\n" +
//                                    ")" );
                                sb.append( "DISTRIBUTED BY HASH(`" ).append( fieldList.get( 0 ).getFieldAlias() ).append( "`) BUCKETS 4\r\n" +
                                        "PROPERTIES (\n" +
                                        "\"replication_num\" = \"1\",\n" +
                                        //"\"colocate_with\" = \"pclab\",\n" +
                                        "\"in_memory\" = \"false\",\n" +
                                        "\"storage_format\" = \"DEFAULT\"\n" +
                                        ")" );
                            }
                            logger.info( sb.toString() );
                            try {
                                jdbcOp.execute( sb.toString() );
                            }
                            catch ( Exception ee ){
                                logger.error( "{}输出表{}创建失败,", meData.getName(), tableName,ee );
                                return resMap.setErr( String.format( "%s的输出表创建失败", meData.getName(), dsDo.getDsName() ) ).getResultMap();
                            }
                        }
                    }
                }

                if ( !flinkModelJob( info,sql, user ) ) {
                    return resMap.setErr( "提交处理任务失败" ).getResultMap();
                }
            }
            else {  //
                String hetuDriver =hutuConfig.getDriver();
                String hetuUrl = hutuConfig.getUrl();
                List<Object> paraSetList = new ArrayList<>();
                if( StringUtils.isBlank( info.getDcId() )) { //跨节点运行
                    try ( HetuJdbcOperate dbop = this.hetuJdbcOperate ) {
                        for ( TOlkModelElementDo elementDo : eleList ) {
                            String viewSql = String.format("CREATE OR REPLACE VIEW %s.tmp_%s AS %s", olkVdmCatalogViewName,elementDo.getId(), elementDo.getRunSql());
                            dbop.execute( viewSql );
                        }
                    }
                }
                else {
                    TOlkDcServerDo dcDo = dcService.findById( info.getDcId() );
                    hetuUrl = dcDo.getJdbcUrl();
                    if( dcDo.getEnable() == null || dcDo.getEnable() !=1 ){
                        return  resMap.setErr( "节点未启用" ).getResultMap();
                    }
                    //HetuInfo hetuInfo = hetuJdbcOperateComponent.genHetuInfo( dcDo );
                    try(HetuJdbcOperate dbop = hetuJdbcOperateComponent.genHetuJdbcOperate(dcDo)){
                        for ( TOlkModelElementDo elementDo : eleList ) {
                            String viewSql = String.format("CREATE OR REPLACE VIEW %s.tmp_%s AS %s", olkVdmCatalogViewName,elementDo.getId(), elementDo.getRunSql());
                            dbop.execute( viewSql );
                        }
                    }
                }

                TOlkModelElementDo meTmp = new TOlkModelElementDo();
                meTmp.setModelId( info.getId() );
                meTmp.setIcon( "t_datasource" );
                List<TOlkModelElementDo> meList = truModelElementService.find( meTmp );
                //String sql = info.getRunSql();
                if ( meList.size() == 0 ) {
                    List<TOlkModelElementRelDo> relList = truModelElementRelService.selectByModelId( info.getId() );
                    List<String> nodeList = new ArrayList<>();
                    for ( TOlkModelElementDo elementDo : eleList ) {
                        nodeList.add( elementDo.getId() );
                    }
                    for ( TOlkModelElementRelDo relDo : relList ) {
                        nodeList.remove( relDo.getStartElementId() );
                    }
                    if ( nodeList.size() != 1 ) {
                        return resMap.setErr( "结果只能为一个组件" ).getResultMap();
                    }
                    info.setCacheFlag( 3 );
                    truModelService.updateBean( info );
                    return resMap.setOk("处理完成").getResultMap();
                    //TOlkModelElementDo elementDo = eleList.stream().filter( x -> x.getId().equals( nodeList.get( 0 ) ) ).collect( Collectors.toList() ).get( 0 );
                    //String tableName = "tmp_" + elementDo.getId();

                }
                for ( TOlkModelElementDo meData : meList ) {
                    String config = meData.getConfig();
                    logger.debug( "{},{}:{}", info.getName(), meData.getName(), config );
                    if ( StringUtils.isBlank( config ) ) {
                        return resMap.setErr( String.format( "%s未配置输出", meData.getName() ) ).getResultMap();
                    }
                    JsonObject jsonObject = JsonUtil.toJsonObject( config );
                    //{  "datasourceId": "814791acd5d54b578c241fe53b752f0e",  "tableName": "test11"}
                    if ( !jsonObject.has( "datasourceId" ) || !jsonObject.has( "tableName" ) ) {
                        return resMap.setErr( String.format( "%s配置不完整", meData.getName() ) ).getResultMap();
                    }
                    String tableName = jsonObject.get( OlkDataSourceOutPutComponent.paraTableName ).getAsString();
                    String datasourceId = jsonObject.get( OlkDataSourceOutPutComponent.paraDsId ).getAsString();
                    String dbschema = jsonObject.get( OlkDataSourceOutPutComponent.paraSchemaName ).getAsString();
                    String sinkType = jsonObject.get( OlkDataSourceOutPutComponent.paraSinkType ) == null || jsonObject.get( OlkDataSourceOutPutComponent.paraSinkType ).isJsonNull() ? "1" : jsonObject.get( OlkDataSourceOutPutComponent.paraSinkType ).getAsString();
                    FDatasourceDo dsDo = dbSourceService.findById( datasourceId );
                    if ( dsDo == null ) {
                        return resMap.setErr( String.format( "%s数据源不存在", meData.getName() ) ).getResultMap();
                    }
                    if ( JdbcOpBuilder.dbOpenLooKeng.equalsIgnoreCase( dsDo.getDsType() ) ) {
                        return resMap.setErr( String.format( "%s的数据源%s不能做为输出数据源", meData.getName(), dsDo.getDsName() ) ).getResultMap();
                    }
                    List<TOlkModelFieldDo> fieldList = truModelFieldService.selectByElementIdAll( meData.getId() ).stream().filter( x -> x.getIsSelect() != null && x.getIsSelect() == 1 ).collect( Collectors.toList() );
                    boolean btab = false;
                    String jdbcUrl = JdbcOpBuilder.genUrl( dsDo.getDsType(), dsDo.getDsIp(), dsDo.getDsPort(), dbschema );
                    try ( IJdbcOp jdbcOp = new JdbcOpBuilder()//.withCatalog( dsDo.getDsDatabase() ).withSchema( dsDo.getDsSchema() )
                            .withSet( dsDo.getDsType(), dsDo.getDsDriver(), jdbcUrl, dsDo.getUsername(), dsDo.getPassword() ).build() ) {

                        try {
                            jdbcOp.setRaiseException( true );
                            List<JdbcColumnInfo> colList = jdbcOp.listColumn( dsDo.getDsDatabase(), dbschema, tableName );
                            if ( colList != null && colList.size() > 0 ) {
                                btab = true;
                            }
                        }
                        catch ( Exception ee ) {
                            logger.info( "{}表{}不存在", meData.getName(), tableName );
                            return resMap.setErr( String.format( "%s的数据源%s配置失败", meData.getName(), dsDo.getDsName() ) ).getResultMap();
                        }
                        //List<String>  comentList = new ArrayList<>();
                        //comentList.addAll( Arrays.asList("",""));
                        if ( !btab || "1".equalsIgnoreCase( sinkType ) ) {
                            StringBuilder sb = new StringBuilder();
                            sb.append( "DROP TABLE IF EXISTS " ).append( tableName ).append( ";\r\n" );
                            sb.append( "CREATE TABLE " ).append( tableName ).append( "(\r\n" );
                            int size = fieldList.size();
                            for ( TOlkModelFieldDo field : fieldList ) {
                                String newType = JdbcTypeTransformUtil.chgType( field.getFieldType(), dsDo.getDsType() );
                                sb.append( field.getFieldAlias() ).append( " " ).append( newType );
                                if ( JdbcOpBuilder.dbStarRocks.equalsIgnoreCase( dsDo.getDsType() ) || JdbcOpBuilder.dbMySql.equalsIgnoreCase( dsDo.getDsType() ) ) {
                                    sb.append( " COMMENT \"" ).append( field.getFieldExpr() ).append( "\"" );
                                }
                                size--;
                                if ( size > 0 ) {
                                    sb.append( ",\r\n" );
                                }
                            }
                            sb.append( ")\r\n" );
                            if ( JdbcOpBuilder.dbStarRocks.equalsIgnoreCase( dsDo.getDsType() ) ) {
                                sb.append( "DUPLICATE KEY(`" ).append( fieldList.get( 0 ).getFieldAlias() ).append( "`)\n" );
                            }
                            if ( JdbcOpBuilder.dbStarRocks.equalsIgnoreCase( dsDo.getDsType() ) || JdbcOpBuilder.dbMySql.equalsIgnoreCase( dsDo.getDsType() ) ) {
                                sb.append( "COMMENT \"" ).append( info.getName() ).append( "\" \r\n" );
                            }
                            if ( JdbcOpBuilder.dbStarRocks.equalsIgnoreCase( dsDo.getDsType() ) ) {
//                            sb.append( "DUPLICATE KEY(`").append( fieldList.get( 0 ).getFieldAlias() ).append( "`)\n" +
//                                    "COMMENT \"").append( "" ).append( "\"\n" +
//                                    "DISTRIBUTED BY HASH(`by_env_date`) BUCKETS 32 \n" +
//                                    "PROPERTIES (\n" +
//                                    "\"replication_num\" = \"1\",\n" +
//                                    "\"colocate_with\" = \"pclab\",\n" +
//                                    "\"in_memory\" = \"false\",\n" +
//                                    "\"storage_format\" = \"DEFAULT\"\n" +
//                                    ")" );
                                sb.append( "DISTRIBUTED BY HASH(`" ).append( fieldList.get( 0 ).getFieldAlias() ).append( "`) BUCKETS 4\r\n" +
                                        "PROPERTIES (\n" +
                                        "\"replication_num\" = \"1\",\n" +
                                        //"\"colocate_with\" = \"pclab\",\n" +
                                        "\"in_memory\" = \"false\",\n" +
                                        "\"storage_format\" = \"DEFAULT\"\n" +
                                        ")" );
                            }
                            logger.info( sb.toString() );
                            try {
                                jdbcOp.execute( sb.toString() );
                            }
                            catch ( Exception ee ){
                                logger.error( "{}输出表{}创建失败,", meData.getName(), tableName,ee );
                                return resMap.setErr( String.format( "%s的输出表创建失败", meData.getName(), dsDo.getDsName() ) ).getResultMap();
                            }
                        }
                    }

                    //StringBuilder sb = new StringBuilder();

                    HashMap<String, Object> fromMap = new HashMap<>();
                    HashMap<String, Object> toMap = new HashMap<>();
                    List<Object> outFieldList = new ArrayList<>();

                    String ddbDriver = dsDo.getDsDriver();
                    String ddbUrl = jdbcUrl;
                    String ddbUser = dsDo.getUsername();
                    String ddbPasswd = dsDo.getPassword();

                    String selFields = fieldList.stream().map( x -> x.getFieldName() ).collect( Collectors.joining( "," ) );

                    for (int norder = 0; norder < fieldList.size(); norder++) {
                        TOlkModelFieldDo columnDo = fieldList.get( norder );
//                        if (norder > 0) {
//                            sb.append(",\r\n");
//                        }
                        String fieldName = columnDo.getFieldName();
                        String fieldType = columnDo.getFieldType();
                        String fieldExpr = columnDo.getFieldExpr();
                        String columnType = columnDo.getColumnType();
                        //sb.append(fieldName);

                        HashMap<String, String> colData = new LinkedHashMap<>();
                        colData.put("name", fieldName);

                        String colType = JdbcTypeToJavaTypeUtil.chgDdbType(fieldType);
                        //logger.info("{}:{},{}",columnDo.getFieldName(), columnDo.getFieldType() ,colType);
                        //sb.append(" ").append(colType).append(" ");
                        colData.put("sourceType", fieldType);
                        int idx = colType.indexOf("(");
                        if (idx > 0) {
                            colData.put("type", colType.substring(0, idx));
                        } else {
                            colData.put("type", colType);
                        }

                        colData.put("comment", fieldExpr);
                        outFieldList.add(colData);
                        //sb.append("COMMENT \"").append(fieldExpr.replaceAll("\\'|\\\"", "")).append("\"");
                    }

                    toMap.put("table", tableName);
                    toMap.put("driver", ddbDriver);
                    toMap.put("jdbcUrl", ddbUrl);
                    toMap.put("user", ddbUser);
                    toMap.put("password", ddbPasswd);
                    toMap.put("dsType", dsDo.getDsType());

                    //fromMap.put("table", String.format("%s.tmp_%s", olkVdmCatalogViewName,meData.getId()));
                    fromMap.put("sql", String.format("select %s from %s.tmp_%s", selFields, olkVdmCatalogViewName,meData.getId()));
                    fromMap.put("driver", hetuDriver);
                    fromMap.put("jdbcUrl", hetuUrl);

                    HashMap<String, Object> dataMap = new HashMap<>();
                    dataMap.put("fromDb", fromMap);
                    dataMap.put("toDb", toMap);
                    dataMap.put("columns", outFieldList);
                    paraSetList.add( dataMap );
                }
                if ( !cacheViewToTable( info, user ,paraSetList ) ) {
                    return resMap.setErr( "提交处理任务失败" ).getResultMap();
                }
            }

            truModelService.updateBean( info );

            //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(user, old, info, "修改-处理联邦分析数据");

            resMap.setOk( "提交处理任务成功" );
        }
        catch ( MessageException ex ){
            resMap.setErr( "提交处理任务失败,".concat( ex.getMessage() ) );
            logger.error( "提交处理任务失败:", ex );
        }
        catch ( Exception ex ) {
            resMap.setErr( "提交处理任务失败" );
            logger.error( "提交处理任务失败:", ex );
        }
        return resMap.getResultMap();
    }

    /*private List<TBydbFieldDo> loadViewColumns( HetuJdbcOperate dbop, String viewName) throws Exception {

        //try {
        //String dbType = PageSQLUtil.dbPresto;

        List<TBydbFieldDo> addFieldList = new ArrayList<>();

        int fieldorder = 10;
        String showField = String.format("SHOW COLUMNS FROM %s", viewName);
        logger.debug(showField);
        List<Map<String, Object>> list3 = dbop.selectData(showField);
        logger.debug("field:{}", list3);

        for (Map<String, Object> dat3 : list3) {
            Iterator<Map.Entry<String, Object>> iterator3 = dat3.entrySet().iterator();
            TBydbFieldDo fieldDo = new TBydbFieldDo();

            while (iterator3.hasNext()) {
                Map.Entry<String, Object> next = iterator3.next();
                String name = next.getKey();
                Object datval = next.getValue();
                if ("column".equalsIgnoreCase(name)) {
                    fieldDo.setFieldName(datval.toString());
                }
                if ("type".equalsIgnoreCase(name) && datval != null) {
                    fieldDo.setFieldType(datval.toString());
                }

                if ("comment".equalsIgnoreCase(name) && datval != null) {
                    fieldDo.setChnName(datval.toString());
                }
            }

            fieldDo.setNorder(fieldorder);
            fieldDo.setEnable(1);
            fieldorder += 10;
            addFieldList.add(fieldDo);

        }
        return addFieldList;

    }*/



    private boolean flinkModelJob( TOlkModelDo info,String sql, UserDo user ) throws Exception {
        StringBuilder sb = new StringBuilder();
//        String bydbCacheTableTemplate = systemParamHolder.getBydbAnalysisModelDealTableTemplate();
//        String cacheDbSet = systemParamHolder.getBydbAnalysisModelDealDbSet();
//        String bydbDealFlag = systemParamHolder.getBydbDealFlag();

        //String cacheDbSet = dcDo.getCacheDbSet();

        HashMap<String, Object> fromMap = new HashMap<>();
        HashMap<String, Object> toMap = new HashMap<>();
        List<Object> fieldList = new ArrayList<>();

//        String ddbDriver = null;
//        String ddbUrl = null;
//        String ddbUser = null;
//        String ddbPasswd = null;
//        try {
//            if (cacheDbSet.startsWith("{")) {
//                JsonObject jsonObject = JsonUtil.toJsonObject(cacheDbSet);
//                ddbDriver = jsonObject.get("driver").getAsString();
//                ddbUrl = jsonObject.get("url").getAsString();
//                ddbUser = jsonObject.get("user").getAsString();
//                ddbPasswd = jsonObject.get("passwd").getAsString();
//            }
//        } catch (Exception ee) {
//
//        }
//        if (ddbDriver == null) {
//            Properties prop = new Properties();
//            prop.load(new StringReader(cacheDbSet));
//            ddbDriver = prop.getProperty("driver");
//            ddbUrl = prop.getProperty("url");
//            ddbUser = prop.getProperty("user");
//            ddbPasswd = prop.getProperty("passwd");
//        }

        //String bydbCacheTableTemplate = dcDo.getCacheTemplete();

//        bydbCacheTableTemplate = bydbCacheTableTemplate.replaceAll("@tableName@", info.getCacheTableName());
//
//        Type type = new TypeToken<List<Map<String, String>>>() {
//        }.getType();
//        List<Map<String, String>> tmpColList = JsonUtil.deserialize(info.getParamConfig(), type);
//
//        for (int norder = 0; norder < tmpColList.size(); norder++) {
//            Map<String, String> columnDo = tmpColList.get(norder);
//            if (norder > 0) {
//                sb.append(",\r\n");
//            }
//            String fieldName = columnDo.get("fieldName");
//            String fieldType = columnDo.get("fieldType");
//            String fieldExpr = columnDo.get("fieldExpr");
//            String columnType = columnDo.get("columnType");
//            sb.append(fieldName);
//
//            HashMap<String, String> colData = new LinkedHashMap<>();
//            colData.put("name", fieldName);
//
//            String colType = JdbcTypeToJavaTypeUtil.chgDdbType(fieldType);
//            //logger.info("{}:{},{}",columnDo.getFieldName(), columnDo.getFieldType() ,colType);
//            sb.append(" ").append(colType).append(" ");
//            colData.put("sourceType", fieldType);
//            int idx = colType.indexOf("(");
//            if (idx > 0) {
//                colData.put("type", colType.substring(0, idx));
//            } else {
//                colData.put("type", colType);
//            }
//
//            colData.put("comment", fieldExpr);
//            fieldList.add(colData);
//            sb.append("COMMENT \"").append(fieldExpr.replaceAll("\\'|\\\"", "")).append("\"");
//        }
//        bydbCacheTableTemplate = bydbCacheTableTemplate.replaceAll("@columns@", sb.toString());
//
//        bydbCacheTableTemplate = bydbCacheTableTemplate.replaceAll("@tableComment@", info.getName());
//
//        logger.info(bydbCacheTableTemplate);
//
//        try (CommonJdbcOp dbop = new CommonJdbcOp(PageSQLUtil.dbMySql, ddbDriver, ddbUrl, ddbUser, ddbPasswd)) {
//            dbop.execute(bydbCacheTableTemplate);
//        }

//        toMap.put("table", info.getCacheTableName());
//        toMap.put("driver", ddbDriver);
//        toMap.put("jdbcUrl", ddbUrl);
//        toMap.put("user", ddbUser);
//        toMap.put("password", ddbPasswd);
//
//        fromMap.put("table", info.getViewName());
//        fromMap.put("driver", hutuConfig.getDriver());
//        fromMap.put("jdbcUrl", hutuConfig.getUrl());
//        DataPermissionRequest request = new DataPermissionRequest();
//        request.setToken(user.getTokenId());
//        hetuJdbcOperateComponent.fillHetuConfig(dataHubProperties, fromMap, request);

        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put( "modelId", info.getId() );
        dataMap.put( "taskNo", info.getCacheTaskNo() );
        dataMap.put( "taskName", String.format( "%s", info.getName().replaceAll( "\\s+|\\'|\\\"", "" ) ) );
        dataMap.put( "userName", user.getUserName() );
        dataMap.put( "userChnName", user.getChnName() );
        //dataMap.put( "runSql", info.getRunSql() );
        dataMap.put( "runSql", sql );
//        dataMap.put("fromDb", fromMap);
//        dataMap.put("toDb", toMap);
//        dataMap.put("columns", fieldList);
        String setInfo1 = JsonUtil.toJson( dataMap );
        String setInfo2 = Base64.getEncoder().encodeToString( setInfo1.getBytes() );
        logger.debug( "{}\r\n{}", setInfo1, setInfo2 );
        //HttpOperaterUtil hou = new HttpOperaterUtil();
        HashMap<String, String> paraMap = new HashMap<>();
        paraMap.put( "setInfo", setInfo2 );
        String ret = modelTaskFlinkApiService.runModelTask( paraMap );
        //String bydbCacheServerUrl = systemParamHolder.getBydbAnalysisModelDealServerUrl();
        //logger.debug("bydb联邦分析处理服务地址:{}", bydbCacheServerUrl);
        //String ret = hou.setDataMap(paraMap).setAppJson(false).setStrUrl(bydbCacheServerUrl).sendPostRequest();
        logger.debug( ret );
        JsonObject jsonObject = JsonUtil.toJsonObject( ret );
        if ( 0 == jsonObject.get( "code" ).getAsInt() ) {
            return true;
        }
        return false;
    }

    /**
     * 创建处理表
     */
    private boolean cacheViewToTable(TOlkModelDo info,UserDo user,List<Object> paraSetList) throws Exception {
        StringBuilder sb = new StringBuilder();

        //DataPermissionRequest request = new DataPermissionRequest();
        //request.setToken(user.getTokenId());
        //hetuJdbcOperateComponent.fillHetuConfig(dataHubProperties, fromMap, request);

        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("modelId", info.getId());
        dataMap.put("taskNo", info.getCacheTaskNo());
        dataMap.put("taskName", String.format("%s", info.getName().replaceAll("\\s+|\\'|\\\"", "")));
        dataMap.put("userName",user.getUserName());
        dataMap.put("userChnName",user.getChnName());
        dataMap.put("paraSetList", paraSetList);
        String setInfo1 = JsonUtil.toJson(dataMap);
        String setInfo2 = Base64.getEncoder().encodeToString(setInfo1.getBytes());
        logger.info("{}\r\n{}", setInfo1, setInfo2);
        HttpOperaterUtil hou = new HttpOperaterUtil();
        HashMap<String, String> paraMap = new HashMap<>();
        paraMap.put("setInfo", setInfo2);
        logger.debug("联邦分析处理服 ");
        String ret = olkTaskFlinkApiService.runModelTask( paraMap );
        logger.debug(ret);
        JsonObject jsonObject = JsonUtil.toJsonObject(ret);
        if (0 == jsonObject.get("code").getAsInt()) {
            return true;
        }
        return false;
    }
    @ApiOperation(value = "数据预览", notes = "数据预览")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "联邦分析id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "当前页", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "Integer", required = false, paramType = "query")

    })
    @RequestMapping(value = "/querymodeldata", method = {RequestMethod.GET})
    @ResponseBody
    public Object queryDatasetData( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            TOlkModelDo modelVo = new TOlkModelDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo, hru );
            if ( StringUtils.isBlank( modelVo.getId() ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }

            TOlkModelDo modelDo = truModelService.findById( modelVo.getId() );
            if ( modelDo == null ) {
                return resMap.setErr( "联邦分析不存在" ).getResultMap();
            }
            UserDo user = LoginUtil.getUser();

            TOlkModelElementDo truModelDo = new TOlkModelElementDo();
            truModelDo.setModelId( modelDo.getId() );
            List<TOlkModelElementDo> eleList = truModelElementService.find( truModelDo );

            if ( modelDo.getBuildType() == 1 ) {
                //组件数据输入
                List<OlkModelElementVo> inPutData = new ArrayList<>();
                List<TOlkModelElementDo> elements = eleList.stream().filter( x -> x.getElementType() != null && x.getElementType().intValue() == 1 ).collect( Collectors.toList() );
                if ( CollectionUtils.isNotEmpty( elements ) ) {
                    elements.stream().forEach( elementDo -> {
                        List<TOlkModelFieldDo> fieldDos = truModelFieldService.selectByElementId( elementDo.getId() );
                        OlkBaseComponenT baseComponenT = new OlkTableComponent();
                        List<TOlkModelFieldDo> fieldDosNew = fieldDos.stream().filter( e -> e.getIsSelect() == 1 ).collect( Collectors.toList() );
                        OlkModelElementVo elementVo = new OlkModelElementVo();
                        try {
                            MyBeanUtils.copyBean2Bean( elementVo, elementDo );
                            elementVo.setField( baseComponenT.getShowField( fieldDosNew ) );
                            inPutData.add( elementVo );

                        }
                        catch ( Exception ex ) {
                            ex.printStackTrace();
                        }
                    } );
                }

//                List<TOlkModelElementRelDo> relList = truModelElementRelService.selectByModelId( modelDo.getId() );
//                List<String> nodeList = new ArrayList<>();
//                for ( TOlkModelElementDo elementDo : eleList ) {
//                    nodeList.add( elementDo.getId() );
//                }
//                //HashMap<String,String> outRelMap = new HashMap<>(); //寻找输出的输入 （输出为1对1）
//                for ( TOlkModelElementRelDo relDo : relList ) {
//                    nodeList.remove( relDo.getStartElementId() );
//                    //outRelMap.put( relDo.getEndElementId(),relDo.getStartElementId() );
//                }
//                List<TOlkModelElementDo> outList = eleList.stream().filter( x -> nodeList.indexOf( x.getId() ) >= 0 ).collect( Collectors.toList() );
//
//
                List<TOlkModelElementDo> outEleList = eleList.stream().filter( x -> OlkComponentEnum.DataSourceOutPut_COMPONENT.getComponentName().equals( x.getIcon() ) ).collect( Collectors.toList() );
                List<OlkModelElementVo> outputList =  new ArrayList<>();
                if ( outEleList != null && outEleList.size() > 0 ) {
                    for ( TOlkModelElementDo elementDos : outEleList ) {
                        OlkModelElementVo output = new OlkModelElementVo();
                        //TOlkModelElementDo elementDos = collect.get( 0 );
                        List<TOlkModelFieldDo> fieldDos = truModelFieldService.selectByElementId( elementDos.getId() );
                        //List<TOlkModelFieldDo> fieldDos = new ArrayList<>();
//                    if (elementDos.getElementType() == 0) {
//                        TOlkModelComponentDo componentDo = truModelComponentService.findById(elementDos.getTcId());
//                        BaseComponenT baseComponenT = ComponentEnum.getInstanceByName(componentDo.getComponentEn().toLowerCase());
//                        //fieldDos = baseComponenT.getShowField(fieldDos1);
//                    }
                        output.setField( fieldDos );
                        output.setName( elementDos.getName() );
                        JsonObject jsonObject = JsonUtil.toJsonObject( elementDos.getConfig() );
                        if ( jsonObject.has( OlkDataSourceOutPutComponent.paraTableName ) ) {
                            String tableName = jsonObject.get( OlkDataSourceOutPutComponent.paraTableName ).getAsString();
                            output.setName( tableName );
                        }
                        output.setId( elementDos.getId() );
                        output.setIcon( elementDos.getIcon() );
                        outputList.add(  output );
                    }
                }
                else{
                    if( modelDo.getCacheFlag()!= null && modelDo.getCacheFlag() ==3 ) {
                        List<TOlkModelElementRelDo> relList = truModelElementRelService.selectByModelId( modelDo.getId() );
                        List<String> nodeList = new ArrayList<>();
                        for ( TOlkModelElementDo elementDo : eleList ) {
                            nodeList.add( elementDo.getId() );
                        }
                        for ( TOlkModelElementRelDo relDo : relList ) {
                            nodeList.remove( relDo.getStartElementId() );
                        }
                        if ( nodeList.size() == 1 ) {
                            OlkModelElementVo output = new OlkModelElementVo();
                            TOlkModelElementDo elementDos = eleList.stream().filter( x -> x.getId().equals( nodeList.get( 0 ) ) ).collect( Collectors.toList() ).get( 0 );
                            String tableName = "tmp_" + elementDos.getId();
                            List<TOlkModelFieldDo> fieldDos = truModelFieldService.selectByElementId( elementDos.getId() ).stream().filter( x -> x.getIsSelect() != null && x.getIsSelect() == 1 ).collect( Collectors.toList() );
                            output.setField( fieldDos );
                            output.setName( elementDos.getName() );
                            output.setName( tableName );
                            output.setId( elementDos.getId() );
                            output.setIcon( elementDos.getIcon() );
                            outputList.add( output );
                        }
//                    if ( JdbcOpBuilder.dbOpenLooKeng.equalsIgnoreCase( dstype ) ) {
//                        return resMap.setErr( String.format( "%s的数据源%s不能做为输出数据源", meData.getName(), dsDo.getDsName() ) ).getResultMap();
//                    }
                    }
                }

                resMap.put( "input", inPutData );
                resMap.put( "output", outputList );
                return resMap.setOk( "查询联邦分析成功" ).getResultMap();
            }
            else {
                //String dcSql = modelDo.getRunSql();
                long count = 0;
                List<Map<String, Object>> list = new ArrayList<>();
                if ( modelDo.getCacheFlag() != null && modelDo.getCacheFlag() == 3 ) {
                    //已处理
                    String olkVdmCatalogViewName = SysParamSetOp.readValue( "olkVdmCatalogViewName", "" );
                    if ( StringUtils.isBlank( modelDo.getDcId() ) ) { //跨节点运行
                        try ( HetuJdbcOperate dbop = this.hetuJdbcOperate ) {
                            String viewSql = String.format( "select * from  %s.tmp_%s limit 100 ", olkVdmCatalogViewName, modelDo.getId() );
                            list = dbop.selectData( viewSql );
                        }
                    }
                    else {
                        TOlkDcServerDo dcDo = dcService.findById( modelDo.getDcId() );
                        if ( dcDo.getEnable() == null || dcDo.getEnable() != 1 ) {
                            return resMap.setErr( "节点未启用" ).getResultMap();
                        }
                        //HetuInfo hetuInfo = hetuJdbcOperateComponent.genHetuInfo( dcDo );
                        try ( HetuJdbcOperate dbop = hetuJdbcOperateComponent.genHetuJdbcOperate(dcDo) ) {
                            String viewSql = String.format( "select * from  %s.tmp_%s limit 100 ", olkVdmCatalogViewName, modelDo.getId() );
                            list = dbop.selectData( viewSql );
                        }
                    }
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

                if ( StringUtils.isNotBlank( modelDo.getParamConfig() ) ) {
                    Type type = new TypeToken<List<Map<String, String>>>() {
                    }.getType();
                    List<Map<String, String>> tmpColList = JsonUtil.deserialize( modelDo.getParamConfig(), type );
                    resMap.put( "colList", tmpColList );
                }
                else {
                    resMap.put( "colList", new ArrayList<>() );
                }

                resMap.setPageInfo( modelVo.getPageSize(), modelVo.getCurrentPage() );
                return resMap.setOk( count, list, "查询联邦分析成功" ).getResultMap();
            }
        }
        catch ( Exception ex ) {
            resMap.setErr( "查询联邦分析失败" );
            logger.error( "查询联邦分析失败:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "任务列表", notes = "任务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qryCond", value = "模糊条件", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "floderId", value = "文件夹id", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "cacheFlag", value = "运行状态 0 未提交 1 已提交 2 运行 3成功 9失败", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "页数", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", required = false, paramType = "query")
    })
    @RequestMapping(value = "/tasklist", method = {RequestMethod.GET})
    public Map<String, Object> taskList( HttpServletRequest request ) {
        ResponeMap result = this.genResponeMap();
        try {
            TOlkModelDo modelInfo = new TOlkModelDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            logger.debug( "{}", hru.getAllParaData() );
            new PageBeanWrapper( modelInfo, hru );
            MyBeanUtils.chgBeanLikeProperties( modelInfo, "name", "qryCond" );

            UserDo userDo = LoginUtil.getUser( request );
//            AuthRoleLevelBean authRoleLevelBean = menuUtil.getMenuCls().userSystemAuthLevel(setOp, userDo);
//            logger.debug("用户数据权限:{},{},{}",authRoleLevelBean.getLevel(),authRoleLevelBean.getCode(),authRoleLevelBean.getName());

            modelInfo.setCreateDeptNo( userDo.getOrgNo() );
            modelInfo.setCreatorAccount( userDo.getUserName() );

//            if( authRoleLevelBean.getLevel() ==1 ){
//
//            }
//            else if( authRoleLevelBean.getLevel() >=4 ){
//                modelInfo.setCreatorAccount(userDo.getUserName());
//            }
//            else if( authRoleLevelBean.getLevel() >=2 && authRoleLevelBean.getLevel() <4  ){
//
//            }

            final Map<String, TOlkDcServerDo> dcMap = dcService.findAll().stream().collect( Collectors.toMap( x -> x.getId(), x -> x ) );

            List<OlkModelVo> doList = new ArrayList<>();
            //List<TOlkModelDo> data = truModelService.findBeanList(modelInfo);
            long cnt = truModelService.findBeanCnt( modelInfo );
            modelInfo.genPage( cnt );
            List<TOlkModelDo> data = truModelService.findBeanList( modelInfo );

            for ( TOlkModelDo datum : data ) {
                OlkModelVo olkModelVo = new OlkModelVo();
                MyBeanUtils.copyBeanNotNull2Bean( datum,olkModelVo );

                TOlkDcServerDo dcDo = null;
                if( StringUtils.isNotBlank( datum.getDcId() ) ){
                    dcDo = dcMap.get( datum.getDcId() );
                    if(dcDo == null){
                        dcDo = new TOlkDcServerDo();
                        dcDo.setDcCode( "" );
                        dcDo.setDcName( "未知节点" );
                    }
                }
                else{
                    dcDo = new TOlkDcServerDo();
                    dcDo.setDcCode( "" );
                    dcDo.setDcName( "中心节点" );
                }
                olkModelVo.setDcCode( dcDo.getDcCode() );
                olkModelVo.setDcName( dcDo.getDcName() );
                doList.add( olkModelVo );
            }
            //data.stream().forEach( e -> {
                //OlkModelVo TOlkModelVo = new OlkModelVo();
                //TOlkModelVo.setModelDo( e );
//                List<TOlkModelElementDo> elements = truModelElementService.selectByModelId(e.getId());
//                List<TOlkModelElementVo> elementsVo = new ArrayList<>();
//                if (CollectionUtils.isNotEmpty(elements)) {
//                    elements.stream().forEach(elementDo -> {
//                        TOlkModelElementVo elementVo = new TOlkModelElementVo();
//                        try {
//                            MyBeanUtils.copyBean2Bean(elementVo, elementDo);
//                            elementsVo.add(elementVo);
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    });
//                }
//                List<TOlkModelElementRelDo> elementRels = truModelElementRelService.selectByModelId(e.getId());
//                TOlkModelVo.setElements(elementsVo);
//                TOlkModelVo.setElementRels(elementRels);
//                doList.add( TOlkModelVo );
//            } );

            result.setPageInfo( modelInfo.getPageSize(), modelInfo.getCurrentPage() );
            TOlkModelDo statsBean = new TOlkModelDo();
            statsBean.setCreateDeptNo( modelInfo.getCreateDeptNo() );
            statsBean.setCreatorAccount( modelInfo.getCreatorAccount() );
            List<Map<String, Object>> allList = truModelService.statsByUser( statsBean );
            StatusCount statusCount = new StatusCount();

            if ( allList.size() > 0 ) {
                allList.stream().forEach( e -> {
                    Integer cacheflag = e.get( "cacheflag" ) == null ? 0 : Integer.parseInt( e.get( "cacheflag" ).toString() );
                    Integer cnt1 = e.get( "cnt" ) == null ? 0 : Integer.parseInt( e.get( "cnt" ).toString() );
                    statusCount.setTotal( statusCount.getTotal() + cnt1 );
                    if ( cacheflag == null ) {
                        statusCount.setUnsubmit( statusCount.getUnsubmit() + cnt1 );
                    }
                    else if ( cacheflag == TaskStatus.SUBMIT.getStatus() ) {
                        statusCount.setSubmit( statusCount.getSubmit() + cnt1 );
                    }
                    else if ( cacheflag == TaskStatus.FAIL.getStatus() ) {
                        statusCount.setFail( statusCount.getFail() + cnt1 );
                    }
                    else if ( cacheflag == TaskStatus.SUCCESS.getStatus() ) {
                        statusCount.setSuccess( statusCount.getSuccess() + cnt1 );
                    }
                    else if ( cacheflag == TaskStatus.START.getStatus() ) {
                        statusCount.setRun( statusCount.getRun() + cnt1 );
                    }
                    else {
                        statusCount.setUnsubmit( statusCount.getUnsubmit() + cnt1 );
                    }
                } );
            }
            result.put( "info", statusCount );
            result.setOk( cnt, doList );
        }
        catch ( Exception e ) {
            logger.error( "获取任务列表", e );
            e.printStackTrace();
        }
        return result.getResultMap();
    }

    /*
        @ApiOperation(value = "任务执行列表", notes = "任务执行列表")
        @ApiImplicitParams({
                @ApiImplicitParam(name = "id", value = "任务ID", dataType = "String", required = true, paramType = "query", example = "11f7e27ce0474ba9b882ceee2e600001")
        })
        @RequestMapping(value = "/joblist", method = {RequestMethod.GET})
        public Map<String, Object> jobList(String id) {
            ResponeMap result = this.genResponeMap();
            try {
                List<TOlkModelElementJobDo> allList = truModelElementJobService.selectByElementId(id);
                allList.stream().forEach(e -> {
                    e.setConfig("");
                });
                result.setSingleOk(allList, "查询执行记录成功");
            } catch (Exception e) {
                logger.error("任务执行列表失败", e);
                e.printStackTrace();
            }

            return result.getResultMap();
        }


        @ApiOperation(value = "执行任务", notes = "执行任务")
        @ApiImplicitParams({
                @ApiImplicitParam(name = "id", value = "任务ID", dataType = "String", required = true, paramType = "query", example = "11f7e27ce0474ba9b882ceee2e600001")
        })
        @RequestMapping(value = "/run", method = {RequestMethod.POST})
        public Map<String, Object> runModel(String id, HttpServletRequest request) {
            ResponeMap result = this.genResponeMap();
            if (StringUtils.isEmpty(id)) {
                return result.setErr("任务id为空").getResultMap();
            }
            try {
                TOlkModelDo modelDo = truModelService.findById(id);
                if (modelDo == null) {
                    return result.setErr("任务不存在").getResultMap();
                }
                TOlkModelElementJobDo tBydbModelElementJobDo = new TOlkModelElementJobDo();
                LoginUtil.setBeanInsertUserInfo(tBydbModelElementJobDo, request);
                CheckComponent component = analysisRunService.runModel(modelDo, tBydbModelElementJobDo);
                if (!component.isSuccess()) {
                    return result.setErr(component.getMessage()).getResultMap();
                }
                result.setSingleOk(tBydbModelElementJobDo, "执行模型");

            } catch (Exception e) {
                logger.error("执行模型失败", e);
                result.setErr("执行模型失败");
            }
            return result.getResultMap();
        }

    */
    @ApiOperation(value = "查看数据源", notes = "查看数据源")
    @RequestMapping(value = "/datasource", method = {RequestMethod.POST})
    public Map<String, Object> datasource( HttpServletRequest request ) {
        ResponeMap result = this.genResponeMap();

        try {
            UserDo user = LoginUtil.getUser();
            FDatasourceDo dsDo = new FDatasourceDo();
            dsDo.setCreatorAccount( user.getUserName() );
            List<FDatasourceDo> list = dbSourceService.findBeanList( dsDo );
            result.setSingleOk( list, "执行成功" );
        }
        catch ( Exception e ) {
            logger.error( "执行查看数据源失败", e );
            result.setErr( "执行查看数据源失败" );
        }
        return result.getResultMap();
    }

    /*
        @ApiOperation(value = "获取数据源下表数据", notes = "根据数据源主键来获取数据源下所有表")
        @ApiImplicitParams({
                @ApiImplicitParam(name = "id", value = "数据源id", dataType = "String", required = true, paramType = "query"),
                @ApiImplicitParam(name = "tableName", value = "表名", dataType = "String", required = false, paramType = "query")
        })
        @RequestMapping(value = {"/tablelist"}, method = {RequestMethod.GET})
        @ResponseBody
        public Object tableListById(String id, String tableName) {
            ResponeMap result = this.genResponeMap();
            try {
                List<String> list = new ArrayList<String>();
                EDatasourceDo datasource = dataSourceService.findById(id);
                //queryTool组装
                if (datasource == null) {
                    list = null;
                } else if (Constants.ODPS.equals(datasource.getDsType())) {
                    list = new OdpsQueryTool(datasource).getTableNames();
                } else {
                    try (BaseQueryTool qTool = QueryToolFactory.getByDbType(datasource)) {
                        list = qTool.getTableNames();
                    }
                }
                if (list != null) {
                    result.setSingleOk(list, "表数据列");
                } else {
                    result.setErr("表数据列获取失败");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("测试连接失败:", ex);
                result.setErr("测试连接失败");
            }
            return result.getResultMap();
        }

        @ApiOperation(value = "表数据字段探查", notes = "表数据字段探查")
        @ApiImplicitParams({
                @ApiImplicitParam(name = "id", value = "节点id", dataType = "String", required = true, paramType = "query"),
        })
        @RequestMapping(value = {"/tablefields"}, method = {RequestMethod.GET})
        @ResponseBody
        public Object tablefields(String id) {
            ResponeMap result = this.genResponeMap();
            try {
                try {
                    TOlkModelElementVo output = new TOlkModelElementVo();
                    TOlkModelElementDo elementDos = truModelElementService.findById(id);

                    List<TOlkModelFieldDo> fieldDos = truModelFieldService.selectByElementId(elementDos.getId());
                    MyBeanUtils.copyBean2Bean(output, elementDos);
                    output.setField(fieldDos);
                    result.setSingleOk(output, "表数据字段探查成功");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.error("表数据字段探查失败:", ex);
                    result.setErr("表数据字段探查失败");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("表数据字段探查失败:", ex);
                result.setErr("表数据字段探查失败");
            }
            return result.getResultMap();
        }

        @ApiOperation(value = "执行数据源任务", notes = "执行数据源任务")
        @ApiImplicitParams({
                @ApiImplicitParam(name = "id", value = "任务id", dataType = "String", required = true, paramType = "query"),
        })
        @RequestMapping(value = {"/transform"}, method = {RequestMethod.POST})
        @ResponseBody
        public Object transform(HttpServletRequest request) {
            ResponeMap result = this.genResponeMap();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            logger.debug("{}", hru.getAllParaData());
            TOlkModelDo tempCol = new TOlkModelDo();
            new PageBeanWrapper(tempCol, hru, "");
            if (StringUtils.isBlank(tempCol.getId())) {
                return result.setErr("任务id不能为空").getResultMap();
            }
            try {
                TOlkModelElementJobDo jobDo = new TOlkModelElementJobDo();
                TOlkModelDo modelDo = truModelService.findById(tempCol.getId());
                if (modelDo == null) {
                    return result.setErr("任务不存在").getResultMap();
                }
                MyBeanUtils.copyBean2Bean(jobDo, modelDo);
                jobDo.setId(ComUtil.genId());
                jobDo.setCreatedTime(ComUtil.getCurTimestamp());
                jobDo.setModifiedTime(ComUtil.getCurTimestamp());
                jobDo.setModelId(modelDo.getId());
                jobDo.setJobStatus(2);
                List<TOlkModelElementDo> doList = truModelElementService.selectByModelId(modelDo.getId());
                if (doList.size() == 0) {
                    return result.setErr("组件未配置").getResultMap();
                }
                TOlkModelElementDo elementDos = null;
                List<TOlkModelFieldDo> fieldList = new ArrayList<>();
                for (TOlkModelElementDo TOlkModelElementDo : doList) {
                    TOlkModelComponentDo componentDo = truModelComponentService.findById(TOlkModelElementDo.getTcId());
                    if (componentDo != null &&
                            componentDo.getComponentEn().equals(DataSourceOutPut_COMPONENT.getComponentName())) {
                        elementDos = TOlkModelElementDo;
                        List<TOlkModelFieldDo> fieldDos = truModelFieldService.selectByElementId(elementDos.getId());
                        BaseComponenT baseComponenT = new TDataSourceOutPutComponent();
                        fieldList = baseComponenT.getShowField(fieldDos);
                        break;
                    }
                }
                if (elementDos == null || StringUtils.isBlank(elementDos.getRunSql())) {
                    return result.setErr("数据源组件未配置").getResultMap();
                }

                Map<String, Object> config = MapTypeAdapter.gsonToMap(elementDos.getConfig());
                TDataSourceOutPutComponent dataSourceOutPutComponent = JsonUtil.gson().fromJson(JsonUtil.toJson(config), TDataSourceOutPutComponent.class);
                EDatasourceDo datasource = dataSourceService.findById(dataSourceOutPutComponent.getDatasourceId());
                if (datasource == null) {
                    return result.setErr("数据源不存在").getResultMap();
                }
                String tableName = dataSourceOutPutComponent.getTableName();
                boolean isFlag = analysisRunService.analyseToTable(fieldList, datasource, jobDo, elementDos
                        , tableName);
                if (!isFlag) {
                    return result.setErr("执行失败").getResultMap();
                }

                truModelElementJobService.insertBean(jobDo);
                result.setOk("执行成功");
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("执行失败:", ex);
                result.setErr("执行失败");
            }
            return result.getResultMap();
        }
*/
    @ApiOperation(value = "执行组件查询", notes = "执行组件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "点ID", dataType = "String", required = true, paramType = "query", example = "11f7e27ce0474ba9b882ceee2e600001")
    })
    @RequestMapping(value = "/execute", method = {RequestMethod.POST})
    public Map<String, Object> execute( String id, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        if ( StringUtils.isEmpty( id ) ) {
            return resMap.setErr( "节点id为空" ).getResultMap();
        }
        try {
            UserDo user = LoginUtil.getUser();
            TOlkModelElementDo meData = truModelElementService.findById( id );
            TOlkModelDo modelDo = truModelService.findById( meData.getModelId() );
            List<Object> colList = truModelFieldService.selectByElementIdAll( meData.getId() )
                    .stream().filter( x -> x.getIsSelect() != null && x.getIsSelect() == 1 )
                    .map( x->{ HashMap<String,Object> dat = new HashMap<>();
                    dat.put( "fieldAlias",x.getFieldAlias());
                    dat.put( "fieldExpr",StringUtils.isBlank( x.getFieldExpr() )?x.getFieldAlias():x.getFieldExpr() );
                    return dat;
                    } ).collect( Collectors.toList() );

            resMap.put( "colList",colList );
            List<Map<String, Object>> list = new ArrayList<>();
            if ( "t_datasource".equals( meData.getIcon()) && modelDo.getCacheFlag() != null && modelDo.getCacheFlag() == 3 ) {
                //已处理
                    /*StringBuilder sb = new StringBuilder();
                    String cacheDbSet = systemParamHolder.getBydbAnalysisModelDealDbSet();
                    String ddbDriver = null;
                    String ddbUrl = null;
                    String ddbUser = null;
                    String ddbPasswd = null;
                    try {
                        if (cacheDbSet.startsWith("{")) {
                            JsonObject jsonObject = JsonUtil.toJsonObject(cacheDbSet);
                            ddbDriver = jsonObject.get("driver").getAsString();
                            ddbUrl = jsonObject.get("url").getAsString();
                            ddbUser = jsonObject.get("user").getAsString();
                            ddbPasswd = jsonObject.get("passwd").getAsString();

                        }
                    } catch (Exception ee) {

                    }
                    if (ddbDriver == null) {
                        Properties prop = new Properties();
                        prop.load(new StringReader(cacheDbSet));
                        ddbDriver = prop.getProperty("driver");
                        ddbUrl = prop.getProperty("url");
                        ddbUser = prop.getProperty("user");
                        ddbPasswd = prop.getProperty("passwd");
                    }*/

                String config = meData.getConfig();
                if ( StringUtils.isBlank( config ) ) {
                    return resMap.setErr( String.format( "%s未配置输出", meData.getName() ) ).getResultMap();
                }
                JsonObject jsonObject = JsonUtil.toJsonObject( config );
                //{  "datasourceId": "814791acd5d54b578c241fe53b752f0e",  "tableName": "test11"}
                if ( !jsonObject.has( "datasourceId" ) || !jsonObject.has( "tableName" ) ) {
                    return resMap.setErr( String.format( "%s配置不完整", meData.getName() ) ).getResultMap();
                }
                String tableName = jsonObject.get( OlkDataSourceOutPutComponent.paraTableName ).getAsString();
                String datasourceId = jsonObject.get( OlkDataSourceOutPutComponent.paraDsId ).getAsString();
                String dbschema = jsonObject.get( OlkDataSourceOutPutComponent.paraSchemaName ).getAsString();
                FDatasourceDo dsDo = dbSourceService.findById( datasourceId );
                if ( dsDo == null ) {
                    return resMap.setErr( String.format( "%s数据源不存在", meData.getName() ) ).getResultMap();
                }
                if ( JdbcOpBuilder.dbOpenLooKeng.equalsIgnoreCase( dsDo.getDsType() ) ) {
                    return resMap.setErr( String.format( "%s的数据源%s不能做为输出数据源", meData.getName(), dsDo.getDsName() ) ).getResultMap();
                }
                String sql = "select * from " + tableName;
                logger.debug( sql );
                String jdbcUrl = JdbcOpBuilder.genUrl( dsDo.getDsType(), dsDo.getDsIp(), dsDo.getDsPort(), dbschema );
                try ( IJdbcOp jdbcOp = new JdbcOpBuilder()//.withCatalog( dsDo.getDsDatabase() ).withSchema( dsDo.getDsSchema() )
                        .withSet( dsDo.getDsType(), dsDo.getDsDriver(), jdbcUrl, dsDo.getUsername(), dsDo.getPassword() ).build() ) {
                    list = jdbcOp.selectData( sql, 0, 100 );
                }
            }
            else if ( modelDo.getCacheFlag() != null && modelDo.getCacheFlag() == 3 ) {

                String olkVdmCatalogViewName = SysParamSetOp.readValue( "olkVdmCatalogViewName","" );
                if ( StringUtils.isBlank( modelDo.getDcId() ) ) { //跨节点运行
                    try ( HetuJdbcOperate dbop = this.hetuJdbcOperate ) {
                        String viewSql = String.format( "select * from  %s.tmp_%s limit 100 ", olkVdmCatalogViewName, meData.getId() );
                        list = dbop.selectData( viewSql );
                    }
                }
                else {
                    TOlkDcServerDo dcDo = dcService.findById( modelDo.getDcId() );
                    if ( dcDo.getEnable() == null || dcDo.getEnable() != 1 ) {
                        return resMap.setErr( "节点未启用" ).getResultMap();
                    }
                    //HetuInfo hetuInfo = hetuJdbcOperateComponent.genHetuInfo( dcDo );
                    try ( HetuJdbcOperate dbop = hetuJdbcOperateComponent.genHetuJdbcOperate(dcDo) ) {
                        String viewSql = String.format( "select * from  %s.tmp_%s limit 100 ", olkVdmCatalogViewName, meData.getId() );
                        list = dbop.selectData( viewSql );
                    }
                }
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
            resMap.setSingleOk( list, "执行成功" );
        }
        catch ( Exception e ) {
            logger.error( "执行组件查询失败", e );
            resMap.setErr( "执行组件查询失败" );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "执行数据探查", notes = "执行数据探查")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "点ID", dataType = "String", required = true, paramType = "query", example = "11f7e27ce0474ba9b882ceee2e600001")
    })
    @RequestMapping(value = "/datacheck", method = {RequestMethod.GET})
    public Map<String, Object> datacheck( String id, HttpServletRequest request ) {
        ResponeMap result = this.genResponeMap();
        if ( StringUtils.isEmpty( id ) ) {
            return result.setErr( "节点id为空" ).getResultMap();
        }
        try {
            /*TOlkModelElementDo elementDo = truModelElementService.findById(id);
            if (elementDo == null || StringUtils.isBlank(elementDo.getRunSql())) {
                return result.setErr("节点不存在,或者节点未保存").getResultMap();
            }
            List<TOlkModelFieldDo> fieldDoList = new ArrayList<>();
            List<TOlkModelFieldDo> fieldDos = truModelFieldService.selectByElementId(elementDo.getId());
            if (fieldDos == null || fieldDos.size() == 0) {
                return result.setOk("数据为空").getResultMap();
            }
            if (elementDo.getElementType() == 1) {
                OlkBaseComponenT baseComponenT = new TTableComponent();
                fieldDoList = baseComponenT.getShowField(fieldDos);
            } else {
                TOlkModelComponentDo componentDo = truModelComponentService.findById(elementDo.getTcId());
                if (componentDo == null) {
                    return result.setErr("组件不存在").getResultMap();
                }
                OlkBaseComponenT baseComponenT = OlkComponentEnum.getInstanceByName(componentDo.getComponentEn().toLowerCase());
                fieldDoList = baseComponenT.getShowField(fieldDos);
            }

            List<Map<String, Object>> list = analysisRunService.datacheck(elementDo, fieldDoList);
            changeValueByType(list);
            result.setSingleOk(list, "执行成功");*/
        }
        catch ( Exception e ) {
            logger.error( "执行数据探查失败", e );
            result.setErr( "执行数据探查失败" );
        }
        return result.getResultMap();
    }
}
