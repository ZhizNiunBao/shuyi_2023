package cn.bywin.business.controller;

import cn.bywin.business.bean.bydb.TBydbDatabaseDo;
import cn.bywin.business.bean.bydb.TBydbDatasetDo;
import cn.bywin.business.bean.bydb.TBydbFieldDo;
import cn.bywin.business.bean.bydb.TBydbObjectDo;
import cn.bywin.business.bean.bydb.TBydbSchemaDo;
import cn.bywin.business.bean.bydb.TTruFavouriteObjectDo;
import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.bydb.TTruModelElementDo;
import cn.bywin.business.bean.bydb.TTruModelObjectDo;
import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.view.VBydbObjectVo;
import cn.bywin.business.bean.view.bydb.BydbObjectFieldsVo;
import cn.bywin.business.bean.view.bydb.BydbTabDatasetUseProjVo;
import cn.bywin.business.bean.view.bydb.DigitalAssetVo;
import cn.bywin.business.bean.view.bydb.TBydbModelVo;
import cn.bywin.business.bean.view.federal.NodePartyView;
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
import cn.bywin.business.service.bydb.BydbDatabaseService;
import cn.bywin.business.service.bydb.BydbDatasetService;
import cn.bywin.business.service.bydb.BydbFieldService;
import cn.bywin.business.service.bydb.BydbObjectService;
import cn.bywin.business.service.bydb.BydbSchemaService;
import cn.bywin.business.service.bydb.DigitalAssetService;
import cn.bywin.business.service.bydb.TruFavouriteObjectService;
import cn.bywin.business.service.bydb.TruModelComponentService;
import cn.bywin.business.service.bydb.TruModelElementRelService;
import cn.bywin.business.service.bydb.TruModelElementService;
import cn.bywin.business.service.bydb.TruModelFieldService;
import cn.bywin.business.service.bydb.TruModelFolderService;
import cn.bywin.business.service.bydb.TruModelObjectService;
import cn.bywin.business.service.bydb.TruModelService;
import cn.bywin.business.service.federal.DataApproveService;
import cn.bywin.business.service.federal.DataPartyService;
import cn.bywin.business.service.federal.DataSourceService;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.service.olk.OlkDigitalAssetService;
import cn.bywin.business.service.olk.OlkModelElementService;
import cn.bywin.business.service.olk.OlkModelObjectService;
import cn.bywin.business.service.olk.OlkModelService;
import cn.bywin.business.service.olk.OlkObjectService;
import cn.bywin.business.service.system.SysUserService;
import cn.bywin.business.util.JdbcTypeToJavaTypeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description ????????????????????????
 * @Author me
 * @Date 2022-04-11
 */
@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        maxAge = 3600)
@RestController
@Api(tags = "????????????????????????")
@RequestMapping("/trumodelapi")
public class TruModelApiController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Autowired
    private TruModelService truModelService;

    @Autowired
    private OlkModelService olkModelService;

    @Autowired
    private TruModelObjectService truModelObjectService;

    @Autowired
    private OlkModelObjectService olkModelObjectService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private NodePartyService nodePartyService;

    @Autowired
    private DataSourceService dbSourceService;

    @Autowired
    private BydbDatabaseService databaseService;

    @Autowired
    private BydbSchemaService schemaService;

    @Autowired
    private BydbObjectService bydbTableService;

    @Autowired
    private OlkObjectService olkTableService;

    @Autowired
    private DigitalAssetService digitalAssetService;

    @Autowired
    private OlkDigitalAssetService olkDigitalAssetService;

    @Autowired
    private BydbObjectService bydbObjectService;

    @Autowired
    private BydbFieldService fieldService;

    @Autowired
    private TruModelService bydbModelService;

    @Autowired
    private TruModelComponentService truModelComponentService;
    @Autowired
    private TruModelFieldService truModelFieldService;

    @Autowired
    private TruModelElementService truModelElementService;
    @Autowired
    private TruModelElementRelService truModelElementRelService;
    @Autowired
    private BydbDatabaseService bydbDatabaseService;
    @Autowired
    private BydbDatasetService datasetService;

    @Autowired
    private TruModelFolderService folderService;
    @Autowired
    private DataPartyService dataPartyService;

    @Autowired
    private TruFavouriteObjectService favouriteObjectService;

