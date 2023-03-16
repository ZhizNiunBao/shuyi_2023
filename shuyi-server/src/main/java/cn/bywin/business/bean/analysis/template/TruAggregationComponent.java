package cn.bywin.business.bean.analysis.template;

import static cn.bywin.business.bean.analysis.TruComponentEnum.GROUP_COMPONENT;
import static cn.bywin.business.bean.analysis.TruComponentEnum.Join_COMPONENT;
import static cn.bywin.business.util.analysis.SqlFilterUtils.getMatchedFrom;

import cn.bywin.business.bean.analysis.TruBaseComponenT;
import cn.bywin.business.bean.analysis.TruCheckComponent;
import cn.bywin.business.bean.bydb.TTruModelComponentDo;
import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.bydb.TTruModelElementDo;
import cn.bywin.business.bean.bydb.TTruModelElementRelDo;
import cn.bywin.business.bean.bydb.TTruModelFieldDo;
import cn.bywin.business.bean.view.bydb.TruNode;
import cn.bywin.business.beanparse.TruSrJdbcWhereCondParse;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description 聚合组件配置
 * @Author wangh
 * @Date 2021-10-23
 */
@Data
public class TruAggregationComponent extends TruBaseComponenT {


    private List<TTruModelFieldDo> field = new ArrayList<>();
    private List<TTruModelFieldDo> group = new ArrayList<>();

    public TruAggregationComponent() {
    }

    @Override
    public List<TTruModelFieldDo> getShowField(List<TTruModelFieldDo> fieldDos) throws Exception {
        List<TTruModelFieldDo> disData = new ArrayList<>();
        fieldDos.stream().forEach(e -> {
            if (e.getIsSelect() == 1 && e.getElementId().equals(e.getExtendsId())) {
                e.setFieldName(e.getFieldAlias());
                e.setFilterSort(1);
                disData.add(e);
            }
        });
        List<TTruModelFieldDo> fieldsLists = new ArrayList<>();
        List<TTruModelFieldDo> fieldDoList = disData.stream().filter(c -> !c.getColumnType().equals("String")).sorted(Comparator.comparing(TTruModelFieldDo::getColumnType)).collect(Collectors.toList());
        disData.forEach(e -> {
            if (e.getColumnType().equals("String")) {
                e.setFilterSort(0);
                fieldsLists.add(e);
            }
        });
        fieldsLists.addAll(fieldDoList);
        return fieldsLists;
    }

    @Override
    public TruNode init( TruNode truNode, TTruModelElementDo fModelElementDo) throws Exception {

        Map<String, Object> params = new HashMap<>(5);
        List<TTruModelFieldDo> fieldsList = truNode.getOperators().getFields().stream().filter( e -> !e.getElementId().equals(e.getExtendsId())).collect(Collectors.toList());
        List<TTruModelFieldDo> orgFieldList = fieldsList.stream().filter( x -> StringUtils.isBlank( x.getAggregation() ) ).collect( Collectors.toList() );
//        List<TTruModelFieldDo> fieldDoList = fieldsList.stream().filter(c -> !c.getColumnType().equals("String")).sorted(Comparator.comparing(TTruModelFieldDo::getColumnType)).collect(Collectors.toList());
//        fieldsList.forEach(e -> {
//            if (e.getColumnType().equals("String")) {
//                fieldsLists.add(e);
//            }
//        });
        //fieldsLists.addAll(fieldDoList);
        truNode.getOperators().setFields(orgFieldList);
        fieldsList.removeAll( orgFieldList );
        for ( TTruModelFieldDo fieldDo : fieldsList ) {
            if( "group_by_field".equals( fieldDo.getAggregation() ) ){
                group.add(fieldDo);
                //fieldDo.setFieldType( "BIGINT" );
            }
            else{
                field.add(fieldDo);
//                if( "distinct_count".equalsIgnoreCase( fieldDo.getAggregation() ) ||  "count".equalsIgnoreCase( fieldDo.getAggregation() ) ){
//                    fieldDo.setFieldType( "BIGINT" );
//                    fieldDo.setColumnType( "Long" );
//                }
            }
        }

//        if (node.getOperators().getFilters().size() > 0) {
//            node.getOperators().getFilters().stream().forEach(e -> {
//                if (StringUtils.isNotBlank(e.getAggregation())) {
//                    field.add(e);
//                } else {
//                    group.add(e);
//                }
//            });
//        }
        params.put("field", field);
        params.put("group", group);
        truNode.setParams(params);
        truNode.setType(GROUP_COMPONENT.getComponentName());
        return truNode;
    }

