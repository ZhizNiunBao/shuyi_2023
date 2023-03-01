package cn.bywin.business.common.base;


import org.apache.commons.lang3.StringUtils;

public enum JdbcFlinkType {
    BIT(-7,"BOOLEAN"),
    TINYINT(-6,"INT"),
    BIGINT(-5,"BIGINT"),
    LONGVARBINARY(-4,"LONGVARBINARY"),
    VARBINARY(-3,"VARBINARY"),
    BINARY(-2,"BINARY"),
    LONGVARCHAR(-1,"STRING"),
    NULL(0,"NULL"),
    CHAR(1,"CHAR"),
    NUMERIC(2,"NUMERIC"),
    DECIMAL(3,"DECIMAL"),
    INTEGER(4,"INT"),
    SMALLINT(5,"INT"),
    FLOAT(6,"FLOAT"),
    REAL(7,"FLOAT"),
    DOUBLE(8,"DOUBLE"),
    VARCHAR(12,"STRING"),
    DATE(91,"DATE"),
    TIME(92,"TIME"),
    TIMESTAMP(93,"TIMESTAMP"),
    OTHER(1111,"OTHER");
    private int value;
    private String key;

    JdbcFlinkType(int value, String key) {
        this.value = value;
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public static JdbcFlinkType getEnmuByValue(int value) {
        for (JdbcFlinkType item : values()) {
            if (value == item.getValue()) {
                return item;
            }
        }
        return null;
    }

    public static JdbcFlinkType getEnmuByKey(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        for (JdbcFlinkType item : values()) {
            if (key.equals(item.getKey())) {
                return item;
            }
        }
        return null;
    }
}
