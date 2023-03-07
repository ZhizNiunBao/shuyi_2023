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

public class PostgreSqlJdbcOp extends DbDataToObject implements IJdbcOp {
    Logger logger = LoggerFactory.getLogger( PostgreSqlJdbcOp.class );
    private final String defDriver = "org.postgresql.Driver";
    private String driver;
    private String url;
    private String user;
    private String password;

    private Connection con;

    private String dbType = JdbcOpBuilder.dbPostgreSql;

    private boolean raiseException = true;

    public PostgreSqlJdbcOp( String driver, String url, String user, String password ) throws ClassNotFoundException {
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
        String pagSql = PageSQLUtil.postgresqlStartNum( sql, start, cnt );
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

                    Object object = results.getObject( i );
                    if ( object == null ) {
                        map.put( rsmd.getColumnLabel( i ), null );
                    }
                    else {
                        if ( object.getClass().getName().startsWith( "org.postgresql.util." ) ) {
                            map.put( rsmd.getColumnLabel( i ), object.toString() );
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
        String pagSql = PageSQLUtil.postgresqlStartNum( sql, start, cnt );
        return selectData( pagSql );
    }

    @Override
    public List selectDataAsLinkMap( String sql, long start, long cnt ) throws Exception {
        String pagSql = PageSQLUtil.postgresqlStartNum( sql, start, cnt );
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

        String catlog = schema;
        String sch = "";
        String sql = "select datname from pg_catalog.pg_database";
        if ( StringUtils.isNotBlank( schema ) ) {
            int idx = schema.indexOf( "/" );
            if ( idx > 0 ) {
                catlog = schema.substring( 0, idx - 1 );
                sch = schema.substring( idx - 1 );
            }
            if ( sch.length() > 0 ) {
                sql += " where datname = '" + sch + "' ";
            }
        }
        logger.debug( sql );
        //Connection connection = con;
        Statement statement = null;
        ResultSet results = null;
        List<String> list = new ArrayList<>();
        if ( !genConn() ) {
            return null;
        }
        try {
            statement = con.createStatement();
            results = statement.executeQuery( sql );
            //ResultSetMetaData rsmd = results.getMetaData();
            while ( results.next() ) {
                if ( sch == null || sch.equals( "" ) || sch.equalsIgnoreCase( results.getString( 1 ) ) ) {
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
        String sql = "select TABLE_NAME as tablename, TABLE_NAME as comment ,TABLE_SCHEMA as schemaname from information_schema.tables ";
        if ( StringUtils.isNotBlank( schema ) ) {
            sql = String.format( " %s where TABLE_SCHEMA ='%s' ", sql, schema );
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
                list.add( new JdbcTableInfo( results.getString( "schemaname" ), results.getString( "tablename" ), results.getString( "comment" ),true ) );
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
        String sql = "select TABLE_SCHEMA schemaname,TABLE_NAME tablename, COLUMN_NAME columnname,ORDINAL_POSITION ordinalposition,\n" +
                "udt_name as datatype,concat( a.udt_name , case when a.character_maximum_length is not null then \n" +
                "concat( '(',character_octet_length ,')' ) \n" +
                "end ) as columntype,null columncomment from information_schema.COLUMNS a where 1=1 ";
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
                list.add( new JdbcColumnInfo( results.getString( "schemaname" )
                        , results.getString( "tablename" )
                        , results.getString( "columnname" )
                        , results.getInt( "ordinalposition" )
                        , results.getString( "datatype" )
                        , results.getString( "columntype" )
                        , results.getString( "columncomment" ) ) );
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
        return new ArrayList<>( Arrays.asList( "INFO_SCHEM", "STAGENT", "SYSAUDIT", "SYSDBA", "SYSFTSDBA" ) );
    }
}
