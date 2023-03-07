package cn.jdbc;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class JdbcOpBuilder {
    protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

    public final static String dbClickHouse ="CLICKHOUSE";
    public final static String dbTDEngine ="TDENGINE";
    public final static String dbMySql ="MYSQL";
    public final static String dbMySql5 ="MYSQL5";
    public final static String dbMySql8 ="MYSQL8";
    public final static String dbTiDb ="TIDB";
    public final static String dbOracle ="ORACLE";
    public final static String dbPostgreSql ="POSTGRESQL";
    public final static String dbPresto ="PRESTO";
    public final static String dbHetu ="HETU";
    public final static String dbOdps = "ODPS";
    public final static String dbDm ="DM";
    public final static String dbStarRocks ="STARROCKS";
    public final static String dbKingBase ="KINGBASE";
    public final static String dbKingBase8 ="KINGBASE8";
    public final static String dbOscar ="OSCAR";
    public final static String dbOpenGauss ="OPENGAUSS";
    public final static String dbSqlServer ="SQLSERVER";
    public final static String dbOpenLooKeng ="OPENLOOKENG";

    public final static String[] dbTypeList=
            {dbClickHouse,dbMySql,dbMySql5,dbMySql8,dbTiDb,dbOracle,
                    dbPostgreSql,dbPresto,dbHetu,dbOdps,dbDm, dbStarRocks,dbKingBase8,dbKingBase,dbOscar,dbOpenGauss,dbSqlServer,dbOpenLooKeng,dbTDEngine};

    private String url;

    //private String db;

    private String catalog;

    private String schema;

    private String dbType;

    private String user;

    private String password;

    private String driver;

    public JdbcOpBuilder withSet(String dbType,String driver, String url,String user,String password ) {
        this.dbType = dbType;
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
        return this;
    }

    public JdbcOpBuilder withUrl( String url ) {
        this.url = url;
        return this;
    }

    public JdbcOpBuilder withCatalog( String catalog ) {
        this.catalog = catalog;
        return this;
    }

    public JdbcOpBuilder withSchema( String schema ) {
        this.schema = schema;
        return this;
    }

    public JdbcOpBuilder withDbType( String dbType ) {
        this.dbType = dbType;
        return this;
    }

    public JdbcOpBuilder withUser( String user ) {
        this.user = user;
        return this;
    }

    public JdbcOpBuilder withPassword( String password ) {
        this.password = password;
        return this;
    }

    public JdbcOpBuilder withDriver( String driver ) {
        this.driver = driver;
        return this;
    }

    public static String genUrl( String dbType, String ip, int port, String dbName ) {
        String type = dbType.toUpperCase();
        if ( dbName == null ) {
            dbName = "";
        }
        if ( dbName.indexOf( "/" ) > 0 ) {
            dbName = dbName.substring( 0, dbName.indexOf( "/" ) - 1 );
        }
        switch ( type ) {
            case JdbcOpBuilder.dbTDEngine:
                return String.format( "jdbc:TAOS-RS://%s:%d/%s", ip, port, dbName );
            case JdbcOpBuilder.dbClickHouse:
                return String.format( "jdbc:clickhouse://%s:%d/%s", ip, port, dbName );
            case JdbcOpBuilder.dbMySql5:
            case JdbcOpBuilder.dbTiDb:
                return String.format( "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf8&useOldAliasMetadataBehavior=true&allowMultiQueries=true&rewriteBatchedStatements=true&useSSL=false", ip, port, dbName );
            case JdbcOpBuilder.dbMySql8:
                return String.format( "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf8&useOldAliasMetadataBehavior=true&allowMultiQueries=true&rewriteBatchedStatements=true&useSSL=false", ip, port, dbName );
            case JdbcOpBuilder.dbMySql:
                return String.format( "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf8&useOldAliasMetadataBehavior=true&allowMultiQueries=true&rewriteBatchedStatements=true&useSSL=false", ip, port, dbName );
            case JdbcOpBuilder.dbStarRocks:
                return String.format( "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf8&useOldAliasMetadataBehavior=true&allowMultiQueries=true&rewriteBatchedStatements=true&useSSL=false", ip, port, dbName );
            case JdbcOpBuilder.dbDm:
                return String.format( "jdbc:dm://%s:%d/%s", ip, port, dbName );
            case JdbcOpBuilder.dbKingBase:
            case JdbcOpBuilder.dbKingBase8:
                return String.format( "jdbc:kingbase8://%s:%d/%s", ip, port, dbName );
            case JdbcOpBuilder.dbOscar:
                return String.format( "jdbc:oscar://%s:%d/%s", ip, port, dbName );
            //            case  dbOpenGauss:
            //                return String.format("jdbc:opengauss://%s:%s/%s",ip,port,dbName);
            case JdbcOpBuilder.dbOpenGauss:
                return String.format( "jdbc:postgresql://%s:%d/%s", ip, port, dbName );
            case JdbcOpBuilder.dbOracle:
                return String.format( "jdbc:oracle:thin:@%s:%d:%s", ip, port, dbName );
            case JdbcOpBuilder.dbSqlServer:
                return String.format( "jdbc:sqlserver://%s:%d%s", ip, port, dbName.length() > 0 ? "; DatabaseName=" + dbName : "" );
            case JdbcOpBuilder.dbOpenLooKeng:
                return String.format( "jdbc:lk://%s:%d/%s", ip, port, dbName );
        }
        return null;
    }

    public static String findDrive( String dbType ) {
        String type = dbType.toUpperCase();
        switch ( type ) {
            case JdbcOpBuilder.dbTDEngine:
                return "com.taosdata.jdbc.rs.RestfulDriver";
            case JdbcOpBuilder.dbClickHouse:
                return "ru.yandex.clickhouse.ClickHouseDriver";
            case JdbcOpBuilder.dbMySql5:
            case JdbcOpBuilder.dbTiDb:
                return "com.mysql.jdbc.Driver";
            case JdbcOpBuilder.dbMySql8:
                return "com.mysql.cj.jdbc.Driver";
            case JdbcOpBuilder.dbMySql:
                return "com.mysql.jdbc.Driver";
            case JdbcOpBuilder.dbStarRocks:
                return "com.mysql.jdbc.Driver";
            case JdbcOpBuilder.dbDm:
                return "dm.jdbc.driver.DmDriver";
            case JdbcOpBuilder.dbKingBase:
            case JdbcOpBuilder.dbKingBase8:
                return "com.kingbase8.Driver";
            case JdbcOpBuilder.dbOscar:
                return "com.oscar.Driver";
//            case  dbOpenGauss:
//                return "org.opengauss.Driver";
            case JdbcOpBuilder.dbOpenGauss:
                return "org.postgresql.Driver";
            case JdbcOpBuilder.dbPostgreSql:
                return "org.postgresql.Driver";
            case JdbcOpBuilder.dbOracle:
                return "oracle.jdbc.OracleDriver";
            case JdbcOpBuilder.dbSqlServer:
                return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case JdbcOpBuilder.dbOpenLooKeng:
                return "io.hetu.core.jdbc.OpenLooKengDriver";
        }

        return null;
    }

    public IJdbcOp build() throws SQLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //logger.info("dbType:{},driver:{},url:{},db:{},user:{},password:{}",dbType,driver,url,db,user, StringUtils.isNotBlank(password));

        if( StringUtils.isBlank( driver ) ){
            driver = JdbcOpBuilder.findDrive( dbType );
        }
        String clsPath = this.getClass().getName();
        int idx = clsPath.lastIndexOf( "." );
        clsPath = clsPath.substring( 0, idx+1 );
        String type = dbType.toUpperCase();
        Class<?> aClass;
        Constructor ct;
        switch ( type ) {
            case JdbcOpBuilder.dbClickHouse:
                clsPath = clsPath.concat( "ChJdbcOp" );
                aClass = Class.forName( clsPath );
                ct = aClass.getDeclaredConstructor(String.class,String.class,String.class,String.class,String.class);
                return (IJdbcOp)ct.newInstance(new Object[] {driver, url, catalog, user, password});
            case JdbcOpBuilder.dbMySql5:
                clsPath = clsPath.concat( "MysqlJdbcOp" );
                break;
            case JdbcOpBuilder.dbTiDb:
                clsPath = clsPath.concat( "TiDbJdbcOp" );
                break;
            case JdbcOpBuilder.dbTDEngine:
                clsPath = clsPath.concat( "TDEngineJdbcOp" );
                break;
            case JdbcOpBuilder.dbMySql8:
                clsPath = clsPath.concat( "MysqlJdbcOp" );
                break;
            case JdbcOpBuilder.dbMySql:
                clsPath = clsPath.concat( "MysqlJdbcOp" );
                break;
            case JdbcOpBuilder.dbStarRocks:
                clsPath = clsPath.concat( "StartRocksJdbcOp" );
                break;
            case JdbcOpBuilder.dbDm:
                clsPath = clsPath.concat( "DmJdbcOp" );
                break;
            case JdbcOpBuilder.dbKingBase:
            case JdbcOpBuilder.dbKingBase8:
                clsPath = clsPath.concat( "KingbaseJdbcOp" );
                break;
            case JdbcOpBuilder.dbOscar:
                clsPath = clsPath.concat( "OscarJdbcOp" );
                break;
//            case  dbOpenGauss:
//                return "org.opengauss.Driver";
            case JdbcOpBuilder.dbOpenGauss:
                clsPath = clsPath.concat( "OpenGaussJdbcOp" );
                break;
            case JdbcOpBuilder.dbPostgreSql:
                clsPath = clsPath.concat( "PostgreSqlJdbcOp" );
                break;
            case JdbcOpBuilder.dbOracle:
                clsPath = clsPath.concat( "OracleJdbcOp" );
                break;
            case JdbcOpBuilder.dbSqlServer:
                clsPath = clsPath.concat( "SqlServerJdbcOp" );
                break;
            case JdbcOpBuilder.dbOpenLooKeng:
                clsPath = clsPath.concat( "OpenLooKengJdbcOp" );
                break;
            default:
                clsPath = clsPath.concat( "CommonJdbcOp" );
                aClass = Class.forName( clsPath );
                ct = aClass.getDeclaredConstructor(String.class,String.class,String.class,String.class,String.class);
                return (IJdbcOp)ct.newInstance(new Object[] {dbType, driver, url, user, password});
        }
        aClass = Class.forName( clsPath );
        ct = aClass.getDeclaredConstructor(String.class,String.class,String.class,String.class);
        return (IJdbcOp)ct.newInstance(new Object[] {driver, url, user, password});
        /*switch ( type ) {
            case JdbcOpBuilder.dbClickHouse:
                return new ChJdbcOp( driver, url, catalog, user, password );
            case JdbcOpBuilder.dbMySql5:
                return new MysqlJdbcOp( driver, url, user, password );
            case JdbcOpBuilder.dbTiDb:
                return new TiDbJdbcOp( driver, url, user, password );
            case JdbcOpBuilder.dbMySql8:
                return new MysqlJdbcOp( driver, url, user, password );
            case JdbcOpBuilder.dbMySql:
                return new MysqlJdbcOp( driver, url, user, password );
            case JdbcOpBuilder.dbStartRocks:
                return new StartRocksJdbcOp( driver, url, user, password );
            case JdbcOpBuilder.dbDm:
                return new DmJdbcOp( driver, url, user, password );
            case JdbcOpBuilder.dbKingBase:
            case JdbcOpBuilder.dbKingBase8:
                return new KingbaseJdbcOp( driver, url, user, password );
            case JdbcOpBuilder.dbOscar:
                return new OscarJdbcOp( driver, url, user, password );
//            case  dbOpenGauss:
//                return "org.opengauss.Driver";
            case JdbcOpBuilder.dbOpenGauss:
                return new OpenGaussJdbcOp( driver, url, user, password );
            case JdbcOpBuilder.dbPostgreSql:
                return new PostgreSqlJdbcOp( driver, url, user, password );
            case JdbcOpBuilder.dbOracle:
                return new OracleJdbcOp( driver, url, user, password );
            case JdbcOpBuilder.dbSqlServer:
                return new SqlServerJdbcOp( driver, url, user, password );
            default:
                return new CommonJdbcOp( dbType, driver, url, user, password );
        }*/
    }
}
