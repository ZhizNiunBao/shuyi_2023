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
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StartRocksJdbcOp extends DbDataToObject implements IJdbcOp {
    Logger logger = LoggerFactory.getLogger( StartRocksJdbcOp.class );
    private final String defDriver = "com.mysql.jdbc.Driver";
    private String driver;
    private String url;
    private String user;
    private String password;

    private Connection con;

    private String dbType = JdbcOpBuilder.dbStarRocks;

    private boolean raiseException = true;

    public StartRocksJdbcOp( String driver, String url, String user, String password ) throws ClassNotFoundException {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
        if ( StringUtils.isBlank( this.driver ) ) {
            this.driver = defDriver;
        }
        Class.forName( this.driver );
        //genConn();
    }

    @Override
    public Long selectTableCount( String cntSql, List<Object> paraList ) throws Exception {
        List<Map<String, Object>> cntList = selectData( cntSql, paraList );
        Iterator<Map.Entry<String, Object>> iterator = cntList.get( 0 ).entrySet().iterator();
        if ( iterator.hasNext() ) {
            return Long.parseLong( iterator.next().getValue().toString() );
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
                //logger.info("{},{},{},{}",driver,url,user,password);
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
    public boolean checkConnect() {
        try {
            genConn();
            if ( con == null || con.isClosed() ) {
                return false;
            }
            else {
                return true;
            }
        }
        catch ( Exception ex ) {
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
                    statement.setTime( i + 1, (Time) paras.get( i ) );
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
                Map map;
                if ( "linkMap".equals( mapType ) )
                    map = new LinkedHashMap();
                else
                    map = new HashMap();
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
                logger.error( sql );
                e.printStackTrace();
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
                e.printStackTrace();
                return null;
            }
        }
        finally {
            colseObject( results, statement );
        }
    }

    @Override
    public List selectData( String sql, long start, long cnt ) throws Exception {
        String pagSql = PageSQLUtil.mysqlStartNum( sql, start, cnt );
        return selectData( pagSql );
    }

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
                e.printStackTrace();
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
            genConn();
            //final PreparedStatement preparedStatement = con.prepareStatement(sql);
            statement = con.createStatement();
            statement.execute( sql );
            return true;
        }
        catch ( Exception e ) {
            if ( raiseException )
                throw e;
            else {
                logger.error( sql );
                e.printStackTrace();
                return false;
            }
        }
        finally {
            colseObject( statement );
        }
    }

    @Override
    public List<FieldType> checkTableField( String sql ) throws Exception {
        String strsql = sql;
        if ( !sql.trim().endsWith( " limit 1" ) ) {
            strsql = PageSQLUtil.mysqlPageNum( sql, 1, 1 );
        }
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
                e.printStackTrace();
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
        //String sql = "SHOW SCHEMAS";
        String sql = "select SCHEMA_NAME schemaname from information_schema.schemata s ";
        logger.debug( sql );
        Statement statement = null;
        ResultSet results = null;
        String sch = schema;
        if ( StringUtils.isBlank( sch ) ) {
            sch = catalog;
        }

        List<String> list = new ArrayList<>();
        if ( !genConn() ) {
            return null;
        }
        try {
//            ResultSet schemas = con.getMetaData().getSchemas();
//            while ( schemas.next() ) {
//                String table_schem = schemas.getString( "TABLE_SCHEM" );
//                logger.info( table_schem );
//                if ( StringUtils.isBlank( sch ) || sch.equalsIgnoreCase( table_schem ) ) {
//                    list.add( table_schem );
//                }
//            }
            statement = con.createStatement();
            results = statement.executeQuery( sql );
            //ResultSetMetaData rsmd = results.getMetaData();
            while ( results.next() ) {
                if ( StringUtils.isBlank( sch ) || sch.equalsIgnoreCase( results.getString( 1 ) ) ) {
                    list.add( results.getString( 1 ) );
                }
            }
            return list;

        }
        catch ( Exception e ) {
            if ( raiseException )
                throw e;
            else {
                logger.error( "sql:{}", sql, e );
                e.printStackTrace();
                return null;
            }
        }
        finally {
            colseObject( results, statement );
        }
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

    public List<JdbcTableInfo> listTableAndView( String catalog, String schema, int tableViewType ) throws Exception {
        String sql = "select TABLE_NAME tablename, TABLE_COMMENT comment ,TABLE_SCHEMA schemaname , TABLE_TYPE tableType from information_schema.tables where  ";
        if ( tableViewType == 1 ) {
            sql = String.format( " %s TABLE_TYPE = 'BASE TABLE' ", sql );
        }
        else if ( tableViewType == 2 ) {
            sql = String.format( " %s TABLE_TYPE = 'VIEW' ", sql );
        }
        else {
            sql = String.format( " %s TABLE_TYPE in( 'BASE TABLE','VIEW' ) ", sql );
        }
        if ( StringUtils.isNotBlank( schema ) ) {
            sql = String.format( " %s and TABLE_SCHEMA ='%s' ", sql, schema );
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
                list.add( new JdbcTableInfo( results.getString( "schemaname" ), results.getString( "tablename" ), results.getString( "comment" ), "BASE TABLE".equalsIgnoreCase( results.getString( "tableType" ) ) ) );
            }
            return list;

        }
        catch ( Exception e ) {
            if ( raiseException )
                throw e;
            else {
                logger.error( sql );
                e.printStackTrace();
                return null;
            }
        }
        finally {
            colseObject( results, statement );
        }
    }

    @Override
    public List<JdbcColumnInfo> listColumn( String catalog, String schema, String table ) throws Exception {
        String sql = "select TABLE_SCHEMA schemaname,TABLE_NAME tablename, COLUMN_NAME columnname,ORDINAL_POSITION ordinalposition," +
                "DATA_TYPE datatype,COLUMN_TYPE columntype,COLUMN_COMMENT columncomment,CHARACTER_MAXIMUM_LENGTH charlen, NUMERIC_PRECISION numlen, DATETIME_PRECISION datelen,  NUMERIC_SCALE colpercision from information_schema.COLUMNS where 1=1 ";
        if ( StringUtils.isNotBlank( schema ) ) {
            sql = String.format( " %s and TABLE_SCHEMA ='%s' ", sql, schema );
        }
        if ( StringUtils.isNotBlank( table ) ) {
            sql = String.format( " %s and TABLE_NAME ='%s' ", sql, table );
        }
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
                        , results.getString( "columntype" ).toUpperCase()
                        , results.getString( "columntype" ).toUpperCase()
                        , results.getString( "columncomment" ) );

                if ( results.getObject( "charlen" ) != null ) {
                    col.setColen( results.getLong( "charlen" ) );
                }
                else if ( results.getObject( "numlen" ) != null ) {
                    col.setColen( results.getLong( "numlen" ) );
                }
                else if ( results.getObject( "datelen" ) != null ) {
                    col.setColen( results.getLong( "datelen" ) );
                }
                if ( results.getObject( "colpercision" ) != null ) {
                    col.setColpercision( results.getInt( "colpercision" ) );
                }
