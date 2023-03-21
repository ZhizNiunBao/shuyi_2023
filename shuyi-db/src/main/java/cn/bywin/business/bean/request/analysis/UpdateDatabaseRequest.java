package cn.bywin.business.bean.request.analysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zzm
 */
@Data
@ApiModel(value = "UpdateDatabaseRequest", description = "修改数据目录请求体")
public class UpdateDatabaseRequest {

    @ApiModelProperty(value = "id", required = true)
    private String id;

    @ApiModelProperty(value = "中文名称", required = true)
    private String dbChnName;
}
