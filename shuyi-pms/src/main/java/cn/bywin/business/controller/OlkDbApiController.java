package cn.bywin.business.controller;

import cn.bywin.business.bean.bydb.TTruFavouriteObjectDo;
import cn.bywin.business.bean.bydb.TTruModelElementDo;
import cn.bywin.business.bean.bydb.TTruModelObjectDo;
import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkDcServerDo;
import cn.bywin.business.bean.olk.TOlkFieldDo;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.olk.TOlkSchemaDo;
import cn.bywin.business.bean.view.bydb.DigitalAssetVo;
import cn.bywin.business.bean.view.federal.FDataApproveVo;
import cn.bywin.business.bean.view.olk.OlkObjectWithFieldsVo;
import cn.bywin.business.bean.view.olk.VOlkObjectVo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.service.bydb.TruFavouriteObjectService;
import cn.bywin.business.service.federal.DataApproveService;
import cn.bywin.business.service.federal.DataPartyService;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.service.olk.OlkDatabaseService;
import cn.bywin.business.service.olk.OlkDcServerService;
import cn.bywin.business.service.olk.OlkDigitalAssetService;
import cn.bywin.business.service.olk.OlkFieldService;
import cn.bywin.business.service.olk.OlkModelElementService;
import cn.bywin.business.service.olk.OlkModelObjectService;
import cn.bywin.business.service.olk.OlkModelService;
import cn.bywin.business.service.olk.OlkObjectService;
import cn.bywin.business.service.olk.OlkSchemaService;
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
@Api(tags = "olk????????????")
@RequestMapping("/olkdbapi")
public class OlkDbApiController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OlkModelService olkModelService;

    @Autowired
    private OlkModelObjectService olkModelObjectService;

    @Autowired
    private NodePartyService nodePartyService;

    @Autowired
    private OlkDcServerService dcService;

    @Autowired
    private OlkDatabaseService databaseService;

    @Autowired
    private OlkSchemaService schemaService;

    @Autowired
    private OlkObjectService tableService;

    @Autowired
    private OlkDigitalAssetService olkDigitalAssetService;

    @Autowired
    private OlkObjectService bydbObjectService;

    @Autowired
    private OlkFieldService fieldService;

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
    @RequestMapping(value = "/synolkdcserver", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object synOlkDcServer(@RequestBody TOlkDcServerDo info) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            TOlkDcServerDo dcServerDo = dcService.findById( info.getId() );
            if(dcServerDo != null){
                dcService.updateBean(  info );
            }
            else{
                dcService.insertBean( info );
            }
            result.setOk("??????????????????");
        } catch (Exception e) {
            logger.error("??????????????????", e);
            result.setErr("??????????????????");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/synolkdatabase", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object synOlkDatabase(@RequestBody TOlkDatabaseDo info) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            TOlkDatabaseDo database = databaseService.findById( info.getId() );
            if(database != null){
                databaseService.updateBean(  info );
            }
            else{
                databaseService.insertBean( info );
            }
            result.setOk("??????????????????");
        } catch (Exception e) {
            logger.error("??????????????????", e);
            result.setErr("??????????????????");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/delolkdatabase", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String,Object> delOlkDatabase(String id) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            if( StringUtils.isBlank( id )){
                return result.setErr( "id????????????" ).getResultMap();
            }
            TOlkDatabaseDo database = databaseService.findById( id );
            if( database == null ){
                return result.setOk( "???????????????" ).getResultMap();
            }
//            Example exp = new Example( TOlkDatabaseDo.class );
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andEqualTo( "id", id );
//            List<TOlkDatabaseDo> list = databaseService.findByExample( exp );

