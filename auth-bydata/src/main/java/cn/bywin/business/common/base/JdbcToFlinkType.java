package cn.bywin.business.common.base;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcToFlinkType {
    protected static final Logger logger = LoggerFactory.getLogger(JdbcToFlinkType.class);
    public static String chgType(int intType, int precision, int scale, String typeName) {
        switch ( intType ){
            case  -7:
                if(precision <=1 )
                    return "BOOLEAN";
                else
                    return "INT";

            case -6: return "TINYINT";
            case -5: return "BIGINT";
            case -4: return"LONGVARBINARY";
            case -3: return"VARBINARY";
            case -2: return "BINARY";
            case -1: return "STRING";
            case 0: return "NULL";
            case 1:
                if( precision == 1){
                    return "CHAR";
                }
                return "STRING";
            case 2:
                //return "NUMERIC";
            case 3:
//                if( scale == 0){
//                    return "BIGINT";
//                }
//                return "DOUBLE";
                return "DECIMAL("+precision+","+scale+")";
            case 4: return "INT";
            case 5: return "SMALLINT";
            case 6:
                return "FLOAT";
            case 7: return "FLOAT";
            case 8: return "DOUBLE";
            case 12: return "STRING";
            case 91: return "DATE";
            case 92:
                if( scale == 0){
                    return "TIME";
                }
                return "TIME("+scale+")";
            case 93:
                if( scale == 0){
                    return "TIMESTAMP";
                }

                return "TIMESTAMP("+scale+")";

            case 1111:
                String other = typeName.toUpperCase();
                if( other.startsWith("DATETIME64")){
                    return "TIMESTAMP"+other.substring("DateTime64".length());
                }
                else if( other.startsWith("DATETIME")){
                    return "TIMESTAMP"+other.substring("DateTime".length());
                }
                return "OTHER";
        }
        return "";
    }
}
