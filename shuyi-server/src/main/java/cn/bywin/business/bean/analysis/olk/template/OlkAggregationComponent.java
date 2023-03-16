package cn.bywin.business.bean.analysis.olk.template;

import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.GROUP_COMPONENT;
import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.Join_COMPONENT;

import cn.bywin.business.bean.analysis.olk.OlkBaseComponenT;
import cn.bywin.business.bean.analysis.olk.OlkCheckComponent;
import cn.bywin.business.bean.olk.TOlkModelComponentDo;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.bean.view.olk.OlkNode;
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
public class OlkAggregationComponent extends OlkBaseComponenT {


    private List<TOlkModelFieldDo> field = new ArrayList<>();
    private List<TOlkModelFieldDo> group = new ArrayList<>();

    public OlkAggregationComponent() {
    }

    @Override
    public List<TOlkModelFieldDo> getShowField( List<TOlkModelFieldDo> fieldDos ) throws Exception {
        List<TOlkModelFieldDo> disData = new ArrayList<>();
        fieldDos.stream().forEach( e -> {
            if ( e.getIsSelect() == 1 && e.getElementId().equals( e.getExtendsId() ) ) {
                e.setFieldName( e.getFieldAlias() );
                e.setFilterSort( 1 );
                disData.add( e );
            }
        } );
        List<TOlkModelFieldDo> fieldsLists = new ArrayList<>();
        List<TOlkModelFieldDo> fieldDoList = disData.stream().filter( c -> !c.getColumnType().equals( "String" ) ).sorted( Comparator.comparing( TOlkModelFieldDo::getColumnType ) ).collect( Collectors.toList() );
        disData.forEach( e -> {
            if ( e.getColumnType().equals( "String" ) ) {
                e.setFilterSort( 0 );
                fieldsLists.add( e );
            }
        } );
        fieldsLists.addAll( fieldDoList );
        return fieldsLists;
    }

