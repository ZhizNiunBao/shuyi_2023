package cn.bywin.business.controller.olk;

import static cn.bywin.business.common.enums.TreeRootNodeEnum.MODEL_OBJECT;

import cn.bywin.business.bean.analysis.olk.OlkBaseComponenT;
import cn.bywin.business.bean.analysis.olk.OlkCheckComponent;
import cn.bywin.business.bean.analysis.olk.OlkComponentEnum;
import cn.bywin.business.bean.analysis.olk.template.OlkDataSourceOutPutComponent;
import cn.bywin.business.bean.analysis.olk.template.OlkTableComponent;
import cn.bywin.business.bean.bydb.TBydbDatabaseDo;
import cn.bywin.business.bean.bydb.TBydbFieldDo;
import cn.bywin.business.bean.bydb.TBydbObjectDo;
import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.olk.TOlkDcServerDo;
import cn.bywin.business.bean.olk.TOlkFieldDo;
import cn.bywin.business.bean.olk.TOlkModelComponentDo;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.bean.olk.TOlkModelFolderDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.view.CoordVo;
import cn.bywin.business.bean.view.bydb.DigitalAssetVo;
import cn.bywin.business.bean.view.bydb.TBydbModelPo;
import cn.bywin.business.bean.view.bydb.TBydbModelVo;
import cn.bywin.business.bean.view.federal.FDataApproveVo;
import cn.bywin.business.bean.view.federal.NodePartyView;
import cn.bywin.business.bean.view.olk.OlkNode;
import cn.bywin.business.bean.view.olk.OlkObjectWithFieldsVo;
import cn.bywin.business.bean.view.olk.TOlkModelComponentVo;
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
import cn.bywin.business.common.util.SqlTextUtil;
import cn.bywin.business.hetu.HetuJdbcOperate;
import cn.bywin.business.hetu.HetuJdbcOperateComponent;
import cn.bywin.business.modeltask.ModelTaskFlinkApiService;
import cn.bywin.business.service.bydb.BydbDatabaseService;
import cn.bywin.business.service.bydb.BydbDatasetService;
import cn.bywin.business.service.bydb.BydbObjectService;
import cn.bywin.business.service.federal.DataPartyService;
import cn.bywin.business.service.federal.DataSourceService;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.service.olk.OlkDcServerService;
import cn.bywin.business.service.olk.OlkModelComponentService;
import cn.bywin.business.service.olk.OlkModelElementRelService;
import cn.bywin.business.service.olk.OlkModelElementService;
import cn.bywin.business.service.olk.OlkModelFieldService;
import cn.bywin.business.service.olk.OlkModelFolderService;
import cn.bywin.business.service.olk.OlkModelObjectService;
import cn.bywin.business.service.olk.OlkModelService;
import cn.bywin.business.trumodel.ApiOlkDbService;
import cn.bywin.business.trumodel.ApiTruModelService;
import cn.bywin.business.util.DbTypeToFlinkType;
import cn.bywin.business.util.JdbcTypeToJavaTypeUtil;
import cn.bywin.business.util.JdbcTypeTransformUtil;
import cn.bywin.business.util.analysis.OlkModelRunService;
import cn.bywin.cache.SysParamSetOp;
import cn.bywin.common.resp.ListResp;
import cn.bywin.common.resp.ObjectResp;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

@RestController
@RequestMapping({"/analyse/olkmodel"})
@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        maxAge = 3600)
@Api(tags = "olk建模-model")
public class OlkModelController extends BaseController {
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
    @Autowired
    private OlkModelElementRelService truModelElementRelService;
    @Autowired
    private BydbDatabaseService bydbDatabaseService;
    @Autowired
    private BydbDatasetService datasetService;
    @Autowired
    private OlkModelRunService analysisRunServicr;

    @Autowired
    private OlkModelFolderService folderService;
    @Autowired
    private DataPartyService dataPartyService;

//    @Autowired
//    private OlkGrantObjectService grantObjectService;

//    @Autowired
//    private OlkApplyObjectService applyObjectService;

    @Autowired
    private NodePartyService nodePartyService;

    @Autowired
    private ApiOlkDbService apiOlkDbService;
    @Autowired
    private ApiTruModelService apiTruModelService;


    @Autowired
    private ModelTaskFlinkApiService modelTaskFlinkApiService;

    @Autowired
    private OlkModelOutDbSet outDbSet;

    @Autowired
    private OlkDcServerService dcService;

//    @Autowired
//    private MenuUtil menuUtil;

//    @Autowired
//    private ISysParamSetOp setOp;
//
    @Autowired
    private HetuJdbcOperateComponent hetuJdbcOperateComponent;

//    @Autowired
//    private SystemParamHolder systemParamHolder;

    @Autowired
    private DataSourceService dbSourceService;

