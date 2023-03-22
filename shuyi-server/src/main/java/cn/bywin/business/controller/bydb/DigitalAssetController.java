package cn.bywin.business.controller.bydb;


import cn.bywin.business.bean.bydb.TBydbDatabaseDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.view.bydb.DigitalAssetVo;
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
import cn.bywin.business.service.bydb.DigitalAssetService;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.trumodel.ApiTruModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Calendar;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        maxAge = 3600)
@RestController
@Api(tags = "digit-数字地图-digitalasset")
@RequestMapping("/digitalasset")
public class DigitalAssetController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BydbDatabaseService databaseService;

    @Autowired
    private BydbSchemaService schemaService;

    @Autowired
    private BydbObjectService objectService;

    @Autowired
    private BydbFieldService fieldService;

    @Autowired
    private DigitalAssetService digitalAssetService;

    @Autowired
    private BydbDatasetService datasetService;

    @Autowired
    private ApiTruModelService apiTruModelService;

    @Autowired
    private NodePartyService nodePartyService;

//    @Autowired
//    private BydbDsColumnService dsColumnService;

//    @Autowired
//    private BydbDcServerService dcserverService;

//    @Autowired
//    private BydbFavouriteObjectService favouriteObjectService;

//    @Autowired
//    private BydbApplyObjectService applyObjectService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @ApiOperation(value = "bydb数字地图内容", notes = "bydb数字地图内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    @ResponseBody
    public Object info(String id) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if (StringUtils.isBlank(id)) {
                return resMap.setErr("id不能为空").getResultMap();
            }
            TBydbDatabaseDo modelVo = databaseService.findById(id);
            resMap.setSingleOk(modelVo, "成功");

        } catch (Exception ex) {
            resMap.setErr("查询失败");
            logger.error("查询异常:", ex);
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "获取数字地图字段列表", notes = "获取数字地图字段列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/tabfieldlist", method = {RequestMethod.GET})
    @ResponseBody
    public Object tabFieldList(String id) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser();
            if (StringUtils.isBlank(id)) {
                return resMap.setErr("id不能为空").getResultMap();
            }
            //if(id.startsWith("db")){
//                String id1 = id.substring(2);
//                TBydbObjectDo objectDo = objectService.findById(id1);
//                TBydbFieldDo tmp = new TBydbFieldDo();
//                tmp.setObjectId( objectDo.getId() );
//                tmp.setEnable( 1 );
//                List<TBydbFieldDo> beanList = fieldService.findBeanList(tmp);
//                List<Object> list = beanList.stream().map(x -> {
//                    HashMap<String, Object> map = new HashMap<>();
//                    map.put("id", x.getId());
//                    map.put("columnName", x.getFieldName());
//                    //map.put("chgStatement", x.getChgStatement());
//                    map.put("chgStatement", "");
//                    map.put("orgType", x.getFieldType());
//                    //map.put("columnType", JdbcTypeToJavaTypeUtil.chgType( x.getFieldType() ) );
//                    map.put("columnType",  x.getFieldType() );
//                    map.put("chnName", x.getChnName());
//                    map.put("tips", StringUtils.isBlank( x.getChnName())? x.getChnName():x.getFieldName());
//                    return map;
//                }).collect(Collectors.toList());
//                resMap.setSingleOk(list, "获取数字地图字段列表成功");
            //}
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
                resMap.setSingleOk(list, "获取数字地图字段列表成功");
            }*/
