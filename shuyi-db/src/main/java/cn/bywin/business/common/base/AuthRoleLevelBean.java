package cn.bywin.business.common.base;


import java.io.Serializable;

public enum AuthRoleLevelBean implements Serializable {

//	all 全部数据权限
//	department 本部门数据权限
//	department_and_below 本部门及以下数据权限
//	personal 仅本人数据权限
//	custom 自定义数据权限

	ALL(1, "all","全部数据权限"),
	DEPARTMENT(2, "department","本部门数据权限"),
	DEPARTMENT_AND_BELOW(3, "department_and_below","本部门及以下数据权限"),
	PERSONAL(4, "personal","仅本人数据权限"),
	CUSTOM(5, "custom","自定义数据权限");

	private int level;
	private String code;
	private String name;

	AuthRoleLevelBean(int level, String code, String name) {
		this.level = level;
		this.code = code;
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

}
