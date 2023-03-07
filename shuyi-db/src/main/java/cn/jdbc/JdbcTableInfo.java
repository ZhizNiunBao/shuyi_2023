package cn.jdbc;

public class JdbcTableInfo {

    String schemaname;
    String tablename;
    String comment;
    boolean talbeFlag;

    public JdbcTableInfo() {
    }

    public JdbcTableInfo(String schemaname, String tablename, String comment,boolean talbeFlag) {
        this.schemaname = schemaname;
        this.tablename = tablename;
        this.comment = comment;
        this.talbeFlag = talbeFlag;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getSchemaname() {
        return schemaname;
    }

    public void setSchemaname(String schemaname) {
        this.schemaname = schemaname;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isTalbeFlag() {
        return talbeFlag;
    }

    public void setTalbeFlag( boolean talbeFlag ) {
        this.talbeFlag = talbeFlag;
    }
}
