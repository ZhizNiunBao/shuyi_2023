package cn.bywin.business.bean.response.sqloperator;

import java.util.List;
import lombok.Data;

/**
 * @author lhw
 */
@Data
public class SqlOperatorInfoVo {

    SqlOperatorVo sqlOperatorVo;

    List<SqlOperatorInVo> sqlOperatorInVoList;

    List<SqlOperatorTableVo> sqlOperatorTableVoList;
}
