package cn.bywin.business.controller.bydb;


import cn.bywin.business.bean.bydb.TBydbDatasetDo;
import cn.bywin.business.bean.bydb.TBydbObjectDo;
import cn.bywin.business.bean.bydb.TTruFavouriteObjectDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.service.bydb.BydbDatasetService;
import cn.bywin.business.service.bydb.BydbObjectService;
import cn.bywin.business.service.bydb.TruFavouriteObjectService;
import cn.bywin.business.service.federal.NodePartyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
@Api(tags = "可信建模-收藏管理-trufavoriteobject")
@RequestMapping("/trufavoriteobject")
public class TruFavoriteObjectController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Autowired
    private BydbObjectService objectService;

    @Autowired
    private BydbDatasetService datasetService;

    @Autowired
    private TruFavouriteObjectService favouriteObjectService;

    @Autowired
    private NodePartyService nodePartyService;


    @ApiOperation(value = "新增收藏资源", notes = "新增收藏资源")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "info", value = "收藏资源", dataType = "TBydbFieldDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public Object add( @RequestBody TTruFavouriteObjectDo bean, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
            if ( bean == null || StringUtils.isBlank( bean.getRelId() ) ) {
                return resMap.setErr( "内容不能为空" ).getResultMap();
            }

            Date now = new Date();

            //List<String> objIdList = new ArrayList<>();
            //List<String> dsIdList = new ArrayList<>();

            TTruFavouriteObjectDo foDo = new TTruFavouriteObjectDo();
            foDo.setUserId( ud.getUserId() );
            foDo.setUserAccount(ud.getUserName());
            foDo.setUserName( ud.getChnName() );
            foDo.setRelId( bean.getRelId() );
            foDo.setCollectTime( now );
            foDo.setId( ComUtil.genId() );
            foDo.setSynFlag( 0 );
            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            foDo.setNodePartyId( nodePartyDo.getId() );

            LoginUtil.setBeanInsertUserInfo( foDo, ud );

            String relId = foDo.getRelId();
            if ( StringUtils.isBlank( relId ) ) {
                return resMap.setErr( "收藏资源id不能为空" ).getResultMap();
            }

            TTruFavouriteObjectDo tmp = new TTruFavouriteObjectDo();
            tmp.setRelId( foDo.getRelId() );
            tmp.setUserId( foDo.getUserId() );

            List<TTruFavouriteObjectDo> tmpList = favouriteObjectService.find( tmp );
            if ( tmpList.size() > 0 ) {
                return resMap.setErr( "资源已经收藏" ).getResultMap();
            }

            foDo.setObjectId( relId );
            TBydbObjectDo objectDo = objectService.findById( foDo.getObjectId() );
            if ( objectDo != null ) {
                foDo.setStype( "db" );
                foDo.setDbId( objectDo.getDbId() );
                foDo.setSchemaId( objectDo.getSchemaId() );
                foDo.setObjName( objectDo.getObjectName() );
                foDo.setObjFullName( objectDo.getObjFullName() );
                foDo.setObjChnName( ComUtil.trsEmpty( objectDo.getObjChnName(), objectDo.getObjectName() ) );
            }
            else {
                return resMap.setErr( "收藏资源不存在" ).getResultMap();
            }
            tmp = favouriteObjectService.findById( foDo.getId() );
            if ( tmp == null ) {
                favouriteObjectService.insertBean( foDo );
            }
            else {
                favouriteObjectService.updateBean( foDo );
            }
            resMap.setOk( "保存成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "批量新增收藏资源", notes = "批量新增收藏资源")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "info", value = "收藏资源", dataType = "TBydbFieldDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/listadd", method = {RequestMethod.POST})
    @ResponseBody
    public Object listAdd( @RequestBody List<TTruFavouriteObjectDo> beanList, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
            if ( beanList == null || beanList.size() == 0 ) {
                return resMap.setErr( "内容不能为空" ).getResultMap();
            }

            Date now = new Date();

            List<TTruFavouriteObjectDo> foList = new ArrayList<>();
            List<String> objIdList = new ArrayList<>();
            List<String> dsIdList = new ArrayList<>();

            for ( TTruFavouriteObjectDo tmp : beanList ) {
                TTruFavouriteObjectDo foDo = new TTruFavouriteObjectDo();
                foDo.setUserId( ud.getUserId() );
                foDo.setRelId( tmp.getRelId() );
                foDo.setCollectTime( now );
                foDo.setId( ComUtil.genId() );
                LoginUtil.setBeanInsertUserInfo( foDo, ud );

                String relId = foDo.getRelId();
                if ( StringUtils.isBlank( relId ) ) {
                    return resMap.setErr( "收藏资源id不能为空" ).getResultMap();
                }
                else if ( relId.startsWith( "db" ) ) {
                    foDo.setObjectId( relId.substring( 2 ) );
                    foDo.setStype( "db" );
                    if ( objIdList.contains( foDo.getObjectId() ) ) {
                        return resMap.setErr( "收藏资源id不能重复" ).getResultMap();
                    }
                    objIdList.add( foDo.getObjectId() );
                    TBydbObjectDo objectDo = objectService.findById( foDo.getObjectId() );
                    if ( objectDo == null ) {
                        return resMap.setErr( "收藏资源表不存在" ).getResultMap();
                    }
                    //foDo.setDcId( objectDo.getDcId() );
                    foDo.setDbId( objectDo.getDbId() );
                    foDo.setSchemaId( objectDo.getSchemaId() );
                    foDo.setObjName( objectDo.getObjectName() );
                    foDo.setObjFullName( objectDo.getObjFullName() );
                    foDo.setObjChnName( objectDo.getObjChnName() );

                }
                else if ( relId.startsWith( "ds" ) ) {
                    foDo.setDatasetId( relId.substring( 2 ) );
                    foDo.setStype( "ds" );
                    if ( dsIdList.contains( foDo.getDatasetId() ) ) {
                        return resMap.setErr( "收藏资源id不能重复" ).getResultMap();
                    }
                    dsIdList.add( foDo.getDatasetId() );
                    TBydbDatasetDo datasetDo = datasetService.findById( foDo.getDatasetId() );
                    if ( datasetDo == null ) {
                        return resMap.setErr( "收藏数据集不存在" ).getResultMap();
                    }
                    //foDo.setDcId( datasetDo.getDcId() );
                    foDo.setObjName( datasetDo.getSetCode() );
                    foDo.setObjFullName( datasetDo.getViewName() );
                    foDo.setObjChnName( datasetDo.getSetChnName() );
                }
                foList.add( foDo );
            }
//            Example exp = new Example(TBydbObjectDo.class);
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andIn("id", objIdList);
//            List<TBydbObjectDo> list = objectService.findByExample(exp);
//
//            if ( list.size() != objIdList.size() ) {
//                return resMap.setErr("没有数据可删除").getResultMap();
//            }
//            if (list.size() != ids.size()) {
//                return resMap.setErr("有数据不存在").getResultMap();
//            }
            favouriteObjectService.batchAdd( foList );

//            String times =""+System.currentTimeMillis();
//            for (TBydbFavouriteObjectDo info : foList) {
//                try {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).addLog(ud, info, "新增-收藏资源" + times);
//                } catch (Exception e1) {
//                    resMap.setErr("新增收藏资源失败");
//                    logger.error("新增收藏资源异常:", e1);
//                }
//            }

            resMap.setOk( "保存成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除收藏资源", notes = "删除收藏资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "收藏资源id", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "relId", value = "收藏资源关联id", dataType = "String", required = false, paramType = "query")
    })
    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public Object delete( String id, String relId, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( StringUtils.isBlank( id ) && StringUtils.isBlank( relId ) ) {
                return resMap.setErr( "id和关联id不能同时为空" ).getResultMap();
            }
            UserDo user = LoginUtil.getUser( request );
//            if( !id.matches("^[a-zA-Z0-9\\-_,]*$") ){
//                return resMap.setErr("id有非法字符").getResultMap();
//            }
            List<String> ids = null;
            List<TTruFavouriteObjectDo> list = new ArrayList<>();
            if ( StringUtils.isNotBlank( id ) ) {
                ids = Arrays.asList( id.split( ",|\\s+" ) );
                for ( String s : ids ) {
                    TTruFavouriteObjectDo fav = new TTruFavouriteObjectDo();
                    fav.setId( s );
                    list.add( fav );
                }
            }
            List<String> rels = null;
            if ( StringUtils.isNotBlank( relId ) ) {
                rels = Arrays.asList( relId.split( ",|\\s+" ) );
                for ( String s : rels ) {
                    TTruFavouriteObjectDo fav = new TTruFavouriteObjectDo();
                    fav.setRelId( s );
                    fav.setUserId( user.getUserId() );
                    list.add( fav );
                }
            }
            Example exp = new Example( TTruFavouriteObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            if ( ids != null ) {
                criteria.andIn( "id", ids );
            }
            if ( rels != null ) {
                criteria.andIn( "relId", rels );
                criteria.andEqualTo( "userId", user.getUserName() );
            }
            List<TTruFavouriteObjectDo> favouriteObjects = favouriteObjectService.findByExample( exp );
            int cnt = favouriteObjects.size();
            if ( cnt == 0 ) {
                return resMap.setErr( "没有数据可删除" ).getResultMap();
            }
            if ( ids != null && favouriteObjects.size() != ids.size() ) {
                return resMap.setErr( "有数据不存在" ).getResultMap();
            }
            if (rels!= null && favouriteObjects.size() != rels.size()) {
                return resMap.setErr("有数据不存在").getResultMap();
            }
            ids = favouriteObjects.stream().map( x -> x.getId() ).collect( Collectors.toList() );
            favouriteObjectService.deleteByIds(ids);
            resMap.setOk( "删除成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "删除失败" );
            logger.error( "删除异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取收藏资源列表", notes = "获取收藏资源列表")
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
            TTruFavouriteObjectDo modelVo = new TTruFavouriteObjectDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo, hru );
            modelVo.setQryCond( ComUtil.chgLikeStr( modelVo.getQryCond() ) );

            long findCnt = favouriteObjectService.findBeanCnt( modelVo );
            modelVo.genPage( findCnt );

            List<TTruFavouriteObjectDo> list = favouriteObjectService.findBeanList( modelVo );

            resMap.setPageInfo( modelVo.getPageSize(), modelVo.getCurrentPage() );
            resMap.setOk( findCnt, list, "获取收藏资源列表成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取收藏资源列表失败" );
            logger.error( "获取收藏资源列表失败:", ex );
        }
        return resMap.getResultMap();
    }

    /*@ApiOperation(value = "获取表字段", notes = "获取表字段")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "objectId", value = "表id", dataType = "String", required = true, paramType = "query"),
    })
    @RequestMapping(value = "/option", method = {RequestMethod.GET})
    @ResponseBody
    public Object option(String objectId,HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            TBydbFieldDo modelVo = new TBydbFieldDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo,hru);
            modelVo.setObjectId( objectId );

            List<TBydbFieldDo> list = fieldService.findBeanList(modelVo);
            resMap.setPageInfo(modelVo.getPageSize(), modelVo.getCurrentPage());
            resMap.setSingleOk(list, "获取表字段");
        } catch (Exception ex) {
            resMap.setErr("获取表字段失败");
            logger.error("获取表字段失败:", ex);
        }
        return resMap.getResultMap();
    }*/

}
