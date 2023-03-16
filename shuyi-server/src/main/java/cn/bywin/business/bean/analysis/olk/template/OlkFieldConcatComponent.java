package cn.bywin.business.bean.analysis.olk.template;

import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.FieldConcat_COMPONENT;

import cn.bywin.business.bean.analysis.olk.OlkBaseComponenT;
import cn.bywin.business.bean.analysis.olk.OlkCheckComponent;
import cn.bywin.business.bean.olk.TOlkModelComponentDo;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.bean.view.bydb.AggConcat;
import cn.bywin.business.bean.view.bydb.Operand;
import cn.bywin.business.bean.view.olk.OlkNode;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description 合并行组件配置
 * @Author wangh
 * @Date 2021-10-20
 */

@Data
public class OlkFieldConcatComponent extends OlkBaseComponenT {


    private List<Operand> operator = new ArrayList<>();
    private List<String> concatTable = new ArrayList<>();
    private long allMatch;
    private long notAllMatch;
    private List<String> viewNodes = new ArrayList<>();

    public OlkFieldConcatComponent() {
    }

    @Override
    public List<TOlkModelFieldDo> getShowField( List<TOlkModelFieldDo> fieldDos ) throws Exception {
        List<TOlkModelFieldDo> disData = new ArrayList<>();
        fieldDos.stream().forEach( e -> {
            if ( e.getIsSelect() == 1 && e.getElementId().equals( e.getExtendsId() ) ) {
                e.setFieldName( e.getFieldAlias() );
                disData.add( e );
            }
        } );
        List<TOlkModelFieldDo> fieldDoList = disData.stream().sorted( Comparator.comparing( TOlkModelFieldDo::getFilterSort ) ).collect( Collectors.toList() );

        return fieldDoList;
    }