    @Override
    public  boolean changeSameFieldName( TruNode truNode, TTruModelElementDo fModelElementDo) throws Exception{
        TruAggregationComponent aggregationComponent = JsonUtil.gson().fromJson(JsonUtil.toJson( truNode.getParams()), TruAggregationComponent.class);
        List<TTruModelFieldDo> field = aggregationComponent.getField();
        List<TTruModelFieldDo> group = aggregationComponent.getGroup();

        List<TTruModelFieldDo> fieldList = truNode.getOperators().getFields();
        HashMap<String,TTruModelFieldDo> fieldMap = new HashMap<>();
        for ( TTruModelFieldDo fieldDo : fieldList ) {
            fieldMap.put( fieldDo.getFieldName(), fieldDo );
        }

        for ( TTruModelFieldDo fieldDo : field ) {
            if( "distinct_count".equalsIgnoreCase( fieldDo.getAggregation() ) ){
                fieldDo.setFieldType( "BIGINT" );
                fieldDo.setColumnType( "BIGINT" );
                if( !fieldDo.getFieldAlias().endsWith( "_dist_cnt" )){
                    fieldDo.setFieldAlias( fieldDo.getFieldName().concat(  "_dist_cnt" ) );
                }
                TTruModelFieldDo tmp = fieldMap.get( fieldDo.getFieldName() );
                if( tmp != null) {
                    fieldDo.setFieldExpr( tmp.getFieldExpr() + "唯一总记录数" );
                }
            }
            if( "count".equalsIgnoreCase( fieldDo.getAggregation() ) ){
                fieldDo.setFieldType( "BIGINT" );
                fieldDo.setColumnType( "BIGINT" );
                if( !fieldDo.getFieldAlias().endsWith( "_cnt" )){
                    fieldDo.setFieldAlias( fieldDo.getFieldName().concat(  "_cnt" ) );
                }
                TTruModelFieldDo tmp = fieldMap.get( fieldDo.getFieldName() );
                if( tmp != null) {
                    fieldDo.setFieldExpr( tmp.getFieldExpr() + "总记录数" );
                }
            }
            List<String> tjList = new ArrayList<>();
            tjList.addAll( Arrays.asList( "max","min","sum","avg" ) );
            List<String> exprList = new ArrayList<>();
            exprList.addAll( Arrays.asList( "最大%s","最小%s","%s总和","平均%s" ) );
            for ( int i = 0; i < tjList.size(); i++ ) {
                String tj = tjList.get( i );
                if( tj.equalsIgnoreCase( fieldDo.getAggregation() )) {
                    Pattern pattern = Pattern.compile( "_" + tj + "\\d*$", Pattern.CASE_INSENSITIVE );
                    Matcher matcher = pattern.matcher( fieldDo.getFieldAlias() );
                    if ( !matcher.find() ) {
                        fieldDo.setFieldAlias( fieldDo.getFieldName().concat( "_" ).concat( tj ) );
                    }
                    TTruModelFieldDo tmp = fieldMap.get( fieldDo.getFieldName() );
                    if( tmp != null) {
                        fieldDo.setFieldExpr( String.format( exprList.get( i ) , tmp.getFieldExpr() ) );
                    }
                }
            }
        }
        for ( TTruModelFieldDo fieldDo : group ) {
            fieldDo.setAggregation( "group_by_field" );
        }

        List<TTruModelFieldDo> fieldsList = new ArrayList<>();
        fieldsList.addAll(  field );
        fieldsList.addAll(  group );
        Map<String,String> nameMap = new HashedMap();
        for ( TTruModelFieldDo fieldDo : fieldsList ) {
            String alias = fieldDo.getFieldAlias();
            int cnt = 1;
            if ( nameMap.containsKey( alias ) ) {
                int pos = alias.length();
                char[] chars = alias.toCharArray();
                for ( int i = chars.length - 1; i >= 0; i-- ) {
                    if ( chars[i] > '0' && chars[i] < '9' ) {
                        pos = i;
                    }
                    else {
                        break;
                    }
                }
                String fn = alias.substring( 0, pos );
                int num = 1;
                while ( true ) {
                    alias = String.format( "%s%d", fn, num++ );
                    //logger.info( "{}:{}", fn, alias );
                    if ( !nameMap.containsKey( alias ) ) {
                        fieldDo.setFieldAlias( alias );
                        nameMap.put( alias, alias );
                        break;
                    }
                }
            }
            else {
                nameMap.put( alias, alias );
            }
        }
        truNode.getParams().put("field", field);
        truNode.getParams().put("group", group);
        return true;
    }

