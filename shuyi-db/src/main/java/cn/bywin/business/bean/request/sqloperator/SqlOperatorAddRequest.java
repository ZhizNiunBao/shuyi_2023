package cn.bywin.business.bean.request.sqloperator;

import cn.bywin.business.bean.response.sqloperator.SqlOperatorInVo;
import cn.bywin.business.bean.response.sqloperator.SqlOperatorTableVo;
import cn.bywin.business.bean.response.sqloperator.SqlOperatorVo;
import java.util.List;
import lombok.Data;

@Data
public class SqlOperatorAddRequest {
    SqlOperatorVo sqlOperatorVo;
    List<SqlOperatorInVo> sqlOperatorInVoList;
    List<SqlOperatorTableVo> sqlOperatorTableVoList;
}
