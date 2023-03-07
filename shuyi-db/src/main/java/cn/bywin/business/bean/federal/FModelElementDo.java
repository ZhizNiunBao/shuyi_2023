package cn.bywin.business.bean.federal;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
@Data
@Entity
@Table ( name ="fl_model_element" )
public class FModelElementDo  extends SidEntityDo {

	@ApiModelProperty(required = true, value = "组件ID", hidden = true, example = "")
	@Column(name = "component_id")
	private String componentId;

	@ApiModelProperty(required = true, value = "前端所需参数", hidden = true, example = "")
	@Column(name = "ports")
	private String ports;

	/**
	 * 组件名称
	 */
	@ApiModelProperty(required = true, value = "组件名称", hidden = false, example = "")
   	@Column(name = "name" )
	private String name;

	/**
	 * 模型id
	 */
	@ApiModelProperty(required = true, value = "模型id", hidden = true, example = "")
   	@Column(name = "model_id" )
	private String modelId;

	/**
	 * 组件数据
	 */
	@Column(name = "data" )
	@ApiModelProperty(required = true, value = "组件数据", hidden = true, example = "")
	private String data;

	/**
	 * 组件配置
	 */
	@Column(name = "config" )
	@ApiModelProperty(required = true, value = "组件配置", hidden = true, example = "")
	private String config;
	/**
	 * x坐标
	 */
	@ApiModelProperty(required = true, value = "x坐标", hidden = false, example = "")
   	@Column(name = "x" )
	private String x;

	/**
	 * y坐标
	 */
	@ApiModelProperty(required = true, value = "y坐标", hidden = false, example = "")
   	@Column(name = "y" )
	private String y;

	/**
	 * 图标形状
	 */
   	@Column(name = "shape" )
	@ApiModelProperty(required = true, value = "图标形状", hidden = false, example = "")
	private String shape;

	/**
	 * 图标
	 */
   	@Column(name = "icon" )
	@ApiModelProperty(required = true, value = "图标", hidden = false, example = "")
	private String icon;

	/**
	 * 创建人ID
	 */
	@ApiModelProperty(required = true, value = "创建人ID", hidden = true, example = "")
	@Column(name = "creator_id")
	private String creatorId;
	/**
	 * 创建人帐号
	 */
	@ApiModelProperty(required = true, value = "创建人帐号", hidden = true, example = "")
	@Column(name = "creator_account")
	private String creatorAccount;
	/**
	 * 创建人姓名
	 */
	@ApiModelProperty(required = true, value = "创建人姓名", hidden = true, example = "")
	@Column(name = "creator_name")
	private String creatorName;

}
