package cn.bywin.business.common.base;


import org.apache.commons.lang3.StringUtils;

public enum JdbcJavaType {
    BIT(-7,"BIT"),
    TINYINT(-6,"TINYINT"),
    BIGINT(-5,"BIGINT"),
    LONGVARBINARY(-4,"LONGVARBINARY"),
    VARBINARY(-3,"VARBINARY"),
    BINARY(-2,"BINARY"),
    LONGVARCHAR(-1,"LONGVARCHAR"),
    NULL(0,"NULL"),
    CHAR(1,"CHAR"),
    NUMERIC(2,"NUMERIC"),
    DECIMAL(3,"DECIMAL"),
    INTEGER(4,"INTEGER"),
    SMALLINT(5,"SMALLINT"),
    FLOAT(6,"FLOAT"),
    REAL(7,"REAL"),
    DOUBLE(8,"DOUBLE"),
    VARCHAR(12,"VARCHAR"),
    DATE(91,"DATE"),
    TIME(92,"TIME"),
    TIMESTAMP(93,"TIMESTAMP"),
    OTHER(1111,"OTHER");
    private int value;
    private String key;

    JdbcJavaType(int value, String key) {
        this.value = value;
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public static JdbcJavaType getEnmuByValue(int value) {
        for (JdbcJavaType item : values()) {
            if (value == item.getValue()) {
                return item;
            }
        }
        return null;
    }

    public static JdbcJavaType getEnmuByKey(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        for (JdbcJavaType item : values()) {
            if (key.equals(item.getKey())) {
                return item;
            }
        }
        return null;
    }
}
