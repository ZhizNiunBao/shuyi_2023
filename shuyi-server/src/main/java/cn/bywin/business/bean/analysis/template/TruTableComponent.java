package cn.bywin.business.bean.analysis.template;

import static cn.bywin.business.bean.analysis.TruComponentEnum.Data_COMPONENT;

import cn.bywin.business.bean.analysis.TruBaseComponenT;
import cn.bywin.business.bean.analysis.TruCheckComponent;
import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.bydb.TTruModelElementDo;
import cn.bywin.business.bean.bydb.TTruModelElementRelDo;
import cn.bywin.business.bean.bydb.TTruModelFieldDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.view.bydb.TruNode;
import cn.bywin.business.beanparse.FlinkWhereCondParse;
import cn.bywin.business.beanparse.TruSrJdbcWhereCondParse;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.util.DbTypeToFlinkType;
import cn.bywin.business.util.JdbcTypeToJavaTypeUtil;
import cn.bywin.business.util.TxtToMap;
import cn.bywin.business.util.analysis.MapTypeAdapter;
import cn.bywin.business.util.analysis.SqlFilterUtils;
import cn.jdbc.JdbcOpBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
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
public class TruTableComponent extends TruBaseComponenT {


    public TruTableComponent() {
    }

    @Override
    public List<TTruModelFieldDo> getShowField(List<TTruModelFieldDo> fieldDos) throws Exception {

        fieldDos.stream().forEach(e -> {
            if (StringUtils.isNotBlank(e.getFieldType())) {
                e.setColumnType(JdbcTypeToJavaTypeUtil.chgTypeCom(e.getFieldType()));
            }
        });
        List<TTruModelFieldDo> fieldDoList = fieldDos.stream().sorted(Comparator.comparing(TTruModelFieldDo::getFilterSort)).collect(Collectors.toList());

        return fieldDoList;
    }

    @Override
    public TruNode init( TruNode truNode, TTruModelElementDo fModelElementDo) throws Exception {
        if (StringUtils.isNotBlank(fModelElementDo.getConfig())) {
            truNode.setParams(MapTypeAdapter.gsonToMap(fModelElementDo.getConfig()));
        } else {//连接配置初始化
            truNode.setParams(null);
        }

        truNode.getOperators().setFields(getShowField( truNode.getOperators().getFields()));
        truNode.setType(Data_COMPONENT.getComponentName());
        return truNode;
    }

    @Override
    public boolean changeSameFieldName( TruNode truNode, TTruModelElementDo fModelElementDo ) throws Exception {
        return false;
    }

    @Override
    public TruCheckComponent check( TruNode truNode, TTruModelElementDo fModelElementDo) throws Exception {
        FlinkWhereCondParse whereCondParse = new FlinkWhereCondParse();
        if (!whereCondParse.paraseComCond( truNode.getParams().toString(), getModel(), truNode.getOperators().getFields())) {
            return new TruCheckComponent(false, whereCondParse.getErrMsg());
        }
        Map<Object, Long> distanceName = truNode.getOperators().getFields().stream().collect(
                Collectors.groupingBy(TTruModelFieldDo::getFieldAlias, Collectors.counting()));
        if (distanceName != null && distanceName.keySet().size() > 0) {
            return new TruCheckComponent(false, String.format("别名有重复%s", distanceName));
        }
        return new TruCheckComponent(true, "success");
    }

