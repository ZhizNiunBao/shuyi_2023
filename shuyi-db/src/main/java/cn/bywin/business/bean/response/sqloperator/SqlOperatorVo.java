package cn.bywin.business.bean.response.sqloperator;

import lombok.Data;

/**
 * @author lhw
 */
@Data
public class SqlOperatorVo {

    private String id;

    private String name;

    private String operatorType;

    private String operatorTypeName;

    private String operatorDesc;

    private String scriptContent;

    private String optStatus;
}
