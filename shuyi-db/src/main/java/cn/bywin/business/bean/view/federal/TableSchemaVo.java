package cn.bywin.business.bean.view.federal;

import lombok.Data;

import java.util.List;

@Data
public class TableSchemaVo {
    private String tableName;
    private String tableComment;
    private String tableSchema;
    private String type;
    private List<TableSchemaVo> children;


}
