package cn.bywin.business.bean.component.template;

import static cn.bywin.business.bean.component.ComponentEnum.DATA_TYPE;

import cn.bywin.business.bean.federal.CheckComponent;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.common.util.JsonUtil;
import java.util.Map;
import lombok.Data;

/**
 * @Description 数据缺省值组件模板
 * @Author wangh
 * @Date 2021-07-30
 */
@Data
public class TFillMissingComponent extends BaseComponenT {


    private String missing_fill_method = "mean";
    private boolean missing_fill = true;

    public TFillMissingComponent() {
    }


    @Override
    public Object init(FModelElementDo preModelElementDo) {

        TFillMissingComponent fillMissingComponentT = new TFillMissingComponent();


        return JsonUtil.toSimpleJson(fillMissingComponentT);
    }

    @Override
    public CheckComponent check(FModelElementDo fModelElementDo, FComponentDo lastComponent, String component) {

        if (!DATA_TYPE.equals(lastComponent.getTypes())) {
            return new CheckComponent(false, String.format("组件[%s]中发生了异常：组件前置配置不匹配(前置组件应为数据集组件或)，请检查流程是否合理。", component));
        }

        return new CheckComponent(true, "success");
    }

    @Override
    public void build(Map<String, Object> module, Map<String, Object> model, Map<String, Integer> num,
                      Map<String, Object> common, String preComponent,
                      FModelElementDo fModelElementDo, FComponentDo componentDo) {


        Object o = model.get(preComponent);
        Map map = JsonUtil.gson().fromJson(JsonUtil.toJson(o), Map.class);
        TFillMissingComponent fillMissingComponent = JsonUtil.gson().fromJson(JsonUtil.toJson(JsonUtil.gson().fromJson(fModelElementDo.getConfig(), Map.class)), TFillMissingComponent.class);
        map.put("missing_fill", fillMissingComponent.isMissing_fill());
        map.put("missing_fill_method", fillMissingComponent.getMissing_fill_method());
        model.put(preComponent, map);


    }

}
