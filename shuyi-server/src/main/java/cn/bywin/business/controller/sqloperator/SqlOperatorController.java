package cn.bywin.business.controller.sqloperator;

import cn.bywin.business.bean.request.sqloperator.SqlOperatorAddRequest;
import cn.bywin.business.bean.response.sqloperator.SqlParseVo;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.result.CommonResult;
import cn.bywin.business.common.result.ResultUtil;
import cn.bywin.business.common.result.SingleResult;
import cn.bywin.business.service.sqloperator.SqlOperatorService;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = {"*"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
    maxAge = 3600)
@RestController
@Api(tags = "sql算子管理")
@RequestMapping("/sqlopetator")
public class SqlOperatorController {

    @Autowired
    private SqlOperatorService sqlOperatorService;

    @RequestMapping(value = "/sqlparse", method = {RequestMethod.POST})
    public SingleResult<SqlParseVo> sqlparse(@RequestBody String sql) {
        UserDo user = LoginUtil.getUser();
        Preconditions.checkArgument(user != null,"用户未登录");
        return ResultUtil.successSingleResult(sqlOperatorService.parseSql(sql));
    }

    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public CommonResult addOperator(@RequestBody SqlOperatorAddRequest sqlOperatorAdd)
        throws Exception {
        UserDo user = LoginUtil.getUser();
        Preconditions.checkArgument(user != null,"用户未登录");
        sqlOperatorService.insertOperator(sqlOperatorAdd, user);
        return ResultUtil.getResult();
    }

    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public CommonResult updateOperator(@RequestBody SqlOperatorAddRequest sqlOperatorAdd)
        throws Exception {
        UserDo user = LoginUtil.getUser();
        Preconditions.checkArgument(user != null,"用户未登录");
        sqlOperatorService.updateOperator(sqlOperatorAdd, user);
        return ResultUtil.getResult();
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.GET})
    public CommonResult deleteOperator(@RequestParam("operatorId") String operatorId)
        throws Exception {
        UserDo user = LoginUtil.getUser();
        Preconditions.checkArgument(user != null,"用户未登录");
        sqlOperatorService.deleteOperator(operatorId, user);
        return ResultUtil.getResult();
    }

    @RequestMapping(value = "/page", method = {RequestMethod.GET})
    public CommonResult page(@RequestParam("currentPage") Integer currentPage,
                             @RequestParam("pageSize") Integer pageSize,
                             @RequestParam(value = "name",required = false) String name,
                             @RequestParam(value = "type",required = false) String type) throws Exception {
        UserDo user = LoginUtil.getUser();
        Preconditions.checkArgument(user != null,"用户未登录");
        return ResultUtil.successListResult(sqlOperatorService.queryPage(currentPage, pageSize, name, type, user));
    }

    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    public CommonResult info(@RequestParam("operatorId") String operatorId) throws Exception {
        UserDo user = LoginUtil.getUser();
        Preconditions.checkArgument(user != null,"用户未登录");
        return ResultUtil.successSingleResult(sqlOperatorService.getOperatorInfo(operatorId, user));
    }
}
