package cn.bywin.business.bean.view.bydb;

import lombok.Data;

import java.util.List;

@Data
public class Operand {
    private String elementId;
    private String fieldName;
    //    private String oldFieldAlias;
    private String fieldExpr;
    //    private String value;
    private String fieldAlias;
    private String tableAlias;
    private String columnType;
    private String aggregation;
    //    private boolean distinct;
    private long cnt;
    private Integer isSelect;
    private List<AggConcat> concat;
    private String fieldType;
    private String id;

}
