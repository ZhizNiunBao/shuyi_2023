package cn.bywin.business.bean.view.olk;

import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OlkNode {

    @ApiModelProperty("数据源")
    private FDatasourceDo dbSource;
    @ApiModelProperty("表对象")
    private TOlkModelObjectDo database;
    @ApiModelProperty("节点id")
    private String id;
    @ApiModelProperty("前端选中节点id")
    private List<String> viewIds;
    @ApiModelProperty("节点名称")
    private String name;
    @ApiModelProperty("原名称")
    private String origName;
    @ApiModelProperty("资源id")
    private String tcId;
    @ApiModelProperty("模型id")
    private String modelId;
    @ApiModelProperty("节点状态")
    private Integer nodeStatus;
    @ApiModelProperty("节点配置")
    private OlkOperators operators;
    @ApiModelProperty("节点配置")
    private Map<String,Object> params;
    @ApiModelProperty("数据限制")
    private Integer total;
    @ApiModelProperty("节点类型")
    private String type;
}
