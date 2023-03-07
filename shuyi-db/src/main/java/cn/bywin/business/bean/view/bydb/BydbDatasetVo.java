package cn.bywin.business.bean.view.bydb;


import cn.bywin.business.bean.bydb.TBydbDatasetDo;
//import cn.bywin.business.bean.bydb.TBydbGrantObjectDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class BydbDatasetVo extends TBydbDatasetDo {

	@ApiModelProperty(value = "服务器sql语句",hidden = true)
	private String centreSql;
	@ApiModelProperty(value = "节点sql语句" ,example = "")
	private String dcSql;
	@ApiModelProperty(value = "where条件", example = "")
	private String whereCond;
	@ApiModelProperty(value = "内容id",hidden = true)
	private String contentId;
	//@ApiModelProperty("授权")
	//List<TBydbGrantObjectDo> grantList;

}
