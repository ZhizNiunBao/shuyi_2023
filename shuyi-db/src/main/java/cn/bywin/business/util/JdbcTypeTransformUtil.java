package cn.bywin.business.util;

import cn.bywin.business.common.except.MessageException;
import cn.jdbc.JdbcOpBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTypeTransformUtil {
    protected  static final Logger logger = LoggerFactory.getLogger( JdbcTypeTransformUtil.class);
    public static String chgType(String fieldType,String dbType) throws MessageException {

        if ( JdbcOpBuilder.dbStarRocks.equalsIgnoreCase( dbType ) ) {
            return chgToStarRocksType( fieldType );
        }
        else if ( JdbcOpBuilder.dbMySql.equalsIgnoreCase( dbType ) ||
                JdbcOpBuilder.dbMySql8.equalsIgnoreCase( dbType ) ||
                JdbcOpBuilder.dbMySql5.equalsIgnoreCase( dbType ) ||
                JdbcOpBuilder.dbTiDb.equalsIgnoreCase( dbType )
        ) {
            return chgToMysqlType( fieldType );
        }
        else{
            logger.error( "未实现类型转换"+ dbType );
            return chgToNodealType( fieldType );
        }
    }

    public static String chgToStarRocksType(String fieldType) throws MessageException {

        String ft1 = fieldType.toUpperCase( );
        String tail = "";
        if( ft1.indexOf( "(" )>0){
            tail  =ft1.substring( ft1.indexOf( "(" ) );
            ft1 = ft1.substring( 0, ft1.indexOf( "(" ) );
            //logger.info("{},{}" ,ft1, tail );
        }
//        if ( ft1.indexOf( "DOUBLE" ) >= 0 || ft1.indexOf( "FLOAT" ) >= 0 ) {
//            if( tail.length()>0 ){
//                return   "DECIMAL"+tail;
//            }
//            return  ft1 ;
//        }
        if( ft1.indexOf("BOOL")>=0){
            return "BOOLEAN";
        }
//        if ( ft1.equals( "TIME" ) ) {
//            throw new MessageException( "不支持数据类型".concat( fieldType ) );
//        }
        if ( ft1.indexOf( "BLOB" ) >= 0 ) {
            throw new MessageException(  "不支持数据类型".concat( fieldType ) );
        }

        if ( ft1.indexOf( "DOUBLE" ) >= 0 || ft1.indexOf( "FLOAT" ) >= 0 || ft1.startsWith("REAL") || ft1.indexOf( "NUMERIC" ) >= 0 || ft1.indexOf( "DECIMAL" ) >= 0 || ft1.indexOf( "NUMBER" ) >= 0 ) {
            if( tail.length()>0 ){
                if( tail.indexOf( "," )>=0) {
                    return "DECIMAL" + tail;
                }
                else{
                    return "DECIMAL(38,10)";
                }
            }
            return "DECIMAL(38,10)";
            //return  ft1 ;
        }

//        if( ft1.startsWith("DOUBLE") || ft1.startsWith("FLOAT") || ft1.startsWith("REAL") ){
//            if( tail.length()>0 ){
//                return   "DECIMAL"+tail;
//            }
//            return  ft1 ;
//        }

        if ( ft1.indexOf( "TEXT" ) >= 0 || ft1.indexOf( "STRING" ) >= 0 || ft1.indexOf( "CLOB" ) >= 0) {
            return  "STRING" ;
        }
        if ( ft1.indexOf( "TINYINT" ) >= 0 ||ft1.indexOf( "BIT" ) >= 0) {
            return   "TINYINT" ;
        }
        if ( ft1.indexOf( "MEDIUMINT" ) >= 0 ) {
            return   "INT" ;
        }

        if ( ft1.indexOf( "SMALLINT" ) >= 0 ) {
            return   "SMALLINT" ;
        }

        if( ft1.indexOf("BIGINT")>=0){
            return "BIGINT";
        }
        if( ft1.indexOf("INT")>=0){
            return "INT";
        }
        if( ft1.indexOf("YEAR")>=0){
            return "INT";
        }
        if ( ft1.equals( "DATE" ) ) {
            return   "DATE" ;
        }

        if ( ft1.indexOf( "TIME" ) >= 0 ) {
            return   "DATETIME" ;
        }

        if ( ft1.indexOf( "CHAR" ) >= 0 ) {
            if( tail.length()==0 ){
                return   "STRING";
            }
            int idx = fieldType.indexOf( "(" );
            String type2 = fieldType.substring( idx + 1 );
            idx = type2.indexOf( ")" );
            int len = Integer.parseInt( type2.substring( 0, idx ) );
            if ( len == -1 || len * 3 > 1000 ) {
                return  "STRING" ;
            }
            len = len * 3;
            return  "VARCHAR(".concat( Integer.toString( len ) ).concat( ")" ) ;
        }
        throw new MessageException( "未知类型".concat( fieldType )  );
        //return   fieldType.toUpperCase();

    }

    public static String chgToMysqlType(String fieldType) throws MessageException {

        String ft1 = fieldType.toUpperCase( );
        String tail = "";
        if( ft1.indexOf( "(" )>0){
            tail  =ft1.substring( ft1.indexOf( "(" ) );
            ft1 = ft1.substring( 0, ft1.indexOf( "(" ) );
            logger.info("{},{}" ,ft1, tail );
        }
        if ( ft1.indexOf( "DOUBLE" ) >= 0 || ft1.indexOf( "FLOAT" ) >= 0 ) {
            if( tail.length()>0 ){
                return   "DECIMAL"+tail;
            }
            return  ft1 ;
        }
        if( ft1.indexOf("BOOL")>=0){
            return "TINYINT";
        }
        if ( ft1.equals( "TIME" ) ) {
            return "TIME";
            //throw new MessageException( "不支持数据类型".concat( fieldType ) );
        }
        if ( ft1.indexOf( "BLOB" ) >= 0 ) {
            return "BLOB";
            //throw new MessageException(  "不支持数据类型".concat( fieldType ) );
        }

        if ( ft1.indexOf( "NUMERIC" ) >= 0 || ft1.indexOf( "DECIMAL" ) >= 0 ) {
            if( tail.length()>0 ){
                return   "DECIMAL"+tail;
            }
            return  ft1 ;
        }

        if( ft1.startsWith("DOUBLE") || ft1.startsWith("FLOAT") ){
            if( tail.length()>0 ){
                return   "DECIMAL"+tail;
            }
            return  ft1 ;
        }

        if ( ft1.indexOf( "TEXT" ) >= 0 || ft1.indexOf( "STRING" ) >= 0 || ft1.indexOf( "CLOB" ) >= 0) {
            return  "TEXT" ;
        }
        if ( ft1.indexOf( "TINYINT" ) >= 0 ) {
            return   "TINYINT" ;
        }
        if ( ft1.indexOf( "MEDIUMINT" ) >= 0 ) {
            return   "INT" ;
        }

        if ( ft1.indexOf( "SMALLINT" ) >= 0 ) {
            return   "SMALLINT" ;
        }

        if( ft1.indexOf("BIGINT")>=0){
            return "BIGINT";
        }
        if( ft1.indexOf("INT")>=0){
            return "INT";
        }
        if( ft1.indexOf("YEAR")>=0){
            return "INT";
        }
        if ( ft1.equals( "DATE" ) ) {
            return   "DATE" ;
        }
        if ( ft1.indexOf( "TIME" ) >= 0 ) {
            return   "DATETIME" ;
        }

        if ( ft1.indexOf( "CHAR" ) >= 0 ) {
            if( tail.length()==0 ){
                return   "TEXT";
            }
            int idx = fieldType.indexOf( "(" );
            String type2 = fieldType.substring( idx + 1 );
            idx = type2.indexOf( ")" );
            int len = Integer.parseInt( type2.substring( 0, idx ) );
            if ( len == -1 || len * 3 > 1000 ) {
                return  "TEXT" ;
            }
            len = len * 3;
            return  "VARCHAR(".concat( Integer.toString( len ) ).concat( ")" ) ;
        }
        throw new MessageException( "未知类型".concat( fieldType )  );
        //return   fieldType.toUpperCase();

    }

    public static String chgToNodealType(String fieldType) throws MessageException {

        String ft1 = fieldType.toUpperCase( );
        String tail = "";
        if( ft1.indexOf( "(" )>0){
            tail  =ft1.substring( ft1.indexOf( "(" ) );
            ft1 = ft1.substring( 0, ft1.indexOf( "(" ) );
            logger.info("{},{}" ,ft1, tail );
        }
        if ( ft1.indexOf( "DOUBLE" ) >= 0 || ft1.indexOf( "FLOAT" ) >= 0 ) {
            if( tail.length()>0 ){
                return   "DECIMAL"+tail;
            }
            return  ft1 ;
        }
        if( ft1.indexOf("BOOL")>=0){
            return "BOOL";
        }
        if ( ft1.equals( "TIME" ) ) {
            throw new MessageException( "不支持数据类型".concat( fieldType ) );
        }
        if ( ft1.indexOf( "BLOB" ) >= 0 ) {
            throw new MessageException(  "不支持数据类型".concat( fieldType ) );
        }

        if ( ft1.indexOf( "NUMERIC" ) >= 0 || ft1.indexOf( "DECIMAL" ) >= 0 ) {
            if( tail.length()>0 ){
                return   "DECIMAL"+tail;
            }
            return  ft1 ;
        }

        if( ft1.startsWith("DOUBLE") || ft1.startsWith("FLOAT") ){
            if( tail.length()>0 ){
                return   "DECIMAL"+tail;
            }
            return  ft1 ;
        }

        if ( ft1.indexOf( "TEXT" ) >= 0 || ft1.indexOf( "STRING" ) >= 0 || ft1.indexOf( "CLOB" ) >= 0) {
            return  "CLOB" ;
        }
        if ( ft1.indexOf( "TINYINT" ) >= 0 ) {
            return   "TINYINT" ;
        }
        if ( ft1.indexOf( "MEDIUMINT" ) >= 0 ) {
            return   "INT" ;
        }

        if ( ft1.indexOf( "SMALLINT" ) >= 0 ) {
            return   "SMALLINT" ;
        }

        if( ft1.indexOf("BIGINT")>=0){
            return "BIGINT";
        }
        if( ft1.indexOf("INT")>=0){
            return "INT";
        }
        if( ft1.indexOf("YEAR")>=0){
            return "INT";
        }
        if ( ft1.equals( "DATE" ) ) {
            return   "DATE" ;
        }
        if ( ft1.indexOf( "TIME" ) >= 0 ) {
            return   "DATETIME" ;
        }

        if ( ft1.indexOf( "CHAR" ) >= 0 ) {
            if( tail.length()==0 ){
                return   "CLOB";
            }
            int idx = fieldType.indexOf( "(" );
            String type2 = fieldType.substring( idx + 1 );
            idx = type2.indexOf( ")" );
            int len = Integer.parseInt( type2.substring( 0, idx ) );
            if ( len == -1 || len * 3 > 1000 ) {
                return  "CLOB" ;
            }
            len = len * 3;
            return  "VARCHAR(".concat( Integer.toString( len ) ).concat( ")" ) ;
        }
        throw new MessageException( "未知类型".concat( fieldType )  );
        //return   fieldType.toUpperCase();

    }
}
