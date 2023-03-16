package cn.bywin.business.bean.analysis.olk.template;

import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.Data_COMPONENT;

import cn.bywin.business.bean.analysis.olk.OlkBaseComponenT;
import cn.bywin.business.bean.analysis.olk.OlkCheckComponent;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.bean.view.olk.OlkNode;
import cn.bywin.business.beanparse.OlkSrJdbcWhereCondParse;
import cn.bywin.business.beanparse.OlkWhereCondParse;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.util.JdbcTypeToJavaTypeUtil;
import cn.bywin.business.util.analysis.MapTypeAdapter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description 库表组件配置
 * @Author wangh
 * @Date 2021-10-20
 */
@Data
public class OlkTableComponent extends OlkBaseComponenT {


    public OlkTableComponent() {
    }

    @Override
    public List<TOlkModelFieldDo> getShowField( List<TOlkModelFieldDo> fieldDos ) throws Exception {

        fieldDos.stream().forEach( e -> {
            if ( StringUtils.isNotBlank( e.getFieldType() ) ) {
                e.setColumnType( JdbcTypeToJavaTypeUtil.chgTypeCom( e.getFieldType() ) );
            }
        } );
        List<TOlkModelFieldDo> fieldDoList = fieldDos.stream().sorted( Comparator.comparing( TOlkModelFieldDo::getFilterSort ) ).collect( Collectors.toList() );

        return fieldDoList;
    }

