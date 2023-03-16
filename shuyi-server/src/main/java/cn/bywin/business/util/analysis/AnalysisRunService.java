package cn.bywin.business.util.analysis;

import static cn.bywin.business.bean.analysis.TruComponentEnum.DataSourceOutPut_COMPONENT;

import cn.bywin.business.bean.analysis.TruBaseComponenT;
import cn.bywin.business.bean.analysis.TruCheckComponent;
import cn.bywin.business.bean.analysis.TruComponentEnum;
import cn.bywin.business.bean.analysis.graph.TruDirectedGraph;
import cn.bywin.business.bean.analysis.graph.TruVertex;
import cn.bywin.business.bean.analysis.template.TruTableComponent;
import cn.bywin.business.bean.bydb.TTruModelComponentDo;
import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.bydb.TTruModelElementDo;
import cn.bywin.business.bean.bydb.TTruModelElementJobDo;
import cn.bywin.business.bean.bydb.TTruModelElementRelDo;
import cn.bywin.business.bean.bydb.TTruModelFieldDo;
import cn.bywin.business.bean.bydb.TTruModelObjectDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.view.bydb.SchemaVo;
import cn.bywin.business.bean.view.bydb.TruNode;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.service.bydb.BydbDatabaseService;
import cn.bywin.business.service.bydb.BydbModelElementJobService;
import cn.bywin.business.service.bydb.BydbObjectService;
import cn.bywin.business.service.bydb.TruModelComponentService;
import cn.bywin.business.service.bydb.TruModelElementRelService;
import cn.bywin.business.service.bydb.TruModelElementService;
import cn.bywin.business.service.bydb.TruModelFieldService;
import cn.bywin.business.service.bydb.TruModelObjectService;
import cn.bywin.business.service.bydb.TruModelService;
import cn.bywin.business.service.federal.DataSourceService;
import cn.bywin.business.util.DbTypeToFlinkType;
import cn.bywin.config.TruModelOutDbSet;
import cn.jdbc.JdbcOpBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AnalysisRunService {
    protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Autowired
    private TruModelObjectService truModelObjectService;
    @Autowired
    private TruModelComponentService truModelComponentService;
    @Autowired
    private TruModelFieldService truModelFieldService;
    //    @Autowired
//    private DataHubJdbcOperateConfigurate hutuConfig;
    @Autowired
    private BydbObjectService bydbObjectService;
    @Autowired
    private TruModelService truModelService;
    @Autowired
    private TruModelElementService truModelElementService;
    @Autowired
    private TruModelElementRelService truModelElementRelService;
    @Autowired
    private BydbDatabaseService bydbDatabaseService;
    @Autowired
    private BydbModelElementJobService bydbModelElementJobService;
    //    @Autowired
//    private HetuJdbcOperate hetuJdbcOperate;
//    @Autowired
//    private HetuJdbcOperateComponent hetuJdbcOperateComponent;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

//    @Autowired
//    @Qualifier("dataHubScheduleProperties")
//    private Properties dataHubProperties;
//
//    @Autowired
//    private SystemParamHolder systemParamHolder;

    @Autowired
    private DataSourceService dbSourceService;

    @Autowired
    private TruModelOutDbSet outSet;


    public TruCheckComponent runModel( TTruModelDo modelDo, TTruModelElementJobDo elementJobDo ) {
        List<TTruModelElementDo> elements = truModelElementService.selectByModelId( modelDo.getId() );
        List<TTruModelElementRelDo> elementRels = truModelElementRelService.selectByModelId( modelDo.getId() );
        TruDirectedGraph<String> directedGraph = new TruDirectedGraph();
        TruCheckComponent component = new TruCheckComponent( true, "成功" );
        for ( TTruModelElementDo elementDo : elements ) {
            directedGraph.addVertex( elementDo.getId() );
            if ( elementDo.getRunStatus() != 1 ) {
                component.setSuccess( false );
                component.setMessage( String.format( "%s组件未保存,请配置", elementDo.getName() ) );
                return component;
            }
        }

        elements.forEach( element -> {
            directedGraph.addVertex( element.getId() );
        } );
        elementRels.forEach( element -> {
            directedGraph.addEdge( element.getStartElementId(), element.getEndElementId() );
        } );
        if ( directedGraph.hasIsolatedVertices() && elementRels.size() > 1 ) {
            component.setSuccess( false );
            component.setMessage( "存在孤立节点,请重新配置" );
            return component;
        }
        if ( directedGraph.hasRecycle() ) {
            component.setSuccess( false );
            component.setMessage( "节点存在循环嵌套,请重新配置" );
            return component;
        }

        List<TruVertex<String>> schedulePath = directedGraph.buildTaskSchedulePath();
        TTruModelElementDo info = truModelElementService.findById( schedulePath.get( schedulePath.size() - 1 ).getId() );
        String sql = info.getRunSql().concat( " LIMIT " ).concat( info.getTotal().toString() );
        elementJobDo.setJobStatus( 3 );
        elementJobDo.setJobType( modelDo.getTypes() );
        elementJobDo.setName( modelDo.getName() );
        elementJobDo.setConfig( modelDo.getConfig() );
        bydbModelElementJobService.insertBean( elementJobDo );
        return component;
    }


    /**
     * 联邦分析数据源输出
     */
    /*public boolean analyseToTable(List<TTruModelFieldDo> fieldDos, EDatasourceDo datasourceDo, TTruModelElementJobDo jobDo,
                                  TTruModelElementDo tTruModelElementDo, String table) throws Exception {
        StringBuilder sb = new StringBuilder();
        String bydbTemplate = SysParamSetOp.readValue(datasourceDo.getDsDriver().replaceAll("\\.", "_"), "");
        String bydbCacheTableTemplate = bydbTemplate.split("--")[1];
        String bydbDropTableTemplate = bydbTemplate.split("--")[0];
        String tableName = StringUtils.isNotBlank(table) ? table : tTruModelElementDo.getElement().concat("_").concat(tTruModelElementDo.getId());

        HashMap<String, Object> fromMap = new HashMap<>();
        HashMap<String, Object> toMap = new HashMap<>();
        List<Object> fieldList = new ArrayList<>();
        String sourceDriver = datasourceDo.getDsDriver();
        String sourceUrl = datasourceDo.getJdbcUrl();
        String sourceUser = datasourceDo.getUsername();
        String sourcePasswd = datasourceDo.getPassword();
        bydbCacheTableTemplate = bydbCacheTableTemplate.replaceAll("@tableName@", tableName
        );
        for (int norder = 0; norder < fieldDos.size(); norder++) {
            TTruModelFieldDo columnDo = fieldDos.get(norder);
            if (norder > 0) {
                sb.append(",\r\n");
            }
            String fieldName = columnDo.getFieldAlias();
            String fieldType = columnDo.getFieldType();
            String fieldExpr = columnDo.getFieldExpr();
            String columnType = columnDo.getColumnType();
            sb.append(fieldName);
            HashMap<String, String> colData = new LinkedHashMap<>();
            colData.put("name", fieldName);
            String colType = JdbcTypeToJavaTypeUtil.chgDdbType(fieldType);
            if ("CLICKHOUSE".equals(datasourceDo.getDsType())) {
                String type=JdbcTypeToJavaTypeUtil.chgTypeCh(fieldType).toLowerCase();
                sb.append("  Nullable(").append(JdbcTypeToJavaTypeUtil.chgTypeCh(fieldType)).append(") ");
                colData.put("sourceType", type);
                colData.put("type", type);
            } else {
                sb.append(" ").append(colType.toLowerCase()).append(" ");
                colData.put("sourceType", colType.toLowerCase());
                int idx = colType.indexOf("(");
                if (idx > 0) {
                    colData.put("type", colType.substring(0, idx).toLowerCase());
                } else {
                    colData.put("type", colType.toLowerCase());
                }
            }

            colData.put("comment", fieldExpr);
            fieldList.add(colData);
            sb.append("COMMENT ").append("\\\'").append(fieldExpr.replaceAll("\\'|\\\"", "")).append("\\\'");
        }
        if ("HIVE".equals(datasourceDo.getDsType())) {
            bydbCacheTableTemplate = bydbCacheTableTemplate.replaceAll("@hdfs@", datasourceDo.getTopic().concat(tableName));
        }
        bydbCacheTableTemplate = bydbCacheTableTemplate.replaceAll("@columns@", sb.toString());
        logger.info(bydbCacheTableTemplate);
        try (CommonJdbcOp dbop = new CommonJdbcOp(datasourceDo.getDsType(),
                sourceDriver, sourceUrl, sourceUser, sourcePasswd)) {
            bydbDropTableTemplate = bydbDropTableTemplate.replaceAll("@tableName@", tableName);
            dbop.execute(bydbDropTableTemplate);
            dbop.execute(bydbCacheTableTemplate);
        }
        toMap.put("table", tableName);
        toMap.put("driverType", datasourceDo.getDsType());
        toMap.put("driver", sourceDriver);
        toMap.put("jdbcUrl", sourceUrl);
        toMap.put("user", sourceUser);
        toMap.put("password", sourcePasswd);

        fromMap.put("sql", tTruModelElementDo.getRunSql());
        fromMap.put("driver", hutuConfig.getDriver());
        fromMap.put("jdbcUrl", hutuConfig.getUrl());
        DataPermissionRequest request = new DataPermissionRequest();
        request.setTaskType(TaskType.QUERY);
        request.setTaskId(jobDo.getId());
        request.setUserName(jobDo.getCreatorAccount());
        hetuJdbcOperateComponent.fillHetuConfig(dataHubProperties, fromMap, request);

        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("jobId", jobDo.getId());
        dataMap.put("userName", jobDo.getCreatorAccount());
        dataMap.put("userChnName", jobDo.getCreatorName());
        dataMap.put("jobStatus", jobDo.getJobStatus());
        dataMap.put("jobName", String.format("%s", jobDo.getName().replaceAll("\\s+|\\'|\\\"", "")));
        dataMap.put("fromDb", fromMap);
        dataMap.put("toDb", toMap);
        dataMap.put("columns", fieldList);
        String setInfo1 = JsonUtil.toJson(dataMap);
        String setInfo2 = Base64.getEncoder().encodeToString(setInfo1.getBytes());
        logger.info("{}\r\n{}", setInfo1, setInfo2);
        HttpOperaterUtil hou = new HttpOperaterUtil();
        HashMap<String, String> paraMap = new HashMap<>();
        paraMap.put("setInfo", setInfo2);
        String bydbTransformServerUrl = systemParamHolder.getBydbAnalysisTransformServerUrl();
        logger.info("bydb联邦分析数据源处理服务地址:{}", bydbTransformServerUrl);
        String ret = hou.setDataMap(paraMap).setAppJson(false).setStrUrl(bydbTransformServerUrl).sendPostRequest();
        logger.debug(ret);
        JsonObject jsonObject = JsonUtil.toJsonObject(ret);
        if (0 == jsonObject.get("code").getAsInt()) {
            logger.info(ret);
            return true;
        }
        return false;
    }*/
    public TruCheckComponent checkField( TruNode truNode, TTruModelElementDo elementDo ) {
        TruCheckComponent component = new TruCheckComponent( true, "成功" );
        try {
            List<String> viewNodes = truNode.getViewIds();
            List<TTruModelFieldDo> elementFieldDos = new ArrayList<>();
            if ( truNode.getOperators().getFilters() != null && truNode.getOperators().getFilters().size() > 0 ) {
                truNode.getOperators().getFilters().stream().forEach( e -> {
                    if ( viewNodes.contains( e.getId() ) ) {
                        e.setIsSelect( 1 );
                    }
                    else {
                        e.setIsSelect( 0 );
                    }
                    elementFieldDos.add( e );
                } );

            }
            else {
                truNode.getOperators().getFields().stream().forEach( e -> {
                    if ( viewNodes.contains( e.getId() ) ) {
                        e.setIsSelect( 1 );
                    }
                    else {
                        e.setIsSelect( 0 );
                    }
                    elementFieldDos.add( e );
                } );
            }


            List<TTruModelElementDo> elements = truModelElementService.selectByModelId( elementDo.getModelId() );
            List<TTruModelElementRelDo> elementRels = truModelElementRelService.selectByModelId( elementDo.getModelId() );
            List<TTruModelComponentDo> componentDos = truModelComponentService.findAll();
            Map<String, TTruModelComponentDo> idComponentMap = componentDos.stream().collect( Collectors.toMap( TTruModelComponentDo::getId, e -> e ) );

            for ( TTruModelElementRelDo elementRel : elementRels ) {
                if(elementDo.getId().equals( elementRel.getEndElementId() )){
                    for ( TTruModelElementDo element : elements ) {
                        if( element.getId().equals(  elementRel.getStartElementId() )){
                            if( element.getRunStatus() ==0 ){
                                return new TruCheckComponent( false, "请先配置上级组" );
                            }
                        }
                    }
                }
            }
            TruDirectedGraph<String> directedGraph = new TruDirectedGraph();
            elements.forEach( element -> {
                directedGraph.addVertex( element.getId() );
            } );
            elementRels.forEach( element -> {
                directedGraph.addEdge( element.getStartElementId(), element.getEndElementId() );
            } );

            if ( directedGraph.hasIsolatedVertices() && elementRels.size() > 1 ) {
                return new TruCheckComponent( true, "成功" );
            }
            if ( directedGraph.hasRecycle() ) {
                return new TruCheckComponent( false, "组件存在循环依赖,请重新配置" );
            }
            Map<String, TTruModelElementDo> idElementMap = elements.stream().collect( Collectors.toMap( TTruModelElementDo::getId, e -> e ) );
            List<TruVertex<String>> schedulePath = directedGraph.buildTaskSchedulePath();
            int indexs = -1;
            for ( int i = 0; i < schedulePath.size(); i++ ) {
                if ( schedulePath.get( i ).getId().equals( elementDo.getId() ) ) {
                    indexs = i;
                }
            }
            if ( indexs == -1 ) {
                return new TruCheckComponent( false, "节点不存在" );
            }

            /*List<TTruModelFieldDo> delFieldList = bydbModelFieldService.selectByElementIdAll( elementDo.getId() );
            List<TTruModelFieldDo> addFieldList = new ArrayList<>();
            List<TTruModelFieldDo> modFieldList = new ArrayList<>();
            boolean bchg = false;
            if ( elementFieldDos.size() != delFieldList.size() ) {
                bchg = true;
            }

            Map<String, TTruModelFieldDo> fieldMap = delFieldList.stream().collect( Collectors.toMap( x -> x.getFieldName(), x -> x ) );

            int norder = 1;
            for ( TTruModelFieldDo f2 : elementFieldDos ) {
                boolean bfound = false;
                TTruModelFieldDo f1 = fieldMap.get( f2.getFieldName() );
                if ( f1 != null ) {
                    modFieldList.add( f1 );
                    if ( !f1.getFieldAlias().equals( f2.getFieldAlias() ) || f1.getIsSelect() != f2.getIsSelect() ) {
                        bchg = true;
                    }
                    f1.setFilterSort( norder++ );
                    f1.setFieldAlias( f2.getFieldAlias() );
                    f1.setIsSelect( f2.getIsSelect() );
                    f1.setAggregation( f2.getAggregation() );
                    f1.setFieldExpr( f2.getFieldExpr() );
                    f1.setModifiedTime( ComUtil.getCurTimestamp() );
                    delFieldList.remove( f1 );
                }
                else {
                    f2.setFilterSort( norder++ );
                    f2.setModifiedTime( ComUtil.getCurTimestamp() );
                    addFieldList.add( f2 );
                    bchg = true;
                }
            }
            bydbModelFieldService.saveFields( addFieldList, modFieldList, delFieldList );

            if ( bchg ) {
            for ( int i = indexs; i < schedulePath.size(); i++ ) {
                TTruModelElementDo elementInfo = idElementMap.get( schedulePath.get( i ).getId() );
                    for ( TruEdge edge : schedulePath.get( i ).getEdges() ) {
                        TTruModelElementDo elem = bydbModelElementService.findById( edge.getEndVertex().getId().toString() );
                        TTruModelElementDo etmp = new TTruModelElementDo();
                        etmp.setId( elem.getId() );
                        etmp.setRunStatus( 0 );
                        etmp.setModifiedTime( ComUtil.getCurTimestamp() );
                        bydbModelElementService.updateNoNull( etmp );
                    }
                }
            }*/

            /*for ( int i = indexs; i < schedulePath.size(); i++ ) {
                TTruModelElementDo elementInfo = idElementMap.get( schedulePath.get( i ).getId() );
                TTruModelComponentDo components = idComponentMap.get( elementInfo.getTcId() );
                List<TTruModelFieldDo> fields = new ArrayList<>();
                if ( elementInfo.getId().equals( elementDo.getId() ) ) {
                    fields = elementFieldDos;
                }
                else {
                    fields = bydbModelFieldService.selectByElementId( schedulePath.get( i ).getId() );
                }

                for ( TruEdge edge : schedulePath.get( i ).getEdges() ) {
                    List<TTruModelFieldDo> fieldDos = bydbModelFieldService.selectByElementIdAll( edge.getEndVertex().getId().toString() );
                    List<TTruModelFieldDo> updateFields = new ArrayList<>();
                    fields.stream().forEach( p -> {
                        fieldDos.stream().forEach( e -> {
                            TTruModelElementDo elementDo1 = idElementMap.get( e.getElementId() );
                            TTruModelComponentDo componentDo = idComponentMap.get( elementDo1.getTcId() );
                            if ( e.getFieldName().equals( p.getFieldAlias() )
                                    && (e.getExtendsId().equals( p.getElementId() ) ||
                                    e.getExtendsId().equals( e.getElementId() )
                                    || (StringUtils.isNotBlank( p.getExtendsId() ) &&
                                    p.getExtendsId().equals( p.getElementId() ))) ) {
                                if ( p.getIsSelect() == 1 ) {
                                    if ( e.getIsSelect() == 1 ) {
                                        e.setIsSelect( 1 );
                                    }
                                    else if ( e.getIsSelect() == 0 ) {
                                        e.setIsSelect( 0 );
                                    }
                                    else {
                                        e.setIsSelect( 0 );
                                    }
                                    updateFields.add( e );
                                }
                                else {

                                    if ( GROUP_COMPONENT.getComponentName().equals( componentDo.getComponentEn() ) ) {
                                        component.setSuccess( false );
                                        component.setMessage( String.format( "修改失败,和%s组件冲突",
                                                elementDo1.getName() ) );
                                        return;
                                    }
                                    else if ( FieldFunc_COMPONENT.getComponentName().equals( componentDo.getComponentEn() ) ) {
                                        component.setSuccess( false );
                                        component.setMessage( String.format( "修改失败,和%s组件冲突",
                                                elementDo1.getName() ) );
                                        return;
                                    }
                                    else if ( FieldConcat_COMPONENT.getComponentName().equals( componentDo.getComponentEn() ) ) {
                                        component.setSuccess( false );
                                        component.setMessage( String.format( "修改失败,和%s组件冲突",
                                                elementDo1.getName() ) );
                                        return;
                                    }
                                    else if ( Collection_COMPONENT.getComponentName().equals( componentDo.getComponentEn() ) ) {
                                        component.setSuccess( false );
                                        component.setMessage( String.format( "修改失败,和%s组件冲突",
                                                elementDo1.getName() ) );
                                        return;
                                    }
                                    else if ( Intersect_COMPONENT.getComponentName().equals( componentDo.getComponentEn() ) ) {
                                        component.setSuccess( false );
                                        component.setMessage( String.format( "修改失败,和%s组件冲突",
                                                elementDo1.getName() ) );
                                        return;
                                    }
                                    else {
                                        e.setIsSelect( -1 );
                                    }
                                    updateFields.add( e );
                                }
                            }
                        } );
                    } );
                    bydbModelFieldService.batchUpdateByPrimaryKey( updateFields );
                }
            }*/
        }
        catch ( Exception e ) {
            logger.error( "校验保存配置失败，", e );
            return component;
        }
        return component;
    }

    @Async
    public void asyncModel( String id, Map<String, FDatasourceDo> tabSourceMap ) throws Exception {
        TTruModelDo modelDo = truModelService.findById( id );
        modelDo.setRunSql( "" );
        truModelService.updateBean( modelDo );
        List<TTruModelElementDo> elements = truModelElementService.selectByModelId( id );
        List<TTruModelElementRelDo> elementRels = truModelElementRelService.selectByModelId( id );
        List<TTruModelComponentDo> componentDos = truModelComponentService.findAll();
        List<TTruModelObjectDo> datasource = truModelObjectService.findAll();
        TruDirectedGraph<String> directedGraph = new TruDirectedGraph();
        StringBuffer createSb = new StringBuffer();
        elements.forEach( element -> {
            directedGraph.addVertex( element.getId() );
        } );
        elementRels.forEach( element -> {
            directedGraph.addEdge( element.getStartElementId(), element.getEndElementId() );
        } );
        if ( directedGraph.hasIsolatedVertices() && elementRels.size() > 1 ) {
            throw new IllegalArgumentException( "存在孤立节点,请重新配置" );
        }
        if ( directedGraph.hasRecycle() ) {
            throw new IllegalArgumentException( "组件存在循环依赖,请重新配置" );
        }

        Map<String, TTruModelElementDo> idElementMap = elements.stream().collect( Collectors.toMap( TTruModelElementDo::getId, e -> e ) );
        Map<String, TTruModelComponentDo> idComponentMap = componentDos.stream().collect( Collectors.toMap( TTruModelComponentDo::getId, e -> e ) );
        List<TruVertex<String>> schedulePath = directedGraph.buildTaskSchedulePath();
        int runOrder = 1;
        String outPutId = null;
        boolean bok = true;
        for ( TruVertex<String> element : schedulePath ) {
            TTruModelElementDo elementInfo = idElementMap.get( element.getId() );
            List<TTruModelElementDo> models = truModelElementService.selectByModelId( elementInfo.getModelId() );
            if ( elementInfo.getElementType() == 0 ) {
                TTruModelComponentDo tTruModelComponentDo = idComponentMap.get( elementInfo.getTcId() );
                if ( tTruModelComponentDo == null ) {
                    throw new IllegalArgumentException( "组件不存在" );
                }
                TruBaseComponenT baseComponenT = TruComponentEnum.getInstanceByName( tTruModelComponentDo.getComponentEn().toLowerCase() );
                List<TTruModelElementDo> pre = truModelElementService.selectStartId( elementInfo.getId() );
                List<TTruModelElementDo> next = truModelElementService.selectEndId( elementInfo.getId() );
                logger.info( "组件:{},{}", elementInfo.getElement(), elementInfo.getConfig() );
                baseComponenT.setComponents( componentDos );
                baseComponenT.setModel( models );
                baseComponenT.setDatasource( datasource );
                baseComponenT.setPreModel( pre );
                baseComponenT.setNextModel( next );
                List<TTruModelFieldDo> extenFieldList = new ArrayList<>();
                for ( TTruModelElementDo eleTmp : pre ) {
                    extenFieldList.addAll( truModelFieldService.selectByElementIdAll( eleTmp.getId() ) );
                }
                baseComponenT.setExtendsDos( extenFieldList );
                TruNode truNode = truModelElementService.getNodes( elementInfo );
                TruNode init = baseComponenT.init( truNode, elementInfo );
                if ( baseComponenT.check( init, elementInfo ).isSuccess() ) {

                    baseComponenT.build( init, elementInfo, modelDo );
                    truModelElementService.updateBeanDetail( elementInfo, init );
                }
                else {
                    logger.error( "{}>组件更新失败,{}", elementInfo.getName(), baseComponenT.check( init, elementInfo ).getMessage() );
                    return;
                }
//                if( TruComponentEnum.DataSourceOutPut_COMPONENT.getComponentName().equals( tTruModelComponentDo.getComponentEn().toLowerCase() )){
//                    outPutId = elementInfo.getId();
//                }
            }
            else if ( elementInfo.getElementType() == 1 ) { //起始表

                FDatasourceDo datasourceDo = tabSourceMap.get( elementInfo.getTcId() );
                TruBaseComponenT baseComponenT = new TruTableComponent();
                TruNode truNode = truModelElementService.getNodes( elementInfo );
                truNode.setDbSource( datasourceDo );
                TruNode init = baseComponenT.init( truNode, elementInfo );
                baseComponenT.setModel( models );
                baseComponenT.build( init, elementInfo, modelDo );
                truModelElementService.updateBeanDetail( elementInfo, init );
                //String runsql1 = elementInfo.getRunSql();
                //Pattern pattern = Pattern.compile( "CREATE TABLE(.|\\s)*?WITH(.|\\s)*?\\);\\s+", Pattern.CASE_INSENSITIVE);
                //Matcher matcher = pattern.matcher( runsql1 );
                //while( matcher.find()){
                //}
            }
            if ( StringUtils.isNotBlank( elementInfo.getTableSql() ) ) {
                createSb.append( elementInfo.getTableSql() );
            }
            TTruModelElementDo eleTmp =  new TTruModelElementDo();
            eleTmp.setId(  elementInfo.getId() );
            eleTmp.setRunOrder( runOrder++ );
            truModelElementService.updateNoNull( eleTmp );

            if( elementInfo.getRunStatus() ==null || elementInfo.getRunStatus() !=1){
                bok = false;
            }

            logger.info( "{}>组件更新成功", elementInfo.getName() );
        }
        String nodeId = schedulePath.size() > 0 ? schedulePath.get( schedulePath.size() - 1 ).getId() : elements.get( 0 ).getId();
        //TTruModelElementDo info = truModelElementService.findById( nodeId );
        //TTruModelComponentDo tTruModelComponentDo = idComponentMap.get( info.getTcId() );
        modelDo = truModelService.findById( id );
        List<SchemaVo> schemaVos = new ArrayList<>();
//        if ( tTruModelComponentDo != null ) {
//            if ( tTruModelComponentDo.getComponentEn().equals( DataSourceOutPut_COMPONENT.getComponentName() ) ) {
//                modelDo.setOutputId( info.getId() );
//            }
//            else {
//                modelDo.setOutputType( 1 );
//            }
//
//            List<TTruModelFieldDo> fieldDos = truModelFieldService.selectByElementId( info.getId() );
//            TruBaseComponenT outComponenT = TruComponentEnum.getInstanceByName( tTruModelComponentDo.getComponentEn().toLowerCase() );
//            List<TTruModelFieldDo> outFieldDos = outComponenT.getShowField( fieldDos );
//
//            outFieldDos.stream().forEach( e -> {
//                if ( e.getIsSelect() == 1 ) {
//                    SchemaVo schemaVo = new SchemaVo();
//                    schemaVo.setFieldName( e.getFieldAlias() );
//                    schemaVo.setColumnType( e.getColumnType() );
//                    schemaVo.setFieldType( e.getFieldType() );
//                    schemaVo.setFieldExpr( e.getFieldExpr() );
//                    schemaVos.add( schemaVo );
//                }
//            } );
//        }
//        else{
//
//        }

        if ( "flink".equalsIgnoreCase( modelDo.getConfig() ) || "tee".equalsIgnoreCase( modelDo.getConfig() ) ) {

            List<TTruModelElementDo> meList = elements.stream().filter( x ->DataSourceOutPut_COMPONENT.getComponentName().equals(  x.getIcon() ) ).collect( Collectors.toList() );
            if ( meList.size() == 0 ) {
                List<TTruModelElementRelDo> relList = truModelElementRelService.selectByModelId( modelDo.getId() );
                List<String> nodeList = new ArrayList<>();
                for ( TTruModelElementDo elementDo : elements ) {
                    nodeList.add( elementDo.getId() );
                }
                for ( TTruModelElementRelDo relDo : relList ) {
                    nodeList.remove( relDo.getStartElementId() );
                }
                if ( nodeList.size() != 1 ) {
                    bok =false;
                }
                else {
                    TTruModelElementDo elementDo = elements.stream().filter( x -> x.getId().equals( nodeList.get( 0 ) ) ).collect( Collectors.toList() ).get( 0 );

                    String tableName = "tmp_" + modelDo.getId();

                    StringBuilder sb = new StringBuilder();

                    //生成语句
                    List<TTruModelFieldDo> fieldList = truModelFieldService.selectByElementIdAll( elementDo.getId() ).stream().filter( x -> x.getIsSelect() != null && x.getIsSelect() == 1 ).collect( Collectors.toList() );

                    List<String> field2List = new ArrayList<>();
                    for ( TTruModelFieldDo field : fieldList ) {
//                        if ( JdbcOpBuilder.dbStarRocks.equals( outSet.getDstype() ) ) {
//                            field2List.add( String.format( "%s %s ", field.getFieldAlias(), DbTypeToFlinkType.chgType( field.getFieldType())) );
//                        }
//                        else{
                            field2List.add( String.format( "%s %s COMMENT '%s' ", field.getFieldAlias(), DbTypeToFlinkType.chgType( field.getFieldType() ), ComUtil.trsEmpty( field.getFieldExpr(), field.getFieldAlias() ) ) );
//                        }
                    }
                    sb.append( "CREATE TABLE " ).append( tableName ).append( " (\r\n  " ).append( String.join( ",\r\n", field2List ) ).append( ") " );

                    //if ( !JdbcOpBuilder.dbStarRocks.equals( outSet.getDstype() ) ) {
                        sb.append( " COMMENT '" ).append( elementDo.getName() ).append( "'\r\n " );
                    //}

                    sb.append( " WITH ( \r\n" );
                    if ( JdbcOpBuilder.dbStarRocks.equals( outSet.getDstype() ) ) {

                        sb.append( "                'connector' = 'starrocks'," ).append( "\r\n" );
                        sb.append( "                'jdbc-url' = '" ).append( outSet.getJdbcUrl() ).append( "'," ).append( "\r\n" );
                        sb.append( "                'database-name' = '" ).append( outSet.getDbName() ).append( "'," ).append( "\r\n" );
                        sb.append( "                'table-name' = '" ).append( tableName ).append( "'," ).append( "\r\n" );
                        sb.append( "                'username' = '" ).append( outSet.getUser() ).append( "'," ).append( "\r\n" );
                        sb.append( "                'password' = '" ).append( outSet.getPassword() ).append( "'," ).append( "\r\n" );
                        sb.append( "                'load-url' = '" ).append( outSet.getLoadUrl() ).append( "'" );

                    }
                    else {
                        if ( JdbcOpBuilder.dbMySql.equals( outSet.getDstype() ) ||
                                JdbcOpBuilder.dbMySql5.equals( outSet.getDstype() ) ||
                                JdbcOpBuilder.dbMySql8.equals( outSet.getDstype() ) ||
                                JdbcOpBuilder.dbPostgreSql.equals( outSet.getDstype() ) ||
                                JdbcOpBuilder.dbTiDb.equals( outSet.getDstype() )
                        ) {
                            sb.append( "'connector' = 'jdbc'," ).append( "\r\n" );
                        }
                        else {
                            sb.append( "'connector' = 'commondb'," ).append( "\r\n" );
                        }
                        sb.append( "                'driver' = '" ).append( outSet.getDriver() ).append( "'," ).append( "\r\n" );
                        sb.append( "                'url' = '" ).append( outSet.getJdbcUrl() ).append( "'," ).append( "\r\n" );
                        sb.append( "                'table-name' = '" ).append( tableName ).append( "'," ).append( "\r\n" );
                        sb.append( "                'username' = '" ).append( outSet.getUser() ).append( "'," ).append( "\r\n" );
                        sb.append( "                'password' = '" ).append( outSet.getPassword() ).append( "'," ).append( "\r\n" );
                        sb.append( "                'sink.buffer-flush.max-rows' = '5000',\n" +
                                "                'sink.buffer-flush.interval' = '1s',\n" +
                                "                'sink.max-retries' = '3'" );
                    }

                    sb.append( ");" ).append( "\r\n" );

                    //sql和配置存储
                    field2List.clear();
                    fieldList.forEach( e -> {
                                if ( e.getIsSelect() == 1 ) {
                                    field2List.add( e.getFieldAlias() );
                                }
                            }
                    );
                    //sql2.append( "CREATE VIEW " ).append( pre ).append( " AS SELECT \r\n" );
                    sb.append( " INSERT INTO " ).append( tableName ).append( " " );
                    sb.append( "(\r\n" ).append( String.join( ",\r\n", field2List ) ).append( ")\r\n" );

                    sb.append( " SELECT " );
                    sb.append( String.join( ",\r\n", field2List ) ).append( "\r\n FROM  " ).append( elementDo.getElement() );

                    sb.append( ";\r\n" );

                    createSb.append( sb.toString() );

                }
            }
            else{

            }
        }

        String old = modelDo.getRunSql();
        String infoSql = createSb.toString();
        if( bok ) {
            modelDo.setRunSql( infoSql );
            //modelDo.setTotal( modelDo.getTotal() );
            modelDo.setStatus( 1 );
            modelDo.setViewName( "tmp_" + modelDo.getId() );
        }
        else{
            modelDo.setRunSql( "" );
            modelDo.setViewName( "" );
        }
        modelDo.setParamConfig( JsonUtil.toJson( schemaVos ) );
        if ( !infoSql.equals( old ) ) {
            //genViewAndColumns( modelDo );
            modelDo.setCacheFlag( 0 );
            modelDo.setLastRunTime( null );
        }
        truModelService.updateBean( modelDo );
    }

    public void genViewAndColumns( TTruModelDo info ) throws Exception {

//        String datasetVmCatalogName = systemParamHolder.getDatasetVmCatalogName();
//        String datasetVmSchemaName = systemParamHolder.getDatasetVmSchemaName();
//        info.setViewName(String.format("%s.%s.t_%s", datasetVmCatalogName, datasetVmSchemaName, info.getId()));
//        UserDo userInfo = new UserDo();
//        userInfo.setTokenId(info.getCreatorId());
//        String viewSql = String.format("CREATE OR REPLACE VIEW %s AS %s", info.getViewName(), info.getRunSql());
//        try (HetuJdbcOperate jdbcOp = hetuJdbcOperateComponent.genDataHubJdbcOperate(userInfo)) {
//            logger.info(viewSql);
//            jdbcOp.execute(viewSql);
//        }
    }

    public List<Map<String, Object>> execute( String id ) {
        List<Map<String, Object>> list = null;
//        TTruModelElementDo elementDo = bydbModelElementService.findById(id);
//        String sql = elementDo.getRunSql();
//        try {
//            String sqlInfo = sql.concat(" LIMIT 10");
//            list = hetuJdbcOperate.selectData(sqlInfo);
//            logger.info(sqlInfo);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return list;
    }

    public List<Map<String, Object>> datacheck( TTruModelElementDo elementDo,
                                                List<TTruModelFieldDo> fieldDoList ) {
        List<Map<String, Object>> list = new ArrayList<>();
//        String sql = elementDo.getRunSql();
//        String preSql = getMatchedFrom(sql);
//        for (TTruModelFieldDo fieldDo : fieldDoList) {
//            String runSql = "";
//            if ("Integer".equals(fieldDo.getColumnType())) {
//                runSql = makeExeIntSql(fieldDo, preSql);
//            } else if ("Date".equals(fieldDo.getColumnType())) {
//                runSql = makeExeDateSql(fieldDo, preSql);
//            } else {
//                runSql = makeExeStrSql(fieldDo, preSql);
//            }
//            try {
//                List<Map<String, Object>> data = hetuJdbcOperate.selectData(runSql);
//                list.addAll(data);
//            } catch (SQLException e) {
//                e.printStackTrace();
//                logger.error(runSql);
//            }
//        }
        return list;
    }

}
