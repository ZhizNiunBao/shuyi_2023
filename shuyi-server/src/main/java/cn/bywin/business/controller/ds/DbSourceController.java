package cn.bywin.business.controller.ds;


import cn.bywin.business.bean.bydb.TBydbCatalogTypeDo;
import cn.bywin.business.bean.bydb.TBydbDatabaseDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.system.SysDictDo;
import cn.bywin.business.bean.view.bydb.BydbDatabaseSourceVo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.job.DbDataLoadThread;
import cn.bywin.business.service.bydb.BydbCatalogTypeService;
import cn.bywin.business.service.bydb.BydbDatabaseService;
import cn.bywin.business.service.bydb.BydbDatasetService;
import cn.bywin.business.service.bydb.BydbFieldService;
import cn.bywin.business.service.bydb.BydbObjectService;
import cn.bywin.business.service.bydb.BydbSchemaService;
import cn.bywin.business.service.federal.DataSourceService;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.service.system.SysDictService;
import cn.bywin.business.trumodel.ApiTruModelService;
import cn.bywin.common.resp.ObjectResp;
import cn.jdbc.IJdbcOp;
import cn.jdbc.JdbcColumnInfo;
import cn.jdbc.JdbcOpBuilder;
import cn.jdbc.JdbcTableInfo;
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
@Api(tags = "联邦学习-数据源管理-dbsource")
@RequestMapping("/dbsource")
public class DbSourceController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Autowired
    private BydbDatabaseService databaseService;

    @Autowired
    private DataSourceService dbSourceService;

    @Autowired
    private SysDictService dictService;

    @Autowired
    private BydbSchemaService schemaService;

    @Autowired
    private BydbObjectService objectService;

    @Autowired
    private BydbDatasetService datasetService;

    @Autowired
    private BydbCatalogTypeService catalogTypeService;

    @Autowired
    private BydbFieldService fieldService;

    @Autowired
    private NodePartyService nodePartyService;

    @Autowired
    private ApiTruModelService apiTruModelService;


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    String dchetu = "dchetu";

    @ApiOperation(value = "新增数据源", notes = "新增数据源")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "info", value = "数据源", dataType = "TDatasourceDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public Object add( @RequestBody BydbDatabaseSourceVo bean, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
            FNodePartyDo nodePartyDo = nodePartyService.findFirst();

            if ( StringUtils.isBlank( nodePartyDo.getPrefixSymbol() ) ) {
                return resMap.setErr( "节点前缀符号未配置" ).getResultMap();
            }

            FDatasourceDo info = new FDatasourceDo();
            MyBeanUtils.copyBeanNotNull2Bean( bean, info );
            info.setNodePartyId( nodePartyDo.getId() );
            info.setSynFlag( 0 ); //待同步
            //info.setShareTime( null );

            if ( StringUtils.isBlank( info.getDsName() ) ) {
                return resMap.setErr( "名称不能为空" ).getResultMap();
            }

            if ( StringUtils.isBlank( info.getDsType() ) ) {
                return resMap.setErr( "类型不能为空" ).getResultMap();
            }

            boolean btype = false;
            for ( String s : JdbcOpBuilder.dbTypeList ) {
                if ( s.equalsIgnoreCase( info.getDsType() ) ) {
                    btype = true;
                    info.setDsType( s );
                    break;
                }
            }
            if ( !btype ) {
                return resMap.setErr( String.format( "类型%s不正确", info.getDsType() ) ).getResultMap();
            }
            if ( JdbcOpBuilder.dbOpenLooKeng.equals( info.getDsType() ) ) {
                return resMap.setErr( "不能添加此类型数据源" ).getResultMap();
            }
            if ( StringUtils.isBlank( info.getDsIp() ) ) {
                return resMap.setErr( "ip不能为空" ).getResultMap();
            }
            if ( info.getDsPort() == null ) {
                return resMap.setErr( "端口不能为空" ).getResultMap();
            }

            //if ( PageSQLUtil.dbKingBase.equalsIgnoreCase( info.getDsType() ) || PageSQLUtil.dbKingBase8.equalsIgnoreCase( info.getDsType() ) ) {
            if (!JdbcOpBuilder.dbOscar.equalsIgnoreCase( info.getDsType() ) && StringUtils.isBlank( info.getDsDatabase() ) ) {
                return resMap.setErr( "数据库不能为空" ).getResultMap();
            }
            //}
//            if ( JdbcOpBuilder.dbOracle.equalsIgnoreCase( info.getDsType() )|| JdbcOpBuilder.dbOscar.equalsIgnoreCase( info.getDsType() )|| JdbcOpBuilder.dbKingBase.equalsIgnoreCase( info.getDsType() ) || JdbcOpBuilder.dbKingBase8.equalsIgnoreCase( info.getDsType() ) || JdbcOpBuilder.dbOpenGauss.equalsIgnoreCase( info.getDsType() ) || JdbcOpBuilder.dbPostgreSql.equalsIgnoreCase( info.getDsType() ) ) {
//                info.setDsSchema( "" );
//            }
//            else {
//                //    if ( StringUtils.isBlank( info.getDsSchema() ) && StringUtils.isNotBlank( info.getDsDatabase() ) ) {
//                info.setDsSchema( info.getDsDatabase() );
//                //   }
//            }

