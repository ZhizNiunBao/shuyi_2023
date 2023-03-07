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
@Table ( name ="sys_param_set" )
public class SysParamSetDo  extends SidEntityDo {


	/**
	 * 代码
	 */
   	@Column(name = "para_code" )
	private String paraCode;

	/**
	 * 名称
	 */
   	@Column(name = "para_name" )
	private String paraName;

	/**
	 * 参数值
	 */
   	@Column(name = "para_value" )
	private String paraValue;

	/**
	 * 类别id
	 */
   	@Column(name = "type_id" )
	private String typeId;

	/**
	 * 类别名称
	 */
   	@Column(name = "type_name" )
	private String typeName;

	/**
	 * 排序
	 */
   	@Column(name = "para_order" )
	private Long paraOrder;

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
