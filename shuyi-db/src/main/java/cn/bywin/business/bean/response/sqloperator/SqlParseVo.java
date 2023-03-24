package cn.bywin.business.bean.response.sqloperator;

import cn.bywin.business.bean.sqloperator.TSqlOperatorInDo;
import java.util.List;
import lombok.Data;

/**
 * @author lhw
 */
@Data
public class SqlParseVo {

    private String sql_format;

    private List<SqlSelectFromVo> selectfromlist;

    private List<TSqlOperatorInDo> sqlIn;

    private List<SqlOutVo> sqlOut;

}