    @Override
    public TruCheckComponent check( TruNode truNode, TTruModelElementDo fModelElementDo) throws Exception {
        TruAggregationComponent aggregationComponent = JsonUtil.gson().fromJson(JsonUtil.toJson( truNode.getParams()), TruAggregationComponent.class);
        List<TTruModelFieldDo> field = aggregationComponent.getField();
        List<TTruModelFieldDo> group = aggregationComponent.getGroup();
        if (getPreModel().size() != 1) {
            return new TruCheckComponent(false, "聚合表个数错误");
        }
        if (getPreModel().stream().filter(e -> e.getRunStatus() == 0).collect(Collectors.toList()).size() > 0) {
            return new TruCheckComponent(false, "前置组件未保存该组件无法保存，请配置");
        }
        if (field == null || field.size() < 1) {
            return new TruCheckComponent(false, "聚合条件不能为空");
        }
        for( TTruModelFieldDo fieldDo : field ) {
            if( StringUtils.isBlank( fieldDo.getAggregation() )){
                return new TruCheckComponent(false, fieldDo.getFieldName()+ "聚合方式不能为空");
            }
        }
//        if (group == null || group.size() < 1) {
//            return new TruCheckComponent(false, "组合条件不能为空");
//        }
//        if (node.getViewIds().size() < 1) {
//            return new CheckComponent(false, "选中字段不能为空");
//        }
        return new TruCheckComponent(true, "success");
    }

