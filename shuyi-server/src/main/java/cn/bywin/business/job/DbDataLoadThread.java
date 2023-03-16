package cn.bywin.business.job;

import cn.bywin.business.bean.bydb.TBydbDatabaseDo;
import cn.bywin.business.bean.bydb.TBydbFieldDo;
import cn.bywin.business.bean.bydb.TBydbObjectDo;
import cn.bywin.business.bean.bydb.TBydbSchemaDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.service.bydb.BydbFieldService;
import cn.bywin.business.service.bydb.BydbObjectService;
import cn.bywin.business.service.bydb.BydbSchemaService;
import cn.bywin.business.trumodel.ApiTruModelService;
import cn.bywin.cache.SysParamSetOp;
import cn.bywin.common.resp.ObjectResp;
import cn.jdbc.IJdbcOp;
import cn.jdbc.JdbcColumnInfo;
import cn.jdbc.JdbcOpBuilder;
import cn.jdbc.JdbcTableInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class DbDataLoadThread extends Thread {

    protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

    private BydbSchemaService schemaService;
    private BydbObjectService objectService;
    private BydbFieldService fieldService;
    private RedisTemplate<String, Object> redisTemplate;
    private ApiTruModelService apiTruModelService;
    TBydbDatabaseDo dbInfo;
    //TBydbDcServerDo dcDo;
    FDatasourceDo dbSourceDo;
    TBydbSchemaDo schemaInfo;
    TBydbObjectDo objectInfo;
    FNodePartyDo nodePartyDo;
    String redKey;
    UserDo user;
    String ips;
    private String bydbRef = "bydb_ref_";

    public DbDataLoadThread( BydbSchemaService schemaService, BydbObjectService objectService, BydbFieldService fieldService, ApiTruModelService apiTruModelServer, RedisTemplate<String, Object> redisTemplate,
                             TBydbDatabaseDo dbInfo, FDatasourceDo dbSourceDo, TBydbSchemaDo schemaInfo, TBydbObjectDo objectInfo, FNodePartyDo nodePartyDo, UserDo user, String ips ) {
        this.schemaService = schemaService;
        this.objectService = objectService;
        this.fieldService = fieldService;
        this.apiTruModelService = apiTruModelServer;
        this.redisTemplate = redisTemplate;
        this.dbInfo = dbInfo;
        //this.dcDo = dcDo;
        this.dbSourceDo = dbSourceDo;
        this.schemaInfo = schemaInfo;
        this.objectInfo = objectInfo;
        this.nodePartyDo = nodePartyDo;
        this.user = user;
        this.ips = ips;
    }

    @Override
    public void run() {
        String redKey = bydbRef + dbInfo.getId();
        loadNotOlkCatalogMeta( dbInfo, dbSourceDo, schemaInfo, objectInfo, redKey, user );
    }

    private void loadNotOlkCatalogMeta( TBydbDatabaseDo dbInfo, FDatasourceDo dbSourceDo, TBydbSchemaDo schemaInfo, TBydbObjectDo objectInfo, String redKey, UserDo user ) {

        List<TBydbObjectDo> allObjectList = new ArrayList<>();
        List<String> schemaNameList = new ArrayList<>();

        List<TBydbSchemaDo> allSchemaList = new ArrayList<>();

        BoundValueOperations<String, Object> bvo = redisTemplate.boundValueOps( redKey );
        HashMap<String, String> schemaMap = new HashMap<>();

        JdbcOpBuilder jb = new JdbcOpBuilder();

        try ( IJdbcOp jdbcOp = jb.withCatalog( dbSourceDo.getDsDatabase() ).withSchema( dbSourceDo.getDsSchema() ).withDbType( dbSourceDo.getDsType() )
                .withDriver( dbSourceDo.getDsDriver() ).withUrl( dbSourceDo.getJdbcUrl() )
                .withPassword( dbSourceDo.getPassword() ).withUser( dbSourceDo.getUsername() )
                .build() ) {

            //某张表
            if ( objectInfo != null ) {
                TBydbSchemaDo schemaDo = schemaService.findById( objectInfo.getSchemaId() );
                schemaMap.put( schemaDo.getId(), schemaDo.getSchemaName() );
                allObjectList.add( objectInfo );
            }
            //整个shcema
            else if ( schemaInfo != null ) {
                allSchemaList.add( schemaInfo );
            }
            else {  //整个catalog
                JsonObject noDealSchema = JsonUtil.toJsonObject( SysParamSetOp.readValue( "BydbNoDealSchema", "{}" ) );
                TBydbSchemaDo schTmp = new TBydbSchemaDo();
                schTmp.setDbId( dbInfo.getId() );
                List<TBydbSchemaDo> oldList = schemaService.find( schTmp );

                JsonElement jsonElement = noDealSchema.get( dbInfo.getDbType().toLowerCase() );

                logger.info( "{},sysTable:{}", jdbcOp.getDbType(), jdbcOp.getSysSchema() );
                logger.info( "dbType:{},{},set:{} , noDealSchema:{}", dbInfo.getDbType(), jsonElement, noDealSchema );

                List<String> noDealList = new ArrayList<>();
                //noDealList.add("information_schema");
                //noDealList.add("_statistics_");
                noDealList.addAll( jdbcOp.getSysSchema() );

                if ( jsonElement != null && !jsonElement.isJsonNull() && StringUtils.isNotBlank( jsonElement.getAsString() ) ) {
                    String[] split = jsonElement.getAsString().split( "\\\r|\\\n|," );
                    for ( String s : split ) {
                        if ( StringUtils.isNotBlank( s ) ) {
                            noDealList.add( s.toUpperCase() );
                        }
                    }
                }
                logger.info( "不处理模式:{},noDealList:{}", dbInfo.getDbType(), noDealList );
                bvo.expire( 3, TimeUnit.MINUTES );

                List<String> list1 = jdbcOp.listSchema( dbSourceDo.getDsDatabase(), dbSourceDo.getDsSchema() );
                logger.info( "schema:{}", list1 );
                bvo.expire( 3, TimeUnit.MINUTES );
                for ( String sn : list1 ) {
                    if ( noDealList.indexOf( sn.toUpperCase() ) < 0 && sn.indexOf( " " ) < 0 ) {
                        if ( "OPENLOOKENG".equalsIgnoreCase( dbSourceDo.getDsType() ) ) {
                            schemaNameList.add( String.format( "%s.%s", dbSourceDo.getDsDatabase(), sn ) );
                        }
                        else {
                            schemaNameList.add( sn );
                        }
                    }
                    else {
                        logger.info( "忽略schema:{}", sn );
                    }
                }
                List<TBydbSchemaDo> delList = new ArrayList<>();
                for ( TBydbSchemaDo sch : oldList ) {
                    if ( schemaNameList.indexOf( sch.getSchemaName() ) < 0 ) {
                        delList.add( sch );
                    }
                }
                if ( delList.size() > 0 ) {
                    List<String> schemaDelIdList = delList.stream().map( x -> x.getId() ).distinct().collect( Collectors.toList() );
                    ObjectResp<String> retVal = apiTruModelService.delSchema( schemaDelIdList, "1", user.getTokenId() );
                    if ( !retVal.isSuccess() ) {
                        logger.error( "删除pms库失败，{}", retVal.getMsg() );
                        return;
                    }
                    schemaService.deleteWhithRel( delList );
//                    for (TBydbSchemaDo delObj : delList) {
//                        new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), ips).delLog(user, delObj, "删除-刷新bydb模式与对象");
//                    }
                }
                int norder = 10;
                oldList = schemaService.find( schTmp );
                for ( String schemaName : schemaNameList ) {
                    boolean bfound = false;
                    for ( TBydbSchemaDo schemaDo : oldList ) {
                        if ( schemaDo.getSchemaName().equals( schemaName ) ) {
                            bfound = true;
                            schemaDo.setNorder( norder );
                            schemaService.updateBean( schemaDo );
                            allSchemaList.add( schemaDo );
                            break;
                        }
                    }
                    if ( !bfound ) {
                        TBydbSchemaDo schemaDo = new TBydbSchemaDo();
                        schemaDo.setNorder( norder );
                        schemaDo.setEnable( 1 );
                        schemaDo.setNodePartyId( dbInfo.getNodePartyId() );
                        schemaDo.setUserId( dbInfo.getUserId() );
                        schemaDo.setUserAccount( dbInfo.getUserAccount() );
                        schemaDo.setUserName( dbInfo.getUserName() );
                        schemaDo.setSchemaName( schemaName );
                        schemaDo.setScheFullName( schemaName );
                        schemaDo.setDbId( dbInfo.getId() );
                        schemaDo.setId( nodePartyDo.getPrefixSymbol() + "db" + ComUtil.genId() );
                        schemaDo.setSynFlag( 0 );
                        LoginUtil.setBeanInsertUserInfo( schemaDo, user );
                        schemaService.insertBean( schemaDo );
                        allSchemaList.add( schemaDo );
                        delList.add( schemaDo );
                        //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), ips).addLog(user, schemaDo, "新增-刷新bydb模式与对象");
                    }
                    norder += 10;
                }
            }

            for ( TBydbSchemaDo schemaDo : allSchemaList ) {
                if ( schemaDo.getEnable() == null || schemaDo.getEnable() != 1 ) {
                    continue;
                }
                schemaMap.put( schemaDo.getId(), schemaDo.getSchemaName() );
                //db 所有表
                TBydbObjectDo objTmp = new TBydbObjectDo();
                objTmp.setSchemaId( schemaDo.getId() );
                List<TBydbObjectDo> delObjectList = objectService.find( objTmp );
                int objorder = 10;
                bvo.expire( 3, TimeUnit.MINUTES );

                //String showTable = String.format("SHOW TABLES from %s.\"%s\"", dbInfo.getDcDbName(), schemaDo.getSchemaName());
                //logger.info(showTable);
                List<JdbcTableInfo> list2 = jdbcOp.listTableAndView( dbSourceDo.getDsDatabase(), schemaDo.getSchemaName() );
                bvo.expire( 3, TimeUnit.MINUTES );
                for ( JdbcTableInfo dat : list2 ) {
                    logger.debug( "处理表 {}.{}", schemaDo.getSchemaName(), dat.getTablename() );
                    boolean bobj = false;
                    for ( TBydbObjectDo objectDo : delObjectList ) {
                        if ( objectDo.getObjectName().equals( dat.getTablename() ) ) {
                            bobj = true;
                            objectDo.setNorder( objorder );
                            objectService.updateBean( objectDo );
                            delObjectList.remove( objectDo );
                            allObjectList.add( objectDo );
                            break;
                        }
                    }
                    if ( !bobj ) {
                        TBydbObjectDo objectDo = new TBydbObjectDo();
                        objectDo.setNodePartyId( dbInfo.getNodePartyId() );
                        objectDo.setUserId( dbInfo.getUserId() );
                        objectDo.setUserAccount( dbInfo.getUserAccount() );
                        objectDo.setUserName( dbInfo.getUserName() );
                        objectDo.setDbId( dbInfo.getId() );
                        objectDo.setSchemaId( schemaDo.getId() );
                        objectDo.setNorder( objorder );
                        objectDo.setObjectName( dat.getTablename() );
                        objectDo.setObjChnName( dat.getComment() );
                        objectDo.setObjFullName( schemaDo.getScheFullName() + "." + objectDo.getObjectName() );
                        objectDo.setStype( "table" );
                        objectDo.setEnable( 1 );
                        objectDo.setShareFlag( 0 );
                        objectDo.setSynFlag( 0 );

                        objectDo.setId( nodePartyDo.getPrefixSymbol() + "db" + ComUtil.genId() );

                        if ( StringUtils.length( objectDo.getObjChnName() ) > 200 ) {
                            logger.info( "表{},备注'{}'太长", objectDo.getObjFullName(), objectDo.getObjChnName() );
                            objectDo.setObjChnName( objectDo.getObjChnName().substring( 0, 200 ) );
                        }

                        LoginUtil.setBeanInsertUserInfo( objectDo, user );
                        objectService.insertBean( objectDo );
                        allObjectList.add( objectDo );
                        //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), ips).addLog(user, objectDo, "新增-刷新bydb模式与对象");
                    }
                    objorder += 10;
                }
                if ( delObjectList.size() > 0 ) {
                    List<String> objDelIdList = delObjectList.stream().map( x -> x.getId() ).distinct().collect( Collectors.toList() );
                    ObjectResp<String> retVal = apiTruModelService.delTable( objDelIdList, user.getTokenId() );
                    if ( retVal.isSuccess() ) {
                        logger.error( "删除pms表失败：{}", retVal.getMsg() );
                        return;
                    }
                    objectService.deleteWhithOthers( delObjectList );
//                    for (TBydbObjectDo objectDo : delObjectList) {
//                        new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), ips).delLog(user, objectDo, "删除-刷新bydb模式与对象");
//                    }
                }
            }
            for ( TBydbObjectDo objectDo : allObjectList ) {
                if ( objectDo.getEnable() == null || objectDo.getEnable() != 1 ) {
                    continue;
                }
                List<TBydbFieldDo> addFieldList = new ArrayList<>();
                List<TBydbFieldDo> modFieldList = new ArrayList<>();

                //db 所有字段
                TBydbFieldDo fieldTmp = new TBydbFieldDo();
                fieldTmp.setObjectId( objectDo.getId() );
                List<TBydbFieldDo> delFieldList = fieldService.find( fieldTmp );
                HashMap<String, TBydbFieldDo> modMap = new HashMap<>();

                String schemaName = schemaMap.get( objectDo.getSchemaId() );
                int fieldorder = 10;
                bvo.expire( 3, TimeUnit.MINUTES );
                List<JdbcColumnInfo> list3 = jdbcOp.listColumn( dbSourceDo.getDsDatabase(), schemaName, objectDo.getObjectName() );
                bvo.expire( 3, TimeUnit.MINUTES );
                for ( JdbcColumnInfo dat3 : list3 ) {
                    if ( "varchar(-1)".equalsIgnoreCase( dat3.getColumntype() ) ) {
                        dat3.setColumntype( "text" );
                    }
                    logger.debug( "处理字段 {}", dat3 );
                    TBydbFieldDo fieldDo = new TBydbFieldDo();
                    fieldDo.setColType( dat3.getColumntype() ); //
                    fieldDo.setFieldName( dat3.getColumnname() );
                    fieldDo.setFieldType( dat3.getDatatype() );
                    fieldDo.setFieldLength( dat3.getColen() );
                    fieldDo.setFieldPrecision( dat3.getColpercision() );
                    fieldDo.setChnName( dat3.getColumncomment() );
                    if ( StringUtils.length( fieldDo.getChnName() ) > 200 ) {
                        logger.info( "字段 {}.{}.{},备注:'{}'太长", dat3.getSchemaname(), dat3.getTablename(), dat3.getColumnname(), dat3.getColumncomment() );
                        fieldDo.setChnName( fieldDo.getChnName().substring( 0, 200 ) );
                    }

                    fieldDo.setNodePartyId( dbInfo.getNodePartyId() );
                    fieldDo.setUserId( dbInfo.getUserId() );
                    fieldDo.setUserAccount( dbInfo.getUserAccount() );
                    fieldDo.setUserName( dbInfo.getUserName() );
                    fieldDo.setDbId( dbInfo.getId() );
                    fieldDo.setSchemaId( objectDo.getSchemaId() );
                    fieldDo.setObjectId( objectDo.getId() );
                    fieldDo.setFieldFullName( objectDo.getObjFullName() + "." + fieldDo.getFieldName() );
                    fieldDo.setNorder( fieldorder );
                    fieldDo.setEnable( 1 );
                    fieldorder += 10;
                    fieldDo.setId( nodePartyDo.getPrefixSymbol() + "db" + ComUtil.genId() );
                    if ( "BLOB".equalsIgnoreCase( fieldDo.getFieldType() ) || "IMAGE".equalsIgnoreCase( fieldDo.getFieldType() )
                            || "BYTEA".equalsIgnoreCase( fieldDo.getFieldType() ) || fieldDo.getFieldType().indexOf( "BINARY") >= 0 ) {
                        fieldDo.setEnable( 0 );
                    }
                    if(fieldDo.getFieldType().indexOf( "INTERVAL" )>=0){
                        fieldDo.setEnable( 0 );
                    }
                    //LoginUtil.setBeanInsertUserInfo(fieldDo, user);
                    boolean bfield = false;
                    for ( TBydbFieldDo fd : delFieldList ) {
                        if ( fieldDo.getObjectId().equals( fd.getObjectId() ) && StringUtils.equalsIgnoreCase( fieldDo.getFieldName(), fd.getFieldName() ) ) {
                            bfield = true;
                            TBydbFieldDo old = new TBydbFieldDo();
                            MyBeanUtils.copyBeanNotNull2Bean( fd, old );
                            modMap.put( fd.getId(), old );
                            modFieldList.add( fd );
                            if ( StringUtils.isBlank( fd.getChnName() ) ) {
                                fd.setChnName( fieldDo.getChnName() );
                            }
                            fd.setFieldName( fieldDo.getFieldName() );
                            fd.setFieldFullName( fieldDo.getFieldFullName() );
                            fd.setFieldType( fieldDo.getFieldType() );
                            fd.setColType( fieldDo.getColType() );
                            fd.setFieldLength( fieldDo.getFieldLength() );
                            fd.setFieldPrecision( fieldDo.getFieldPrecision() );
                            fd.setNorder( fieldDo.getNorder() );
                            delFieldList.remove( fd );
                            break;
                        }
                    }
                    if ( !bfield ) {
                        addFieldList.add( fieldDo );
                    }
                }
                TBydbObjectDo objTmp = new TBydbObjectDo();
                objTmp.setId( objectDo.getId() );
                objTmp.setSynFlag( 0 );
                objectService.updateNoNull( objTmp );
                fieldService.saveOneObject( addFieldList, modFieldList, delFieldList );

//                for (TBydbFieldDo fieldDo : addFieldList) {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), ips).addLog(user, fieldDo, "新增-刷新bydb模式与对象");
//                }
//                for (TBydbFieldDo fieldDo : modFieldList) {
//                    TBydbFieldDo old = modMap.get(fieldDo.getId());
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), ips).updateLog(user, old, fieldDo, "修改-刷新bydb模式与对象");
//                }
//                for (TBydbFieldDo fieldDo : addFieldList) {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), ips).delLog(user, fieldDo, "删除-刷新bydb模式与对象");
//                }
            }
        }
        catch ( Exception e ) {
            logger.error( "刷新" + dbInfo.getDcDbName() + "元数据数据失败", e );
        }
        finally {
            BydbDataNodeJob.reInit();
            redisTemplate.delete( redKey );
            logger.info( "刷新" + dbInfo.getDcDbName() + "元数据数据结束" );
        }

    }

}
