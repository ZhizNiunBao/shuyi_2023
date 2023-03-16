package cn.bywin.business.job;


import static cn.bywin.business.common.util.Constants.FLSYSTEM;

import cn.bywin.business.bean.bydb.TBydbDataNodeDo;
import cn.bywin.business.bean.bydb.TBydbDatabaseDo;
import cn.bywin.business.bean.bydb.TBydbFieldDo;
import cn.bywin.business.bean.bydb.TBydbObjectDo;
import cn.bywin.business.bean.bydb.TBydbSchemaDo;
import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.bydb.TTruModelObjectDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.view.TokenVo;
import cn.bywin.business.bean.view.bydb.BydbObjectFieldsVo;
import cn.bywin.business.common.encrypt.JwtHs;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.Constants;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.service.bydb.BydbDataNodeService;
import cn.bywin.business.service.bydb.BydbDatabaseService;
import cn.bywin.business.service.bydb.BydbFieldService;
import cn.bywin.business.service.bydb.BydbObjectService;
import cn.bywin.business.service.bydb.BydbSchemaService;
import cn.bywin.business.service.bydb.TruModelObjectService;
import cn.bywin.business.service.bydb.TruModelService;
import cn.bywin.business.service.federal.DataSourceService;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.service.system.SysUserService;
import cn.bywin.business.trumodel.ApiTruModelService;
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
public class BydbDataNodeJob {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private BydbDatabaseService databaseService;

    @Autowired
    private BydbSchemaService schemaService;

    @Autowired
    private BydbObjectService bydbObjectService;

    @Autowired
    private BydbFieldService fieldService;

    @Autowired
    private TruModelObjectService modelObjService;

    @Autowired
    private TruModelService truModelService;

    @Autowired
    private TruModelObjectService truModelObjectService;

//    @Autowired
//    private OlkModelService olkModelService;

//    @Autowired
//    private OlkModelObjectService olkModelObjectService;

    @Autowired
    private ApiTruModelService apiTruModelService;

    @Autowired
    private NodePartyService nodePartyService;

    @Autowired
    private BydbDataNodeService dataNodeService;

    @Autowired
    private SysUserService sysUserService;

    private static List<String> dbSourceList =  Lists.newCopyOnWriteArrayList();
    private static List<String>  dbList =  Lists.newCopyOnWriteArrayList();
    private static List<String> schemaList =  Lists.newCopyOnWriteArrayList();
    private static List<String> tableList =  Lists.newCopyOnWriteArrayList();
    private static List<String> truModelList =  Lists.newCopyOnWriteArrayList();
    private static List<String> truModelObjectList =  Lists.newCopyOnWriteArrayList();

//    private static List<String> olkModelList =  Lists.newCopyOnWriteArrayList();
//    private static List<String> olkModelObjectList =  Lists.newCopyOnWriteArrayList();

    private static boolean  binit =false;

