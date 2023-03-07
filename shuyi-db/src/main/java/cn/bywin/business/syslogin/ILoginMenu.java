package cn.bywin.business.syslogin;

import cn.bywin.business.common.base.*;
import cn.bywin.cache.ISysParamSetOp;

import java.util.List;

/**
 * 登录与权限控制层
 *
 * @author me
 */

public interface ILoginMenu {

    public List userTopMenu(ISysParamSetOp setOp, String systemCode, UserDo user) throws Exception;

    public List userSubMenu(ISysParamSetOp setOp, String systemCode, UserDo user, String menuCode) throws Exception;

    public List userAllMenu(ISysParamSetOp setOp, String systemCode, UserDo user) throws Exception;

    public List userSystem(ISysParamSetOp setOp, String excludeSystemCode, UserDo user) throws Exception;

    public List<AuthMenuBean> allMenu(ISysParamSetOp setOp, String systemCode, UserDo user) throws Exception;

    public boolean checkUserUrlPriv(ISysParamSetOp setOp, String systemCode, String url, UserDo user) throws Exception;

    public List userPrivItem(ISysParamSetOp setOp,String systemCode, UserDo user, String menuCode) throws Exception ;

    public List<AuthUserBean> systemUserList(ISysParamSetOp setOp, String systemCode, UserDo user, String deptNo) throws Exception ;

    public List<AuthDepartmentBean> allDepartmentList(ISysParamSetOp setOp, UserDo user) throws Exception ;

    public AuthDepartmentBean userDepartment(ISysParamSetOp setOp,UserDo user) throws Exception ;

    public List<AuthDepartmentUserBean> departmentAndUser(ISysParamSetOp setOp, UserDo user) throws Exception ;

    public List<AuthDepartmentBean> userSubDepartment(ISysParamSetOp setOp,UserDo user) throws Exception ;

    public AuthRoleLevelBean userSystemAuthLevel(ISysParamSetOp setOp, UserDo user) throws Exception ;
}
