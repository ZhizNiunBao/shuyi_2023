package cn.bywin.business.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class ComUtil {
    /**
     * 短时间yyyy-MM-dd
     */
    public static String shortDtFormat = "yyyy-MM-dd";
    /**
     * 长时间yyyy-MM-dd-HH-mm-ss
     */
    public static String strDtFormat = "yyyy-MM-dd-HH-mm-ss";
    /**
     * 长时间yyyy-MM-dd HH:mm:ss
     */
    public static String longDtFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 长时间yyyy-MM-dd HH:mm:ss.SSS
     */
    public static String milDtFormat = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 时间HH:mm:ss
     */
    public static String timeFormat = "HH:mm:ss";
    /**
     * 时间HH:mm:ss.SSS
     */
    public static String milTimeFormat = "HH:mm:ss.SSS";

    /**
     * 判断数据为null转成""，否则返回自己
     *
     * @param str
     * @return
     */
    //private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>();
    public static String trsEmpty( String str ) {
        if ( str == null )
            return "";
        return str;
    }

    /**
     * 判断数据为null转成指定def，否则返回自己
     *
     * @param str
     * @param def 默认值
     * @return
     */
    public static String trsEmpty( String str, String def ) {
        if ( StringUtils.isBlank( str ) )
            return def;
        return str;
    }
    /**
     * 判断数据是否为空 null 和 "" 都判断为空
     * @param str
     * @return
     */
//	public static  boolean isNull(String str){
//		if( str == null || str.equals(""))
//			return true;
//		return false;
//	}
    /**
     * 判断数据是否为不空 null 和 "" 都判断为空
     * @param str
     * @return
     */
//	public static  boolean isNotNull(String str){
//		if( str == null || str.equals(""))
//			return false;
//		return true;
//	}

    /**
     * 自动根据时间字符串长度转成时间
     * 默认短格式yyyy-MM-dd
     * 默认长格式yyyy-MM-dd HH:mm:ss
     *
     * @param str
     * @return
     */
    public static Date strToDate( String str ) {
        Date date = null;
        if ( str != null || str.trim().length() > 0 ) {
            String fmtStr = longDtFormat;
            str = str.trim();
            if ( str.trim().length() <= 10 ) {
                fmtStr = shortDtFormat;
            }
            SimpleDateFormat format = new SimpleDateFormat( fmtStr );
            try {
                date = format.parse( str );
            }
            catch ( ParseException e ) {
                // TODO Auto-generated catch block
            }
        }
        return date;
    }

    /**
     * 按格式将字符串转成时间
     *
     * @param str
     * @param fmtStr
     * @return
     */
    public static Date strToDate( String str, String fmtStr ) {
        Date date = null;
        if ( str != null || str.trim().length() > 0 ) {
            SimpleDateFormat format = new SimpleDateFormat( fmtStr );
            try {
                date = format.parse( str.trim() );
            }
            catch ( ParseException e ) {
                // TODO Auto-generated catch block
            }
        }
        return date;
    }

    /**
     * 时间转化成yyyy-MM-dd
     *
     * @param dt
     * @return
     */
    public static String dateToStr( Date dt ) {
        if ( dt == null )
            return "";
        //SimpleDateFormat format = new SimpleDateFormat(shortDtFormat);
        //return  format.format(dt);
        return DateFormatUtils.format( dt, shortDtFormat );
    }

    /**
     * 时间转化成yyyy-MM-dd HH:mm:ss
     *
     * @param dt
     * @return
     */
    public static String dateToLongStr( Date dt ) {
        if ( dt == null )
            return "";
        //SimpleDateFormat format = new SimpleDateFormat(longDtFormat);
        //return  format.format(dt);
        return DateFormatUtils.format( dt, longDtFormat );
    }

    /**
     * 时间转化成指定格式字符串
     *
     * @param dt
     * @param fmtStr
     * @return
     */
    public static String dateToStr( Date dt, String fmtStr ) {
        if ( dt == null )
            return "";
        //SimpleDateFormat format = new SimpleDateFormat(fmtStr);
        //return  format.format(dt);
        return DateFormatUtils.format( dt, fmtStr );
    }

    /**
     * 时间转化成yyyy-MM-dd
     *
     * @param dt
     * @return
     */
    public static String dateToEffStr( Date dt ) {
        if ( dt == null )
            return "";
        String strdate = DateFormatUtils.format( dt, milDtFormat );
        if ( strdate.endsWith( ".000" ) ) {
            strdate = strdate.substring( 0, strdate.length() - 4 );
            if ( strdate.endsWith( " 00:00:00" ) ) {
                strdate = strdate.substring( 0, strdate.length() - 9 );
            }
        }
        return strdate;
    }

    /**
     * 获取星期一
     *
     * @param dt
     * @return
     */
    public static Date getFirstDayOfWeek( Date dt ) {
        Calendar cal = Calendar.getInstance();
        cal.setTime( dt );
        int nweek = cal.get( Calendar.DAY_OF_WEEK );
        if ( nweek >= 2 ) {
            nweek = 2 - nweek;
        }
        else {
            nweek = nweek - 7;
        }
        cal.add( Calendar.DATE, nweek );
        return cal.getTime();
    }

    /**
     * 取当前时间转成Timestamp
     *
     * @return
     */
    public static Timestamp getCurTimestamp() {
        return new Timestamp( System.currentTimeMillis() );
    }

    public static Timestamp getCurTimestamp( String format ) {
        Date date = strToDate( dateToStr( new Date(), format ), format );
        return new Timestamp( date.getTime() );
    }

    /**
     * 时间转化成yyyy-MM-dd-HH-mm-ss 
     *strDtFormat
     * @param
     * @return
     */
    public static String dateToTimestampStr( Date date ) {

        SimpleDateFormat format = new SimpleDateFormat( strDtFormat );
        return format.format( date );
    }

    /**
     * 将时间转成Timestamp
     *
     * @param dt
     * @return
     */
    public static Timestamp chgToTimestamp( Date dt ) {
        if ( dt == null )
            return null;
        return new Timestamp( dt.getTime() );
    }

    /**
     * 取当天yyyy-MM-dd
     *
     * @return str
     */
    public static String getCurStrDt() {
        return dateToStr( new Date() );
    }

    /**
     * 取当天yyyy-MM-dd
     *
     * @return Date
     */
    public static Date getCurDt() {
        return strToDate( getCurStrDt() );
    }

    public static String chgLikeStr( String strVal ) {
        if ( strVal == null || strVal.trim().equals( "" ) )
            return null;
        return "%" + strVal.toString().trim().replaceAll( "\\\\", "\\\\\\\\" ).replaceAll( "%", "\\%" )
                .replaceAll( "\\_", "\\\\_" ).replaceAll( "\\%", "\\\\%" ) + "%";
    }

    public static String chgEquStr( String strVal ) {
        if ( strVal == null || strVal.trim().equals( "" ) )
            return null;
        return strVal.toString().trim().replaceAll( "\\\\", "\\\\\\\\" ).replaceAll( "%", "\\%" )
                .replaceAll( "\\_", "\\\\_" ).replaceAll( "\\%", "\\\\%" );
    }

    /**
     * 将double 转成字符
     *
     * @param db
     * @return
     */
    public static String dblToStr( Double db ) {
        if ( db == null ) {
            return "";
        }
        if ( Math.abs( Math.floor( db ) - db ) < 0.00000001 ) {
            return Long.toString( (long) Math.floor( db ) );
        }
        return db.toString();
    }

    /**
     * 将double 转成字符
     *
     * @param db
     * @return
     */
    public static String dblToStr( Double db, String def ) {
        String val = dblToStr( db );
        if ( val == null || val.length() == 0 )
            return def;
        return val;
    }

    public static String objToString( Object res, String nullVal ) {
        if ( res == null )
            return nullVal;
        String className = res.getClass().getName();
        if ( className.equals( "byte[]" ) )
            return nullVal;
        else if ( className.equals( "java.lang.String" ) ) {
            return res.toString().trim();
        }
        else if ( className.equals( "java.sql.Timestamp" ) ) {
            SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
            //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format( res );
        }
        else if ( className.equals( "java.lang.Boolean" ) ) {
            if ( ((Boolean) res) )
                return "1";
            else
                return "0";
        }
	      /*if( className.equals("java.sql.Timestamp")){
	         String st = "TO_DATE( '";
	         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	         st += format.format(res) +"', 'yyyy-mm-dd HH24:mi:ss')";
	         return st; 
	      }*/
        return res.toString();
    }

    public static String objToSqlStr( Object res ) {
        if ( res == null )
            return "NULL";
        String className = res.getClass().getName();
        if ( Byte[].class.equals( res.getClass() ) || byte[].class.equals( res.getClass() ) || className.equalsIgnoreCase( "byte[]" ) )
            return "NULL";
        else if ( className.equalsIgnoreCase( "java.lang.Boolean" ) ) {
            return String.valueOf( res );
        }
        else if ( className.equalsIgnoreCase( "java.lang.String" ) || className.equalsIgnoreCase( "java.lang.char" ) ) {
            return "'" + res.toString().trim().replaceAll( "'", "''" ).replaceAll( "\\\\", "" ) + "'";
        }
        else if ( className.equalsIgnoreCase( "java.sql.Time" ) ) {
            String st = "'";
            SimpleDateFormat format = new SimpleDateFormat( "HH:mm:ss" );
            //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            st += format.format( res ) + "'";
            return st;
        }
        else if ( className.equalsIgnoreCase( "java.util.Date" ) ) {
            String st = "'";
            SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            st += format.format( res ) + "'";
            return st;
        }
        else if ( className.equalsIgnoreCase( "java.sql.Timestamp" ) ) {
            String st = "'";
            SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
            //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            st += format.format( res ) + "'";
            return st;
        }
        else if ( className.equalsIgnoreCase( "java.lang.Boolean" ) ) {

            if ( ((Boolean) res) )
                return "true";
            else
                return "false";

        }
	      /*if( className.equals("java.sql.Timestamp")){
	         String st = "TO_DATE( '";
	         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	         st += format.format(res) +"', 'yyyy-mm-dd HH24:mi:ss')";
	         return st; 
	      }*/
        return res.toString();
    }


    public static String toHexString( byte[] bytes ) {
        StringBuffer buffer = new StringBuffer();
        for ( int i = 0; i < bytes.length; i++ ) {
            buffer.append( String.format( "%02X", bytes[i] ) );
        }
        return buffer.toString().toLowerCase();
    }

    public static String getErrorInfoFromException( Throwable e ) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter( sw );
            e.printStackTrace( pw );
            return sw.toString();
        }
        catch ( Exception e2 ) {
            return "bad getErrorInfoFromException";
        }
    }

