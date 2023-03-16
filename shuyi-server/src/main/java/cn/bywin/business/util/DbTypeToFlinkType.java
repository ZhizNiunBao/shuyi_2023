package cn.bywin.business.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbTypeToFlinkType {
    protected final static Logger logger = LoggerFactory.getLogger( DbTypeToFlinkType.class );
    public static String chgType(String dbtype) {
        if ( dbtype == null || dbtype.trim().equals( "" ) ) {
            return null;
        }
        dbtype = dbtype.trim().toUpperCase();
        if ( dbtype.indexOf( "CHAR" )>=0 || dbtype.startsWith( "STRING" )
                || dbtype.indexOf( "TEXT" ) >=0  || dbtype.indexOf( "CLOB" )>=0) { //|| dbtype.indexOf( "BPCHAR" )>=0
            return "STRING";
        }
        if ( dbtype.startsWith( "TINYINT(1)" ) || dbtype.equals( "BIT" ) || dbtype.indexOf( "BOOL" )>=0 ) {
            return "BOOLEAN";
        }
        if ( dbtype.indexOf( "BLOB" )>=0 || dbtype.equals( "BYTEA" ) || dbtype.indexOf( "BINARY" )>=0   ) {
            return "BYTES";
        }
        if ( dbtype.startsWith( "BYTE" ) ) {
            return "BYTE";
        }
        if ( dbtype.startsWith( "SHORT" ) ) {
            return "SHORT";
        }
        if ( dbtype.startsWith( "TINYINT" ) ) {
            //return "TINYINT";
            return "TINYINT";
        }
        if ( dbtype.startsWith( "SMALLINT" ) ) {
            //return "SMALLINT";
            return "SMALLINT";
        }
        if ( dbtype.startsWith( "MEDIUMINT" ) || dbtype.startsWith( "INT" ) || dbtype.startsWith( "INTEGER" ) ) {
            return "INT";
        }
        if ( dbtype.startsWith( "LONG" ) || dbtype.startsWith( "BIGINT" ) ) {
            return "BIGINT";
        }
        if ( dbtype.startsWith( "FLOAT" )  || dbtype.startsWith( "REAL" ) ) {
            //return "DOUBLE";
            return "FLOAT";
        }
        if ( dbtype.startsWith( "DOUBLE" )  ) {
            //return "DOUBLE";
            return "DOUBLE";
        }
        if ( dbtype.startsWith( "REAL" ) || dbtype.equals( "MONEY" ) ) {
            //return "DOUBLE";
            return "DECIMAL(38, 10)";
        }
        if ( dbtype.equals( "TIME" ) ) {
            return "TIME";
        }
        if ( dbtype.equals( "DATE" ) ) {
            return "DATE";
        }
        if (dbtype.startsWith( "DATETIME" )|| dbtype.startsWith( "TIMESTAMP" ) ) {
            if( dbtype.indexOf( "(" )>0){
                return "TIMESTAMP".concat( dbtype.substring( dbtype.indexOf( "(" ) ) );
            }
            return "TIMESTAMP(3)";
        }
        if ( dbtype.indexOf( "DECIMAL" ) >=0 || dbtype.indexOf( "NUMERIC" ) >=0 || dbtype.indexOf( "NUMBER" ) >=0) {
            return "DECIMAL(38, 10)";
        }
        logger.error( "Unsupported type:{}", dbtype );
        return  dbtype;
    }


}
