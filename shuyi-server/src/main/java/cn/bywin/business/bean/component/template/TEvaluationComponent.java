package cn.bywin.business.bean.component.template;

import static cn.bywin.business.bean.component.ComponentEnum.ALGO_TYPE;

import cn.bywin.business.bean.federal.CheckComponent;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.common.util.JsonUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;


/**
 * @Description 预测组件模板
 * @Author wangh
 * @Date 2021-07-30
 */
@Data
public class TEvaluationComponent extends BaseComponenT {


    private String eval_type = "binary";
    private Integer pos_label = 1;

    public TEvaluationComponent() {
    }

    @Override
    public Object init(FModelElementDo preModelElementDo) {

        TEvaluationComponent evaluationComponentT = new TEvaluationComponent();

        return JsonUtil.toSimpleJson(evaluationComponentT);
    }

    @Override
    public void build(Map<String, Object> module, Map<String, Object> model, Map<String, Integer> num,
                      Map<String, Object> common, String preComponent,
                      FModelElementDo fModelElementDo, FComponentDo componentDo) {
        Map<String, Object> input = new HashMap<>(5);
        ComponentDsl componentDsl = new ComponentDsl();
        Map<String, Object> data = new HashMap<>(5);
        input.put("data", data);
        data.put("data", Arrays.asList(preComponent + ".train"));
        model.put(componentDo.getComponent().toLowerCase()  + "_"+num.get(componentDo.getComponent().toLowerCase()), JsonUtil.toJsonObject(fModelElementDo.getConfig()));

        module.put(componentDo.getComponent().toLowerCase() + "_" + num.get(componentDo.getComponent().toLowerCase()), componentDsl);
        componentDsl.setInput(input);
        componentDsl.setModule(componentDo.getComponentEn());
    }

    @Override
    public CheckComponent check(FModelElementDo fModelElementDo, FComponentDo lastComponent, String component) {

        if (!ALGO_TYPE.equals(lastComponent.getTypes())) {
            return new CheckComponent(false, String.format("组件[%s]中发生了异常：组件前置配置不匹配(前置组件应为算法组件)，请检查流程是否合理。", component));
        }

        return new CheckComponent(true, "success");
    }
}