//            if (StringUtils.isBlank(info.getUsername())) {
//                return resMap.setErr("用户不能为空").getResultMap();
//            }

            String driver = JdbcOpBuilder.findDrive( info.getDsType() );
            String jdbcUrl = JdbcOpBuilder.genUrl( info.getDsType(), info.getDsIp(), info.getDsPort(), info.getDsDatabase() );
            info.setDsDriver( driver );
            info.setJdbcUrl( jdbcUrl );
            //info.setDcId( dcDo.getId() );
            if ( info.getEnable() == null ) {
                info.setEnable( 1 );
            }
            info.setId( ComUtil.genId() );
            LoginUtil.setBeanInsertUserInfo( info, ud );

            final long sameNameCount = dbSourceService.findSameNameCount( info );
            if ( sameNameCount > 0 ) {
                return resMap.setErr( "名称已使用" ).getResultMap();
            }


            TBydbDatabaseDo databaseDo = new TBydbDatabaseDo();
            databaseDo.setId( nodePartyDo.getPrefixSymbol() + "db" + ComUtil.genId() );
            //databaseDo.setDcId( ud.getUserName() );
            databaseDo.setUserId( ud.getUserId() );
            databaseDo.setUserAccount( ud.getUserName() );
            databaseDo.setUserName( ud.getChnName() );
            databaseDo.setDbsourceId( info.getId() );
            databaseDo.setDcDbName( info.getDsName() );
            databaseDo.setDbName( info.getDsName() );
            databaseDo.setDbChnName( info.getDsName() );
            //databaseDo.setDcCode( dcDo.getDcCode() );
            databaseDo.setDcDbName( info.getDsName() );
            databaseDo.setDbType( info.getDsType() );
            databaseDo.setSynFlag( 0 );
            databaseDo.setNodePartyId( nodePartyDo.getId() );
            if ( StringUtils.isNotBlank( bean.getCatalogType() ) ) {
                databaseDo.setCatalogType( bean.getCatalogType() );
            }
            if ( StringUtils.isNotBlank( bean.getCatalogType() ) ) {
                TBydbCatalogTypeDo cataTypeDo = catalogTypeService.findById( bean.getCatalogType() );
                if ( cataTypeDo == null ) {
                    return resMap.setErr( "目录不存在" ).getResultMap();
                }
//                if( !dcDo.getId().equals( cataTypeDo.getDcId() )){
//                    return  resMap.setErr("只能使用同节点目录").getResultMap();
//                }
                databaseDo.setCatalogType( cataTypeDo.getId() );
            }
            Integer maxOrder = databaseService.findMaxOrder( ud.getUserName() );
            int norder = 10;
            if ( maxOrder != null ) {
                norder = maxOrder.intValue() + 10;
            }
            databaseDo.setNorder( norder );
            databaseDo.setEnable( 1 );
            LoginUtil.setBeanInsertUserInfo( databaseDo, ud );

            resMap.setSingleOk( info, "保存成功" );

            JdbcOpBuilder jb = new JdbcOpBuilder();
            try ( IJdbcOp jdbcOp = jb.withCatalog( info.getDsDatabase() ).withSchema( info.getDsSchema() ).withDbType( info.getDsType() )
                    .withDriver( info.getDsDriver() ).withUrl( info.getJdbcUrl() )
                    .withPassword( info.getPassword() ).withUser( info.getUsername() )
                    .build() ) {
                List<String> list = jdbcOp.listSchema( info.getDsDatabase(), info.getDsSchema() );
                if ( list == null || list.size() == 0 ) {
                    resMap.setOk( "数据源保存成功，但配置不正确，数据资源未生成" );
                    databaseDo = null;
                }
            }
            catch ( Exception ee ) {
                databaseDo = null;
                resMap.setOk( "数据源保存成功，但配置不正确，数据资源未生成" );
                logger.error( "生成数据资源获取数据库信息失败", ee );
            }

            dbSourceService.insertWithDatabase( info, databaseDo );

            if ( databaseDo != null ) {
                DbDataLoadThread thread = new DbDataLoadThread( schemaService, objectService, fieldService, apiTruModelService, redisTemplate,
                        databaseDo, info, null, null, nodePartyDo, ud, HttpRequestUtil.getAllIp( request ) );
                thread.start();
            }

            //databaseService.insertBean( databaseDo );
            //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).addLog(ud, info, "新增-数据源");

        }
        catch ( Exception ex ) {
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "修改数据源", notes = "修改数据源")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "modelVo", value = "数据源", dataType = "TDatasourceDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update( @RequestBody BydbDatabaseSourceVo bean, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
            //@RequestBody TBydbSchemaDo modelVo,
            //HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            //logger.debug( "{}",hru.getAllParaData() );
            if ( bean == null || StringUtils.isBlank( bean.getId() ) ) {
                return resMap.setErr( "数据不能为空" ).getResultMap();
            }
            FDatasourceDo info = dbSourceService.findById( bean.getId() );

            if ( info == null ) {
                return resMap.setErr( "内容不存在" ).getResultMap();
            }

            FDatasourceDo old = new FDatasourceDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, old );

            MyBeanUtils.copyBeanNotNull2Bean( bean, info );
            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            info.setNodePartyId( nodePartyDo.getId() );
            info.setSynFlag( 0 );
//            if( info.getShareFlag() == null ){
//                info.setShareFlag(  0 );
//            }
            //new PageBeanWrapper( info,hru,"");

            if ( StringUtils.isBlank( info.getDsName() ) ) {
                return resMap.setErr( "名称不能为空" ).getResultMap();
            }

            if ( StringUtils.isBlank( info.getDsType() ) ) {
                return resMap.setErr( "类型不能为空" ).getResultMap();
            }

            boolean btype = false;
            for ( String s : JdbcOpBuilder.dbTypeList ) {
                if ( s.equalsIgnoreCase( info.getDsType() ) ) {
                    btype = true;
                    info.setDsType( s );
                    break;
                }
            }
            if ( !btype ) {
                return resMap.setErr( String.format( "类型%s不能正确", info.getDsType() ) ).getResultMap();
            }
            if ( JdbcOpBuilder.dbOpenLooKeng.equals( info.getDsType() ) ) {
                return resMap.setErr( "不能添加此类型数据源" ).getResultMap();
            }
            if ( StringUtils.isBlank( info.getDsIp() ) ) {
                return resMap.setErr( "ip不能为空" ).getResultMap();
            }
            if ( info.getDsPort() == null ) {
                return resMap.setErr( "端口不能为空" ).getResultMap();
            }

            //if ( PageSQLUtil.dbKingBase.equalsIgnoreCase( info.getDsType() ) || PageSQLUtil.dbKingBase8.equalsIgnoreCase( info.getDsType() ) ) {
            if (!JdbcOpBuilder.dbOscar.equalsIgnoreCase( info.getDsType() ) && StringUtils.isBlank( info.getDsDatabase() ) ) {
                return resMap.setErr( "数据库不能为空" ).getResultMap();
            }
            //}
//            if ( JdbcOpBuilder.dbOracle.equalsIgnoreCase( info.getDsType() )|| JdbcOpBuilder.dbOscar.equalsIgnoreCase( info.getDsType() )|| JdbcOpBuilder.dbKingBase.equalsIgnoreCase( info.getDsType() ) || JdbcOpBuilder.dbKingBase8.equalsIgnoreCase( info.getDsType() ) || JdbcOpBuilder.dbOpenGauss.equalsIgnoreCase( info.getDsType() ) || JdbcOpBuilder.dbPostgreSql.equalsIgnoreCase( info.getDsType() ) ) {
//                info.setDsSchema( "" );
//            }
//            else {
//                //    if ( StringUtils.isBlank( info.getDsSchema() ) && StringUtils.isNotBlank( info.getDsDatabase() ) ) {
//                info.setDsSchema( info.getDsDatabase() );
//                //   }
//            }

