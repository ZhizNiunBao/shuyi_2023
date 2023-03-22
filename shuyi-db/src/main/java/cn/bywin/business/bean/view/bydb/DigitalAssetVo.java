package cn.bywin.business.bean.view.bydb;


import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;


@Data
public class DigitalAssetVo extends SidEntityDo {

	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "data_type")
	private String dataType;

	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "scatalog")
	private String scatalog;
	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "obj_id")
	private String objId;

	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "owner_id")
	private String ownerId;

	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "user_id")
	private String userId;

	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "user_name")
	private String userName;

	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "share_flag")
	private Integer shareFlag;
	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "schema_id")
	private String schemaId;
	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "db_id")
	private String dbId;
	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "datasource_id")
	private String datasourceId;
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "model_object_id")
	private String modelObjectId;

	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "object_name")
	private String objectName;
	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "obj_full_name")
	private String objFullName;
	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "obj_chn_name")
	private String objChnName;


	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "use_cnt")
	private Integer useCnt;
	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "priv_flag")
	private Integer privFlag;

	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "ds_label")
	private String dsLabel;
	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "stype")
	private String stype;
	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "obj_size")
	private Long objSize;
	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "schema_name")
	private String schemaName;
	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "schema_chn_name")
	private String schemaChnName;
	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "db_name")
	private String dbName;
	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "dc_db_name")
	private String dcDbName;
	/**
	 *
	 */
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "db_chn_name")
	private String dbChnName;

	@ApiModelProperty(required = false, value = "删除标志", hidden = true, example = "")
	@Column(name = "del_flag")
	private Integer delFlag;

	@ApiModelProperty(required = false, value = "是否可用", hidden = true, example = "")
	@Column(name = "enable")
	private Integer enable;

	@ApiModelProperty(required = false, value = "备注", hidden = true, example = "")
	@Column(name = "remark")
	private String remark;

	@ApiModelProperty(required = false, value = "是否收藏", hidden = true, example = "")
	@Column(name = "favourite_flag")
	private Integer favouriteFlag;

	@ApiModelProperty(required = false, value = "收藏次数", hidden = true, example = "")
	@Column(name = "favourite_count")
	private Integer favouriteCount;

	@ApiModelProperty(required = false, value = "用户评论条数", hidden = true, example = "")
	@Column(name = "discuss_user_count")
	private Integer discussUserCount;

	@ApiModelProperty(required = false, value = "评论总条数", hidden = true, example = "")
	@Column(name = "discuss_total_count")
	private Integer discussTotalCount;

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
