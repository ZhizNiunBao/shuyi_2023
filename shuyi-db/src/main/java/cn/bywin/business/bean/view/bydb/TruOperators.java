package cn.bywin.business.bean.view.bydb;


import cn.bywin.business.bean.bydb.TTruModelFieldDo;
import cn.bywin.business.bean.bydb.TTruModelWaterMarkDo;
import cn.bywin.business.bean.bydb.TTruModelWindowDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TruOperators {
    @ApiModelProperty("字段")
    private List<TTruModelFieldDo> fields;
    @ApiModelProperty("额外添加的字段")
    private List<TTruModelFieldDo> filters;
    @ApiModelProperty("自定义的函数字段")
    private List<TTruModelFieldDo> functions;
    @ApiModelProperty("水位线配置")
    private TTruModelWaterMarkDo watermark;
    @ApiModelProperty("窗口配置、")
    private TTruModelWindowDo window;
}
