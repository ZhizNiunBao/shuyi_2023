package cn.bywin.business.bean.request.analysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zzm
 */
@Data
@ApiModel(value = "NewDbRequest", description = "新增数据目录请求体")
public class NewDbRequest {

    @ApiModelProperty(value = "目录名称", required = true)
    private String catalogName;

    @ApiModelProperty("父目录Id, 如果没有则为空")
    private String catalogType;

    @ApiModelProperty(value = "目录中文名称", required = true)
    private String chName;

    @ApiModelProperty(value = "数据库类型", required = true)
    private String connectorName;

    @ApiModelProperty(value = "连接参数", required = true)
    private String propSet;

}
