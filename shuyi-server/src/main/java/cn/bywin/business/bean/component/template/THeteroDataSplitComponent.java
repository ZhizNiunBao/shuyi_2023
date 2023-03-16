package cn.bywin.business.bean.component.template;

import static cn.bywin.business.bean.component.ComponentEnum.DATA_TYPE;
import static cn.bywin.business.bean.component.ComponentEnum.TRANSFROM_TYPE;

import cn.bywin.business.bean.federal.CheckComponent;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.common.util.JsonUtil;
import java.util.Map;
import lombok.Data;

/**
 * @Description 数据分割组件模板
 * @Author wangh
 * @Date 2021-07-30
 */
@Data
public class THeteroDataSplitComponent extends BaseComponenT {


    private double validate_size = 0.3;
    private boolean stratified = true;

    public THeteroDataSplitComponent() {
    }


    @Override
    public Object init(FModelElementDo preModelElementDo) {

        THeteroDataSplitComponent heteroDataSplitComponentT = new THeteroDataSplitComponent();


        return JsonUtil.toSimpleJson(heteroDataSplitComponentT);
    }

    @Override
    public void build(Map<String, Object> module, Map<String, Object> model, Map<String, Integer> num,
                      Map<String, Object> common, String preComponent,
                      FModelElementDo fModelElementDo, FComponentDo componentDo) {

        super.build(module,model,num,common,preComponent,fModelElementDo,componentDo);

    }
    @Override
    public CheckComponent check(FModelElementDo fModelElementDo, FComponentDo lastComponent, String component) {

        if (!DATA_TYPE.equals(lastComponent.getTypes())&&!TRANSFROM_TYPE.equals(lastComponent.getTypes())) {
            return new CheckComponent(false, String.format("组件[%s]中发生了异常：组件前置配置不匹配(前置组件应为数据集组件或者特征工程组件)，请检查流程是否合理。", component));
        }
        return new CheckComponent(true, "success");
    }


}
