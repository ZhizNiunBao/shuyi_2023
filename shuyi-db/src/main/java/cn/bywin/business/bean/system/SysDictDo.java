package cn.bywin.business.bean.system;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table ( name ="sys_dict" )
public class SysDictDo  extends SidEntityDo {


	/**
	 * 代码
	 */
   	@ApiModelProperty("代码")
   	@Column(name = "dict_code" )
	private String dictCode;

	/**
	 * 名称
	 */
   	@ApiModelProperty("名称")
   	@Column(name = "dict_name" )
	private String dictName;

	/**
	 * 排序
	 */
   	@ApiModelProperty("排序")
   	@Column(name = "dict_order" )
	private Long dictOrder;

	/**
	 * 上级ID
	 */
   	@ApiModelProperty("上级ID")
   	@Column(name = "pid" )
	private String pid;

	@ApiModelProperty("备注")
	@Column(name = "remark" )
	private String remark;

	/**
	 * 顶级ID
	 */
   	@ApiModelProperty("顶级ID")
   	@Column(name = "top_id" )
	private String topId;

   	@Column(name = "top_code" )
	private String topCode;

	/**
	 * 是否显示 ：1显示  0不显示
	 */
   	@ApiModelProperty("是否显示 ：1显示  0不显示")
   	@Column(name = "display" )
	private String display;

	/**
	 * 创建人ID
	 */
   	@ApiModelProperty("创建人ID")
   	@Column(name = "creator_id" )
	private String creatorId;

	/**
	 * 创建人帐号
	 */
   	@ApiModelProperty("创建人帐号")
   	@Column(name = "creator_account" )
	private String creatorAccount;

	/**
	 * 创建人姓名
	 */
   	@ApiModelProperty("创建人姓名")
   	@Column(name = "creator_name" )
	private String creatorName;

}
