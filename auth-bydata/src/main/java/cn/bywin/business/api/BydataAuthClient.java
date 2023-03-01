package cn.bywin.business.api;

import cn.bywin.business.common.bean.BydataApiResult;
import cn.bywin.business.common.bean.BydataListData;
import cn.bywin.business.common.bean.BydataMenuResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 中台认证相关接口
 * @author zzm
 */
@RequestMapping(value = "/api/bydata-auth/auth")
public interface BydataAuthClient {

    @RequestMapping(value = "/getUserInfoByToken", method = RequestMethod.GET)
    BydataApiResult<Map> getUserByToken(@RequestParam("token") String token);

    @RequestMapping(value = "/user/menu/loginUserMenutree", method = RequestMethod.GET)
    BydataApiResult<BydataListData<BydataMenuResult>> getUserMenuTree(@RequestParam("modType") String modType);

    @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
    BydataApiResult logout();


}
