package cn.bywin.business.bean.view.bydb;


import cn.bywin.business.bean.bydb.TBydbFieldDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.List;


@Data
public class BydbDatabaseSourceVo  extends FDatasourceDo {

	@ApiModelProperty( value = "目录分组ID，最大长度(32)" )
	@Column( name = "catalog_type" )
	private String catalogType;

	@ApiModelProperty( value = "数据源id" )
	@Column( name = "dbsource_id" )
	private String dbsourceId;

	@Transient
	private List<TBydbFieldDo> fieldList;

}
