package cn.bywin.business.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTypeToJavaTypeUtil {
    protected  static final Logger logger = LoggerFactory.getLogger(JdbcTypeToJavaTypeUtil.class);
    public static String chgType(String type){
        String stype = type.toLowerCase();
        if( stype.indexOf("bool")>=0){
            return "Boolean";
        }

        if(stype.equals("string") || stype.indexOf("char")>=0||stype.indexOf("text")>=0){
            return "String";
        }
        if( stype.startsWith("double") || stype.startsWith("decimal")  || stype.startsWith("float") || stype.startsWith( "numeric" )){
            return "Double";
        }

//        if( stype.indexOf("datetime")>=0){
//            return "Timestamp";
//        }
        if( stype.indexOf("datetime")>=0 || stype.indexOf("timestamp")>=0 ||stype.indexOf("time")>=0){
            return "Date";
        }
        if( stype.indexOf("date")>=0){
            return "Date";
        }
        if( stype.indexOf("bigint")>=0){
            return "Long";
        }
        if( stype.indexOf("int")>=0){
            return "Integer";
        }
        if( stype.indexOf("year")>=0){
            return "Integer";
        }
        if( stype.indexOf("blob")>=0){
            return "Blob";
        }
        logger.warn( "未处理类型" + type );
        return type.substring(0,1).toUpperCase() + type.substring(1);
    }
    public static String chgTypeCom(String type){
        String stype = type.toLowerCase();
        if( stype.indexOf("bool")>=0 || stype.indexOf("bit")>=0){
            return "Boolean";
        }

        if(stype.equals("string") || stype.indexOf("char")>=0||stype.indexOf("text")>=0){
            return "String";
        }
        if( stype.startsWith("double") || stype.startsWith("decimal")  || stype.startsWith("float") || stype.startsWith("numeric")){
            return "Integer";
        }

//        if( stype.indexOf("datetime")>=0){
//            return "Timestamp";
//        }
        if( stype.indexOf("datetime")>=0 || stype.indexOf("timestamp")>=0){
            return "Date";
        }
        if( stype.indexOf("date")>=0){
            return "Date";
        }
        if( stype.indexOf("bigint")>=0){
            return "Integer";
        }
        if( stype.indexOf("int")>=0){
            return "Integer";
        }
        logger.warn( "未处理类型" + type );
        return type.substring(0,1).toUpperCase() + type.substring(1);
    }
    public static String chgTypeCh(String type){
        String stype = type.toLowerCase();
        if( stype.indexOf("bool")>=0){
            return "Boolean";
        }

        if(stype.equals("string") || stype.indexOf("char")>=0||stype.indexOf("text")>=0|| stype.startsWith("varchar")){
            return "String";
        }
        if( stype.startsWith("double") || stype.startsWith("decimal")  || stype.startsWith("float")){
            return "Double";
        }

//        if( stype.indexOf("datetime")>=0){
//            return "Timestamp";
//        }
        if( stype.indexOf("datetime")>=0 ){
            return "DateTime";
        }
        if(stype.indexOf("timestamp")>=0){
            return "timestamp";
        }
        if( stype.indexOf("date")>=0){
            return "Date";
        }
        if( stype.indexOf("bigint")>=0){
            return "Long";
        }
        if( stype.startsWith("int")){
            return "Integer";
        }
        logger.warn( "未处理类型" + type );
        return type.substring(0,1).toUpperCase() + type.substring(1);
    }
    /**
     * 转成ddb类型
     * @param type
     * @return
     */
    public static String chgDdbType(String type){
        String stype = type.toLowerCase();
//        if( stype.startsWith("bool") || stype.equals("tinyint(1)") ){
//            return "TINYINT(4)";
//        }
//        if( stype.startsWith("tinyint") ){
//            return "INT";
//        }
        if( stype.startsWith("timestamp") ){
            return "DATETIME";
        }
        if( stype.equals("char") || stype.equals("varchar")){
            return "varchar(255)";
        }
        if(stype.startsWith("char(") ){
            stype = "var"+stype;
        }
        if(stype.startsWith("varchar(") ){
            int idx1 = stype.indexOf("(");
            int idx2 = stype.indexOf(")");
            int len = Integer.parseInt( stype.substring( idx1+1,idx2) ) *3 +10;
            if( len>1000 ){
                return "STRING";
            }
            return String.format( "VARCHAR(%d)" , len );
        }

//        if(stype.equals("string") || stype.indexOf("char")>=0||stype.indexOf("text")>=0){
//            return "VARCHAR(128)";
//        }
//        if( stype.startsWith("double") || stype.startsWith("decimal")  || stype.startsWith("float")){
//            return "DECIMAL(27,9)";
//        }
//
////        if( stype.indexOf("datetime")>=0){
////            return "Timestamp";
////        }
//        if( stype.indexOf("datetime")>=0 || stype.indexOf("timestamp")>=0){
//            return "DATETIME";
//        }
//        if( stype.indexOf("date")>=0){
//            return "DATE";
//        }
//        if( stype.indexOf("bigint")>=0){
//            return "BIGINT";
//        }
//        if( stype.indexOf("int")>=0){
//            return "INT";
//        }
//        logger.warn( "未处理类型" + type );
        return type.toUpperCase();
    }
}