    @Override
    public OlkNode init( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {
        if ( StringUtils.isNotBlank( fModelElementDo.getConfig() ) ) {
            truNode.setParams( MapTypeAdapter.gsonToMap( fModelElementDo.getConfig() ) );
        }
        else {//连接配置初始化
            truNode.setParams( null );
        }

        truNode.getOperators().setFields( getShowField( truNode.getOperators().getFields() ) );
        truNode.setType( Data_COMPONENT.getComponentName() );
        return truNode;
    }

    @Override
    public boolean changeSameFieldName( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {
        return false;
    }

    @Override
    public OlkCheckComponent check( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {
        OlkWhereCondParse whereCondParse = new OlkWhereCondParse();
        if ( !whereCondParse.paraseComCond( truNode.getParams().toString(), getModel(), truNode.getOperators().getFields() ) ) {
            return new OlkCheckComponent( false, whereCondParse.getErrMsg() );
        }
        Map<Object, Long> distanceName = truNode.getOperators().getFields().stream().collect(
                Collectors.groupingBy( TOlkModelFieldDo::getFieldAlias, Collectors.counting() ) );
        if ( distanceName != null && distanceName.keySet().size() > 0 ) {
            return new OlkCheckComponent( false, String.format( "别名有重复%s", distanceName ) );
        }
        return new OlkCheckComponent( true, "success" );
    }

    @Override
    public void build( OlkNode truNode, TOlkModelElementDo fModelElementDo, TOlkModelDo model ) throws Exception {

        List<TOlkModelFieldDo> tBydbModelFieldDos = truNode.getOperators().getFields();
        String pre = fModelElementDo.getElement();
        List<TOlkModelFieldDo> filters = tBydbModelFieldDos.stream().filter( e -> e.getFilterStatus() != null && e.getFilterStatus() == 1 ).collect( Collectors.toList() );

        if ( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
            StringBuilder sql = new StringBuilder();
            List<String> field = new ArrayList<>();
            //获取表的字段
            tBydbModelFieldDos.stream().forEach( e -> {
                        if ( e.getIsSelect() == 1 ) {
                            if ( StringUtils.equals( e.getFieldName(), e.getFieldAlias() ) ) {
                                field.add( e.getFieldName() );
                            }
                            else {
                                field.add( String.format( "%s AS %s", e.getFieldName(), e.getFieldAlias() ) );
                                //field.add( pre.concat( "." ).concat( e.getFieldName() ).concat( " AS " ).concat( e.getFieldAlias() ) );
                            }
                        }
                    }
            );
            //获取表过滤的字段
            sql.append( "SELECT " ).append( String.join( ",", field ) ).append( "\r\n FROM " );
            String objFullName = truNode.getDatabase().getObjFullName();
            if ( StringUtils.isNotBlank( model.getDcId() ) ) {
                int idx = objFullName.indexOf( "." );
                sql.append( objFullName.substring( idx + 1 ) );
            }
            else {
                sql.append( objFullName );
            }
            sql.append( " AS " ).append( pre ).append( "_tmp" );
//            for ( TOlkModelFieldDo filter : filters ) {
//                sql.append( "\r\n AND " ).append( pre ).append( "." ).append( filter.getFieldName() );
//                SqlFilterUtils.makeSqlFilter( sql, filter.getFilterConfig(), filter.getFieldType(), Arrays.asList( filter.getFilterValue().split( "," ) ) );
//            }

            if ( truNode.getParams() != null && StringUtils.isNotBlank( truNode.getParams().toString() ) ) {
                fModelElementDo.setConfig( JsonUtil.toJson( truNode.getParams() ).replace( "\n", "" ) );
                OlkSrJdbcWhereCondParse whereCondParse = new OlkSrJdbcWhereCondParse();

                List<TOlkModelFieldDo> fieldList = new ArrayList<>();
                for ( TOlkModelFieldDo tOlkModelFieldDo : truNode.getOperators().getFields() ) {
                    TOlkModelFieldDo cp = new TOlkModelFieldDo();
                    MyBeanUtils.copyBeanNotNull2Bean( tOlkModelFieldDo,cp );
                    cp.setTableAlias( cp.getTableAlias()+"_tmp" );
                    fieldList.add(  cp );
                }
                if ( whereCondParse.paraseComCond( truNode.getParams().toString(), getModel(), fieldList ) ) {
                    if ( StringUtils.isNotBlank( whereCondParse.getWhereSql() ) ) {
                        sql.append( "\r\n WHERE " );
                        sql.append( "\r\n " ).append( whereCondParse.getWhereSql() );
                    }
                }
            }
            if( fModelElementDo.getTotal() != null ){
                sql.append( "\r\n LIMIT " ).append( fModelElementDo.getTotal() );
            }
            fModelElementDo.setRunSql( sql.toString() );
            fModelElementDo.setTableSql( "" );
        }
        else { //flink

            /*String tableName = pre;
            String whereSql = null;
            if ( truNode.getParams() != null && StringUtils.isNotBlank( truNode.getParams().toString() ) ) {
                fModelElementDo.setConfig( JsonUtil.toJson( truNode.getParams() ).replace( "\n", "" ) );
                FlinkWhereCondParse whereCondParse = new FlinkWhereCondParse();
                if ( whereCondParse.paraseComCond( truNode.getParams().toString(), getModel(), truNode.getOperators().getFields() ) ) {
                    whereSql = whereCondParse.getWhereSql();
                    if ( fModelElementDo.getTotal() != null || StringUtils.isNotBlank( whereSql ) ) {
                        whereSql = whereSql.replaceAll( String.format( " %s\\.", pre ), " " );
                        tableName = "tmp_".concat( pre );
                    }
                }
            }

            boolean bwhere  = StringUtils.isNotBlank( whereSql ) ;

            StringBuffer sql2 = new StringBuffer();
            List<String> field2 = new ArrayList<>();
            //获取表的字段
            tBydbModelFieldDos.stream().forEach( e -> {
                    if (bwhere || fModelElementDo.getTotal() != null || e.getIsSelect() == 1 ) {
                        field2.add( String.format( "%s %s COMMENT '%s' " , e.getFieldName() ,DbTypeToFlinkType.chgType(  e.getFieldType() ),ComUtil.trsEmpty( e.getFieldExpr(),e.getFieldName()  ) ) );
                    }
                }
            );
            //获取表过滤的字段
            FDatasourceDo dsDo = truNode.getDbSource();
            if( dsDo != null ) {
                String objFullName = truNode.getDatabase().getObjFullName();
                int idx = objFullName.indexOf( "." );
                String jdbcUrl = dsDo.getJdbcUrl();
                String tab = truNode.getDatabase().getObjectName();
                String dbname = dsDo.getDsDatabase();
                if ( idx > 0 ) {
                    dbname = objFullName.substring( 0, idx );
                    tab = objFullName.substring( idx + 1 );
                    jdbcUrl = JdbcOpBuilder.genUrl( dsDo.getDsType(), dsDo.getDsIp(), dsDo.getDsPort(), dbname );
                }

                sql2.append( "CREATE TABLE " ).append( tableName ).append( " (\r\n  " ).append( String.join( ",\r\n", field2 ) ).append( ") COMMENT '" ).append( fModelElementDo.getName() ).append( "'\r\n WITH ( \r\n" );

                if( JdbcOpBuilder.dbStarRocks.equals(  dsDo.getDsType() )){
//                     'connector'='starrocks',
//                       'scan-url'='fe_ip1:8030,fe_ip2:8030,fe_ip3:8030',
//                       'jdbc-url'='jdbc:mysql://fe_ip:9030',
//                       'username'='root',
//                       'password'='',
//                       'database-name'='flink_test',
//                       'table-name'='flink_test'

                    sql2.append( "                'connector' = 'starrocks'," ).append( "\r\n" );
                    sql2.append( "                'jdbc-url' = '" ).append( jdbcUrl ).append( "'," ).append( "\r\n" );
                    sql2.append( "                'database-name' = '" ).append( dbname ).append( "'," ).append( "\r\n" );
                    sql2.append( "                'table-name' = '" ).append( tab ).append( "'," ).append( "\r\n" );
                    sql2.append( "                'username' = '" ).append( dsDo.getUsername() ).append( "'," ).append( "\r\n" );
                    sql2.append( "                'password' = '" ).append( dsDo.getPassword() ).append( "'" );
                    Map<String, String> map = TxtToMap.toMap( dsDo.getSourceSet() );
                    if(map.size()>0){
                        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
                        while ( iterator.hasNext() ){
                            Map.Entry<String, String> next = iterator.next();
                            String key = next.getKey();
                            String value = next.getValue();
                            if( key.startsWith( "'" )){
                                sql2.append( ",\r\n" ).append( key ).append( "=" ).append( value );
                            }
                            else{
                                sql2.append( ",\r\n'" ).append( key ).append( "'='" ).append( value ).append( "'" );
                            }
                        }
                    }
                }
                else { //人大金仓，神通 ，olk ，opengauss ， 达梦source
                    if( JdbcOpBuilder.dbMySql.equals(  dsDo.getDsType() ) ||
                            JdbcOpBuilder.dbMySql5.equals(  dsDo.getDsType() ) ||
                            JdbcOpBuilder.dbMySql8.equals(  dsDo.getDsType() ) ||
                            JdbcOpBuilder.dbPostgreSql.equals(  dsDo.getDsType() ) ||
                            JdbcOpBuilder.dbTiDb.equals(  dsDo.getDsType() )
                    )
                    {
                        sql2.append( "'connector' = 'jdbc'," ).append( "\r\n" );
                    }
                    else{
                        sql2.append( "'connector' = 'commondb'," ).append( "\r\n" );
                    }
                    sql2.append( "'driver' = '" ).append( dsDo.getDsDriver() ).append( "'," ).append( "\r\n" );

                    if ( JdbcOpBuilder.dbOpenLooKeng.equalsIgnoreCase( dsDo.getDsType() ) ) {
                        sql2.append( "'url' = '" ).append( dsDo.getJdbcUrl() ).append( "'," ).append( "\r\n" );
                        sql2.append( "'table-name' = '" ).append( truNode.getDatabase().getObjFullName() ).append( "'," ).append( "\r\n" );
                    }
                    else {
                        sql2.append( "'table-name' = '" ).append( tab ).append( "'," ).append( "\r\n" );
                        sql2.append( "'url' = '" ).append( jdbcUrl ).append( "'," ).append( "\r\n" );
                    }
                    sql2.append( "'username' = '" ).append( dsDo.getUsername() ).append( "'," ).append( "\r\n" );
                    sql2.append( "'password' = '" ).append( dsDo.getPassword() ).append( "'" ).append( "\r\n" );
                }
                sql2.append( ");" ).append( "\r\n" );


                //sql2 = new StringBuffer();
                //sql和配置存储

                //sql2.append( "CREATE VIEW " ).append( pre ).append( " AS SELECT \r\n" );
                if ( fModelElementDo.getTotal() != null || bwhere ) {
                    sql2.append( "CREATE VIEW " ).append( fModelElementDo.getElement() );
                    sql2.append( " AS SELECT \r\n" );
                    field2.clear();
                    tBydbModelFieldDos.stream().forEach( e -> {
                                if ( e.getIsSelect() == 1 ) {
                                    if ( e.getFieldName().equals( e.getFieldAlias() ) ) {
                                        field2.add( e.getFieldName() );
                                    }
                                    else {
                                        field2.add( e.getFieldName().concat( " AS " ).concat( e.getFieldAlias() ) );
                                    }
                                }
                            }
                    );
                    sql2.append( String.join( ",\r\n", field2 ) ).append( "\r\n FROM " ).append( tableName );

                    if( bwhere ) {
                        sql2.append( "\r\n WHERE " ).append( whereSql );
                        for ( TOlkModelFieldDo filter : filters ) {
                            sql2.append( "\r\n AND " ).append( pre ).append( "." ).append( filter.getFieldName() );
                            SqlFilterUtils.makeSqlFilter( sql2, filter.getFilterConfig(), filter.getFieldType(), Arrays.asList( filter.getFilterValue().split( "," ) ) );
                        }
                    }
                    if( fModelElementDo.getTotal() != null ){
                        sql2.append( "\r\n LIMIT " ).append( fModelElementDo.getTotal() );
                    }
                    sql2.append( ";\r\n" );
                }
            }
            fModelElementDo.setTableSql( sql2.toString() );
            fModelElementDo.setRunSql( "" );*/
        }
    }

    @Override
    public List<TOlkModelFieldDo> relExtends( TOlkModelElementRelDo elementInfo ) throws Exception {
        List<TOlkModelFieldDo> list = new ArrayList<>();
        getExtendsDos().stream().forEach( e -> {
            e.setId( ComUtil.genId() );
            if ( e.getIsSelect() == 0 ) {
                e.setIsSelect( -1 );
            }
            e.setElementId( elementInfo.getEndElementId() );
            e.setExtendsId( elementInfo.getStartElementId() );
            list.add( e );

        } );
        return list;
    }


}