//            cnt = list.size();
//            if ( cnt == 0 ) {
//                return resMap.setErr( "?????????????????????" ).getResultMap();
//            }
//            if ( cnt != split.size() ) {
//                return resMap.setErr( "??????????????????????????????" ).getResultMap();
//            }

            String ids = "'"+ id+"'";
            Example exp = new Example( TTruModelObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andCondition( " real_obj_id in (select a.id from t_bydb_object a, t_bydb_database b where a.db_id =b.id  and b.id in( "+ids+") )" );
            int cnt = olkModelObjectService.findCountByExample( exp );
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
    @RequestMapping(value = "/olkdatabaseinfo", method = {RequestMethod.POST})
    public TOlkDatabaseDo olkDatabaseInfo(String id) {
        try {
            //resMap.initSingleObject();
            if( StringUtils.isBlank( id ) ){
                return null;
            }
            TOlkDatabaseDo database = databaseService.findById( id );
            return  database;
        } catch (Exception e) {
            logger.error("????????????", e);
            return null;
        }
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/olkdatabaseList", method = {RequestMethod.POST})
    public List<TOlkDatabaseDo> olkDatabaseList(@RequestBody TOlkDatabaseDo info) {
        try {
            List<TOlkDatabaseDo> data = databaseService.findBeanList( info );
            return data;
        } catch (Exception e) {
            logger.error("????????????", e);
            return null;
        }
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/synolkschema", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object synOlkSchema(@RequestBody TOlkSchemaDo info) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            TOlkSchemaDo schema = schemaService.findById( info.getId() );
            if(schema != null){
                schemaService.updateBean(  info );
            }
            else{
                schemaService.insertBean( info );
            }
            result.setOk("???????????????");
        } catch (Exception e) {
            logger.error("???????????????", e);
            result.setErr("???????????????");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/delolkschema", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String,Object> delOlkSchema(@RequestBody List<String> idList,String notCheck) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            if( idList == null || idList.size()==0 ){
                return result.setErr( "id??????" ).getResultMap();
            }

//            Example exp = new Example( TOlkObjectDo.class );
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andIn( "schemaId", idList );
//            //criteria.andCondition( " id in(select table_id from t_bydb_ds_entity where table_id is not null ) ");
//            int cnt = tableService.findCountByExample( exp );
//            if ( cnt > 0 ) {
//                return resMap.setErr( "??????????????????????????????????????????" ).getResultMap();
//            }

            Example exp = new Example( TOlkSchemaDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", idList );
            //criteria.andEqualTo( "dcId", user.getUserName() );
            List<TOlkSchemaDo> list = schemaService.findByExample( exp );

//            if( !"1".equals( notCheck )) {
//                String ids = "'" + StringUtils.join( idList, "','" ) + "'";
//                exp = new Example( TTruModelObjectDo.class );
//                criteria = exp.createCriteria();
//                criteria.andCondition( " real_obj_id in (select a.id from t_bydb_object a, t_bydb_schema b where a.schema_id =b.id  and b.id in( " + ids + ") )" );
//                int cnt = olkModelObjectService.findCountByExample( exp );
//                if ( cnt > 0 ) {
//                    return result.setErr( "??????????????????????????????" ).getResultMap();
//                }
//            }
            schemaService.deleteWhithRel( list );

            result.setOk("???????????????");
        } catch (Exception e) {
            logger.error("???????????????", e);
            result.setErr("???????????????");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/olkschemainfo", method = {RequestMethod.POST})
    public TOlkSchemaDo olkSchemaInfo(String id) {
        try {
            if( StringUtils.isBlank( id )){
                return null;
            }
            TOlkSchemaDo info = schemaService.findById( id );
            return  info;
        } catch (Exception e) {
            logger.error("????????????", e);
            return null;
        }
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/olkschemaList", method = {RequestMethod.POST})
    public List<TOlkSchemaDo> olkSchemaList(@RequestBody TOlkSchemaDo info) {
        try {
            List<TOlkSchemaDo> data = schemaService.findBeanList( info );
            return data;
        } catch (Exception e) {
            logger.error("????????????", e);
            return null;
        }
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/synolktable", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object synOLkTable( @RequestBody List<OlkObjectWithFieldsVo> bofList ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
           tableService.saveWithFields( bofList );
            result.setOk("???????????????");
        } catch (Exception e) {
            logger.error("???????????????", e);
            result.setErr("???????????????");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/delolktable", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String,Object> delOlkTable(@RequestBody List<TOlkObjectDo> list) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            if ( list == null || list.size() == 0 ) {
                return result.setErr( "?????????????????????" ).getResultMap();
            }
//            List<String> userIdList = list.stream().map( x -> x.getUserId() ).distinct().collect( Collectors.toList() );
//            String userId = "";
//            if( userIdList.size()>1){
//                return result.setErr( "????????????????????????????????????" ).getResultMap();
//            }
//            else if(userIdList.size()==1){
//                userId = userIdList.get( 0 );
//            }
            List<String> idList = list.stream().map( x -> x.getId() ).distinct().collect( Collectors.toList() );
            Example exp = new Example( TOlkObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", idList );

            List<TOlkObjectDo> objList = bydbObjectService.findByExample( exp );
            int cnt = objList.size();
            if ( cnt == 0 ) {
                return result.setOk( "?????????????????????" ).getResultMap();
            }

//            exp = new Example( TOlkModelObjectDo.class );
//            criteria = exp.createCriteria();
//            criteria.andIn( "realObjId", idList );
//            if( StringUtils.isNotBlank(  userId )){
//                criteria.andEqualTo( "userId", userId );
//            }
//            cnt = olkModelObjectService.findCountByExample( exp );
//            if ( cnt > 0 ) {
//                return result.setErr( "??????????????????????????????" ).getResultMap();
//            }
            //dbsource.setEnable(  0 );
            tableService.deleteWhithOthers( list );

            result.setOk("???????????????");
        } catch (Exception e) {
            logger.error("???????????????", e);
            result.setErr("???????????????");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/olktableinfo", method = {RequestMethod.POST})
    public TOlkObjectDo olkTableInfo(String id) {
        try {

            if( StringUtils.isBlank( id )){
                return null;
            }
            TOlkObjectDo table = tableService.findById( id );
            return  table;
        } catch (Exception e) {
            logger.error("????????????", e);
            return null;
        }
    }

    @ApiOperation(value = "?????????", notes = "?????????")
    @RequestMapping(value = "/olktablewithsubinfo", method = {RequestMethod.POST})
    public Object OlkTableWithSubInfo(String id) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            TOlkObjectDo info = tableService.findById( id );
            OlkObjectWithFieldsVo data = new OlkObjectWithFieldsVo();
            MyBeanUtils.copyBeanNotNull2Bean( info, data );
            List<TOlkFieldDo> fieldList = fieldService.selectByObjectId( info.getId() );
            data.setFieldList( fieldList );
//            Example exp = new Example( FDataApproveDo.class );
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andEqualTo( "dataId", id ).andNotEqualTo( "approve", 9 );
            FDataApproveVo bean = new FDataApproveVo();
            bean.setDataId( info.getId() );
            List<FDataApproveVo> dataApproveList = applyObjectService.findOlkBeanList( bean );
            data.setApproveList( dataApproveList );
            result.setSingleOk(data,"????????????");
            return result.getResultMap();
        } catch (Exception e) {
            logger.error("????????????", e);
            result.setErr("????????????");
        }
        return result.getResultMap();
    }



    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    @RequestMapping(value = "/olktablelist", method = {RequestMethod.POST})
    public Object olkTableList( @RequestBody TOlkObjectDo info) {
        ResponeMap result = genResponeMap();
        try {
            MyBeanUtils.chgBeanLikeAndOtherStringEmptyToNull( info,"qryCond","objectName" ,"objChnName");
            long beanCnt = tableService.findBeanCnt( info );
            info.genPage(beanCnt);
            List<TOlkObjectDo> list = tableService.findBeanList( info );
            result.setOk(beanCnt,list,"????????????");
            return result.getResultMap();
        } catch (Exception e) {
            logger.error("????????????", e);
            result.setErr("????????????");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    @RequestMapping(value = "/pmolktablelist", method = {RequestMethod.POST})
    public Object pmOlkTableList( @RequestBody VOlkObjectVo info) {
        ResponeMap result = genResponeMap();
        try {
            MyBeanUtils.chgBeanLikeAndOtherStringEmptyToNull( info,"qryCond","objectName" ,"objChnName");
            if( StringUtils.isNotBlank( info.getId() ) ){
                info.setIdList( Arrays.asList(info.getId().split( "," )) );
                info.setId( null );
            }
            long beanCnt = tableService.findNodeBeanCnt( info );
            if( info.getPageSize() != null ) {
                info.genPage( beanCnt );
            }
            List<VOlkObjectVo> list = tableService.findNodeBeanList( info );
            result.setOk(beanCnt,list,"????????????");
            return result.getResultMap();
        } catch (Exception e) {
            logger.error("????????????", e);
            result.setErr("????????????");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    @RequestMapping(value = "/userolktablewithinfo", method = {RequestMethod.POST})
    public Object userOlkTableWithInfo( @RequestBody  OlkObjectWithFieldsVo bean) {
        ResponeMap result = genResponeMap();
        try {
            List<OlkObjectWithFieldsVo> userTableList = tableService.findUserTable( bean );
            result.setSingleOk(userTableList,"????????????");
            return result.getResultMap();
        } catch (Exception e) {
            logger.error("????????????", e);
            result.setErr("????????????");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/savegrantobject", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object saveGrantObject( @RequestBody List<FDataApproveDo> list ) {
        ResponeMap result = genResponeMap();
        try {
            //result.initSingleObject();
            List<FDataApproveDo> modList = new ArrayList<>();
            List<FDataApproveDo> addList = new ArrayList<>();
            for ( int i = list.size() - 1; i >= 0; i-- ) {
                FDataApproveDo approveDo = list.get( i );
                if( "database".equals( approveDo.getDataCatalog() )) {
                    TOlkObjectDo objTmp = new TOlkObjectDo();
                    objTmp.setDbId( approveDo.getDataId() );
                    objTmp.setEnable( 1 );
                    List<TOlkObjectDo> oblList = bydbObjectService.find( objTmp );
                    for ( TOlkObjectDo tOlkObjectDo : oblList ) {
                        Example exp = new Example( FDataApproveDo.class );
                        Example.Criteria criteria = exp.createCriteria();
                        criteria.andEqualTo( "dataId", tOlkObjectDo.getId() ).andEqualTo( "creatorId", approveDo.getCreatorId() )
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
                                da.setDataCatalog( "olk" );
                                da.setApproval( approveDo.getApproval() );
                                modList.add( da );
                            }
                        }
                        else {
                            FDataApproveDo da = new FDataApproveDo();
                            MyBeanUtils.copyBeanNotNull2Bean( approveDo,da );
                            da.setDataCatalog( "odb" );
                            da.setDataId( tOlkObjectDo.getId() );
                            da.setId( ComUtil.genId() );
                            addList.add( da );
                        }
                    }
                }
                else if("schema".equals( approveDo.getDataCatalog() )){
                    TOlkObjectDo objTmp = new TOlkObjectDo();
                    objTmp.setSchemaId( approveDo.getDataId() );
                    objTmp.setEnable( 1 );
                    List<TOlkObjectDo> oblList = bydbObjectService.find( objTmp );
                    for ( TOlkObjectDo tOlkObjectDo : oblList ) {
                        Example exp = new Example( FDataApproveDo.class );
                        Example.Criteria criteria = exp.createCriteria();
                        criteria.andEqualTo( "dataId", tOlkObjectDo.getId() ).andEqualTo( "creatorId", approveDo.getCreatorId() )
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
                                da.setDataCatalog( "olk" );
                                da.setApproval( approveDo.getApproval() );
                                modList.add( da );
                            }
                        }
                        else{
                            FDataApproveDo da = new FDataApproveDo();
                            MyBeanUtils.copyBeanNotNull2Bean( approveDo,da );
                            da.setDataCatalog( "odb" );
                            da.setDataId( tOlkObjectDo.getId() );
                            da.setId( ComUtil.genId() );
                            addList.add( da );
                        }
                    }
                }
                else if("table".equals( approveDo.getDataCatalog() )){
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
                        //list.remove( i );
                    }
                    else{
                        FDataApproveDo da = new FDataApproveDo();
                        MyBeanUtils.copyBeanNotNull2Bean( approveDo,da );
                        da.setDataCatalog( "odb" );
                        da.setDataId( approveDo.getId() );
                        da.setId( ComUtil.genId() );
                        addList.add( da );
                    }
                }
                else{
                    return result.setErr( "?????????????????????" ).getResultMap();
                }
            }
            applyObjectService.saveAndUpdateBeans( addList, modList );
            //list.addAll( modList );
            result.setOk( list.size(), list, "????????????" );

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
    @RequestMapping(value = "/digitalassetolksearchlist", method = {RequestMethod.POST})
    @ResponseBody
    public Object digitalAssetOlkSearchList(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            //UserDo user = LoginUtil.getUser(request);
            DigitalAssetVo modelVo = new DigitalAssetVo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            logger.debug( JsonUtil.toSimpleJson( hru.getAllParaData()));

            new PageBeanWrapper( modelVo, hru);

            modelVo.setQryCond( ComUtil.chgLikeStr(modelVo.getQryCond()));
            //modelVo.setDbName(ComUtil.chgLikeStr(modelVo.getDbName()));
            //modelVo.setOtherId( user.getUserName() );
            modelVo.setScatalog( "db" );
            if( "pub".equalsIgnoreCase(modelVo.getDataType())){
                //modelVo.setPrivFlag( 0 );
            }
            else if( "priv".equalsIgnoreCase(modelVo.getDataType())){
                //modelVo.setPrivFlag( 1 );
                //modelVo.setDcId( user.getUserName() );
            }
//            else if( "priv".equalsIgnoreCase(modelVo.getDataType())){
//                modelVo.setPrivFlag( 1 );
//            }
            else if( "favorite".equalsIgnoreCase(modelVo.getDataType())){

            }
            else if( "grant".equalsIgnoreCase(modelVo.getDataType())){

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

            long findCnt = olkDigitalAssetService.findBeanCnt(modelVo);
            modelVo.genPage(findCnt);

            List<DigitalAssetVo> list = olkDigitalAssetService.findBeanList(modelVo);
            for (DigitalAssetVo digitalAssetVo : list) {
                if( StringUtils.isBlank( digitalAssetVo.getObjChnName() )){
                    digitalAssetVo.setObjChnName( digitalAssetVo.getObjectName());
                }
            }

            resMap.setPageInfo(modelVo.getPageSize(), modelVo.getCurrentPage());
            resMap.setOk(findCnt, list, "??????????????????????????????");
        } catch (Exception ex) {
            resMap.setErr("??????????????????????????????");
            logger.error("??????????????????????????????:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "??????????????????????????????", notes = "??????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/digitalassetolktabfieldlist", method = {RequestMethod.GET})
    @ResponseBody
    public Map<String,Object> digitalAssetOlkTabFieldList(String id) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if (StringUtils.isBlank(id)) {
                return resMap.setErr("id????????????").getResultMap();
            }
            //if(id.startsWith("db")){
            //String id1 = id.substring(2);
            TOlkObjectDo objectDo = bydbObjectService.findById(id);
            if( objectDo != null){
                TOlkFieldDo tmp = new TOlkFieldDo();
                tmp.setObjectId( objectDo.getId() );
                tmp.setEnable( 1 );
                List<TOlkFieldDo> beanList = fieldService.findBeanList(tmp);
                List<Object> list = beanList.stream().map(x -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", x.getId());
                    map.put("columnName", x.getFieldName());
                    //map.put("chgStatement", x.getChgStatement());
                    map.put("chgStatement", "");
                    map.put("orgType", x.getFieldType());
                    map.put("columnType", JdbcTypeToJavaTypeUtil.chgType( x.getFieldType() ) );
                    //map.put("columnType",  x.getFieldType() );
                    map.put("chnName", x.getChnName());
                    map.put("tips", StringUtils.isBlank( x.getChnName())? x.getChnName():x.getFieldName());
                    return map;
                }).collect( Collectors.toList());
                resMap.setSingleOk(list, "????????????????????????????????????");
            }
            /*else if(id.startsWith("ds")){
                String id1 = id.substring(2);
                TOlkDatasetDo datasetDo = datasetService.findById(id1);
                List<TOlkDsColumnDo> colList = dsColumnService.findByDatasetId(datasetDo.getId());
                colList = colList.stream().filter(x->x.getEnable()!= null && x.getEnable() ==1).collect(Collectors.toList());
                List<TOlkDsColumnDo> groupList = colList.stream().filter(x -> "group".equalsIgnoreCase(x.getEtype())).collect(Collectors.toList());
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
            else{
                resMap.setErr("????????????????????????????????????,??????id");
            }

        } catch (Exception ex) {
            resMap.setErr("????????????????????????????????????");
            logger.error("????????????????????????????????????:", ex);
        }
        return resMap.getResultMap();
    }

    /*@ApiOperation(value = "pms??????????????????", notes = "pms??????????????????")
    @RequestMapping(value = "/pmsaddobject", method = {RequestMethod.POST})
    public Map<String, Object> pmsAddObject( @RequestBody TOlkModelVo modelInfo, HttpServletRequest request ) {
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

            HashMap<String, TOlkDatabaseDo> dbMap = new HashMap<>();

            TTruModelObjectDo tmp = new TTruModelObjectDo();
            tmp.setModelId( modelDo.getId() );
            List<TTruModelObjectDo> moList = truModelObjectService.find( tmp );
            List<String> idList = moList.stream().map( x -> x.getObjectId() ).collect( Collectors.toList() );
            if ( modelInfo.getObjectDos().size() == 1 ) {
                ts = "";
            }
            List<TTruModelObjectDo> tabList = new ArrayList<>();
            for ( TOlkObjectDo x : modelInfo.getObjectDos() ) {
                if ( idList.indexOf( x.getId() ) >= 0 ) {
                    return result.setErr( ts + "???????????????" ).getResultMap();
                }
                TTruModelObjectDo obj = new TTruModelObjectDo();
                obj.setModelId( modelDo.getId() );
                if ( x.getId().startsWith( "db" ) ) {
                    String id = x.getId().substring( 2 );
                    TOlkObjectDo table = bydbObjectService.findById( id );
                    if ( table == null ) {
                        return result.setErr( ts + "???????????????" ).getResultMap();
                    }
                    TOlkDatabaseDo databaseDo = dbMap.get( table.getDbId() );
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
                    TOlkDatasetDo datasetDo = datasetService.findById( id );
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
    }*/

    @ApiOperation(value = "??????olk??????", notes = "??????olk??????")
    @RequestMapping(value = "/synolkmodel", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String,Object> synOlkModel(@RequestBody TOlkModelDo modelDo) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            TOlkModelDo info = olkModelService.findById( modelDo.getId() );
            if(info != null){
                olkModelService.updateBean(  modelDo );
            }
            else{
                olkModelService.insertBean( modelDo );
            }
            result.setOk("??????????????????");
        } catch (Exception e) {
            logger.error("??????????????????", e);
            result.setErr("??????????????????");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "??????olk??????")
    @RequestMapping(value = "/delolkmodel", method = {RequestMethod.POST})
    public Map<String, Object> delOlkModel( @RequestBody TOlkModelDo modelDo ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            resMap.initSingleObject();
            //UserDo user = LoginUtil.getUser();
            olkModelService.deleteById( modelDo.getId() );
            resMap.setOk("??????????????????");
        }
        catch ( Exception ex ) {
            resMap.setErr( "??????????????????" );
            logger.error( "??????????????????", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "??????olk???????????????", notes = "??????olk???????????????")
    @RequestMapping(value = "/synolkmodelobject", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String,Object> synOlkModelObject(@RequestBody TOlkModelObjectDo info) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            TOlkObjectDo table = bydbObjectService.findById( info.getRealObjId() );
            if( table == null ){
                return null;
            }
            TOlkModelObjectDo tmp = new TOlkModelObjectDo();
            tmp.setModelId( info.getId() );
            tmp.setRealObjId( info.getRealObjId() );
            List<TOlkModelObjectDo> moList = olkModelObjectService.find( tmp );
            if(moList.size()>0){
                return null;
            }
            info.setStype( "db" );
            info.setDbId( table.getDbId() );
            info.setSchemaId( table.getSchemaId() );
            info.setObjectName( table.getObjectName() );
            info.setObjChnName( table.getObjChnName() );
            info.setObjFullName( table.getObjFullName() );
            olkModelObjectService.insertBean( info );
            result.setSingleOk( info,"????????????" );
        } catch (Exception e) {
            logger.error("???????????????????????????", e);
            result.setErr("???????????????????????????");
        }
        return result.getResultMap();
    }

    

    
    @ApiOperation(value = "??????olk????????????????????????", notes = "??????olk????????????????????????")
    @RequestMapping(value = "/findolkmodelobjecreldata", method = {RequestMethod.GET})
    @ApiImplicitParams({
    })
    public Map<String,Object> findOlkModelObjecRelData(String modelId) {
        ResponeMap result = genResponeMap();
        try {
            if( StringUtils.isBlank( modelId )){
                return result.setErr( "modelId??????" ).getResultMap();
            }
            TOlkModelDo modelDo = olkModelService.findById( modelId );
            if( modelDo == null){
                return result.setErr( "???????????????" ).getResultMap();
            }
            List<TOlkModelObjectDo> list = olkModelObjectService.selectByModelId( modelId );
            if( list.size()==0 ){
                return result.setSingleOk( new ArrayList<>(), "??????????????????????????????" ).getResultMap();
            }
            List<String> idList = list.stream().map( x -> x.getRealObjId() ).distinct().collect( Collectors.toList() );

            VOlkObjectVo objTmp = new VOlkObjectVo();
            objTmp.setOwnerNodeId( modelDo.getNodePartyId() );
            objTmp.setOwnerUserId( modelDo.getCreatorId() );
            objTmp.setIdList( idList );

            List<VOlkObjectVo> nodeBeanList = tableService.findNodeBeanList( objTmp );
//            FDataApproveVo approveVo =new FDataApproveVo();
//            approveVo.setCreatorId( modelDo.getCreatorId() );
//            String [] ids = idList.toArray(new String[idList.size()]);
//            approveVo.setOther1( ids );

            List<FDataApproveDo> beanList = dataApproveService.selectApproveByUserDataId( modelDo.getCreatorId(), "'" + String.join( "','", idList ) + "'" );
            for ( VOlkObjectVo bean : nodeBeanList ) {
                for ( FDataApproveDo approve : beanList ) {
                    if( bean.getId().equals( approve.getDataId() ) ){
                        bean.setUserPrivGrant( approve.getApprove() );
                        break;
                    }
                }
            }
            result.setSingleOk(nodeBeanList,"????????????" );
        } catch (Exception e) {
            result.setErr( "????????????????????????????????????" );
            logger.error("????????????????????????????????????,modelId:{}",modelId, e);
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "??????olk???????????????", notes = "??????olk???????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "???ID", dataType = "String", required = true, paramType = "query", example = "")
    })
    @RequestMapping(value = "/delolkmodelobject", method = {RequestMethod.POST})
    public Map<String, Object> delOlkModelObject(@RequestBody List<String> idList ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            if ( idList == null && idList.size()>0 ) {
                return result.setErr( "ID????????????" ).getResultMap();
            }
            Example exp = new Example( TOlkModelObjectDo.class);
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn("id", idList);
            List<TOlkModelObjectDo> list = olkModelObjectService.findByExample( exp );
            if( list.size() ==0 ){
                return result.setOk("??????????????????").getResultMap();
            }
            List<String> modelIdList = list.stream().map( x -> x.getModelId() ).distinct().collect( Collectors.toList() );
            if( modelIdList.size()!=1){
                return result.setErr("???????????????????????????????????????").getResultMap();
            }
            List<String> objIdList = list.stream().map( x -> x.getObjectId() ).distinct().collect( Collectors.toList() );

            exp = new Example( TTruModelElementDo.class);
            criteria = exp.createCriteria();
            criteria.andIn("tcId", objIdList).andEqualTo( "modelId",modelIdList.get( 0 ) );
            int cnt = olkModelElementService.findCountByExample(exp);
            if( cnt >0 ){
                return result.setErr("??????????????????????????????").getResultMap();
            }

            List<String> collect = list.stream().map( x -> x.getId() ).collect( Collectors.toList() );
            olkModelObjectService.deleteByIds( collect );

            result.setOk("????????????" );
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
    public Object synFavouriteObject(@RequestBody TTruFavouriteObjectDo info) {
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
            TOlkObjectDo objectDo = tableService.findById( info.getRelId() );
            if ( objectDo != null ) {
                info.setStype( "db" );
                info.setObjectId(objectDo.getId());
                info.setDbId( objectDo.getDbId() );
                info.setSchemaId( objectDo.getSchemaId() );
                info.setObjName( objectDo.getObjectName() );
                info.setObjFullName( objectDo.getObjFullName() );
                info.setObjChnName( ComUtil.trsEmpty( objectDo.getObjChnName(), objectDo.getObjectName() ) );
            }
            else {
                return result.setErr( "?????????????????????" ).getResultMap();
//                info.setDatasetId( info.getRelId() );
//                info.setStype( "ds" );
//                TOlkDatasetDo datasetDo = datasetService.findById( info.getDatasetId() );
//                if ( datasetDo == null ) {
//                    return result.setErr( "????????????????????????" ).getResultMap();
//                }
//                info.setObjName( datasetDo.getSetCode() );
//                info.setObjFullName( datasetDo.getViewName() );
//                info.setObjChnName( datasetDo.getSetChnName() );
//                info.setObjChnName( ComUtil.trsEmpty( datasetDo.getSetChnName(), datasetDo.getSetCode() ) );
            }

            tmp = favouriteObjectService.findById( info.getId() );
            if( tmp == null ){
                favouriteObjectService.insertBean( info );
            }
            else{
                favouriteObjectService.updateBean( info );
            }
            result.setSingleOk( tmp,"??????????????????");
        } catch (Exception e) {
            logger.error("??????????????????", e);
            result.setErr("??????????????????");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/delfavouriteobject", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String,Object> delFavouriteObject(@RequestBody List<TTruFavouriteObjectDo> list) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            Example exp = new Example( TTruFavouriteObjectDo.class );
            Example.Criteria criteria = exp.createCriteria();
            for ( TTruFavouriteObjectDo tmp : list ) {
                if( StringUtils.isNotBlank(  tmp.getId() )) {
                    criteria.orEqualTo( "id", tmp.getId() );
                }
                else{
                    criteria.orEqualTo( "relId",tmp.getRelId() ).andEqualTo( "userId" ,tmp.getUserId() );
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
            result.setOk("??????????????????");
        } catch (Exception e) {
            logger.error("??????????????????", e);
            result.setErr("??????????????????");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "??????olk??????", notes = "??????olk??????")
    @RequestMapping(value = "/synolkapplyobject", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object synOlkApplyObject(@RequestBody FDataApproveDo info) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            Example exp = new Example(FDataApproveDo.class);
            Example.Criteria criteria = exp.createCriteria();
            criteria.andEqualTo( "dataId" ,info.getDataId() ).andEqualTo( "userId",info.getUserId() )
                    .andNotEqualTo( "approve" ,9 );
            Integer cnt = applyObjectService.findCountByExample( exp );
            if(cnt>0){
                result.setErr("????????????????????????");
            }
            TOlkObjectDo tab = tableService.findById( info.getDataId() );
            if( tab !=  null){
                info.setUserId(  tab.getUserId() );
                info.setUserName( tab.getUserName() );
                applyObjectService.insertBean( info );

                result.setSingleOk(info,"??????????????????");
            }
            else{
                result.setErr("?????????????????????");
            }

        } catch (Exception e) {
            logger.error("??????????????????", e);
            result.setErr("??????????????????");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @RequestMapping(value = "/delapplyobject", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Map<String,Object> delApplyObject(@RequestBody List<FDataApproveDo> list) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            List<String> idList = list.stream().map( x -> x.getId() ).collect( Collectors.toList() );
            applyObjectService.deleteByIds( idList );
            result.setOk("??????????????????");
        } catch (Exception e) {
            logger.error("??????????????????", e);
            result.setErr("??????????????????");
        }
        return result.getResultMap();
    }
    @ApiOperation(value = "??????olk??????", notes = "??????olk??????")
    @RequestMapping(value = "/cancelolkapplyobject", method = {RequestMethod.POST})
    @ApiImplicitParams({
    })
    public Object cancelOlkApplyObject( @RequestParam(value = "id",required = false) String id, @RequestParam(value = "relId" ,required = false) String relId, @RequestParam(value = "userId",required = false) String userId ) {
        ResponeMap result = genResponeMap();
        try {
            result.initSingleObject();
            if (StringUtils.isBlank(id) && ( StringUtils.isBlank(relId) || StringUtils.isBlank( userId ))) {
                return result.setErr("id?????????id??????????????????").getResultMap();
            }
            List<String> ids = null;

            if (StringUtils.isNotBlank(id)) {
                ids = Arrays.asList(id.split(",|\\s+"));
            }
            List<String> rels = null;
            if (StringUtils.isNotBlank(relId)) {
                rels = Arrays.asList(relId.split(",|\\s+"));
            }

            Example exp = new Example(FDataApproveDo.class);
            Example.Criteria criteria = exp.createCriteria();

            if (ids != null) {
                criteria.andIn("id", ids);
            }
            if (rels != null) {
                criteria.andIn("dataId", rels);
                criteria.andEqualTo("creatorId", userId);
            }
            criteria.andEqualTo( "approve" ,2 );
            List<FDataApproveDo> list = applyObjectService.findByExample( exp );
            int cnt = list.size();
            if (cnt == 0) {
                return result.setErr("?????????????????????").getResultMap();
            }
            if (ids != null && list.size() != ids.size()) {
                return result.setErr("??????????????????").getResultMap();
            }
            for ( FDataApproveDo approveDo : list ) {
                approveDo.setApprove( 9 );
            }
            applyObjectService.batchUpdateByPrimaryKey( list );
            result.setOk("??????????????????");
        } catch (Exception e) {
            logger.error("??????????????????", e);
            result.setErr("??????????????????");
        }
        return result.getResultMap();
    }

}
