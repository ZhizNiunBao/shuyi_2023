package cn.bywin.business.common.base;


import java.io.Serializable;
import java.util.List;

public class AuthDepartmentUserBean extends AuthDepartmentBean implements Serializable {

	 List<AuthUserBean> userList;
	 List<AuthDepartmentUserBean> subDeptList;

	public List<AuthUserBean> getUserList() {
		return userList;
	}

	public void setUserList(List<AuthUserBean> userList) {
		this.userList = userList;
	}

	public List<AuthDepartmentUserBean> getSubDeptList() {
		return subDeptList;
	}

	public void setSubDeptList(List<AuthDepartmentUserBean> subDeptList) {
		this.subDeptList = subDeptList;
	}
}
