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
 * @Description Poisson组件模板
 * @Author wangh
 * @Date 2021-07-30
 */
@Data
public class THeteroPoissonComponent extends BaseComponenT {
    private String penalty = "L2";
    private String optimizer = "nesterov_momentum_sgd";
    private BigDecimal tol = new BigDecimal("0.0001");
    private BigDecimal alpha = new BigDecimal("0.01");
    private int max_iter = 10;
    private String early_stop = "weight_diff";
    private int batch_size = -1;
    @Data
    private static class InitMethod {
        private String init_method = "zeros";
    }

    private BigDecimal learning_rate = new BigDecimal("0.15");

    @Data
    private static class CvParam {
        private int n_splits = 3;
        private int random_seed = 103;
        private boolean shuffle = false;
        private boolean need_cv = false;
    }

    @Data
    private static class EncryptParam {
        private String method = "Paillier";
    }
    private CvParam cv_param = new CvParam();
    private InitMethod init_param = new InitMethod();
    private EncryptParam encrypt_param = new EncryptParam();

    public THeteroPoissonComponent() {

    }

    @Override
    public Object init(FModelElementDo preModelElementDo) {
        THeteroPoissonComponent heteroLrComponentT = new THeteroPoissonComponent();
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
