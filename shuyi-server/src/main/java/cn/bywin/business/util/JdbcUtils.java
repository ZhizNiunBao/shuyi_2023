package cn.bywin.business.util;


import static cn.bywin.business.util.MapTypeAdapter.chgTypeCom;

import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.common.util.ComUtil;
import com.google.common.collect.Maps;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcUtils implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

    private Connection connection;

    public JdbcUtils(FDatasourceDo datasourceDo) throws SQLException {
        connection = DriverManager.getConnection(datasourceDo.getJdbcUrl(),
                datasourceDo.getUsername(), datasourceDo.getPassword());
    }

    public Connection getConnection() {
        return this.connection;
    }

    public static boolean exeSql(Connection connection, String sql) throws SQLException {
        boolean res;
        try {
            Statement statement = connection.createStatement();
            res = statement.execute(sql);
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 执行查询语句
     *
     * @param sql 查询语句
     * @return 查询结果
     * @throws SQLException
     */
    public List<Map<String, Object>> selectData(String sql) throws SQLException {
        List<Map<String, Object>> resultData = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, Object> rowData = Maps.newLinkedHashMap();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    Object object = resultSet.getObject(columnLabel);
                    if (object != null) {
                        if (object instanceof Date) {
                            object = ComUtil.dateToStr((Date) object);
                        } else if (object instanceof Timestamp) {
                            object = ComUtil.dateToLongStr((Timestamp) object);
                        } else if (object instanceof Boolean) {
                            if ((Boolean) object) {
                                object = "是";
                            } else {
                                object = "否";
                            }
                        }
                    }
                    rowData.put(columnLabel, object);
                }
                resultData.add(rowData);
            }
        }
        return resultData;
    }


    /**
     * 执行查询语句
     *
     * @param sql 查询语句
     * @return 查询结果
     * @throws SQLException
     */
    public List<Map<String, Object>> selectData(String sql, Map<String, Object> meta) throws SQLException {
        List<Map<String, Object>> resultData = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, Object> rowData = Maps.newLinkedHashMap();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    String type = chgTypeCom(metaData.getColumnTypeName(i));
                    meta.put(columnLabel, type);
                    Object object = resultSet.getObject(columnLabel);
                    if (object != null) {
                        if (object instanceof Date) {
                            object = ComUtil.dateToStr((Date) object);
                        } else if (object instanceof Timestamp) {
                            object = ComUtil.dateToLongStr((Timestamp) object);
                        } else if (object instanceof Boolean) {
                            if ((Boolean) object) {
                                object = "是";
                            } else {
                                object = "否";
                            }
                        }
                    }
                    rowData.put(columnLabel, object);
                }
                resultData.add(rowData);
            }
        }
        return resultData;
    }


    /**
     * 执行修改语句
     *
     * @param sql 修改语句
     * @return 是否成功
     * @throws SQLException
     */
    public boolean execute(String sql) throws SQLException {
        logger.debug(sql);
        try (Statement statement = connection.createStatement()) {
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