    public static  void addDbSource( String id ) {
        if( id != null ) {
            dbSourceList.add( id );
        }
    }
    public static  void addDbSourceList( List<String> list ) {
        if( list != null && list.size()>0) {
            dbSourceList.addAll( list );
        }
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

    public static  void addTruModel( String id ) {
        if( id != null ) {
            truModelList.add( id );
        }
    }
    public static  void addTruModelList( List<String> list ) {
        truModelList.addAll( list );
    }
    public static  void addTruModelObject( String id ) {
        if( id != null ) {
            truModelObjectList.add( id );
        }
    }
    public static  void addTruModelObjectList( List<String> list ) {
        truModelObjectList.addAll( list );
    }

//    public static  void addOlkModel( String id ) {
//        if( id != null ) {
//            olkModelList.add( id );
//        }
//    }
//    public static  void addOlkModelList( List<String> list ) {
//        olkModelList.addAll( list );
//    }
//    public static  void addOlkModelObject( String id ) {
//        if( id != null ) {
//            olkModelObjectList.add( id );
//        }
//    }
//    public static  void addOlkModelObjectList( List<String> list ) {
//        olkModelObjectList.addAll( list );
//    }

    public static void reInit(){
        binit  = false;
    }

    @Scheduled(fixedRate =10 * 1000, initialDelay = 10 * 1000)
    public void synData() {

        try {
            if(!binit){
                logger.info( "初始化待上报数据" );
                FDatasourceDo sourceTmp = new FDatasourceDo();
                sourceTmp.setSynFlag( 0 );
                List<FDatasourceDo> sourceTmpList = dataSourceService.find( sourceTmp );
                if( sourceTmpList.size()>0){
                    List<String> collect = sourceTmpList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                    logger.info( "初始化待上报数据源:{}",collect );
                    addDbSourceList( collect );
                }

                TBydbDatabaseDo dbTmp = new TBydbDatabaseDo();
                dbTmp.setSynFlag( 0 );
                List<TBydbDatabaseDo> dbTmpList = databaseService.find( dbTmp );
                if( dbTmpList.size()>0){
                    List<String> collect = dbTmpList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                    logger.info( "初始化待上报目录:{}",collect );
                    addDbList( collect );
                }

                TBydbSchemaDo schemaTmp = new TBydbSchemaDo();
                schemaTmp.setSynFlag( 0 );
                List<TBydbSchemaDo> schemaTmpList = schemaService.find( schemaTmp );
                if( schemaTmpList.size()>0){
                    List<String> collect = schemaTmpList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                    logger.info( "初始化待上报库:{}",collect );
                    addSchemaList( collect );
                }

                TBydbObjectDo objTmp = new TBydbObjectDo();
                objTmp.setSynFlag( 0 );
                List<TBydbObjectDo> objTmpList = bydbObjectService.find( objTmp );
                if( objTmpList.size()>0){
                    List<String> collect = objTmpList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                    logger.info( "初始化待上报对象:{}",collect );
                    addTableList( collect );
                }

                TTruModelDo modelTmp = new TTruModelDo();
                modelTmp.setSynFlag( 0 );
                List<TTruModelDo> modelTmpList = truModelService.find( modelTmp );
                if( modelTmpList.size()>0){
                    List<String> collect = modelTmpList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                    logger.info( "初始化待上报可信模型:{}",collect );
                    addTruModelList( collect );
                }

                TTruModelObjectDo moTmp = new TTruModelObjectDo();
                moTmp.setSynFlag( 0 );
                List<TTruModelObjectDo> moTmpList = truModelObjectService.find( moTmp );
                if( moTmpList.size()>0){
                    List<String> collect = moTmpList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
                    logger.info( "初始化待上报可信模型引用对象:{}",collect );
                    addTruModelObjectList( collect );
                }


//                TOlkModelDo olkMTmp = new TOlkModelDo();
//                olkMTmp.setSynFlag( 0 );
//                List<TOlkModelDo> olkMList = olkModelService.find( olkMTmp );
//                if( olkMList.size()>0){
//                    List<String> collect = olkMList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
//                    logger.info( "初始化待上报olk模型:{}",collect );
//                    addOlkModelList( collect );
//                }
//
//                TOlkModelObjectDo olkMoTmp = new TOlkModelObjectDo();
//                olkMoTmp.setSynFlag( 0 );
//                List<TOlkModelObjectDo> olkMoList = olkModelObjectService.find( olkMoTmp );
//                if( olkMoList.size()>0){
//                    List<String> collect = olkMoList.stream().map( x -> x.getId() ).collect( Collectors.toList() );
//                    logger.info( "初始化待上报olk模型引用对象:{}",collect );
//                    addOlkModelObjectList( collect );
//                }

                binit = true;
            }

            String token = null;
            Map<String,FDatasourceDo> sourceMap = new HashMap<>();
            Map<String,TBydbDatabaseDo> dbMap = new HashMap<>();
            Map<String,TBydbSchemaDo> schemaMap = new HashMap<>();
            //同步数据源
            FNodePartyDo nodePartyDo = null;

            while( dbSourceList.size()>0){
               String id =dbSourceList.get( 0 );
                logger.info( "上报数据源:{}",id );
                try{
                    FDatasourceDo info = dataSourceService.findById( id );
                    if( info == null ){ //数据已删除
                        dbSourceList.remove( id );
                    }
                    else {
                        if( StringUtils.isBlank( token ) ){
                            token = genToken();
                        }
                        if( nodePartyDo == null ){
                            nodePartyDo = nodePartyService.findFirst();
                        }
                        if( synSource(info,token,nodePartyDo)) {
                            sourceMap.put( info.getId(),info );
                            dbSourceList.remove( id );
                        }
                    }
                }
                catch ( Exception ex ){
                    logger.error( "上报数据源{}错误,",id,ex );
                }
            }

            while( dbList.size()>0){
                String id =dbList.get( 0 );
                logger.info( "上报目录:{}",id );
                try{
                    TBydbDatabaseDo info = databaseService.findById( id );
                    if( info == null ){ //数据已删除
                        dbList.remove( id );
                    }
                    else {
                        if( StringUtils.isBlank( token ) ){
                            token = genToken();
                        }
                        if( !Constants.dchetu.equals( info.getDbsourceId() ) && !sourceMap.containsKey( info.getDbsourceId() )){
                            FDatasourceDo sourceDo = dataSourceService.findById( info.getDbsourceId() );
                            if( sourceDo != null ){
                                if( nodePartyDo == null ){
                                    nodePartyDo = nodePartyService.findFirst();
                                }
                                if( synSource(sourceDo,token,nodePartyDo)) {
                                    sourceMap.put( sourceDo.getId(),sourceDo );
                                }
                            }
                        }
                        if( !Constants.dchetu.equals( info.getDbsourceId() ) &&  !sourceMap.containsKey( info.getDbsourceId() )){ //找不到数据源
                            dbList.remove( id );
                        }
                        else {
                            if( nodePartyDo == null ){
                                nodePartyDo = nodePartyService.findFirst();
                            }
                            if ( synDb( info, token ,nodePartyDo) ) {
                                dbMap.put( info.getId(), info );
                                dbList.remove( id );
                            }
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
                    TBydbSchemaDo info = schemaService.findById( id );
                    if( info == null ){ //数据已删除
                        schemaList.remove( id );
                    }
                    else {
                        if( StringUtils.isBlank( token ) ){
                            token = genToken();
                        }
                        if( !dbMap.containsKey(  info.getDbId() )) {
                            TBydbDatabaseDo dbDo = databaseService.findById( info.getDbId() );
                            if( !Constants.dchetu.equals( dbDo.getDbsourceId() ) && !sourceMap.containsKey( dbDo.getDbsourceId() )){
                                FDatasourceDo sourceDo = dataSourceService.findById( dbDo.getDbsourceId() );
                                if( sourceDo != null ){
                                    if( nodePartyDo == null ){
                                        nodePartyDo = nodePartyService.findFirst();
                                    }
                                    if( synSource(sourceDo,token,nodePartyDo)) {
                                        sourceMap.put( sourceDo.getId(),sourceDo );
                                    }
                                }
                            }
                            if(  !Constants.dchetu.equals( dbDo.getDbsourceId() ) && !sourceMap.containsKey( dbDo.getDbsourceId() )){ //找不到数据源
                                schemaList.remove( id );
                            }
                            else {
                                if( nodePartyDo == null ){
                                    nodePartyDo = nodePartyService.findFirst();
                                }
                                if ( synDb( dbDo, token ,nodePartyDo) ) {
                                    dbMap.put( dbDo.getId(), dbDo );
                                }
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
                    TBydbObjectDo info = bydbObjectService.findById( id );
                    if( info == null ){ //数据已删除
                        tableList.remove( id );
                    }
                    else {
                        if( StringUtils.isBlank( token ) ){
                            token = genToken();
                        }

                        if( !dbMap.containsKey(  info.getDbId() )) {
                            TBydbDatabaseDo dbDo = databaseService.findById( info.getDbId() );
                            if( !Constants.dchetu.equals( dbDo.getDbsourceId() ) && !sourceMap.containsKey( dbDo.getDbsourceId() )){
                                FDatasourceDo sourceDo = dataSourceService.findById( dbDo.getDbsourceId() );
                                if( sourceDo != null ){
                                    if( nodePartyDo == null ){
                                        nodePartyDo = nodePartyService.findFirst();
                                    }
                                    if( synSource(sourceDo,token,nodePartyDo)) {
                                        sourceMap.put( sourceDo.getId(),sourceDo );
                                    }
                                }
                            }
                            if( !Constants.dchetu.equals( dbDo.getDbsourceId() ) && !sourceMap.containsKey( dbDo.getDbsourceId() )){ //找不到数据源
                                tableList.remove( id );
                            }
                            else {
                                if( nodePartyDo == null ){
                                    nodePartyDo = nodePartyService.findFirst();
                                }
                                if ( synDb( dbDo, token ,nodePartyDo) ) {
                                    dbMap.put( dbDo.getId(), dbDo );
                                }
                            }
                        }

                        if( !dbMap.containsKey( info.getDbId() )){ //目录不存在
                            tableList.remove( id );
                        }
                        else{
                            if( !schemaMap.containsKey( info.getSchemaId() )){
                                TBydbSchemaDo schemaDo = schemaService.findById( info.getSchemaId() );
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
                            List<TBydbDataNodeDo> tabNodeList = dataNodeService.findByDataId( info.getId() );
                            List<TBydbFieldDo> fieldList = fieldService.selectByObjectId( info.getId() );
                            BydbObjectFieldsVo objFld = new BydbObjectFieldsVo();
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

            while( truModelList.size()>0){
                String id = truModelList.get( 0 );
                logger.info( "上报模型:{}",id );
                try{
                    TTruModelDo info = truModelService.findById( id );
                    if( info == null ){ //数据已删除
                        truModelList.remove( id );
                    }
                    else {
                        if( StringUtils.isBlank( token ) ){
                            token = genToken();
                        }
                        if( nodePartyDo == null ){
                            nodePartyDo = nodePartyService.findFirst();
                        }
                        if( synTruModel(info,token,nodePartyDo)) {
                        }
                    }
                }
                catch ( Exception ex ){
                    logger.error( "上报模型{}错误,",id,ex );
                }
            }

            while( truModelObjectList.size()>0){
                String id =truModelObjectList.get( 0 );
                logger.info( "上报模型引用对象:{}",id );
                try{
                    TTruModelObjectDo info = truModelObjectService.findById( id );
                    if( info == null ){ //数据已删除
                        truModelObjectList.remove( id );
                    }
                    else {
                        if( StringUtils.isBlank( token ) ){
                            token = genToken();
                        }
                        if( nodePartyDo == null ){
                            nodePartyDo = nodePartyService.findFirst();
                        }
                        if( synTruModelObject(info,token,nodePartyDo)) {
                        }
                    }
                }
                catch ( Exception ex ){
                    logger.error( "上报模型引用对象{}错误,",id,ex );
                }
            }

            //olk model
//            while( olkModelList.size()>0){
//                String id = olkModelList.get( 0 );
//                logger.info( "上报olk模型:{}",id );
//                try{
//                    TOlkModelDo info = olkModelService.findById( id );
//                    if( info == null ){ //数据已删除
//                        olkModelList.remove( id );
//                    }
//                    else {
//                        if( StringUtils.isBlank( token ) ){
//                            token = genToken();
//                        }
//                        if( nodePartyDo == null ){
//                            nodePartyDo = nodePartyService.findFirst();
//                        }
//                        if( synOlkModel(info,token,nodePartyDo)) {
//                        }
//                    }
//                }
//                catch ( Exception ex ){
//                    logger.error( "上报olk模型{}错误,",id,ex );
//                }
//            }

//            while( olkModelObjectList.size()>0){
//                String id =olkModelObjectList.get( 0 );
//                logger.info( "上报模型引用对象:{}",id );
//                try{
//                    TOlkModelObjectDo info = olkModelObjectService.findById( id );
//                    if( info == null ){ //数据已删除
//                        olkModelObjectList.remove( id );
//                    }
//                    else {
//                        if( StringUtils.isBlank( token ) ){
//                            token = genToken();
//                        }
//                        if( nodePartyDo == null ){
//                            nodePartyDo = nodePartyService.findFirst();
//                        }
//                        if( synOlkModelObject(info,token,nodePartyDo)) {
//                        }
//                    }
//                }
//                catch ( Exception ex ){
//                    logger.error( "上报模型引用对象{}错误,",id,ex );
//                }
//            }
        }
        catch (Exception ex){
            logger.error( "" ,ex);
            System.out.println("it is error");
        }
        logger.debug( "上报数据完成" );
    }

    private boolean synSource(FDatasourceDo info, String token,FNodePartyDo nodePartyDo){
        info.setNodePartyId( nodePartyDo.getId() );
        ObjectResp<String> retMap = apiTruModelService.synDbsource( info, token );
        if ( retMap.isSuccess() ) {
            FDatasourceDo tmp = new FDatasourceDo();
            tmp.setId( info.getId() );
            tmp.setSynFlag( 1 );
            tmp.setNodePartyId( info.getNodePartyId() );
            //tmp.setShareTime( ComUtil.getCurTimestamp() );
            dataSourceService.updateNoNull( tmp );
            dbSourceList.remove( info.getId() );
            return true;
        }
        else{
            String msg = String.format( "上报数据源(%s)失败,%s",info.getId(),retMap.getMsg());
            logger.error( msg );
            return false;
        }
    }

    private boolean synDb(TBydbDatabaseDo info, String token,FNodePartyDo nodePartyDo){
        info.setNodePartyId( nodePartyDo.getId() );
        ObjectResp<String> retMap = apiTruModelService.synDatabase( info, token );
        if ( retMap.isSuccess() ) {
            TBydbDatabaseDo tmp = new TBydbDatabaseDo();
            tmp.setId( info.getId() );
            tmp.setSynFlag( 1 );
            tmp.setNodePartyId( info.getNodePartyId() );
            //tmp.setShareTime( ComUtil.getCurTimestamp() );
            databaseService.updateNoNull( tmp );
            dbSourceList.remove( info.getId() );
            return true;
        }
        else{
            String msg = String.format( "上报目录(%s)失败,%s",info.getId(),retMap.getMsg());
            logger.error( msg );
            return false;
        }
    }
    private boolean synSchema(TBydbSchemaDo info,String token,FNodePartyDo nodePartyDo){
        info.setNodePartyId( nodePartyDo.getId() );
        ObjectResp<String> retMap = apiTruModelService.synSchema( info, token );
        if ( retMap.isSuccess() ) {
            TBydbSchemaDo tmp = new TBydbSchemaDo();
            tmp.setId( info.getId() );
            tmp.setSynFlag( 1 );
            tmp.setNodePartyId( info.getNodePartyId() );
            //tmp.setShareTime( ComUtil.getCurTimestamp() );
            schemaService.updateNoNull( tmp );
            dbSourceList.remove( info.getId() );
            return true;
        }
        else{
            String msg = String.format( "上报库(%s)失败,%s",info.getId(),retMap.getMsg());
            logger.error( msg );
            return false;
        }
    }
    private boolean synTable( BydbObjectFieldsVo data,String token,FNodePartyDo nodePartyDo ){
        data.setNodePartyId( nodePartyDo.getId() );
        List<BydbObjectFieldsVo> list = new ArrayList<>();
        list.add(  data );
        ObjectResp<String> retMap = apiTruModelService.synTable( list, token );
        if ( retMap.isSuccess() ) {
            TBydbObjectDo tmp = new TBydbObjectDo();
            tmp.setId( data.getId() );
            tmp.setSynFlag( 1 );
            tmp.setNodePartyId( data.getNodePartyId() );
            tmp.setShareTime( ComUtil.getCurTimestamp() );
            bydbObjectService.updateNoNull( tmp );
            dbSourceList.remove( data.getId() );
            return true;
        }
        else{
            String msg = String.format( "上报库(%s)失败,%s",data.getId(),retMap.getMsg());
            logger.error( msg );
            return false;
        }
    }

    private boolean synTruModel(TTruModelDo info, String token,FNodePartyDo nodePartyDo){
        info.setNodePartyId( nodePartyDo.getId() );
        ObjectResp<String> retVal = apiTruModelService.synTruModel( info, token );
        if ( retVal.isSuccess() ) {
            TTruModelDo tmp = new TTruModelDo();
            tmp.setId( info.getId() );
            tmp.setSynFlag( 1 );
            tmp.setNodePartyId( info.getNodePartyId() );
            //tmp.setShareTime( ComUtil.getCurTimestamp() );
            truModelService.updateNoNull( tmp );
            truModelList.remove( info.getId() );
            return true;
        }
        else{
            String msg = String.format( "上报模型引用对象(%s)失败,错误信息：%s",info.getId(),retVal.getMsg() );
            logger.error( msg );
            return false;
        }
    }

    private boolean synTruModelObject(TTruModelObjectDo info, String token,FNodePartyDo nodePartyDo){
        info.setNodePartyId( nodePartyDo.getId() );
        List<TTruModelObjectDo> list = new ArrayList<>();
        list.add(  info );
        ObjectResp<TTruModelObjectDo> retMap = apiTruModelService.synTruModelObject( info, token );
        if ( retMap.isSuccess() ) {
            TTruModelObjectDo tmp = new TTruModelObjectDo();
            tmp.setId( info.getId() );
            tmp.setSynFlag( 1 );
            tmp.setNodePartyId( info.getNodePartyId() );
            //tmp.setShareTime( ComUtil.getCurTimestamp() );
            truModelObjectService.updateNoNull( tmp );
            truModelObjectList.remove( info.getId() );
            return true;
        }
        else{
            String msg = String.format( "上报模型引用对象(%s)失败",info.getId());
//            if ( retMap.containsKey( "msg" ) ) {
//                msg = String.format( "%s,错误信息：%s",msg,retMap.get( "msg" ) );
//            }
            logger.error( msg );
            return false;
        }
    }

    /*private boolean synOlkModel(TOlkModelDo info, String token,FNodePartyDo nodePartyDo){
        info.setNodePartyId( nodePartyDo.getId() );
        ObjectResp<String> retVal = apiTruModelService.synOlkModel( info, token );
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
        ObjectResp<TOlkModelObjectDo> retMap = apiTruModelService.synOlkModelObject( info, token );
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
    }*/

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
