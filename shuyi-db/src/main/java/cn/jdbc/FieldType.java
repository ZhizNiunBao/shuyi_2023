package cn.jdbc;

public class FieldType {
    String name;
    String javaType;
    int jdbcType;
    String jdbcTypeName;

    public FieldType() {
    }

    public FieldType(String name, String javaType, int jdbcType, String jdbcTypeName) {
        this.name = name;
        this.javaType = javaType;
        this.jdbcType = jdbcType;
        this.jdbcTypeName = jdbcTypeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public int getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(int jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getJdbcTypeName() {
        return jdbcTypeName;
    }

    public void setJdbcTypeName(String jdbcTypeName) {
        this.jdbcTypeName = jdbcTypeName;
    }
}
