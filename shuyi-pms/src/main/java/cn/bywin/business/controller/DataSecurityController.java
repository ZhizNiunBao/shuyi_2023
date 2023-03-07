package cn.bywin.business.controller;

import cn.bywin.business.bean.view.security.DataSecurityRequest;
import cn.bywin.business.bean.view.security.DataSecurityResult;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.service.olk.DataSecurityComponent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author zzm
 */
@RestController
@Api(tags = "数据安全接口")
@RequestMapping("/security")
@Slf4j
public class DataSecurityController {

    @Autowired
    private DataSecurityComponent dataSecurityComponent;

    @RequestMapping(value = "/hasPermission", method = {RequestMethod.POST})
    @ApiOperation(value = "判断用户是否有对应资源的权限", notes = "判断用户是否有对应资源的权限")
    public Map<String, Object> hasPermission(@RequestBody DataSecurityRequest request) {
        log.info("用户数据权限判断, 请求参数: {}", request);
        ResponeMap result = new ResponeMap();
        try {
            DataSecurityResult dataSecurityResult = dataSecurityComponent.hasPermission(request);
            result.setSingleOk(dataSecurityResult, "权限校验成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            DataSecurityResult dataPermissionResult = new DataSecurityResult(false, DataSecurityComponent.ERROR_MESSAGE);
            result.put("data", dataPermissionResult);
            result.setErr(e.getMessage());
        }
        return result.getResultMap();
    }
}
