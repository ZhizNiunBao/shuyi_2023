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
 * @Description 纵向nn组件模板
 * @Author wangh
 * @Date 2022-03-22
 */
@Data
public class THeteroNNComponent extends BaseComponenT {
    private int epochs = 1000;
    private BigDecimal interactive_layer_lr = new BigDecimal("0.15");
    private int batch_size = -1;
    private String early_stop = "diff";
    private String loss = "binary_crossentropy";
    private String config_type = "keras";

    @Data
    private static class Optimizer {
        private String optimizer = "Adam";
        private BigDecimal decay = new BigDecimal("0.0");
        private BigDecimal epsilon = new BigDecimal("1e-7");
        private boolean amsgrad = false;
        private BigDecimal learning_rate = new BigDecimal("0.15");
    }

    @Data
    private static class KernelInitializer {
        private String class_name = "Constant";
        private Map<String,Object> config=new HashMap<>();
        public KernelInitializer(){
            config.put("dtype","float32");
            config.put("value",1);
        }
    }

    @Data
    private static class BiasInitializer {
        private String class_name = "Zeros";
        private Map<String,Object> config=new HashMap<>();
        public BiasInitializer(){
            config.put("dtype","float32");
        }
    }

    @Data
    private static class BottomNnDefine {
        private String class_name = "Sequential";
        private String keras_version = "2.2.4-tf";
        private String backend = "tensorflow";
        private Map<String, Object> config = new HashMap<>();
    }

    @Data
    private static class TopNnDefine {
        private String class_name = "Sequential";
        private String keras_version = "2.2.4-tf";
        private String backend = "tensorflow";
        private Map<String, Object> config = new HashMap<>();
    }

    @Data
    private static class InteractiveLayerDefine {
        private String class_name = "Sequential";
        private String keras_version = "2.2.4-tf";
        private String backend = "tensorflow";
        private Map<String, Object> config = new HashMap<>();
    }

    @Data
    private static class Config {
        private String name = "dense_1";
        private Integer[] batch_input_shape = new Integer[]{null, 2};
        private String dtype = "float32";
        private Integer units = 2;
        private String activation = "linear";
        private boolean use_bias = true;
        private boolean trainable = true;
        private KernelInitializer kernel_initializer = new KernelInitializer();
        private BiasInitializer bias_initializer = new BiasInitializer();
        private String activity_regularizer = null;
        private String bias_regularizer = null;
        private String kernel_constraint = null;
        private String bias_constraint = null;
    }

    private Optimizer optimizer = new Optimizer();
    private BottomNnDefine bottom_nn_define = new BottomNnDefine();
    private TopNnDefine top_nn_define = new TopNnDefine();
    private InteractiveLayerDefine interactive_layer_define = new InteractiveLayerDefine();

    public THeteroNNComponent() {
    }

    @Override
    public Object init(FModelElementDo preModelElementDo) {
        THeteroNNComponent heteroLrComponentT = new THeteroNNComponent();
        BottomNnDefine bottom_nn_define = new BottomNnDefine();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "sequential");
        Config config1 = new Config();
        config1.setActivation("relu");
        config1.setUnits(3);
        config1.setName("dense");
        Map<String, Object> layers1 = new HashMap<>();
        layers1.put("class_name", "Dense");
        layers1.put("config", config1);
        map1.put("layers", new Object[]{layers1});
        bottom_nn_define.setConfig(map1);
        Map<String, Object> map2 = new HashMap<>();
        TopNnDefine top_nn_define = new TopNnDefine();
        map2.put("name", "sequential_2");
        Config config2 = new Config();
        config2.setName("dense_2");
        config2.setUnits(1);
        config2.setActivation("sigmoid");
        Map<String, Object> layers2 = new HashMap<>();
        layers2.put("class_name", "Dense");
        layers2.put("config", config2);
        map2.put("layers", new Object[]{layers2});
        top_nn_define.setConfig(map2);
        Map<String, Object> map3 = new HashMap<>();
        InteractiveLayerDefine interactive_layer_define = new InteractiveLayerDefine();
        map3.put("name", "sequential_1");
        Config config3 = new Config();
        config3.setName("dense_1");
        config3.setActivation("linear");
        config3.setUnits(2);
        Map<String, Object> layers3 = new HashMap<>();
        layers3.put("class_name", "Dense");
        layers3.put("config", config3);
        map3.put("layers", new Object[]{layers3});
        interactive_layer_define.setConfig(map3);
        heteroLrComponentT.setBottom_nn_define(bottom_nn_define);
        heteroLrComponentT.setTop_nn_define(top_nn_define);
        heteroLrComponentT.setInteractive_layer_define(interactive_layer_define);
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