package cn.bywin.business.util.analysis;

import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.DataSourceOutPut_COMPONENT;

import cn.bywin.business.bean.analysis.olk.OlkBaseComponenT;
import cn.bywin.business.bean.analysis.olk.OlkCheckComponent;
import cn.bywin.business.bean.analysis.olk.OlkComponentEnum;
import cn.bywin.business.bean.analysis.olk.graph.OlkDirectedGraph;
import cn.bywin.business.bean.analysis.olk.graph.OlkVertex;
import cn.bywin.business.bean.analysis.olk.template.OlkTableComponent;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.olk.TOlkModelComponentDo;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementJobDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.view.bydb.SchemaVo;
import cn.bywin.business.bean.view.olk.OlkNode;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.service.bydb.BydbDatabaseService;
import cn.bywin.business.service.bydb.BydbObjectService;
import cn.bywin.business.service.federal.DataSourceService;
import cn.bywin.business.service.olk.OlkModelComponentService;
import cn.bywin.business.service.olk.OlkModelElementJobService;
import cn.bywin.business.service.olk.OlkModelElementRelService;
import cn.bywin.business.service.olk.OlkModelElementService;
import cn.bywin.business.service.olk.OlkModelFieldService;
import cn.bywin.business.service.olk.OlkModelObjectService;
import cn.bywin.business.service.olk.OlkModelService;
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
public class OlkModelRunService {
    protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Autowired
    private OlkModelObjectService truModelObjectService;
    @Autowired
    private OlkModelComponentService truModelComponentService;
    @Autowired
    private OlkModelFieldService truModelFieldService;
    //    @Autowired
//    private DataHubJdbcOperateConfigurate hutuConfig;
    @Autowired
    private BydbObjectService bydbObjectService;
    @Autowired
    private OlkModelService truModelService;
    @Autowired
    private OlkModelElementService truModelElementService;
    @Autowired
    private OlkModelElementRelService truModelElementRelService;
    @Autowired
    private BydbDatabaseService bydbDatabaseService;
    @Autowired
    private OlkModelElementJobService bydbModelElementJobService;
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

