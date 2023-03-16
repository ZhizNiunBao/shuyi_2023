package cn.bywin.business.bean.component.template;

import static cn.bywin.business.bean.component.ComponentEnum.HomoFeatureBinning_COMPONENT;

import cn.bywin.business.bean.federal.CheckComponent;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.common.util.JsonUtil;
import java.util.Map;
import lombok.Data;


/**
 * @Description one_hot组件模板
 * @Author wangh
 * @Date 2021-07-30
 */
@Data
public class THomoOneHotEncoderComponent extends BaseComponenT {


    private String transform_col_names = null;
    private Integer transform_col_indexes = -1;
    private boolean need_run =true;

    public THomoOneHotEncoderComponent(){}

    @Override
    public Object init(FModelElementDo preModelElementDo) {

        THomoOneHotEncoderComponent oneHotEncoderComponentT=new THomoOneHotEncoderComponent();

        return JsonUtil.toSimpleJson(oneHotEncoderComponentT);
    }
    @Override
    public CheckComponent check(FModelElementDo fModelElementDo, FComponentDo lastComponent,String component) {

        if (!lastComponent.getComponentEn().toLowerCase().endsWith(HomoFeatureBinning_COMPONENT.getComponentName())){
            return new CheckComponent(false, String.format("组件[%s]中发生了异常：组件前置配置不匹配(前置组件应为分箱编码组件)，请检查流程是否合理。", component));

        }
        return new CheckComponent(true,"success");
    }

    @Override
    public void build(Map<String, Object> module, Map<String, Object> model, Map<String, Integer> num,
                      Map<String, Object> common, String preComponent,
                      FModelElementDo fModelElementDo, FComponentDo componentDo) {

        super.build(module,model,num,common,preComponent,fModelElementDo,componentDo);

    }

}