    @Override
    public void build( TruNode truNode, TTruModelElementDo fModelElementDo, TTruModelDo  model) throws Exception {
        TruAggregationComponent aggregationComponent = JsonUtil.gson().fromJson(JsonUtil.toJson( truNode.getParams()), TruAggregationComponent.class);
        List<TTruModelFieldDo> field = aggregationComponent.getField();

        List<TTruModelFieldDo> group = aggregationComponent.getGroup();
        List<TTruModelFieldDo> filters = new ArrayList<>();


        List<String> alias = new ArrayList<>();
        if( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
            group.stream().forEach( e -> {
                Long count = alias.stream().filter( y -> y.equals( e.getFieldAlias() ) ).count();
                alias.add( e.getFieldAlias() );
                if ( count > 0 ) {
                    e.setFieldAlias( e.getFieldAlias().concat( "_" ).concat( String.valueOf( count ) ) );
                }
                e.setFilterSort( 0 );
                e.setAggregation( "" );
            } );
            field.stream().forEach( e -> {
                Long count = alias.stream().filter( y -> y.equals( e.getFieldAlias() ) ).count();
                alias.add( e.getFieldAlias() );
                if ( count > 0 ) {
                    e.setFieldAlias( e.getFieldAlias().concat( "_" ).concat( String.valueOf( count ) ) );
                }

                e.setFilterSort( 1 );
            } );
            group.addAll( field );
            StringBuffer sql = new StringBuffer();
            List<String> fields = new ArrayList<>();
            List<String> groups = new ArrayList<>();
            truNode.getOperators().getFields();
            TTruModelElementDo pre = preModel.get( 0 );
            String preSql = pre.getRunSql();
            String newsql = getMatchedFrom( preSql );
            Map<String, TTruModelFieldDo> idFieldMap = truNode.getOperators().getFields().stream().collect( Collectors.toMap( TTruModelFieldDo::getFieldAlias, e -> e ) );

            if ( group != null && group.size() > 0 ) {
                //组合字段
                for ( TTruModelFieldDo info : group ) {
                    String fieldName = TruSrJdbcWhereCondParse.getDataFieldName( info, idFieldMap );
                    StringBuffer sb = new StringBuffer();
                    if ( StringUtils.isBlank( info.getAggregation() ) ) {
                        sb.append( fieldName );
                        fields.add( fieldName.concat( " AS " ).concat( info.getFieldAlias() ) );
                        groups.add( sb.toString() );
                    }
                    else {
                        sb.append( fieldName );
                        sb.append( " AS " ).append( info.getFieldAlias() );
                        fields.add( sb.toString() );
                    }
                    info.setId( ComUtil.genId() );
                    info.setElementId( fModelElementDo.getId() );
                    info.setExtendsId( fModelElementDo.getId() );
                    info.setTableId( fModelElementDo.getId() );
                    filters.add( info );
                }
            }
            sql.append( "SELECT " );
            sql.append( String.join( ",", fields ) ).append( " FROM " ).append( newsql );
            if ( groups.size() > 0 ) {
                sql.append( " GROUP BY " ).append( String.join( ",", groups ) );
            }
            sql.append( " HAVING 1=1" );
            truNode.getOperators().setFilters( filters );
            fModelElementDo.setRunSql( sql.toString() );
        }
        else{
//            group.stream().forEach( e -> {
//                Long count = alias.stream().filter( y -> y.equals( e.getFieldAlias() ) ).count();
//                alias.add( e.getFieldAlias() );
//                if ( count > 0 ) {
//                    e.setFieldAlias( e.getFieldAlias().concat( "_" ).concat( String.valueOf( count ) ) );
//                }
//                e.setFilterSort( 0 );
//                e.setAggregation( "" );
//            } );
//            field.stream().forEach( e -> {
//                Long count = alias.stream().filter( y -> y.equals( e.getFieldAlias() ) ).count();
//                alias.add( e.getFieldAlias() );
//                if ( count > 0 ) {
//                    e.setFieldAlias( e.getFieldAlias().concat( "_" ).concat( String.valueOf( count ) ) );
//                }
//
//                e.setFilterSort( 1 );
//            } );
//            group.addAll( field );
            StringBuffer sql = new StringBuffer();
            List<String> fields = new ArrayList<>();
            List<String> groups = new ArrayList<>();
            truNode.getOperators().getFields();
            TTruModelElementDo pre = preModel.get( 0 );
            //String preSql = pre.getRunSql();
            //String newsql = getMatchedFrom( preSql );
            //Map<String, TTruModelFieldDo> idFieldMap = node.getOperators().getFields().stream().collect( Collectors.toMap( TTruModelFieldDo::getFieldAlias, e -> e ) );
            sql.append( "CREATE VIEW ").append( fModelElementDo.getElement() ).append(  " AS SELECT \r\n" );

            if ( field != null && field.size() > 0 ) {
                //分组字段
                for ( TTruModelFieldDo info : field ) {
                    //String fieldName = getDataFieldName( info, idFieldMap );
                    StringBuffer sb = new StringBuffer();

//                    if( "distinct_count".equalsIgnoreCase( info.getAggregation() ) ||  "count".equalsIgnoreCase( info.getAggregation() ) ){
//                        info.setFieldType( "BIGINT" );
//                        info.setColumnType( "BIGINT" );
//                    }

                    if( "distinct_count".equalsIgnoreCase( info.getAggregation() )){
                        sb.append( "count( distinct " );
                        sb.append( info.getFieldName() ).append( " ) AS " );
                        sb.append( info.getFieldAlias() );
                    }
                    else {
                        sb.append( info.getAggregation() ).append( "( " );
                        sb.append( info.getFieldName() ).append( " ) AS " );
                        sb.append( info.getFieldAlias() );
                    }
                    fields.add( sb.toString() );

//                    info.setId( ComUtil.genId() );
//                    info.setElementId( fModelElementDo.getId() );
//                    info.setExtendsId( fModelElementDo.getId() );
//                    info.setTableId( fModelElementDo.getId() );
                    filters.add( info );
                }
            }

            if ( group != null && group.size() > 0 ) {
                //组合字段
                for ( TTruModelFieldDo info : group ) {
                    //String fieldName = getDataFieldName( info, idFieldMap );
                    fields.add( info.getFieldName() );
                    groups.add( info.getFieldName() );

//                    info.setId( ComUtil.genId() );
//                    info.setElementId( fModelElementDo.getId() );
//                    info.setExtendsId( fModelElementDo.getId() );
//                    info.setTableId( fModelElementDo.getId() );
                    filters.add( info );
                }
            }

            sql.append( String.join( " ,\r\n ", fields ) ).append( " \r\nFROM " ).append( pre.getElement() ).append( "\r\n" );
            if ( groups.size() > 0 ) {
                sql.append( " GROUP BY " ).append( String.join( ",", groups ) );
            }
            //sql.append( " HAVING 1=1" );
            sql.append( ";\r\n" );
            truNode.getOperators().setFilters( filters );
            fModelElementDo.setRunSql( "" );
            fModelElementDo.setTableSql( sql.toString() );
            //node.getParams().put( "field",field );
        }
    }