    @Override
    public void build( TruNode truNode, TTruModelElementDo fModelElementDo, TTruModelDo model) throws Exception {

        List<TTruModelFieldDo> tBydbModelFieldDos = truNode.getOperators().getFields();
        String pre = fModelElementDo.getElement();
        List<TTruModelFieldDo> filters = tBydbModelFieldDos.stream().filter( e -> e.getFilterStatus() != null && e.getFilterStatus() == 1 ).collect( Collectors.toList() );

        if( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
            StringBuffer sql = new StringBuffer();
            List<String> field = new ArrayList<>();
            //获取表的字段
            tBydbModelFieldDos.stream().forEach( e -> {
                        if ( e.getIsSelect() == 1 ) {
                            field.add( pre.concat( "." ).concat( e.getFieldName() ).concat( " AS " ).concat( e.getFieldAlias() ) );

                        }
                    }
            );
            //获取表过滤的字段
            sql.append( "SELECT " ).append( String.join( ",", field ) ).append( " FROM " ).append( truNode.getDatabase().getObjFullName() ).append( " AS " )
                    .append( pre );
            for ( TTruModelFieldDo filter : filters ) {
                sql.append( " AND " ).append( pre ).append( "." ).append( filter.getFieldName() );
                SqlFilterUtils.makeSqlFilter( sql, filter.getFilterConfig(), filter.getFieldType(), Arrays.asList( filter.getFilterValue().split( "," ) ) );
            }
            sql.append( " WHERE 1=1" );
            if ( truNode.getParams() != null && StringUtils.isNotBlank( truNode.getParams().toString() ) ) {
                fModelElementDo.setConfig( JsonUtil.toJson( truNode.getParams() ).replace( "\n", "" ) );
                TruSrJdbcWhereCondParse whereCondParse = new TruSrJdbcWhereCondParse();
                if ( whereCondParse.paraseComCond( truNode.getParams().toString(), getModel(), truNode.getOperators().getFields() ) ) {
                    if ( StringUtils.isNotBlank( whereCondParse.getWhereSql() ) ) {
                        sql.append( " AND " ).append( whereCondParse.getWhereSql() );
                    }
                }
            }
            fModelElementDo.setRunSql( sql.toString() );
        }
        else { //flink
            String tableName = pre;
            String whereSql = null;
            List<String> condFieldList = new ArrayList<>();
            if ( truNode.getParams() != null && StringUtils.isNotBlank( truNode.getParams().toString() ) ) {
                fModelElementDo.setConfig( JsonUtil.toJson( truNode.getParams() ).replace( "\n", "" ) );
                FlinkWhereCondParse whereCondParse = new FlinkWhereCondParse();
                List<TTruModelFieldDo> colParaList = truNode.getOperators().getFields();
                if ( whereCondParse.paraseComCond( truNode.getParams().toString(), getModel(), colParaList ) ) {
                    whereSql = whereCondParse.getWhereSql();
                    if ( fModelElementDo.getTotal() != null || StringUtils.isNotBlank( whereSql ) ) {
                        List<String> tmpList = whereCondParse.getUseFieldList();
                        if( tmpList != null ) {
                            for ( String tmpFld : tmpList ) {
                                int idx = tmpFld.indexOf( "." );
                                if( idx>=0){
                                    condFieldList.add( tmpFld.substring( idx+1 ) );
                                }
                                else{
                                    condFieldList.add( tmpFld );
                                }
                            }
                        }
                        whereSql = whereSql.replaceAll( String.format( " %s\\.", pre ), " " );
                        tableName = "tmp_".concat( pre );
                    }
                }
            }

            boolean bwhere  = StringUtils.isNotBlank( whereSql ) ;

            StringBuffer sql2 = new StringBuffer();
            List<String> field2 = new ArrayList<>();
            //获取表的字段
            FDatasourceDo dsDo = truNode.getDbSource();
            tBydbModelFieldDos.stream().forEach( e -> {
                    if ( e.getIsSelect() == 1 || condFieldList.indexOf( e.getFieldName() )>=0 ) {
//                        if( JdbcOpBuilder.dbStarRocks.equals(  dsDo.getDsType() )) {
//                            field2.add( String.format( "%s %s ", e.getFieldName(), DbTypeToFlinkType.chgType( e.getFieldType() ) ) );
//                        }
//                        else{
                            field2.add( String.format( "%s %s COMMENT '%s' ", e.getFieldName(), DbTypeToFlinkType.chgType( e.getFieldType() ), ComUtil.trsEmpty( e.getFieldExpr(), e.getFieldName() ) ) );
//                        }
                    }
                }
            );
            //获取表过滤的字段
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

                sql2.append( "CREATE TABLE " ).append( tableName ).append( " (\r\n  " ).append( String.join( ",\r\n  ", field2 ) ).append( ") COMMENT '" ).append( fModelElementDo.getName() ).append( "'\r\n WITH ( \r\n" );

                if( JdbcOpBuilder.dbStarRocks.equals(  dsDo.getDsType() )){
                    /* 'connector'='starrocks',
                       'scan-url'='fe_ip1:8030,fe_ip2:8030,fe_ip3:8030',
                       'jdbc-url'='jdbc:mysql://fe_ip:9030',
                       'username'='root',
                       'password'='',
                       'database-name'='flink_test',
                       'table-name'='flink_test'
                     */
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
                else {
                    //人大金仓，神通 ，olk ，opengauss ， 达梦source
                    if( JdbcOpBuilder.dbMySql.equals(  dsDo.getDsType() ) ||
                            JdbcOpBuilder.dbMySql5.equals(  dsDo.getDsType() ) ||
                            JdbcOpBuilder.dbMySql8.equals(  dsDo.getDsType() ) ||
                            JdbcOpBuilder.dbPostgreSql.equals(  dsDo.getDsType() ) ||
                            JdbcOpBuilder.dbTiDb.equals(  dsDo.getDsType() )
                    )
                    {
                        sql2.append( "'driver' = '" ).append( dsDo.getDsDriver() ).append( "'," ).append( "\r\n" );
                        sql2.append( "'connector' = 'jdbc'," ).append( "\r\n" );
                        sql2.append( "'url' = '" ).append( jdbcUrl ).append( "'," ).append( "\r\n" );
                        sql2.append( "'table-name' = '" ).append( tab ).append( "'," ).append( "\r\n" );
                    }
                    else if( JdbcOpBuilder.dbSqlServer.equals(  dsDo.getDsType() ) )
                    {
                        jdbcUrl = JdbcOpBuilder.genUrl( dsDo.getDsType(), dsDo.getDsIp(), dsDo.getDsPort(), "" );
                        sql2.append( "'connector' = 'commondb'," ).append( "\r\n" );
                        sql2.append( "'url' = '" ).append( jdbcUrl ).append( "'," ).append( "\r\n" );
                        sql2.append( "'table-name' = '" ).append( truNode.getDatabase().getObjFullName() ).append( "'," ).append( "\r\n" );
                    }
                    else if( JdbcOpBuilder.dbClickHouse.equals(  dsDo.getDsType() ) )
                    {
                        jdbcUrl = JdbcOpBuilder.genUrl( dsDo.getDsType(), dsDo.getDsIp(), dsDo.getDsPort(), "" );
                        sql2.append( "'connector' = 'clickhouse'," ).append( "\r\n" );
                        sql2.append( "'url' = '" ).append( jdbcUrl ).append( "'," ).append( "\r\n" );
                        sql2.append( "'table-name' = '" ).append( truNode.getDatabase().getObjFullName() ).append( "'," ).append( "\r\n" );
                    }
                    else {
                        sql2.append( "'driver' = '" ).append( dsDo.getDsDriver() ).append( "'," ).append( "\r\n" );
                        sql2.append( "'connector' = 'commondb'," ).append( "\r\n" );
                        if ( JdbcOpBuilder.dbOpenLooKeng.equalsIgnoreCase( dsDo.getDsType() )|| JdbcOpBuilder.dbOracle.equalsIgnoreCase( dsDo.getDsType() ) || JdbcOpBuilder.dbDm.equalsIgnoreCase( dsDo.getDsType() ) || JdbcOpBuilder.dbKingBase8.equalsIgnoreCase( dsDo.getDsType() ) || JdbcOpBuilder.dbKingBase.equalsIgnoreCase( dsDo.getDsType() ) ) {
                            sql2.append( "'url' = '" ).append( dsDo.getJdbcUrl() ).append( "'," ).append( "\r\n" );
                            sql2.append( "'table-name' = '" ).append( truNode.getDatabase().getObjFullName() ).append( "'," ).append( "\r\n" );
                        }
                        else {
                            sql2.append( "'table-name' = '" ).append( tab ).append( "'," ).append( "\r\n" );
                            sql2.append( "'url' = '" ).append( jdbcUrl ).append( "'," ).append( "\r\n" );
                        }
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
                        for ( TTruModelFieldDo filter : filters ) {
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
            fModelElementDo.setRunSql( "" );
        }
    }

    @Override
    public List<TTruModelFieldDo> relExtends(TTruModelElementRelDo elementInfo) throws Exception {
        List<TTruModelFieldDo> list = new ArrayList<>();
        getExtendsDos().stream().forEach(e -> {
            e.setId( ComUtil.genId());
            if (e.getIsSelect() == 0) {
                e.setIsSelect(-1);
            }
            e.setElementId(elementInfo.getEndElementId());
            e.setExtendsId(elementInfo.getStartElementId());
            list.add(e);

        });
        return list;
    }


}
