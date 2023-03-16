package cn.bywin.business.bean.component.template;

import static cn.bywin.business.bean.component.ComponentEnum.ALGO_TYPE;
import static cn.bywin.business.bean.component.ComponentEnum.DATA_TYPE;
import static cn.bywin.business.bean.component.ComponentEnum.Data_Split;
import static cn.bywin.business.bean.component.ComponentEnum.TRANSFROM_TYPE;

import cn.bywin.business.bean.federal.CheckComponent;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.common.util.JsonUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;


/**
 * @Description 纵向Kmeans组件模板
 * @Author wangh
 * @Date 2021-09-22
 */
@Data
public class THeteroKmeansComponent extends BaseComponenT {


    private Integer k = 2;
    private Integer max_iter = 10;
    public THeteroKmeansComponent(){}

    @Override
    public Object init(FModelElementDo preModelElementDo) {

        THeteroKmeansComponent heteroKmeansComponentT=new THeteroKmeansComponent();
        return JsonUtil.toSimpleJson(heteroKmeansComponentT);
    }
    @Override
    public CheckComponent check(FModelElementDo fModelElementDo, FComponentDo lastComponent,String component) {

        if (!DATA_TYPE.equals(lastComponent.getTypes()) && !TRANSFROM_TYPE.equals(lastComponent.getTypes())) {
            return new CheckComponent(false, String.format("组件[%s]中发生了异常：组件前置配置不匹配(前置组件应为数据集组件或者特征工程组件)，请检查流程是否合理。", component));
        }
        return new CheckComponent(true,"success");
    }


    @Override
    public void build(Map<String, Object> module, Map<String, Object> model, Map<String, Integer> num,
                      Map<String, Object> common, String preComponent,
                      FModelElementDo fModelElementDo, FComponentDo componentDo) {
        Map<String, Object> input = new HashMap<>(5);
        Map<String, Object> output = new HashMap<>(5);
        Map<String, Object> data = new HashMap<>(5);
        ComponentDsl componentDsl = new ComponentDsl();
        if (ALGO_TYPE.equals(componentDo.getTypes())){
            data.put("train_data", Arrays.asList(preComponent+".train"));
            if (preComponent.toLowerCase().contains(Data_Split)){
                data.put("validate_data", Arrays.asList(preComponent+".validate"));
            }
        }else {
            data.put("data", Arrays.asList(preComponent+".train"));
        }
        output.put("data", new String []{"data","train"});
        //判断是否是分割组件
        if (componentDo.getComponentEn().toLowerCase().endsWith(Data_Split)){
            output.put("data",new String[]{"train", "validate","test"});
        }
        input.put("data", data);
        output.put("model", Arrays.asList("model"));

        model.put(componentDo.getComponent().toLowerCase()  + "_"+num.get(componentDo.getComponent().toLowerCase()), JsonUtil.toJsonObject(fModelElementDo.getConfig()));
        componentDsl.setInput(input);
        componentDsl.setOutput(output);
        componentDsl.setModule(componentDo.getComponentEn());
        module.put(componentDo.getComponent().toLowerCase() + "_"+num.get(componentDo.getComponent().toLowerCase()), componentDsl);

    }


}
