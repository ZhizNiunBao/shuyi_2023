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
 * @Description 纵向boost组件模板
 * @Author wangh
 * @Date 2021-07-30
 */
@Data
public class THeteroSecureBoostComponent extends BaseComponenT {


    private String task_type = "classification";
    private BigDecimal tol = new BigDecimal("0.0001");
    private int num_trees = 3;
    private boolean n_iter_no_change = false;
    private int subsample_feature_rate = 1;
    private int validation_freqs = 1;
    private int bin_num = 50;
    private BigDecimal learning_rate = new BigDecimal("0.1");
    @Data
    private static class TreeParam {
        private int max_depth = 3;
    }
    @Data
    private static class ObjectiveParam {
        private String objective = "cross_entropy";
    }

    @Data
    private static class Encryptparam {
        private String method = "Paillier";
    }

    @Data
    private static class PredictParam {
        private BigDecimal threshold = new BigDecimal("0.5");
    }
    @Data
    private static class CvParam {
        private int n_splits = 5;
        private int random_seed = 103;
        private boolean shuffle = false;
        private boolean need_cv = false;
    }

    private TreeParam tree_param = new TreeParam();
    private ObjectiveParam objective_param = new ObjectiveParam();
    private Encryptparam encrypt_param = new Encryptparam();
    private PredictParam predict_param = new PredictParam();
    private CvParam cv_param = new CvParam();

    public THeteroSecureBoostComponent() {

    }
    @Override
    public Object init(FModelElementDo preModelElementDo) {
        THeteroSecureBoostComponent heteroSecureBoostComponentT = new THeteroSecureBoostComponent();
        return JsonUtil.toSimpleJson(heteroSecureBoostComponentT);
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
