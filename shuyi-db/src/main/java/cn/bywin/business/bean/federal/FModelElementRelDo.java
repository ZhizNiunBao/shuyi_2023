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
@Table ( name ="fl_model_element_rel" )
public class FModelElementRelDo extends SidEntityDo {


	/**
	 * 模型ID
	 */
	@ApiModelProperty(required = true, value = "模型ID", hidden = true, example = "")
   	@Column(name = "model_id" )
	private String modelId;

	/**
	 * 起始元素ID
	 */

	@ApiModelProperty(required = true, value = "起始元素ID", hidden = false, example = "")
   	@Column(name = "start_element_id" )
	private String startElementId;

	/**
	 * 目标元素ID
	 */

	@ApiModelProperty(required = true, value = "目标元素ID", hidden = false, example = "")
   	@Column(name = "end_element_id" )
	private String endElementId;

	@ApiModelProperty(required = true, value = "起点瞄点", hidden = false, example = "")
	@Column(name = "start_port_id")
	private String startPortId;

	@ApiModelProperty(required = true, value = "终点瞄点", hidden = false, example = "")
	@Column(name = "end_port_id")
	private String endPortId;


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
