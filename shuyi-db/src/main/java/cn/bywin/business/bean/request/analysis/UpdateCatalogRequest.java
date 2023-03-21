package cn.bywin.business.bean.request.analysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zzm
 */
@Data
@ApiModel(value = "UpdateCatalogRequest", description = "修改目录请求体")
public class UpdateCatalogRequest {

    @ApiModelProperty(value = "目录Id", required = true)
    private String id;

    @ApiModelProperty(value = "目录名称", required = true)
    private String typeName;

}
