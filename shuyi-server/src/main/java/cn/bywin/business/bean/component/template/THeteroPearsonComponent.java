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
 * @Description  Pearson组件模板
 * @Author wangh
 * @Date 2021-07-30
 */
@Data
public class THeteroPearsonComponent extends BaseComponenT {



    private Integer  column_indexes =-1;
    public THeteroPearsonComponent(){}


    @Override
    public Object init(FModelElementDo preModelElementDo) {
        THeteroPearsonComponent dataStatisticsComponentT=new THeteroPearsonComponent();
        return JsonUtil.toSimpleJson(dataStatisticsComponentT);
    }

    @Override
    public void build(Map<String, Object> module, Map<String, Object> model, Map<String, Integer> num,
                      Map<String, Object> common, String preComponent,
                      FModelElementDo fModelElementDo, FComponentDo componentDo) {

        super.build(module,model,num,common,preComponent,fModelElementDo,componentDo);

    }
    @Override
    public CheckComponent check(FModelElementDo fModelElementDo, FComponentDo lastComponent,String component) {

        if (!DATA_TYPE.equals(lastComponent.getTypes())&&!TRANSFROM_TYPE.equals(lastComponent.getTypes())){
            return new CheckComponent(false, String.format("组件[%s]中发生了异常：组件前置配置不匹配(前置组件应为数据集组件或者特征工程组件)，请检查流程是否合理。", component));
        }
        return new CheckComponent(true,"success");
    }

}
