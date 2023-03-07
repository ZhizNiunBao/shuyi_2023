package cn.bywin.business.hetu;

import cn.common.base.AuditLog;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * hetu 操作类
 * @author firepation
 */
public class HetuJdbcOperate implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(HetuJdbcOperate.class);

    private Connection connection;

    public void init(String url, Properties properties) throws SQLException {
        TimeZone.setDefault( TimeZone.getTimeZone( "Asia/Shanghai" ) );
        connection = DriverManager.getConnection(url, properties);
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * 执行查询语句
     * @param sql 查询语句
     * @return 查询结果
     * @throws SQLException
     */
    @AuditLog
    public List<Map<String, Object>> selectData(String sql) throws SQLException {
        List<Map<String, Object>> resultData = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)){
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, Object> rowData = Maps.newLinkedHashMap();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    Object object = resultSet.getObject(columnLabel);
                    rowData.put(columnLabel, object);
                }
                resultData.add(rowData);
            }
        }
        return resultData;
    }

    @AuditLog
    public List<Map<String,Object>>  selectData(String sql,Class<? extends Map> cls) throws SQLException, IllegalAccessException, InstantiationException {
        List<Map<String,Object>> resultData = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)){
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String,Object> rowData = cls.newInstance();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    Object object = resultSet.getObject(columnLabel);
                    rowData.put(columnLabel, object);
                }
                resultData.add(rowData);
            }
        }
        return resultData;
    }

    /**
     * 执行修改语句
     * @param sql 修改语句
     * @return 是否成功
     * @throws SQLException
     */
    @AuditLog
    public boolean execute(String sql) throws SQLException {
        logger.debug(sql);
        try (Statement statement = connection.createStatement()){
            statement.execute(sql);
            return true;
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignore) {

            }
        }
    }
}
