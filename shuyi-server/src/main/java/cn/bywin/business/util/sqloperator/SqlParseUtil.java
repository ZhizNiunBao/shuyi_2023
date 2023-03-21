package cn.bywin.business.util.sqloperator;

import cn.bywin.business.bean.response.sqloperator.SqlOutVo;
import cn.bywin.business.bean.response.sqloperator.SqlParseVo;
import cn.bywin.business.bean.response.sqloperator.SqlSelectFromVo;
import cn.bywin.business.bean.sqloperator.TSqlOperatorInDo;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Name;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlParseUtil {

    protected final static Logger logger = LoggerFactory.getLogger(SqlParseUtil.class);

    public static SqlParseVo parseSql(String sql) {
        SqlParseVo sqlparseVo = new SqlParseVo();
        logger.debug("sql解析开始:#{}", sql);

        MySqlStatementParser sqlStatementParser = new MySqlStatementParser(sql);
        SQLSelectStatement sqlStatement = (SQLSelectStatement) sqlStatementParser.parseSelect();
        SQLSelect sqlSelect = sqlStatement.getSelect();
        SQLSelectQueryBlock sqlSelectQuery = (SQLSelectQueryBlock) sqlSelect.getQuery();

        Preconditions.checkArgument(
            !(sqlSelectQuery.getSelectList().size() == 1 && sqlSelectQuery.getSelectList().get(0)
                .getExpr().toString().equals("*") ), "输出字段不能为*,请列出具体字段名称!");

        sqlparseVo.setSql_format(formatSql(sql));
        sqlparseVo.setSqlOut(getOutSelect(sql));
        String repsql = sql.replaceAll("\\{", "____").replaceAll("\\}", "____");  //大括号替换
        repsql = repsql.replaceAll("\\#", "\\$S");  //#号替换
        List<SQLStatement> stmtList = SQLUtils.parseStatements(repsql, JdbcConstants.MYSQL);
        sqlparseVo.setSelectfromlist(getSelectfrom(stmtList));
        sqlparseVo.setSqlIn(getInSelect(stmtList));
        logger.debug("sgetSql_format解析完成:  " + sqlparseVo.getSql_format());
        logger.debug("getSelectfromlist解析完成:  " + sqlparseVo.getSelectfromlist());
        logger.debug("getSqlOut解析完成:  " + sqlparseVo.getSqlOut());
        logger.debug("getSqlIn解析完成:  " + sqlparseVo.getSqlIn());

        return sqlparseVo;
    }

    public static String formatSql(String sql) {
        DbType dbType = JdbcConstants.MYSQL;

        String repsql = sql.replaceAll("\\{", "____").replaceAll("\\}", "____");  //大括号替换
        repsql = repsql.replaceAll("\\#", "\\$S");  //#号替换
        //System.out.println("大括号替换=  " + repsql);
        String result = SQLUtils.format(repsql, dbType);
        String return_sql = result.replaceAll("\\$____", "\\${").replaceAll("\\$S____", "\\#{")
            .replaceAll("____", "\\}"); //大括号还原
        return return_sql;
    }

    public static List<SqlOutVo> getOutSelect(String sql) {
        List<SqlOutVo> outresult = new ArrayList<>();
        MySqlStatementParser sqlStatementParser = new MySqlStatementParser(sql);
        //解析select查询repsql
        SQLSelectStatement sqlStatement = (SQLSelectStatement) sqlStatementParser.parseSelect();
        SQLSelect sqlSelect = sqlStatement.getSelect();
        //获取sql查询块}
        SQLSelectQueryBlock sqlSelectQuery = (SQLSelectQueryBlock) sqlSelect.getQuery();

        StringBuffer out = new StringBuffer();
        //创建sql解析的标准化输出
        SQLASTOutputVisitor sqlastOutputVisitor = SQLUtils.createFormatOutputVisitor(out, null, JdbcUtils.MYSQL);

        //解析select项
        out.delete(0, out.length());
        for (SQLSelectItem sqlSelectItem : sqlSelectQuery.getSelectList()) {
            if (out.length() > 1) {
                out.append(",");
            }
            sqlSelectItem.accept(sqlastOutputVisitor);
        }

        String[] spout = out.toString().split("\\,");
        if (spout.length > 1) {  //逗号分割处理，当有多个字段进行处理
            for (int i = 0; i < spout.length; i++) {
                String[] spoutdouhao = spout[i].split("\\.");
                SqlOutVo sqlOutVo = new SqlOutVo();
                if (spoutdouhao.length > 1 && i < spout.length) {
                    if (spoutdouhao[1].toString().contains(" AS ") || spoutdouhao[1].toString().contains(" as ")) {
                        String spoutdouhaoas = "";
                        spoutdouhaoas = spoutdouhao[1].toString().replace(" AS ", "|");
                        spoutdouhaoas = spoutdouhaoas.replace(" as ", "|");
                        String[] spas = spoutdouhaoas.split("\\|");
                        sqlOutVo.setOutCode(spas[1].toString());
                        sqlOutVo.setOutName(spas[1].toString());
                    } else {
                        sqlOutVo.setOutCode(spoutdouhao[1].toString());
                        sqlOutVo.setOutName(spoutdouhao[1].toString());
                    }
                } else {
                    if (spoutdouhao[0].toString().contains(" AS ") || spoutdouhao[0].toString().contains(" as ")) {
                        String spoutdouhaoas = "";
                        spoutdouhaoas = spoutdouhao[0].toString().replace(" AS ", "|");
                        spoutdouhaoas = spoutdouhaoas.replace(" as ", "|");
                        String[] spas = spoutdouhaoas.split("\\|");
                        sqlOutVo.setOutCode(spas[1].toString());
                        sqlOutVo.setOutName(spas[1].toString());
                    } else {
                        sqlOutVo.setOutCode(spoutdouhao[0].toString());
                        sqlOutVo.setOutName(spoutdouhao[0].toString());
                    }
                }
                outresult.add(sqlOutVo);
            }
        } else {  //逗号分割处理，只有一个字段进行处理
            String[] spoutdouhao = out.toString().split("\\.");
            SqlOutVo sqlOutVo = new SqlOutVo();
            if (spoutdouhao.length > 1) {
                if (spoutdouhao[1].toString().contains(" AS ") || spoutdouhao[1].toString().contains(" as ")) {
                    String spoutdouhaoas = "";
                    spoutdouhaoas = spoutdouhao[1].toString().replace(" AS ", "|");
                    spoutdouhaoas = spoutdouhaoas.replace(" as ", "|");
                    String[] spas = spoutdouhaoas.split("\\|");
                    sqlOutVo.setOutCode(spas[1].toString());
                    sqlOutVo.setOutName(spas[1].toString());
                } else {
                    sqlOutVo.setOutCode(spoutdouhao[1].toString());
                    sqlOutVo.setOutName(spoutdouhao[1].toString());
                }
            } else {
                if (spoutdouhao.toString().contains(" AS ") || spoutdouhao.toString().contains(" as ")) {
                    String spoutdouhaoas = "";
                    spoutdouhaoas = spoutdouhao.toString().replace(" AS ", "|");
                    spoutdouhaoas = spoutdouhaoas.replace(" as ", "|");
                    String[] spas = spoutdouhaoas.split("\\|");
                    sqlOutVo.setOutCode(spas[1].toString());
                    sqlOutVo.setOutName(spas[1].toString());
                } else {
                    sqlOutVo.setOutCode(out.toString());
                    sqlOutVo.setOutName(out.toString());
                }
            }
            outresult.add(sqlOutVo);
        }
        return outresult;
    }

    /* sql语句返回数据源需要的字段和表名*/
    public static List<SqlSelectFromVo> getSelectfrom(List<SQLStatement> stmtList) {
        List<SqlSelectFromVo> sqlselectfromVoList = new ArrayList<>();
      /*  String repsql = sql.replaceAll("\\{", "____").replaceAll("\\}", "____");  //大括号替换
        repsql = repsql.replaceAll("\\#", "\\$S");  //#号替换
        //System.out.println("大括号替换=  " + repsql);
        //System.out.println("sql=  " + sql);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(repsql, JdbcConstants.MYSQL);*/
        //System.out.println("size is:" + stmtList.size());
        for (int i = 0; i < stmtList.size(); i++) {
            SQLStatement stmt = stmtList.get(i);
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            stmt.accept(visitor);
            //获取表名称
            Set<Name> keyset = visitor.getTables().keySet();
            Iterator<Name> iterator = keyset.iterator();
            while (iterator.hasNext()) {
                SqlSelectFromVo sqlselectfromVo = new SqlSelectFromVo();
                TableStat.Name temp = iterator.next();
                if (temp.toString().contains("$S")) {
                    sqlselectfromVo.setSourceTable(temp.toString());
                    sqlselectfromVoList.add(sqlselectfromVo);
                }
            }
            //获取字段名称
            //for(int j=0;j<sqlselectfromVoList.size();j++){
            for (SqlSelectFromVo sqlselfromVo : sqlselectfromVoList) {
                StringBuffer outselectfrom = new StringBuffer();
                StringBuffer outselect = new StringBuffer();
                outselectfrom.append("select ");
                Iterator<TableStat.Column> Columnit = visitor.getColumns().iterator();
                Integer cnt = 0;
                String stable = sqlselfromVo.getSourceTable().replaceAll("\\$S", "")
                    .replaceAll("____", "");
                while (Columnit.hasNext()) {
                    TableStat.Column Columntemp = Columnit.next();
                    if (Columntemp.getTable().equals(sqlselfromVo.getSourceTable())) {
                        if (!Columntemp.getName().contains("____")) {
                            if (cnt == 0) {
                                outselect.append(Columntemp.getName());
                                outselectfrom.append(Columntemp.getName());
                            } else {
                                outselect.append(",");
                                outselect.append(Columntemp.getName());
                                outselectfrom.append(",");
                                outselectfrom.append(Columntemp.getName());
                            }
                            cnt++;
                        }
                    }
                    if (!Columnit.hasNext() && cnt > 0) {
                        outselectfrom.append(" from " + stable);
                    }
                }
                sqlselfromVo.setSourceTable(stable);
                sqlselfromVo.setSFields(outselect.toString());
                sqlselfromVo.setSourceFields(outselectfrom.toString());
            }

        }
        return sqlselectfromVoList;
    }

    /* sql语句返回输入字段*/
    public static List<TSqlOperatorInDo> getInSelect(List<SQLStatement> stmtList) {

        List<TSqlOperatorInDo> sqlInVoList = new ArrayList<>();
    /*    String repsql = sql.replaceAll("\\{", "____").replaceAll("\\}", "____");  //大括号替换
        repsql = repsql.replaceAll("\\#", "\\$S");  //#号替换
        List<SQLStatement> stmtList = SQLUtils.parseStatements(repsql, JdbcConstants.MYSQL);*/
        //System.out.println("size is:" + stmtList.size());
        for (int i = 0; i < stmtList.size(); i++) {
            SQLStatement stmt = stmtList.get(i);
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            stmt.accept(visitor);
            //获取表名称
            Set<TableStat.Name> keyset = visitor.getTables().keySet();
            Iterator<TableStat.Name> iterator = keyset.iterator();

            while (iterator.hasNext()) {
                TableStat.Name temp = iterator.next();
                if (temp.toString().contains("$____")) {
                    String tableName = temp.toString();
                    Integer cnt = 0;
                    Iterator<TableStat.Column> Columnit = visitor.getColumns().iterator();
                    while (Columnit.hasNext()) {
                        TableStat.Column Columntemp = Columnit.next();
                        if (Columntemp.getTable().equals(tableName)) {
                            TSqlOperatorInDo sqlInVo = new TSqlOperatorInDo();
                            sqlInVo.setFieldCode(Columntemp.getName());
                            sqlInVo.setFieldName(Columntemp.getName());
                            String return_sql = tableName.replaceAll("\\$____", "")
                                .replaceAll("____", ""); //大括号还原
                            sqlInVo.setInCode(return_sql);
                            sqlInVo.setInName(return_sql);
                            sqlInVoList.add(sqlInVo);
                            cnt++;
                        }
                    }
                }
            }
        }
        return sqlInVoList;
    }
}
