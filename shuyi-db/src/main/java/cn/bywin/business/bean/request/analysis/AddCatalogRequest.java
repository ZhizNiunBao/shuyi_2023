package cn.bywin.business.bean.request.analysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zzm
 */
@Data
@ApiModel(value = "AddCatalogRequest", description = "新增目录请求体")
public class AddCatalogRequest {

    @ApiModelProperty(value = "父目录Id，如果父目录是根节点则为空")
    private String pid;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "标签")
    private String tags;

    @ApiModelProperty(value = "目录名称", required = true)
    private String typeName;

}
