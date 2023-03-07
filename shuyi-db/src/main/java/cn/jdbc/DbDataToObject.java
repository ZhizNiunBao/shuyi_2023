package cn.jdbc;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class DbDataToObject {
    public static Object toEffVal( String className,Object obj){
        if( obj == null )
            return null;
        if( className.equals( "java.lang.String" )){
            return obj.toString();
        }
        else if( className.equals("java.sql.Timestamp")){
            return new Date( ((Timestamp) obj).getTime());
        }
        else if(className.equals("java.sql.Date")){
            return new Date( ((java.sql.Date) obj).getTime());
        }
        else if(className.equals("java.sql.Time")){
            return new Date( ((java.sql.Time) obj).getTime());
        }
        else if(className.startsWith("java.lang.")){
            return obj;
        }
        else
            return obj;
    }

    public static Object toEffVal( Object obj,String newClss) throws  Exception{
        if( obj == null )
            return null;
        String type1 =obj.getClass().getSimpleName().toLowerCase();
        String type2 =newClss.toLowerCase();
        if( type1.endsWith(type2))
            return obj;

        if( "long".equals(type2)){
            return Long.parseLong(chg2NumStr( obj ).toString());
        }
        else if( "int".equals(type2) || "integer".equals(type2)){
            System.out.println(obj);
            return Integer.parseInt(chg2NumStr( obj ).toString());
        }
        if( "long".equals(type2)){
            return Long.parseLong(chg2NumStr( obj ).toString());
        }
        else if( "short".equals(type2)){
            return Short.parseShort( chg2NumStr( obj ).toString() );
        }
        else if( "double".equals( type2 ) ) {
            return Double.parseDouble( chg2NumStr( obj ).toString() );
        }
        else if( "float".equals( type2 ) ) {
            return Float.parseFloat( chg2NumStr( obj ).toString() );
        }
        else if( "date".equals( type2 ) ) {
            if( type1.endsWith("string")){
                if( obj.toString().trim().equals(""))
                    return null;
                SimpleDateFormat sf = null;
                if( obj.toString().length()==10)
                    sf =new SimpleDateFormat("yyyy-MM-dd" );
                else if( obj.toString().length()==19)
                    sf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
                else
                    sf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS" );
                //try {
                return sf.parse(obj.toString());
            }
            return new java.util.Date( Long.parseLong( chg2NumStr( obj ).toString() ) ) ;
        }
		else if( "stirng".equals( type2 ) ) {
			return obj.toString();
		}
        else{
            System.out.println( " 未知类型："+ type2 );
        }

        return obj.toString();

    }


    private static Object chg2NumStr( Object dataVal ){
        if( dataVal.getClass().equals(java.util.Date.class) ){
            return ""+((java.util.Date)dataVal).getTime();
        }
        else if( dataVal.getClass().equals(java.sql.Date.class) ){
            return ""+((java.sql.Date)dataVal).getTime();
        }
        else if( dataVal.getClass().equals(java.sql.Time.class) ){
            return ""+((java.sql.Time)dataVal).getTime();
        }
        else if( dataVal.getClass().equals(java.sql.Timestamp.class) ){
            return ""+((java.sql.Timestamp)dataVal).getTime();
        }
        //System.out.println("未知时间类型"+ dataVal.getClass().getName());
        return dataVal;
    }
    private static String chg2FormatStr( Object dataVal ){
        if( dataVal.getClass().equals(java.util.Date.class) ){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return sf.format((java.util.Date)dataVal);
        }
        else if( dataVal.getClass().equals(java.sql.Date.class) ){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return sf.format((java.sql.Date)dataVal);
        }
        else if( dataVal.getClass().equals(java.sql.Time.class) ){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return sf.format((java.sql.Time)dataVal);
        }
        else if( dataVal.getClass().equals(java.sql.Timestamp.class) ){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return sf.format((java.sql.Timestamp)dataVal);
        }
        //System.out.println("未知时间类型"+ dataVal.getClass().getName());
        return dataVal.toString();
    }
}
