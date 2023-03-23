package cn.bywin.business.bean.request.analysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zzm
 */
@Data
@ApiModel(value = "AddObjectRequest", description = "新增模型对象请求体")
public class AddObjectRequest {

    @ApiModelProperty(value = "模型Id", required = true)
    private String modelId;

    @ApiModelProperty(value = "表对象Id", required = true)
    private String objectId;

}
