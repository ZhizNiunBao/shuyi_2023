package cn.bywin.business.bean.analysis.template;

import static cn.bywin.business.bean.analysis.TruComponentEnum.FieldConcat_COMPONENT;
import static cn.bywin.business.util.analysis.SqlFilterUtils.getMatchedFrom;

import cn.bywin.business.bean.analysis.TruBaseComponenT;
import cn.bywin.business.bean.analysis.TruCheckComponent;
import cn.bywin.business.bean.bydb.TTruModelComponentDo;
import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.bydb.TTruModelElementDo;
import cn.bywin.business.bean.bydb.TTruModelElementRelDo;
import cn.bywin.business.bean.bydb.TTruModelFieldDo;
import cn.bywin.business.bean.view.bydb.AggConcat;
import cn.bywin.business.bean.view.bydb.Operand;
import cn.bywin.business.bean.view.bydb.TruNode;
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
public class TruFieldConcatComponent extends TruBaseComponenT {


    private List<Operand> operator = new ArrayList<>();
    private List<String> concatTable = new ArrayList<>();
    private long allMatch;
    private long notAllMatch;
    private List<String> viewNodes = new ArrayList<>();

    public TruFieldConcatComponent() {
    }

    @Override
    public List<TTruModelFieldDo> getShowField( List<TTruModelFieldDo> fieldDos ) throws Exception {
        List<TTruModelFieldDo> disData = new ArrayList<>();
        fieldDos.stream().forEach( e -> {
            if ( e.getIsSelect() == 1 && e.getElementId().equals( e.getExtendsId() ) ) {
                e.setFieldName( e.getFieldAlias() );
                disData.add( e );
            }
        } );
        List<TTruModelFieldDo> fieldDoList = disData.stream().sorted( Comparator.comparing( TTruModelFieldDo::getFilterSort ) ).collect( Collectors.toList() );

        return fieldDoList;
    }

