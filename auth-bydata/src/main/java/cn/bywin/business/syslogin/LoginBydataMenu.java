package cn.bywin.business.syslogin;

import cn.bywin.business.api.BydataAuthClient;
import cn.bywin.business.common.base.AuthDepartmentBean;
import cn.bywin.business.common.base.AuthDepartmentUserBean;
import cn.bywin.business.common.base.AuthMenuBean;
import cn.bywin.business.common.base.AuthRoleLevelBean;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.bean.BydataApiResult;
import cn.bywin.business.common.bean.BydataListData;
import cn.bywin.business.common.bean.BydataMenuResult;
import cn.bywin.business.common.bean.KungraphMenuVo;
import cn.bywin.cache.ISysParamSetOp;
import cn.bywin.tools.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static cn.bywin.business.common.enums.SystemCode.KUNGRAPH;

/**
 * 获取中台菜单
 * @author zzm
 */
@Slf4j
public class LoginBydataMenu implements ILoginMenu {

    @Override
    public List userTopMenu(ISysParamSetOp setOp, String systemCode, UserDo user) throws Exception {
        BydataAuthClient authClient = SpringContextUtil.getBean(BydataAuthClient.class);
        BydataApiResult<BydataListData<BydataMenuResult>> userMenuTree = authClient.getUserMenuTree(KUNGRAPH);

        List<KungraphMenuVo> result = new ArrayList<>();
        if (!userMenuTree.isSuccess()) {
            log.error(userMenuTree.getMessage());
            return result;
        }
        List<BydataMenuResult> bydataMenuResults = userMenuTree.getData().getDataList();
        bydataMenuResults.stream().forEach(e -> result.add(e.changeToKungraphMenu()));
        return result;
    }

    @Override
    public List userSubMenu(ISysParamSetOp setOp, String systemCode, UserDo user, String menuCode) {
        BydataAuthClient authClient = SpringContextUtil.getBean(BydataAuthClient.class);
        BydataApiResult<BydataListData<BydataMenuResult>> userMenuTree = authClient.getUserMenuTree(KUNGRAPH);

        List<KungraphMenuVo> result = new ArrayList<>();
        if (!userMenuTree.isSuccess()) {
            log.error(userMenuTree.getMessage());
            return result;
        }
        List<BydataMenuResult> bydataMenuResults = userMenuTree.getData().getDataList();
        for (BydataMenuResult bydataMenuResult : bydataMenuResults) {
            String menuPath = bydataMenuResult.getPath();
            if (menuCode.equals(menuPath)) {
                bydataMenuResult.getChildren().stream().forEach(e -> result.add(e.changeToKungraphMenu()));
                break;
            }
        }
        return result;
    }

    @Override
    public List<KungraphMenuVo> userAllMenu(ISysParamSetOp setOp, String systemCode, UserDo user) throws Exception {
        return null;
    }

    @Override
    public List userSystem(ISysParamSetOp setOp, String excludeSystemCode, UserDo user) throws Exception {
        return null;
    }

    @Override
    public List<AuthMenuBean> allMenu(ISysParamSetOp setOp, String systemCode, UserDo user) throws Exception {
        return null;
    }

    @Override
    public boolean checkUserUrlPriv(ISysParamSetOp setOp, String systemCode, String url, UserDo user) throws Exception {
        return false;
    }

    @Override
    public List userPrivItem(ISysParamSetOp setOp, String systemCode, UserDo user, String menuCode) throws Exception {
        return null;
    }

    @Override
    public List systemUserList(ISysParamSetOp setOp, String systemCode, UserDo user, String deptId) throws Exception {
        return null;
    }

    @Override
    public List<AuthDepartmentBean> allDepartmentList(ISysParamSetOp setOp, UserDo user)
        throws Exception {
        return null;
    }

    @Override
    public AuthDepartmentBean userDepartment(ISysParamSetOp setOp, UserDo user) throws Exception {
        return null;
    }

    @Override
    public List<AuthDepartmentUserBean> departmentAndUser(ISysParamSetOp setOp, UserDo user)
        throws Exception {
        return null;
    }

    @Override
    public List<AuthDepartmentBean> userSubDepartment(ISysParamSetOp setOp, UserDo user)
        throws Exception {
        return null;
    }

    @Override
    public AuthRoleLevelBean userSystemAuthLevel(ISysParamSetOp setOp, UserDo user)
        throws Exception {
        return null;
    }
}
