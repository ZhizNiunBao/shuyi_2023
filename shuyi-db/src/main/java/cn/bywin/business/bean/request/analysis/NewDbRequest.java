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

    @ApiModelProperty("目录名称")
    private String catalogName;

    @ApiModelProperty("父目录Id, 如果没有则为空")
    private String catalogType;

    @ApiModelProperty("目录中文名称")
    private String chName;

    @ApiModelProperty("数据库类型")
    private String connectorName;

    @ApiModelProperty("连接参数")
    private String propSet;

}