    @Override
    public OlkNode init( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {
        truNode.getOperators().getFields().stream().forEach( e -> {
            e.setElementId( e.getExtendsId() );
        } );
        Map<String, TOlkModelFieldDo> fieldMap = truNode.getOperators().getFields().stream().collect( Collectors.toMap( x -> x.getId(), x -> x ) );
        Map<String, TOlkModelFieldDo> idElementMap = truNode.getOperators().getFields().stream().collect( Collectors.toMap( TOlkModelFieldDo::getId, e -> e, ( k1, k2 ) -> k1 ) );

        concatTable = getPreModel().stream().sorted( Comparator.comparing( TOlkModelElementDo::getCreatedTime ) ).map( e -> e.getId() ).collect( Collectors.toList() );
        Map<String, Object> params = new HashMap<>( 5 );
        params.put( "concatTable", concatTable );
        if ( truNode.getOperators().getFilters().size() > 0 ) {
            truNode.getOperators().getFilters().stream().forEach( e -> {
                Operand elementVo = new Operand();
                try {
                    List<AggConcat> agg = new ArrayList<>();
                    MyBeanUtils.copyBean2Bean( elementVo, e );
                    /*Map<String, Object> config = MapTypeAdapter.gsonToMap(e.getAggregation());
                    for (String s : config.keySet()) {
                        AggConcat aggConcat = new AggConcat();
                        TOlkModelFieldDo tOlkModelFieldDo = idElementMap.get(s);
                        aggConcat.setFieldId(s);
                        aggConcat.setId(tOlkModelFieldDo.getElementId());
                        if (StringUtils.isNotBlank(String.valueOf(config.get(s)))) {
                            aggConcat.setFieldName(String.valueOf(config.get(s)));
                        }
                        aggConcat.setColumnType(tOlkModelFieldDo.getColumnType());


                        agg.add(aggConcat);
                    }*/
                    List list = JsonUtil.deserialize( e.getFilterConfig(), ArrayList.class );
                    AggConcat concat1 = new AggConcat();
                    //JsonUtil.toJson( o )

                    concat1.setId( e.getElementId() );
                    concat1.setFieldId( e.getId() );
                    concat1.setColumnType( e.getColumnType() );
                    concat1.setFieldName( e.getFieldName() );
                    agg.add( concat1 );
                    for ( Object obj : list ) {
                        Map<String, String> dat = (Map<String, String>) obj;
                        String fieldId = dat.keySet().iterator().next();
                        fieldId = dat.get( "fieldId" );
                        AggConcat concat = new AggConcat();
                        //JsonUtil.toJson( o )
                        TOlkModelFieldDo fieldDo = fieldMap.get( fieldId );
                        concat.setId( fieldDo.getElementId() );
                        concat.setFieldId( fieldDo.getId() );
                        concat.setColumnType( e.getColumnType() );
                        concat.setFieldName( fieldDo.getFieldName() );
                        agg.add( concat );
                    }
                    elementVo.setConcat( agg );
                    operator.add( elementVo );
                }
                catch ( Exception ex ) {
                    ex.printStackTrace();
                }
            } );
        }
        else {//连接配置初始化
            if ( getPreModel().size() > 0 ) {
                List<TOlkModelFieldDo> fieldIndex = truNode.getOperators().getFields().stream().filter( e -> e.getElementId().equals( concatTable.get( 0 ) ) ).collect( Collectors.toList() );
                if ( CollectionUtils.isNotEmpty( fieldIndex ) ) {
                    fieldIndex.stream().forEach( elementDo -> {
                        Operand elementVo = new Operand();
                        try {
                            MyBeanUtils.copyBean2Bean( elementVo, elementDo );
                            operator.add( elementVo );
                        }
                        catch ( Exception ex ) {
                            ex.printStackTrace();
                        }
                    } );
                }

            }
        }
        operator.stream().forEach( e -> {
            if ( e.getIsSelect() == 1 ) {
                viewNodes.add( e.getId() );
            }
        } );
        //修改
        operator.stream().forEach( e -> {
            List<AggConcat> agg = new ArrayList<>();
            if ( e.getConcat() == null ) {

//                AggConcat aggConcat = new AggConcat();
//                aggConcat.setFieldId(e.getId());
//                aggConcat.setId(e.getElementId());
//                aggConcat.setColumnType(e.getColumnType());
//                aggConcat.setFieldName(e.getFieldName());
//                agg.add(aggConcat);
//                for (int i = 1; i < concatTable.size(); i++) {
//                    int finalI = i;
//                    List<TOlkModelFieldDo> fieldDos = node.getOperators().getFields().stream().filter(x -> x.getExtendsId().equals(concatTable.get(finalI))).collect(Collectors.toList());
//                    AggConcat concat = new AggConcat();
//                    fieldDos.stream().forEach(y -> {
//                        concat.setId(y.getElementId());
//                        concat.setFieldId(y.getId());
//                        concat.setColumnType(e.getColumnType());
//                        if ((y.getFieldName().equals(e.getFieldName())
//                                && y.getColumnType().equals(e.getColumnType()))) {
//                            concat.setFieldName(y.getFieldName());
//                        }
//                    });
//                    agg.add(concat);
//                }
                TOlkModelFieldDo leftField = fieldMap.get( e.getId() );
                AggConcat concat1 = new AggConcat();
                //JsonUtil.toJson( o )

                concat1.setId( e.getElementId() );
                concat1.setFieldId( e.getId() );
                concat1.setColumnType( e.getColumnType() );
                concat1.setFieldName( e.getFieldName() );
                agg.add( concat1 );

                JsonObject json = null;
                if ( StringUtils.isNotBlank( leftField.getFilterConfig() ) ) {
                    try {
                        json = JsonUtil.toJsonObject( leftField.getFilterConfig() );
                    }
                    catch ( Exception ex ) {
                        ex.printStackTrace();
                    }
                }
                for ( int i = 1; i < concatTable.size(); i++ ) {
                    AggConcat concat = new AggConcat();
                    TOlkModelFieldDo fieldDo = null;
                    if ( json != null && json.has( concatTable.get( i ) ) ) {
                        String fieldId = json.getAsJsonObject( concatTable.get( i ) ).keySet().iterator().next();
                        //fieldId = dat.get( fieldId );
                        fieldDo = fieldMap.get( fieldId );
                        if ( fieldDo != null && !StringUtils.equals( concatTable.get( i ), fieldDo.getElementId() ) ) {
                            fieldDo = null;
                        }
                    }
                    if( fieldDo != null){
                        concat.setId( concatTable.get( i ) );
                        concat.setFieldId( fieldDo.getId() );
                        concat.setColumnType( e.getColumnType() );
                        concat.setFieldName( fieldDo.getFieldName() );
                    }
                    else {
                        concat.setId( concatTable.get( i ) );
                        concat.setFieldId( "" );
                        concat.setColumnType( "" );
                        concat.setFieldName( "" );
                    }
                    agg.add( concat );
                }

                e.setConcat( agg );
            }
//            else {
//                agg.addAll(e.getConcat());
//            }

            e.setCnt( agg.stream().filter( v -> StringUtils.isNotBlank( v.getFieldName() ) &&
                    !"null".equals( v.getFieldName() ) ).count() );
        } );
        long macth = operator.stream().filter( e -> e.getCnt() == concatTable.size() ).count();
        params.put( "allMatch", macth );
        params.put( "notAllMatch", operator.size() - macth );
        params.put( "operator", operator );
        truNode.setViewIds( viewNodes );
        truNode.setType( FieldConcat_COMPONENT.getComponentName() );
        truNode.setParams( params );
        return truNode;
    }

    @Override
    public boolean changeSameFieldName( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {

        OlkFieldConcatComponent concatComponent = JsonUtil.gson().fromJson( JsonUtil.toJson( truNode.getParams() ), OlkFieldConcatComponent.class );
        concatComponent.setViewNodes( truNode.getViewIds() );
        //Map<String, TOlkModelElementDo> idElementMap = getPreModel().stream().collect(Collectors.toMap(TOlkModelElementDo::getId, e -> e, (k1, k2) -> k1));

        List<TOlkModelFieldDo> fieldList = truNode.getOperators().getFields();
        for ( TOlkModelFieldDo fieldDo : fieldList ) {
            fieldDo.setIsSelect( 0 );
            fieldDo.setFilterConfig( null );
            fieldDo.setFilterValue( null );
        }
        Map<String, TOlkModelFieldDo> fieldMap = fieldList.stream().collect( Collectors.toMap( x -> x.getId(), x -> x ) );

        List<Operand> operator = concatComponent.getOperator();
        for ( Operand operand : operator ) {
            if ( concatComponent.getViewNodes().indexOf( operand.getId() ) < 0 ) continue;
            TOlkModelFieldDo fieldDo = fieldMap.get( operand.getId() );
            HashMap<String, Object> filterConfig = new HashMap<>();
            for ( int i = 1; i < operand.getConcat().size(); i++ ) {
                AggConcat aggConcat = operand.getConcat().get( i );
                for ( TOlkModelFieldDo tmpDo : fieldList ) {
                    if ( tmpDo.getElementId().equals( concatComponent.getConcatTable().get( i ) ) && tmpDo.getFieldName().equals( aggConcat.getFieldName() ) ) {
                        HashMap<String, String> datMap = new HashMap<>();
                        datMap.put( tmpDo.getId(), tmpDo.getFieldName() );
                        filterConfig.put( tmpDo.getExtendsId(), datMap );
                        tmpDo.setFilterValue( fieldDo.getFieldName() );
                        break;
                    }
                }
            }
//            for ( AggConcat aggConcat : operand.getConcat() ) {
//                if( !fieldDo.getId().equals(  aggConcat.getFieldId() )) {
//
//                    TOlkModelFieldDo tmp1 = fieldMap.get( aggConcat.getFieldId() );
//                    filterConfig.put( tmp1.getId(), tmp1.getFieldName() );
////                        }
//                    tmp1.setFilterValue( fieldDo.getFieldName() );
//                }
//            }
            fieldDo.setFilterConfig( JsonUtil.toJson( filterConfig ) );
        }
        /*for ( int i = 0; i < operator.size(); i++ ) {
                if (concatComponent.getViewNodes().contains( concatComponent.getOperator().get( i ).getId() ) ) {
                    TOlkModelFieldDo fieldDo = fieldMap.get( operator.get( i ).getId() );
                    Map<String, String> filterConfig = new HashMap<>();
                    for ( AggConcat aggConcat : operator.get( i ).getConcat() ) {

                        if( !fieldDo.getId().equals(  aggConcat.getFieldId() )) {
                            TOlkModelFieldDo tmp1 = fieldMap.get( aggConcat.getFieldId() );

//                        if( StringUtils.isNotBlank( tmp1.getFieldName() )) {
//                            filterConfig.put( tmp1.getId(), tmp1.getFieldName() );
//                        }
//                        else{
                            filterConfig.put( tmp1.getId(), tmp1.getFieldName() );
//                        }
                            tmp1.setFilterValue( fieldDo.getFieldName() );
                        }
                    }
                    fieldDo.setFilterConfig( JsonUtil.toJson( filterConfig ) );
                }
            }*/

//            List<TOlkModelFieldDo> filters = new ArrayList<>();
//            if ( CollectionUtils.isNotEmpty( concatComponent.getOperator() ) ) {
//                for ( int i = 0; i < concatComponent.getOperator().size(); i++ ) {
//                    TOlkModelFieldDo elementVo = new TOlkModelFieldDo();
//                    Operand elementDo = concatComponent.getOperator().get( i );
//                    MyBeanUtils.copyBean2Bean( elementVo, elementDo );
//                    if ( node.getViewIds().contains( elementDo.getId() ) ) {
//                        elementVo.setIsSelect( 1 );
//                    }
//                    else {
//                        elementVo.setIsSelect( 0 );
//                    }
//                    Map<String, String> constr = Maps.newLinkedHashMap();
//                    elementDo.getConcat().stream().forEach( e -> {
//                        constr.put( e.getFieldId(), e.getFieldName() );
//                    } );
//                    elementVo.setAggregation( JsonUtil.toJson( constr ).replace( "\n", "" ) );
//                    elementVo.setId( ComUtil.genId() );
//                    elementVo.setFilterSort( i );
//                    elementVo.setExtendsId( fModelElementDo.getId() );
//                    elementVo.setElementId( fModelElementDo.getId() );
//                    elementVo.setTableId( fModelElementDo.getId() );
//                    filters.add( elementVo );
//                }
//            }
//
//            long macth = concatComponent.getOperator().stream().filter( e -> e.getConcat().size() == concatTable.size() ).count();
//            concatComponent.setAllMatch( macth );
//            concatComponent.setNotAllMatch( concatComponent.getOperator().size() - macth );
//            node.getOperators().setFilters( filters );

        return true;
    }

    @Override
    public OlkCheckComponent check( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {
        if ( getPreModel().size() < 0 || getPreModel().size() > 3 ) {
            return new OlkCheckComponent( false, "合并行组件前置组件个数错误" );
        }
        if ( getPreModel().stream().filter( e -> e.getRunStatus() == 0 ).collect( Collectors.toList() ).size() > 0 ) {
            return new OlkCheckComponent( false, "前置组件未保存该组件无法保存，请配置" );
        }
        if ( truNode.getViewIds().size() < 1 ) {
            return new OlkCheckComponent( false, "选中字段不能为空" );
        }

        OlkFieldConcatComponent concatComponent = JsonUtil.gson().fromJson( JsonUtil.toJson( truNode.getParams() ), OlkFieldConcatComponent.class );
        //concatComponent.setViewNodes(node.getViewIds());
        List<String> tabList = concatComponent.getConcatTable();
        List<String> viewIds = new ArrayList<>();
        viewIds.addAll( truNode.getViewIds() );
        if ( concatComponent.getOperator() == null ) {
            return new OlkCheckComponent( false, "无字段关联配置" );
        }

        for ( Operand operand : concatComponent.getOperator() ) {
            if ( operand.getId() != null ) {
                if ( viewIds.indexOf( operand.getId() ) >= 0 ) {
                    viewIds.remove( operand.getId() );
                    if ( operand.getConcat() == null || operand.getConcat().size() != tabList.size() ) {
                        return new OlkCheckComponent( false, operand.getFieldName() + "字段关联配置不正确" );
                    }
                    for ( AggConcat aggConcat : operand.getConcat() ) {
                        if ( StringUtils.isBlank( aggConcat.getFieldName() ) ) {
                            return new OlkCheckComponent( false, operand.getFieldName() + "字段关联未配置" );
                        }
                    }
                }
            }
        }
        return new OlkCheckComponent( true, "success" );
    }

    @Override
    public void build( OlkNode truNode, TOlkModelElementDo fModelElementDo, TOlkModelDo model ) throws Exception {
        OlkFieldConcatComponent concatComponent = JsonUtil.gson().fromJson( JsonUtil.toJson( truNode.getParams() ), OlkFieldConcatComponent.class );
        concatComponent.setViewNodes( truNode.getViewIds() );

        //  Map<String, TOlkModelObjectDo> idObjectMap = getDatasource().stream().collect(Collectors.toMap(TOlkModelObjectDo::getObjectId, e -> e, (k1, k2) -> k1));
        Map<String, TOlkModelElementDo> idElementMap = getPreModel().stream().collect( Collectors.toMap( TOlkModelElementDo::getId, e -> e, ( k1, k2 ) -> k1 ) );
        Map<String, List<String>> fieldDos = new HashMap<>();
        if ( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {

            StringBuilder sql = new StringBuilder();

            //StringBuffer startsql = new StringBuffer();
            List<String> fieldNameList2 = new ArrayList<>();
            for ( int i = 0; i < concatComponent.getOperator().size(); i++ ) {
                if ( concatComponent.getOperator().size() > 0 &&
                        concatComponent.getViewNodes().contains( concatComponent.getOperator().get( i ).getId() ) ) {
                    for ( AggConcat aggConcat : concatComponent.getOperator().get( i ).getConcat() ) {
                        if ( fieldDos.keySet().contains( aggConcat.getId() ) ) {
                            if( i ==0 ){
                                fieldNameList2.add( aggConcat.getFieldName() );
                            }
                            List<String> value = fieldDos.get( aggConcat.getId() );
                            //  String fieldName = getDataFieldName(fModelElementDo.getElement(), info);
                            if ( StringUtils.isBlank( aggConcat.getFieldName() ) ) {
                                value.add( "null" );
                            }
                            else {
                                value.add( aggConcat.getFieldName() );
                            }
                            fieldDos.put( aggConcat.getId(), value );
                        }
                        else {
                            List<String> value = new ArrayList<>();
                            value.add( aggConcat.getFieldName() );
                            fieldDos.put( aggConcat.getId(), value );
                        }
                    }
                }
            }

//            for ( int j = 0; j < concatComponent.getConcatTable().size(); j++ ) {
//                TOlkModelElementDo tOlkModelElementDo = idElementMap.get( concatComponent.getConcatTable().get( j ) );
//                sql.append( tOlkModelElementDo.getTableSql() ).append( ",\r\n" );
//            }

            for ( int j = 0; j < concatComponent.getConcatTable().size(); j++ ) {
                TOlkModelElementDo tOlkModelElementDo = idElementMap.get
                        ( concatComponent.getConcatTable().get( j ) );
//                String newSql = getMatchedFrom( tOlkModelElementDo.getRunSql() );
//                if ( StringUtils.isBlank( newSql ) ) {
//                    newSql = tOlkModelElementDo.getElement();
//                }

                sql.append( "SELECT " ).append( String.join( " ,\r\n ", fieldDos.get( concatComponent.getConcatTable().get( j ) ) ) ).append( " FROM " );
                sql.append( "( \r\n" ).append( tOlkModelElementDo.getRunSql() ).append( "\r\n ) AS " ).append( tOlkModelElementDo.getElement() );
                if ( j != getPreModel().size() - 1 ) {
                    sql.append( "\r\n UNION ALL \r\n" );
                }
            }
            List<String> fields = fieldDos.get( concatComponent.getConcatTable().get( 0 ) ).stream().map( e -> fModelElementDo.getElement().concat( "." ).concat( e ) ).collect( Collectors.toList() );
//            if ( getNextModel() != null && getNextModel().size() > 0 ) {
//                sql.append( "SELECT " ).append( String.join( ",", fields ) ).append( " FROM " ).append( "(" );
//                sql.append( startsql.toString() ).append( ") AS " ).append( fModelElementDo.getElement() ).append( " WHERE 1=1 " );
//            }
//            else {
//                sql.append( startsql );
//            }

            List<TOlkModelFieldDo> filters = new ArrayList<>();
            if ( CollectionUtils.isNotEmpty( concatComponent.getOperator() ) ) {
                //sql.append( "SELECT " ).append( String.join( ",", fields ) ).append( " FROM " ).append( "(" );
                for ( int i = 0; i < concatComponent.getOperator().size(); i++ ) {
                    TOlkModelFieldDo elementVo = new TOlkModelFieldDo();
                    Operand elementDo = concatComponent.getOperator().get( i );
                    MyBeanUtils.copyBean2Bean( elementVo, elementDo );
                    if ( truNode.getViewIds().contains( elementDo.getId() ) ) {
                        elementVo.setIsSelect( 1 );
                    }
                    else {
                        elementVo.setIsSelect( 0 );
                    }
                    Map<String, String> constr = Maps.newLinkedHashMap();
                    elementDo.getConcat().stream().forEach( e -> {
                        constr.put( e.getFieldId(), e.getFieldName() );
                    } );
                    elementVo.setAggregation( JsonUtil.toJson( constr ).replace( "\n", "" ) );
                    elementVo.setId( ComUtil.genId() );
                    elementVo.setFilterSort( i );
                    elementVo.setExtendsId( fModelElementDo.getId() );
                    elementVo.setElementId( fModelElementDo.getId() );
                    elementVo.setTableId( fModelElementDo.getId() );
                    filters.add( elementVo );
                }
                //sql.append( ") as tmp2_" );
            }

            fModelElementDo.setRunSql( sql.toString() );
            fModelElementDo.setTableSql( "" );

            long macth = concatComponent.getOperator().stream().filter( e -> e.getConcat().size() == concatTable.size() ).count();
            concatComponent.setAllMatch( macth );
            concatComponent.setNotAllMatch( concatComponent.getOperator().size() - macth );
            truNode.getOperators().setFilters( filters );
        }
        else if ( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
            /*StringBuffer sql = new StringBuffer();
            StringBuffer startsql = new StringBuffer();
            for ( int i = 0; i < concatComponent.getOperator().size(); i++ ) {
                if ( concatComponent.getOperator().size() > 0 &&
                        concatComponent.getViewNodes().contains( concatComponent.getOperator().get( i ).getId() ) ) {
                    for ( AggConcat aggConcat : concatComponent.getOperator().get( i ).getConcat() ) {
                        if ( fieldDos.keySet().contains( aggConcat.getId() ) ) {
                            List<String> value = fieldDos.get( aggConcat.getId() );
                            //  String fieldName = getDataFieldName(fModelElementDo.getElement(), info);
                            if ( StringUtils.isBlank( aggConcat.getFieldName() ) ) {
                                value.add( "null" );
                            }
                            else {
                                value.add( aggConcat.getFieldName() );
                            }
                            fieldDos.put( aggConcat.getId(), value );
                        }
                        else {
                            List<String> value = new ArrayList<>();
                            value.add( aggConcat.getFieldName() );
                            fieldDos.put( aggConcat.getId(), value );
                        }
                    }
                }
            }
            for ( int j = 0; j < concatComponent.getConcatTable().size(); j++ ) {
                TOlkModelElementDo tOlkModelElementDo = idElementMap.get
                        ( concatComponent.getConcatTable().get( j ) );
                String newSql =  tOlkModelElementDo.getRunSql() ;
//                if ( StringUtils.isBlank( newSql ) ) {
//                    newSql = tOlkModelElementDo.getElement();
//                }
                startsql.append( "SELECT " ).append( String.join( ",", fieldDos.get( concatComponent.getConcatTable().get( j ) ) ) ).append( " FROM " );
                if ( tOlkModelElementDo.getElementType() == 1 ) {
                    startsql.append( newSql );
                }
                else {
                    startsql.append( "(" ).append( tOlkModelElementDo.getRunSql() ).append( ")" );
                }
                if ( j != getPreModel().size() - 1 ) {
                    startsql.append( "\r\n UNION ALL " );
                }
            }
            List<String> fields = fieldDos.get( concatComponent.getConcatTable().get( 0 ) ).stream().map( e -> fModelElementDo.getElement().concat( "." ).concat( e ) ).collect( Collectors.toList() );
            if ( getNextModel() != null && getNextModel().size() > 0 ) {
                sql.append( "SELECT " ).append( String.join( ",", fields ) ).append( " FROM " ).append( "(" );
                sql.append( startsql.toString() ).append( ") AS " ).append( fModelElementDo.getElement() ).append( " WHERE 1=1 " );
            }
            else {
                sql.append( startsql );
            }
            List<TOlkModelFieldDo> filters = new ArrayList<>();
            if ( CollectionUtils.isNotEmpty( concatComponent.getOperator() ) ) {
                for ( int i = 0; i < concatComponent.getOperator().size(); i++ ) {
                    TOlkModelFieldDo elementVo = new TOlkModelFieldDo();
                    Operand elementDo = concatComponent.getOperator().get( i );
                    MyBeanUtils.copyBean2Bean( elementVo, elementDo );
                    if ( truNode.getViewIds().contains( elementDo.getId() ) ) {
                        elementVo.setIsSelect( 1 );
                    }
                    else {
                        elementVo.setIsSelect( 0 );
                    }
                    Map<String, String> constr = Maps.newLinkedHashMap();
                    elementDo.getConcat().stream().forEach( e -> {
                        constr.put( e.getFieldId(), e.getFieldName() );
                    } );
                    elementVo.setAggregation( JsonUtil.toJson( constr ).replace( "\n", "" ) );
                    elementVo.setId( ComUtil.genId() );
                    elementVo.setFilterSort( i );
                    elementVo.setExtendsId( fModelElementDo.getId() );
                    elementVo.setElementId( fModelElementDo.getId() );
                    elementVo.setTableId( fModelElementDo.getId() );
                    filters.add( elementVo );
                }
            }
//            concatComponent.getOperator().stream().forEach(elementDo -> {
//
//            });
            long macth = concatComponent.getOperator().stream().filter( e -> e.getConcat().size() == concatTable.size() ).count();
            concatComponent.setAllMatch( macth );
            concatComponent.setNotAllMatch( concatComponent.getOperator().size() - macth );
            truNode.getOperators().setFilters( filters );
            fModelElementDo.setRunSql( sql.toString() );*/

            StringBuffer sql = new StringBuffer();
            //StringBuffer startsql = new StringBuffer();
            for ( int i = 0; i < concatComponent.getOperator().size(); i++ ) {
                if ( concatComponent.getOperator().size() > 0 &&
                        concatComponent.getViewNodes().contains( concatComponent.getOperator().get( i ).getId() ) ) {
                    for ( AggConcat aggConcat : concatComponent.getOperator().get( i ).getConcat() ) {
                        if ( fieldDos.keySet().contains( aggConcat.getId() ) ) {
                            List<String> value = fieldDos.get( aggConcat.getId() );
                            //  String fieldName = getDataFieldName(fModelElementDo.getElement(), info);
                            if ( StringUtils.isBlank( aggConcat.getFieldName() ) ) {
                                value.add( "null" );
                            }
                            else {
                                value.add( aggConcat.getFieldName() );
                            }
                            fieldDos.put( aggConcat.getId(), value );
                        }
                        else {
                            List<String> value = new ArrayList<>();
                            value.add( aggConcat.getFieldName() );
                            fieldDos.put( aggConcat.getId(), value );
                        }
                    }
                }
            }

            for ( int j = 0; j < concatComponent.getConcatTable().size(); j++ ) {
                TOlkModelElementDo tOlkModelElementDo = idElementMap.get
                        ( concatComponent.getConcatTable().get( j ) );
//                String newSql = getMatchedFrom( tOlkModelElementDo.getRunSql() );
//                if ( StringUtils.isBlank( newSql ) ) {
//                    newSql = tOlkModelElementDo.getElement();
//                }

                sql.append( "SELECT " ).append( String.join( " ,\r\n ", fieldDos.get( concatComponent.getConcatTable().get( j ) ) ) ).append( " FROM \r\n" );
                sql.append( "( " ).append( tOlkModelElementDo.getRunSql() ).append( " ) " );
//                if ( tOlkModelElementDo.getElementType() == 1 ) {
//                    sql.append( newSql );
//                }
//                else {
//                    sql.append( "(" ).append( tOlkModelElementDo.getRunSql() ).append( ")" );
//                }
                if ( j != getPreModel().size() - 1 ) {
                    sql.append( "\r\n UNION ALL \r\n" );
                }
            }
            List<String> fields = fieldDos.get( concatComponent.getConcatTable().get( 0 ) ).stream().map( e -> fModelElementDo.getElement().concat( "." ).concat( e ) ).collect( Collectors.toList() );
//            if ( getNextModel() != null && getNextModel().size() > 0 ) {
//                sql.append( "SELECT " ).append( String.join( ",", fields ) ).append( " FROM " ).append( "(" );
//                sql.append( startsql.toString() ).append( ") AS " ).append( fModelElementDo.getElement() ).append( " WHERE 1=1 " );
//            }
//            else {
//                sql.append( startsql );
//            }

            List<TOlkModelFieldDo> filters = new ArrayList<>();
            if ( CollectionUtils.isNotEmpty( concatComponent.getOperator() ) ) {
                //sql.append( "SELECT " ).append( String.join( ",", fields ) ).append( " FROM " ).append( "(" );
                for ( int i = 0; i < concatComponent.getOperator().size(); i++ ) {
                    TOlkModelFieldDo elementVo = new TOlkModelFieldDo();
                    Operand elementDo = concatComponent.getOperator().get( i );
                    MyBeanUtils.copyBean2Bean( elementVo, elementDo );
                    if ( truNode.getViewIds().contains( elementDo.getId() ) ) {
                        elementVo.setIsSelect( 1 );
                    }
                    else {
                        elementVo.setIsSelect( 0 );
                    }
                    Map<String, String> constr = Maps.newLinkedHashMap();
                    elementDo.getConcat().stream().forEach( e -> {
                        constr.put( e.getFieldId(), e.getFieldName() );
                    } );
                    elementVo.setAggregation( JsonUtil.toJson( constr ).replace( "\n", "" ) );
                    elementVo.setId( ComUtil.genId() );
                    elementVo.setFilterSort( i );
                    elementVo.setExtendsId( fModelElementDo.getId() );
                    elementVo.setElementId( fModelElementDo.getId() );
                    elementVo.setTableId( fModelElementDo.getId() );
                    filters.add( elementVo );
                }
                //sql.append( ") as tmp2_" );
            }
            sql.append( "\r\n" );

            long macth = concatComponent.getOperator().stream().filter( e -> e.getConcat().size() == concatTable.size() ).count();
            concatComponent.setAllMatch( macth );
            concatComponent.setNotAllMatch( concatComponent.getOperator().size() - macth );
            truNode.getOperators().setFilters( filters );

            fModelElementDo.setRunSql( sql.toString() );
            fModelElementDo.setTableSql( "" );

        }
        else {
            StringBuffer sql = new StringBuffer();
            //StringBuffer startsql = new StringBuffer();
            for ( int i = 0; i < concatComponent.getOperator().size(); i++ ) {
                if ( concatComponent.getOperator().size() > 0 &&
                        concatComponent.getViewNodes().contains( concatComponent.getOperator().get( i ).getId() ) ) {
                    for ( AggConcat aggConcat : concatComponent.getOperator().get( i ).getConcat() ) {
                        if ( fieldDos.keySet().contains( aggConcat.getId() ) ) {
                            List<String> value = fieldDos.get( aggConcat.getId() );
                            //  String fieldName = getDataFieldName(fModelElementDo.getElement(), info);
                            if ( StringUtils.isBlank( aggConcat.getFieldName() ) ) {
                                value.add( "null" );
                            }
                            else {
                                value.add( aggConcat.getFieldName() );
                            }
                            fieldDos.put( aggConcat.getId(), value );
                        }
                        else {
                            List<String> value = new ArrayList<>();
                            value.add( aggConcat.getFieldName() );
                            fieldDos.put( aggConcat.getId(), value );
                        }
                    }
                }
            }
            sql.append( "CREATE VIEW " ).append( fModelElementDo.getElement() ).append( " AS " );
            for ( int j = 0; j < concatComponent.getConcatTable().size(); j++ ) {
                TOlkModelElementDo tOlkModelElementDo = idElementMap.get
                        ( concatComponent.getConcatTable().get( j ) );
//                String newSql = getMatchedFrom( tOlkModelElementDo.getRunSql() );
//                if ( StringUtils.isBlank( newSql ) ) {
//                    newSql = tOlkModelElementDo.getElement();
//                }

                sql.append( "\r\nSELECT " ).append( String.join( " ,\r\n ", fieldDos.get( concatComponent.getConcatTable().get( j ) ) ) ).append( " FROM " );
                sql.append( tOlkModelElementDo.getElement() );
//                if ( tOlkModelElementDo.getElementType() == 1 ) {
//                    sql.append( newSql );
//                }
//                else {
//                    sql.append( "(" ).append( tOlkModelElementDo.getRunSql() ).append( ")" );
//                }
                if ( j != getPreModel().size() - 1 ) {
                    sql.append( "\r\n UNION ALL" );
                }
            }
            List<String> fields = fieldDos.get( concatComponent.getConcatTable().get( 0 ) ).stream().map( e -> fModelElementDo.getElement().concat( "." ).concat( e ) ).collect( Collectors.toList() );
//            if ( getNextModel() != null && getNextModel().size() > 0 ) {
//                sql.append( "SELECT " ).append( String.join( ",", fields ) ).append( " FROM " ).append( "(" );
//                sql.append( startsql.toString() ).append( ") AS " ).append( fModelElementDo.getElement() ).append( " WHERE 1=1 " );
//            }
//            else {
//                sql.append( startsql );
//            }

            List<TOlkModelFieldDo> filters = new ArrayList<>();
            if ( CollectionUtils.isNotEmpty( concatComponent.getOperator() ) ) {
                //sql.append( "SELECT " ).append( String.join( ",", fields ) ).append( " FROM " ).append( "(" );
                for ( int i = 0; i < concatComponent.getOperator().size(); i++ ) {
                    TOlkModelFieldDo elementVo = new TOlkModelFieldDo();
                    Operand elementDo = concatComponent.getOperator().get( i );
                    MyBeanUtils.copyBean2Bean( elementVo, elementDo );
                    if ( truNode.getViewIds().contains( elementDo.getId() ) ) {
                        elementVo.setIsSelect( 1 );
                    }
                    else {
                        elementVo.setIsSelect( 0 );
                    }
                    Map<String, String> constr = Maps.newLinkedHashMap();
                    elementDo.getConcat().stream().forEach( e -> {
                        constr.put( e.getFieldId(), e.getFieldName() );
                    } );
                    elementVo.setAggregation( JsonUtil.toJson( constr ).replace( "\n", "" ) );
                    elementVo.setId( ComUtil.genId() );
                    elementVo.setFilterSort( i );
                    elementVo.setExtendsId( fModelElementDo.getId() );
                    elementVo.setElementId( fModelElementDo.getId() );
                    elementVo.setTableId( fModelElementDo.getId() );
                    filters.add( elementVo );
                }
                //sql.append( ") as tmp2_" );
            }
            sql.append( ";\r\n" );

            long macth = concatComponent.getOperator().stream().filter( e -> e.getConcat().size() == concatTable.size() ).count();
            concatComponent.setAllMatch( macth );
            concatComponent.setNotAllMatch( concatComponent.getOperator().size() - macth );
            truNode.getOperators().setFilters( filters );

            fModelElementDo.setRunSql( "" );
            fModelElementDo.setTableSql( sql.toString() );
        }
    }

    @Override
    public List<TOlkModelFieldDo> relExtends( TOlkModelElementRelDo elementInfo ) throws Exception {
        List<TOlkModelFieldDo> list = new ArrayList<>();
        Map<String, TOlkModelComponentDo> idcomponentDoMap = getComponents().stream().collect( Collectors.toMap( TOlkModelComponentDo::getId, e -> e ) );
        Map<String, TOlkModelElementDo> idElementMap = getModel().stream().collect( Collectors.toMap( TOlkModelElementDo::getId, e -> e ) );
        TOlkModelComponentDo componentDo = idcomponentDoMap.get( idElementMap.get( elementInfo.getEndElementId() ).getTcId() );
        getExtendsDos().stream().filter( y -> y.getElementId().equals( y.getExtendsId() ) ).forEach( tOlkModelFieldDo -> {
            tOlkModelFieldDo.setId( ComUtil.genId() );
            if ( tOlkModelFieldDo.getIsSelect() == 0 ) {
                tOlkModelFieldDo.setIsSelect( -1 );
            }
            tOlkModelFieldDo.setAggregation( "" );
            tOlkModelFieldDo.setTableAlias( idElementMap.get( elementInfo.getStartElementId() ).getElement() );
            tOlkModelFieldDo.setElementId( elementInfo.getEndElementId() );
            tOlkModelFieldDo.setExtendsId( elementInfo.getStartElementId() );
            list.add( tOlkModelFieldDo );

        } );
        return list;
    }
}