//	public static  int getStart(Integer page, Integer rows) {
//		return null != page && null != rows && page.intValue() >= 1 && rows.intValue() >= 1?(page.intValue() - 1) * rows.intValue():0;
//	}

    /**
     * 路径合并，消除多余的
     *
     * @param path1
     * @param path2
     * @return
     */
    public static String mergePaths( String path1, String... path2 ) {
        //String path = path1.replaceAll("\\\\","/");

        String path = path1;
        if ( path2 == null || path2.length == 0 ) {
            return path;
        }

        char ch = path.charAt( path.length() - 1 );
        if ( ch == '/' || ch == '\\' ) {
            path = path.substring( 0, path.length() - 1 ) + "/";
        }
        else
            path = path + "/";

        for ( String str : path2 ) {
            if ( str != null && str != "" ) {
                String s = str.replaceAll( "\\\\", "/" );
                if ( path.endsWith( "/" ) && s.startsWith( "/" ) ) {
                    path = path + s.substring( 1 );
                }
                else if ( !path.endsWith( "/" ) && !s.startsWith( "/" ) ) {
                    path = path + "/" + s;
                }
                else
                    path = path + s;
            }
        }
        return path;
    }

    /**
     * 删除历史文件
     *
     * @param filePath         文件路径
     * @param beforeSecond     早于当前秒数
     * @param bdelSubFile      是否删除下级文件
     * @param bdelSubEmptyPath 是否删除下级空目录
     */
    public static void clearOldFile( String filePath, long beforeSecond, boolean bdelSubFile, boolean bdelSubEmptyPath ) {
        try {
            long yes = System.currentTimeMillis() - beforeSecond * 1000;
            File file = new File( filePath );
            if ( file.exists() ) {
                File[] files = file.listFiles();
                for ( File f : files ) {
                    if ( f.isDirectory() && bdelSubFile ) {
                        clearSubFile( f, yes );
                    }
                    if ( yes > f.lastModified() ) {
                        f.delete();
                    }
                }
                if ( bdelSubEmptyPath ) {
                    files = file.listFiles();
                    for ( File f : files ) {
                        if ( f.isDirectory() ) {
                            clearSubPath( f );
                        }
                    }
                }
            }

        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 删除下级文件
     *
     * @param file 当前目录
     * @param time 过期时间
     */
    private static void clearSubFile( File file, long time ) {
        try {
            File[] files = file.listFiles();
            for ( File f : files ) {
                if ( f.isDirectory() ) {
                    clearSubFile( f, time );
                }
                else {
                    if ( time > f.lastModified() ) {
                        f.delete();
                    }
                }
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 删除当前空目录及下级空目录
     *
     * @param file 当前目录
     */
    private static void clearSubPath( File file ) {
        try {
            File[] files = file.listFiles();
            for ( File f : files ) {
                if ( f.isDirectory() ) {
                    clearSubPath( f );
                }
            }
            files = file.listFiles();
            if ( files.length == 0 ) {
                file.delete();
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * @description: 判断字符串是否是数字 可以判断正负、整数小数
     * @author: drp
     * @date: 2019-10-21 17:53
     */
    public static boolean isNumeric( String str ) {
        Boolean strResult = str.matches( "^-?\\d+(\\.\\d+)?$" );
        if ( strResult == true ) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 判断 List 是否为空
     */
//	public static boolean isArrayEmpty(List list) {
//		if (list == null || list.isEmpty()) {
//			return true;
//		}
//		return false;
//	}
    public static String genId() {
        return UUID.randomUUID().toString().replaceAll( "-", "" );
    }

//	public static void main( String[] args ) {
//		System.out.println( ComUtil.dateToStr( new Date(), ComUtil.shortDtFormat  ) );
//		System.out.println( ComUtil.dateToStr( new Date(), ComUtil.longDtFormat  ) );
//		System.out.println( ComUtil.dateToStr( new Date(), ComUtil.milDtFormat  ) );
//		System.out.println( ComUtil.dateToStr( new Date(), ComUtil.timeFormat  ) );
//		System.out.println( ComUtil.dateToStr( new Date(), ComUtil.milTimeFormat  ) );
//	}

}