//            if (StringUtils.isBlank(info.getUsername())) {
//                return resMap.setErr("用户不能为空").getResultMap();
//            }

            String driver = JdbcOpBuilder.findDrive( info.getDsType() );
            String jdbcUrl = JdbcOpBuilder.genUrl( info.getDsType(), info.getDsIp(), info.getDsPort(), info.getDsDatabase() );
            info.setDsDriver( driver );
            info.setJdbcUrl( jdbcUrl );
            //info.setDcId( dcDo.getId() );
            if ( info.getEnable() == null ) {
                info.setEnable( 1 );
            }

            final long sameNameCount = dbSourceService.findSameNameCount( info );
            if ( sameNameCount > 0 ) {
                return resMap.setErr( "名称已使用" ).getResultMap();
            }

            String msg = "保存成功";
            JdbcOpBuilder jb = new JdbcOpBuilder();
            try ( IJdbcOp jdbcOp = jb.withCatalog( info.getDsDatabase() ).withSchema( info.getDsSchema() ).withDbType( info.getDsType() )
                    .withDriver( info.getDsDriver() ).withUrl( info.getJdbcUrl() )
                    .withPassword( info.getPassword() ).withUser( info.getUsername() )
                    .build() ) {
                if ( !jdbcOp.checkConnect() ) {
                    msg = "数据源保存成功，但配置不正确";
                }
            }
            catch ( Exception ee ) {
                resMap.setOk( "数据源保存成功，但配置不正确" );
                logger.error( "生成数据资源获取数据库信息失败", ee );
            }

            TBydbDatabaseDo databaseDo = new TBydbDatabaseDo();
            databaseDo.setDbsourceId( info.getId() );
            List<TBydbDatabaseDo> dbList = databaseService.find( databaseDo );
            databaseDo = null;
            if ( !info.getDsName().equals( old.getDsName() ) ) {
                if ( dbList.size() > 0 ) {
                    databaseDo = dbList.get( 0 );
                    if ( databaseDo.getDbName().equals( databaseDo.getDbChnName() ) ) {
                        databaseDo.setDbChnName( info.getDsName() );
                    }
                    databaseDo.setDcDbName( info.getDsName() );
                    databaseDo.setDbName( info.getDsName() );
                    databaseDo.setDbType( info.getDsType() );
                    if ( StringUtils.isNotBlank( bean.getCatalogType() ) ) {
                        databaseDo.setCatalogType( bean.getCatalogType() );
                    }
                }
            }
//            if( info.getShareFlag() == 1) {
//                info.setShareTime( ComUtil.getCurTimestamp() );
//            }

            dbSourceService.updateWithDatabase( info, databaseDo );

            if ( dbList.size() > 0 ) {
                databaseDo = dbList.get( 0 );
                //TBydbDcServerDo dcDo = dcserverService.findById(databaseDo.getDcId());
                DbDataLoadThread thread = new DbDataLoadThread( schemaService, objectService, fieldService, apiTruModelService, redisTemplate,
                        databaseDo, info, null, null, nodePartyDo, ud, HttpRequestUtil.getAllIp( request ) );
                thread.start();
            }

