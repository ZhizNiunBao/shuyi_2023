package cn.bywin.business.service.sqloperator;

import cn.bywin.business.bean.request.sqloperator.SqlOperatorAddRequest;
import cn.bywin.business.bean.response.sqloperator.SqlOperatorInfoVo;
import cn.bywin.business.bean.response.sqloperator.SqlOperatorVo;
import cn.bywin.business.bean.response.sqloperator.SqlParseVo;
import cn.bywin.business.common.base.UserDo;
import java.util.List;

public interface SqlOperatorService {

    SqlParseVo parseSql(String sql);

    void insertOperator(SqlOperatorAddRequest sqlOperatorAdd, UserDo userDo) throws Exception;

    void updateOperator(SqlOperatorAddRequest sqlOperatorAdd, UserDo userDo) throws Exception;

    void deleteOperator(String operatorId, UserDo userDo) throws Exception;

    List<SqlOperatorVo> queryPage(Integer currentPage, Integer pageSize, String name, String type, UserDo userDo);

    SqlOperatorInfoVo getOperatorInfo(String operatorId, UserDo userDo);
}
