package cn.jdbc;

import java.util.List;

public interface IJdbcOp extends  AutoCloseable {

    /**
     * 关闭连接
     */
    //public void close();

    /**
     * 连接测试 判断是否可以连接
     * @return
     */
    public boolean checkConnect();

    public List selectData(String sql, List<Object> paras ) throws  Exception;

    public List selectData(String sql, List<Object> paras, long start, long cnt) throws  Exception;

    public List selectData(String sql ) throws  Exception;

    public <T> List<T> selectData(String sql ,Class<T> cls) throws  Exception;

    public List selectData(String sql, long start, long cnt ) throws  Exception;

    public List selectDataAsLinkMap(String sql ) throws  Exception;

    public List selectDataAsLinkMap(String sql, long start, long cnt) throws  Exception;

    public boolean execute(String sql) throws  Exception;

    public boolean execute(String sql,List<Object> paras) throws  Exception;

    public List<FieldType> checkTableField(String sql ) throws  Exception;

    public Long selectTableCount(String sql, List<Object> paras) throws Exception;

    public String getDbType();

    public void setRaiseException( boolean raiseException );

    public boolean isRaiseException();

    public List<String> listSchema(String catalog, String schema ) throws Exception ;

    public List<JdbcTableInfo> listTable(String catalog,String schema) throws Exception ;

    public List<JdbcTableInfo> listView(String catalog,String schema) throws Exception ;

    public List<JdbcTableInfo> listTableAndView(String catalog,String schema) throws Exception ;

    public List<JdbcColumnInfo> listColumn(String catalog,String schema,String table) throws Exception ;

    public List<String> getSysSchema();
}
