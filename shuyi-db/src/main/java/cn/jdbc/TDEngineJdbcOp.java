package cn.jdbc;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TDEngineJdbcOp extends DbDataToObject implements IJdbcOp {
    Logger logger = LoggerFactory.getLogger( TDEngineJdbcOp.class );
    private final String defDriver = "com.taosdata.jdbc.rs.RestfulDriver";
    private String driver;
    private String url;
    private String user;
    private String password;

    private Connection con;

    private String dbType = JdbcOpBuilder.dbTDEngine;

    private boolean raiseException = true;

    public TDEngineJdbcOp(String driver, String url, String user, String password) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
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

    @Override
    public List selectData(String sql, List<Object> paras) throws Exception {
        return null;
    }

    @Override
    public List selectData(String sql, List<Object> paras, long start, long cnt) throws Exception {
        return null;
    }

    @Override
    public List selectData(String sql) throws Exception {
        return null;
    }

    @Override
    public <T> List<T> selectData(String sql, Class<T> cls) throws Exception {
        return null;
    }

    @Override
    public List selectData(String sql, long start, long cnt) throws Exception {
        return null;
    }

    @Override
    public List selectDataAsLinkMap(String sql) throws Exception {
        return null;
    }

    @Override
    public List selectDataAsLinkMap(String sql, long start, long cnt) throws Exception {
        return null;
    }

    @Override
    public boolean execute(String sql) throws Exception {
        return false;
    }

    @Override
    public boolean execute(String sql, List<Object> paras) throws Exception {
        return false;
    }

    @Override
    public List<FieldType> checkTableField(String sql) throws Exception {
        return null;
    }

    @Override
    public Long selectTableCount(String sql, List<Object> paras) throws Exception {
        return null;
    }

    @Override
    public String getDbType() {
        return dbType;
    }

    @Override
    public void setRaiseException(boolean raiseException) {

    }

    @Override
    public boolean isRaiseException() {
        return false;
    }

    @Override
    public List<String> listSchema(String catalog, String schema) throws Exception {
        String sql = "SHOW DATABASES";
        logger.debug(sql);
        //Connection connection = con;
        Statement statement = null;
        ResultSet results = null;
        List<String> list = new ArrayList<>();
        if (!genConn()) {
            return null;
        }
        try {
            statement = con.createStatement();
            results = statement.executeQuery(sql);
            ResultSetMetaData rsmd = results.getMetaData();
            while (results.next()) {
                if (schema == null || schema.equals("") || schema.equalsIgnoreCase(results.getString(1))) {
                    list.add(results.getString(1));
                }
            }
            return list;

        } catch (Exception e) {
            if (raiseException)
                throw e;
            else {
                logger.error("sql:{}", sql, e);
                e.printStackTrace();
                return null;
            }
        } finally {
            colseObject(results, statement);
        }
    }

    @Override
    public List<JdbcTableInfo> listTable(String catalog, String schema) throws Exception {
        return null;
    }

    @Override
    public List<JdbcTableInfo> listView(String catalog, String schema) throws Exception {
        return null;
    }

    @Override
    public List<JdbcTableInfo> listTableAndView(String catalog, String schema) throws Exception {
        String sql = "select TABLE_NAME tablename, TABLE_COMMENT `comment` ,db_name schemaname , type tabletype from information_schema.ins_tables ";

        if ( StringUtils.isNotBlank( schema ) ) {
            sql = String.format( " %s where db_name ='%s' ", sql, schema );
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
                list.add( new JdbcTableInfo( results.getString( "schemaname" ), results.getString( "tablename" ), results.getString( "comment" ), "NORMAL_TABLE".equalsIgnoreCase( results.getString( "tabletype" ) ) ) );
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
    public List<JdbcColumnInfo> listColumn(String catalog, String schema, String table) throws Exception {
        String sql = "desc ";
        if ( StringUtils.isNotBlank( schema ) ) {
            sql = String.format( " %s %s. ", sql, schema );
        }
        if ( StringUtils.isNotBlank( table ) ) {
            sql = String.format( " %s %s ", sql, table );
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
                JdbcColumnInfo col = new JdbcColumnInfo( schema
                        , table
                        , results.getString( "field" )
                        , 0
                        , results.getString( "type" ).toUpperCase()
                        , results.getString( "type" ).toUpperCase()
                        , results.getString( "note" ) );

                if ( results.getObject( "length" ) != null ) {
                    col.setColen( results.getLong( "length" ) );
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
        return new ArrayList<>( Arrays.asList( "INFORMATION_SCHEMA", "PERFORMANCE_SCHEMA", "TDENGINE" ) );
    }

    @Override
    public void close() throws Exception {

    }

    public void colseObject(AutoCloseable... autoCloObj) {
        if (autoCloObj == null) {
            return;
        }
        for (AutoCloseable autoCloseable : autoCloObj) {
            try {
                if (autoCloseable != null) {
                    autoCloseable.close();
                }
            } catch (Exception e) {

            }
        }
    }
}
