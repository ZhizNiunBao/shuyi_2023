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
import org.apache.commons.lang3.StringUtils;

/**
 * @Description 特征统计组件模板
 * @Author wangh
 * @Date 2021-07-30
 */
@Data
public class TDataStatisticsComponent extends BaseComponenT {


    private String[] statistics = {"95%", "coefficient_of_variance", "stddev"};
    private Object column_indexes = -1;
    private List<String> column_names = new ArrayList<>();

    public TDataStatisticsComponent() {
    }


    @Override
    public Object init(FModelElementDo preModelElementDo) {
        TDataStatisticsComponent dataStatisticsComponentT = new TDataStatisticsComponent();
        return JsonUtil.toSimpleJson(dataStatisticsComponentT);
    }


    @Override
    public void build(Map<String, Object> module, Map<String, Object> model, Map<String, Integer> num,
                      Map<String, Object> common, String preComponent,
                      FModelElementDo fModelElementDo, FComponentDo componentDo) {
        Map<String, Object> input = new HashMap<>(5);
        Map<String, Object> output = new HashMap<>(5);
        Map<String, Object> data = new HashMap<>(5);
        ComponentDsl componentDsl = new ComponentDsl();
        data.put("data", Arrays.asList(preComponent + ".train"));
        output.put("data", Arrays.asList("train"));
        input.put("data", data);
        output.put("model", Arrays.asList(componentDo.getComponent().toLowerCase()));
        TDataStatisticsComponent aggregationComponent = JsonUtil.gson().fromJson(JsonUtil.toJson(JsonUtil.gson().fromJson(fModelElementDo.getConfig(), Map.class)), TDataStatisticsComponent.class);
        List<Integer> sum_cols = new ArrayList<>();
        if (StringUtils.isNotBlank(fModelElementDo.getData())) {
            String[] split = fModelElementDo.getData().split(",");
            List<String> splitList = Arrays.asList(split);
            for (String cls : aggregationComponent.getColumn_names()) {
                int indexOf = splitList.indexOf(cls);
                if (indexOf >= 0) {
                    sum_cols.add(indexOf);
                }
            }
        }

        aggregationComponent.setColumn_names(aggregationComponent.getColumn_names());
        aggregationComponent.setColumn_indexes(aggregationComponent.getColumn_names().size() == 0 ? -1 : new ArrayList<>());
        model.put(componentDo.getComponent().toLowerCase() + "_" + num.get(componentDo.getComponent().toLowerCase()), JsonUtil.toJsonObject(JsonUtil.toSimpleJson(aggregationComponent)));
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
        return new CheckComponent(true, "success");
    }

}