    public OlkCheckComponent runModel( TOlkModelDo modelDo, TOlkModelElementJobDo elementJobDo ) {
        List<TOlkModelElementDo> elements = truModelElementService.selectByModelId( modelDo.getId() );
        List<TOlkModelElementRelDo> elementRels = truModelElementRelService.selectByModelId( modelDo.getId() );
        OlkDirectedGraph<String> directedGraph = new OlkDirectedGraph();
        OlkCheckComponent component = new OlkCheckComponent( true, "成功" );
        for ( TOlkModelElementDo elementDo : elements ) {
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

        List<OlkVertex<String>> schedulePath = directedGraph.buildTaskSchedulePath();
        TOlkModelElementDo info = truModelElementService.findById( schedulePath.get( schedulePath.size() - 1 ).getId() );
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
    /*public boolean analyseToTable(List<TOlkModelFieldDo> fieldDos, EDatasourceDo datasourceDo, TOlkModelElementJobDo jobDo,
                                  TOlkModelElementDo tOlkModelElementDo, String table) throws Exception {
        StringBuilder sb = new StringBuilder();
        String bydbTemplate = SysParamSetOp.readValue(datasourceDo.getDsDriver().replaceAll("\\.", "_"), "");
        String bydbCacheTableTemplate = bydbTemplate.split("--")[1];
        String bydbDropTableTemplate = bydbTemplate.split("--")[0];
        String tableName = StringUtils.isNotBlank(table) ? table : tOlkModelElementDo.getElement().concat("_").concat(tOlkModelElementDo.getId());

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
            TOlkModelFieldDo columnDo = fieldDos.get(norder);
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

        fromMap.put("sql", tOlkModelElementDo.getRunSql());
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
    public OlkCheckComponent checkField( OlkNode truNode, TOlkModelElementDo elementDo ) {
        OlkCheckComponent component = new OlkCheckComponent( true, "成功" );
        try {
            List<String> viewNodes = truNode.getViewIds();
            List<TOlkModelFieldDo> elementFieldDos = new ArrayList<>();
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


            List<TOlkModelElementDo> elements = truModelElementService.selectByModelId( elementDo.getModelId() );
            List<TOlkModelElementRelDo> elementRels = truModelElementRelService.selectByModelId( elementDo.getModelId() );
            List<TOlkModelComponentDo> componentDos = truModelComponentService.findAll();
            Map<String, TOlkModelComponentDo> idComponentMap = componentDos.stream().collect( Collectors.toMap( TOlkModelComponentDo::getId, e -> e ) );

            for ( TOlkModelElementRelDo elementRel : elementRels ) {
                if ( elementDo.getId().equals( elementRel.getEndElementId() ) ) {
                    for ( TOlkModelElementDo element : elements ) {
                        if ( element.getId().equals( elementRel.getStartElementId() ) ) {
                            if ( element.getRunStatus() == 0 ) {
                                return new OlkCheckComponent( false, "请先配置上级组" );
                            }
                        }
                    }
                }
            }
            OlkDirectedGraph<String> directedGraph = new OlkDirectedGraph();
            elements.forEach( element -> {
                directedGraph.addVertex( element.getId() );
            } );
            elementRels.forEach( element -> {
                directedGraph.addEdge( element.getStartElementId(), element.getEndElementId() );
            } );

            if ( directedGraph.hasIsolatedVertices() && elementRels.size() > 1 ) {
                return new OlkCheckComponent( true, "成功" );
            }
            if ( directedGraph.hasRecycle() ) {
                return new OlkCheckComponent( false, "组件存在循环依赖,请重新配置" );
            }
            Map<String, TOlkModelElementDo> idElementMap = elements.stream().collect( Collectors.toMap( TOlkModelElementDo::getId, e -> e ) );
            List<OlkVertex<String>> schedulePath = directedGraph.buildTaskSchedulePath();
            int indexs = -1;
            for ( int i = 0; i < schedulePath.size(); i++ ) {
                if ( schedulePath.get( i ).getId().equals( elementDo.getId() ) ) {
                    indexs = i;
                }
            }
            if ( indexs == -1 ) {
                return new OlkCheckComponent( false, "节点不存在" );
            }

            /*List<TOlkModelFieldDo> delFieldList = bydbModelFieldService.selectByElementIdAll( elementDo.getId() );
            List<TOlkModelFieldDo> addFieldList = new ArrayList<>();
            List<TOlkModelFieldDo> modFieldList = new ArrayList<>();
            boolean bchg = false;
            if ( elementFieldDos.size() != delFieldList.size() ) {
                bchg = true;
            }

            Map<String, TOlkModelFieldDo> fieldMap = delFieldList.stream().collect( Collectors.toMap( x -> x.getFieldName(), x -> x ) );

            int norder = 1;
            for ( TOlkModelFieldDo f2 : elementFieldDos ) {
                boolean bfound = false;
                TOlkModelFieldDo f1 = fieldMap.get( f2.getFieldName() );
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
                TOlkModelElementDo elementInfo = idElementMap.get( schedulePath.get( i ).getId() );
                    for ( OlkEdge edge : schedulePath.get( i ).getEdges() ) {
                        TOlkModelElementDo elem = bydbModelElementService.findById( edge.getEndVertex().getId().toString() );
                        TOlkModelElementDo etmp = new TOlkModelElementDo();
                        etmp.setId( elem.getId() );
                        etmp.setRunStatus( 0 );
                        etmp.setModifiedTime( ComUtil.getCurTimestamp() );
                        bydbModelElementService.updateNoNull( etmp );
                    }
                }
            }*/

            /*for ( int i = indexs; i < schedulePath.size(); i++ ) {
                TOlkModelElementDo elementInfo = idElementMap.get( schedulePath.get( i ).getId() );
                TOlkModelComponentDo components = idComponentMap.get( elementInfo.getTcId() );
                List<TOlkModelFieldDo> fields = new ArrayList<>();
                if ( elementInfo.getId().equals( elementDo.getId() ) ) {
                    fields = elementFieldDos;
                }
                else {
                    fields = bydbModelFieldService.selectByElementId( schedulePath.get( i ).getId() );
                }

                for ( OlkEdge edge : schedulePath.get( i ).getEdges() ) {
                    List<TOlkModelFieldDo> fieldDos = bydbModelFieldService.selectByElementIdAll( edge.getEndVertex().getId().toString() );
                    List<TOlkModelFieldDo> updateFields = new ArrayList<>();
                    fields.stream().forEach( p -> {
                        fieldDos.stream().forEach( e -> {
                            TOlkModelElementDo elementDo1 = idElementMap.get( e.getElementId() );
                            TOlkModelComponentDo componentDo = idComponentMap.get( elementDo1.getTcId() );
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
        TOlkModelDo modelDo = truModelService.findById( id );
        modelDo.setRunSql( "" );
        truModelService.updateBean( modelDo );
        List<TOlkModelElementDo> elements = truModelElementService.selectByModelId( id );
        List<TOlkModelElementRelDo> elementRels = truModelElementRelService.selectByModelId( id );
        List<TOlkModelComponentDo> componentDos = truModelComponentService.findAll();
        List<TOlkModelObjectDo> datasource = truModelObjectService.findAll();
        OlkDirectedGraph<String> directedGraph = new OlkDirectedGraph();
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

        Map<String, TOlkModelElementDo> idElementMap = elements.stream().collect( Collectors.toMap( TOlkModelElementDo::getId, e -> e ) );
        Map<String, TOlkModelComponentDo> idComponentMap = componentDos.stream().collect( Collectors.toMap( TOlkModelComponentDo::getId, e -> e ) );
        List<OlkVertex<String>> schedulePath = directedGraph.buildTaskSchedulePath();
        int runOrder = 1;
        String outPutId = null;
        boolean bok = true;
        for ( OlkVertex<String> element : schedulePath ) {
            TOlkModelElementDo elementInfo = idElementMap.get( element.getId() );
            List<TOlkModelElementDo> models = truModelElementService.selectByModelId( elementInfo.getModelId() );
            if ( elementInfo.getElementType() == 0 ) {
                TOlkModelComponentDo tOlkModelComponentDo = idComponentMap.get( elementInfo.getTcId() );
                if ( tOlkModelComponentDo == null ) {
                    throw new IllegalArgumentException( "组件不存在" );
                }
                OlkBaseComponenT baseComponenT = OlkComponentEnum.getInstanceByName( tOlkModelComponentDo.getComponentEn().toLowerCase() );
                List<TOlkModelElementDo> pre = truModelElementService.selectStartId( elementInfo.getId() );
                List<TOlkModelElementDo> next = truModelElementService.selectEndId( elementInfo.getId() );
                logger.info( "组件:{},{}", elementInfo.getElement(), elementInfo.getConfig() );
                baseComponenT.setComponents( componentDos );
                baseComponenT.setModel( models );
                baseComponenT.setDatasource( datasource );
                baseComponenT.setPreModel( pre );
                baseComponenT.setNextModel( next );
                List<TOlkModelFieldDo> extenFieldList = new ArrayList<>();
                for ( TOlkModelElementDo eleTmp : pre ) {
                    extenFieldList.addAll( truModelFieldService.selectByElementIdAll( eleTmp.getId() ) );
                }
                baseComponenT.setExtendsDos( extenFieldList );
                OlkNode truNode = truModelElementService.getNodes( elementInfo );
                OlkNode init = baseComponenT.init( truNode, elementInfo );
                if ( baseComponenT.check( init, elementInfo ).isSuccess() ) {

                    baseComponenT.build( init, elementInfo, modelDo );
                    truModelElementService.updateBeanDetail( elementInfo, init );
                }
                else {
                    logger.error( "{}>组件更新失败,{}", elementInfo.getName(), baseComponenT.check( init, elementInfo ).getMessage() );
                    return;
                }
//                if( OlkComponentEnum.DataSourceOutPut_COMPONENT.getComponentName().equals( tOlkModelComponentDo.getComponentEn().toLowerCase() )){
//                    outPutId = elementInfo.getId();
//                }
            }
            else if ( elementInfo.getElementType() == 1 ) { //起始表

                FDatasourceDo datasourceDo = tabSourceMap.get( elementInfo.getTcId() );
                OlkBaseComponenT baseComponenT = new OlkTableComponent();
                OlkNode truNode = truModelElementService.getNodes( elementInfo );
                truNode.setDbSource( datasourceDo );
                OlkNode init = baseComponenT.init( truNode, elementInfo );
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
            TOlkModelElementDo eleTmp = new TOlkModelElementDo();
            eleTmp.setId( elementInfo.getId() );
            eleTmp.setRunOrder( runOrder++ );
            truModelElementService.updateNoNull( eleTmp );

            if ( elementInfo.getRunStatus() == null || elementInfo.getRunStatus() != 1 ) {
                bok = false;
            }

            logger.info( "{}>组件更新成功", elementInfo.getName() );
        }
        String nodeId = schedulePath.size() > 0 ? schedulePath.get( schedulePath.size() - 1 ).getId() : elements.get( 0 ).getId();
        //TOlkModelElementDo info = truModelElementService.findById( nodeId );
        //TOlkModelComponentDo tOlkModelComponentDo = idComponentMap.get( info.getTcId() );
        modelDo = truModelService.findById( id );
        List<SchemaVo> schemaVos = new ArrayList<>();
//        if ( tOlkModelComponentDo != null ) {
//            if ( tOlkModelComponentDo.getComponentEn().equals( DataSourceOutPut_COMPONENT.getComponentName() ) ) {
//                modelDo.setOutputId( info.getId() );
//            }
//            else {
//                modelDo.setOutputType( 1 );
//            }
//
//            List<TOlkModelFieldDo> fieldDos = truModelFieldService.selectByElementId( info.getId() );
//            OlkBaseComponenT outComponenT = OlkComponentEnum.getInstanceByName( tOlkModelComponentDo.getComponentEn().toLowerCase() );
//            List<TOlkModelFieldDo> outFieldDos = outComponenT.getShowField( fieldDos );
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

            List<TOlkModelElementDo> meList = elements.stream().filter( x -> DataSourceOutPut_COMPONENT.getComponentName().equals( x.getIcon() ) ).collect( Collectors.toList() );
            if ( meList.size() == 0 ) {
                List<TOlkModelElementRelDo> relList = truModelElementRelService.selectByModelId( modelDo.getId() );
                List<String> nodeList = new ArrayList<>();
                for ( TOlkModelElementDo elementDo : elements ) {
                    nodeList.add( elementDo.getId() );
                }
                for ( TOlkModelElementRelDo relDo : relList ) {
                    nodeList.remove( relDo.getStartElementId() );
                }
                if ( nodeList.size() != 1 ) {
                    bok = false;
                }
                else {
                    TOlkModelElementDo elementDo = elements.stream().filter( x -> x.getId().equals( nodeList.get( 0 ) ) ).collect( Collectors.toList() ).get( 0 );

                    String tableName = "tmp_" + modelDo.getId();

                    StringBuilder sb = new StringBuilder();

                    //生成语句
                    List<TOlkModelFieldDo> fieldList = truModelFieldService.selectByElementIdAll( elementDo.getId() ).stream().filter( x -> x.getIsSelect() != null && x.getIsSelect() == 1 ).collect( Collectors.toList() );

                    List<String> field2List = new ArrayList<>();
                    for ( TOlkModelFieldDo field : fieldList ) {
                        field2List.add( String.format( "%s %s COMMENT '%s' ", field.getFieldAlias(), DbTypeToFlinkType.chgType( field.getFieldType() ), ComUtil.trsEmpty( field.getFieldExpr(), field.getFieldAlias() ) ) );
                    }
                    sb.append( "CREATE TABLE " ).append( tableName ).append( " (\r\n  " ).append( String.join( ",\r\n", field2List ) ).append( ") COMMENT '" ).append( elementDo.getName() ).append( "'\r\n WITH ( \r\n" );
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
            else {

            }
        }
        else {
            //List<TOlkModelElementDo> meList = elements.stream().filter( x ->DataSourceOutPut_COMPONENT.getComponentName().equals(  x.getIcon() ) ).collect( Collectors.toList() );
            //if ( meList.size() == 0 ) {
            List<TOlkModelElementRelDo> relList = truModelElementRelService.selectByModelId( modelDo.getId() );
            List<String> nodeList = new ArrayList<>();
            for ( TOlkModelElementDo elementDo : elements ) {
                nodeList.add( elementDo.getId() );
            }
            //HashMap<String,String> outRelMap = new HashMap<>(); //寻找输出的输入 （输出为1对1）
            for ( TOlkModelElementRelDo relDo : relList ) {
                nodeList.remove( relDo.getStartElementId() );
                //outRelMap.put( relDo.getEndElementId(),relDo.getStartElementId() );
            }
            List<TOlkModelElementDo> outList = elements.stream().filter( x -> nodeList.indexOf( x.getId() ) >= 0 ).collect( Collectors.toList() );
            createSb = new StringBuffer();
            for ( TOlkModelElementDo ele : outList ) {
                if ( DataSourceOutPut_COMPONENT.getComponentName().equals( ele.getIcon() ) ) {
                    createSb.append( ele.getRunSql() ).append( ";\r\n" );
                    //String soureId = outRelMap.get( ele.getId() );
//                    for ( TOlkModelElementDo element : elements ) {
//                        if( element.getId().equals( soureId )){
//                            createSb.append( element.getRunSql() ).append( ";\r\n" );
//                            break;
//                        }
//                    }
                }
                else{
                    if( outList.size()>1 ) {
                        bok = false;
                        break;
                    }
                    createSb.append( ele.getRunSql() ).append( ";\r\n" );
                }
            }
        }

        String old = modelDo.getRunSql();
        String infoSql = createSb.toString();
        if ( bok ) {
            modelDo.setRunSql( infoSql );
            //modelDo.setTotal( modelDo.getTotal() );
            modelDo.setStatus( 1 );
            modelDo.setViewName( "tmp_" + modelDo.getId() );
        }
        else {
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

    public void genViewAndColumns( TOlkModelDo info ) throws Exception {

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
//        TOlkModelElementDo elementDo = bydbModelElementService.findById(id);
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

    public List<Map<String, Object>> datacheck( TOlkModelElementDo elementDo,
                                                List<TOlkModelFieldDo> fieldDoList ) {
        List<Map<String, Object>> list = new ArrayList<>();
//        String sql = elementDo.getRunSql();
//        String preSql = getMatchedFrom(sql);
//        for (TOlkModelFieldDo fieldDo : fieldDoList) {
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
