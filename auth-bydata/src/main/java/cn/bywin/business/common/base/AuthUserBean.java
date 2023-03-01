package cn.bywin.business.common.base;



import java.io.Serializable;

public class AuthUserBean implements Serializable {

	private String username;

	private String chnname;

	//private String deptId;

	private String deptNo;

	private String department;

	private String mobile;

	private String email;

	//private Date regDate;

	private Long isLock;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getChnname() {
		return chnname;
	}

	public void setChnname(String chnname) {
		this.chnname = chnname;
	}

	public String getDeptNo() {
		return deptNo;
	}

	public void setDeptNo(String deptNo) {
		this.deptNo = deptNo;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getIsLock() {
		return isLock;
	}

	public void setIsLock(Long isLock) {
		this.isLock = isLock;
	}
}
