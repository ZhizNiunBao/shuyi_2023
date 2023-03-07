package cn.bywin.business.service.olk;

import cn.bywin.business.bean.olk.TOlkDcServerDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.view.TokenVo;
import cn.bywin.business.bean.view.security.DataSecurityRequest;
import cn.bywin.business.bean.view.security.DataSecurityResult;
import cn.bywin.business.common.encrypt.JwtHs;
import cn.bywin.business.hetu.AuthType;
import cn.bywin.business.hetu.RsaUtil;
import cn.bywin.business.service.federal.DataApproveService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zzm
 */
@Component
public class DataSecurityComponent {

    public static final String SUCCESS_MESSAGE = "权限校验成功";

    public static final String ERROR_MESSAGE = "权限校验失败";

    public static final String NO_PERMISSION_PATTERN = "无访问 [%s] 权限";

    @Autowired
    private OlkObjectService objectService;

    @Autowired
    private DataApproveService dataApproveService;

    private Gson gson = new Gson();

    /**
     * 判断用户是否有权限访问指定资源
     * @param request   请求参数
     * @return          是否有权限
     */
    public DataSecurityResult hasPermission(DataSecurityRequest request) {
        String authType = request.getAuthType();
        if (AuthType.TOKEN.equals(authType)) {
            return checkDataPermission(request);
        } else if (AuthType.KEY.equals(authType)) {
            return checkKeyValid(request);
        }
        throw new IllegalArgumentException("未知认证方式: " + authType);
    }

    private DataSecurityResult checkDataPermission(DataSecurityRequest request) {
        // 获取用户信息
        String requestToken = request.getToken();
        String tokenString = JwtHs.vaildToken(requestToken);
        TokenVo tokenVo = gson.fromJson(tokenString, TokenVo.class);
        String userId = tokenVo.getUserId();

        String resourceName = request.getResourceName();
        TOlkObjectDo objectInfo = objectService.findByFullName(resourceName);
        if (objectInfo == null) {
            return DataSecurityResult.noPermissionResult(String.format("指定资源 [%s] 不存在", resourceName));
        }

        // 判断该资源是否为该用户添加
        boolean hasPermission = userId.equals(objectInfo.getUserId());

        // 判断用户是否有该资源的授权记录
        if (!hasPermission) {
            hasPermission = dataApproveService.hasApprove(userId, objectInfo.getId());
        }
        String message = hasPermission ? SUCCESS_MESSAGE : String.format(NO_PERMISSION_PATTERN, resourceName);
        return new DataSecurityResult(hasPermission, message);
    }

    private DataSecurityResult checkKeyValid(DataSecurityRequest request) {
        TOlkDcServerDo dcServerInfo = objectService.findBelongDcServer(request.getResourceName());
        if (dcServerInfo == null) {
            return DataSecurityResult.noPermissionResult("资源绑定节点不存在");
        }

        boolean validKey = true;
        try {
            String requestCode = RsaUtil.decryptByPrivateKey(dcServerInfo.getDcPriv(), request.getKey());
            if (!requestCode.equals(dcServerInfo.getDcCode())) {
                validKey = false;
            }
        } catch (Exception e) {
            validKey = false;
        }

        if (validKey) {
            DataSecurityResult dataSecurityResult = new DataSecurityResult(true, SUCCESS_MESSAGE);
            dataSecurityResult.setAuthType(AuthType.KEY);
            return dataSecurityResult;
        } else {
            return DataSecurityResult.noPermissionResult(String.format("请求 key [%s] 不合法", request.getKey()));
        }
    }
}
