package cn.bywin.business.bean.component.template;

import static cn.bywin.business.bean.component.ComponentEnum.DATA_TYPE;
import static cn.bywin.business.bean.component.ComponentEnum.TRANSFROM_TYPE;

import cn.bywin.business.bean.federal.CheckComponent;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.common.util.JsonUtil;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;


/**
 * @Description 纵向迁移学习组件模板
 * @Author wangh
 * @Date 2023-01-12
 */
@Data
public class THeteroFtlComponent extends BaseComponenT {
    private int epochs = 10;
    private BigDecimal interactive_layer_lr = new BigDecimal("0.15");
    private int batch_size = -1;
    private int alpha = 1;
    private String mode = "plain";
    private String config_type = "keras";

    @Data
    private static class Optimizer {
        private String optimizer = "Adam";
        private BigDecimal decay = new BigDecimal("0.0");
        private BigDecimal beta_1 = new BigDecimal("0.9");
        private BigDecimal beta_2 = new BigDecimal("0.999");
        private BigDecimal epsilon = new BigDecimal("1e-7");
        private boolean amsgrad = false;
        private BigDecimal learning_rate = new BigDecimal("0.01");
    }

    @Data
    private static class NnDefine {
        private String class_name = "Sequential";
        private String keras_version = "2.2.4-tf";
        private String backend = "tensorflow";
        private Map<String, Object> config = new HashMap<>();
    }

    @Data
    private static class KernelInitializer {
        private String class_name = "RandomNormal";
        private Map<String, Object> config = new HashMap<>();

        public KernelInitializer() {
            config.put("mean", new BigDecimal("0.0"));
            config.put("stddev", new BigDecimal("1.0"));
            config.put("dtype", "float32");
            config.put("seed", 1);
        }
    }

    @Data
    private static class BiasInitializer {
        private String class_name = "Zeros";
        private Map<String, Object> config = new HashMap<>();

        public BiasInitializer() {
            config.put("dtype", "float32");
        }
    }

    @Data
    private static class Config {
        private String name = "dense";
        private String dtype =null;
        private Integer units = 32;
        private String activation = "sigmoid";
        private boolean use_bias = true;
        private boolean trainable = true;
        private KernelInitializer kernel_initializer = new KernelInitializer();
        private BiasInitializer bias_initializer = new BiasInitializer();
        private String kernel_regularizer = null;
        private String bias_regularizer = null;
        private String activity_regularizer = null;
        private String kernel_constraint = null;
        private String bias_constraint = null;
    }

    public THeteroFtlComponent() {
    }

    @Override
    public Object init(FModelElementDo preModelElementDo) {
        THeteroFtlComponent heteroLrComponentT = new THeteroFtlComponent();
        NnDefine bottom_nn_define = new NnDefine();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "sequential");
        Config config1 = new Config();
        config1.setActivation("sigmoid");
        config1.setName("dense");
        Map<String, Object> layers1 = new HashMap<>();
        layers1.put("class_name", "Dense");
        layers1.put("config", config1);
        map.put("layers", new Object[]{layers1});
        bottom_nn_define.setConfig(map);
        return JsonUtil.toSimpleJson(heteroLrComponentT);
    }

    @Override
    public CheckComponent check(FModelElementDo fModelElementDo, FComponentDo lastComponent, String component) {
        if (!DATA_TYPE.equals(lastComponent.getTypes()) && !TRANSFROM_TYPE.equals(lastComponent.getTypes())) {
            return new CheckComponent(false, String.format("组件[%s]中发生了异常：组件前置配置不匹配(前置组件应为数据集组件或者特征工程组件)，请检查流程是否合理。", component));
        }
        return new CheckComponent(true, "success");
    }

    @Override
    public void build(Map<String, Object> module, Map<String, Object> model, Map<String, Integer> num,
                      Map<String, Object> common, String preComponent,
                      FModelElementDo fModelElementDo, FComponentDo componentDo) {
        super.build(module, model, num, common, preComponent, fModelElementDo, componentDo);

    }

}