    public List<TTruModelFieldDo> relExtends(TTruModelElementRelDo elementInfo) throws Exception {
        List<TTruModelFieldDo> list = new ArrayList<>();
        Map<String, TTruModelComponentDo> idcomponentDoMap = getComponents().stream().collect(Collectors.toMap(TTruModelComponentDo::getId, e -> e));
        Map<String, TTruModelElementDo> idElementMap = getModel().stream().collect(Collectors.toMap(TTruModelElementDo::getId, e -> e));
        TTruModelComponentDo componentDo = idcomponentDoMap.get(idElementMap.get(elementInfo.getEndElementId()).getTcId());
        getExtendsDos().stream().filter(y -> y.getElementId().equals(y.getExtendsId())).forEach(tBydbModelFieldDo -> {
            tBydbModelFieldDo.setId( ComUtil.genId());
            if (tBydbModelFieldDo.getIsSelect() == 0) {
                tBydbModelFieldDo.setIsSelect(-1);
            }
            if (componentDo != null && componentDo.getComponentEn().equals(Join_COMPONENT.getComponentName())) {
                tBydbModelFieldDo.setTableAlias(idElementMap.get(elementInfo.getStartElementId()).getElement());
            }
            if (StringUtils.isNotBlank(tBydbModelFieldDo.getAggregation())){
                tBydbModelFieldDo.setFieldAlias(tBydbModelFieldDo.getAggregation().concat("_")
                        .concat(tBydbModelFieldDo.getFieldAlias()));
            }
            tBydbModelFieldDo.setElementId(elementInfo.getEndElementId());
            tBydbModelFieldDo.setExtendsId(elementInfo.getStartElementId());
            list.add(tBydbModelFieldDo);

        });
        return list;
    }

}
