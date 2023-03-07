package cn.jdbc;

import lombok.Data;

@Data
public class JdbcColumnInfo {

    String schemaname;
    String tablename;
    String columnname;
    int ordinalposition;
    String datatype;
    String columntype;
    Long colen;
    Integer colpercision;
    Integer colscale;
    String columncomment;

    public JdbcColumnInfo() {
    }

    public JdbcColumnInfo(String schemaname, String tablename, String columnname, int ordinalposition, String datatype, String columntype, String columncomment) {
        this.schemaname = schemaname;
        this.tablename = tablename;
        this.columnname = columnname;
        this.ordinalposition = ordinalposition;
        this.datatype = datatype;
        this.columntype = columntype;
        this.columncomment = columncomment;
    }
    public JdbcColumnInfo(String schemaname, String tablename, String columnname, int ordinalposition, String datatype, String columntype,Long colen,Integer colpercision, String columncomment) {
        this.schemaname = schemaname;
        this.tablename = tablename;
        this.columnname = columnname;
        this.ordinalposition = ordinalposition;
        this.datatype = datatype;
        this.columntype = columntype;
        this.columncomment = columncomment;
        this.colen = colen;
        this.colpercision  = colpercision;
    }
//
//    public String getSchemaname() {
//        return schemaname;
//    }
//
//    public void setSchemaname(String schemaname) {
//        this.schemaname = schemaname;
//    }
//
//    public String getTablename() {
//        return tablename;
//    }
//
//    public void setTablename(String tablename) {
//        this.tablename = tablename;
//    }
//
//    public String getColumnname() {
//        return columnname;
//    }
//
//    public void setColumnname(String columnname) {
//        this.columnname = columnname;
//    }
//
//    public int getOrdinalposition() {
//        return ordinalposition;
//    }
//
//    public void setOrdinalposition(int ordinalposition) {
//        this.ordinalposition = ordinalposition;
//    }
//
//    public String getDatatype() {
//        return datatype;
//    }
//
//    public void setDatatype(String datatype) {
//        this.datatype = datatype;
//    }
//
//    public String getColumntype() {
//        return columntype;
//    }
//
//    public void setColumntype(String columntype) {
//        this.columntype = columntype;
//    }
//
//    public String getColumncomment() {
//        return columncomment;
//    }
//
//    public void setColumncomment(String columncomment) {
//        this.columncomment = columncomment;
//    }
//

}