//            if( info.getShareFlag() == 1){
//                Map<String, Object> retMap = apiTruModelService.synDbsource( info, ud.getTokenId() );
//                if( retMap.containsKey( "success" ) && ! (boolean)  retMap.get( "success" ) ){
//                    return resMap.setErr("保存完成，更新同步信息失败" ).getResultMap();
//                }
//            }
            //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(ud, old, info, "修改-数据源");

            resMap.setSingleOk( info, msg );

        }
        catch ( Exception ex ) {
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }

    /*@ApiOperation(value = "上报或取消上报数据源", notes = "上报或取消上报数据源")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "modelVo", value = "数据源", dataType = "TDatasourceDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/syndbsource", method = {RequestMethod.POST})
    @ResponseBody
    public Object synDbSource( String id,Integer flag, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
            if( StringUtils.isBlank( id ) ){
                return resMap.setErr( "数据源id不能为空" ).getResultMap();
            }
            FDatasourceDo info = dbSourceService.findById( id );
            if( info == null){
                return resMap.setErr( "数据源不存在" ).getResultMap();
            }
            if( StringUtils.isBlank(  info.getNodePartyId() )){
                FNodePartyDo nodePartyDo = nodePartyService.findFirst();
                info.setNodePartyId( nodePartyDo.getId() );
            }
            if( flag == 1){
                info.setShareFlag( 1 );
                info.setShareTime( ComUtil.getCurTimestamp() );
                FDatasourceDo tmp = new FDatasourceDo();
                tmp.setId( info.getId() );
                tmp.setNodePartyId( info.getNodePartyId() );
                tmp.setShareTime( info.getShareTime() );
                tmp.setShareFlag( info.getShareFlag() );
                dbSourceService.updateNoNull( tmp );
                Map<String, Object> retMap = apiTruModelService.synDbsource( info,ud.getTokenId() );
                if( retMap.containsKey( "success" ) && ! (boolean)  retMap.get( "success" ) ){
                    return resMap.setErr("保存完成，更新同步信息失败" ).getResultMap();
                }
            }
            else{
                info.setShareFlag( 0 );
                info.setShareTime( ComUtil.getCurTimestamp() );
                FDatasourceDo tmp = new FDatasourceDo();
                tmp.setId( info.getId() );
                tmp.setNodePartyId( info.getNodePartyId() );
                tmp.setShareTime( info.getShareTime() );
                tmp.setShareFlag( info.getShareFlag() );
                dbSourceService.updateNoNull( tmp );
                Map<String, Object> retMap = apiTruModelService.delDbsource( info.getId(),ud.getTokenId() );
                if( retMap.containsKey( "success" ) && ! (boolean)  retMap.get( "success" ) ){
                    return resMap.setErr("保存完成，取消同步信息失败" ).getResultMap();
                }
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

    @ApiOperation(value = "检测连接", notes = "检测连接")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "info", value = "数据源", dataType = "TDatasourceDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/checkconnect", method = {RequestMethod.POST})
    @ResponseBody
    public Object checkConnect( @RequestBody FDatasourceDo info, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {

            if ( StringUtils.isBlank( info.getDsType() ) ) {
                return resMap.setErr( "类型不能为空" ).getResultMap();
            }

            boolean btype = false;
            for ( String s : JdbcOpBuilder.dbTypeList ) {
                if ( s.equalsIgnoreCase( info.getDsType() ) ) {
                    btype = true;
                    info.setDsType( s );
                    break;
                }
            }
            if ( !btype ) {
                return resMap.setErr( String.format( "类型%s不正确", info.getDsType() ) ).getResultMap();
            }
            if ( StringUtils.isBlank( info.getDsIp() ) ) {
                return resMap.setErr( "ip不能为空" ).getResultMap();
            }
            if ( info.getDsPort() == null ) {
                return resMap.setErr( "端口不能为空" ).getResultMap();
            }

            if (!JdbcOpBuilder.dbOscar.equalsIgnoreCase( info.getDsType() ) && StringUtils.isBlank( info.getDsDatabase() ) ) {
                return resMap.setErr( "数据库不能为空" ).getResultMap();
            }
            //}
            if ( JdbcOpBuilder.dbOracle.equalsIgnoreCase( info.getDsType() )|| JdbcOpBuilder.dbOscar.equalsIgnoreCase( info.getDsType() )|| JdbcOpBuilder.dbKingBase.equalsIgnoreCase( info.getDsType() ) || JdbcOpBuilder.dbKingBase8.equalsIgnoreCase( info.getDsType() ) || JdbcOpBuilder.dbOpenGauss.equalsIgnoreCase( info.getDsType() ) || JdbcOpBuilder.dbPostgreSql.equalsIgnoreCase( info.getDsType() ) ) {
                info.setDsSchema( "" );
            }
            else {
                //    if ( StringUtils.isBlank( info.getDsSchema() ) && StringUtils.isNotBlank( info.getDsDatabase() ) ) {
                info.setDsSchema( info.getDsDatabase() );
                //   }
            }

//            if (StringUtils.isBlank(info.getUsername())) {
//                return resMap.setErr("用户不能为空").getResultMap();
//            }

            String driver = JdbcOpBuilder.findDrive( info.getDsType() );
            String jdbcUrl = JdbcOpBuilder.genUrl( info.getDsType(), info.getDsIp(), info.getDsPort(), info.getDsDatabase() );
            info.setDsDriver( driver );
            info.setJdbcUrl( jdbcUrl );

            JdbcOpBuilder jb = new JdbcOpBuilder();
            try ( IJdbcOp jdbcOp = jb.withCatalog( info.getDsDatabase() ).withSchema( info.getDsSchema() ).withDbType( info.getDsType() )
                    .withDriver( info.getDsDriver() ).withUrl( info.getJdbcUrl() )
                    .withPassword( info.getPassword() ).withUser( info.getUsername() )
                    .build() ) {
                jdbcOp.setRaiseException( true );
                //jdbcOp.selectData( "select 1" );
                List<String> list = jdbcOp.listSchema( info.getDsDatabase(), info.getDsSchema() );
                if ( list == null ) {
                    return resMap.setErr( "数据源配置不正确" ).getResultMap();
                }
            }
            resMap.setSingleOk( info, "测试成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "测试失败" );
            logger.error( "测试异常:", ex );
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "数据源内容", notes = "数据源内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "语句元素id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    @ResponseBody
    public Object info( String id ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( StringUtils.isBlank( id ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }
            FDatasourceDo modelVo = dbSourceService.findById( id );
            resMap.setSingleOk( modelVo, "成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "查询失败" );
            logger.error( "查询异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除数据源", notes = "删除数据源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据源 id", dataType = "String", required = true, paramType = "query")
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

            Example exp = new Example( FDatasourceDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", idList );
            List<FDatasourceDo> list = dbSourceService.findByExample( exp );
            if ( list.size() != idList.size() ) {
                return resMap.setErr( "数据已变化，删除失败" ).getResultMap();
            }

            exp = new Example( TBydbDatabaseDo.class );
            criteria = exp.createCriteria();
            criteria.andIn( "dbsourceId", idList );
            Integer cnt = databaseService.findCountByExample( exp );
            if ( cnt != null && cnt > 0 ) {
                return resMap.setErr( "有数据源被使用，不能删除" ).getResultMap();
            }

            ObjectResp<String> retVal = apiTruModelService.delDbsource( idList, user.getTokenId() );
            if ( retVal.isSuccess() ) {
                dbSourceService.deleteList( list );
            }
            else {
                return retVal;
            }

//            String times = String.valueOf( System.currentTimeMillis() );
//            for ( FDatasourceDo info : list ) {
//                try {
//                    HashMap<String, Object> map = new HashMap<>();
//
//                    //String msg = JsonUtil.toJson(map);
//
//                    //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(user, info, msg,"删除-数据源" + times);
//                }
//                catch ( Exception e1 ) {
//                    resMap.setErr( "删除失败" );
//                    logger.error( "删除异常:", e1 );
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

    @ApiOperation(value = "获取管理组列表", notes = "获取管理组列表")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "currentPage", value = "当前页", dataType = "String", required = true, paramType = "query"),
            //@ApiImplicitParam(name = "pageSize", value = "页数", dataType = "String", required = true, paramType = "query"),
            //@ApiImplicitParam(name = "modelVo", value = "管理组信息", dataType = "TDatasourceDo", required = false, paramType = "body")
    })
    @RequestMapping(value = "/page", method = {RequestMethod.GET})
    @ResponseBody
    public Object page( FDatasourceDo modelVo, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( modelVo == null )
                modelVo = new FDatasourceDo();

            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            modelVo.setQryCond( ComUtil.chgLikeStr( modelVo.getQryCond() ) );
            modelVo.setDsName( ComUtil.chgLikeStr( modelVo.getDsName() ) );

            long findCnt = dbSourceService.findBeanCnt( modelVo );
            //modelVo.genPage();
            modelVo.genPage( findCnt );

            List<FDatasourceDo> list = dbSourceService.findBeanList( modelVo );

            resMap.setPageInfo( modelVo.getPageSize(), modelVo.getCurrentPage() );
            resMap.setOk( findCnt, list, "获取管理组列表成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取管理组列表失败" );
            logger.error( "获取管理组列表失败:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取管理组选择列表", notes = "获取管理组选择列表")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/option", method = {RequestMethod.GET})
    @ResponseBody
    public Object option() {
        ResponeMap resMap = this.genResponeMap();
        try {
            FDatasourceDo modelVo = new FDatasourceDo();
//            if( StringUtils.isNotBlank( dbId ) ){
//                modelVo.setDbId( dbId );
//            }
            List<FDatasourceDo> list = dbSourceService.findBeanList( modelVo );
            resMap.setSingleOk( list, "获取管理组选择列表成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取管理组选择列表失败" );
            logger.error( "获取管理组选择列表失败:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取数据源下表数据", notes = "根据数据源主键来获取数据源下所有表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据源id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "tableName", value = "表名", dataType = "String", required = false, paramType = "query")
    })
    @RequestMapping(value = {"/tablelistbyid"}, method = {RequestMethod.GET})
    @ResponseBody
    public Object tableListById( String id, String tableName ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            //List<String> list = new ArrayList<String>();
            FDatasourceDo info = dbSourceService.findById( id );
            if ( info == null ) {
                return resMap.setErr( "数据库配置不存在" ).getResultMap();
            }
//            String driver = JdbcOpBuilder.findDrive( info.getDsType() );
//            String jdbcUrl = JdbcOpBuilder.genUrl( info.getDsType(), info.getDsIp(), info.getDsPort(), info.getDsDatabase() );
//            info.setDsDriver( driver );
//            info.setJdbcUrl( jdbcUrl );

//            String tableName = tableName;
//            if(StringUtils.isBlank( dbsch )){
//                dbsch = info.getDsSchema();
//            }
            JdbcOpBuilder jb = new JdbcOpBuilder();
            List<Object> retList = new ArrayList<>();
            try ( IJdbcOp jdbcOp = jb.withCatalog( info.getDsDatabase() ).withSchema( info.getDsSchema() ).withDbType( info.getDsType() )
                    .withDriver( info.getDsDriver() ).withUrl( info.getJdbcUrl() )
                    .withPassword( info.getPassword() ).withUser( info.getUsername() )
                    .build() ) {
                List<JdbcTableInfo> tabList = jdbcOp.listTable( info.getDsDatabase(), info.getDsSchema() );
                if ( StringUtils.isNotBlank( tableName ) ) {
                    String tname = tableName.trim();
                    tabList = tabList.stream().filter( x -> x.getTablename().contains( tname ) ).collect( Collectors.toList() );
                }
                HashMap<String, String> nameMap = new HashMap<>();
                for ( JdbcTableInfo tabInfo1 : tabList ) {
                    if ( !nameMap.containsKey( tabInfo1.getSchemaname() ) ) {
                        List<Object> subList = new ArrayList<>();
                        nameMap.put( tabInfo1.getSchemaname(), tabInfo1.getSchemaname() );
                        for ( JdbcTableInfo tabInfo2 : tabList ) {
                            if ( tabInfo1.getSchemaname().equals( tabInfo2.getSchemaname() ) ) {
                                HashMap<Object, Object> dat = new HashMap<>();
                                dat.put( "tableComment", tabInfo2.getComment() );
                                dat.put( "tableName", tabInfo2.getTablename() );
                                dat.put( "tableSchema", tabInfo2.getSchemaname() );
                                // dat.put( "tablename", x.getTablename() );
                                //dat.put( "comment", x.getComment() );
                                //dat.put( "schemaname", x.getSchemaname() );
                                dat.put( "type", "table" );
                                subList.add( dat );
                            }
                        }
                        HashMap<Object, Object> dat = new HashMap<>();
                        //dat.put( "tableComment", tabInfo1.getComment() );
                        //dat.put( "tableName", tabInfo1.getTablename() );
                        dat.put( "tableSchema", tabInfo1.getSchemaname() );
                        // dat.put( "tablename", x.getTablename() );
                        //dat.put( "comment", x.getComment() );
                        //dat.put( "schemaname", x.getSchemaname() );
                        dat.put( "type", "database" );
                        dat.put( "children", subList );
                        retList.add( dat );
                    }
                }
                return resMap.setSingleOk( retList, "获取数据源下表成功" ).getResultMap();
            }
        }
        catch ( Exception ex ) {
            logger.error( "获取数据源下表失败:", ex );
            resMap.setErr( "获取数据源下表失败" );
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "获取数据库和模式", notes = "获取数据库和模式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据源id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = {"/schemalistbyid"}, method = {RequestMethod.GET})
    @ResponseBody
    public Object schemaListById( String id ) {
        ResponeMap resMap = this.genResponeMap();
        try {

            FDatasourceDo info = dbSourceService.findById( id );
            if ( info == null ) {
                return resMap.setErr( "数据库配置不存在" ).getResultMap();
            }

            JdbcOpBuilder jb = new JdbcOpBuilder();
            try ( IJdbcOp jdbcOp = jb.withCatalog( info.getDsDatabase() ).withSchema( info.getDsSchema() ).withDbType( info.getDsType() )
                    .withDriver( info.getDsDriver() ).withUrl( info.getJdbcUrl() )
                    .withPassword( info.getPassword() ).withUser( info.getUsername() )
                    .build() ) {
                List<String> retList = jdbcOp.listSchema( info.getDsDatabase(), info.getDsSchema() );
                List<String> sysSchema = jdbcOp.getSysSchema();
                if ( sysSchema != null ) {
                    List<String> collect = sysSchema.stream().map( x -> x.toUpperCase() ).collect( Collectors.toList() );
                    for ( int i = retList.size() - 1; i >= 0; i-- ) {
                        if ( collect.indexOf( retList.get( i ).toUpperCase() ) >= 0 ) {
                            retList.remove( i );
                        }
                    }
                }
                return resMap.setSingleOk( retList, "获取数据库和模式成功" ).getResultMap();
            }
        }
        catch ( Exception ex ) {
            logger.error( "获取数据库和模式失败:", ex );
            resMap.setErr( "获取数据库和模式失败" );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取表字段", notes = "获取表字段")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据源id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "tableName", value = "表名", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = {"/fieldtable"}, method = {RequestMethod.GET})
    public Object fieldTable( String id, String schema, String tableName ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            FDatasourceDo info = dbSourceService.findById( id );
            if ( info == null ) {
                return resMap.setErr( "内容不存在" ).getResultMap();
            }
//            String driver = JdbcOpBuilder.findDrive( info.getDsType() );
//            String jdbcUrl = JdbcOpBuilder.genUrl( info.getDsType(), info.getDsIp(), info.getDsPort(), info.getDsDatabase() );
//            info.setDsDriver( driver );
//            info.setJdbcUrl( jdbcUrl );

            String dbsch = schema;
            if ( StringUtils.isBlank( dbsch ) ) {
                dbsch = info.getDsSchema();
            }
            JdbcOpBuilder jb = new JdbcOpBuilder();
            try ( IJdbcOp jdbcOp = jb.withCatalog( info.getDsDatabase() ).withSchema( info.getDsSchema() ).withDbType( info.getDsType() )
                    .withDriver( info.getDsDriver() ).withUrl( info.getJdbcUrl() )
                    .withPassword( info.getPassword() ).withUser( info.getUsername() )
                    .build() ) {
                List<JdbcColumnInfo> columnlist = jdbcOp.listColumn( info.getDsDatabase(), dbsch, tableName );
                resMap.setSingleOk( columnlist, "字段数据" );
            }
        }
        catch ( Exception ex ) {
            logger.error( "获取表字段失败:", ex );
            resMap.setErr( "获取表字段失败" );
        }

        /*List<ColumnInfo> columnlist = new ArrayList<ColumnInfo>();
        try {
            if (tableName == null) {
                return resMap.setErr("表名不能为空").getResultMap();
            }
            TBydbDbSourceDo datasource = dbSourceService.findById(id);
            if (datasource == null) {
                return resMap.setErr("字段获取失败").getResultMap();
//            } else if (Constants.HBASE.equals(datasource.getDsType())) {
//                columnlist = new HBaseQueryTool(datasource).getColumns(tableName);
//            } else if (Constants.MONGODB.equals(datasource.getDsType())) {
//                columnlist = new MongoDBQueryTool(datasource).getColumns(tableName);
//            } else if (Constants.ODPS.equals(datasource.getDsType())) {
//                columnlist = new OdpsQueryTool(datasource).getColumns(tableName);
            } else {
//                try(BaseQueryTool queryTool = QueryToolFactory.getByDbType(datasource);) {
//                    columnlist = queryTool.getColumnNames(tableName, datasource.getDsType());
//                }
            }

            if (columnlist != null) {

                for (ColumnInfo columnInfo : columnlist) {
                    String type = columnInfo.getType();
                    if( StringUtils.isNotBlank( type )){
                        final int idx = type.lastIndexOf(".");
                        if( idx >=0 ){
                            type = type.substring( idx +1).toUpperCase();
                        }
                        columnInfo.setShortType( type );
                    }
                    else{
                        columnInfo.setShortType( "" );
                    }
                    if( StringUtils.isBlank( columnInfo.getComment() ) ){
                        columnInfo.setComment( columnInfo.getName() );
                    }
                }
                resMap.setSingleOk(columnlist, "字段数据");
            } else {
                resMap.setErr("字段获取失败");
            }
        } catch (Exception ex) {
            logger.error("测试连接失败:", ex);
            resMap.setErr("测试连接失败");
        }*/

        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取数据源与类型", notes = "获取数据源与类型")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/datasourceandtype", method = {RequestMethod.GET})
    @ResponseBody
    public Object dataSourceAndType( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setSingleOk( new ArrayList<>(), "请先登录" ).getResultMap();
            }
