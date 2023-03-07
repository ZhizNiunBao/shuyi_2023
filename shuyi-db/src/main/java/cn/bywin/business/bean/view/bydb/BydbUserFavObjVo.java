package cn.bywin.business.bean.view.bydb;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class BydbUserFavObjVo {

	@ApiModelProperty(value = "归属帐号",hidden = false)
	private String ownerAccount;
	@ApiModelProperty("授权")
	List<String> objIdList;

}
