package cn.bywin.business.hetu;

import static cn.bywin.business.common.enums.ErrorCodeConstants.HETU_CONNECTION_ERROR;
import static cn.bywin.business.common.enums.ErrorCodeConstants.HETU_EXECUTE_ERROR;

import cn.bywin.business.common.except.ServerException;
import cn.common.base.AuditLog;
import com.google.common.collect.Maps;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hetu 操作类
 * @author firepation
 */
public class HetuJdbcOperate implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(HetuJdbcOperate.class);

    private Connection connection;

    public void init(String url, Properties properties) {
        TimeZone.setDefault( TimeZone.getTimeZone( "Asia/Shanghai" ) );
        try {
            connection = DriverManager.getConnection(url, properties);
        } catch (SQLException e) {
            throw new ServerException(HETU_CONNECTION_ERROR.getCode(), HETU_CONNECTION_ERROR.getMessage() + ": " + e.getMessage());
        }
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
    public List<Map<String, Object>> selectData(String sql) {
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
        } catch (SQLException e) {
            throw new ServerException(HETU_EXECUTE_ERROR.getCode(), HETU_EXECUTE_ERROR.getMessage() + ": " + e.getMessage());
        }
        return resultData;
    }

    @AuditLog
    public List<Map<String,Object>>  selectData(String sql,Class<? extends Map> cls) {
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
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            throw new ServerException(HETU_EXECUTE_ERROR.getCode(), HETU_EXECUTE_ERROR.getMessage() + ": " + e.getMessage());
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
    public boolean execute(String sql) {
        logger.debug(sql);
        try (Statement statement = connection.createStatement()){
            statement.execute(sql);
            return true;
        } catch (SQLException e) {
            throw new ServerException(HETU_EXECUTE_ERROR.getCode(), HETU_EXECUTE_ERROR.getMessage() + ": " + e.getMessage());
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
