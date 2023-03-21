package cn.bywin.business.bean.request.analysis;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zzm
 */
@Data
@ApiModel(value = "RefreshSchemaRequest", description = "刷新数据目录请求体")
public class RefreshSchemaRequest {

    @ApiModelProperty("目录id")
    private String dbId;

    @ApiModelProperty("模式id")
    private String schemaId;

    @ApiModelProperty("对象id")
    private String objectId;

}
