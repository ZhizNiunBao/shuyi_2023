package cn.bywin.business.bean.system;

import cn.bywin.business.common.base.SidEntityDo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description  
 * @Author  drp
 * @Date 2019-12-20 
 */
@Data
@Entity
@Table ( name ="sys_menu" )
public class SysMenuDo  extends SidEntityDo {


	/**
	 * 名称
	 */
   	@Column(name = "pid" )
	private String pid;


	/**
	 * 名称
	 */
   	@Column(name = "menu_name" )
	private String menuName;


	/**
	 * iframe 菜单值
	 */
	@Column(name = "menu_value" )
	private String menuValue;

	/**
	 * 编码
	 */
   	@Column(name = "menu_code" )
	private String menuCode;
	/**
	 * 层级
	 */
	@Column(name = "level" )
	private Integer level;
	/**
	 * 图标大小
	 */
	@Column(name = "icon_size" )
	private Integer iconSize;

	/**
	 * 类型 菜单 功能
	 */
   	@Column(name = "menu_type" )
	private String menuType;

	/**
	 * 图标
	 */
   	@Column(name = "icon" )
	private String icon;

	/**
	 * 功能地址
	 */
   	@Column(name = "url" )
	private String url;

	/**
	 * 是否显示
	 */
   	@Column(name = "show_flag" )
	private Long showFlag;

	/**
	 * 排序
	 */
   	@Column(name = "menu_order" )
	private Integer menuOrder;

	/**
	 * 创建人ID
	 */
   	@Column(name = "creator_id" )
	private String creatorId;

	/**
	 * 创建人帐号
	 */
   	@Column(name = "creator_account" )
	private String creatorAccount;

	/**
	 * 创建人姓名
	 */
   	@Column(name = "creator_name" )
	private String creatorName;

}
