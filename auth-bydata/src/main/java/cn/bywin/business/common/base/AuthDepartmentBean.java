package cn.bywin.business.common.base;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AuthDepartmentBean implements Serializable {

	//private String id;

	//private String pid;

	private String parentNo;

	private String parentName;

	private String deptNo;

	private String deptName;

	//private String deptCode;

	private String deptType;

	private String icon;

	private String url;

	private Long deptOrder;

	private List<AuthDepartmentBean> children;

	public String getParentNo() {
		return parentNo;
	}

	public void setParentNo(String parentNo) {
		this.parentNo = parentNo;
	}

	public String getDeptNo() {
		return deptNo;
	}

	public void setDeptNo(String deptNo) {
		this.deptNo = deptNo;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getDeptType() {
		return deptType;
	}

	public void setDeptType(String deptType) {
		this.deptType = deptType;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getDeptOrder() {
		return deptOrder;
	}

	public void setDeptOrder(Long deptOrder) {
		this.deptOrder = deptOrder;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public List<AuthDepartmentBean> getChildren() {
		return children;
	}

	public void setChildren(List<AuthDepartmentBean> children) {
		this.children = children;
	}
}
