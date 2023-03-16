package cn.bywin.business.job;


import static cn.bywin.business.common.util.Constants.FLSYSTEM;

import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.olk.TOlkDataNodeDo;
import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkDcServerDo;
import cn.bywin.business.bean.olk.TOlkFieldDo;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.olk.TOlkSchemaDo;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.view.TokenVo;
import cn.bywin.business.bean.view.olk.OlkObjectWithFieldsVo;
import cn.bywin.business.common.encrypt.JwtHs;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.service.olk.OlkDataNodeService;
import cn.bywin.business.service.olk.OlkDatabaseService;
import cn.bywin.business.service.olk.OlkDcServerService;
import cn.bywin.business.service.olk.OlkFieldService;
import cn.bywin.business.service.olk.OlkModelObjectService;
import cn.bywin.business.service.olk.OlkModelService;
import cn.bywin.business.service.olk.OlkObjectService;
import cn.bywin.business.service.olk.OlkSchemaService;
import cn.bywin.business.service.system.SysUserService;
import cn.bywin.business.trumodel.ApiOlkDbService;
import cn.bywin.common.resp.ObjectResp;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class OlkDataNodeJob {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OlkDatabaseService databaseService;

    @Autowired
    private OlkDcServerService dcServer;

    @Autowired
    private OlkSchemaService schemaService;

    @Autowired
    private OlkObjectService bydbObjectService;

    @Autowired
    private OlkFieldService fieldService;


    @Autowired
    private OlkModelService olkModelService;

    @Autowired
    private OlkModelObjectService olkModelObjectService;

    @Autowired
    private ApiOlkDbService apiOlkDbService;

    @Autowired
    private NodePartyService nodePartyService;

    @Autowired
    private OlkDataNodeService dataNodeService;

    @Autowired
    private SysUserService sysUserService;

    private static List<String>  dcList =  Lists.newCopyOnWriteArrayList();

    private static List<String>  dbList =  Lists.newCopyOnWriteArrayList();
    private static List<String> schemaList =  Lists.newCopyOnWriteArrayList();
    private static List<String> tableList =  Lists.newCopyOnWriteArrayList();

    private static List<String> olkModelList =  Lists.newCopyOnWriteArrayList();
    private static List<String> olkModelObjectList =  Lists.newCopyOnWriteArrayList();

    private static boolean  binit =false;


    public static  void addDc( String id ) {
        if( id != null ) {
            dcList.add( id );
        }
    }
    public static  void addDcList( List<String> list ) {
        dcList.addAll( list );
    }

    public static  void addDb( String id ) {
        if( id != null ) {
            dbList.add( id );
        }
    }
    public static  void addDbList( List<String> list ) {
        dbList.addAll( list );
    }

    public static  void addSchema( String id ) {
        if( id != null ) {
            schemaList.add( id );
        }
    }
    public static  void addSchemaList( List<String> list ) {
        schemaList.addAll( list );
    }

    public static  void addTable( String id ) {
        if( id != null ) {
            tableList.add( id );
        }
    }
    public static  void addTableList( List<String> list ) {
        tableList.addAll( list );
    }

    public static  void addOlkModel( String id ) {
        if( id != null ) {
            olkModelList.add( id );
        }
    }
    public static  void addOlkModelList( List<String> list ) {
        olkModelList.addAll( list );
    }
    public static  void addOlkModelObject( String id ) {
        if( id != null ) {
            olkModelObjectList.add( id );
        }
    }
    public static  void addOlkModelObjectList( List<String> list ) {
        olkModelObjectList.addAll( list );
    }

    public static void reInit(){
        binit  = false;
    }

    @Scheduled(fixedRate =10 * 1000, initialDelay = 10 * 1000)
    public void synData() {

        try {
            if(!binit){
                logger.info( "初始化OLK待上报数据" );

                TOlkDcServerDo dcTmp = new TOlkDcServerDo();
                dcTmp.setSynFlag( 0 );
                List<TOlkDcServerDo> dcTmpList = dcServer.find( dcTmp );
                if( dcTmpList.size()>0){
                    List<String> collect = dcTmpList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                    logger.info( "初始化OLK待上报节点:{}",collect );
                    addDcList( collect );
                }

                TOlkDatabaseDo dbTmp = new TOlkDatabaseDo();
                dbTmp.setSynFlag( 0 );
                List<TOlkDatabaseDo> dbTmpList = databaseService.find( dbTmp );
                if( dbTmpList.size()>0){
                    List<String> collect = dbTmpList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                    logger.info( "初始化OLK待上报目录:{}",collect );
                    addDbList( collect );
                }

                TOlkSchemaDo schemaTmp = new TOlkSchemaDo();
                schemaTmp.setSynFlag( 0 );
                List<TOlkSchemaDo> schemaTmpList = schemaService.find( schemaTmp );
                if( schemaTmpList.size()>0){
                    List<String> collect = schemaTmpList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                    logger.info( "初始化OLK待上报库:{}",collect );
                    addSchemaList( collect );
                }

                TOlkObjectDo objTmp = new TOlkObjectDo();
                objTmp.setSynFlag( 0 );
                List<TOlkObjectDo> objTmpList = bydbObjectService.find( objTmp );
                if( objTmpList.size()>0){
                    List<String> collect = objTmpList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                    logger.info( "初始化OLK待上报对象:{}",collect );
                    addTableList( collect );
                }

                TOlkModelDo olkMTmp = new TOlkModelDo();
                olkMTmp.setSynFlag( 0 );
                List<TOlkModelDo> olkTmpMList = olkModelService.find( olkMTmp );
                if( olkTmpMList.size()>0){
                    List<String> collect = olkTmpMList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                    logger.info( "初始化OLK待上报olk模型:{}",collect );
                    addOlkModelList( collect );
                }

                TOlkModelObjectDo olkMoTmp = new TOlkModelObjectDo();
                olkMoTmp.setSynFlag( 0 );
                List<TOlkModelObjectDo> olkMoTmpList = olkModelObjectService.find( olkMoTmp );
                if( olkMoTmpList.size()>0){
                    List<String> collect = olkMoTmpList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                    logger.info( "初始化待上报olk模型引用对象:{}",collect );
                    addOlkModelObjectList( collect );
                }

                binit = true;
            }

            String token = null;
            Map<String,TOlkDatabaseDo> dbMap = new HashMap<>();
            Map<String,TOlkSchemaDo> schemaMap = new HashMap<>();
            //同步数据源
            FNodePartyDo nodePartyDo = null;

            while( dcList.size()>0){
                String id =dcList.get( 0 );
                logger.info( "上报节点:{}",id );
                try{
                    TOlkDcServerDo info = dcServer.findById( id );
                    if( info == null ){ //数据已删除
                        dcList.remove( id );
                    }
                    else {
                        if( StringUtils.isBlank( token ) ){
                            token = genToken();
                        }
                        if( nodePartyDo == null ){
                            nodePartyDo = nodePartyService.findFirst();
                        }
                        if ( synDc( info, token ,nodePartyDo) ) {
                            //dbMap.put( info.getId(), info );
                            dcList.remove( id );
                        }
                    }
                }
                catch ( Exception ex ){
                    logger.error( "上报节点{}错误,",id,ex );
                }
            }

            while( dbList.size()>0){
                String id =dbList.get( 0 );
                logger.info( "上报目录:{}",id );
                try{
                    TOlkDatabaseDo info = databaseService.findById( id );
                    if( info == null ){ //数据已删除
                        dbList.remove( id );
                    }
                    else {
                        if( StringUtils.isBlank( token ) ){
                            token = genToken();
                        }
                        if( nodePartyDo == null ){
                            nodePartyDo = nodePartyService.findFirst();
                        }
                        if ( synDb( info, token ,nodePartyDo) ) {
                            dbMap.put( info.getId(), info );
                            dbList.remove( id );
                        }
                    }
                }
                catch ( Exception ex ){
                    logger.error( "上报目录{}错误,",id,ex );
                }
            }

            while( schemaList.size()>0){
                String id =schemaList.get( 0 );
                logger.info( "上报库:{}",id );
                try{
                    TOlkSchemaDo info = schemaService.findById( id );
                    if( info == null ){ //数据已删除
                        schemaList.remove( id );
                    }
                    else {
                        if( StringUtils.isBlank( token ) ){
                            token = genToken();
                        }
                        if( !dbMap.containsKey(  info.getDbId() )) {
                            TOlkDatabaseDo dbDo = databaseService.findById( info.getDbId() );
                            if( nodePartyDo == null ){
                                nodePartyDo = nodePartyService.findFirst();
                            }
                            if ( synDb( dbDo, token ,nodePartyDo) ) {
                                dbMap.put( dbDo.getId(), dbDo );
                            }
                        }
                        if( !dbMap.containsKey( info.getDbId() )){
                            schemaMap.remove( id );
                        }
                        else {
                            if( nodePartyDo == null ){
                                nodePartyDo = nodePartyService.findFirst();
                            }
                            if ( synSchema( info, token ,nodePartyDo) ) {
                                schemaMap.put( info.getId(), info );
                                schemaList.remove( id );;
                            }
                        }
                    }
                }
                catch ( Exception ex ){
                    logger.error( "上报目录{}错误,",id,ex );
                }
            }

            while( tableList.size()>0){
                String id =tableList.get( 0 );
                logger.info( "上报对象:{}",id );
                try{
                    TOlkObjectDo info = bydbObjectService.findById( id );
                    if( info == null ){ //数据已删除
                        tableList.remove( id );
                    }
                    else {
                        if( StringUtils.isBlank( token ) ){
                            token = genToken();
                        }

                        if( !dbMap.containsKey(  info.getDbId() )) {
                            TOlkDatabaseDo dbDo = databaseService.findById( info.getDbId() );
                            if( nodePartyDo == null ){
                                nodePartyDo = nodePartyService.findFirst();
                            }
                            if ( synDb( dbDo, token ,nodePartyDo) ) {
                                dbMap.put( dbDo.getId(), dbDo );
                            }
                        }

                        if( !dbMap.containsKey( info.getDbId() )){ //目录不存在
                            tableList.remove( id );
                        }
                        else{
                            if( !schemaMap.containsKey( info.getSchemaId() )){
                                TOlkSchemaDo schemaDo = schemaService.findById( info.getSchemaId() );
                                if( nodePartyDo == null ){
                                    nodePartyDo = nodePartyService.findFirst();
                                }
                                if( synSchema( schemaDo,token,nodePartyDo )){
                                    schemaMap.put( schemaDo.getId(),schemaDo );
                                }
                            }
                        }
                        if( !schemaMap.containsKey( info.getSchemaId() )){
                            tableList.remove( id );
                        }
                        else {
                            if ( StringUtils.isBlank( info.getNodePartyId() ) ) {
                                info.setNodePartyId( nodePartyDo.getId() );
                            }
                            List<TOlkDataNodeDo> tabNodeList = dataNodeService.findByDataId( info.getId() );
                            List<TOlkFieldDo> fieldList = fieldService.selectByObjectId( info.getId() );
                            OlkObjectWithFieldsVo objFld = new OlkObjectWithFieldsVo();
                            MyBeanUtils.copyBeanNotNull2Bean( info,objFld );
                            objFld.setFieldList( fieldList );
                            objFld.setDataNodeList(  tabNodeList );

                            if( nodePartyDo == null ){
                                nodePartyDo = nodePartyService.findFirst();
                            }
                            if ( synTable( objFld, token ,nodePartyDo) ) {
                                tableList.remove( id );
                            }
                        }
                    }
                }
                catch ( Exception ex ){
                    logger.error( "上报目录{}错误,",id,ex );
                }
            }

            //olk model
            while( olkModelList.size()>0){
                String id = olkModelList.get( 0 );
                logger.info( "上报olk模型:{}",id );
                try{
                    TOlkModelDo info = olkModelService.findById( id );
                    if( info == null ){ //数据已删除
                        olkModelList.remove( id );
                    }
                    else {
                        if( StringUtils.isBlank( token ) ){
                            token = genToken();
                        }
                        if( nodePartyDo == null ){
                            nodePartyDo = nodePartyService.findFirst();
                        }
                        if( synOlkModel(info,token,nodePartyDo)) {
                        }
                    }
                }
                catch ( Exception ex ){
                    logger.error( "上报olk模型{}错误,",id,ex );
                }
            }

            while( olkModelObjectList.size()>0){
                String id =olkModelObjectList.get( 0 );
                logger.info( "上报模型引用对象:{}",id );
                try{
                    TOlkModelObjectDo info = olkModelObjectService.findById( id );
                    if( info == null ){ //数据已删除
                        olkModelObjectList.remove( id );
                    }
                    else {
                        if( StringUtils.isBlank( token ) ){
                            token = genToken();
                        }
                        if( nodePartyDo == null ){
                            nodePartyDo = nodePartyService.findFirst();
                        }
                        if( synOlkModelObject(info,token,nodePartyDo)) {
                        }
                    }
                }
                catch ( Exception ex ){
                    logger.error( "上报模型引用对象{}错误,",id,ex );
                }
            }
        }
        catch (Exception ex){
            logger.error( "" ,ex);
            System.out.println("it is error");
        }
        logger.debug( "上报数据完成" );
    }

    private boolean synDc(TOlkDcServerDo info, String token,FNodePartyDo nodePartyDo){
        info.setNodePartyId( nodePartyDo.getId() );
        ObjectResp<String> retMap = apiOlkDbService.synOlkDcServer( info, token );
        if ( retMap.isSuccess() ) {
            TOlkDcServerDo tmp = new TOlkDcServerDo();
            tmp.setId( info.getId() );
            tmp.setSynFlag( 1 );
            tmp.setNodePartyId( info.getNodePartyId() );
            //tmp.setShareTime( ComUtil.getCurTimestamp() );
            dcServer.updateNoNull( tmp );
            return true;
        }
        else{
            String msg = String.format( "上报节点(%s)失败,%s",info.getId(),retMap.getMsg());
            logger.error( msg );
            return false;
        }
    }

    private boolean synDb(TOlkDatabaseDo info, String token,FNodePartyDo nodePartyDo){
        info.setNodePartyId( nodePartyDo.getId() );
        ObjectResp<String> retMap = apiOlkDbService.synOlkDatabase( info, token );
        if ( retMap.isSuccess() ) {
            TOlkDatabaseDo tmp = new TOlkDatabaseDo();
            tmp.setId( info.getId() );
            tmp.setSynFlag( 1 );
            tmp.setNodePartyId( info.getNodePartyId() );
            //tmp.setShareTime( ComUtil.getCurTimestamp() );
            databaseService.updateNoNull( tmp );
            return true;
        }
        else{
            String msg = String.format( "上报目录(%s)失败,%s",info.getId(), retMap.getMsg());
            logger.error( msg );
            return false;
        }
    }
    private boolean synSchema(TOlkSchemaDo info,String token,FNodePartyDo nodePartyDo){
        info.setNodePartyId( nodePartyDo.getId() );
        ObjectResp<String> retMap = apiOlkDbService.synOlkSchema( info, token );
        if ( retMap.isSuccess() ) {
            TOlkSchemaDo tmp = new TOlkSchemaDo();
            tmp.setId( info.getId() );
            tmp.setSynFlag( 1 );
            tmp.setNodePartyId( info.getNodePartyId() );
            //tmp.setShareTime( ComUtil.getCurTimestamp() );
            schemaService.updateNoNull( tmp );
            return true;
        }
        else{
            String msg = String.format( "上报库(%s)失败,%s",info.getId(),retMap.getMsg());
            logger.error( msg );
            return false;
        }
    }
    private boolean synTable( OlkObjectWithFieldsVo data, String token, FNodePartyDo nodePartyDo ){
        data.setNodePartyId( nodePartyDo.getId() );
        List<OlkObjectWithFieldsVo> list = new ArrayList<>();
        list.add(  data );
        ObjectResp<String> retMap = apiOlkDbService.synOlkTable( list, token );
        if ( retMap.isSuccess() ) {
            TOlkObjectDo tmp = new TOlkObjectDo();
            tmp.setId( data.getId() );
            tmp.setSynFlag( 1 );
            tmp.setNodePartyId( data.getNodePartyId() );
            tmp.setShareTime( ComUtil.getCurTimestamp() );
            bydbObjectService.updateNoNull( tmp );
            return true;
        }
        else{
            String msg = String.format( "上报库(%s)失败,%s",data.getId(),retMap.getMsg());
            logger.error( msg );
            return false;
        }
    }


    private boolean synOlkModel(TOlkModelDo info, String token,FNodePartyDo nodePartyDo){
        info.setNodePartyId( nodePartyDo.getId() );
        ObjectResp<String> retVal = apiOlkDbService.synOlkModel( info, token );
        if ( retVal.isSuccess() ) {
            TOlkModelDo tmp = new TOlkModelDo();
            tmp.setId( info.getId() );
            tmp.setSynFlag( 1 );
            tmp.setNodePartyId( info.getNodePartyId() );
            //tmp.setShareTime( ComUtil.getCurTimestamp() );
            olkModelService.updateNoNull( tmp );
            olkModelList.remove( info.getId() );
            return true;
        }
        else{
            String msg = String.format( "上报olk模型引用对象(%s)失败,错误信息：%s",info.getId(),retVal.getMsg() );
            logger.error( msg );
            return false;
        }
    }

    private boolean synOlkModelObject( TOlkModelObjectDo info, String token, FNodePartyDo nodePartyDo){
        info.setNodePartyId( nodePartyDo.getId() );
        List<TOlkModelObjectDo> list = new ArrayList<>();
        list.add(  info );
        ObjectResp<TOlkModelObjectDo> retMap = apiOlkDbService.synOlkModelObject( info, token );
        if ( retMap.isSuccess() ) {
            TOlkModelObjectDo tmp = new TOlkModelObjectDo();
            tmp.setId( info.getId() );
            tmp.setSynFlag( 1 );
            tmp.setNodePartyId( info.getNodePartyId() );
            //tmp.setShareTime( ComUtil.getCurTimestamp() );
            olkModelObjectService.updateNoNull( tmp );
            olkModelObjectList.remove( info.getId() );
            return true;
        }
        else{
            String msg = String.format( "上报olk模型引用对象(%s)失败",info.getId());
//            if ( retMap.containsKey( "msg" ) ) {
//                msg = String.format( "%s,错误信息：%s",msg,retMap.get( "msg" ) );
//            }
            logger.error( msg );
            return false;
        }
    }

    private String genToken(){
            SysUserDo sysUserDo = sysUserService.getSystemUser(FLSYSTEM);
//            UserDo userDo = new UserDo();
//            userDo.setUserId(sysUserDo.getId());
//            userDo.setUserName(sysUserDo.getMobile());
//            userDo.setChnName(sysUserDo.getUsername());
           FNodePartyDo nodePartyDo = nodePartyService.findFirst();

            TokenVo tokenVo = new TokenVo();
            tokenVo.setUuid( ComUtil.genId());
            tokenVo.setNode(nodePartyDo.getId());
            tokenVo.setTs(System.currentTimeMillis());
            String text = JsonUtil.toJson(tokenVo);
            //  String token = Des.encrypt(text, Constants.DESTOKEN);
            return JwtHs.buildJWT(text);
    }
}
