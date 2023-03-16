package cn.bywin.business.bean.component.template;

import static cn.bywin.business.bean.component.ComponentEnum.DATA_TYPE;
import static cn.bywin.business.bean.component.ComponentEnum.TRANSFROM_TYPE;

import cn.bywin.business.bean.federal.CheckComponent;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.common.util.JsonUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @Description Psi组件模板
 * @Author wangh
 * @Date 2021-07-30
 */
@Data
public class TPsiComponent extends BaseComponenT {


    private Integer max_bin_num = 20;

    public TPsiComponent() {
    }


    @Override
    public Object init(FModelElementDo preModelElementDo) {
        TPsiComponent dataStatisticsComponentT = new TPsiComponent();
        return JsonUtil.toSimpleJson(dataStatisticsComponentT);
    }

    @Override
    public void build(Map<String, Object> module, Map<String, Object> model, Map<String, Integer> num,
                      Map<String, Object> common, String preComponent,
                      FModelElementDo fModelElementDo, FComponentDo componentDo) {
        List<String> inputs = new ArrayList<>();
        Object reader_0 = module.get("reader_0");
        Object data_transform_0 = module.get("data_transform_0");
        inputs.add("data_transform_0.train");
        Map reader = JsonUtil.gson().fromJson(JsonUtil.toJson(reader_0), Map.class);
        if (getData() != null && getData().size() > 0) {
            for (int i = 1; i < getData().size(); i++) {
                Map data_transform = JsonUtil.gson().fromJson(JsonUtil.toJson(data_transform_0).replace("reader_0", "reader_" + i), Map.class);
                module.put("reader_" + i, reader);
                module.put("data_transform_" + i, data_transform);
            }
        }
        Map<String, Object> input = new HashMap<>(5);
        Map<String, Object> output = new HashMap<>(5);
        Map<String, Object> data = new HashMap<>(5);
        ComponentDsl componentDsl = new ComponentDsl();
        data.put("train_data", Arrays.asList("data_transform_0.train"));
        data.put("validate_data", Arrays.asList("data_transform_1.train"));
        output.put("data", Arrays.asList("train"));
        input.put("data", data);
        output.put("model", Arrays.asList("model"));
        componentDsl.setInput(input);
        componentDsl.setOutput(output);
        componentDsl.setModule(componentDo.getComponentEn());
        module.put(componentDo.getComponent().toLowerCase() + "_" + num.get(componentDo.getComponent().toLowerCase()), componentDsl);


}

    @Override
    public CheckComponent check(FModelElementDo fModelElementDo, FComponentDo lastComponent, String component) {

        if (!DATA_TYPE.equals(lastComponent.getTypes()) && !TRANSFROM_TYPE.equals(lastComponent.getTypes())) {
            return new CheckComponent(false, String.format("组件[%s]中发生了异常：组件前置配置不匹配(前置组件应为数据集组件或者特征工程组件)，请检查流程是否合理。", component));
        }
        if (getData() == null || getData().size() != 2) {
            return new CheckComponent(false, String.format("组件[%s]中发生了异常：前置数据集组件选择数据应该为2个。", component));
        }
        return new CheckComponent(true, "success");
    }

}
