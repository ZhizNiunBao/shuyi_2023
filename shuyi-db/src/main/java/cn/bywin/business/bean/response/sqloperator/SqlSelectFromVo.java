package cn.bywin.business.bean.response.sqloperator;

import lombok.Data;

/**
 * @author lhw
 */
@Data
public class SqlSelectFromVo {

    private String sourceTable;

    private String sFields;

    private String sourceFields;
}
