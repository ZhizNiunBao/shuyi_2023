package cn.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageSQLUtil {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    //public static String DB_TYPE = dbMySql;//mysql,postgrsql,oracle



//    protected static String pageSqlPageNum(String dbType,String sql, int pageNumber, int perPageSize) {
//        return pageSqlPageNum(dbType,sql, (long) pageNumber, (long) perPageSize);
//    }

//    public static String pageSqlPageNum(String dbType,String sql, long pageNumber, long perPageSize) {
//        if (dbType.equals(dbMySql) ||dbType.equals(dbDm)) {
//            return mysqlPageNum(sql, pageNumber, perPageSize);
//        } else if (dbType.equals(dbOdps)) {
//            return mysqlPageNum(sql, pageNumber, perPageSize);
//        } else if (dbType.equals(dbClickHouse)) {
//            return mysqlPageNum(sql, pageNumber, perPageSize);
//        }        else if (dbType.equals(dbPostgrSql)) {
//            return postgrsqlPageNum(sql, pageNumber, perPageSize);
//        } else if (dbType.equals(dbOracle)) {
//            return oraclePageNum(sql, pageNumber, perPageSize);
//        }else if (dbType.equals(dbPresto)) {
//            return prestoPageNum(sql, pageNumber, perPageSize);
//        }else if (dbType.equals(dbHetu)) {
//            return hetuPageNum(sql, pageNumber, perPageSize);
//        } else {
//            throw new RuntimeException("PageSQLUtil.DB_TYPE 错误");
//        }
//    }

//    protected static String pageSqlStartNum(String dbType,String sql, int startNumber, int perPageSize) {
//        return pageSqlStartNum(dbType,sql, (long) startNumber, (long) perPageSize);
//    }

//    public static String pageSqlStartNum(String dbType,String sql, long startNumber, long perPageSize) {
//        if (dbType.equals(dbMySql)||dbType.equals(dbMySql5)||dbType.equals(dbMySql8)||dbType.equals(dbStartRocks)||dbType.equals(dbDm) ) {
//            return mysqlStartNum(sql, startNumber, perPageSize);
//        } else if (dbType.equals(dbOdps)) {
//            return mysqlStartNum(sql, startNumber, perPageSize);
//        } else if (dbType.equals(dbClickHouse)) {
//            return mysqlStartNum(sql, startNumber, perPageSize);
//        }        else if (dbType.equals(dbPostgrSql)) {
//            return postgrsqlStartNum(sql, startNumber, perPageSize);
//        } else if (dbType.equals(dbOracle)) {
//            return oracleStartNum(sql, startNumber, perPageSize);
//        }else if (dbType.equals(dbPresto)) {
//            return prestoStartNum(sql, startNumber, perPageSize);
//        }else if (dbType.equals(dbHetu)) {
//            return hetuStartNum(sql, startNumber, perPageSize);
//        } else {
//            throw new RuntimeException("PageSQLUtil.DB_TYPE 错误" +dbType);
//        }
//    }


    /**
     * @param pageNumber  1+
     * @param perPageSize 1+
     */
    public static String mysqlPageNum(String sql, long pageNumber, long perPageSize) {
        long limit = (pageNumber - 1) * perPageSize;
        long offset = perPageSize;
        String tmp = sql.toLowerCase().replaceAll("\\\r|\\\n|\\\t"," ");
        int idx  = tmp.indexOf( " limit ");
        if( idx >0 )
            return "select * from ( "+ sql +" ) xx " + " LIMIT " + limit + "," + offset;
        return sql + " LIMIT " + limit + "," + offset;
    }
    public static String mysqlStartNum(String sql, long startNumber, long perPageSize) {
        long limit = startNumber;
        long offset = perPageSize;

        String tmp = sql.toLowerCase().replaceAll("\\\r|\\\n|\\\t"," ");
        int idx  = tmp.indexOf( " limit ");
        if( idx >0 )
            return "select * from ( "+ sql +" ) xx " + " LIMIT " + limit + "," + offset;

        return sql + " LIMIT " + limit + "," + offset;
    }

    public static String postgresqlStartNum(String sql, long startNumber, long perPageSize) {
        long offset = startNumber;
        if ( offset < 0 ) {
            offset = 0;
        }
        long limit = perPageSize;
        String tmp = sql.toLowerCase().replaceAll("\\\r|\\\n|\\\t"," ");
        int idx  = tmp.indexOf( " limit ");
        if( idx >0 ) {
            if( offset>0 ) {
                return "select * from ( " + sql + " ) xx OFFSET " + offset + " LIMIT " + limit;
            }
            return "select * from ( " + sql + " ) xx " + " LIMIT " + limit;
        }
        if( offset>0 ) {
            return sql + " OFFSET " + offset + " LIMIT " + limit;
        }
        return sql + " LIMIT " + limit;
    }

    public static String clickhousePageNum(String sql, long pageNumber, long perPageSize) {
        long limit = (pageNumber - 1) * perPageSize;
        long offset = perPageSize;

        String tmp = sql.toLowerCase().replaceAll("\\\r|\\\n|\\\t"," ");
        int idx  = tmp.indexOf( " limit ");
        if( idx >0 )
            return "select * from ( "+ sql +" ) xx " + " LIMIT " + limit + "," + offset;

        return sql + " LIMIT " + limit + "," + offset;
    }
    public static String clickhouseStartNum(String sql, long startNumber, long perPageSize) {
        long limit = startNumber;
        long offset = perPageSize;

        String tmp = sql.toLowerCase().replaceAll("\\\r|\\\n|\\\t"," ");
        int idx  = tmp.indexOf( " limit ");
        if( idx >0 )
            return "select * from ( "+ sql +" ) xx " + " LIMIT " + limit + "," + offset;

        return sql + " LIMIT " + limit + "," + offset;
    }

    public static String postgrsqlPageNum(String sql, long pageNumber, long perPageSize) {
        long offset = (pageNumber - 1) * perPageSize;
        long limit = perPageSize;

        String tmp = sql.toLowerCase().replaceAll("\\\r|\\\n|\\\t"," ");
        int idx  = tmp.indexOf( " limit ");
        if( idx >0 ) {
            if( offset>0 ) {
                return "select * from ( " + sql + " ) xx OFFSET " + offset + " LIMIT " + limit;
            }
            return "select * from ( " + sql + " ) xx " + " LIMIT " + limit;
        }
        if( offset>0 ) {
            return sql + " OFFSET " + offset + " LIMIT " + limit;
        }
        return sql + " LIMIT " + limit;
    }
    public static String postgrsqlStartNum(String sql, long startNumber, long perPageSize) {
        long limit = startNumber;
        long offset = perPageSize;

        String tmp = sql.toLowerCase().replaceAll("\\\r|\\\n|\\\t"," ");
        int idx  = tmp.indexOf( " limit ");
        if( idx >0 )
            return "select * from ( "+ sql +" ) xx " + " LIMIT " + limit + " OFFSET" + offset;

        return sql + " LIMIT " + limit + " OFFSET" + offset;
    }
	public static String prestoStartNum(String sql, long startNumber, long perPageSize) {
        long limit = startNumber;
        long offset = startNumber + perPageSize-1;

        String pageSql ="SELECT * FROM (SELECT ROW_NUMBER() over() as Row,tempT1.* FROM ( " + sql +
                " ) as tempT1 ) tempT2 WHERE tempT2.Row BETWEEN "+limit+" AND "+offset;

        return pageSql  ;
    }

    public static String prestoPageNum(String sql, long pageNumber, long perPageSize) {
        long limit = (pageNumber - 1) * perPageSize +1 ;
        long offset = pageNumber * perPageSize;
        String pageSql ="SELECT * FROM (SELECT ROW_NUMBER() over() as Row,tempT1.* FROM ( " + sql +
                " ) as tempT1 ) tempT2 WHERE tempT2.Row BETWEEN "+limit+" AND "+offset;
        return pageSql  ;
    }

    public static String hetuStartNum(String sql, long startNumber, long perPageSize) {
        long limit = startNumber;
        long offset = startNumber + perPageSize-1;

        String pageSql ="SELECT * FROM (SELECT ROW_NUMBER() over() as Row,tempT1.* FROM ( " + sql +
                " ) as tempT1 ) tempT2 WHERE tempT2.Row BETWEEN "+limit+" AND "+offset;

        return pageSql  ;
    }

    public static String hetuPageNum(String sql, long pageNumber, long perPageSize) {
        long limit = (pageNumber - 1) * perPageSize +1 ;
        long offset = pageNumber * perPageSize;
        String pageSql ="SELECT * FROM (SELECT ROW_NUMBER() over() as Row,tempT1.* FROM ( " + sql +
                " ) as tempT1 ) tempT2 WHERE tempT2.Row BETWEEN "+limit+" AND "+offset;
        return pageSql  ;
    }

    public static String oraclePageNum(String sql, long pageNumber, long perPageSize) {
        long limit = (pageNumber - 1) * perPageSize;
        long endRowNum = limit + perPageSize;

        return "SELECT * FROM " +
                " ( SELECT A.*, ROWNUM RN   FROM  " +
                " ( " + sql + " ) A   WHERE ROWNUM " +
                " <= " + endRowNum + ") WHERE RN >=  " + limit;
    }
    public static String oracleStartNum(String sql, long startNumber, long perPageSize) {
        long limit = startNumber;
        long endRowNum = limit + perPageSize;

        return "SELECT * FROM " +
                " ( SELECT A.*, ROWNUM RN   FROM  " +
                " ( " + sql + " ) A   WHERE ROWNUM " +
                " <= " + endRowNum + ") WHERE RN >=  " + limit;
    }
}

