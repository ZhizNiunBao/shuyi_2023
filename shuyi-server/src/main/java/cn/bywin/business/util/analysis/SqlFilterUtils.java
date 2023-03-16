package cn.bywin.business.util.analysis;

import cn.bywin.business.bean.bydb.TTruModelFieldDo;
import cn.bywin.business.common.util.ComUtil;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SqlFilterUtils {


    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static boolean makeSqlFilter(StringBuffer sql, String fun, String dataType, List<String> paraList) {

        switch (fun) {
            case "contain":
                sql.append(" like % ");
                if (!"INTEGER".equalsIgnoreCase(dataType)) {
                    sql.append("'").append(paraList.get(0)).append("' ");
                } else {
                    sql.append(paraList.get(0));
                }
                sql.append("%");
            case "notcontain":
                sql.append(" not like % ");
                if (!"INTEGER".equalsIgnoreCase(dataType)) {
                    sql.append("'").append(paraList.get(0)).append("' ");
                } else {
                    sql.append(paraList.get(0));
                }
                sql.append("%");
            case "eq":
                if (paraList.size() > 1) {
                    sql.append(" IN ");
                    if (!"INTEGER".equalsIgnoreCase(dataType)) {
                        final String collect = paraList.stream().map(x -> "'" + x + "'").collect(Collectors.joining(","));
                        sql.append(" (").append(collect).append(") ");
                    } else {
                        final String collect = paraList.stream().collect(Collectors.joining(","));
                        sql.append(" (").append(collect).append(") ");
                    }
                } else {
                    sql.append(" = ");
                    if (!"INTEGER".equalsIgnoreCase(dataType)) {
                        sql.append("'").append(paraList.get(0)).append("' ");
                    } else {
                        sql.append(paraList.get(0));
                    }
                }
                break;
            case "noteq":
                if (paraList.size() > 1) {
                    sql.append(" NOT IN ");
                    if (!"INTEGER".equalsIgnoreCase(dataType)) {
                        final String collect = paraList.stream().map(x -> "'" + x + "'").collect(Collectors.joining(","));
                        sql.append(" (").append(collect).append(") ");
                    } else {
                        final String collect = paraList.stream().collect(Collectors.joining(","));
                        sql.append(" (").append(collect).append(") ");
                    }
                } else {
                    sql.append(" != ");
                    if (!"INTEGER".equalsIgnoreCase(dataType)) {
                        sql.append("'").append(paraList.get(0)).append("' ");
                    } else {
                        sql.append(paraList.get(0));
                    }
                }
                break;
            case "between":
                sql.append(" BETWEEN ");
                if (!"INTEGER".equalsIgnoreCase(dataType)) {
                    sql.append(" '").append(paraList.get(0)).append("' and '").append(paraList.get(1)).append("' ");
                } else {
                    final String collect = paraList.stream().collect(Collectors.joining(","));
                    sql.append(" ").append(paraList.get(0)).append(" and ").append(paraList.get(1)).append(" ");
                }
                break;
            case "reg":
                sql.append("  REGEXP ");
                sql.append("'").append(paraList.get(0)).append("' ");
                break;
            case "notreg":
                sql.append(" NOT REGEXP ");
                sql.append("'").append(paraList.get(0)).append("' ");
                break;
            case "lt":
                sql.append(" < ");
                if (!"INTEGER".equalsIgnoreCase(dataType)) {
                    sql.append("'").append(paraList.get(0)).append("' ");
                } else {
                    sql.append(paraList.get(0));
                }
                break;
            case "let":
                sql.append(" <= ");
                if (!"INTEGER".equalsIgnoreCase(dataType)) {
                    sql.append("'").append(paraList.get(0)).append("' ");
                } else {
                    sql.append(paraList.get(0));
                }
                break;
            case "gt":
                sql.append(" > ");
                if (!"INTEGER".equalsIgnoreCase(dataType)) {
                    sql.append("'").append(paraList.get(0)).append("' ");
                } else {
                    sql.append(paraList.get(0));
                }
                break;
            case "get":
                sql.append(" >= ");
                if (!"INTEGER".equalsIgnoreCase(dataType)) {
                    sql.append("'").append(paraList.get(0)).append("' ");
                } else {
                    sql.append(paraList.get(0));
                }
                break;
            case "benull":
                sql.append(" IS NULL ");
                break;
            case "notnull":
                sql.append(" is not null ");
                break;
            default:
                return false;
        }
        return false;
    }

    public static String getMatchedFrom(String sql) {

        if (sql.indexOf("FROM") != -1) {
            int end = sql.indexOf("FROM") + 5;
            String text = sql.substring(end, sql.length());
            return text;
        }
        return null;
    }

    public static String getMatchedSelect(String sql) {

        if (sql.indexOf("SELECT") != -1) {
            int end = sql.indexOf("SELECT") + 7;
            String text = sql.substring(end, sql.length());
            return text;
        }
        return null;
    }



    public static String makeExeDateSql(TTruModelFieldDo fieldDo, String tableName) {

        String strSql = "\n" +
                "SELECT '%s' \"fieldName\" ,\n" +
                "'%s' \"fieldType\",\n" +
                "'%s' \"fieldExpr\",\n" +
                "COUNT(DISTINCT %s) AS \"distinct_count\",\n" +
                "COUNT(*) AS \"count\" ,\n" +
                "SUM(\n" +
                "CASE\n" +
                " WHEN %s IS NULL THEN 1\n" +
                " END) \"null_count\",\n" +
                "MIN(%s) \"min\",\n" +
                "MAX(%s) \"max\" \n" +
                " FROM %s";
        String field = fieldDo.getTableAlias().concat(".").concat(fieldDo.getFieldAlias());
        String result = String.format(strSql, fieldDo.getFieldAlias(), fieldDo.getColumnType()
                , fieldDo.getFieldExpr(),
                field, field, field, field, tableName);
        return result;

    }

    public static void changeValueByType(List<Map<String, Object>> list) {
        if (list != null && list.size() > 0) {
            for (Map<String, Object> dat : list) {
                final Iterator<Map.Entry<String, Object>> iterator = dat.entrySet().iterator();
                while (iterator.hasNext()) {
                    final Map.Entry<String, Object> next = iterator.next();
                    final String key = next.getKey();
                    final Object value = next.getValue();
                    if (value != null) {
                        if (value instanceof Date) {
                            dat.put(key, ComUtil.dateToStr((Date) value));
                        } else if (value instanceof Timestamp) {
                            dat.put(key, ComUtil.dateToLongStr((Timestamp) value));
                        } else if (value instanceof Boolean) {
                            if ((Boolean) value) {
                                dat.put(key, "是");
                            } else {
                                dat.put(key, "否");
                            }
                        }
                    }
                }
            }
        }
    }

    public static String makeExeIntSql(TTruModelFieldDo fieldDo, String tableName) {

        String intSql = "SELECT '%s' \"fieldName\" ,\n" +
                "'%s' \"fieldType\",\n" +
                "'%s' \"fieldExpr\",\n" +
                "COUNT(DISTINCT %s) AS \"distinct_count\",\n" +
                "COUNT(*) AS \"count\" ,\n" +
                "SUM(\n" +
                "CASE\n" +
                " WHEN %s IS NULL THEN 1\n" +
                " ELSE 0\n" +
                " END) \"null_count\",\n" +
                "MIN(%s) \"min\",\n" +
                "MAX(%s) \"max\",\n" +
                "AVG(%s) \"avg\",\n" +
                "STDDEV(%s) \"std\"\n" +
                " FROM %s";
        String field = fieldDo.getTableAlias().concat(".").concat(fieldDo.getFieldAlias());
        String result = String.format(intSql, fieldDo.getFieldAlias(), fieldDo.getColumnType(), fieldDo.getFieldExpr(), field,
                field, field, field, field, field, tableName);
        return result;

    }


    public static String makeExeStrSql(TTruModelFieldDo fieldDo, String tableName) {

        String strSql = "\n" +
                "SELECT '%s' \"fieldName\" ,\n" +
                "'%s' \"fieldType\",\n" +
                "'%s' \"fieldExpr\",\n" +
                "COUNT(DISTINCT %s) AS \"distinct_count\",\n" +
                "COUNT(*) AS \"count\" ,\n" +
                "SUM(\n" +
                "CASE\n" +
                " WHEN %s IS NULL THEN 1\n" +
                " END) \"null_count\"\n" +
                " FROM %s";
        String field = fieldDo.getTableAlias().concat(".").concat(fieldDo.getFieldAlias());
        String result = String.format(strSql, fieldDo.getFieldAlias(), fieldDo.getColumnType()
                , fieldDo.getFieldExpr(),
                field, field, tableName);
        return result;

    }
}
