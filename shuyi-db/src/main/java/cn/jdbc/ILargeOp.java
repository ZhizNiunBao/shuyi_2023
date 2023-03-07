package cn.jdbc;

import java.util.List;
import java.util.Map;

public interface ILargeOp {
    /**
     * 关闭连接
     */
    public void close();

    public long selectTableCount(String cntSql, List<Object> paraList) throws Exception;

    public boolean startSelect(String sql, List<Object> paras, int fetchSize) throws Exception;


    public List<Map<String, Object>> selectData(int size, String mapType) throws Exception;

    public List<Map<String, Object>> selectDataAsLinkMap(int size) throws Exception;

    public List<Map<String, Object>> selectDataAsHashMap(int size) throws Exception;

    public <T> List<T> selectData(int size, Class<T> cls) throws Exception;


    public void setRaiseException(boolean raiseException);

    public boolean isRaiseException();

    public String getDbType();
}
