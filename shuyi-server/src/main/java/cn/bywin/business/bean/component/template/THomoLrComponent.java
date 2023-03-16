package cn.bywin.business.bean.component.template;

import static cn.bywin.business.bean.component.ComponentEnum.DATA_TYPE;
import static cn.bywin.business.bean.component.ComponentEnum.TRANSFROM_TYPE;

import cn.bywin.business.bean.federal.CheckComponent;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.common.util.JsonUtil;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

/**
 * @Description 横向逻辑组件模板
 * @Author wangh
 * @Date 2021-07-30
 */
@Data
public class THomoLrComponent extends BaseComponenT {
    private String penalty = "L2";
    private String optimizer = "sgd";
    private BigDecimal tol = new BigDecimal("0.00001");
    private BigDecimal alpha = new BigDecimal("0.01");
    private int max_iter = 30;
    private String early_stop = "weight_diff";
    private int batch_size = -1;
    private BigDecimal decay = new BigDecimal("1.0");
    private boolean decay_sqrt = true;
    private BigDecimal learning_rate = new BigDecimal("0.15");

    @Data
    private static class InitMethod {
        private String init_method = "zeros";
    }

    @Data
    private static class CvParam {
        private int n_splits = 5;
        private int random_seed = 55;
        private boolean shuffle = true;
        private boolean need_cv = false;
    }

    @Data
    private static class EncryptParam {
        private String method = null;
    }

    private InitMethod init_param = new InitMethod();
    private CvParam cv_param = new CvParam();
    private EncryptParam encrypt_param = new EncryptParam();

    public THomoLrComponent() {
    }

    @Override
    public Object init(FModelElementDo preModelElementDo) {
        THomoLrComponent heteroLrComponentT = new THomoLrComponent();
        return JsonUtil.toSimpleJson(heteroLrComponentT);
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

        super.build(module,model,num,common,preComponent,fModelElementDo,componentDo);

    }

}