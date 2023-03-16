package cn.bywin.business.bean.component.template;

import static cn.bywin.business.bean.component.ComponentEnum.DATA_TYPE;
import static cn.bywin.business.bean.component.ComponentEnum.TRANSFROM_TYPE;

import cn.bywin.business.bean.federal.CheckComponent;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.common.util.JsonUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @Description 横向nn组件模板
 * @Author wangh
 * @Date 2022-03-01
 */
@Data
public class THomoNNComponent extends BaseComponenT {
    private String config_type = "pytorch";
    private String [] metrics = {"accuracy"};
    private int max_iter =1000;
    private String loss = "CrossEntropyLoss";
    private int batch_size = -1;



    @Data
    private static class Optimizer {
        private String optimizer = "Adam";
        private BigDecimal lr = new BigDecimal("0.05");
    }

    @Data
    private static class EarlyStop {
        private String early_stop = "diff";
        private BigDecimal eps = new BigDecimal("0.00001");
    }

    private List<Map<String,Object>> nn_define = new ArrayList<>();
    private Optimizer optimizer = new Optimizer();
    private EarlyStop early_stop = new EarlyStop();

    public THomoNNComponent() {
    }

    @Override
    public Object init(FModelElementDo preModelElementDo) {
        THomoNNComponent heteroLrComponentT = new THomoNNComponent();
        Map<String,Object> map1=new HashMap<>();
        Map<String,Object> map2=new HashMap<>();
        Map<String,Object> map3=new HashMap<>();
        map1.put("layer","Linear");
        map1.put("name","line1");
        map1.put("type","normal");
        map1.put("config",new Integer []{18,5});
        map2.put("layer","Relu");
        map2.put("type","activate");
        map2.put("name","relu");
        map3.put("layer","Linear");
        map3.put("name","line2");
        map3.put("type","normal");
        map3.put("config",new Integer []{5,4});
        nn_define.add(map1);
        nn_define.add(map2);
        nn_define.add(map3);
        heteroLrComponentT.setNn_define(nn_define);
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