//            else{
//                resMap.setErr("获取数字地图字段列表失败,无效id");
//            }

            return apiTruModelService. digitalAssetTabFieldList( id, user.getTokenId() );

        } catch (Exception ex) {
            resMap.setErr("获取数字地图字段列表失败");
            logger.error("获取数字地图字段列表异常:", ex);
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "可信任务数据地图列表", notes = "可信任务数据地图列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qryCond", value = "模糊条件", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dataType", value = "数据类型", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "deptNo", value = "部门编号", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "ssj", value = "时间条件 day week month year", dataType = "String", required = false, paramType = "query"),
            //@ApiImplicitParam(name = "sdt1", value = "开始时间", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "页数", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", required = false, paramType = "query")
    })
    @RequestMapping(value = "/searchlist", method = {RequestMethod.GET})
    @ResponseBody
    public Object searchList(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser(request);
            DigitalAssetVo modelVo = new DigitalAssetVo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            logger.debug("{}",hru.getAllParaData());
            new PageBeanWrapper( modelVo, hru);
            modelVo.setDatasourceId( "notdc" );

            //modelVo.setQryCond( ComUtil.chgLikeStr(modelVo.getQryCond()));
            //modelVo.setDbName(ComUtil.chgLikeStr(modelVo.getDbName()));

            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            modelVo.setOwnerId( user.getUserId() );
            modelVo.setScatalog( "db" );

            if( "node".equalsIgnoreCase(modelVo.getDataType())){

            }
//            else if( "priv".equalsIgnoreCase(modelVo.getDataType())){
//                modelVo.setPrivFlag( 1 );
//            }
            else if( "favorite".equalsIgnoreCase(modelVo.getDataType())){

            }
            else if( "grant".equalsIgnoreCase(modelVo.getDataType())){

            }
            {
                //modelVo.setDataType( "priv" );
                //modelVo.setPrivFlag( 1 );

                // ( "newly".equalsIgnoreCase( modelVo.getDataType()))
                String ssj = hru.getNvlPara("ssj");
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
//                else if( "newly".equalsIgnoreCase(modelVo.getDataType())){
//                    cal = Calendar.getInstance();
//                    cal.add( Calendar.DAY_OF_WEEK,-7);
//                }
                if( cal != null){
                    modelVo.setSdt1( ComUtil.strToDate( ComUtil.dateToStr( cal.getTime(),ComUtil.shortDtFormat),ComUtil.shortDtFormat));
                }
            }
            HashMap<String,Object> paraMap = new HashMap<>();
            MyBeanUtils.copyBeanNotNull2Map( modelVo,paraMap );
            logger.debug( "{}",paraMap );

            return apiTruModelService. digitalAssetSearchList( paraMap, user.getTokenId() );

//            long findCnt = digitalAssetService.findBeanCnt(modelVo);
//            modelVo.genPage(findCnt);
//
//            List<DigitalAssetVo> list = digitalAssetService.findBeanList(modelVo);
//            for (DigitalAssetVo digitalAssetVo : list) {
//                if( StringUtils.isBlank( digitalAssetVo.getObjChnName() )){
//                    digitalAssetVo.setObjChnName( digitalAssetVo.getObjectName());
//                }
//            }
//
//            resMap.setPageInfo(modelVo.getPageSize(), modelVo.getCurrentPage());
//            resMap.setOk(findCnt, list, "获取数字地图列表成功");
        } catch (Exception ex) {
            resMap.setErr("获取数字地图列表失败");
            logger.error("获取数字地图列表失败:", ex);
        }
        return resMap.getResultMap();
    }

    /*@ApiOperation(value = "olk数据地图列表", notes = "olk数据地图列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qryCond", value = "模糊条件", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dataType", value = "数据类型", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dcId", value = "节点id", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "deptNo", value = "部门编号", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "ssj", value = "时间条件 day week month year", dataType = "String", required = false, paramType = "query"),
            //@ApiImplicitParam(name = "sdt1", value = "开始时间", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "页数", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", required = false, paramType = "query")
    })
    @RequestMapping(value = "/olksearchlist", method = {RequestMethod.GET})
    @ResponseBody
    public Object olkSearchList(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser(request);
            DigitalAssetVo modelVo = new DigitalAssetVo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            logger.debug("{}",hru.getAllParaData());
            new PageBeanWrapper( modelVo, hru);
            modelVo.setDatasourceId( "dchetu" );
            //modelVo.setQryCond( ComUtil.chgLikeStr(modelVo.getQryCond()));
            //modelVo.setDbName(ComUtil.chgLikeStr(modelVo.getDbName()));

            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            modelVo.setOwnerId( user.getUserId() );
            //modelVo.setUserName( user.getUserName() );
            modelVo.setUserNodePartyId( nodePartyDo.getId() );
            modelVo.setScatalog( "db" );
            if( "priv".equalsIgnoreCase(modelVo.getDataType())){
                modelVo.setNodePartyId( nodePartyDo.getId() );
                modelVo.setUserId( user.getUserId() );
            }
            else if( "node".equalsIgnoreCase(modelVo.getDataType())){

            }
//            else if( "priv".equalsIgnoreCase(modelVo.getDataType())){
//                modelVo.setPrivFlag( 1 );
//            }
            else if( "favorite".equalsIgnoreCase(modelVo.getDataType())){

            }
            else if( "grant".equalsIgnoreCase(modelVo.getDataType())){

            }
            {
                //modelVo.setDataType( "priv" );
                //modelVo.setPrivFlag( 1 );

                // ( "newly".equalsIgnoreCase( modelVo.getDataType()))
                String ssj = hru.getNvlPara("ssj");
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
//                else if( "newly".equalsIgnoreCase(modelVo.getDataType())){
//                    cal = Calendar.getInstance();
//                    cal.add( Calendar.DAY_OF_WEEK,-7);
//                }
                if( cal != null){
                    modelVo.setSdt1( ComUtil.strToDate( ComUtil.dateToStr( cal.getTime(),ComUtil.shortDtFormat),ComUtil.shortDtFormat));
                }
            }
            HashMap<String,Object> paraMap = new HashMap<>();
            MyBeanUtils.copyBeanNotNull2Map( modelVo,paraMap );
            logger.debug( "{}",paraMap );

            return apiTruModelService. digitalAssetSearchList( paraMap, user.getTokenId() );

//            long findCnt = digitalAssetService.findBeanCnt(modelVo);
//            modelVo.genPage(findCnt);
//
//            List<DigitalAssetVo> list = digitalAssetService.findBeanList(modelVo);
//            for (DigitalAssetVo digitalAssetVo : list) {
//                if( StringUtils.isBlank( digitalAssetVo.getObjChnName() )){
//                    digitalAssetVo.setObjChnName( digitalAssetVo.getObjectName());
//                }
//            }
//
//            resMap.setPageInfo(modelVo.getPageSize(), modelVo.getCurrentPage());
//            resMap.setOk(findCnt, list, "获取数字地图列表成功");
        } catch (Exception ex) {
            resMap.setErr("获取数字地图列表失败");
            logger.error("获取数字地图列表失败:", ex);
        }
        return resMap.getResultMap();
    }*/


    /*@ApiOperation(value = "数字地图检索", notes = "数字地图检索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qryCond", value = "模糊条件", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dataType", value = "数据类型", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "deptNo", value = "部门编号", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "ssj", value = "时间条件 day week month year", dataType = "String", required = false, paramType = "query"),
            //@ApiImplicitParam(name = "sdt1", value = "开始时间", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "findType", value = "查找类型 db 表 ds数据集 ", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "页数", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", required = false, paramType = "query")
    })
    @RequestMapping(value = "/assetmapquery", method = {RequestMethod.GET})
    @ResponseBody
    public Object assetMapQuery(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser(request);
            DigitalAssetVo modelVo = new DigitalAssetVo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            logger.debug("{}",hru.getAllParaData());
            new PageBeanWrapper( modelVo, hru);

            TBydbDcServerDo dcTmp = new TBydbDcServerDo();
            dcTmp.setManageAccount(user.getUserName());
            List<TBydbDcServerDo> dcList = dcserverService.find(dcTmp);
            if (dcList.size() ==1 ) {
                resMap.put("userDcId",dcList.get(0).getId());
            }
            else{
                resMap.put("userDcId","");
            }

            modelVo.setQryCond(ComUtil.chgLikeStr(modelVo.getQryCond()));
            //modelVo.setDbName(ComUtil.chgLikeStr(modelVo.getDbName()));
            modelVo.setOtherId( user.getUserName() );
            if( "pub".equalsIgnoreCase(modelVo.getDataType())){
                modelVo.setPrivFlag( 0 );
            }
            else if( "priv".equalsIgnoreCase(modelVo.getDataType())){
                modelVo.setPrivFlag( 1 );
            }
            else if( "priv".equalsIgnoreCase(modelVo.getDataType())){
                modelVo.setPrivFlag( 1 );
            }
            else if( "favorite".equalsIgnoreCase(modelVo.getDataType())){

            }
            else if( "grant".equalsIgnoreCase(modelVo.getDataType())){

            }
            else {// ( "newly".equalsIgnoreCase( modelVo.getDataType()))
                String ssj = hru.getNvlPara("ssj");
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
                if( cal != null){
                    modelVo.setSdt1( ComUtil.strToDate( ComUtil.dateToStr( cal.getTime(),ComUtil.shortDtFormat),ComUtil.shortDtFormat));
                }
            }

            String findType = hru.getNvlPara("findType");
            if( "db".equals( findType ) ){
                modelVo.setScatalog( "db" );
            }
            else if( "ds".equals( findType ) ){
                modelVo.setScatalog( "ds" );
            }

            long findCnt = digitalAssetService.findBeanCnt(modelVo);
            modelVo.genPage(findCnt);

            List<DigitalAssetVo> list = digitalAssetService.findBeanList(modelVo);
            for (DigitalAssetVo digitalAssetVo : list) {
                if( StringUtils.isBlank( digitalAssetVo.getObjChnName() )){
                    digitalAssetVo.setObjChnName( digitalAssetVo.getObjectName());
                }
            }

            if( list.size()>0) {
                for (DigitalAssetVo daVo : list) {
                    if( daVo.getFavouriteFlag() == null ) {
                        daVo.setFavouriteFlag(0);
                    }
                    if( daVo.getApplyFlag() == null ) {
                        daVo.setApplyFlag( 0 );
                    }
                }
//                List<String> idsList = list.stream().map(x -> x.getId()).collect(Collectors.toList());
//
//                Example exp = new Example(TBydbFavouriteObjectDo.class);
//                Example.Criteria criteria = exp.createCriteria();
//                criteria.andIn("relId", idsList).andEqualTo("ownerAccount", user.getUserName());
//                List<TBydbFavouriteObjectDo> favList = favouriteObjectService.findByExample(exp);
//                for (DigitalAssetVo daVo : list) {
//                    daVo.setFavouriteFlag(0);
//                    for (TBydbFavouriteObjectDo favDo : favList) {
//                        if (daVo.getId().equals(favDo.getRelId())) {
//                            daVo.setFavouriteFlag(1);
//                            break;
//                        }
//                    }
//                }
//
//                exp = new Example(TBydbApplyObjectDo.class);
//                criteria = exp.createCriteria();
//                criteria.andIn("relId", idsList).andEqualTo("applyAccount", user.getUserName())
//                        .andCondition(" status in(1,2) ");
//                List<TBydbApplyObjectDo> applyList = applyObjectService.findByExample(exp);
//                for (DigitalAssetVo daVo : list) {
//                    daVo.setApplyFlag(0);
//                    for (TBydbApplyObjectDo applyDo : applyList) {
//                        if (daVo.getId().equals(applyDo.getRelId())) {
//                            daVo.setApplyFlag(applyDo.getStatus());
//                            break;
//                        }
//                    }
//                }
            }

            resMap.setPageInfo(modelVo.getPageSize(), modelVo.getCurrentPage());
            resMap.setOk(findCnt, list, "获取数字地图列表成功");
        } catch (Exception ex) {
            resMap.setErr("获取数字地图列表失败");
            logger.error("获取数字地图列表失败:", ex);
        }
        return resMap.getResultMap();
    }*/

}