//    @Autowired
//    private TruApplyObjectService applyObjectService;

    @Autowired
    private DataApproveService applyObjectService;

    @Autowired
    private DataApproveService dataApproveService;


    @Autowired
    private OlkModelElementService olkModelElementService;


    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/nodepartylist", method = {RequestMethod.POST})
    public Object nodePartyList( @RequestBody FNodePartyDo info, HttpServletRequest request ) {
        ResponeMap result = genResponeMap();
        try {
            MyBeanUtils.chgBeanLikeProperties( info, "name", "qryCond" );
            info.genPage();
            long cnt = nodePartyService.findBeanCnt( info );
            List<NodePartyView> data = nodePartyService.findVNodePartyList( info );
            result.setPageInfo( info.getPageSize(), info.getCurrentPage() );

            data.stream().forEach( e -> {
                e.setIp( "*" );
                e.setPort( 0 );
            } );
            for ( NodePartyView datum : data ) {
                Example exp = new Example( TBydbObjectDo.class );
                Example.Criteria criteria = exp.createCriteria();
                criteria.andEqualTo( "nodePartyId", datum.getId() )
                        .andEqualTo( "enable", 1 );

                int tabCnt = bydbTableService.findCountByExample( exp );
                datum.setDataPartyCnt( "" + tabCnt );
            }
            result.setOk( cnt, data, "????????????????????????" );
        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            result.setErr( "????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/allnodeparty", method = {RequestMethod.POST})
    public List<NodePartyView> allNodeParty( HttpServletRequest request ) {
        //ResponeMap result = genResponeMap();
        try {
            FNodePartyDo info = new FNodePartyDo();
            List<NodePartyView> list = nodePartyService.findVNodePartyList( info );
            list.stream().forEach( e -> {
                e.setIp( "*" );
                e.setPort( 0 );
            } );
            return list;
        }
        catch ( Exception e ) {
            logger.error( "????????????????????????", e );
            //result.setErr("????????????");
        }
        return null;
    }

    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    @RequestMapping(value = "/nodeuserlist", method = {RequestMethod.POST})
    public Map<String, Object> nodeUserList( @RequestBody SysUserDo info, HttpServletRequest request ) {
        ResponeMap result = genResponeMap();
        try {
            if ( StringUtils.isNotBlank( info.getId() ) ) {
                List<String> userIdList = Arrays.asList( info.getId().split( "(,|\\s)+" ) ).stream().filter( StringUtils::isNotBlank ).distinct().collect( Collectors.toList() );
                info.setId( null );
                info.setIdList( userIdList );
            }
            List<SysUserDo> list = sysUserService.findBeanList( info );
            list.stream().forEach( e -> {
                e.setPassword( "" );
            } );
            result.setOk( list.size(), list, "??????????????????" );
        }
        catch ( Exception e ) {
            logger.error( "??????????????????", e );
            result.setErr( "??????????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "???????????????", notes = "???????????????")
    @RequestMapping(value = "/syndbsource", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object synDbsource( @RequestBody FDatasourceDo info ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            FDatasourceDo dbsource = dbSourceService.findById( info.getId() );
            //info.setShareTime( ComUtil.getCurTimestamp() );
            if ( dbsource != null ) {
                dbSourceService.updateBean( info );
            }
            else {
                dbSourceService.insertBean( info );
            }
            result.setOk( "?????????????????????" );
        }
        catch ( Exception e ) {
            logger.error( "?????????????????????", e );
            result.setErr( "?????????????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "???????????????", notes = "???????????????")
    @RequestMapping(value = "/deldbsource", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String, Object> delDbsource( @RequestBody List<String> idList ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            if ( idList == null || idList.size() == 0 ) {
                return result.setErr( "id????????????" ).getResultMap();
            }
            Example exp = new Example( FDatasourceDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", idList );
            List<FDatasourceDo> list = dbSourceService.findByExample( exp );
            dbSourceService.deleteList( list );

            result.setOk( "?????????????????????" );
        }
        catch ( Exception e ) {
            logger.error( "?????????????????????", e );
            result.setErr( "?????????????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "???????????????", notes = "???????????????")
    @RequestMapping(value = "/dbsourceinfo", method = {RequestMethod.POST})
    public FDatasourceDo dbsourceInfo( String id ) {
        try {
            if ( StringUtils.isBlank( id ) ) {
                return null;
            }
            FDatasourceDo dbsource = dbSourceService.findById( id );
            return dbsource;
        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            return null;
        }
    }

    @ApiOperation(value = "???????????????", notes = "???????????????")
    @RequestMapping(value = "/dbsourceList", method = {RequestMethod.POST})
    public List<FDatasourceDo> dbsourceList( @RequestBody FDatasourceDo info ) {
        try {
            List<FDatasourceDo> data = dbSourceService.findBeanList( info );
            return data;
        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            return null;
        }
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/syndatabase", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object synDatabase( @RequestBody TBydbDatabaseDo info ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            TBydbDatabaseDo database = databaseService.findById( info.getId() );
            if ( database != null ) {
                databaseService.updateBean( info );
            }
            else {
                databaseService.insertBean( info );
            }
            result.setOk( "??????????????????" );
        }
        catch ( Exception e ) {
            logger.error( "??????????????????", e );
            result.setErr( "??????????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/deldatabase", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String, Object> delDatabase( String id ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            if ( StringUtils.isBlank( id ) ) {
                return result.setErr( "id????????????" ).getResultMap();
            }
            TBydbDatabaseDo database = databaseService.findById( id );
            if ( database == null ) {
                return result.setOk( "???????????????" ).getResultMap();
            }
//            Example exp = new Example( TBydbDatabaseDo.class );
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andEqualTo( "id", id );
//            List<TBydbDatabaseDo> list = databaseService.findByExample( exp );

//            cnt = list.size();
//            if ( cnt == 0 ) {
//                return resMap.setErr( "?????????????????????" ).getResultMap();
//            }
//            if ( cnt != split.size() ) {
//                return resMap.setErr( "??????????????????????????????" ).getResultMap();
//            }

            String ids = "'" + id + "'";
            Example exp = new Example( TTruModelObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andCondition( " real_obj_id in (select a.id from t_bydb_object a, t_bydb_database b where a.db_id =b.id  and b.id in( " + ids + ") )" );
            int cnt = truModelObjectService.findCountByExample( exp );
            if ( cnt > 0 ) {
                return result.setErr( "??????????????????????????????" ).getResultMap();
            }
            databaseService.deleteWithOther( database );
            result.setOk( "??????????????????" );
        }
        catch ( Exception ex ) {
            result.setErr( "????????????" );
            logger.error( "????????????:", ex );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/databaseinfo", method = {RequestMethod.POST})
    public TBydbDatabaseDo databaseInfo( String id ) {
        try {
            //resMap.initSingleObject();
            if ( StringUtils.isBlank( id ) ) {
                return null;
            }
            TBydbDatabaseDo database = databaseService.findById( id );
            return database;
        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            return null;
        }
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/databaseList", method = {RequestMethod.POST})
    public List<TBydbDatabaseDo> databaseList( @RequestBody TBydbDatabaseDo info ) {
        try {
            List<TBydbDatabaseDo> data = databaseService.findBeanList( info );
            return data;
        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            return null;
        }
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/synschema", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object synSchema( @RequestBody TBydbSchemaDo info ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            TBydbSchemaDo schema = schemaService.findById( info.getId() );
            if ( schema != null ) {
                schemaService.updateBean( info );
            }
            else {
                schemaService.insertBean( info );
            }
            result.setOk( "???????????????" );
        }
        catch ( Exception e ) {
            logger.error( "???????????????", e );
            result.setErr( "???????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/delschema", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String, Object> delSchema( @RequestBody List<String> idList, String notCheck ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            if ( idList == null || idList.size() == 0 ) {
                return result.setErr( "id??????" ).getResultMap();
            }

//            Example exp = new Example( TBydbObjectDo.class );
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andIn( "schemaId", idList );
//            //criteria.andCondition( " id in(select table_id from t_bydb_ds_entity where table_id is not null ) ");
//            int cnt = tableService.findCountByExample( exp );
//            if ( cnt > 0 ) {
//                return resMap.setErr( "??????????????????????????????????????????" ).getResultMap();
//            }

            Example exp = new Example( TBydbSchemaDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", idList );
            //criteria.andEqualTo( "dcId", user.getUserName() );
            List<TBydbSchemaDo> list = schemaService.findByExample( exp );

            if ( !"1".equals( notCheck ) ) {
                String ids = "'" + StringUtils.join( idList, "','" ) + "'";
                exp = new Example( TTruModelObjectDo.class );
                criteria = exp.createCriteria();
                criteria.andCondition( " real_obj_id in (select a.id from t_bydb_object a, t_bydb_schema b where a.schema_id =b.id  and b.id in( " + ids + ") )" );
                int cnt = truModelObjectService.findCountByExample( exp );
                if ( cnt > 0 ) {
                    return result.setErr( "??????????????????????????????" ).getResultMap();
                }
            }
            schemaService.deleteWhithRel( list );

            result.setOk( "???????????????" );
        }
        catch ( Exception e ) {
            logger.error( "???????????????", e );
            result.setErr( "???????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/schemainfo", method = {RequestMethod.POST})
    public TBydbSchemaDo schemaInfo( String id ) {
        try {
            if ( StringUtils.isBlank( id ) ) {
                return null;
            }
            TBydbSchemaDo info = schemaService.findById( id );
            return info;
        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            return null;
        }
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/schemaList", method = {RequestMethod.POST})
    public List<TBydbSchemaDo> schemaList( @RequestBody TBydbSchemaDo info ) {
        try {
            List<TBydbSchemaDo> data = schemaService.findBeanList( info );
            return data;
        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            return null;
        }
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/syntable", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object synTable( @RequestBody List<BydbObjectFieldsVo> bofList ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            bydbTableService.saveWithFields( bofList );
            result.setOk( "???????????????" );
        }
        catch ( Exception e ) {
            logger.error( "???????????????", e );
            result.setErr( "???????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/deltable", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String, Object> delTable( @RequestBody List<String> idList ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            if ( idList == null || idList.size() == 0 ) {
                return result.setErr( "id??????" ).getResultMap();
            }
            Example exp = new Example( TBydbObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", idList );

            List<TBydbObjectDo> list = bydbObjectService.findByExample( exp );
            int cnt = list.size();
            if ( cnt == 0 ) {
                return result.setOk( "?????????????????????" ).getResultMap();
            }

            exp = new Example( TTruModelObjectDo.class );
            criteria = exp.createCriteria();
            criteria.andIn( "realObjId", idList );
            cnt = truModelObjectService.findCountByExample( exp );
            if ( cnt > 0 ) {
                return result.setErr( "??????????????????????????????" ).getResultMap();
            }
            //dbsource.setEnable(  0 );
            bydbTableService.deleteWhithOthers( list );

            result.setOk( "???????????????" );
        }
        catch ( Exception e ) {
            logger.error( "???????????????", e );
            result.setErr( "???????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/tableinfo", method = {RequestMethod.POST})
    public TBydbObjectDo tableInfo( String id ) {
        try {

            if ( StringUtils.isBlank( id ) ) {
                return null;
            }
            TBydbObjectDo table = bydbTableService.findById( id );
            return table;
        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            return null;
        }
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/tablewithsubinfo", method = {RequestMethod.POST})
    public Object tableWithSubInfo( String id ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            TBydbObjectDo info = bydbTableService.findById( id );
            BydbObjectFieldsVo data = new BydbObjectFieldsVo();
            MyBeanUtils.copyBeanNotNull2Bean( info, data );
            List<TBydbFieldDo> fieldList = fieldService.selectByObjectId( info.getId() );
            data.setFieldList( fieldList );
            result.setSingleOk( data, "????????????" );
            return result.getResultMap();
        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            result.setErr( "????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/tabledbsource", method = {RequestMethod.POST})
    public Map<String, Object> tableDbSource( String id ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            TBydbObjectDo info = bydbTableService.findById( id );
            if ( info == null ) {
                return result.setErr( "???????????????" ).getResultMap();
            }
            FDatasourceDo dbSource = dbSourceService.findByDatabaseId( info.getDbId() );
            result.setSingleOk( dbSource, "????????????" );
            return result.getResultMap();
        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            result.setErr( "????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    @RequestMapping(value = "/tabdatasetuseproj", method = {RequestMethod.POST})
    public Map<String, Object> tabDataSetUseProj( String id ) {
        ResponeMap result = genResponeMap();
        try {
            //result.initSingleObject();
            if ( StringUtils.isBlank( id ) ) {
                return result.setErr( "id??????" ).getResultMap();
            }
            TBydbObjectDo objectDo = bydbTableService.findById( id );
            if ( objectDo == null ) {
                return result.setErr( "???????????????" ).getResultMap();
            }
            List<FNodePartyDo> all = nodePartyService.findAll();
            Map<String, FNodePartyDo> npMap = all.stream().collect( Collectors.toMap( x -> x.getId(), x -> x ) );

            TTruModelObjectDo moTmp = new TTruModelObjectDo();
            moTmp.setRealObjId( id );
            List<TTruModelObjectDo> moList = truModelObjectService.find( moTmp );

            List<String> idList = moList.stream().map( x -> x.getModelId() ).distinct().collect( Collectors.toList() );
            if ( idList.size() == 0 ) {
                return result.setErr( "?????????????????????" ).getResultMap();
            }

            Example exp = new Example( TTruModelDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", idList );
            List<TTruModelDo> list = truModelService.findByExample( exp );
            List<BydbTabDatasetUseProjVo> retList = new ArrayList<>();
            for ( TTruModelDo modelDo : list ) {
                BydbTabDatasetUseProjVo dup = new BydbTabDatasetUseProjVo();
                dup.setId( ComUtil.genId() );
                dup.setObjectId( objectDo.getId() );
                dup.setObjChnName( objectDo.getObjChnName() );
                dup.setObjectName( objectDo.getObjectName() );
                dup.setProjId( modelDo.getId() );
                dup.setProjName( modelDo.getName() );
                dup.setProjUserName( modelDo.getCreatorName() );
                dup.setProjUserId( modelDo.getCreatorId() );
                dup.setProjUserAccount( modelDo.getCreatorAccount() );
                dup.setNodePartyId( modelDo.getNodePartyId() );
                FNodePartyDo nodePartyDo = npMap.get( modelDo.getNodePartyId() );
                dup.setNodePartyName( nodePartyDo.getName() );
                retList.add( dup );
            }
            result.setSingleOk( retList, "????????????" );
            return result.getResultMap();
        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            result.setErr( "????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    @RequestMapping(value = "/tablelist", method = {RequestMethod.POST})
    public Object tableList( @RequestBody TBydbObjectDo info ) {
        ResponeMap result = genResponeMap();
        try {
            MyBeanUtils.chgBeanLikeAndOtherStringEmptyToNull( info, "qryCond", "objectName", "objChnName" );
            long beanCnt = bydbTableService.findBeanCnt( info );
            info.genPage( beanCnt );
            List<TBydbObjectDo> list = bydbTableService.findBeanList( info );
            result.setOk( beanCnt, list, "????????????" );
            return result.getResultMap();
        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            result.setErr( "????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    @RequestMapping(value = "/pmtablelist", method = {RequestMethod.POST})
    public Object pmtableList( @RequestBody VBydbObjectVo info ) {
        ResponeMap result = genResponeMap();
        try {
            MyBeanUtils.chgBeanLikeAndOtherStringEmptyToNull( info, "qryCond", "objectName", "objChnName" );
            if ( StringUtils.isNotBlank( info.getId() ) ) {
                info.setIdList( Arrays.asList( info.getId().split( "," ) ) );
                info.setId( null );
            }
            long beanCnt = bydbTableService.findNodeBeanCnt( info );
            if ( info.getPageSize() != null ) {
                info.genPage( beanCnt );
            }
            List<VBydbObjectVo> list = bydbTableService.findNodeBeanList( info );
            result.setOk( beanCnt, list, "????????????" );
            return result.getResultMap();
        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            result.setErr( "????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "pms??????????????????????????????", notes = "pms??????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qryCond", value = "????????????", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dataType", value = "????????????", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "deptNo", value = "????????????", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "ssj", value = "???????????? day week month year", dataType = "String", required = false, paramType = "query"),
            //@ApiImplicitParam(name = "sdt1", value = "????????????", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "??????", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", dataType = "Integer", required = false, paramType = "query")
    })
    @RequestMapping(value = "/digitalassetsearchlist", method = {RequestMethod.POST})
    @ResponseBody
    public Object digitalAssetSearchList( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            //UserDo user = LoginUtil.getUser(request);
            DigitalAssetVo modelVo = new DigitalAssetVo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            logger.debug( JsonUtil.toSimpleJson( hru.getAllParaData() ) );

            new PageBeanWrapper( modelVo, hru );

            modelVo.setQryCond( ComUtil.chgLikeStr( modelVo.getQryCond() ) );
            //modelVo.setDbName(ComUtil.chgLikeStr(modelVo.getDbName()));
            //modelVo.setOtherId( user.getUserName() );
            modelVo.setScatalog( "db" );
            if ( "pub".equalsIgnoreCase( modelVo.getDataType() ) ) {
                //modelVo.setPrivFlag( 0 );
            }
            else if ( "priv".equalsIgnoreCase( modelVo.getDataType() ) ) {
                //modelVo.setPrivFlag( 1 );
                //modelVo.setDcId( user.getUserName() );
            }
//            else if( "priv".equalsIgnoreCase(modelVo.getDataType())){
//                modelVo.setPrivFlag( 1 );
//            }
            else if ( "favorite".equalsIgnoreCase( modelVo.getDataType() ) ) {

            }
            else if ( "grant".equalsIgnoreCase( modelVo.getDataType() ) ) {

            }
            else {// ( "newly".equalsIgnoreCase( modelVo.getDataType()))
                /*String ssj = hru.getNvlPara("ssj");
                Calendar cal =  null ;
                if( "day".equalsIgnoreCase( ssj ) ){
                    cal = Calendar.getInstance();
                }
                else if( "week".equalsIgnoreCase( ssj ) ){
                    cal = Calendar.getInstance();
                    //cal.add( Calendar.DATE, -6 );
                    cal.setTime( ComUtil.getFirstDayOfWeek( cal.getTime() ));
                }
                else if( "month".equalsIgnoreCase( ssj ) ){
                    cal = Calendar.getInstance();
                    cal.set( Calendar.DAY_OF_MONTH, 1 );
                }
                else if( "year".equalsIgnoreCase( ssj ) ){
                    cal = Calendar.getInstance();
                    cal.set(Calendar.MONTH ,1 );
                    cal.set( Calendar.DAY_OF_MONTH, 1 );
                }
                else if( "newly".equalsIgnoreCase(modelVo.getDataType())){
                    cal = Calendar.getInstance();
                    cal.add( Calendar.DAY_OF_WEEK,-7);
                }
                if( cal != null){
                    modelVo.setSdt1( ComUtil.strToDate( ComUtil.dateToStr( cal.getTime(),ComUtil.shortDtFormat),ComUtil.shortDtFormat));
                }*/
            }

            long findCnt = digitalAssetService.findBeanCnt( modelVo );
            modelVo.genPage( findCnt );

            List<DigitalAssetVo> list = digitalAssetService.findBeanList( modelVo );
            for ( DigitalAssetVo digitalAssetVo : list ) {
                if ( StringUtils.isBlank( digitalAssetVo.getObjChnName() ) ) {
                    digitalAssetVo.setObjChnName( digitalAssetVo.getObjectName() );
                }
            }

            resMap.setPageInfo( modelVo.getPageSize(), modelVo.getCurrentPage() );
            resMap.setOk( findCnt, list, "??????????????????????????????" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "??????????????????????????????" );
            logger.error( "??????????????????????????????:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "??????????????????????????????", notes = "??????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/digitalassettabfieldlist", method = {RequestMethod.GET})
    @ResponseBody
    public Map<String, Object> digitalAssetTabFieldList( String id ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( StringUtils.isBlank( id ) ) {
                return resMap.setErr( "id????????????" ).getResultMap();
            }
            //if(id.startsWith("db")){
            //String id1 = id.substring(2);
            TBydbObjectDo objectDo = bydbObjectService.findById( id );
            if ( objectDo != null ) {
                TBydbFieldDo tmp = new TBydbFieldDo();
                tmp.setObjectId( objectDo.getId() );
                tmp.setEnable( 1 );
                List<TBydbFieldDo> beanList = fieldService.findBeanList( tmp );
                List<Object> list = beanList.stream().map( x -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put( "id", x.getId() );
                    map.put( "columnName", x.getFieldName() );
                    //map.put("chgStatement", x.getChgStatement());
                    map.put( "chgStatement", "" );
                    map.put( "orgType", x.getFieldType() );
                    map.put( "columnType", JdbcTypeToJavaTypeUtil.chgType( x.getFieldType() ) );
                    //map.put("columnType",  x.getFieldType() );
                    map.put( "chnName", x.getChnName() );
                    map.put( "tips", StringUtils.isBlank( x.getChnName() ) ? x.getChnName() : x.getFieldName() );
                    return map;
                } ).collect( Collectors.toList() );
                resMap.setSingleOk( list, "????????????????????????????????????" );
            }
            /*else if(id.startsWith("ds")){
                String id1 = id.substring(2);
                TBydbDatasetDo datasetDo = datasetService.findById(id1);
                List<TBydbDsColumnDo> colList = dsColumnService.findByDatasetId(datasetDo.getId());
                colList = colList.stream().filter(x->x.getEnable()!= null && x.getEnable() ==1).collect(Collectors.toList());
                List<TBydbDsColumnDo> groupList = colList.stream().filter(x -> "group".equalsIgnoreCase(x.getEtype())).collect(Collectors.toList());
                if( groupList.size()>0 ){
                    colList = groupList;
                }
                List<Object> list = colList.stream().map(x -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", x.getId());
                    map.put("columnName", x.getColumnAliasName());
                    //map.put("chgStatement", x.getChgStatement());
                    map.put("chgStatement", "");
                    map.put("orgType", x.getOrgType());
                    map.put("columnType", x.getColumnType());
                    map.put("chnName", x.getChnName());
                    map.put("tips", StringUtils.isBlank( x.getChnName())? x.getChnName():x.getColumnAliasName());

                    return map;
                }).collect( Collectors.toList());
                resMap.setSingleOk(list, "????????????????????????????????????");
            }*/
            else {
                resMap.setErr( "????????????????????????????????????,??????id" );
            }

        }
        catch ( Exception ex ) {
            resMap.setErr( "????????????????????????????????????" );
            logger.error( "????????????????????????????????????:", ex );
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "pms??????????????????", notes = "pms??????????????????")
    @RequestMapping(value = "/pmsaddobject", method = {RequestMethod.POST})
    public Map<String, Object> pmsAddObject( @RequestBody TBydbModelVo modelInfo, HttpServletRequest request ) {
        ResponeMap result = this.genResponeMap();

        try {
            UserDo user = LoginUtil.getUser( request );
            if ( modelInfo.getModelDo() == null || StringUtils.isBlank( modelInfo.getModelDo().getId() ) ) {
                return result.setErr( "??????id??????" ).getResultMap();
            }
            TTruModelDo modelDo = bydbModelService.findById( modelInfo.getModelDo().getId() );
            if ( modelDo == null ) {
                return result.setErr( "???????????????" ).getResultMap();
            }

            String ts = "??????";

            HashMap<String, TBydbDatabaseDo> dbMap = new HashMap<>();

            TTruModelObjectDo tmp = new TTruModelObjectDo();
            tmp.setModelId( modelDo.getId() );
            List<TTruModelObjectDo> moList = truModelObjectService.find( tmp );
            List<String> idList = moList.stream().map( x -> x.getObjectId() ).collect( Collectors.toList() );
            if ( modelInfo.getObjectDos().size() == 1 ) {
                ts = "";
            }
            List<TTruModelObjectDo> tabList = new ArrayList<>();
            for ( TBydbObjectDo x : modelInfo.getObjectDos() ) {
                if ( idList.indexOf( x.getId() ) >= 0 ) {
                    return result.setErr( ts + "???????????????" ).getResultMap();
                }
                TTruModelObjectDo obj = new TTruModelObjectDo();
                obj.setModelId( modelDo.getId() );
                if ( x.getId().startsWith( "db" ) ) {
                    String id = x.getId().substring( 2 );
                    TBydbObjectDo table = bydbObjectService.findById( id );
                    if ( table == null ) {
                        return result.setErr( ts + "???????????????" ).getResultMap();
                    }
                    TBydbDatabaseDo databaseDo = dbMap.get( table.getDbId() );
                    if ( databaseDo == null ) {
                        databaseDo = bydbDatabaseService.findById( table.getDbId() );
                        dbMap.put( databaseDo.getId(), databaseDo );
                    }
//                    if(!Constants.dchetu.equals( databaseDo.getDbsourceId() )){
//                        return result.setErr(ComUtil.trsEmpty( table.getObjChnName(),table.getObjectName() ) +"??????????????????").getResultMap();
//                    }
                    obj.setStype( "db" );
                    obj.setObjectId( x.getId() );
                    obj.setRealObjId( table.getId() );
                    obj.setDbId( table.getDbId() );
                    obj.setUserId( table.getUserId() );
                    obj.setUserAccount( table.getUserAccount() );
                    obj.setUserName( table.getUserName() );
                    obj.setSchemaId( table.getSchemaId() );
                    obj.setObjectName( table.getObjectName() );
                    obj.setObjChnName( table.getObjChnName() );
                    obj.setObjFullName( table.getObjFullName() );
                }
                else if ( x.getId().startsWith( "ds" ) ) {
                    String id = x.getId().substring( 2 );
                    TBydbDatasetDo datasetDo = datasetService.findById( id );
                    if ( datasetDo == null ) {
                        return result.setErr( ts + "???????????????" ).getResultMap();
                    }
                    if ( !Constants.dchetu.equals( datasetDo.getDatasourceId() ) ) {
                        return result.setErr( ComUtil.trsEmpty( datasetDo.getSetChnName(), datasetDo.getSetCode() ) + "??????????????????" ).getResultMap();
                    }
                    obj.setStype( "ds" );
                    obj.setObjectId( x.getId() );
                    obj.setRealObjId( datasetDo.getId() );
                    obj.setDbId( datasetDo.getDatasourceId() );
                    obj.setUserId( datasetDo.getUserId() );
                    obj.setUserAccount( datasetDo.getUserAccount() );
                    obj.setUserName( datasetDo.getUserName() );
                    obj.setObjectName( datasetDo.getSetCode() );
                    obj.setObjChnName( datasetDo.getSetChnName() );
                    obj.setObjFullName( datasetDo.getViewName() );
                }
                else {
                    return result.setErr( ts + "??????id?????????" ).getResultMap();
                }
                obj.setId( ComUtil.genId() );
                LoginUtil.setBeanInsertUserInfo( obj, user );
                tabList.add( obj );
            }
            truModelObjectService.batchAdd( tabList );
            result.setSingleOk( modelInfo, "????????????????????????" );
        }
        catch ( Exception e ) {
            logger.error( "????????????????????????", e );
            result.setErr( "????????????????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    @RequestMapping(value = "/syntrumodel", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String, Object> synTruModel( @RequestBody TTruModelDo modelDo ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            TTruModelDo info = truModelService.findById( modelDo.getId() );
            if ( info != null ) {
                truModelService.updateBean( modelDo );
            }
            else {
                truModelService.insertBean( modelDo );
            }
            result.setOk( "??????????????????" );
        }
        catch ( Exception e ) {
            logger.error( "??????????????????", e );
            result.setErr( "??????????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "??????????????????")
    @RequestMapping(value = "/deltrumodel", method = {RequestMethod.POST})
    public Map<String, Object> deltruModel( @RequestBody TTruModelDo modelDo ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            resMap.initSingleObject();
            //UserDo user = LoginUtil.getUser();
            truModelService.deleteById( modelDo.getId() );
            resMap.setOk( "??????????????????" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "??????????????????" );
            logger.error( "??????????????????", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "???????????????????????????", notes = "???????????????????????????")
    @RequestMapping(value = "/syntrumodelobject", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String, Object> synTruModelObject( @RequestBody TTruModelObjectDo info ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            TBydbObjectDo table = bydbObjectService.findById( info.getRealObjId() );
            if ( table == null ) {
                return null;
            }
            TTruModelObjectDo tmp = new TTruModelObjectDo();
            tmp.setModelId( info.getId() );
            tmp.setRealObjId( info.getRealObjId() );
            List<TTruModelObjectDo> moList = truModelObjectService.find( tmp );
            if ( moList.size() > 0 ) {
                return null;
            }
            info.setStype( "db" );
            info.setDbId( table.getDbId() );
            info.setSchemaId( table.getSchemaId() );
            info.setObjectName( table.getObjectName() );
            info.setObjChnName( table.getObjChnName() );
            info.setObjFullName( table.getObjFullName() );
            truModelObjectService.insertBean( info );
            result.setSingleOk( info, "????????????" );
        }
        catch ( Exception e ) {
            logger.error( "???????????????????????????", e );
            result.setErr( "???????????????????????????" );
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "??????????????????????????????", notes = "??????????????????????????????")
    @RequestMapping(value = "/queryuserapprove", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String, Object> queryUserApprove( @RequestBody List<FDataApproveDo> list ) {
        ResponeMap result = genResponeMap();
        try {
            if ( list.size() == 0 ) {
                return result.setErr( "???????????????" ).getResultMap();
            }
            String creatorId = list.get( 0 ).getCreatorId();
            if ( StringUtils.isBlank( creatorId ) ) {
                return result.setErr( "???????????????" ).getResultMap();
            }
            List<String> idList = list.stream().map( x -> x.getDataId() ).collect( Collectors.toList() );
            String dataIds = "'" + String.join( "','", idList ) + "'";
            //BydbObjectVo
//            Example exp = new Example( TBydbObjectDo.class );
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andIn( "id", idList );

//            VBydbObjectVo bean = new VBydbObjectVo();
//            bean.setIdList( idList );
//            List<VBydbObjectVo> tabList = tableService.findNodeBeanList( bean );
//
//            List<FDataApproveVo> daList = new ArrayList<>();

//            List<Integer> statusList = new ArrayList<>();
//            statusList.add( 1 );
//            statusList.add( 2 );
//            Example exp = new Example( FDataApproveDo.class );
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andIn( "dataId", idList ).andEqualTo( "creatorId",creatorId ).andIn( "approve" ,statusList);
//            List<FDataApproveDo> daList = dataApproveService.findByExample( exp );
            List<FDataApproveDo> apList = dataApproveService.selectApproveByUserDataId( creatorId, dataIds );

//            for ( FDataApproveDo approveDo : apList ) {
//                TBydbObjectDo objectDo = bydbObjectService.findById( approveDo.getDataId() );
//                if(  objectDo!= null){
//                    approveDo.setCreatorAccount( objectDo.getObjectName() );
//                    approveDo.setCreatorName( objectDo.getObjChnName() );
//                }
//            }
            result.setSingleOk( apList, "????????????" );

        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            result.setErr( "????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    @RequestMapping(value = "/synmodelobjectlist", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String, Object> synModelObjectList( @RequestBody List<TTruModelObjectDo> list ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            truModelObjectService.saveModelObjectList( list );
            result.setOk( "???????????????????????????" );
        }
        catch ( Exception e ) {
            logger.error( "???????????????????????????", e );
            result.setErr( "???????????????????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "????????????????????????????????????", notes = "????????????????????????????????????")
    @RequestMapping(value = "/findtrumodelobjecreldata", method = {RequestMethod.GET})
    @ApiImplicitParams({
    })
    public Map<String, Object> findTruModelObjecRelData( String modelId ) {
        ResponeMap result = genResponeMap();
        try {
            if ( StringUtils.isBlank( modelId ) ) {
                return result.setErr( "modelId??????" ).getResultMap();
            }
            TTruModelDo modelDo = truModelService.findById( modelId );
            if ( modelDo == null ) {
                return result.setErr( "???????????????" ).getResultMap();
            }
            List<TTruModelObjectDo> list = truModelObjectService.selectByModelId( modelId );
            if ( list.size() == 0 ) {
                return result.setSingleOk( new ArrayList<>(), "??????????????????????????????" ).getResultMap();
            }
            List<String> idList = list.stream().map( x -> x.getRealObjId() ).distinct().collect( Collectors.toList() );

            VBydbObjectVo objTmp = new VBydbObjectVo();
            objTmp.setOwnerNodeId( modelDo.getNodePartyId() );
            objTmp.setOwnerUserId( modelDo.getCreatorId() );
            objTmp.setIdList( idList );

            List<VBydbObjectVo> nodeBeanList = bydbTableService.findNodeBeanList( objTmp );
//            FDataApproveVo approveVo =new FDataApproveVo();
//            approveVo.setCreatorId( modelDo.getCreatorId() );
//            String [] ids = idList.toArray(new String[idList.size()]);
//            approveVo.setOther1( ids );

            List<FDataApproveDo> beanList = dataApproveService.selectApproveByUserDataId( modelDo.getCreatorId(), "'" + String.join( "','", idList ) + "'" );
            for ( VBydbObjectVo bean : nodeBeanList ) {
                for ( FDataApproveDo approve : beanList ) {
                    if ( bean.getId().equals( approve.getDataId() ) ) {
                        bean.setUserPrivGrant( approve.getApprove() );
                        break;
                    }
                }
            }
            result.setSingleOk( nodeBeanList, "????????????" );
        }
        catch ( Exception e ) {
            result.setErr( "????????????????????????????????????" );
            logger.error( "????????????????????????????????????,modelId:{}", modelId, e );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "???????????????????????????", notes = "???????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "???ID", dataType = "String", required = true, paramType = "query", example = "")
    })
    @RequestMapping(value = "/deltrumodelobject", method = {RequestMethod.POST})
    public Map<String, Object> delTruModelObject( @RequestBody List<String> idList ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            if ( idList == null && idList.size() > 0 ) {
                return result.setErr( "ID????????????" ).getResultMap();
            }
            Example exp = new Example( TTruModelObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", idList );
            List<TTruModelObjectDo> list = truModelObjectService.findByExample( exp );
            if ( list.size() == 0 ) {
                return result.setOk( "??????????????????" ).getResultMap();
            }
            List<String> modelIdList = list.stream().map( x -> x.getModelId() ).distinct().collect( Collectors.toList() );
            if ( modelIdList.size() != 1 ) {
                return result.setErr( "???????????????????????????????????????" ).getResultMap();
            }
            List<String> objIdList = list.stream().map( x -> x.getObjectId() ).distinct().collect( Collectors.toList() );

            exp = new Example( TTruModelElementDo.class );
            criteria = exp.createCriteria();
            criteria.andIn( "tcId", objIdList ).andEqualTo( "modelId", modelIdList.get( 0 ) );
            int cnt = truModelElementService.findCountByExample( exp );
            if ( cnt > 0 ) {
                return result.setErr( "??????????????????????????????" ).getResultMap();
            }

            List<String> collect = list.stream().map( x -> x.getId() ).collect( Collectors.toList() );
            truModelObjectService.deleteByIds( collect );

            result.setOk( "????????????" );
        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            result.setErr( "????????????" );
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/synfavouriteobject", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object synFavouriteObject( @RequestBody TTruFavouriteObjectDo info ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            TTruFavouriteObjectDo tmp = new TTruFavouriteObjectDo();
            tmp.setRelId( info.getRelId() );
            tmp.setUserId( info.getUserId() );

            List<TTruFavouriteObjectDo> tmpList = favouriteObjectService.find( tmp );
            if ( tmpList.size() > 0 ) {
                return result.setErr( "??????????????????" ).getResultMap();
            }

            //if (relId.startsWith("db")) {
            //info.setObjectId(  );


//                if( objIdList.contains( foDo.getObjectId() )){
//                    return resMap.setErr("????????????id????????????").getResultMap();
//                }
//                objIdList.add( foDo.getObjectId() );
            TBydbObjectDo objectDo = bydbTableService.findById( info.getRelId() );
            if ( objectDo != null ) {
                info.setStype( "db" );
                info.setObjectId( objectDo.getId() );
                info.setDbId( objectDo.getDbId() );
                info.setSchemaId( objectDo.getSchemaId() );
                info.setObjName( objectDo.getObjectName() );
                info.setObjFullName( objectDo.getObjFullName() );
                info.setObjChnName( ComUtil.trsEmpty( objectDo.getObjChnName(), objectDo.getObjectName() ) );
            }
            else {
                TOlkObjectDo olkTable = olkTableService.findById( info.getRelId() );
                if ( olkTable != null ) {
                    info.setStype( "olktab" );
                    info.setObjectId( olkTable.getId() );
                    info.setDbId( olkTable.getDbId() );
                    info.setSchemaId( olkTable.getSchemaId() );
                    info.setObjName( olkTable.getObjectName() );
                    info.setObjFullName( olkTable.getObjFullName() );
                    info.setObjChnName( ComUtil.trsEmpty( olkTable.getObjChnName(), olkTable.getObjectName() ) );
                }
                else {
                    return result.setErr( "?????????????????????" ).getResultMap();
//                info.setDatasetId( info.getRelId() );
//                info.setStype( "ds" );
//                TBydbDatasetDo datasetDo = datasetService.findById( info.getDatasetId() );
//                if ( datasetDo == null ) {
//                    return result.setErr( "????????????????????????" ).getResultMap();
//                }
//                info.setObjName( datasetDo.getSetCode() );
//                info.setObjFullName( datasetDo.getViewName() );
//                info.setObjChnName( datasetDo.getSetChnName() );
//                info.setObjChnName( ComUtil.trsEmpty( datasetDo.getSetChnName(), datasetDo.getSetCode() ) );
                }
            }

            tmp = favouriteObjectService.findById( info.getId() );
            if ( tmp == null ) {
                favouriteObjectService.insertBean( info );
            }
            else {
                favouriteObjectService.updateBean( info );
            }
            result.setSingleOk( tmp, "??????????????????" );
        }
        catch ( Exception e ) {
            logger.error( "??????????????????", e );
            result.setErr( "??????????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/delfavouriteobject", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String, Object> delFavouriteObject( @RequestBody List<TTruFavouriteObjectDo> list ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            Example exp = new Example( TTruFavouriteObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            for ( TTruFavouriteObjectDo tmp : list ) {
                if ( StringUtils.isNotBlank( tmp.getId() ) ) {
                    criteria.orEqualTo( "id", tmp.getId() );
                }
                else {
                    criteria.orEqualTo( "relId", tmp.getRelId() ).andEqualTo( "userId", tmp.getUserId() );
                }
            }
//            if ( ids != null ) {
//                criteria.andIn( "id", ids );
//            }
//            if ( rels != null ) {
//                criteria.andIn( "relId", rels );
//                criteria.andEqualTo( "userId", user.getUserName() );
//            }
            List<TTruFavouriteObjectDo> delList = favouriteObjectService.findByExample( exp );
            int cnt = list.size();
            if ( cnt == 0 ) {
                return result.setErr( "?????????????????????" ).getResultMap();
            }
//            if ( ids != null && list.size() != ids.size() ) {
//                return result.setErr( "??????????????????" ).getResultMap();
//            }

            List<String> idList = delList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
            favouriteObjectService.deleteByIds( idList );
            result.setOk( "??????????????????" );
        }
        catch ( Exception e ) {
            logger.error( "??????????????????", e );
            result.setErr( "??????????????????" );
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/synapplyobject", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object synApplyObject( @RequestBody FDataApproveDo info ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            Example exp = new Example( FDataApproveDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andEqualTo( "dataId", info.getDataId() ).andEqualTo( "userId", info.getUserId() )
                    .andNotEqualTo( "approve", 9 );
            Integer cnt = applyObjectService.findCountByExample( exp );
            if ( cnt > 0 ) {
                result.setErr( "????????????????????????" );
            }
            TBydbObjectDo tab = bydbTableService.findById( info.getDataId() );
            if ( tab != null ) {
                info.setUserId( tab.getUserId() );
                info.setUserName( tab.getUserName() );
                applyObjectService.insertBean( info );

                result.setSingleOk( info, "??????????????????" );
            }
            else {
                result.setErr( "?????????????????????" );
            }

        }
        catch ( Exception e ) {
            logger.error( "??????????????????", e );
            result.setErr( "??????????????????" );
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/savedataapprove", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object saveDataApprove( @RequestBody List<FDataApproveDo> list ) {
        ResponeMap result = genResponeMap();
        try {
            //result.initSingleObject();
            /*List<FDataApproveDo> modList = new ArrayList<>();
            for ( int i = list.size() - 1; i >= 0; i-- ) {
                FDataApproveDo approveDo = list.get( i );
                Example exp = new Example( FDataApproveDo.class );
                Example.Criteria criteria = exp.createCriteria();
                criteria.andEqualTo( "dataId", approveDo.getDataId() ).andEqualTo( "creatorId", approveDo.getCreatorId() )
                        .andNotEqualTo( "approve", 9 ).andNotEqualTo( "approve", 0 );
                List<FDataApproveDo> tmpList = applyObjectService.findByExample( exp );
                if ( tmpList.size() > 0 ) {
                    FDataApproveDo da = tmpList.get( 0 );
                    for ( FDataApproveDo fDataApproveDo : tmpList ) {
                        if ( fDataApproveDo.getApprove() != null && fDataApproveDo.getApprove() == 1 ) {
                            da = fDataApproveDo;
                        }
                    }
                    tmpList.remove( da );
                    if ( tmpList.size() > 0 ) {
                        for ( FDataApproveDo fDataApproveDo : tmpList ) {
                            fDataApproveDo.setApprove( 9 );
                            applyObjectService.updateBean( fDataApproveDo );
                        }
                    }
                    if ( da.getApprove() == null || da.getApprove() != 1 ) {
                        da.setApprove( 1 );
                        da.setApproval( approveDo.getApproval() );
                        modList.add( da );
                    }
                    list.remove( i );
                }
            }
            applyObjectService.saveAndUpdateBeans( list, modList );
            list.addAll( modList );
            result.setOk( list.size(), list, "????????????" );*/

        }
        catch ( Exception e ) {
            logger.error( "????????????", e );
            result.setErr( "????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/delapplyobject", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String, Object> delApplyObject( @RequestBody List<FDataApproveDo> list ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            List<String> idList = list.stream().map( x -> x.getId() ).collect( Collectors.toList() );
            applyObjectService.deleteByIds( idList );
            result.setOk( "??????????????????" );
        }
        catch ( Exception e ) {
            logger.error( "??????????????????", e );
            result.setErr( "??????????????????" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/cancelapplyobject", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object cancelApplyObject( @RequestParam(value = "id", required = false) String id, @RequestParam(value = "relId", required = false) String relId, @RequestParam(value = "userId", required = false) String userId ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            if ( StringUtils.isBlank( id ) && (StringUtils.isBlank( relId ) || StringUtils.isBlank( userId )) ) {
                return result.setErr( "id?????????id??????????????????" ).getResultMap();
            }
            List<String> ids = null;

            if ( StringUtils.isNotBlank( id ) ) {
                ids = Arrays.asList( id.split( ",|\\s+" ) );
            }
            List<String> rels = null;
            if ( StringUtils.isNotBlank( relId ) ) {
                rels = Arrays.asList( relId.split( ",|\\s+" ) );
            }

            Example exp = new Example( FDataApproveDo.class );
            Example.Criteria criteria = exp.createCriteria();

            if ( ids != null ) {
                criteria.andIn( "id", ids );
            }
            if ( rels != null ) {
                criteria.andIn( "dataId", rels );
                criteria.andEqualTo( "creatorId", userId );
            }
            criteria.andEqualTo( "approve", 2 );
            List<FDataApproveDo> list = applyObjectService.findByExample( exp );
            int cnt = list.size();
            if ( cnt == 0 ) {
                return result.setErr( "?????????????????????" ).getResultMap();
            }
            if ( ids != null && list.size() != ids.size() ) {
                return result.setErr( "??????????????????" ).getResultMap();
            }
            for ( FDataApproveDo approveDo : list ) {
                approveDo.setApprove( 9 );
            }
            applyObjectService.batchUpdateByPrimaryKey( list );
            result.setOk( "??????????????????" );
        }
        catch ( Exception e ) {
            logger.error( "??????????????????", e );
            result.setErr( "??????????????????" );
        }
        return result.getResultMap();
    }

}
