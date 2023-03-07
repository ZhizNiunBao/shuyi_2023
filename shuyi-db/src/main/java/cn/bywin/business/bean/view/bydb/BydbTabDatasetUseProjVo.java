package cn.bywin.business.bean.view.bydb;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

@Data
public class BydbTabDatasetUseProjVo implements Serializable {
	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "id")
	private String id;

	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "object_Id")
	private String objectId;

	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "object_name")
	private String objectName;

	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "obj_chn_name")
	private String objChnName;

	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "node_party_id")
	private String nodePartyId;

	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "node_party_name")
	private String nodePartyName;

	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "proj_id")
	private String projId;

	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "proj_name")
	private String projName;

	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "proj_user_id")
	private String projUserId;

	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "proj_user_name")
	private String projUserName;

	@ApiModelProperty(required = false, value = "", hidden = true, example = "")
	@Column(name = "proj_user_account")
	private String projUserAccount;

}
