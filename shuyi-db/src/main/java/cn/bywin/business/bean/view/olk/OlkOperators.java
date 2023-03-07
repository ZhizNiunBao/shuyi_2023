package cn.bywin.business.bean.view.olk;


import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class OlkOperators {
    @ApiModelProperty("字段")
    private List<TOlkModelFieldDo> fields;
    @ApiModelProperty("额外添加的字段")
    private List<TOlkModelFieldDo> filters;



}