    @Override
    public OlkNode init( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {

        Map<String, Object> params = new HashMap<>( 5 );
        List<TOlkModelFieldDo> fieldsList = truNode.getOperators().getFields().stream().filter( e -> !e.getElementId().equals( e.getExtendsId() ) ).collect( Collectors.toList() );
        List<TOlkModelFieldDo> orgFieldList = fieldsList.stream().filter( x -> StringUtils.isBlank( x.getAggregation() ) ).collect( Collectors.toList() );
//        List<TOlkModelFieldDo> fieldDoList = fieldsList.stream().filter(c -> !c.getColumnType().equals("String")).sorted(Comparator.comparing(TOlkModelFieldDo::getColumnType)).collect(Collectors.toList());
//        fieldsList.forEach(e -> {
//            if (e.getColumnType().equals("String")) {
//                fieldsLists.add(e);
//            }
//        });
        //fieldsLists.addAll(fieldDoList);
        truNode.getOperators().setFields( orgFieldList );
        fieldsList.removeAll( orgFieldList );
        for ( TOlkModelFieldDo fieldDo : fieldsList ) {
            if ( "group_by_field".equals( fieldDo.getAggregation() ) ) {
                group.add( fieldDo );
                //fieldDo.setFieldType( "BIGINT" );
            }
            else {
                field.add( fieldDo );
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
        params.put( "field", field );
        params.put( "group", group );
        truNode.setParams( params );
        truNode.setType( GROUP_COMPONENT.getComponentName() );
        return truNode;
    }

    @Override
    public boolean changeSameFieldName( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {
        OlkAggregationComponent aggregationComponent = JsonUtil.gson().fromJson( JsonUtil.toJson( truNode.getParams() ), OlkAggregationComponent.class );
        List<TOlkModelFieldDo> field = aggregationComponent.getField();
        List<TOlkModelFieldDo> group = aggregationComponent.getGroup();

        List<TOlkModelFieldDo> fieldList = truNode.getOperators().getFields();
        HashMap<String, TOlkModelFieldDo> fieldMap = new HashMap<>();
        for ( TOlkModelFieldDo fieldDo : fieldList ) {
            fieldMap.put( fieldDo.getFieldName(), fieldDo );
        }

        for ( TOlkModelFieldDo fieldDo : field ) {
            if ( "distinct_count".equalsIgnoreCase( fieldDo.getAggregation() ) ) {
                fieldDo.setFieldType( "BIGINT" );
                fieldDo.setColumnType( "BIGINT" );
                if ( !fieldDo.getFieldAlias().endsWith( "_dist_cnt" ) ) {
                    fieldDo.setFieldAlias( fieldDo.getFieldName().concat( "_dist_cnt" ) );
                }
                TOlkModelFieldDo tmp = fieldMap.get( fieldDo.getFieldName() );
                if ( tmp != null ) {
                    fieldDo.setFieldExpr( tmp.getFieldExpr() + "唯一总记录数" );
                }
            }
            if ( "count".equalsIgnoreCase( fieldDo.getAggregation() ) ) {
                fieldDo.setFieldType( "BIGINT" );
                fieldDo.setColumnType( "BIGINT" );
                if ( !fieldDo.getFieldAlias().endsWith( "_cnt" ) ) {
                    fieldDo.setFieldAlias( fieldDo.getFieldName().concat( "_cnt" ) );
                }
                TOlkModelFieldDo tmp = fieldMap.get( fieldDo.getFieldName() );
                if ( tmp != null ) {
                    fieldDo.setFieldExpr( tmp.getFieldExpr() + "总记录数" );
                }
            }
            List<String> tjList = new ArrayList<>();
            tjList.addAll( Arrays.asList( "max", "min", "sum", "avg" ) );
            List<String> exprList = new ArrayList<>();
            exprList.addAll( Arrays.asList( "最大%s", "最小%s", "%s总和", "平均%s" ) );
            for ( int i = 0; i < tjList.size(); i++ ) {
                String tj = tjList.get( i );
                if ( tj.equalsIgnoreCase( fieldDo.getAggregation() ) ) {
                    Pattern pattern = Pattern.compile( "_" + tj + "\\d*$", Pattern.CASE_INSENSITIVE );
                    Matcher matcher = pattern.matcher( fieldDo.getFieldAlias() );
                    if ( !matcher.find() ) {
                        fieldDo.setFieldAlias( fieldDo.getFieldName().concat( "_" ).concat( tj ) );
                    }
                    TOlkModelFieldDo tmp = fieldMap.get( fieldDo.getFieldName() );
                    if ( tmp != null ) {
                        fieldDo.setFieldExpr( String.format( exprList.get( i ), tmp.getFieldExpr() ) );
                    }
                }
            }
        }
        for ( TOlkModelFieldDo fieldDo : group ) {
            fieldDo.setAggregation( "group_by_field" );
        }

        List<TOlkModelFieldDo> fieldsList = new ArrayList<>();
        fieldsList.addAll( field );
        fieldsList.addAll( group );
        Map<String, String> nameMap = new HashedMap();
        for ( TOlkModelFieldDo fieldDo : fieldsList ) {
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
        truNode.getParams().put( "field", field );
        truNode.getParams().put( "group", group );
        return true;
    }

    @Override
    public OlkCheckComponent check( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {
        OlkAggregationComponent aggregationComponent = JsonUtil.gson().fromJson( JsonUtil.toJson( truNode.getParams() ), OlkAggregationComponent.class );
        List<TOlkModelFieldDo> field = aggregationComponent.getField();
        List<TOlkModelFieldDo> group = aggregationComponent.getGroup();
        if ( getPreModel().size() != 1 ) {
            return new OlkCheckComponent( false, "聚合表个数错误" );
        }
        if ( getPreModel().stream().filter( e -> e.getRunStatus() == 0 ).collect( Collectors.toList() ).size() > 0 ) {
            return new OlkCheckComponent( false, "前置组件未保存该组件无法保存，请配置" );
        }
        if ( field == null || field.size() < 1 ) {
            return new OlkCheckComponent( false, "聚合条件不能为空" );
        }
        for ( TOlkModelFieldDo fieldDo : field ) {
            if ( StringUtils.isBlank( fieldDo.getAggregation() ) ) {
                return new OlkCheckComponent( false, fieldDo.getFieldName() + "聚合方式不能为空" );
            }
        }
//        if (group == null || group.size() < 1) {
//            return new OlkCheckComponent(false, "组合条件不能为空");
//        }
//        if (node.getViewIds().size() < 1) {
//            return new CheckComponent(false, "选中字段不能为空");
//        }
        return new OlkCheckComponent( true, "success" );
    }

    @Override
    public void build( OlkNode truNode, TOlkModelElementDo fModelElementDo, TOlkModelDo model ) throws Exception {
        OlkAggregationComponent aggregationComponent = JsonUtil.gson().fromJson( JsonUtil.toJson( truNode.getParams() ), OlkAggregationComponent.class );
        List<TOlkModelFieldDo> field = aggregationComponent.getField();

        List<TOlkModelFieldDo> group = aggregationComponent.getGroup();
        List<TOlkModelFieldDo> filters = new ArrayList<>();


        List<String> alias = new ArrayList<>();
        if ( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
            /*group.stream().forEach( e -> {
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
            TOlkModelElementDo pre = preModel.get( 0 );
            String preSql = pre.getRunSql();
            String newsql = getMatchedFrom( preSql );
            Map<String, TOlkModelFieldDo> idFieldMap = truNode.getOperators().getFields().stream().collect( Collectors.toMap( TOlkModelFieldDo::getFieldAlias, e -> e ) );

            if ( group != null && group.size() > 0 ) {
                //组合字段
                for ( TOlkModelFieldDo info : group ) {
                    String fieldName = OlkSrJdbcWhereCondParse.getDataFieldName( info, idFieldMap );
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
            fModelElementDo.setRunSql( sql.toString() );*/

            StringBuffer sql = new StringBuffer();
            List<String> fields = new ArrayList<>();
            List<String> groups = new ArrayList<>();
            truNode.getOperators().getFields();
            TOlkModelElementDo pre = preModel.get( 0 );
            //String preSql = pre.getRunSql();
            //String newsql = getMatchedFrom( preSql );
            //Map<String, TOlkModelFieldDo> idFieldMap = node.getOperators().getFields().stream().collect( Collectors.toMap( TOlkModelFieldDo::getFieldAlias, e -> e ) );
            sql.append( " SELECT \r\n" );

            if ( field != null && field.size() > 0 ) {
                //分组字段
                for ( TOlkModelFieldDo info : field ) {
                    //String fieldName = getDataFieldName( info, idFieldMap );
                    StringBuffer sb = new StringBuffer();

//                    if( "distinct_count".equalsIgnoreCase( info.getAggregation() ) ||  "count".equalsIgnoreCase( info.getAggregation() ) ){
//                        info.setFieldType( "BIGINT" );
//                        info.setColumnType( "BIGINT" );
//                    }

                    if ( "distinct_count".equalsIgnoreCase( info.getAggregation() ) ) {
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
                for ( TOlkModelFieldDo info : group ) {
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

            sql.append( String.join( " ,\r\n ", fields ) ).append( " \r\nFROM \r\n(" ).append( pre.getRunSql() ).append( ") AS " ).append( pre.getElement() ).append( " \r\n" );
            if ( groups.size() > 0 ) {
                sql.append( " GROUP BY " ).append( String.join( ",", groups ) );
            }
            //sql.append( " HAVING 1=1" );
            truNode.getOperators().setFilters( filters );
            fModelElementDo.setRunSql( sql.toString() );
            fModelElementDo.setTableSql( "" );
            //node.getParams().put( "field",field );
        }
        else {
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
            TOlkModelElementDo pre = preModel.get( 0 );
            //String preSql = pre.getRunSql();
            //String newsql = getMatchedFrom( preSql );
            //Map<String, TOlkModelFieldDo> idFieldMap = node.getOperators().getFields().stream().collect( Collectors.toMap( TOlkModelFieldDo::getFieldAlias, e -> e ) );
            sql.append( "CREATE VIEW " ).append( fModelElementDo.getElement() ).append( " AS SELECT \r\n" );

            if ( field != null && field.size() > 0 ) {
                //分组字段
                for ( TOlkModelFieldDo info : field ) {
                    //String fieldName = getDataFieldName( info, idFieldMap );
                    StringBuffer sb = new StringBuffer();

//                    if( "distinct_count".equalsIgnoreCase( info.getAggregation() ) ||  "count".equalsIgnoreCase( info.getAggregation() ) ){
//                        info.setFieldType( "BIGINT" );
//                        info.setColumnType( "BIGINT" );
//                    }

                    if ( "distinct_count".equalsIgnoreCase( info.getAggregation() ) ) {
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
                for ( TOlkModelFieldDo info : group ) {
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

    public List<TOlkModelFieldDo> relExtends( TOlkModelElementRelDo elementInfo ) throws Exception {
        List<TOlkModelFieldDo> list = new ArrayList<>();
        Map<String, TOlkModelComponentDo> idcomponentDoMap = getComponents().stream().collect( Collectors.toMap( TOlkModelComponentDo::getId, e -> e ) );
        Map<String, TOlkModelElementDo> idElementMap = getModel().stream().collect( Collectors.toMap( TOlkModelElementDo::getId, e -> e ) );
        TOlkModelComponentDo componentDo = idcomponentDoMap.get( idElementMap.get( elementInfo.getEndElementId() ).getTcId() );
        getExtendsDos().stream().filter( y -> y.getElementId().equals( y.getExtendsId() ) ).forEach( tBydbModelFieldDo -> {
            tBydbModelFieldDo.setId( ComUtil.genId() );
            if ( tBydbModelFieldDo.getIsSelect() == 0 ) {
                tBydbModelFieldDo.setIsSelect( -1 );
            }
            if ( componentDo != null && componentDo.getComponentEn().equals( Join_COMPONENT.getComponentName() ) ) {
                tBydbModelFieldDo.setTableAlias( idElementMap.get( elementInfo.getStartElementId() ).getElement() );
            }
            if ( StringUtils.isNotBlank( tBydbModelFieldDo.getAggregation() ) ) {
                tBydbModelFieldDo.setFieldAlias( tBydbModelFieldDo.getAggregation().concat( "_" )
                        .concat( tBydbModelFieldDo.getFieldAlias() ) );
            }
            tBydbModelFieldDo.setElementId( elementInfo.getEndElementId() );
            tBydbModelFieldDo.setExtendsId( elementInfo.getStartElementId() );
            list.add( tBydbModelFieldDo );

        } );
        return list;
    }

}