//                if ( "varchar".equalsIgnoreCase( col.getColumntype() ) || "char".equalsIgnoreCase( col.getColumntype() ) ) {
//                    col.setColumntype( String.format( "%s(%d)", col.getColumntype(), col.getColen() ) );
//                }
//                else if ( "NUMERIC".equalsIgnoreCase( col.getColumntype() ) || "DECIMAL".equalsIgnoreCase( col.getColumntype() ) ) {
//                    if ( col.getColen() != null && col.getColpercision() != null ) {
//                        col.setColumntype( String.format( "%s(%d,%d)", col.getColumntype(), col.getColen(), col.getColpercision() ) );
//                    }
//                    else if ( col.getColen() != null ) {
//                        col.setColumntype( String.format( "%s(%d)", col.getColumntype(), col.getColen() ) );
//                    }
//                }
//                else if ( "TIMESTAMP".equalsIgnoreCase( col.getColumntype() ) && col.getColpercision() != null && col.getColpercision() > 0 ) {
//                    col.setColumntype( String.format( "%s(%d)", col.getColumntype(), col.getColpercision() ) );
//                }
                switch ( col.getColumntype().toUpperCase() ) {
                    case "BIT":
                        col.setDatatype( col.getColumntype() );
                        break;
                    case "INT1":
                        col.setDatatype( "TINYINT" );
                        break;
                    case "INT2":
                        col.setDatatype( "SMALLINT" );
                        break;
                    case "INT4":
                    case "INT(11)":
                        col.setDatatype( "INT" );
                        break;
                    case "BIGINT(20)":
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
                e.printStackTrace();
                return null;
            }
        }
        finally {
            colseObject( results, statement );
        }
    }

    @Override
    public List<String> getSysSchema() {
        return new ArrayList<>( Arrays.asList( "INFORMATION_SCHEMA", "PERFORMANCE_SCHEMA", "MYSQL", "_STATISTICS_" ) );
    }
}