    @ApiOperation(value = "新增模型", notes = "新增模型")
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public Object addModel( @RequestBody TOlkModelDo modelInfo, HttpServletRequest request ) {
        ResponeMap result = this.genResponeMap();
        if ( StringUtils.isEmpty( modelInfo.getName() ) ) {
            return result.setErr( "模型名称为空" ).getResultMap();
        }
        TOlkModelDo info = null;
        try {
            UserDo userDo = LoginUtil.getUser( request );
            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
//            if( StringUtils.isBlank( userDo.getOrgNo() ) ){
//                return result.setErr("用户信息不完整").getResultMap();
//            }
            LoginUtil.setBeanInsertUserInfo( modelInfo, userDo );
            modelInfo.setStatus( 4 );
            modelInfo.setRunType( 1 );
            modelInfo.setCreateDeptNo( userDo.getOrgNo() );
            modelInfo.setCreateDeptNa( userDo.getOrgName() );
            modelInfo.setSynFlag( 0 );
            modelInfo.setNodePartyId( nodePartyDo.getId() );
            modelInfo.setConfig( "olk" );
            if(StringUtils.isBlank( modelInfo.getDcId() )) {
                modelInfo.setDcId( null );
            }
            else{
                TOlkDcServerDo dcDo = dcService.findById( modelInfo.getDcId() );
                if( dcDo ==null || dcDo.getEnable()== null || dcDo.getEnable() !=1 ){
                    return result.setErr( "节点不存在或未启用" ).getResultMap();
                }
            }

            TOlkModelDo tmp = new TOlkModelDo();
            tmp.setName( modelInfo.getName() );
            tmp.setCreatorAccount( userDo.getUserName() );
            List<TOlkModelDo> TOlkModelDos = truModelService.find( tmp );
            if ( TOlkModelDos != null && TOlkModelDos.size() > 0 ) {
                return result.setErr( "模型名称已存在" ).getResultMap();
            }
            info = modelInfo;
            truModelService.insertBeanDetail( modelInfo );
            ObjectResp<String> retVal = apiOlkDbService.synOlkModel( modelInfo, userDo.getTokenId() );
            if ( retVal.isSuccess() ) {
                modelInfo.setSynFlag( 1 );
                truModelService.updateBean( modelInfo );
            }
            else {
                truModelService.deleteById( modelInfo.getId() );
                info = null;
                return retVal;
            }
            result.setSingleOk( info, "新增模型成功" );
        }
        catch ( Exception e ) {
            logger.error( "新增模型失败", e );
            result.setErr( "新增模型失败" );
            if ( info != null ) {
                truModelService.deleteById( info.getId() );
            }
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "模型复制", notes = "模型复制")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", example = "c2e9e8a5ce0e4a1b8be389dd1a7d5871", required = true, paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "name", value = "中文名称", dataType = "String", example = "100", required = false, paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "dsLabel", value = "标签", dataType = "String", example = "", required = false, paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "folderId", value = "文件夹id", dataType = "String", example = "", required = false, paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "description", value = "备注", dataType = "String", example = "", required = false, paramType = "query", dataTypeClass = String.class),
    })
    @RequestMapping(value = "/copymodel", method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> copymodel( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser( request );
            if ( user == null || StringUtils.isBlank( user.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }

            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            logger.debug( "{}", hru.getAllParaData() );
            TOlkModelDo fromModel = truModelService.findById( hru.getNvlPara( "id" ) );

            if ( fromModel == null ) {
                return resMap.setErr( "模型不存在" ).getResultMap();
            }
            TOlkModelDo info = new TOlkModelDo();
            MyBeanUtils.copyBeanNotNull2Bean( fromModel, info );
            new PageBeanWrapper( info, hru, "" );
//            if ( StringUtils.isNotBlank( info.getFolderId() ) ) {
//                info.setFolderId( info.getFolderId() );
//            }
//            else {
//                info.setFolderId( null );
//            }
            if ( StringUtils.isBlank( info.getName() ) ) {
                return resMap.setErr( "名称不能为空" ).getResultMap();
            }
            info.setId( ComUtil.genId() );
            LoginUtil.setBeanInsertUserInfo( info, user );
            info.setRunType( 0 );
            info.setCacheFlag( 0 );
            info.setModifiedTime( ComUtil.getCurTimestamp() );
            info.setRunSql( null );
            info.setSynFlag( 0 );

            List<TOlkModelDo> TOlkModelDos = truModelService.findByName( info.getName() );
            if ( TOlkModelDos != null ) {
                for ( TOlkModelDo TOlkModelDo : TOlkModelDos ) {
                    if ( !TOlkModelDo.getId().equals( info.getId() ) ) {
                        return resMap.setErr( "模型名称已存在" ).getResultMap();
                    }
                }
            }

            List<TOlkModelObjectDo> oldObjList = truModelObjectService.selectByModelId( fromModel.getId() );

            List<TOlkModelElementDo> oldEleList = truModelElementService.selectByModelId( fromModel.getId() );

            List<TOlkModelElementRelDo> oldRelList = truModelElementRelService.selectByModelId( fromModel.getId() );

            List<TOlkModelFieldDo> oldFieldList = truModelFieldService.selectByModelId( fromModel.getId() );

            List<TOlkModelObjectDo> newObjList = new ArrayList<>();
            List<TOlkModelElementDo> newEleList = new ArrayList<>();
            List<TOlkModelElementRelDo> newRelList = new ArrayList<>();
            List<TOlkModelFieldDo> newFieldList = new ArrayList<>();

            for ( TOlkModelObjectDo tmp : oldObjList ) {
                TOlkModelObjectDo obj = new TOlkModelObjectDo();
                MyBeanUtils.copyBeanNotNull2Bean( tmp, obj );
                obj.setId( ComUtil.genId() );
                obj.setModelId( info.getId() );
                LoginUtil.setBeanInsertUserInfo( obj, user );
                obj.setModifiedTime( null );
                newObjList.add( obj );
                obj.setSynFlag( 0 );
            }

            HashMap<String, String> eleMap = new HashMap<>();
            for ( TOlkModelElementDo tmp : oldEleList ) {
                TOlkModelElementDo ele = new TOlkModelElementDo();
                MyBeanUtils.copyBeanNotNull2Bean( tmp, ele );
                ele.setId( ComUtil.genId() );
                ele.setModelId( info.getId() );
                LoginUtil.setBeanInsertUserInfo( ele, user );
                ele.setModifiedTime( null );
                ele.setRunStatus( 0 );
                ele.setTableSql( null );
                ele.setRunSql( null );
                newEleList.add( ele );
                eleMap.put( tmp.getId(), ele.getId() );
            }
            for ( TOlkModelElementRelDo tmp : oldRelList ) {
                TOlkModelElementRelDo rel = new TOlkModelElementRelDo();
                MyBeanUtils.copyBeanNotNull2Bean( tmp, rel );
                rel.setId( ComUtil.genId() );
                rel.setModelId( info.getId() );
                LoginUtil.setBeanInsertUserInfo( rel, user );
                rel.setModifiedTime( null );
                rel.setStartElementId( eleMap.get( tmp.getStartElementId() ) );
                rel.setEndElementId( eleMap.get( tmp.getEndElementId() ) );
                newRelList.add( rel );
            }

            HashMap<String, String> fieldMap = new HashMap<>();
            for ( TOlkModelFieldDo tmp : oldFieldList ) {
                TOlkModelFieldDo field = new TOlkModelFieldDo();
                MyBeanUtils.copyBeanNotNull2Bean( tmp, field );
                field.setId( ComUtil.genId() );
                field.setElementId( eleMap.get( tmp.getElementId() ) );
                field.setExtendsId( eleMap.get( tmp.getExtendsId() ) );
                LoginUtil.setBeanInsertUserInfo( field, user );
                field.setModifiedTime( null );
                newFieldList.add( field );
                fieldMap.put( tmp.getId(), field.getId() );
            }
            for ( TOlkModelFieldDo field : newFieldList ) {
                if ( StringUtils.isNotBlank( field.getFromFieldId() ) ) {
                    field.setFromFieldId( fieldMap.get( field.getFromFieldId() ) );
                }
            }
            for ( TOlkModelFieldDo fieldDo : newFieldList ) {
                if ( StringUtils.isNotBlank( fieldDo.getFilterConfig() ) ) {
                    String filterConfig = fieldDo.getFilterConfig();
                    Iterator<Map.Entry<String, String>> iterator = fieldMap.entrySet().iterator();
                    while ( iterator.hasNext() ) {
                        Map.Entry<String, String> next = iterator.next();
                        filterConfig = filterConfig.replaceAll( next.getKey(), next.getValue() );
                    }
                    iterator = eleMap.entrySet().iterator();
                    while ( iterator.hasNext() ) {
                        Map.Entry<String, String> next = iterator.next();
                        filterConfig = filterConfig.replaceAll( next.getKey(), next.getValue() );
                    }
                    fieldDo.setFilterConfig( filterConfig );
                }
            }

            for ( TOlkModelElementDo elementDo : newEleList ) {
                if ( StringUtils.isNotBlank( elementDo.getConfig() ) ) {
                    String config = elementDo.getConfig();
                    Iterator<Map.Entry<String, String>> iterator = fieldMap.entrySet().iterator();
                    while ( iterator.hasNext() ) {
                        Map.Entry<String, String> next = iterator.next();
                        config = config.replaceAll( next.getKey(), next.getValue() );
                    }
                    iterator = eleMap.entrySet().iterator();
                    while ( iterator.hasNext() ) {
                        Map.Entry<String, String> next = iterator.next();
                        config = config.replaceAll( next.getKey(), next.getValue() );
                    }
                    elementDo.setConfig( config );
                }
            }

            truModelService.copyModel( info, newEleList, newRelList, newFieldList, newObjList );

            resMap.setSingleOk( info, "模型复制成功" );
        }
        catch ( Exception e ) {
            logger.error( "模型复制失败", e );
            resMap.setErr( "模型复制失败" );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "修改基本数据", notes = "修改基本数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", example = "c2e9e8a5ce0e4a1b8be389dd1a7d5871", required = true, paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "setChnName", value = "中文名称", dataType = "String", example = "100", required = false, paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "dsLabel", value = "标签", dataType = "String", example = "", required = false, paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "folderId", value = "文件夹id", dataType = "String", example = "", required = false, paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "remark", value = "备注", dataType = "String", example = "", required = false, paramType = "query", dataTypeClass = String.class),
    })
    @RequestMapping(value = "/baseupdate", method = {RequestMethod.POST})
    @ResponseBody
    public Object baseUpdate( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser( request );
            if ( user == null || StringUtils.isBlank( user.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }

            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            logger.debug( "{}", hru.getAllParaData() );
            TOlkModelDo info = truModelService.findById( hru.getNvlPara( "id" ) );

            if ( info == null ) {
                return resMap.setErr( "模型不存在" ).getResultMap();
            }

            TOlkModelDo old = new TOlkModelDo();
            TOlkModelDo tmpData = new TOlkModelDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, old );
            MyBeanUtils.copyBeanNotNull2Bean( info, tmpData );

            new PageBeanWrapper( tmpData, hru, "" );

            info.setName( tmpData.getName() );
            info.setDescription( tmpData.getDescription() );
            info.setTags( tmpData.getTags() );
            if ( StringUtils.isNotBlank( tmpData.getFolderId() ) ) {
                info.setFolderId( tmpData.getFolderId() );
            }
            else {
                info.setFolderId( null );
            }

            if ( StringUtils.isBlank( info.getName() ) ) {
                return resMap.setErr( "名称不能为空" ).getResultMap();
            }

            List<TOlkModelDo> TOlkModelDos = truModelService.findByName( info.getName() );
            if ( TOlkModelDos != null ) {
                for ( TOlkModelDo TOlkModelDo : TOlkModelDos ) {
                    if ( !TOlkModelDo.getId().equals( info.getId() ) ) {
                        return resMap.setErr( "模型名称已存在" ).getResultMap();
                    }
                }
            }
            info.setSynFlag( 0 );
            truModelService.updateBean( info );
            resMap.setSingleOk( info, "保存成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "数据列表搜索", notes = "数据列表搜索")
    @RequestMapping(value = "/objecttree", method = {RequestMethod.GET})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "name", dataType = "String", required = true, paramType = "query", example = "")
    })
    public Map<String, Object> objectTree( String name, HttpServletRequest request ) {
        ResponeMap result = this.genResponeMap();

        try {

            List<TBydbObjectDo> tBydbObjectDos = bydbObjectService.findLikeName( name );

            result.setSingleOk( tBydbObjectDos, "查询数据列表搜索成功" );
        }
        catch ( Exception e ) {
            logger.error( "查询数据列表搜索失败", e );
            result.setErr( "查询数据列表搜索失败" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "新增数据列表", notes = "新增数据列表")
    @RequestMapping(value = "/addobject", method = {RequestMethod.POST})
    public Map<String, Object> addObject( @RequestBody TBydbModelVo modelInfo, HttpServletRequest request ) {
        ResponeMap result = this.genResponeMap();

        try {
            UserDo user = LoginUtil.getUser( request );
            if ( modelInfo.getModelDo() == null || StringUtils.isBlank( modelInfo.getModelDo().getId() ) ) {
                return result.setErr( "模型id为空" ).getResultMap();
            }
            TOlkModelDo modelDo = truModelService.findById( modelInfo.getModelDo().getId() );
            if ( modelDo == null ) {
                return result.setErr( "模型不存在" ).getResultMap();
            }

            String ts = "部分";

            HashMap<String, TBydbDatabaseDo> dbMap = new HashMap<>();

            TOlkModelObjectDo tmp = new TOlkModelObjectDo();
            tmp.setModelId( modelDo.getId() );
            List<TOlkModelObjectDo> moList = truModelObjectService.find( tmp );
            List<String> idList = moList.stream().map( x -> x.getObjectId() ).collect( Collectors.toList() );
            if ( modelInfo.getObjectDos().size() == 1 ) {
                ts = "";
            }
            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            List<TOlkModelObjectDo> tabList = new ArrayList<>();
            for ( TBydbObjectDo x : modelInfo.getObjectDos() ) {
                if ( idList.indexOf( x.getId() ) >= 0 ) {
                    return result.setErr( ts + "资源已存在" ).getResultMap();
                }
                TOlkModelObjectDo obj = new TOlkModelObjectDo();
                obj.setModelId( modelDo.getId() );

                //if ( x.getId().startsWith( "db" ) ) {
                //    String id = x.getId().substring( 2 );

                //TBydbObjectDo table = bydbObjectService.findById( x.getId() );
//                if ( table != null )
//                {
//                        return result.setErr( ts + "资源不存在" ).getResultMap();
//                    }
//                    TBydbDatabaseDo databaseDo = dbMap.get( table.getDbId() );
//                    if ( databaseDo == null ) {
//                        databaseDo = bydbDatabaseService.findById( table.getDbId() );
//                        dbMap.put( databaseDo.getId(), databaseDo );
//                    }
//                    if(!Constants.dchetu.equals( databaseDo.getDbsourceId() )){
//                        return result.setErr(ComUtil.trsEmpty( table.getObjChnName(),table.getObjectName() ) +"不可参与分析").getResultMap();
//                    }
                //obj.setStype( "db" );
                obj.setObjectId( x.getId() );
                obj.setRealObjId( x.getId() );
                //obj.setDbId( table.getDbId() );
                obj.setUserId( user.getUserId() );
                obj.setUserAccount( user.getUserName() );
                obj.setUserName( user.getChnName() );
                //obj.setSchemaId( table.getSchemaId() );
                //obj.setObjectName( table.getObjectName() );
                //obj.setObjChnName( table.getObjChnName() );
                //obj.setObjFullName( table.getObjFullName() );
                //}
                //else {
                //else if ( x.getId().startsWith( "ds" ) ) {
//                    String id = x.getId();//.substring( 2 );
//                    TBydbDatasetDo datasetDo = datasetService.findById( id );
//                    if ( datasetDo == null ) {
//                        return result.setErr( ts + "资源不存在" ).getResultMap();
//                    }
//                    if ( !Constants.dchetu.equals( datasetDo.getDatasourceId() ) ) {
//                        return result.setErr( ComUtil.trsEmpty( datasetDo.getSetChnName(), datasetDo.getSetCode() ) + "不可参与分析" ).getResultMap();
//                    }
//                    obj.setStype( "ds" );
//                    obj.setObjectId( x.getId() );
//                    obj.setRealObjId( datasetDo.getId() );
//                    obj.setDbId( datasetDo.getDatasourceId() );
//                    obj.setUserId( datasetDo.getUserId() );
//                    obj.setUserAccount( datasetDo.getUserAccount() );
//                    obj.setUserName( datasetDo.getUserName() );
//                    obj.setObjectName( datasetDo.getSetCode() );
//                    obj.setObjChnName( datasetDo.getSetChnName() );
//                    obj.setObjFullName( datasetDo.getViewName() );
//                }
//                else {
//                    return result.setErr( ts + "资源id不正确" ).getResultMap();
//                }
                obj.setNodePartyId( nodePartyDo.getId() );
                obj.setSynFlag( 1 );
                obj.setId( ComUtil.genId() );
                LoginUtil.setBeanInsertUserInfo( obj, user );
                tabList.add( obj );
            }
            for ( TOlkModelObjectDo tOlkModelObjectDo : tabList ) {
                ObjectResp<TOlkModelObjectDo> retVal = apiOlkDbService.synOlkModelObject( tOlkModelObjectDo, user.getTokenId() );
                if( !retVal.isSuccess() ){
                    return result.setErr( "保存失败,"+retVal.getMsg() ).getResultMap();
                }
                TOlkModelObjectDo info =retVal.getData();
                truModelObjectService.insertBean( info );
            }
            //truModelObjectService.batchAdd( tabList );
//            List<TOlkModelObjectDo> saveList =  apiOlkModelService.synmodelobject( tabList, user.getTokenId() );
//
//            if ( saveList!= null && saveList.size()>0 ) {
//                truModelObjectService.batchAdd( saveList );
//            }
//            else {
//                String msg = String.format( "保存失败" );
////                if ( retMap.containsKey( "msg" ) ) {
////                    msg = String.format( "%s,错误信息：%s", msg, retMap.get( "msg" ) );
////                }
////                for ( TOlkModelObjectDo tOlkModelObjectDo : tabList ) {
////                    truModelService.deleteById( tOlkModelObjectDo.getId() );
////                }
//                return result.setErr( msg ).getResultMap();
//            }
            result.setSingleOk( modelInfo, "新增数据列表成功" );
        }
        catch ( Exception e ) {
            logger.error( "新增数据列表失败", e );
            result.setErr( "新增数据列表失败" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "移除数据", notes = "移除数据")
    @RequestMapping(value = "/removeobject", method = {RequestMethod.POST})
    public Map<String, Object> removeObject( String id, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();

        try {
            if ( StringUtils.isBlank( id ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }
            UserDo user = LoginUtil.getUser( request );

            List<String> split = Arrays.asList( id.split( "(,|\\s)+" ) );

            Example exp = new Example( TOlkModelObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", split );
            List<TOlkModelObjectDo> dsList = truModelObjectService.findByExample( exp );
            if ( dsList.size() != split.size() ) {
                return resMap.setErr( "数据已变化，移除失败，请刷新后重试" ).getResultMap();
            }

            List<String> colIds = dsList.stream().map( x -> x.getId() ).collect( Collectors.toList() );

            String ids = "'" + colIds.stream().collect( Collectors.joining( "','" ) ) + "'";
            Long cnt = truModelObjectService.checkUse( ids );
            if ( cnt != null && cnt > 0 ) {
                return resMap.setErr( "数据使用中，不能移除" ).getResultMap();
            }

            truModelObjectService.deleteByIds( colIds );
            resMap.setOk( "移除数据成功" );
        }
        catch ( Exception e ) {
            logger.error( "移除数据失败", e );
            resMap.setErr( "移除数据失败" );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除数据", notes = "删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "点ID", dataType = "String", required = true, paramType = "query", example = "")
    })
    @RequestMapping(value = "/delobject", method = {RequestMethod.DELETE})
    public Map<String, Object> delObject( String id ) {
        ResponeMap result = genResponeMap();
        if ( StringUtils.isEmpty( id ) ) {
            return result.setErr( "点ID不能为空" ).getResultMap();
        }
        try {
            UserDo user = LoginUtil.getUser();
            TOlkModelObjectDo info = truModelObjectService.findById( id );
            if ( info == null ) {
                return result.setErr( "指定数据不存在" ).getResultMap();
            }

            Example exp = new Example( TOlkModelElementDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andEqualTo( "tcId", info.getRealObjId() ).andEqualTo( "modelId", info.getModelId() );
            int cnt = truModelElementService.findCountByExample( exp );
            if ( cnt > 0 ) {
                return result.setErr( "数据已被使用不能删除" ).getResultMap();
            }

            List<String> idList = new ArrayList<>();
            idList.add( id );
            ObjectResp<String> retMap = apiOlkDbService.delOlkModelObject( idList, user.getTokenId() );
            if ( !retMap.isSuccess() ) {
                return result.setErr( retMap.getMsg() ).getResultMap();
            }

            truModelObjectService.deleteById( id );
//            Map<String, Object> retMap = apiOlkModelService.synModel( info ,user.getTokenId() );
//            if ( retMap.containsKey( "success" ) && (boolean) retMap.get( "success" ) ) {
//                info.setSynFlag( 1 );
//                bydbModelService.updateBean( info );
//            }
//            else{
//                String msg = String.format( "保存失败");
//                if ( retMap.containsKey( "msg" ) ) {
//                    msg = String.format( "%s,错误信息：%s",msg,retMap.get( "msg" ) );
//                }
//                return resMap.setErr( msg ).getResultMap();
//            }
            result.setSingleOk( info.getId(), "删除成功" );
        }
        catch ( Exception e ) {
            logger.error( "删除失败", e );
            result.setErr( "删除失败" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "查看模型数据", notes = "查看模型数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query", example = "")
    })
    @RequestMapping(value = "/getmodelobject", method = {RequestMethod.GET})
    public Map<String, Object> getModelObject( String id ) {
        ResponeMap result = genResponeMap();
        if ( StringUtils.isEmpty( id ) ) {
            return result.setErr( "ID不能为空" ).getResultMap();
        }
        try {
            List<TOlkModelObjectDo> TOlkModelObjectDo = truModelObjectService.selectByModelId( id );
            if ( TOlkModelObjectDo == null ) {
                return result.setErr( "指定数据不存在" ).getResultMap();
            }
            result.setSingleOk( TOlkModelObjectDo, "查看数据成功" );
        }
        catch ( Exception e ) {
            logger.error( "查看数据失败", e );
            result.setErr( "查看数据失败" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "模型数据表树", notes = "模型数据表树")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query", example = ""),
            @ApiImplicitParam(name = "qryCond", value = "查询条件", dataType = "String", required = false, paramType = "query", example = "")
    })
    @RequestMapping(value = "/modelobjecttree", method = {RequestMethod.GET})
    public Map<String, Object> modelObjectTree( String id, String qryCond ) {
        ResponeMap result = genResponeMap();
        if ( StringUtils.isEmpty( id ) ) {
            return result.setErr( "ID不能为空" ).getResultMap();
        }
        logger.debug( "id:{},qryCond:{}", id, qryCond );
        List<Object> retList = new ArrayList<>();
        TOlkModelDo modelDo = truModelService.findById( id );

        List<TOlkModelObjectDo> modObjList = truModelObjectService.selectByModelId( modelDo.getId() );
        if ( StringUtils.isNotBlank( qryCond ) ) {
            String cnd = qryCond.trim();
            modObjList = modObjList.stream().filter( x -> x.getObjectName().indexOf( cnd ) >= 0 || ComUtil.trsEmpty( x.getObjChnName() ).indexOf( cnd ) >= 0 ).collect( Collectors.toList() );
        }

        List<TOlkModelObjectDo> dealList = new ArrayList<>();

        HashMap<String, Object> dMap2 = new HashMap<>();
        dMap2.put( "id", MODEL_OBJECT.getId());
        dMap2.put( "modelId", id );
        dMap2.put( "type", "node" );
        dMap2.put( "schemaId", "" );
        dMap2.put( "dbId", "" );
        dMap2.put( "npId", MODEL_OBJECT.getId());
        dMap2.put( "hasLeaf", true );
        dMap2.put( "name", MODEL_OBJECT.getName());
        dMap2.put( "tips", MODEL_OBJECT.getTitle());
        List<Object> d2List = new ArrayList<>();
        for ( TOlkModelObjectDo moDo : modObjList ) {
            dealList.add( moDo );
            HashMap<String, Object> dMap = new HashMap<>();
            dMap.put( "id", moDo.getId() );
            dMap.put( "type", "db" );
            dMap.put( "modelId", id );
            dMap.put( "stype", moDo.getStype() );
            dMap.put( "objectId", moDo.getRealObjId() );
            dMap.put( "hasLeaf", true );
            dMap.put( "name", moDo.getObjFullName() );
            dMap.put( "simpleName", moDo.getObjectName() );
            dMap.put( "objectName", moDo.getObjectName() );
            dMap.put( "objChnName", moDo.getObjChnName() );
            dMap.put( "objFullName", moDo.getObjFullName() );
            d2List.add( dMap );
        }
        if ( d2List.size() > 0 ) {
            dMap2.put( "children", d2List );
            retList.add( dMap2 );
        }

        modObjList.removeAll( dealList );

        List<HashMap<String, Object>> dList = null;
        if ( modObjList.size() > 0 ) {
            String dcId = "dcId";
            dList = new ArrayList<>();
            //idMap.put( dcId, dList );
            HashMap<String, Object> dMap = new HashMap<>();
            dMap.put( "id", dcId );
            dMap.put( "modelId", id );
            dMap.put( "type", "dc" );
            dMap.put( "schemaId", "" );
            dMap.put( "dbId", "" );
            dMap.put( "hasLeaf", true );
            dMap.put( "name", "资源已失效或已删除" );
            //dMap.put("tips", String.format("%s", nvl(dcDo.getDcName(), dcDo.getDcCode())));
            dMap.put( "children", dList );
            retList.add( dMap );
        }
        for ( TOlkModelObjectDo dat : modObjList ) {

            String priv = "资源不存在或已删除";

            if ( "odb".equals( dat.getStype() ) ) {
                HashMap<String, Object> dMap = new HashMap<>();
                dMap.put( "id", dat.getId() );
                dMap.put( "type", "db" );
                dMap.put( "modelId", id );
                dMap.put( "stype", dat.getStype() );
                dMap.put( "objectId", dat.getId() );
                dMap.put( "privFlag", null );
                dMap.put( "hasLeaf", true );
                dMap.put( "enable", 99 );
                dMap.put( "userPrivGrant", 9 );
                dMap.put( "name", dat.getObjectName() );
                dMap.put( "simpleName", dat.getObjectName() );
                dMap.put( "objectName", dat.getObjectName() );
                dMap.put( "objChnName", dat.getObjChnName() );
                dMap.put( "objFullName", dat.getObjFullName() );
                dMap.put( "tips", String.format( "%s\r\n%s", nvl( dat.getObjChnName(), dat.getObjectName() ), priv ) );
                dList.add( dMap );
            }
            else {
                HashMap<String, Object> dMap = new HashMap<>();
                dMap.put( "id", dat.getId() );
                dMap.put( "type", "ds" );
                dMap.put( "stype", dat.getStype() );
                dMap.put( "objectId", dat.getRealObjId() );
                dMap.put( "privFlag", null );
                dMap.put( "name", dat.getObjectName() );
                dMap.put( "hasLeaf", true );

                dMap.put( "enable", 99 );
                dMap.put( "userPrivGrant", 9 );

                dMap.put( "objectName", dat.getObjectName() );
                dMap.put( "objChnName", dat.getObjChnName() );
                dMap.put( "objFullName", dat.getObjFullName() );
                dMap.put( "tips", String.format( "%s\r\n%s", nvl( dat.getObjChnName(), dat.getObjectName() ), priv ) );
                dList.add( dMap );
            }
        }
        result.setSingleOk( retList, "获取模型数据表树成功" );
        return result.getResultMap();
    }

    private String nvl( String s1, String s2 ) {
        if ( StringUtils.isBlank( s1 ) ) {
            return s2;
        }
        return s1;
    }

    @ApiOperation(value = "查看数据", notes = "查看数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query", example = "")
    })
    @RequestMapping(value = "/getobject", method = {RequestMethod.GET})
    public Map<String, Object> getObject( String id ) {
        ResponeMap result = genResponeMap();
        if ( StringUtils.isEmpty( id ) ) {
            return result.setErr( "ID不能为空" ).getResultMap();
        }
        try {
            TOlkModelObjectDo TOlkModelObjectDo = truModelObjectService.findById( id );
            if ( TOlkModelObjectDo == null ) {
                return result.setErr( "指定数据不存在" ).getResultMap();
            }
            result.setSingleOk( TOlkModelObjectDo, "查看数据成功" );
        }
        catch ( Exception e ) {
            logger.error( "查看数据失败", e );
            result.setErr( "查看数据失败" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "逻辑删除模型")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "ids", value = "模型ID列表", dataType = "String", allowMultiple = true, required = true, paramType = "query", example = "")
    )
    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    public Map<String, Object> deleteModel( String[] ids ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( ids == null || ids.length == 0 ) {
                return resMap.setErr( "id列表不能为空" ).getResultMap();
            }
            int cnt = ids.length;
            boolean allDelete = true;
            for ( String id : ids ) {
                try {
                    TOlkModelDo info = truModelService.findById( id );
                    if ( info == null ) {
                        allDelete = false;
                        cnt--;
                    }
                    else {
                        info.setStatus( 9 );
                        info.setSynFlag( 0 );
                        truModelService.updateBean( info );
                    }
                }
                catch ( Exception e1 ) {
                    logger.error( "删除模型出错{}，", id, e1 );
                    allDelete = false;
                    cnt--;
                }
            }
            if ( allDelete ) {
                resMap.setOk( "删除模型成功" );
            }
            else {
                if ( cnt > 0 ) {
                    resMap.setOk( "删除模型部分成功，请刷新页面重试" );
                }
                else {
                    resMap.setErr( "删除模型失败，请刷新页面重试" );
                }
            }
        }
        catch ( Exception ex ) {
            resMap.setErr( "删除模型失败" );
            logger.error( "删除模型失败", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "批量恢复模型")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "ids", value = "模型ID列表", dataType = "String", allowMultiple = true, required = true, paramType = "query", example = "")
    )
    @RequestMapping(value = "/reback", method = {RequestMethod.DELETE})
    public Map<String, Object> reback( String ids, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( StringUtils.isBlank( ids ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }
            UserDo user = LoginUtil.getUser( request );
            List<String> split = Arrays.asList( ids.split( "(,|\\s)+" ) );

            int cnt = split.size();
            boolean allDelete = true;
            for ( String id : split ) {
                try {
                    TOlkModelDo info = truModelService.findById( id );
                    if ( info == null ) {
                        allDelete = false;
                        cnt--;
                    }
                    else {
                        TOlkModelDo temp = new TOlkModelDo();
                        temp.setId( info.getId() );
                        temp.setStatus( 4 );
                        temp.setModifiedTime( ComUtil.getCurTimestamp() );
                        truModelService.updateNoNull( temp );
                    }
                }
                catch ( Exception e1 ) {
                    logger.error( "恢复模型,", e1 );
                    allDelete = false;
                    cnt--;
                }
            }
            if ( allDelete ) {
                resMap.setOk( "恢复模型成功" );
            }
            else {
                if ( cnt > 0 ) {
                    resMap.setOk( "恢复模型部分成功，请刷新页面重试" );
                }
                else {
                    resMap.setErr( "恢复模型失败，请刷新页面重试" );
                }
            }
        }
        catch ( Exception ex ) {
            resMap.setErr( "恢复模型失败" );
            logger.error( "恢复模型失败", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "物理删除模型")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "ids", value = "模型ID列表", dataType = "String", allowMultiple = true, required = true, paramType = "query", example = "")
    )
    @RequestMapping(value = "/deletereal", method = {RequestMethod.DELETE})
    public Map<String, Object> deleteModelReal( String[] ids ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser();
            if ( ids == null || ids.length == 0 ) {
                return resMap.setErr( "id列表不能为空" ).getResultMap();
            }
            int cnt = ids.length;
            boolean allDelete = true;
            for ( String id : ids ) {
                try {
                    TOlkModelDo info = truModelService.findById( id );
                    if ( info == null ) {
                        allDelete = false;
                        cnt--;
                    }
                    else {
                        ObjectResp<String> retVal = apiOlkDbService.delOlkModel( info, user.getTokenId() );
                        if ( retVal.isSuccess() ) {
                            truModelService.deleteById( id );
                        }
                        else {
                            allDelete = false;
                            cnt--;
                        }
                    }
                }
                catch ( Exception e1 ) {
                    logger.error( e1.getMessage(), e1 );
                    allDelete = false;
                    cnt--;
                }
            }
            if ( allDelete ) {
                resMap.setOk( "删除模型成功" );
            }
            else {
                if ( cnt > 0 ) {
                    resMap.setOk( "删除模型部分成功，请刷新页面重试" );
                }
                else {
                    resMap.setErr( "删除模型失败，请刷新页面重试" );
                }
            }
        }
        catch ( Exception ex ) {
            resMap.setErr( "删除模型失败" );
            logger.error( "删除模型失败", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "组件列表")

    @RequestMapping(value = "/tree", method = {RequestMethod.GET})
    public Object tree( TOlkModelComponentDo modelInfo ) {
        ResponeMap resMap = genResponeMap();
        try {
            //状态不为0
            modelInfo.setStatus( 0 );
            List<TOlkModelComponentVo> data = truModelComponentService.findBeanListTree( modelInfo );
            resMap.setSingleOk( data, "获取成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取组件列表失败" );
            logger.error( "获取组件列表失败", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "新增组件", notes = "新增组件")
    @RequestMapping(value = "/addComponent", method = {RequestMethod.POST})
    public Map<String, Object> addComponent( @RequestBody TOlkModelComponentDo componentDo, HttpServletRequest request ) {
        ResponeMap result = genResponeMap();
        if ( StringUtils.isEmpty( componentDo.getName() ) ) {
            return result.setErr( "模型名称为空" ).getResultMap();
        }
        try {
            UserDo userDo = LoginUtil.getUser( request );
            LoginUtil.setBeanInsertUserInfo( componentDo, userDo );
            Integer id = truModelComponentService.insertBean( componentDo );
            result.setSingleOk( componentDo, "新增模型成功" );
        }
        catch ( Exception e ) {
            logger.error( "模型模型失败", e );
            result.setErr( "模型模型失败" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "模型列表")
    @RequestMapping(value = "/taskpage", method = {RequestMethod.GET})
    public Object taskPage( TOlkModelDo modelInfo, HttpServletRequest request ) {
        ResponeMap resMap = genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser( request );
            modelInfo.setCreatorAccount( userDo.getUserName() );
            MyBeanUtils.chgBeanLikeProperties( modelInfo, "name", "qryCond" );
            modelInfo.genPage();
            List<TOlkModelDo> data = truModelService.findBeanList( modelInfo );
            long cnt = truModelService.findBeanCnt( modelInfo );
            List<TBydbModelPo> result = new ArrayList<>();
            if ( cnt > 0 && data.size() > 0 ) {
                Type type = new TypeToken<List<Map<String, String>>>() {
                }.getType();
                data.stream().forEach( e -> {
                    TBydbModelPo tBydbModelVo = new TBydbModelPo();
                    List<Map<String, String>> tmpColList = JsonUtil.deserialize( e.getParamConfig(), type );
                    e.setRunSql( "" );
                    e.setParamConfig( "" );
                    try {
                        MyBeanUtils.copyBean2Bean( tBydbModelVo, e );
                    }
                    catch ( Exception ex ) {
                        ex.printStackTrace();
                    }
                    tBydbModelVo.setFields( tmpColList );
                    result.add( tBydbModelVo );
                } );
            }
            resMap.setPageInfo( modelInfo.getPageSize(), modelInfo.getCurrentPage() );
            resMap.setOk( cnt, result );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取模型列表失败" );
            logger.error( "获取模型表失败", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "数据源组件模型列表")
    @RequestMapping(value = "/page", method = {RequestMethod.GET})
    public Object page( TOlkModelDo modelInfo, HttpServletRequest request ) {
        ResponeMap resMap = genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser( request );
            modelInfo.setCreatorAccount( userDo.getUserName() );
            modelInfo.setOutputType( 1 );
            MyBeanUtils.chgBeanLikeProperties( modelInfo, "name", "qryCond" );
            modelInfo.genPage();
            List<TOlkModelDo> data = truModelService.findBeanList( modelInfo );
            long cnt = truModelService.findBeanCnt( modelInfo );
            if ( cnt > 0 && data.size() > 0 ) {
                data.stream().forEach( e -> {
                    e.setRunSql( "" );
                    e.setParamConfig( "" );
                } );
            }
            resMap.setPageInfo( modelInfo.getPageSize(), modelInfo.getCurrentPage() );
            resMap.setOk( cnt, data );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取模型列表失败" );
            logger.error( "获取模型表失败", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "执行数据集sql", notes = "执行数据集sql")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "runSql", value = "数据集sql", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "数据集id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "当前页", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "Integer", required = false, paramType = "query")

    })
    @RequestMapping(value = "/excutesql", method = {RequestMethod.GET})
    @ResponseBody
    public Object excutesql( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser();
            TOlkModelDo modelVo = new TOlkModelDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo, hru );
            if ( StringUtils.isBlank( modelVo.getRunSql() ) ) {
                return resMap.setErr( "语句不能为空" ).getResultMap();
            }
            TOlkModelDo info = truModelService.findById(modelVo.getId());
            if (info == null) {
                return resMap.setErr("模型不存在").getResultMap();
            }

            List<Map<String, Object>> list = null;
            modelVo.genPage();
            //sql = dcSql + " offset " + ((modelVo.getCurrentPage()-1) * modelVo.getPageSize()) + " limit " + modelVo.getPageSize();
            String sql = SqlTextUtil.limitSelet( modelVo.getRunSql(), modelVo.getPageSize() );
            logger.info( sql );
            if( StringUtils.isBlank( info.getDcId() )) { //跨节点运行
                try ( HetuJdbcOperate dbop = hetuJdbcOperateComponent.genHetuJdbcOperate() ) {
                    list = dbop.selectData( sql, LinkedHashMap.class );
                }
            }
            else {
                TOlkDcServerDo dcDo = dcService.findById( info.getDcId() );
                if( dcDo.getEnable() == null || dcDo.getEnable() !=1 ){
                    return  resMap.setErr( "节点未启用" ).getResultMap();
                }
                //HetuInfo hetuInfo = hetuJdbcOperateComponent.genHetuInfo( dcDo );
                try(HetuJdbcOperate dbop = hetuJdbcOperateComponent.genHetuJdbcOperate()){
                    list = dbop.selectData( sql, LinkedHashMap.class );
                }
            }

            if( list != null ){
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

            int count = 100;

            resMap.setPageInfo( modelVo.getPageSize(), modelVo.getCurrentPage() );
            return resMap.setOk( count, list, "执行数据集sql成功" ).getResultMap();
        }
        catch ( Exception ex ) {
            resMap.setErr( "执行数据集sql失败" );
            logger.error( "执行数据集sql失败:", ex );
        }
        return resMap.getResultMap();
    }


//    @ApiOperation(value = "格式化数据集sql", notes = "格式化数据集sql")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "dcSql", value = "数据集sql", dataType = "String", required = true, paramType = "query"),
//            @ApiImplicitParam(name = "id", value = "数据集id", dataType = "String", required = true, paramType = "query"),
//
//    })
//    @RequestMapping(value = "/formatsql", method = {RequestMethod.GET})
//    @ResponseBody
//    public Object formatSql(HttpServletRequest request) {
//        ResponeMap resMap = this.genResponeMap();
//        try {
//            TBydbDsContentDo modelVo = new TBydbDsContentDo();
//            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
//            new PageBeanWrapper(modelVo, hru);
//            if (StringUtils.isBlank(modelVo.getDcSql())) {
//                return resMap.setErr("语句不能为空").getResultMap();
//            }
////            BydbDatasetVo info = datasetService.findVoById(modelVo.getId());
////            if (info == null) {
////                return resMap.setErr("数据集不存在").getResultMap();
////            }
////
////            TBydbDcServerDo dcDo = dcserverService.findById(info.getDcId());
////            if (dcDo == null) {
////                return resMap.setErr("数据集对应节点不存在").getResultMap();
////            }
//
//            String sql = SQLUtils.format(modelVo.getDcSql(), JdbcConstants.MYSQL);
//            if (StringUtils.isBlank(sql)) {
//                sql = modelVo.getDcSql();
//                resMap.put("dcSql", sql);
//                return resMap.setErr("格式化数据集sql失败").getResultMap();
//            }
//            resMap.put("dcSql", sql);
//            return resMap.setOk("格式化数据集sql成功").getResultMap();
//        } catch (Exception ex) {
//            resMap.setErr("格式化数据集sql失败");
//            logger.error("格式化数据集sql失败:", ex);
//        }
//        return resMap.getResultMap();
//    }


    @ApiOperation(value = "获取模型详情", notes = "获取模型详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "模型id", dataType = "String", required = true, paramType = "query", example = "")
    })
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    @ResponseBody
    public Object exchangeInfo( String id ) {
        ResponeMap result = genResponeMap();
        if ( StringUtils.isEmpty( id ) ) {
            return result.setErr( "模型id为空" ).getResultMap();
        }
        try {
            TOlkModelDo modelInfo = truModelService.findById( id );
            result.setSingleOk( modelInfo, "获取成功" );
        }
        catch ( Exception e ) {
            logger.error( "获取失败", e );
            result.setErr( "获取失败" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "修改模型信息", notes = "修改模型信息")
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public Object update( @RequestBody TOlkModelDo modelInfo, HttpServletRequest request ) {
        ResponeMap result = genResponeMap();
        if ( StringUtils.isEmpty( modelInfo.getId() ) ) {
            return result.setErr( "模型id为空" ).getResultMap();
        }
        try {
            UserDo user = LoginUtil.getUser( request );
            TOlkModelDo info = truModelService.findById( modelInfo.getId() );
            TOlkModelDo old = new TOlkModelDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, old );
            MyBeanUtils.copyBeanNotNull2Bean( modelInfo, info );
//            if ( StringUtils.isNotBlank( info.getRunSql() ) && (!info.getRunSql().equals( old.getRunSql() ) ||
//                    StringUtils.isBlank( info.getParamConfig() ) || "[]".equals( info.getParamConfig() )) ) {
//                genViewAndColumns( info );
//            }
            info.setSynFlag( 0 );

            if( info.getBuildType()!= null && info.getBuildType() ==0 ){ //语句
                if( StringUtils.isBlank( info.getRunSql() )){
                    info.setCacheFlag( 0 );
                }
                else {
                    if ( info.getCacheFlag() == null || info.getCacheFlag() != 3 || !info.getRunSql().equals( old.getRunSql() )
                            || StringUtils.isBlank( info.getParamConfig() ) || "[]".equals( info.getParamConfig() ) ) { //语句有变化
                        info.setCacheFlag( 0 );
                        genViewAndColumns( info, user );
                    }
                }
            }

            truModelService.updateBean( info );
            result.setOk( "更新模型成功" );
        }
        catch ( Exception e ) {
            logger.error( "更新模型失败", e );
            result.setErr( "更新模型失败" );
        }
        return result.getResultMap();
    }

    public void genViewAndColumns( TOlkModelDo info ,UserDo user ) throws Exception {


        String olkVdmCatalogViewName = SysParamSetOp.readValue( "olkVdmCatalogViewName", "" );
        info.setViewName(String.format("%s.tmp_%s", olkVdmCatalogViewName, info.getId()));
        List<TBydbFieldDo> fieldList = null;
        if ( StringUtils.isBlank( info.getDcId() ) ) { //跨节点运行
            try ( HetuJdbcOperate dbop = hetuJdbcOperateComponent.genHetuJdbcOperate() ) {
                String viewSql = String.format( "CREATE OR REPLACE VIEW %s AS %s", info.getViewName(), info.getRunSql() );
                dbop.execute( viewSql );
                fieldList = loadViewColumns(dbop, info.getViewName());
            }
        }
        else {
            TOlkDcServerDo dcDo = dcService.findById( info.getDcId() );
            if ( dcDo.getEnable() == null || dcDo.getEnable() != 1 ) {
                throw new MessageException( "节点未启用" );
            }
            //HetuInfo hetuInfo = hetuJdbcOperateComponent.genHetuInfo( dcDo );
            try ( HetuJdbcOperate dbop = hetuJdbcOperateComponent.genHetuJdbcOperate() ) {
                String viewSql = String.format( "CREATE OR REPLACE VIEW %s AS %s", info.getViewName(), info.getRunSql() );
                dbop.execute( viewSql );
                fieldList = loadViewColumns(dbop, info.getViewName());
            }
        }
        info.setCacheFlag( 3 );

        List<Object> colMapList = new ArrayList<>();

            String config = info.getConfig();
            List<Map<String, String>> cfList = null;
            try {
                if (StringUtils.isNotBlank(config)) {
                    Type type = new TypeToken<List<Map<String, String>>>() {
                    }.getType();
                    cfList = JsonUtil.deserialize(config, type);
                }
            } catch (Exception e1) {
            }
            if (cfList != null && cfList.size() > 0) {
                for ( Map<String, String> map : cfList ) {
                    String fieldName = map.get( "fieldName" );

                    for ( TBydbFieldDo fieldDo : fieldList ) {
                        if ( fieldDo.getFieldName().equalsIgnoreCase( fieldName ) ) {
                            HashMap<String, Object> col = new HashMap<>();
                            col.put( "fieldName", fieldDo.getFieldName() );
                            col.put( "fieldType", fieldDo.getFieldType() );
                            col.put( "columnType", JdbcTypeToJavaTypeUtil.chgType( fieldDo.getFieldType() ) );
                            col.put( "fieldExpr", fieldDo.getFieldName() );

                            if ( StringUtils.isNotBlank( map.get( "fieldExpr" ) ) ) {
                                //fieldDo.setChnName(map.get("fieldExpr"));
                                col.put( "fieldExpr", map.get( "fieldExpr" ) );
                            }
                            fieldList.remove( fieldDo );
                            colMapList.add( col );
                            break;
                        }
                    }
                }
            }
            for (TBydbFieldDo fieldDo : fieldList) {
                HashMap<String, Object> col = new HashMap<>();
                col.put("fieldName", fieldDo.getFieldName());
                col.put("fieldType", fieldDo.getFieldType());
                col.put("columnType", JdbcTypeToJavaTypeUtil.chgType(fieldDo.getFieldType()));
                col.put("fieldExpr", fieldDo.getFieldName());
                colMapList.add(col);
            }
            //logger.info(JsonUtil.toJson(colMapList));
            info.setParamConfig(JsonUtil.toJson(colMapList));


    }

    private List<TBydbFieldDo> loadViewColumns(HetuJdbcOperate dbop, String viewName) throws Exception {

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

    }


    @ApiOperation(value = "获取模型所有元素", notes = "获取模型所有元素")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "模型ID", dataType = "String", required = true, paramType = "query", example = "")
    })
    @RequestMapping(value = "/getelements", method = {RequestMethod.GET})
    public Map<String, Object> getModelElements( String id ) {
        ResponeMap result = genResponeMap();
        if ( StringUtils.isEmpty( id ) ) {
            return result.setErr( "模型ID为空" ).getResultMap();
        }
        try {
            TOlkModelDo fModelDo = truModelService.findById( id );
            List<TOlkModelElementDo> elements = truModelElementService.selectByModelId( id );
            //隐藏配置
            elements.stream().forEach( e -> {
                e.setRunSql( "" );
                e.setConfig( "" );
                //e.setParamSql( "" );
            } );
            List<TOlkModelElementRelDo> elementRels = truModelElementRelService.selectByModelId( id );
            result.put( "nodes", elements );
            result.put( "edges", elementRels );
            result.setSingleOk( fModelDo, "获取成功" );
        }
        catch ( Exception e ) {
            logger.error( "获取模型失败", e );
            result.setErr( "获取模型失败" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "添加点", notes = "添加点")
    @RequestMapping(value = "/addelement", method = {RequestMethod.POST})
    public Map<String, Object> addElement( @RequestBody TOlkModelElementDo elementVo, HttpServletRequest request ) {
        ResponeMap result = genResponeMap();
        if ( StringUtils.isEmpty( elementVo.getName() ) ) {
            return result.setErr( "模型名称为空" ).getResultMap();
        }
        try {
            TOlkModelElementDo elementInfo = new TOlkModelElementDo();
            UserDo userDo = LoginUtil.getUser( request );
            LoginUtil.setBeanInsertUserInfo( elementInfo, userDo );
            MyBeanUtils.copyBean2Bean( elementInfo, elementVo );
            if ( StringUtils.isEmpty( elementVo.getId() ) ) {
                if ( StringUtils.isEmpty( elementVo.getModelId() ) ) {
                    return result.setErr( "模型Id为空" ).getResultMap();
                }
                TOlkModelDo modelInfo = truModelService.findById( elementVo.getModelId() );
                if ( modelInfo == null ) {
                    return result.setErr( "指定模型为空" ).getResultMap();
                }
                LoginUtil.setBeanInsertUserInfo( elementVo, request );
                modelInfo.setRunSql( "" );
                modelInfo.setCacheFlag( 0 );
                truModelService.updateBean( modelInfo );

                List<TOlkFieldDo> fieldList = null;
                if ( elementInfo.getElementType() == 1 ) {
                    String tcId = elementInfo.getTcId();
                    TOlkModelObjectDo modelObjectDo = truModelObjectService.selectByObjectId( tcId, modelInfo.getId() );
                    ObjectResp<OlkObjectWithFieldsVo> retVal = apiOlkDbService.olkTableWithSubInfo( tcId, userDo.getTokenId() );
                    if ( !retVal.isSuccess() ) {
                        return result.setErr( retVal.getMsg() ).getResultMap();
                    }
                    if(retVal.getData().getEnable() == null || retVal.getData().getEnable() !=1 ){
                        return result.setErr( "资源已失效" ).getResultMap();
                    }
                    fieldList = retVal.getData().getFieldList();
                    elementInfo.setOrigName( modelObjectDo.getObjectName() );
                }
                else {
                    if ( StringUtils.isBlank( elementInfo.getOrigName() ) ) {
                        elementInfo.setOrigName( elementInfo.getName() );
                    }
                }
                truModelElementService.insertBeanDetail( elementInfo, fieldList );
                List<TOlkModelElementDo> models = truModelElementService.selectByModelId( elementInfo.getModelId() );

                if ( elementInfo.getElementType() == 1 ) {
                    OlkBaseComponenT truBaseComponenT = new OlkTableComponent();
                    OlkNode truNode = truModelElementService.getNodes( elementInfo );
                    OlkNode data = truBaseComponenT.init( truNode, elementInfo );
                    truBaseComponenT.setModel( models );
                    truBaseComponenT.build( data, elementInfo, modelInfo );
                    elementInfo.setRunStatus( 1 );
                    truModelElementService.updateBeanDetail( elementInfo, data );
                }
            }
            else {
                truModelElementService.updateNoNull( elementInfo );
            }
            

            result.setSingleOk( elementInfo, "操作成功" );
        }
        catch ( Exception e ) {
            logger.error( "操作失败", e );
            result.setErr( "操作失败" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "移动点", notes = "移动点")
    @RequestMapping(value = "/moveelement", method = {RequestMethod.POST})
    @ApiImplicitParams({
            //  @ApiImplicitParam(name = "id", value = "点ID", dataType = "String", required = true, paramType = "query",example = "")
    })
    public Map<String, Object> moveElement( @RequestBody CoordVo coordVo ) {
        ResponeMap result = genResponeMap();
        if ( StringUtils.isEmpty( coordVo.getId() ) ) {
            return result.setErr( "点ID为空" ).getResultMap();
        }
        if ( StringUtils.isEmpty( coordVo.getX() ) || StringUtils.isEmpty( coordVo.getY() ) ) {
            return result.setErr( "x,y坐标为空" ).getResultMap();
        }
        try {
            TOlkModelElementDo elementInfo = new TOlkModelElementDo();
            MyBeanUtils.copyBean2Bean( elementInfo, coordVo );
            truModelElementService.updateNoNull( elementInfo );
            result.setOk( "移动成功" );
        }
        catch ( NumberFormatException n ) {
            result.setErr( "x,y应为数字" );
        }
        catch ( Exception e ) {
            logger.error( "更新点坐标失败", e );
            result.setErr( "更新点坐标失败" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "重复名", notes = "重复名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "点ID", dataType = "String", required = true, paramType = "query", example = ""),
            @ApiImplicitParam(name = "name", value = "点名称", dataType = "String", required = true, paramType = "query", example = "")

    })
    @RequestMapping(value = "/updateelement", method = {RequestMethod.POST})
    public Map<String, Object> updateElement( @RequestBody TOlkModelElementDo info ) {
        ResponeMap result = genResponeMap();
        if ( StringUtils.isEmpty( info.getId() ) ) {
            return result.setErr( "点ID不能为空" ).getResultMap();
        }
        try {
            TOlkModelElementDo elementInfo = truModelElementService.findById( info.getId() );
            if ( elementInfo == null ) {
                return result.setErr( "指定节点不存在" ).getResultMap();
            }
            truModelElementService.updateNoNull( info );
            result.setSingleOk( elementInfo.getId(), "重复名成功" );
        }
        catch ( Exception e ) {
            logger.error( "重复名失败", e );
            result.setErr( "重复名失败" );
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "删除点", notes = "删除点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "点ID", dataType = "String", required = true, paramType = "query", example = "")
    })
    @RequestMapping(value = "/deleteelement", method = {RequestMethod.DELETE})
    public Map<String, Object> deleteElement( String id, HttpServletRequest request ) {
        ResponeMap result = genResponeMap();
        if ( StringUtils.isEmpty( id ) ) {
            return result.setErr( "点ID不能为空" ).getResultMap();
        }
        try {
            TOlkModelElementDo elementInfo = truModelElementService.findById( id );
            if ( elementInfo == null ) {
                return result.setErr( "指定节点不存在" ).getResultMap();
            }
            TOlkModelDo modelInfo = truModelService.findById( elementInfo.getModelId() );
            modelInfo.setRunSql( "" );
            modelInfo.setCacheFlag( 0 );
            truModelService.updateBean( modelInfo );
            truModelElementService.deleteById( id );
            result.setSingleOk( elementInfo.getId(), "删除成功" );
            //异步更新整体模型配置
            //analysisRunServicr.asyncModel(elementInfo.getModelId());
        }
        catch ( Exception e ) {
            logger.error( "删除失败", e );
            result.setErr( "删除失败" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "获取模型信息", notes = "获取模型信息")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "模型id", dataType = "String", required = true, paramType = "query", example = "")
    )
    @RequestMapping(value = "/modelinfo", method = {RequestMethod.GET})
    public Map<String, Object> modelInfo( String id ) {
        ResponeMap result = genResponeMap();
        try {
            TOlkModelDo modelInfo = truModelService.findById( id );
            modelInfo.setRunSql( null );
            if ( modelInfo == null ) {
                return result.setErr( "模型不存在" ).getResultMap();
            }
            result.setSingleOk( modelInfo, "获取模型信息成功" );
        }
        catch ( Exception e ) {
            logger.error( "获取模型信息失败", e );
            result.setErr( "获取模型信息失败" );
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "查看点模板配置", notes = "查看点模板配置")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "点id", dataType = "String", required = true, paramType = "query", example = "")
    )
    @RequestMapping(value = "/elementconfiginfo", method = {RequestMethod.GET})
    public Map<String, Object> elementconfigInfo( String id ) {
        ResponeMap result = genResponeMap();
        try {
            UserDo user = LoginUtil.getUser();
            TOlkModelElementDo elementInfo = truModelElementService.findById( id );
            if ( elementInfo == null ) {
                return result.setErr( "指定节点不存在" ).getResultMap();
            }
            String modelId = elementInfo.getModelId();
            TOlkModelDo modelInfo = truModelService.findById( modelId );
            if ( modelInfo == null ) {
                return result.setErr( "模型不存在" ).getResultMap();
            }
            OlkNode data = null;
            if ( elementInfo.getElementType() == 0 ) {
                TOlkModelComponentDo tBydbModelComponentDo = truModelComponentService.findById( elementInfo.getTcId() );
                if ( tBydbModelComponentDo == null ) {
                    return result.setErr( "组件不存在" ).getResultMap();
                }
                OlkBaseComponenT baseComponenT = OlkComponentEnum.getInstanceByName( tBydbModelComponentDo.getComponentEn().toLowerCase() );
                List<TOlkModelElementDo> pre = truModelElementService.selectStartId( elementInfo.getId() );
                if ( pre.size() < 1 ) {
                    return result.setErr( "缺少前置组件" ).getResultMap();
                }
                if ( elementInfo.getRunStatus() == 0 ) { //前置组件字段已变更，重置字段
                    List<TOlkModelFieldDo> flist = new ArrayList<>();
                    for ( TOlkModelElementDo eleTmp : pre ) {
                        flist.addAll( truModelFieldService.selectByElementIdAll( eleTmp.getId() ) );
                    }
                    //flist.sort( (x,y)-> x.getFilterSort()-x.getFilterSort() );
                    List<TOlkModelFieldDo> addList = new ArrayList<>();
                    List<TOlkModelFieldDo> modList = new ArrayList<>();
                    List<TOlkModelFieldDo> delList = new ArrayList<>();
                    List<TOlkModelFieldDo> allList = truModelFieldService.selectByElementIdAll( elementInfo.getId() );
                    //if("t_datasource".equalsIgnoreCase(  elementInfo.getIcon() ) ){
                    flist = flist.stream().filter( x -> x.getIsSelect() > 0 ).collect( Collectors.toList() );
                    //}
                    Map<String, TOlkModelFieldDo> sourceMap = flist.stream().collect( Collectors.toMap( x -> x.getId(), x -> x ) );

                    String[] fieldNames = {"fieldName", "FieldAlias", "origFieldType", "origFlag", "fieldExpr", "FilterSort", "ColumnType", "FieldType"};

                    for ( TOlkModelFieldDo fieldDo : allList ) {
                        if ( StringUtils.isBlank( fieldDo.getAggregation() ) && StringUtils.isBlank( fieldDo.getOrderFunc() ) ) {
                            if ( sourceMap.containsKey( fieldDo.getFromFieldId() ) ) {
                                TOlkModelFieldDo ftmp = sourceMap.get( fieldDo.getFromFieldId() );
                                MyBeanUtils.copyBeanProp( ftmp, fieldDo, true, fieldNames );
                                //fieldDo.setAggregation( null );
                                //field.setFilterSort( norder );
                                //fieldDo.setFilterValue( null );
                                //fieldDo.setFilterConfig( null );
                                if(!(OlkComponentEnum.Collection_COMPONENT.getComponentName().equalsIgnoreCase( elementInfo.getIcon() )
                                        || OlkComponentEnum.Intersect_COMPONENT.getComponentName().equalsIgnoreCase( elementInfo.getIcon() )
                                        || OlkComponentEnum.FieldConcat_COMPONENT.getComponentName().equalsIgnoreCase( elementInfo.getIcon() )) ){
                                    fieldDo.setFilterValue( null );
                                    fieldDo.setFilterConfig( null );
                                }
                                if(!(OlkComponentEnum.FieldFunc_COMPONENT.getComponentName().equalsIgnoreCase( elementInfo.getIcon() )
                                        || OlkComponentEnum.GROUP_COMPONENT.getComponentName().equalsIgnoreCase( elementInfo.getIcon() )
                                        || OlkComponentEnum.Join_COMPONENT.getComponentName().equalsIgnoreCase( elementInfo.getIcon() )) ){
                                    fieldDo.setAggregation( null );
                                    fieldDo.setFilterSort( null );
                                }
                                fieldDo.setFieldName( ftmp.getFieldAlias() );
                                fieldDo.setFieldAlias( ftmp.getFieldAlias() );
                                fieldDo.setOrigFieldType( ftmp.getFieldType() );
                                fieldDo.setOrigFlag( 1 );

                                fieldDo.setExtendsId( ftmp.getElementId() );
                                fieldDo.setFromFieldId( ftmp.getId() );
                                fieldDo.setTableAlias( elementInfo.getElement() );
                                fieldDo.setElementId( elementInfo.getId() );
                                modList.add( fieldDo );
                                sourceMap.remove( fieldDo.getFromFieldId() );
                            }
                            else { //来源字段不存在
                                delList.add( fieldDo );
                            }
                        }
                        else{
                            modList.add( fieldDo );
                        }
                    }
                    allList.removeAll( delList );
                    allList.removeAll( modList );

                    Map<String, TOlkModelFieldDo> modFieldMap = new HashMap<>();
                    for ( TOlkModelFieldDo fieldDo : modList ) {
                        if ( StringUtils.isNotBlank( fieldDo.getFromFieldId() ) ) {
                            modFieldMap.put( fieldDo.getFromFieldId(), fieldDo );
                        }
                    }

                    int norder = 1;
                    //Map<String,TOlkModelFieldDo> nochgList = new HashMap<>();
                    for ( TOlkModelFieldDo fTmp : flist ) {
                        TOlkModelFieldDo field = modFieldMap.get( fTmp.getId() );
                        if ( field != null ) {
                            field.setFilterSort( norder );
                            norder++;
                        }
                        else {
                            field = new TOlkModelFieldDo();
                            MyBeanUtils.copyBeanProp( fTmp, field, true, fieldNames );
                            field.setIsSelect( 1 );
                            field.setExtendsId( fTmp.getElementId() );
                            field.setFromFieldId( fTmp.getId() );
                            field.setFieldName( fTmp.getFieldAlias() );
                            field.setFieldAlias( fTmp.getFieldAlias() );
                            field.setTableAlias( elementInfo.getElement() );
                            field.setOrigFieldType( fTmp.getFieldType() );
                            field.setOrderFunc( null );
                            field.setOrigFlag( 0 );
                            field.setAggregation( null );
                            field.setFilterValue( null );
                            field.setFilterConfig( null );
                            field.setFilterSort( norder++ );
                            field.setId( ComUtil.genId() );
                            field.setElementId( elementInfo.getId() );
                            LoginUtil.setBeanInsertUserInfo( field, user );
                            addList.add( field );
                        }
                        norder++;
                    }
                    sourceMap = flist.stream().collect( Collectors.toMap( x -> x.getId(), x -> x ) );
                    for ( TOlkModelFieldDo fieldDo : allList ) {
                        if ( sourceMap.containsKey( fieldDo.getFromFieldId() ) ) {
                            TOlkModelFieldDo ftmp = sourceMap.get( fieldDo.getFromFieldId() );
                            //MyBeanUtils.copyBeanProp( ftmp,fieldDo,true,fieldNames );
                            //fieldDo.setAggregation( null );
                            //field.setFilterSort( norder );
                            fieldDo.setExtendsId( ftmp.getElementId() );
                            fieldDo.setFieldName( ftmp.getFieldAlias() );
                            //fieldDo.setFieldAlias( ftmp.getFieldAlias() );
                            //fieldDo.setFilterValue( null );
                            //fieldDo.setFilterConfig( null );
                            //fieldDo.setExtendsId(  ftmp.getElementId() );
                            //fieldDo.setFromFieldId( ftmp.getId() );
                            fieldDo.setTableAlias( elementInfo.getElement() );
                            fieldDo.setElementId( elementInfo.getId() );
                            fieldDo.setFilterSort( norder++ );
                            modList.add( fieldDo );
                        }
                        else { //来源字段不存在
                            delList.add( fieldDo );
                        }
                    }

                    allList = new ArrayList<>();
                    allList.addAll( addList );
                    allList.addAll( modList );
                    HashMap<String, String> nameMap = new HashMap<>();

                    allList = allList.stream().sorted( ( x, y ) -> x.getFilterSort() - x.getFilterSort() ).collect( Collectors.toList() );

                    if ( OlkComponentEnum.Join_COMPONENT.getComponentName().equalsIgnoreCase( elementInfo.getIcon() ) ) {
                        for ( TOlkModelFieldDo field : allList ) {
                            String alias = field.getFieldAlias();
                            if ( nameMap.containsKey( alias ) ) {
                                int pos = alias.length();
                                char[] chars = alias.toCharArray();
                                for ( int i = chars.length - 1; i >= 0; i-- ) {
                                    if ( chars[i] > '0' && chars[i] < '9' ) {
                                        pos = i;
                                    }
                                    else {
                                        break;
                                    }
                                }
                                String fn = alias.substring( 0, pos );
                                logger.info( "{}:{}", fn, alias );
                                int num = 1;
                                while ( true ) {
                                    alias = String.format( "%s%d", fn, num++ );
                                    //logger.info( "{}:{}", fn, alias );
                                    if ( !nameMap.containsKey( alias ) ) {
                                        field.setFieldAlias( alias );
                                        nameMap.put( alias, alias );
                                        break;
                                    }
                                }
                            }
                            else {
                                nameMap.put( alias, alias );
                            }
                        }
                    }
                    truModelFieldService.saveFields( addList, modList, delList );
                }

                List<TOlkModelComponentDo> componentDos = truModelComponentService.findAll();
                List<TOlkModelObjectDo> datasource = truModelObjectService.selectByModelId( modelId );
                baseComponenT.setComponents( componentDos );
                baseComponenT.setDatasource( datasource );
                baseComponenT.setPreModel( pre );
                List<TOlkModelFieldDo> extenFieldList = new ArrayList<>();
                for ( TOlkModelElementDo eleTmp : pre ) {
                    extenFieldList.addAll( truModelFieldService.selectByElementIdAll( eleTmp.getId() ) );
                }
                baseComponenT.setExtendsDos( extenFieldList );
                OlkNode node = truModelElementService.getNodes( elementInfo );
                data = baseComponenT.init( node, elementInfo );
            }
            else if ( elementInfo.getElementType() == 1 ) {
                OlkBaseComponenT baseComponenT = new OlkTableComponent();
                OlkNode truNode = truModelElementService.getNodes( elementInfo );
                data = baseComponenT.init( truNode, elementInfo );
            }
            data.setDbSource( null );
            result.put( "element", elementInfo );
            result.setSingleOk( data, "获取成功" );

        }
        catch ( Exception e ) {
            logger.error( "获取失败", e );
            result.setErr( "获取失败" ).getResultMap();
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "添加组件配置", notes = "添加组件配置")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/addelementconfig", method = {RequestMethod.POST})
    public Map<String, Object> addElementConfig( @RequestBody OlkNode params ) {
        ResponeMap result = genResponeMap();
        try {
            UserDo user = LoginUtil.getUser();
            String elementId = params.getId();
            TOlkModelElementDo elementInfo = truModelElementService.findById( elementId );
            if ( elementInfo == null ) {
                return result.setErr( "指定节点不存在" ).getResultMap();
            }
            TOlkModelDo info = truModelService.findById( elementInfo.getModelId() );
            OlkCheckComponent component = analysisRunServicr.checkField( params, elementInfo );
            if ( !component.isSuccess() ) {
                return result.setErr( component.getMessage() ).getResultMap();
            }
            List<TOlkModelElementDo> models = truModelElementService.selectByModelId( elementInfo.getModelId() );
            if ( elementInfo.getElementType() == 0 ) {

                TOlkModelComponentDo tBydbModelComponentDo = truModelComponentService.findById( elementInfo.getTcId() );
                if ( tBydbModelComponentDo == null ) {
                    return result.setErr( "组件不存在" ).getResultMap();
                }

                OlkBaseComponenT baseComponenT = OlkComponentEnum.getInstanceByName( tBydbModelComponentDo.getComponentEn().toLowerCase() );
                List<TOlkModelObjectDo> datasource = truModelObjectService.selectByModelId( elementInfo.getModelId() );
                List<TOlkModelElementDo> pre = truModelElementService.selectStartId( elementInfo.getId() );
                List<TOlkModelElementDo> next = truModelElementService.selectEndId( elementInfo.getId() );
                List<TOlkModelComponentDo> componentDos = truModelComponentService.findAll();
                baseComponenT.setDatasource( datasource );
                baseComponenT.setModel( models );
                baseComponenT.setNextModel( next );
                baseComponenT.setPreModel( pre );
                baseComponenT.setComponents( componentDos );
                List<TOlkModelFieldDo> extFieldList = new ArrayList<>();
                for ( TOlkModelElementDo elementDo : pre ) {
                    extFieldList.addAll( truModelFieldService.selectByElementIdAll( elementDo.getId() ) );
                }
                baseComponenT.setExtendsDos( extFieldList );
                //判断组件配置是否正确
                OlkCheckComponent checkComponent = baseComponenT.check( params, elementInfo );
                if ( !checkComponent.isSuccess() ) {
                    return result.setErr( checkComponent.getMessage() ).getResultMap();
                }

                baseComponenT.build( params, elementInfo, info );
                baseComponenT.changeSameFieldName( params, elementInfo );
            }
            else if ( elementInfo.getElementType() == 1 ) {
                OlkBaseComponenT baseComponenT = new OlkTableComponent();
                baseComponenT.setModel( models );
                baseComponenT.build( params, elementInfo, info );
                elementInfo.setTotal( params.getTotal() );
            }
            int sortOrder = 1;
            for ( TOlkModelFieldDo field : params.getOperators().getFields() ) {
                field.setColumnType( JdbcTypeToJavaTypeUtil.chgType( field.getFieldType() ) );
                field.setFilterSort( sortOrder++ );
                if ( StringUtils.isBlank( field.getOrigFieldType() ) ) {
                    field.setOrigFieldType( field.getFieldType() );
                }
            }
            elementInfo.setRunStatus( 1 );
            truModelElementService.updateBeanDetail( elementInfo, params );
            //异步更新整体模型配置
            TOlkModelElementDo tmpEle = new TOlkModelElementDo();
            tmpEle.setModelId( elementInfo.getModelId() );
            tmpEle.setElementType( 1 );
            List<TOlkModelElementDo> tabEleList = truModelElementService.find( tmpEle );
            HashMap<String, FDatasourceDo> tabSourceMap = new HashMap<>();
            for ( TOlkModelElementDo eleInfo : tabEleList ) {
//                if ( !tabSourceMap.containsKey( eleInfo.getTcId() ) ) {
//                    ObjectResp<FDatasourceDo> retVal = apiTruModelService.tableDbSource( eleInfo.getTcId(), user.getTokenId() );
//                    if ( !retVal.isSuccess() ) {
//                        return result.setErr( retVal.getMsg() ).getResultMap();
//                    }
//                    tabSourceMap.put( eleInfo.getTcId(), retVal.getData() );
//                }
            }
            analysisRunServicr.asyncModel( elementInfo.getModelId(), tabSourceMap );

            result.setOk( "成功" );
        }
        catch ( Exception e ) {
            logger.error( "添加失败", e );
            result.setErr( "添加失败" ).getResultMap();
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "添加边", notes = "添加边")
    @RequestMapping(value = "/addelementrel", method = {RequestMethod.POST})
    public Map<String, Object> addElementRel( @RequestBody TOlkModelElementRelDo elementRelInfo, HttpServletRequest request ) {
        ResponeMap result = genResponeMap();
        if ( StringUtils.isEmpty( elementRelInfo.getModelId() ) ) {
            return result.setErr( "模型ID为空" ).getResultMap();
        }
        if ( StringUtils.isEmpty( elementRelInfo.getStartElementId() ) ||
                StringUtils.isEmpty( elementRelInfo.getEndElementId() ) ) {
            return result.setErr( "起始点ID为空" ).getResultMap();
        }

        try {
            TOlkModelDo modelInfo = truModelService.findById( elementRelInfo.getModelId() );
            if ( modelInfo == null ) {
                return result.setErr( "指定模型为空" ).getResultMap();
            }
            TOlkModelElementDo startElement = truModelElementService.findById( elementRelInfo.getStartElementId() );
            TOlkModelElementDo endElement = truModelElementService.findById( elementRelInfo.getEndElementId() );
            if ( endElement == null || startElement == null ) {
                return result.setErr( "指定节点不存在" ).getResultMap();
            }
            if ( startElement.getRunStatus() != 1 ) {
                return result.setErr( "前置组件未保存" ).getResultMap();
            }
            if ( endElement.getElementType() == 1 ) {
                return result.setErr( "不支持的连接方向，表组件只能为前置节点" ).getResultMap();
            }
            List<TOlkModelElementRelDo> elementList = truModelElementRelService.selectByExist( startElement.getId(), endElement.getId() );
            if ( elementList.size() > 0 ) {
                return result.setErr( "该边已存在" ).getResultMap();
            }

            UserDo userDo = LoginUtil.getUser( request );
            LoginUtil.setBeanInsertUserInfo( elementRelInfo, userDo );
            List<TOlkModelElementDo> models = truModelElementService.selectByModelId( elementRelInfo.getModelId() );
            List<TOlkModelFieldDo> list = null;
            List<TOlkModelFieldDo> fieldDos = truModelFieldService.selectByElementId( elementRelInfo.getStartElementId() );

            if ( startElement.getElementType() == 0 ) {

                TOlkModelComponentDo tBydbModelComponentDo = truModelComponentService.findById( startElement.getTcId() );
                if ( tBydbModelComponentDo == null ) {
                    return result.setErr( "组件不存在" ).getResultMap();
                }
                OlkBaseComponenT baseComponenT = OlkComponentEnum.getInstanceByName( tBydbModelComponentDo.getComponentEn().toLowerCase() );
                List<TOlkModelComponentDo> componentDos = truModelComponentService.findAll();
                baseComponenT.setModel( models );
                baseComponenT.setExtendsDos( fieldDos );
                baseComponenT.setComponents( componentDos );
                list = baseComponenT.relExtends( elementRelInfo );
            }
            else if ( startElement.getElementType() == 1 ) {
                OlkBaseComponenT baseComponenT = new OlkTableComponent();
                baseComponenT.setExtendsDos( fieldDos );
                baseComponenT.setModel( models );
                list = baseComponenT.relExtends( elementRelInfo );
            }
            else {
                return result.setErr( "组件类型不存在" ).getResultMap();
            }
            List<String> endIdList = new ArrayList<>();
            endIdList.add( elementRelInfo.getEndElementId() );
            List<TOlkModelElementRelDo> relList = truModelElementRelService.selectByModelId( modelInfo.getId() );
            boolean bfound = true;
            while ( bfound ) {
                bfound = false;
                for ( TOlkModelElementRelDo relDo : relList ) {
                    if ( endIdList.indexOf( relDo.getStartElementId() ) >= 0 && endIdList.indexOf( relDo.getEndElementId() ) < 0 ) {
                        endIdList.add( relDo.getEndElementId() );
                        bfound = true;
                    }
                }
            }

            List<TOlkModelElementDo> updateList = new ArrayList<>();
            for ( String s : endIdList ) {
                TOlkModelElementDo upd = new TOlkModelElementDo();
                upd.setId( s );
                upd.setRunStatus( 0 );
                updateList.add( upd );
            }

            modelInfo.setRunSql( "" );
            modelInfo.setCacheFlag( 0 );
            truModelService.updateBean( modelInfo );

            truModelElementRelService.insertBeanDetail( elementRelInfo, list, updateList );

            result.setSingleOk( elementRelInfo.getId(), "添加成功" );
        }
        catch ( Exception e ) {
            logger.error( "添加失败", e );
            result.setErr( "添加失败" );//注释
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "删除边", notes = "删除边")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "边ID", dataType = "String", required = true, paramType = "query", example = "")
    )
    @RequestMapping(value = "/deleteelementrel", method = {RequestMethod.DELETE})
    public Map<String, Object> deleteElementRel( String id ) {
        ResponeMap result = genResponeMap();
        if ( StringUtils.isEmpty( id ) ) {
            return result.setErr( "边ID为空" ).getResultMap();
        }
        try {
            TOlkModelElementRelDo elementRelInfo = truModelElementRelService.findById( id );
            if ( elementRelInfo == null ) {
                return result.setErr( "指定边不存在" ).getResultMap();
            }

            TOlkModelDo modelInfo = truModelService.findById( elementRelInfo.getModelId() );

            List<String> endIdList = new ArrayList<>();
            endIdList.add( elementRelInfo.getEndElementId() );
            List<TOlkModelElementRelDo> relList = truModelElementRelService.selectByModelId( elementRelInfo.getModelId() );
            relList = relList.stream().filter( x -> !x.getId().equals( elementRelInfo.getId() ) ).collect( Collectors.toList() );
            boolean bfound = true;
            while ( bfound ) {
                bfound = false;
                for ( TOlkModelElementRelDo relDo : relList ) {
                    if ( endIdList.indexOf( relDo.getStartElementId() ) >= 0 && endIdList.indexOf( relDo.getEndElementId() ) < 0 ) {
                        endIdList.add( relDo.getEndElementId() );
                        bfound = true;
                    }
                }
            }

            List<TOlkModelElementDo> updateList = new ArrayList<>();
            for ( String s : endIdList ) {
                TOlkModelElementDo upd = new TOlkModelElementDo();
                upd.setId( s );
                upd.setRunStatus( 0 );
                updateList.add( upd );
            }

            modelInfo.setRunSql( "" );
            modelInfo.setCacheFlag( 0 );
            truModelService.updateBean( modelInfo );

            truModelElementRelService.deleteDetail( id, updateList );
            result.setSingleOk( elementRelInfo.getId(), "删除成功" );
            ;
        }
        catch ( Exception e ) {
            logger.error( "删除失败", e );
            result.setErr( "删除失败" );
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "数据血缘", notes = "数据血缘")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据集id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/bloodkinship", method = {RequestMethod.GET})
    @ResponseBody
    public Object bloodKinship( String id, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {

            UserDo user = LoginUtil.getUser( request );
            if ( user == null || StringUtils.isBlank( user.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
            TOlkModelDo info = truModelService.findById( id );
            if ( info == null ) {
                return resMap.setErr( "联邦模型不存在" ).getResultMap();
            }
//
//            if ( StringUtils.isBlank( info.getRunSql() ) ) {
//                return resMap.setErr( "数据分析不完整" ).getResultMap();
//            }
            if( info.getBuildType() == 1 ){ //

                List<TOlkModelElementDo> elementList = truModelElementService.selectByModelId( id );
                List<TOlkModelElementRelDo> elementRelList = truModelElementRelService.selectByModelId( id );
                List<TOlkModelFieldDo> fieldList = truModelFieldService.selectByModelId( id );
                //List<TOlkModelComponentDo> componentDos = truModelComponentService.findAll();
                //List<TOlkModelObjectDo> datasource = truModelObjectService.findAll();
                HashMap<String,Object> retMap = new HashMap<>();
                List<Object> nodeList = new ArrayList<>();
                List<Object> lineList = new ArrayList<>();

                HashMap<String,TOlkModelFieldDo> fieldMap = new HashMap<>();
                for ( TOlkModelFieldDo tmp : fieldList ) {
                    //if( StringUtils.isNotBlank( tmp.getFromFieldId() ) ) {
                        fieldMap.put( tmp.getId(), tmp );
                    //}
                }
                double x = 0;
                double y = 0;

                for ( TOlkModelElementDo tmp : elementList ) {
                    double x1 = Double.parseDouble( ComUtil.trsEmpty( tmp.getX(), "0" ) );
                    double y1 = Double.parseDouble( ComUtil.trsEmpty( tmp.getY(), "0" ) );
                    x = Math.min( x,x1 );
                    y = Math.min( y,y1 );
                }
                x+=-20;
                y+=-20;
                DecimalFormat df = new DecimalFormat("##0");
                for ( TOlkModelElementDo tmp : elementList ) {
                    double x1 = Double.parseDouble( ComUtil.trsEmpty( tmp.getX(), "0" ) );
                    double y1 = Double.parseDouble( ComUtil.trsEmpty( tmp.getY(), "0" ) );
                    tmp.setX( df.format(  x1 -x ) );
                    tmp.setY( df.format(  y1 -y ) );
                }
                for ( TOlkModelElementDo tmp : elementList ) {
                    HashMap<String,Object> node = new HashMap<>();

                    node.put( "left", Double.parseDouble( ComUtil.trsEmpty( tmp.getX(),"0") ) );
                    node.put( "top",Double.parseDouble( ComUtil.trsEmpty( tmp.getY(),"0") ) );
                    node.put( "id",tmp.getId() );
                    node.put( "name",tmp.getName() );
                    if( tmp.getElementType() ==1 ) {
                        node.put( "type", "table" );
                    }
                    else{
                        node.put( "type", "comp" );
                    }
                    List<String> fldList = new ArrayList<>();
                    List<Object> fldDataList = new ArrayList<>();
                    for ( TOlkModelFieldDo fld : fieldList ) {
                        if( fld.getIsSelect() == 1 && tmp.getId().equals( fld.getElementId() )){
                            fldList.add( fld.getFieldName() );
                            HashMap<String,Object> datMap = new HashMap<>();
                            datMap.put( "id", fld.getId() );
                            datMap.put( "fieldName",fld.getFieldName() );
                            datMap.put( "fieldAlias" , fld.getFieldAlias());
                            datMap.put( "fieldExpr" , fld.getFieldExpr());
                            fldDataList.add( datMap );
                        }
                    }
                    node.put( "list", fldList );
                    node.put( "fields", fldDataList );
                    nodeList.add( node );
                }
                retMap.put( "nodeList" ,nodeList);

                for ( TOlkModelFieldDo fld : fieldList ) {
                    if( fld.getIsSelect() == 1 && StringUtils.isNotBlank( fld.getFromFieldId() ) ){
                        HashMap<String,Object> datMap = new HashMap<>();
                        TOlkModelFieldDo from = fieldMap.get( fld.getFromFieldId() );
                        if( from != null ) {
                            //datMap.put( "from", String.format( "%s_%s_in",  from.getElementId(), from.getFieldName()) );
                            //datMap.put( "to", String.format( "%s_%s_out",  fld.getElementId(), fld.getFieldName()) );
                            datMap.put( "from", String.format( "%s_%s_out",from.getElementId(), fld.getFromFieldId()) );
                            datMap.put( "to", String.format( "%s_%s_in",fld.getElementId(), fld.getId() ) );

                            //datMap.put( "from", String.format( "%s_out",fld.getFromFieldId()) );
                            //datMap.put( "to", String.format( "%s_in",fld.getId() ) );

                            lineList.add( datMap );
                        }
                        if( StringUtils.isNotBlank( fld.getFilterConfig() )){
                            JsonObject jsonObject = JsonUtil.toJsonObject( fld.getFilterConfig() );
                            Set<String> keySet = jsonObject.keySet();
                            for ( String keyid : keySet ) {
                                JsonObject subJson = jsonObject.getAsJsonObject( keyid );
                                for ( String id2 : subJson.keySet() ) {
                                    //String idname = subJson.get( id2 ).getAsString();
                                    datMap = new HashMap<>();
                                    TOlkModelFieldDo relField = fieldMap.get( id2 );
                                    relField = fieldMap.get( relField.getFromFieldId() );
                                    datMap.put( "from", String.format( "%s_%s_out",keyid, relField.getId()) );
                                    datMap.put( "to", String.format( "%s_%s_in",fld.getElementId(), fld.getId() ) );

                                    //datMap.put( "from", String.format( "%s_out",relField.getId()) );
                                    //datMap.put( "to", String.format( "%s_in",fld.getId() ) );
                                    lineList.add( datMap );
                                }
                            }
                        }
                        else if( StringUtils.isNotBlank( fld.getFilterValue() )){
                            TOlkModelElementRelDo relTmp = new TOlkModelElementRelDo();
                            relTmp.setEndElementId( fld.getElementId() );
                            List<String> relIdList = truModelElementRelService.find( relTmp ).stream().map(rel ->rel.getStartElementId()).filter( rel -> !rel.equals( fld.getExtendsId() ) ).collect( Collectors.toList() );
                            for ( String nodeId : relIdList ) {
                                datMap = new HashMap<>();
                                List<String> sameName = new ArrayList<>();
                                for ( TOlkModelFieldDo olkf : fieldList ) {
                                    if ( nodeId.equals( olkf.getElementId() ) && fld.getFilterValue().equals( olkf.getFieldAlias() ) ) {

                                        if( sameName.indexOf(  olkf.getId() )>=0 ) continue;
                                        sameName.add( olkf.getId() );
                                        datMap.put( "from", String.format( "%s_%s_out", nodeId, olkf.getId() ) );
                                        datMap.put( "to", String.format( "%s_%s_in", fld.getElementId(), fld.getId() ) );

                                        //datMap.put( "from", String.format( "%s_out",olkf.getId()) );
                                        //datMap.put( "to", String.format( "%s_in",fld.getId() ) );
                                        lineList.add( datMap );
                                    }
                                }
                            }
                        }
                    }
                }
                retMap.put( "lineList",lineList );

//                for ( TOlkModelElementRelDo tOlkModelElementRelDo : elementRelList ) {
//
//                }
                return resMap.setSingleOk( retMap,"获取成功" ).getResultMap();
            }
//            boolean bok = createTable( resMap, info );
//            if(!bok){
//                return resMap.getResultMap();
//            }
//            logger.debug( "{}", info.getRunSql() );

            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put( "modelId", info.getId() );
            dataMap.put( "taskNo", info.getCacheTaskNo() );
            dataMap.put( "taskName", String.format( "%s", info.getName().replaceAll( "\\s+|\\'|\\\"", "" ) ) );
            dataMap.put( "userName", user.getUserName() );
            dataMap.put( "userChnName", user.getChnName() );
            dataMap.put( "sql", info.getRunSql() );
            String setInfo1 = JsonUtil.toJson( dataMap );
            String setInfo2 = Base64.getEncoder().encodeToString( setInfo1.getBytes() );
            logger.debug( "{}\r\n{}", setInfo1, setInfo2 );
            //HttpOperaterUtil hou = new HttpOperaterUtil();
            HashMap<String, String> paraMap = new HashMap<>();
            paraMap.put( "setInfo", setInfo2 );

            String retVal = modelTaskFlinkApiService.bloodShip( paraMap );
            logger.info( retVal );
            return retVal;
//
//            BloodKinshipProcess bloodKinshipProcess = new BloodKinshipProcess();
//            boolean b = bloodKinshipProcess.processSql(info.getRunSql());
//            List<BlTableInfo> tableList = bloodKinshipProcess.getTableList();
//
//            List<Object> relList = bloodKinshipProcess.getRelList();
//
//            HashMap<String, Object> retMap = new HashMap<>();
//            retMap.put("tables", tableList);
//            retMap.put("relations", relList);
//
//            resMap.setSingleOk(retMap, "获取数据血缘成功");
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取数据血缘失败" );
            logger.error( "获取数据血缘失败:", ex );
        }
        return resMap.getResultMap();
    }
    private boolean createTable( ResponeMap resMap,TOlkModelDo info ) throws Exception {

        if ( "flink".equalsIgnoreCase( info.getConfig() ) || "tee".equalsIgnoreCase( info.getConfig() ) ) {
            List<TOlkModelElementDo> eleList = truModelElementService.selectByModelId( info.getId() );
            List<TOlkModelElementDo> meList = eleList.stream().filter( x -> "t_datasource".equals( x.getIcon() ) ).collect( Collectors.toList() );
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
                    resMap.setErr( "结果只能为一个组件" ).getResultMap();
                    return false;
                }
                TOlkModelElementDo elementDo = eleList.stream().filter( x -> x.getId().equals( nodeList.get( 0 ) ) ).collect( Collectors.toList() ).get( 0 );
                String tableName = info.getViewName();
                try ( IJdbcOp jdbcOp = new JdbcOpBuilder()//.withCatalog( dsDo.getDsDatabase() ).withSchema( dsDo.getDsSchema() )
                        .withSet( outDbSet.getDstype(), outDbSet.getDriver(), outDbSet.getJdbcUrl(), outDbSet.getUser(), outDbSet.getPassword() ).build() ) {

                    List<TOlkModelFieldDo> fieldList = truModelFieldService.selectByElementIdAll( elementDo.getId() ).stream().filter( x -> x.getIsSelect() != null && x.getIsSelect() == 1 ).collect( Collectors.toList() );
                    StringBuilder sb = new StringBuilder();
                    sb.append( "DROP TABLE IF EXISTS " ).append( tableName ).append( ";\r\n" );
                    sb.append( "CREATE TABLE " ).append( tableName ).append( "(\r\n" );
                    int size = fieldList.size();
                    List<String> field2List = new ArrayList<>();
                    for ( TOlkModelFieldDo field : fieldList ) {
                        field2List.add("".concat(field.getFieldAlias()).concat(" ").concat( DbTypeToFlinkType.chgType(field.getFieldType())));
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
                }
            }
            for ( TOlkModelElementDo meData : meList ) {
                String config = meData.getConfig();
                logger.debug( "{},{}:{}", info.getName(), meData.getName(), config );
                if ( StringUtils.isBlank( config ) ) {
                    resMap.setErr( String.format( "%s未配置输出", meData.getName() ) ).getResultMap();
                    return false;
                }
                JsonObject jsonObject = JsonUtil.toJsonObject( config );
                //{  "datasourceId": "814791acd5d54b578c241fe53b752f0e",  "tableName": "test11"}
                if ( !jsonObject.has( "datasourceId" ) || !jsonObject.has( "tableName" ) ) {
                    resMap.setErr( String.format( "%s配置不完整", meData.getName() ) ).getResultMap();
                    return false;
                }
                String tableName = jsonObject.get( OlkDataSourceOutPutComponent.paraTableName ).getAsString();
                String datasourceId = jsonObject.get( OlkDataSourceOutPutComponent.paraDsId ).getAsString();
                String dbschema = jsonObject.get( OlkDataSourceOutPutComponent.paraSchemaName ).getAsString();
                String sinkType = jsonObject.get( OlkDataSourceOutPutComponent.paraSinkType ) == null || jsonObject.get( OlkDataSourceOutPutComponent.paraSinkType ).isJsonNull() ? "1" : jsonObject.get( OlkDataSourceOutPutComponent.paraSinkType ).getAsString();
                FDatasourceDo dsDo = dbSourceService.findById( datasourceId );
                if ( dsDo == null ) {
                    resMap.setErr( String.format( "%s数据源不存在", meData.getName() ) ).getResultMap();
                    return false;
                }
                if ( JdbcOpBuilder.dbOpenLooKeng.equalsIgnoreCase( dsDo.getDsType() ) ) {
                    resMap.setErr( String.format( "%s的数据源%s不能做为输出数据源", meData.getName(), dsDo.getDsName() ) ).getResultMap();
                    return false;
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
                        resMap.setErr( String.format( "%s的数据源%s配置失败", meData.getName(), dsDo.getDsName() ) ).getResultMap();
                        return false;
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
//
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
                            resMap.setErr( String.format( "%s的输出表创建失败", meData.getName(), dsDo.getDsName() ) );
                            return false;
                        }
                    }
                }
            }

        }
        else {
            resMap.setErr(  "非flink模型" ) ;
            return false;
        }

        return true;
}

    @ApiOperation(value = "节点联邦分析树", notes = "节点联邦分析树")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "type", value = "类型", dataType = "String", required = false, paramType = "form"),
            //@ApiImplicitParam(name = "id", value = "上级id", dataType = "String", required = false, paramType = "form")
    })
    @RequestMapping(value = "/dcmodeltree", method = {RequestMethod.GET})
    @ResponseBody
    public Object dcModelTree( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser( request );

            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            String qryCond = hru.getNvlPara( "qryCond" );
//            String type = hru.getNvlPara("type");
//            if (type == null || type.equals("")) {
//                type = "root";
//            }
            //String connectChar = "^";
            //String splitChar = "\\^";

            String sql = "";
            List<Object> dataList = new ArrayList<>();

            TOlkModelFolderDo typeTmp = new TOlkModelFolderDo();
            logger.info( "{}", user );
            typeTmp.setUserAccount( user.getUserName() );
            typeTmp.setUserDeptNo( user.getOrgNo() );
            //typeTmp.setPid( "#NULL#" );
            final List<TOlkModelFolderDo> typeList = folderService.findBeanList( typeTmp );

            TOlkModelDo dbTmp = new TOlkModelDo();
            if ( StringUtils.isNotBlank( qryCond ) ) {
                dbTmp.setQryCond( "%" + qryCond.trim() + "%" );
            }
            dbTmp.setConfig( "olk" );
            dbTmp.setCreatorAccount( user.getUserName() );
//            AuthRoleLevelBean authRoleLevelBean = menuUtil.getMenuCls().userSystemAuthLevel(setOp, user);
//            logger.debug("用户数据权限:{},{},{}",authRoleLevelBean.getLevel(),authRoleLevelBean.getCode(),authRoleLevelBean.getName());

            dbTmp.setCreateDeptNo( user.getOrgNo() );

//            if( authRoleLevelBean.getLevel() ==1 ){
//
//            }
//            else if( authRoleLevelBean.getLevel() >=4 ){
//                dbTmp.setCreatorAccount(user.getUserName());
//            }
//            else if( authRoleLevelBean.getLevel() >=2 && authRoleLevelBean.getLevel() <4  ){
//
//            }

            //dbTmp.setStatus(4);

            List<TOlkModelDo> dsList = truModelService.findBeanList( dbTmp );

            HashMap<String, Object> node = new HashMap<>();
            node.put( "type", "root" );
            node.put( "id", "root" );
            node.put( "relId", "root" );
            node.put( "name", "联邦分析" );
            node.put( "hasLeaf", true );
            node.put( "tips", "联邦分析" );

            List<Object> fnList = makeTree( typeList, "", dsList );
//            for ( TOlkModelFolderDo folderDo : typeList ) {
//                HashMap<String, Object> fn = new HashMap<>();
//                fn.put( "type", "folder" );
//                fn.put( "id", folderDo.getId() );
//                fn.put( "relId", folderDo.getId() );
//                fn.put( "name", folderDo.getFolderName() );
//                fn.put( "hasLeaf", true );
//                fn.put( "tips", String.format( "%s", folderDo.getFolderName() ) );
//                fnList.add( fn );
//                List<Object> dsNodeList = new ArrayList<>();
//                List<TOlkModelDo> delList = new ArrayList<>();
//                for ( TOlkModelDo dsDo : dsList ) {
//                    if ( folderDo.getId().equals( dsDo.getFolderId() ) ) {
//                        HashMap<String, Object> dn = new HashMap<>();
//                        dn.put( "type", "model" );
//                        dn.put( "stype", dsDo.getBuildType() );
//                        //dn.put("enable", dsDo());
//                        dn.put( "id", dsDo.getId() );
//                        dn.put( "relId", dsDo.getId() );
//                        dn.put( "folderId", dsDo.getFolderId() );
//                        dn.put( "name", dsDo.getName() );
//                        dn.put( "hasLeaf", true );
//                        dn.put( "tips", String.format( "%s", dsDo.getName() ) );
//                        dsNodeList.add( dn );
//                        delList.add( dsDo );
//                    }
//                }
//                dsList.removeAll( delList );
//                if ( dsNodeList.size() > 0 ) {
//                    fn.put( "children", dsNodeList );
//                }
//            }
//
//            //List<Object> dsNodeList = new ArrayList<>();
//            for ( TOlkModelDo dsDo : dsList ) {
//                //if (StringUtils.isBlank(dsDo.getFolderId())) {
//                HashMap<String, Object> dn = new HashMap<>();
//                dn.put( "type", "model" );
//                dn.put( "stype", dsDo.getBuildType() );
//                //dn.put("enable", dsDo());
//                dn.put( "id", dsDo.getId() );
//                dn.put( "relId", dsDo.getId() );
//                dn.put( "folderId", dsDo.getFolderId() );
//                dn.put( "name", dsDo.getName() );
//                dn.put( "hasLeaf", true );
//                dn.put( "tips", String.format( "%s", dsDo.getName() ) );
//                fnList.add( dn );
//                //}
//            }

            //if ( fnList.size() > 0 ) {
            node.put( "children", fnList );
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

    private List<Object> makeTree( final List<TOlkModelFolderDo> typeList, String pid, final List<TOlkModelDo> dsList) {
        List<Object> fnList = new ArrayList<>();
        for ( TOlkModelFolderDo folderDo : typeList ) {
            if ( !ComUtil.trsEmpty( pid ).equals( ComUtil.trsEmpty( folderDo.getPid() ) ) ) {
                continue;
            }
            HashMap<String, Object> fn = new HashMap<>();
            fn.put( "type", "folder" );
            fn.put( "id", folderDo.getId() );
            fn.put( "relId", folderDo.getId() );
            fn.put( "name", folderDo.getFolderName() );
            fn.put( "hasLeaf", true );
            fn.put( "tips", String.format( "%s", folderDo.getFolderName() ) );
            fnList.add( fn );
            List<Object> subList = makeTree( typeList, folderDo.getId(), dsList );
            //if( subList != null && subList.size()>0 ){
                fn.put( "children",subList );
            //}
            //List<Object> dsNodeList = new ArrayList<>();
            //List<TOlkModelDo> delList = new ArrayList<>();
//            for ( TOlkModelDo dsDo : dsList ) {
//                if ( folderDo.getId().equals( dsDo.getFolderId() ) ) {
//                    HashMap<String, Object> dn = new HashMap<>();
//                    dn.put( "type", "model" );
//                    dn.put( "stype", dsDo.getBuildType() );
//                    //dn.put("enable", dsDo());
//                    dn.put( "id", dsDo.getId() );
//                    dn.put( "relId", dsDo.getId() );
//                    dn.put( "folderId", dsDo.getFolderId() );
//                    dn.put( "name", dsDo.getName() );
//                    dn.put( "hasLeaf", true );
//                    dn.put( "tips", String.format( "%s", dsDo.getName() ) );
//                    dsNodeList.add( dn );
//                    delList.add( dsDo );
//                }
//            }
//            dsList.removeAll( delList );
//            if ( dsNodeList.size() > 0 ) {
//                fn.put( "children", dsNodeList );
//            }
        }

        //List<Object> dsNodeList = new ArrayList<>();
        for ( TOlkModelDo dsDo : dsList ) {
            if ( ComUtil.trsEmpty( pid ).equals( ComUtil.trsEmpty( dsDo.getFolderId() ) ) ) {
                HashMap<String, Object> dn = new HashMap<>();
                dn.put( "type", "model" );
                dn.put( "stype", dsDo.getBuildType() );
                //dn.put("enable", dsDo());
                dn.put( "id", dsDo.getId() );
                dn.put( "relId", dsDo.getId() );
                dn.put( "folderId", dsDo.getFolderId() );
                dn.put( "name", dsDo.getName() );
                dn.put( "hasLeaf", true );
                dn.put( "tips", String.format( "%s", dsDo.getName() ) );
                fnList.add( dn );
            }
        }
        return fnList;

//        if ( fnList.size() > 0 ) {
//            node.put( "children", fnList );
//        }
//        dataList.add( node );
    }

    @ApiOperation(value = "获取点字段", notes = "获取点字段")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "点id", dataType = "String", required = true, paramType = "query", example = "")
    )
    @RequestMapping(value = "/elementfieldlist", method = {RequestMethod.GET})
    public Map<String, Object> elementfieldlist( String id ) {
        ResponeMap result = genResponeMap();
        try {
            UserDo user = LoginUtil.getUser();
            TOlkModelElementDo elementInfo = truModelElementService.findById( id );
            if ( elementInfo == null ) {
                return result.setErr( "指定节点不存在" ).getResultMap();
            }
            String modelId = elementInfo.getModelId();
            TOlkModelDo modelInfo = truModelService.findById( modelId );
            if ( modelInfo == null ) {
                return result.setErr( "模型不存在" ).getResultMap();
            }
            List<TOlkModelFieldDo> fieldList = truModelFieldService.selectByElementIdAll( elementInfo.getId() );
            result.put( "fieldList", fieldList );
            result.setSingleOk( elementInfo, "获取点字段成功" );
        }
        catch ( Exception e ) {
            logger.error( "获取点字段失败", e );
            result.setErr( "获取点字段失败" ).getResultMap();
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "模型对象详情", notes = "模型对象详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "模型对象id", dataType = "String", required = false, paramType = "query")
    })
    @RequestMapping(value = "/modelobjectinfo", method = {RequestMethod.GET})
    @ResponseBody
    public Object modelObjectInfo( String id, String objId, String modelId, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser();
//            TOlkModelObjectDo modObj = truModelObjectService.findById( id );
//            if( modObj == null){
//                return resMap.setErr( "模型对象不存在" ).getResultMap();
//            }

            if ( StringUtils.isNotBlank( objId ) ) {

            }

            DigitalAssetVo modelVo = new DigitalAssetVo();
            modelVo.setId( objId );
            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            modelVo.setOwnerId( user.getUserId() );
            modelVo.setScatalog( "db" );
            modelVo.setDataType( "nodeview" );

            HashMap<String, Object> paraMap = new HashMap<>();
            MyBeanUtils.copyBeanNotNull2Map( modelVo, paraMap );
            logger.debug( "{}", paraMap );

            ListResp<DigitalAssetVo> daVal = apiOlkDbService.digitalAssetOlkSearchList( paraMap, user.getTokenId() );
            if ( !daVal.isSuccess() ) {
                return resMap.setErr( "获取失败,"+daVal.getMsg() ).getResultMap();
            }
            if ( daVal.getData().size() == 0 ) {
                return resMap.setErr( "获取失败,不存在或以无权限" ).getResultMap();
            }
            DigitalAssetVo digitalAssetVo = daVal.getData().get( 0 );

            ObjectResp<OlkObjectWithFieldsVo> retVal = apiOlkDbService.olkTableWithSubInfo( objId, user.getTokenId() );

            if ( retVal.isSuccess() ) {
                //BydbObjectFieldsVo data = retVal.getData();
                apiOlkDbService.digitalAssetOlkTabFieldList( id, user.getTokenId() );
                ListResp<HashMap> fieldRet = apiOlkDbService.digitalAssetOlkTabFieldList( objId, user.getTokenId() );
                //List<TBydbFieldDo> fieldList = data.getFieldList().stream().filter( x->x.getEnable()!=null && x.getEnable()==1 ).collect( Collectors.toList());
                //List<NodePartyView> nodePartyViews = apiOlkModelService.nodePartys(data.getNodePartyId(), user.getTokenId() );
                //TBydbObjectDo objectDo = new TBydbObjectDo();
                //MyBeanUtils.copyBeanNotNull2Bean( data,objectDo );
                resMap.put( "tabledataset", digitalAssetVo );
//                for ( NodePartyView nodePartyView : nodePartyViews ) {
//                    if(data.getNodePartyId().equals( nodePartyView.getId() )){
//                        resMap.put( "node",nodePartyView );
//                    }
//                }
                //ListResp<BydbTabDatasetUseProjVo> upRet = apiOlkModelService.tabDataSetUseProj( data.getId(), user.getTokenId() );
                //resMap.put( "useProjList", upRet.getData() );
                resMap.setOk( fieldRet.getData().size(), fieldRet.getData(), "" );
            }
            //resMap.setOk(findCnt, list, "获取联邦成员列表成功");
        }
        catch ( Exception ex ) {
            resMap.setErr( "模型对象详情失败" );
            logger.error( "模型对象详情失败:", ex );
        }
        return resMap.getResultMap();
    }

}
