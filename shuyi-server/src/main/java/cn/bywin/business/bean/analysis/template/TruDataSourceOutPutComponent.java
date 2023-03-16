package cn.bywin.business.bean.analysis.template;

import static cn.bywin.business.bean.analysis.TruComponentEnum.DataSourceOutPut_COMPONENT;

import cn.bywin.business.bean.analysis.TruBaseComponenT;
import cn.bywin.business.bean.analysis.TruCheckComponent;
import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.bydb.TTruModelElementDo;
import cn.bywin.business.bean.bydb.TTruModelElementRelDo;
import cn.bywin.business.bean.bydb.TTruModelFieldDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.view.bydb.TruNode;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.util.DbTypeToFlinkType;
import cn.bywin.business.util.TxtToMap;
import cn.bywin.business.util.analysis.MapTypeAdapter;
import cn.jdbc.JdbcOpBuilder;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class TruDataSourceOutPutComponent extends TruBaseComponenT {

    private String datasourceId;
    private String tableName;
    private String schemaName;
    private String sinkType;

    public final static String paraDsId ="datasourceId";
    public final static  String paraSchemaName = "schemaName";
    public final static  String paraTableName = "tableName";
    public final static  String paraSinkType = "sinkType";

    public TruDataSourceOutPutComponent() {
    }

    @Override
    public List<TTruModelFieldDo> getShowField( List<TTruModelFieldDo> fieldDos ) throws Exception {
        //List<TTruModelFieldDo> fieldDoList =;
        return fieldDos.stream().filter( c -> c.getIsSelect() == 1 ).
                sorted( Comparator.comparing( TTruModelFieldDo::getFilterSort ) ).collect( Collectors.toList() );
    }

    @Override
    public TruNode init( TruNode truNode, TTruModelElementDo fModelElementDo ) throws Exception {
        if ( StringUtils.isNotBlank( fModelElementDo.getConfig() ) ) {
            truNode.setParams( MapTypeAdapter.gsonToMap( fModelElementDo.getConfig() ) );
        }
        else {//连接配置初始化
            Map<String, Object> params = new HashMap<>( 5 );
            //params.put( paraDsId, null );
            //params.put( paraTableName, null );
            params.put( paraSinkType, "1" );
            truNode.setParams( params );
        }
        truNode.setType( DataSourceOutPut_COMPONENT.getComponentName() );
        return truNode;
    }

    @Override
    public boolean changeSameFieldName( TruNode truNode, TTruModelElementDo fModelElementDo ) throws Exception {
        return false;
    }

    @Override
    public TruCheckComponent check( TruNode truNode, TTruModelElementDo fModelElementDo ) throws Exception {
        if ( getPreModel().size() != 1 ) {
            return new TruCheckComponent( false, "输出源前置组件个数错误" );
        }
        Map<String, Object> params = truNode.getParams();
        if(params.get( paraDsId )==null || StringUtils.isBlank( params.get( paraDsId ).toString() ) ){
            return new TruCheckComponent( false, "请选择输出数据源" );
        }
        if(params.get( paraSchemaName )==null || StringUtils.isBlank( params.get( paraSchemaName ).toString() ) ){
            return new TruCheckComponent( false, "输出库不能为空" );
        }
        if(params.get( paraTableName )==null || StringUtils.isBlank( params.get( paraTableName ).toString() ) ){
            return new TruCheckComponent( false, "输出表不能为空" );
        }
        return new TruCheckComponent( true, "success" );
    }

    @Override
    public void build( TruNode truNode, TTruModelElementDo fModelElementDo, TTruModelDo model ) throws Exception {
        if ( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
            fModelElementDo.setConfig( JsonUtil.toJson( truNode.getParams() ).replace( "\n", "" ) );
            String preSql = getPreModel().get( 0 ).getRunSql();
            fModelElementDo.setRunSql( preSql );
        }
        else {
            fModelElementDo.setConfig( JsonUtil.toJson( truNode.getParams() ).replace( "\n", "" ) );
            String pre = fModelElementDo.getElement();
            TTruModelElementDo preElem = getPreModel().get( 0 );
            //String preSql = preElem.getRunSql();
            StringBuilder sql = new StringBuilder();
            List<String> field2 = new ArrayList<>();

            JsonObject jsonObject = JsonUtil.toJsonObject( fModelElementDo.getConfig() );
            if ( truNode.getDbSource() != null && jsonObject != null && jsonObject.has( paraTableName ) ) {

                tableName = jsonObject.get(paraTableName).getAsString();
                schemaName = jsonObject.get(paraSchemaName).getAsString();
                FDatasourceDo dsDo = truNode.getDbSource();
                String jdbcUrl = JdbcOpBuilder.genUrl(dsDo.getDsType(), dsDo.getDsIp(), dsDo.getDsPort(), schemaName);

                //String datasourceId = jsonObject.get( paraDsId ).getAsString();
                //获取表的字段
                truNode.getOperators().getFields().forEach( e -> {
                            //if ( e.getIsSelect() == 1 ) {
//                            if (JdbcOpBuilder.dbStarRocks.equals(dsDo.getDsType())) {
//                                field2.add( String.format( "%s %s ", e.getFieldName(), DbTypeToFlinkType.chgType( e.getFieldType() ) ) );
//                            }
//                            else{
                                field2.add( String.format( "%s %s COMMENT '%s' ", e.getFieldName(), DbTypeToFlinkType.chgType( e.getFieldType() ), ComUtil.trsEmpty( e.getFieldExpr(), e.getFieldName() ) ) );
//                            }
                            //}
                        }
                );
                //获取表过滤的字段
                sql.append("CREATE TABLE ").append(pre).append(" (\r\n  ").append(String.join(",\r\n", field2)).append( ")");
                //if (!JdbcOpBuilder.dbStarRocks.equals(dsDo.getDsType())) {
                    sql.append( " COMMENT '" ).append( fModelElementDo.getName() ).append( "'\r\n " );
                //}
                sql.append(" WITH ( \r\n");
                if (JdbcOpBuilder.dbStarRocks.equals(dsDo.getDsType())) {
                    /* "'connector' = 'starrocks'," +
                    "'jdbc-url'='jdbc:mysql://ip:port,ip:port?xxxxx'," +
                    "'load-url'='ip:port;ip:port'," +
                    "'database-name' = 'xxx'," +
                    "'table-name' = 'xxx'," +
                    "'username' = 'xxx'," +
                    "'password' = 'xxx'," +
                    "'sink.buffer-flush.interval-ms' = '15000'," +
                    "'sink.properties.format' = 'json'," +
                    "'sink.properties.strip_outer_array' = 'true'," +
                    "'sink.parallelism' = '1'," +
                    "'sink.max-retries' = '10'," +
                     */
                    sql.append("                'connector' = 'starrocks',").append("\r\n");
                    sql.append("                'jdbc-url' = '").append(jdbcUrl).append("',").append("\r\n");
                    sql.append("                'database-name' = '").append(schemaName).append("',").append("\r\n");
                    sql.append("                'table-name' = '").append(tableName).append("',").append("\r\n");
                    sql.append("                'username' = '").append(dsDo.getUsername()).append("',").append("\r\n");
                    sql.append("                'password' = '").append(dsDo.getPassword()).append("'");
                    Map<String, String> map = TxtToMap.toMap(dsDo.getSinkSet());
                    if (map.size() > 0) {
                        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<String, String> next = iterator.next();
                            String key = next.getKey();
                            String value = next.getValue();
                            if (key.startsWith("'")) {
                                sql.append(",\r\n").append(key).append("=").append(value);
                            } else {
                                sql.append(",\r\n'").append(key).append("'='").append(value).append("'");
                            }
                        }
                    }
                } else {

                    if( JdbcOpBuilder.dbMySql.equals(  dsDo.getDsType() ) ||
                            JdbcOpBuilder.dbMySql5.equals(  dsDo.getDsType() ) ||
                            JdbcOpBuilder.dbMySql8.equals(  dsDo.getDsType() ) ||
                            JdbcOpBuilder.dbPostgreSql.equals(  dsDo.getDsType() ) ||
                            JdbcOpBuilder.dbTiDb.equals(  dsDo.getDsType() )
                    )
                    {
                        sql.append( "'connector' = 'jdbc'," ).append( "\r\n" );
                    }
                    else{
                        sql.append( "'connector' = 'commondb'," ).append( "\r\n" );
                    }
                    sql.append("                'driver' = '").append(dsDo.getDsDriver()).append("',").append("\r\n");
                    sql.append("                'url' = '").append(jdbcUrl).append("',").append("\r\n");
                    sql.append("                'table-name' = '").append(tableName).append("',").append("\r\n");
                    sql.append("                'username' = '").append(dsDo.getUsername()).append("',").append("\r\n");
                    sql.append("                'password' = '").append(dsDo.getPassword()).append("',").append("\r\n");
                    sql.append("                'sink.buffer-flush.max-rows' = '5000',\n" +
                            "                'sink.buffer-flush.interval' = '1s',\n" +
                            "                'sink.max-retries' = '3'");
                }

                sql.append(");").append("\r\n");

                //sql和配置存储

                field2.clear();
                truNode.getOperators().getFields().forEach( e -> {
                            //if ( e.getIsSelect() == 1 ) {
                            field2.add(e.getFieldName());
                            //}
                        }
                );
                //sql2.append( "CREATE VIEW " ).append( pre ).append( " AS SELECT \r\n" );
                sql.append(" INSERT INTO ").append(pre).append(" ");
                sql.append("(\r\n").append(String.join(",\r\n", field2)).append(")\r\n");
                sql.append(" SELECT ");
                sql.append(String.join(",\r\n", field2)).append("\r\n FROM  ").append(preElem.getElement());

                sql.append(";\r\n");
            }
            fModelElementDo.setTableSql( sql.toString() );
            fModelElementDo.setRunSql( "" );
        }

    }

    @Override
    public List<TTruModelFieldDo> relExtends( TTruModelElementRelDo elementInfo ) throws Exception {
        //List<TTruModelFieldDo> list = new ArrayList<>();
        return new ArrayList<>();
    }
}