    @Override
    public TruNode init( TruNode truNode, TTruModelElementDo fModelElementDo ) throws Exception {
        truNode.getOperators().getFields().stream().forEach( e -> {
            e.setElementId( e.getExtendsId() );
        } );
        Map<String, TTruModelFieldDo> fieldMap = truNode.getOperators().getFields().stream().collect( Collectors.toMap( x -> x.getId(), x -> x ) );
        Map<String, TTruModelFieldDo> idElementMap = truNode.getOperators().getFields().stream().collect( Collectors.toMap( TTruModelFieldDo::getId, e -> e, ( k1, k2 ) -> k1 ) );

        concatTable = getPreModel().stream().sorted( Comparator.comparing( TTruModelElementDo::getCreatedTime ) ).map( e -> e.getId() ).collect( Collectors.toList() );
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
                        TTruModelFieldDo tTruModelFieldDo = idElementMap.get(s);
                        aggConcat.setFieldId(s);
                        aggConcat.setId(tTruModelFieldDo.getElementId());
                        if (StringUtils.isNotBlank(String.valueOf(config.get(s)))) {
                            aggConcat.setFieldName(String.valueOf(config.get(s)));
                        }
                        aggConcat.setColumnType(tTruModelFieldDo.getColumnType());


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
                        TTruModelFieldDo fieldDo = fieldMap.get( fieldId );
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
                List<TTruModelFieldDo> fieldIndex = truNode.getOperators().getFields().stream().filter( e -> e.getElementId().equals( concatTable.get( 0 ) ) ).collect( Collectors.toList() );
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
//                    List<TTruModelFieldDo> fieldDos = node.getOperators().getFields().stream().filter(x -> x.getExtendsId().equals(concatTable.get(finalI))).collect(Collectors.toList());
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
                TTruModelFieldDo leftField = fieldMap.get( e.getId() );
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
                    TTruModelFieldDo fieldDo = null;
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
    public boolean changeSameFieldName( TruNode truNode, TTruModelElementDo fModelElementDo ) throws Exception {

        TruFieldConcatComponent concatComponent = JsonUtil.gson().fromJson( JsonUtil.toJson( truNode.getParams() ), TruFieldConcatComponent.class );
        concatComponent.setViewNodes( truNode.getViewIds() );
        //Map<String, TTruModelElementDo> idElementMap = getPreModel().stream().collect(Collectors.toMap(TTruModelElementDo::getId, e -> e, (k1, k2) -> k1));

        List<TTruModelFieldDo> fieldList = truNode.getOperators().getFields();
        for ( TTruModelFieldDo fieldDo : fieldList ) {
            fieldDo.setIsSelect( 0 );
            fieldDo.setFilterConfig( null );
            fieldDo.setFilterValue( null );
        }
        Map<String, TTruModelFieldDo> fieldMap = fieldList.stream().collect( Collectors.toMap( x -> x.getId(), x -> x ) );

        List<Operand> operator = concatComponent.getOperator();
        for ( Operand operand : operator ) {
            if ( concatComponent.getViewNodes().indexOf( operand.getId() ) < 0 ) continue;
            TTruModelFieldDo fieldDo = fieldMap.get( operand.getId() );
            HashMap<String, Object> filterConfig = new HashMap<>();
            for ( int i = 1; i < operand.getConcat().size(); i++ ) {
                AggConcat aggConcat = operand.getConcat().get( i );
                for ( TTruModelFieldDo tmpDo : fieldList ) {
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
//                    TTruModelFieldDo tmp1 = fieldMap.get( aggConcat.getFieldId() );
//                    filterConfig.put( tmp1.getId(), tmp1.getFieldName() );
////                        }
//                    tmp1.setFilterValue( fieldDo.getFieldName() );
//                }
//            }
            fieldDo.setFilterConfig( JsonUtil.toJson( filterConfig ) );
        }
        /*for ( int i = 0; i < operator.size(); i++ ) {
                if (concatComponent.getViewNodes().contains( concatComponent.getOperator().get( i ).getId() ) ) {
                    TTruModelFieldDo fieldDo = fieldMap.get( operator.get( i ).getId() );
                    Map<String, String> filterConfig = new HashMap<>();
                    for ( AggConcat aggConcat : operator.get( i ).getConcat() ) {

                        if( !fieldDo.getId().equals(  aggConcat.getFieldId() )) {
                            TTruModelFieldDo tmp1 = fieldMap.get( aggConcat.getFieldId() );

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

//            List<TTruModelFieldDo> filters = new ArrayList<>();
//            if ( CollectionUtils.isNotEmpty( concatComponent.getOperator() ) ) {
//                for ( int i = 0; i < concatComponent.getOperator().size(); i++ ) {
//                    TTruModelFieldDo elementVo = new TTruModelFieldDo();
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
    public TruCheckComponent check( TruNode truNode, TTruModelElementDo fModelElementDo ) throws Exception {
        if ( getPreModel().size() < 0 || getPreModel().size() > 3 ) {
            return new TruCheckComponent( false, "合并行组件前置组件个数错误" );
        }
        if ( getPreModel().stream().filter( e -> e.getRunStatus() == 0 ).collect( Collectors.toList() ).size() > 0 ) {
            return new TruCheckComponent( false, "前置组件未保存该组件无法保存，请配置" );
        }
        if ( truNode.getViewIds().size() < 1 ) {
            return new TruCheckComponent( false, "选中字段不能为空" );
        }

        TruFieldConcatComponent concatComponent = JsonUtil.gson().fromJson( JsonUtil.toJson( truNode.getParams() ), TruFieldConcatComponent.class );
        //concatComponent.setViewNodes(node.getViewIds());
        List<String> tabList = concatComponent.getConcatTable();
        List<String> viewIds = new ArrayList<>();
        viewIds.addAll( truNode.getViewIds() );
        if ( concatComponent.getOperator() == null ) {
            return new TruCheckComponent( false, "无字段关联配置" );
        }

        for ( Operand operand : concatComponent.getOperator() ) {
            if ( operand.getId() != null ) {
                if ( viewIds.indexOf( operand.getId() ) >= 0 ) {
                    viewIds.remove( operand.getId() );
                    if ( operand.getConcat() == null || operand.getConcat().size() != tabList.size() ) {
                        return new TruCheckComponent( false, operand.getFieldName() + "字段关联配置不正确" );
                    }
                    for ( AggConcat aggConcat : operand.getConcat() ) {
                        if ( StringUtils.isBlank( aggConcat.getFieldName() ) ) {
                            return new TruCheckComponent( false, operand.getFieldName() + "字段关联未配置" );
                        }
                    }
                }
            }
        }
        return new TruCheckComponent( true, "success" );
    }

    @Override
    public void build( TruNode truNode, TTruModelElementDo fModelElementDo, TTruModelDo model ) throws Exception {
        TruFieldConcatComponent concatComponent = JsonUtil.gson().fromJson( JsonUtil.toJson( truNode.getParams() ), TruFieldConcatComponent.class );
        concatComponent.setViewNodes( truNode.getViewIds() );

        //  Map<String, TTruModelObjectDo> idObjectMap = getDatasource().stream().collect(Collectors.toMap(TTruModelObjectDo::getObjectId, e -> e, (k1, k2) -> k1));
        Map<String, TTruModelElementDo> idElementMap = getPreModel().stream().collect( Collectors.toMap( TTruModelElementDo::getId, e -> e, ( k1, k2 ) -> k1 ) );
        Map<String, List<String>> fieldDos = new HashMap<>();
        if ( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
            StringBuffer sql = new StringBuffer();
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
                TTruModelElementDo tTruModelElementDo = idElementMap.get
                        ( concatComponent.getConcatTable().get( j ) );
                String newSql = getMatchedFrom( tTruModelElementDo.getRunSql() );
                if ( StringUtils.isBlank( newSql ) ) {
                    newSql = tTruModelElementDo.getElement();
                }
                startsql.append( "SELECT " ).append( String.join( ",", fieldDos.get( concatComponent.getConcatTable().get( j ) ) ) ).append( " FROM " );
                if ( tTruModelElementDo.getElementType() == 1 ) {
                    startsql.append( newSql );
                }
                else {
                    startsql.append( "(" ).append( tTruModelElementDo.getRunSql() ).append( ")" );
                }
                if ( j != getPreModel().size() - 1 ) {
                    startsql.append( " UNION ALL " );
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
            List<TTruModelFieldDo> filters = new ArrayList<>();
            if ( CollectionUtils.isNotEmpty( concatComponent.getOperator() ) ) {
                for ( int i = 0; i < concatComponent.getOperator().size(); i++ ) {
                    TTruModelFieldDo elementVo = new TTruModelFieldDo();
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
            fModelElementDo.setRunSql( sql.toString() );
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
                TTruModelElementDo tTruModelElementDo = idElementMap.get
                        ( concatComponent.getConcatTable().get( j ) );
                String newSql = getMatchedFrom( tTruModelElementDo.getRunSql() );
                if ( StringUtils.isBlank( newSql ) ) {
                    newSql = tTruModelElementDo.getElement();
                }

                sql.append( "\r\nSELECT " ).append( String.join( " ,\r\n ", fieldDos.get( concatComponent.getConcatTable().get( j ) ) ) ).append( " FROM " );
                sql.append( tTruModelElementDo.getElement() );
//                if ( tTruModelElementDo.getElementType() == 1 ) {
//                    sql.append( newSql );
//                }
//                else {
//                    sql.append( "(" ).append( tTruModelElementDo.getRunSql() ).append( ")" );
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

            List<TTruModelFieldDo> filters = new ArrayList<>();
            if ( CollectionUtils.isNotEmpty( concatComponent.getOperator() ) ) {
                //sql.append( "SELECT " ).append( String.join( ",", fields ) ).append( " FROM " ).append( "(" );
                for ( int i = 0; i < concatComponent.getOperator().size(); i++ ) {
                    TTruModelFieldDo elementVo = new TTruModelFieldDo();
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
    public List<TTruModelFieldDo> relExtends( TTruModelElementRelDo elementInfo ) throws Exception {
        List<TTruModelFieldDo> list = new ArrayList<>();
        Map<String, TTruModelComponentDo> idcomponentDoMap = getComponents().stream().collect( Collectors.toMap( TTruModelComponentDo::getId, e -> e ) );
        Map<String, TTruModelElementDo> idElementMap = getModel().stream().collect( Collectors.toMap( TTruModelElementDo::getId, e -> e ) );
        TTruModelComponentDo componentDo = idcomponentDoMap.get( idElementMap.get( elementInfo.getEndElementId() ).getTcId() );
        getExtendsDos().stream().filter( y -> y.getElementId().equals( y.getExtendsId() ) ).forEach( tTruModelFieldDo -> {
            tTruModelFieldDo.setId( ComUtil.genId() );
            if ( tTruModelFieldDo.getIsSelect() == 0 ) {
                tTruModelFieldDo.setIsSelect( -1 );
            }
            tTruModelFieldDo.setAggregation( "" );
            tTruModelFieldDo.setTableAlias( idElementMap.get( elementInfo.getStartElementId() ).getElement() );
            tTruModelFieldDo.setElementId( elementInfo.getEndElementId() );
            tTruModelFieldDo.setExtendsId( elementInfo.getStartElementId() );
            list.add( tTruModelFieldDo );

        } );
        return list;
    }
}