//            TBydbDcServerDo dcTmp = new TBydbDcServerDo();
//            dcTmp.setManageAccount(ud.getUserName());
//            dcTmp.setEnable( 1 );
//            List<TBydbDcServerDo> dcList = dcserverService.find(dcTmp);
//            if (dcList.size() == 0) {
//                return resMap.setSingleOk(new ArrayList<>(),"用户未指定节点").getResultMap();
//            }
            //TBydbDcServerDo dcDo = dcList.get(0);
            FDatasourceDo tmpDs = new FDatasourceDo();
            tmpDs.setEnable( 99 );
            tmpDs.setCreatorAccount( ud.getUserName() );

            List<BydbDatabaseSourceVo> dsList = dbSourceService.findBeanWithUsedFlag( tmpDs );

            List<SysDictDo> dictList = dictService.findVisibleLevel1DictByTopCode( "databasesourcetype" );
            List<Object> list = new ArrayList<>();

            HashMap<String, Object> dat = new HashMap<>();
            /*dat.put("id", "hetutype" );
            dat.put("dictCode","hetutype");
            dat.put("dictName","OpenLooKeng");
            dat.put("desc","OpenLooKeng");
            dat.put("type","insidedbtyepe");
            dat.put("tips","OpenLooKeng");*/

            List<Object> list2 = new ArrayList<>();
            HashMap<String, Object> dsMap = new HashMap<>();
            /*dsMap.put("id", dchetu );
            dsMap.put("dsName","OpenLooKeng");
            //dsMap.put("dsCode",Constants.dchetu);
            dsMap.put("type","insidedb");
            dsMap.put("desc","OpenLooKeng");
            dat.put("enable","1");
            dat.put("tips","OpenLooKeng");
            dsMap.put("useFlag",1);
            list2.add( dsMap );
            dat.put("children", list2);
            list.add( dat );*/

            List<BydbDatabaseSourceVo> delList = new ArrayList<>();

            for ( SysDictDo sysDictDo : dictList ) {

                dat = new HashMap<>();
                dat.put( "id", sysDictDo.getId() );
                dat.put( "dictCode", sysDictDo.getDictCode() );
                dat.put( "dictName", sysDictDo.getDictName() );
                dat.put( "desc", sysDictDo.getRemark() );
                dat.put( "type", "outsidedbtype" );
                dat.put( "tips", String.format( "%s(%s)\r\n%s", sysDictDo.getDictName(), sysDictDo.getDictCode(), sysDictDo.getRemark() ) );
//                if( "1".equals(sysDictDo.getDisplay() ) ) {
//                    dat.put("enable","1");
//                }
//                else if( "2".equals(sysDictDo.getDisplay() ) ) {
//                    dat.put("enable","2");
//                }
//                else{
//                    continue;
//                }
                list2 = new ArrayList<>();
                for ( BydbDatabaseSourceVo dsDo : dsList ) {
                    if ( sysDictDo.getDictCode().equals( dsDo.getDsType() ) ) {
                        dsMap = new HashMap<>();
                        dsMap.put( "id", dsDo.getId() );
                        dsMap.put( "dsName", dsDo.getDsName() );
                        //dsMap.put("dsCode",dsDo.getDsName());
                        dsMap.put( "type", "outsidedb" );
                        dsMap.put( "useFlag", StringUtils.isNotBlank( dsDo.getDbsourceId() ) );
                        dsMap.put( "desc", dsDo.getDsDesc() );
                        dsMap.put( "enable", dsDo.getEnable() );
                        dsMap.put( "tips", String.format( "%s:%d/%s\r\n%s", dsDo.getDsIp(), dsDo.getDsPort(), dsDo.getDsDatabase(), dsDo.getDsDesc() ) );

                        list2.add( dsMap );
                        delList.add( dsDo );
                    }
                }
                if ( list2.size() > 0 ) {
                    dat.put( "children", list2 );
                    list.add( dat );
                }
            }
            dsList.removeAll( delList );
            if ( dsList.size() > 0 ) {
                dat = new HashMap<>();
                dat.put( "id", "untyped" );
                dat.put( "dictCode", "未分类" );
                dat.put( "dictName", "未分类" );
                dat.put( "desc", "未分类" );
                dat.put( "type", "outsidedbtype" );
                dat.put( "tips", String.format( "未分类" ) );
                list2 = new ArrayList<>();
                for ( BydbDatabaseSourceVo dsDo : dsList ) {
                    dsMap = new HashMap<>();
                    dsMap.put( "id", dsDo.getId() );
                    dsMap.put( "dsName", dsDo.getDsName() );
                    //dsMap.put("dsCode",dsDo.getDsName());
                    dsMap.put( "type", "outsidedb" );
                    dsMap.put( "useFlag", StringUtils.isNotBlank( dsDo.getDbsourceId() ) );
                    dsMap.put( "desc", dsDo.getDsDesc() );
                    dsMap.put( "enable", dsDo.getEnable() );
                    dsMap.put( "tips", String.format( "%s:%d/%s\r\n%s", dsDo.getDsIp(), dsDo.getDsPort(), dsDo.getDsDatabase(), dsDo.getDsDesc() ) );
                    list2.add( dsMap );
                }
                //if (list2.size() > 0) {
                dat.put( "children", list2 );
                list.add( dat );
                //}
            }

            resMap.setSingleOk( list, "获取数据源类型列表成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取数据源类型列表失败" );
            logger.error( "获取数据源类型列表失败:", ex );
        }
        return resMap.getResultMap();
    }

    /*@ApiOperation(value = "获取内外部表结构", notes = "获取内外部表结构")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据源id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "tableNameId", value = "表名", dataType = "String", required = false, paramType = "query")
    })
    @RequestMapping(value = {"/dstable"}, method = {RequestMethod.GET})
    @ResponseBody
    public Object dsTable(String id, String tableNameId, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser(request);
            List<String> list = new ArrayList<String>();
            List<Object> dataList = new ArrayList<>();
            if(Constants.dchetu.equals( id ) ){
                TBydbDcServerDo dcTmp = new TBydbDcServerDo();
                dcTmp.setManageAccount(user.getUserName());
                List<TBydbDcServerDo> dcList = dcserverService.find(dcTmp);
                if (dcList.size() == 0) {
                    return resMap.setErr("用户未关联节点").getResultMap();
                } else if (dcList.size() > 1) {
                    String dcName = dcList.stream().map(x -> x.getDcName() + "(" + x.getDcCode() + ")").collect(Collectors.joining(","));
                    logger.error("用户{},关联多节点:{}", user.getUserName(), dcName);
                    resMap.setDebugeInfo("关联多节点:" + dcName);
                    return resMap.setErr("用户关联多节点").getResultMap();
                }
                TBydbDcServerDo dcDo = dcList.get(0);

                if( StringUtils.isNotBlank( tableNameId ) ){
                    TBydbFieldDo fieldTmp = new TBydbFieldDo();
                    fieldTmp.setDcId(dcDo.getId());
                    fieldTmp.setEnable( 1 );
                    fieldTmp.setObjectId( tableNameId );
                    final List<TBydbFieldDo> fieldList = fieldService.findBeanList(fieldTmp);
                    for (TBydbFieldDo fieldDo : fieldList) {

                        HashMap<String, Object> fldNode = new HashMap<>();
                        fldNode.put("type", "field");
                        fldNode.put("id", fieldDo.getId());
                        fldNode.put("relId", fieldDo.getId());
                        fldNode.put("name", String.format( "%s %s", fieldDo.getFieldName(), fieldDo.getFieldType() ) );
                        fldNode.put("fieldName", fieldDo.getFieldName());
                        fldNode.put("fieldType", fieldDo.getFieldType() );
                        fldNode.put("dataType", JdbcTypeToJavaTypeUtil.chgType( fieldDo.getFieldType() ));
                        fldNode.put("hasLeaf", false);
                        fldNode.put("tips", StringUtils.isNoneBlank( fieldDo.getChnName()) ? fieldDo.getChnName() : fieldDo.getFieldName());
                        dataList.add( fldNode );
                    }
                    return resMap.setSingleOk(dataList, "获取树结构成功").getResultMap();

                }

                TBydbCatalogTypeDo typeTmp = new TBydbCatalogTypeDo();
                typeTmp.setDcId(dcDo.getId());
                typeTmp.setPid("#NULL#");
                final List<TBydbCatalogTypeDo> typeList = catalogTypeService.findBeanList(typeTmp);

                TBydbDatabaseDo dbTmp = new TBydbDatabaseDo();
                dbTmp.setDcId(dcDo.getId());
                dbTmp.setEnable(1);
                final List<TBydbDatabaseDo> dsList = databaseService.findBeanList(dbTmp);

                TBydbSchemaDo schemaTmp = new TBydbSchemaDo();
                schemaTmp.setDcId( dcDo.getId());
                schemaTmp.setEnable( 1 );
                final List<TBydbSchemaDo> schemaList = schemaService.findBeanList(schemaTmp);

                TBydbObjectDo objectTmp = new TBydbObjectDo();
                objectTmp.setDcId( dcDo.getId());
                objectTmp.setEnable( 1 );
                final List<TBydbObjectDo> tabList = objectService.findBeanList(objectTmp);

                HashMap<String,Object> node = new HashMap<>();
                node.put("type","root");
                node.put("id",dcDo.getId());
                node.put("relId",dcDo.getId());
                node.put("name",dcDo.getDcName());
                node.put("hasLeaf",true);
                node.put( "tips", StringUtils.isNoneBlank( dcDo.getDcName()) ?dcDo.getDcName():dcDo.getDcCode()  );

                List<Object> list1 = new ArrayList<>();
                for (TBydbCatalogTypeDo typeDo : typeList) {
                    HashMap<String,Object> fn = new HashMap<>();
                    fn.put("type","folder");
                    fn.put("id",typeDo.getId());
                    fn.put("relId",typeDo.getId());
                    fn.put("name",typeDo.getTypeName());
                    fn.put("hasLeaf",true);
                    fn.put( "tips",  typeDo.getTypeName() );
                    List<Object> dbNodeList = new ArrayList<>();
                    for (TBydbDatabaseDo dsDo : dsList) {
                        if( typeDo.getId().equals( dsDo.getCatalogType() ) ){
                            HashMap<String,Object> dbNode = new HashMap<>();
                            dbNode.put("type","db");
                            dbNode.put("id",dsDo.getId());
                            dbNode.put("relId",dsDo.getId());
                            dbNode.put("catalogType",dsDo.getCatalogType());
                            dbNode.put("name",dsDo.getDcDbName());
                            dbNode.put("hasLeaf",true);
                            dbNode.put( "tips", StringUtils.isNoneBlank(dsDo.getDbChnName())?dsDo.getDbChnName():dsDo.getDbName() );

                            List<Object> schNodeList = schemaList.stream().filter(x -> dsDo.getId().equals(x.getDbId())).map(
                                    x -> {
                                        HashMap<String, Object> schNode = new HashMap<>();
                                        schNode.put("type", "schema");
                                        schNode.put("id", x.getId());
                                        schNode.put("relId", x.getId());
                                        schNode.put("name", x.getSchemaName());
                                        schNode.put("hasLeaf", true);
                                        schNode.put("tips", StringUtils.isNoneBlank( x.getSchemaChnName())?x.getSchemaChnName(): x.getSchemaName());
                                        List<HashMap<String, Object>> tabNodeList = tabList.stream().filter(y -> x.getId().equals(y.getSchemaId())).map(y -> {
                                            HashMap<String, Object> tabNode = new HashMap<>();
                                            tabNode.put("type", "table");
                                            tabNode.put("id", y.getId());
                                            tabNode.put("relId", y.getId());
                                            tabNode.put("name", y.getObjectName());
                                            tabNode.put("serverObjectCode", y.getObjFullName());
                                            tabNode.put("dcObjectCode", y.getObjFullName().substring(dcDo.getDcCode().length()+1));
                                            tabNode.put("hasLeaf", true);
                                            tabNode.put("tips", StringUtils.isNoneBlank( y.getObjChnName())?y.getObjChnName(): y.getObjectName());
                                            return tabNode;
                                        }).collect(Collectors.toList());
                                        if (tabNodeList.size() > 0) {
                                            schNode.put("children", tabNodeList);
                                            return schNode;
                                        }
                                        return null;
                                    }
                            ).filter( x -> x!= null).collect(Collectors.toList());
                            if( schNodeList.size()>0 ){
                                dbNode.put("children", schNodeList);
                                dbNodeList.add( dbNode );
                            }
                        }
                    }
                    if( dbNodeList.size()>0){
                        fn.put( "children", dbNodeList );
                        list1.add( fn );
                    }
                }

                //List<Object> dsNodeList = new ArrayList<>();
                for (TBydbDatabaseDo dsDo : dsList) {
                    if( StringUtils.isBlank( dsDo.getCatalogType() ) ){
                        HashMap<String,Object> dbNode = new HashMap<>();
                        dbNode.put("type","db");
                        dbNode.put("id",dsDo.getId());
                        dbNode.put("relId",dsDo.getId());
                        dbNode.put("catalogType",dsDo.getCatalogType());
                        dbNode.put("name",dsDo.getDcDbName());
                        dbNode.put("hasLeaf",true);
                        dbNode.put( "tips", StringUtils.isNoneBlank( dsDo.getDbChnName())?dsDo.getDbChnName():dsDo.getDbName() );

                        List<Object> schNodeList = schemaList.stream().filter(x -> dsDo.getId().equals(x.getDbId())).map(
                                x -> {
                                    HashMap<String, Object> schNode = new HashMap<>();
                                    schNode.put("type", "schema");
                                    schNode.put("id", x.getId());
                                    schNode.put("relId", x.getId());
                                    schNode.put("name", x.getSchemaName());
                                    schNode.put("hasLeaf", true);
                                    schNode.put("tips", StringUtils.isNoneBlank( x.getSchemaChnName())?x.getSchemaChnName(): x.getSchemaName());
                                    List<HashMap<String, Object>> tabNodeList = tabList.stream().filter(y -> x.getId().equals(y.getSchemaId())).map(y -> {
                                        HashMap<String, Object> tabNode = new HashMap<>();
                                        tabNode.put("type", "table");
                                        tabNode.put("id", y.getId());
                                        tabNode.put("relId", y.getId());
                                        tabNode.put("name", y.getObjectName());
                                        tabNode.put("serverObjectCode", y.getObjFullName());
                                        tabNode.put("dcObjectCode", y.getObjFullName().substring(dcDo.getDcCode().length()+1));
                                        tabNode.put("hasLeaf", true);
                                        tabNode.put("tips", StringUtils.isNoneBlank( y.getObjChnName())?y.getObjChnName(): y.getObjectName());
                                        return tabNode;
                                    }).collect(Collectors.toList());
                                    if (tabNodeList.size() > 0) {
                                        schNode.put("children", tabNodeList);
                                        return schNode;
                                    }
                                    return null;
                                }
                        ).filter( x -> x!= null).collect(Collectors.toList());
                        if( schNodeList.size()>0 ){
                            dbNode.put("children", schNodeList);
                            list1.add( dbNode );
                        }
                    }
                }
                node.put("children", list1);

                dataList.add( node );

                resMap.setSingleOk(dataList, "获取树结构成功");
            }
            else {
                TBydbDbSourceDo datasource = dbSourceService.findById(id);
                if (datasource == null) {
                    return resMap.setErr("数据源不存在").getResultMap();
                }
                //queryTool组装
                if (datasource == null) {
                    list = null;
//                } else if (Constants.HBASE.equals(datasource.getDsType())) {
//                    list = new HBaseQueryTool(datasource).getTableNames();
//                } else if (Constants.MONGODB.equals(datasource.getDsType())) {
//                    list = new MongoDBQueryTool(datasource).getCollectionNames(datasource.getDsDatabase());
//                } else if (Constants.ODPS.equals(datasource.getDsType())) {
//                    list = new OdpsQueryTool(datasource).getTableNames();
                } else {
//                    try (BaseQueryTool qTool = QueryToolFactory.getByDbType(datasource)) {
//                        list = qTool.getTableNames();
//                    }
                }
                if (list != null) {
                    HashMap<String, Object> node = new HashMap<>();
                    node.put("type", "root");
                    node.put("id", datasource.getId());
                    node.put("relId", datasource.getId());
                    node.put("name", datasource.getDsName());
                    node.put("hasLeaf", true);
                    node.put("tips", String.format("%s(%s)", datasource.getDsDesc(), datasource.getDsName()));

                    List<Object> list1 = new ArrayList<>();
                    for (String tableName : list) {
                        HashMap<String, Object> fn = new HashMap<>();
                        fn.put("type", "table");
                        fn.put("id", tableName);
                        fn.put("relId", tableName);
                        fn.put("name", tableName);
                        fn.put("fullCode", tableName);
                        fn.put("hasLeaf", true);
                        fn.put("tips", String.format("%s", tableName));
                        list1.add(fn);
                    }
                    node.put("children", list1);
                    dataList.add(node);

                    resMap.setSingleOk(dataList, "获取树结构成功");
                } else {
                    resMap.setErr("表数据列获取失败");
                }
            }
        } catch (Exception ex) {
            logger.error("测试连接失败:", ex);
            resMap.setErr("测试连接失败");
        }
        return resMap.getResultMap();
    }*/

}
