package cn.bywin.business.bean.component.template;

import static cn.bywin.business.bean.component.ComponentEnum.DATA_TYPE;

import cn.bywin.business.bean.federal.CheckComponent;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.common.util.JsonUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * @Description 数据交集组件模板
 * @Author wangh
 * @Date 2021-07-30
 */
@Data
public class TIntersectionComponent extends BaseComponenT {
    private final String intersect_method = "rsa";
    private final boolean sync_intersect_ids = true;
    private final boolean only_output_key = false;

    public TIntersectionComponent() {
    }


    @Override
    public Object init(FModelElementDo preModelElementDo) {

        TIntersectionComponent intersectionComponentT = new TIntersectionComponent();


        return JsonUtil.toSimpleJson(intersectionComponentT);
    }

    @Override
    public CheckComponent check(FModelElementDo fModelElementDo, FComponentDo lastComponent, String component) {

        if (!DATA_TYPE.equals(lastComponent.getTypes())) {
            return new CheckComponent(false, String.format("组件[%s]中发生了异常：组件前置配置不匹配(前置组件应为数据集组件或者特征工程组件)，请检查流程是否合理。", component));
        }
        return new CheckComponent(true, "success");
    }


    @Override
    public void build(Map<String, Object> module, Map<String, Object> model, Map<String, Integer> num,
                      Map<String, Object> common, String preComponent,
                      FModelElementDo fModelElementDo, FComponentDo componentDo) {


        Map<String, Object> output = new HashMap<>(5);
        Map<String, Object> input = new HashMap<>(5);
        ComponentDsl componentDsl = new ComponentDsl();
        Map<String, Object> data = new HashMap<>(5);

        data.put("data", Arrays.asList(preComponent + ".train"));
        output.put("data", Arrays.asList("train"));
        input.put("data", data);
        model.put(componentDo.getComponent().toLowerCase() + "_" + num.get(componentDo.getComponent().toLowerCase()), JsonUtil.toJsonObject(fModelElementDo.getConfig()));
        componentDsl.setInput(input);
        componentDsl.setModule(componentDo.getComponentEn());
        componentDsl.setOutput(output);
        module.put(componentDo.getComponent().toLowerCase() + "_" + num.get(componentDo.getComponent().toLowerCase()), componentDsl);
    }


}
