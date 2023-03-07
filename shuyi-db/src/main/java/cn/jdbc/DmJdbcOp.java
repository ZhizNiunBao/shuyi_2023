package cn.jdbc;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DmJdbcOp extends DbDataToObject implements IJdbcOp {
    Logger logger = LoggerFactory.getLogger( DmJdbcOp.class );
    private final String defDriver = "dm.jdbc.driver.DmDriver";
    private String driver;
    private String url;
    private String user;
    private String password;
    private Connection con;
    private String convertChar = "\"";
    private String dbType = JdbcOpBuilder.dbDm;
    private boolean raiseException = true;

    public DmJdbcOp( String dbType, String driver, String url, String user, String password ) throws ClassNotFoundException {
        if ( StringUtils.isNotBlank( dbType ) ) {
            this.dbType = dbType;
        }
        if ( StringUtils.isNotBlank( driver ) ) {
            this.driver = driver;
        }
        else {
            this.driver = defDriver;
        }
        this.url = url;
        this.user = user;
        this.password = password;
        Class.forName( this.driver );
        //genConn();
    }

    public DmJdbcOp( String driver, String url, String user, String password ) throws ClassNotFoundException {
        this( null, driver, url, user, password );
    }

    public DmJdbcOp( String url, String user, String password ) throws ClassNotFoundException {
        this( null, null, url, user, password );
    }

    @Override
    public Long selectTableCount( String cntSql, List<Object> paraList ) throws Exception {
        List<Map<String, Object>> cntList = selectData( cntSql, paraList );
        Iterator<Map.Entry<String, Object>> iterator = cntList.get( 0 ).entrySet().iterator();
        if(iterator.hasNext()){
            return  Long.parseLong( iterator.next().getValue().toString() ) ;
        }
        return null;
    }

    @Override
    public boolean isRaiseException() {
        return raiseException;
    }

    @Override
    public void setRaiseException( boolean raiseException ) {
        this.raiseException = raiseException;
    }

    /**
     * 获取连接
     *
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private boolean genConn() throws SQLException {
        if ( con == null || con.isClosed() ) {
            try {
                con = DriverManager.getConnection( url, user, password );
                return true;
            }
            catch ( SQLException e ) {
                if ( raiseException )
                    throw e;
                else {
                    logger.error( "连接失败,", e );
                }
            }
        }
        else {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkConnect()  {
        try {
            genConn();
            if ( con == null || con.isClosed() ) {
                return false;
            }
            else{
                return true;
            }
        }
        catch ( Exception ex){
            return false;
        }
    }

    /**
     * 关闭连接
     */
    @Override
    public void close() {
        try {
            if ( con != null && !con.isClosed() ) {
                con.close();
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        con = null;
    }

    public void colseObject( AutoCloseable... autoCloObj ) {
        if ( autoCloObj == null ) {
            return;
        }
        for ( AutoCloseable autoCloseable : autoCloObj ) {
            try {
                if ( autoCloseable != null ) {
                    autoCloseable.close();
                }
            }
            catch ( Exception e ) {

            }
        }
    }

    private void setParameter( PreparedStatement statement, List<Object> paras ) throws Exception {
        if ( paras == null )
            return;
        for ( int i = 0; i < paras.size(); i++ ) {
            Object dataval = paras.get( i );
            if ( dataval == null ) {
                statement.setTimestamp( i + 1, (Timestamp) paras.get( i ) );
            }
            else {
                String className = dataval.getClass().toString();
                if ( className.equals( "java.lang.String" ) ) {
                    statement.setString( i + 1, (String) paras.get( i ) );
                }
                else if ( className.equals( "java.sql.Timestamp" ) ) {
                    statement.setTimestamp( i + 1, (Timestamp) paras.get( i ) );
                }
                else if ( className.equals( "java.sql.Date" ) ) {
                    statement.setDate( i + 1, (java.sql.Date) paras.get( i ) );
                }
                else if ( className.equals( "java.sql.Time" ) ) {
                    statement.setTime( i + 1, (java.sql.Time) paras.get( i ) );
                }
                else if ( className.equals( "java.util.Date" ) ) {
                    statement.setTimestamp( i + 1, new Timestamp( ((java.util.Date) paras.get( i )).getTime() ) );
                }
                else
                    statement.setObject( i + 1, paras.get( i ) );

            }
        }
    }

    @Override
    public List selectData( String sql, List<Object> paras ) throws Exception {
        logger.debug( sql );
        PreparedStatement statement = null;
        ResultSet results = null;
        List<Map> list = new ArrayList();
        if ( !genConn() ) {
            return null;
        }
        try {

            statement = con.prepareStatement( sql );
            setParameter( statement, paras );
            //statement.setQueryTimeout( timeout );
            results = statement.executeQuery();
            ResultSetMetaData rsmd = results.getMetaData();

            while ( results.next() ) {
                Map map = new HashMap();
                for ( int i = 1; i <= rsmd.getColumnCount(); i++ ) {
                    map.put( rsmd.getColumnLabel( i ), toEffVal( rsmd.getColumnClassName( i ), results.getObject( i ) ) );
                }
                list.add( map );
            }
            return list;
        }
        catch ( Exception e ) {
            if ( raiseException )
                throw e;
            else {
                logger.error( "sql:{}", sql, e );
                return null;
            }
        }
        finally {
            colseObject( results, statement );
        }
    }

    /**
     * 计数从0开始
     */
    @Override
    public List selectData( String sql, List<Object> paras, long start, long cnt ) throws Exception {
        String pagSql = PageSQLUtil.mysqlStartNum( sql, start, cnt );
        return selectData( pagSql, paras );
    }

    @Override
    public List selectData( String sql ) throws Exception {
        return select( sql, "hashMap" );
    }

    @Override
    public List selectDataAsLinkMap( String sql ) throws Exception {
        return select( sql, "linkMap" );
    }

    private List select( String sql, String mapType ) throws Exception {
        logger.debug( sql );
        Statement statement = null;
        ResultSet results = null;
        List<Map> list = new ArrayList();
        if ( !genConn() ) {
            return null;
        }
        try {
            statement = con.createStatement();
            results = statement.executeQuery( sql );
            ResultSetMetaData rsmd = results.getMetaData();
            while ( results.next() ) {
                Map map = null;
                if ( "linkMap".equalsIgnoreCase( mapType ) ) {
                    map = new LinkedHashMap();
                }
                else {
                    map = new HashMap();
                }
                for ( int i = 1; i <= rsmd.getColumnCount(); i++ ) {

                    Object object = results.getObject( i );
                    if ( object == null ) {
                        map.put( rsmd.getColumnLabel( i ), null );
                    }
                    else {
                        if ( object.getClass().getName().startsWith( "dm.jdbc.driver.DmdbBlob" ) ) {
                            map.put( rsmd.getColumnLabel( i ), results.getBytes( i ) );
                        }
                        else if ( object.getClass().getName().startsWith( "dm.jdbc.driver." ) ) {
                            map.put( rsmd.getColumnLabel( i ), results.getString( i ) );
                        }
                        else {
                            map.put( rsmd.getColumnLabel( i ), toEffVal( rsmd.getColumnClassName( i ), results.getObject( i ) ) );
                        }
                    }
                }
                list.add( map );
            }
            return list;
        }
        catch ( Exception e ) {
            if ( raiseException )
                throw e;
            else {
                logger.error( sql );
                return null;
            }
        }
        finally {
            colseObject( results, statement );
        }
    }

    @Override
    public <T> List<T> selectData( String sql, Class<T> cls ) throws Exception {
        logger.debug( sql );
        PreparedStatement statement = null;
        ResultSet results = null;
        List<T> list = new ArrayList();
        if ( !genConn() ) {
            return null;
        }
        try {
            statement = con.prepareStatement( sql );
            results = statement.executeQuery();
            ResultSetMetaData rsmd = results.getMetaData();

            int colNumber = rsmd.getColumnCount();
            Field[] fields = cls.getDeclaredFields();

            while ( results.next() ) {
                T obj = cls.newInstance();
                Map map = new HashMap();
                for ( int i = 1; i <= colNumber; i++ ) {
                    for ( Field f : fields ) {
                        String labName = rsmd.getColumnLabel( i ).replaceAll( "_", "" );
                        if ( f.getName().equalsIgnoreCase( labName ) ) {
                            boolean flag = f.isAccessible();
                            f.setAccessible( true );
                            f.set( obj, toEffVal( results.getObject( i ), f.getType().getSimpleName() ) );
                            f.setAccessible( flag );
                            break;
                        }
                    }
                    //map.put( rsmd.getColumnLabel(i), toEffVal(rsmd.getColumnClassName(i), results.getObject(i)));
                }
                list.add( obj );
            }
            return list;

        }
        catch ( Exception e ) {
            if ( raiseException )
                throw e;
            else {
                logger.error( sql );
                return null;
            }
        }
        finally {
            colseObject( results, statement );
        }
    }

    /**
     * 计数从0开始
     */
    @Override
    public List selectData( String sql, long start, long cnt ) throws Exception {
        String pagSql = PageSQLUtil.mysqlStartNum( sql, start, cnt );
        return selectData( pagSql );
    }

    /**
     * 计数从0开始
     */
    @Override
    public List selectDataAsLinkMap( String sql, long start, long cnt ) throws Exception {
        String pagSql = PageSQLUtil.mysqlStartNum( sql, start, cnt );
        return selectDataAsLinkMap( pagSql );
    }


    @Override
    public boolean execute( String sql, List<Object> paras ) throws Exception {
        logger.debug( sql );
        PreparedStatement statement = null;
        if ( !genConn() ) {
            return false;
        }
        try {
            statement = con.prepareStatement( sql );
            for ( int i = 0; i < paras.size(); i++ ) {
                statement.setObject( i + 1, paras.get( i ) );
            }
            statement.execute();
            return true;
        }
        catch ( Exception e ) {
            if ( raiseException )
                throw e;
            else {
                logger.error( sql );
                return false;
            }
        }
        finally {
            colseObject( statement );
        }
    }

    @Override
    public boolean execute( String sql ) throws Exception {
        logger.debug( sql );
        Statement statement = null;
        if ( !genConn() ) {
            return false;
        }
        try {
            statement = con.createStatement();
            statement.execute( sql );
            return true;
        }
        catch ( Exception e ) {
            if ( raiseException )
                throw e;
            else {
                logger.error( sql );
                return false;
            }
        }
        finally {
            colseObject( statement );
        }
    }

    @Override
    public List<FieldType> checkTableField( String sql ) throws Exception {
        String strsql = PageSQLUtil.mysqlPageNum( sql, 1, 1 );
        logger.debug( strsql );
        //Connection connection = con;
        Statement statement = null;
        ResultSet results = null;
        List<FieldType> list = new ArrayList<>();
        if ( !genConn() ) {
            return null;
        }
        try {
            statement = con.createStatement();
            results = statement.executeQuery( strsql );
            ResultSetMetaData rsmd = results.getMetaData();
            int colNumber = rsmd.getColumnCount();
            for ( int i = 1; i <= colNumber; i++ ) {
                String name = rsmd.getColumnLabel( i );

                String type = "string";
                switch ( rsmd.getColumnType( i ) ) {
                    case Types.BIT:
                        type = "bit";
                        break;
                    case Types.TINYINT:
                        type = "tinyint";
                        break;
                    case Types.SMALLINT:
                        type = "smallint";
                        break;
                    case Types.INTEGER:
                        type = "integer";
                        break;
                    case Types.BIGINT:
                        type = "bigint";
                        break;
                    case Types.FLOAT:
                        type = "float";
                        break;
                    case Types.REAL:
                        type = "real";
                        break;
                    case Types.DOUBLE:
                        type = "double";
                        break;
                    case Types.NUMERIC:
                        type = "decimal";
                        break;
                    case Types.DECIMAL:
                        type = "decimal";
                        break;
                    case Types.DATE:
                        type = "date";
                        break;
                    case Types.TIME:
                        type = "date";
                        break;
                    case Types.TIMESTAMP:
                        type = "timestamp";
                        break;
                }
                list.add( new FieldType( name, type, rsmd.getColumnType( i ), rsmd.getColumnTypeName( i ) ) );
            }
            return list;

        }
        catch ( Exception e ) {
            if ( raiseException )
                throw e;
            else {
                logger.error( sql );
                return null;
            }
        }
        finally {
            colseObject( results, statement );
        }
    }

    @Override
    public String getDbType() {
        return dbType;
    }

    @Override
    public List<String> listSchema( String catalog, String schema ) throws Exception {

        String sch = "";
        if ( StringUtils.isNotBlank( schema ) ) {
            sch = schema;
        }
        else if ( StringUtils.isNotBlank( catalog ) ) {
            sch = catalog;
        }
        /*String sql = "SELECT object_name FROM all_objects WHERE object_type ='SCH' ";
        if ( StringUtils.isNotBlank( sch ) ) {
            sql = String.format( "%s and object_name='%s'", sql, sch.toUpperCase() );
        }
        logger.debug( sql );*/
        //Connection connection = con;
        //Statement statement = null;
        //ResultSet results = null;
        List<String> list = new ArrayList<>();
        if ( !genConn() ) {
            return null;
        }
        try {
            //ResultSet catalogs = con.getMetaData().getCatalogs();
            //while ( catalogs.next() ) {
            //    logger.info( catalogs.getString( "TABLE_CAT" ) );
            //}
            ResultSet schemas = con.getMetaData().getSchemas();
            while ( schemas.next() ) {
                //logger.info("{}.{}",schemas.getString("TABLE_CATALOG"), schemas.getString("TABLE_SCHEM") );
                String table_schem = schemas.getString( "TABLE_SCHEM" );
                if ( StringUtils.isBlank( sch ) || sch.equalsIgnoreCase( table_schem ) ) {
                    list.add( table_schem );
                }
            }
//            statement = con.createStatement();
//            results = statement.executeQuery(sql);
//            ResultSetMetaData rsmd = results.getMetaData();
//            while ( results.next() ){
//                if( schema == null || schema.equals("") || schema.equalsIgnoreCase( results.getString(1) )) {
//                    list.add(results.getString(1));
//                }
//            }
            return list;
        }
        catch ( Exception e ) {
            if ( raiseException )
                throw e;
            else {
                logger.error( "", e );
                return null;
            }
        }
//        finally {
//            colseObject( results, statement );
//        }
    }

    @Override
    public List<JdbcTableInfo> listTable( String catalog, String schema ) throws Exception {
        return listTableAndView( catalog, schema, 1 );
    }

    @Override
    public List<JdbcTableInfo> listView( String catalog, String schema ) throws Exception {
        return listTableAndView( catalog, schema, 2 );
    }

    @Override
    public List<JdbcTableInfo> listTableAndView( String catalog, String schema ) throws Exception {
        return listTableAndView( catalog, schema, 3 );
    }
    public List<JdbcTableInfo> listTableAndView( String catalog, String schema ,int tableViewType) throws Exception {
        String type ="";
        if ( tableViewType == 1 ) {
            type ="u";
        }
        else if ( tableViewType == 2 ) {
            type ="u";
        }
        else {
            type ="u";
        }
        String sql = "select a.table_name tablename, comments tablecomment ,a.owner schemaname from all_tables a LEFT JOIN all_tab_comments b \n" +
                "ON a.owner =b.owner AND a.table_name=b.table_name ";
        String sch = "";
        if ( StringUtils.isNotBlank( schema ) ) {
            sch = schema;
        }
        else if ( StringUtils.isNotBlank( catalog ) ) {
            sch = catalog;
        }
        if ( StringUtils.isNotBlank( sch ) ) {
            sql = String.format( " %s where a.owner ='%s' ", sql, sch.toUpperCase() );
        }
        logger.debug( sql );
        //Connection connection = con;
        Statement statement = null;
        ResultSet results = null;
        List<JdbcTableInfo> list = new ArrayList<>();
        if ( !genConn() ) {
            return null;
        }
        try {
            statement = con.createStatement();
            results = statement.executeQuery( sql );
            //ResultSetMetaData rsmd = results.getMetaData();
            while ( results.next() ) {
                list.add( new JdbcTableInfo( results.getString( "schemaname" ), results.getString( "tablename" ), results.getString( "tablecomment" ),true ) );
            }
            return list;
        }
        catch ( Exception e ) {
            if ( raiseException )
                throw e;
            else {
                logger.error( sql );
                return null;
            }
        }
        finally {
            colseObject( results, statement );
        }
    }

    @Override
    public List<JdbcColumnInfo> listColumn( String catalog, String schema, String table ) throws Exception {
        String sql = "select a.owner schemaname,a.TABLE_NAME tablename, a.COLUMN_NAME columnname,column_id ordinalposition,DATA_TYPE datatype,\n" +
                "DATA_TYPE columntype,NVL(data_precision,data_length) colen,data_length,data_precision,data_scale,comments columncomment FROM\n" +
                "all_tab_columns a LEFT JOIN all_col_comments b\n" +
                "ON a.owner=b.schema_name AND a.table_name=b.table_name and a.COLUMN_NAME=b.COLUMN_NAME  where 1=1 ";

        String sch = "";
        if ( StringUtils.isNotBlank( schema ) ) {
            sch = schema;
        }
        else if ( StringUtils.isNotBlank( catalog ) ) {
            sch = catalog;
        }
        if ( StringUtils.isNotBlank( sch ) ) {
            sql = String.format( " %s and a.owner ='%s' ", sql, sch.toUpperCase() );
        }
        if ( StringUtils.isNotBlank( table ) ) {
            sql = String.format( " %s and a.TABLE_NAME ='%s' ", sql, table );
        }
        sql = String.format( "%s order by column_id", sql );
        logger.debug( sql );
        //Connection connection = con;
        Statement statement = null;
        ResultSet results = null;
        List<JdbcColumnInfo> list = new ArrayList<>();
        if ( !genConn() ) {
            return null;
        }
        try {
            statement = con.createStatement();
            results = statement.executeQuery( sql );
            //ResultSetMetaData rsmd = results.getMetaData();
            while ( results.next() ) {
                JdbcColumnInfo col = new JdbcColumnInfo( results.getString( "schemaname" )
                        , results.getString( "tablename" )
                        , results.getString( "columnname" )
                        , results.getInt( "ordinalposition" )
                        , results.getString( "datatype" )
                        , results.getString( "columntype" )
                        , results.getString( "columncomment" ) );
                if ( results.getObject( "colen" ) != null ) {
                    col.setColen( results.getLong( "colen" ) );
                }
                if ( results.getObject( "data_scale" ) != null ) {
                    col.setColpercision( results.getInt( "data_scale" ) );
                }
                if ( "varchar".equalsIgnoreCase( col.getColumntype() ) || "char".equalsIgnoreCase( col.getColumntype() ) ) {
                    col.setColumntype( String.format( "%s(%d)", col.getColumntype(), col.getColen() ) );
                }
                else if ( "NUMERIC".equalsIgnoreCase( col.getColumntype() ) || "DECIMAL".equalsIgnoreCase( col.getColumntype() ) ) {
                    if ( col.getColen() != null && col.getColpercision() != null ) {
                        col.setColumntype( String.format( "%s(%d,%d)", col.getColumntype(), col.getColen(), col.getColpercision() ) );
                    }
                    else if ( col.getColen() != null ) {
                        col.setColumntype( String.format( "%s(%d)", col.getColumntype(), col.getColen() ) );
                    }
                }
                else if ( "TIMESTAMP".equalsIgnoreCase( col.getColumntype() ) && col.getColpercision() != null && col.getColpercision() > 0 ) {
                    col.setColumntype( String.format( "%s(%d)", col.getColumntype(), col.getColpercision() ) );
                }
                switch ( col.getColumntype().toUpperCase() ) {
                    case "REAL":
                        col.setDatatype( "FLOAT" );
                        break;
                    case "FLOAT":
                        col.setDatatype( "DOUBLE" );
                        break;
                    case "DOUBLE PRECISION":
                        col.setDatatype( "DOUBLE" );
                        break;
                    case "BIT":
                        col.setDatatype( col.getColumntype() );
                        break;
                    case "TINYINT":
                        col.setDatatype( "BYTE" );
                        break;
                    case "INT1":
                        col.setDatatype( "BYTE" );
                        break;
                    case "INT2":
                        col.setDatatype( "SMALLINT" );
                        break;
                    case "INT4":
                        col.setDatatype( "INT" );
                        break;
                    case "INT8":
                        col.setDatatype( "BIGINT" );
                        break;
                    case "FLOAT4":
                        col.setDatatype( "FLOAT" );
                        break;
                    case "FLOAT8":
                        col.setDatatype( "DOUBLE" );
                        break;
                    default:
                        col.setDatatype( col.getColumntype() );
                }
                list.add( col );
            }
            return list;
        }
        catch ( Exception e ) {
            if ( raiseException )
                throw e;
            else {
                logger.error( sql );
                return null;
            }
        }
        finally {
            colseObject( results, statement );
        }
    }

    @Override
    public List<String> getSysSchema() {
        return new ArrayList<>( Arrays.asList( "SYS", "SYSAUDITOR", "SYSSSO", "SYSDBA", "CTISYS" ) );
    }
}
