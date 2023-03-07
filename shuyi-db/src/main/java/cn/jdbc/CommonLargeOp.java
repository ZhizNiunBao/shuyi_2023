package cn.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.*;
import java.util.*;


public class CommonLargeOp implements ILargeOp {
    Logger logger = LoggerFactory.getLogger(CommonLargeOp.class);
    private String url;

    private String db;

    private int timeout = 600000;

    private String user;

    private String password;

    private String driver;

    private String dbType = JdbcOpBuilder.dbClickHouse;

    private boolean raiseException = true;

    private Connection connection;

    PreparedStatement statement = null;
    ResultSet results = null;
    ResultSetMetaData rsmd = null;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public CommonLargeOp(String dbType, String driver, String url, String db, String user, String password) throws SQLException {
        if (dbType == null) {
            throw new RuntimeException("数据库类型未指定");
        }
        String dbt = dbType.toUpperCase();
        if (dbt.equals(JdbcOpBuilder.dbMySql)) {
            this.dbType = JdbcOpBuilder.dbMySql;
        }
        else if (dbt.equals(JdbcOpBuilder.dbOracle)) {
            this.dbType = JdbcOpBuilder.dbOracle;
        }
        else if (dbt.equals(JdbcOpBuilder.dbPresto)) {
            this.dbType = JdbcOpBuilder.dbPresto;
        }
        else if (dbt.equals(JdbcOpBuilder.dbOdps)) {
            this.dbType = JdbcOpBuilder.dbOdps;
        }
        else if (dbt.equals(JdbcOpBuilder.dbDm)) {
            this.dbType = JdbcOpBuilder.dbDm;
        }
        else {
            this.dbType = JdbcOpBuilder.dbMySql;
            logger.error("数据库类型{}未指定,使用mysql模式",dbt);
        }
        this.driver = driver;
        this.url = url;
        this.db = db;
        this.user = user;
        this.password = password;
        //genConn();
    }

    /**
     * 获取连接
     *
     * @return
     * @throws ClassNotFoundException
     * @throws Exception
     */
    private void genConn() throws Exception {
        if (connection == null || connection.isClosed()) {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        }
    }

    /**
     * 关闭连接
     */
    @Override
    public void close() {
        rsmd = null;
        try {
            if (results != null) {
                results.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        results = null;
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        statement = null;
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection = null;
    }

    @Override
    public long selectTableCount(String cntSql, List<Object> paraList) throws Exception {
        startSelect(cntSql, paraList, 100);
        List<Map<String, Object>> cntList = selectDataAsHashMap(1);
        long total = Long.parseLong(cntList.get(0).get("cnt").toString());
        return total;
    }


    private void setParameter(PreparedStatement statement, List<Object> paras) throws Exception {
        if (paras == null || paras.size() == 0) {
            return;
        }
        for (int i = 0; i < paras.size(); i++) {
            Object dataval = paras.get(i);
            if (dataval == null) {
                statement.setString(i + 1, null);
            } else {
                String className = dataval.getClass().getName();
                if (className.equals("java.lang.String")) {
                    statement.setString(i + 1, (String) paras.get(i));
                } else if (className.equals("java.sql.Timestamp")) {
                    //statement.setString(i + 1, "'"+ (Timestamp) paras.get(i) + "'");
                    statement.setTimestamp(i + 1, (Timestamp) paras.get(i));
                } else if (className.equals("java.sql.Date")) {
                    statement.setDate(i + 1, (java.sql.Date) paras.get(i));
                } else if (className.equals("java.sql.Time")) {
                    statement.setTime(i + 1, (java.sql.Time) paras.get(i));
                } else if (className.equals("java.util.Date")) {
                    //statement.setString(i + 1, "'"+ new Timestamp( ((java.util.Date)paras.get(i)).getTime()) + "'");
                    statement.setTimestamp(i + 1, new Timestamp(((java.util.Date) paras.get(i)).getTime()));
                } else {
                    statement.setObject(i + 1, paras.get(i));
                }
            }
        }
    }

    @Override
    public boolean startSelect(String sql, List<Object> paras, int fetchSize) throws Exception {
        try {
            genConn();
            statement = connection.prepareStatement(sql);
            setParameter(statement, paras);
            statement.setQueryTimeout(timeout);
            results = statement.executeQuery();
            rsmd = results.getMetaData();
            results.setFetchSize(fetchSize);
            return true;
        } catch (Exception e) {
            if (raiseException)
                throw e;
            else {
                logger.error(sql);
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public List<Map<String, Object>> selectData(int size, String mapType) throws Exception {
        List<Map<String, Object>> list = new ArrayList();
        int cnt = size;
        try {
            while (results.next()) {
                Map map;
                if ("linkMap".equals(mapType)) {
                    map = new LinkedHashMap();
                } else {
                    map = new HashMap();
                }
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    map.put(rsmd.getColumnLabel(i), toEffVal(i, rsmd, results));
                }
                list.add(map);
                cnt--;
                if (cnt <= 0) {
                    break;
                }
            }
            return list;
        } catch (Exception e) {
            if (raiseException) {
                throw e;
            } else {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public List<Map<String, Object>> selectDataAsLinkMap(int size) throws Exception {
        return selectData(size, "linkMap");
    }

    @Override
    public List<Map<String, Object>> selectDataAsHashMap(int size) throws Exception {
        return selectData(size, "hashMap");
    }

    @Override
    public <T> List<T> selectData(int size, Class<T> cls) throws Exception {
        List<T> list = new ArrayList();
        int cnt = size;
        try {
            int colNumber = rsmd.getColumnCount();
            Field[] fields = cls.getDeclaredFields();

            while (results.next()) {
                T obj = cls.newInstance();
                Map map = new HashMap();
                for (int i = 1; i <= colNumber; i++) {
                    for (Field f : fields) {
                        if (f.getName().equals(rsmd.getColumnLabel(i))) {
                            boolean flag = f.isAccessible();
                            f.setAccessible(true);
                            f.set(obj, toEffVal(i, rsmd, results));
                            f.setAccessible(flag);
                            break;
                        }
                    }
                }
                list.add(obj);
                cnt--;
                if (cnt <= 0) {
                    break;
                }
            }
            return list;

        } catch (Exception e) {
            if (raiseException) {
                throw e;
            } else {
                e.printStackTrace();
                return null;
            }
        }

    }

    private Object toEffVal(int idx, ResultSetMetaData rsmd, ResultSet set) throws Exception {
        final Object objval = set.getObject(idx);
        if (objval == null) {
            return null;
        }

        if (rsmd.getColumnTypeName(idx).startsWith("DateTime")) {
            final String s = objval.toString();
            if (s == null || s.length() == 0 || s.equals("0000-00-00 00:00:00.000")) {
                return null;
            }
            final Timestamp timestamp = Timestamp.valueOf(s);
            if (timestamp == null || timestamp.getTime() < 0) {
                return null;
            }
            return timestamp;
        }
        final String className = rsmd.getColumnClassName(idx);

        if (className.equals("java.lang.String")) {
            return objval;
        } else if (className.equals("java.sql.Timestamp")) {
            return new Date(((Timestamp) objval).getTime());
        } else if (className.equals("java.sql.Date")) {
            return new Date(((Date) objval).getTime());
        } else if (className.equals("java.sql.Time")) {
            return new Date(((Time) objval).getTime());
        } else {
            return objval;
        }
    }

    @Override
    public void setRaiseException(boolean raiseException) {
        this.raiseException = raiseException;
    }

    @Override
    public boolean isRaiseException() {
        return raiseException;
    }

    @Override
    public String getDbType() {
        return dbType;
    }
}
