package cn.bywin.business.bean.component.template;

import static cn.bywin.business.bean.component.ComponentEnum.ALGO_TYPE;
import static cn.bywin.business.bean.component.ComponentEnum.Data_Split;

import cn.bywin.business.bean.federal.CheckComponent;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FDataPartyDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.federal.FProjectGuestDo;
import cn.bywin.business.common.util.JsonUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 组件模板
 * @Author wangh
 * @Date 2021-07-30
 */
public abstract class BaseComponenT {


    /**
     * @Description 表数据
     * @Author wangh
     * @Date 2021-10-20
     */
    protected List<FDataPartyDo> data;

    public List<FDataPartyDo> getData() {
        return data;
    }

    public void setData(List<FDataPartyDo> data) {
        this.data = data;
    }

    protected List<FProjectGuestDo> guest;

    public List<FProjectGuestDo> getGuest() {
        return guest;
    }

    public void setGuest(List<FProjectGuestDo> guest) {
        this.guest = guest;
    }
    protected FNodePartyDo host;

    public FNodePartyDo getHost() {
        return host;
    }

    public void setHost(FNodePartyDo host) {
        this.host = host;
    }
    /**
     * 件初始组化配置
     *
     * @return 返回初始化配置
     * @Description
     * @Author wangh
     * @Date 2021-07-30
     */

    /**
     * 件初始组化配置
     *
     * @return 返回初始化配置
     * @DescriptionFModelElementDo fModelElementDo
     * @Author wangh
     * @Date 2021-07-30
     */
    public abstract Object init(FModelElementDo preModelElementDo);


    /**
     * @Description 组件初始化
     * @Author wangh
     * @Date 2021-07-30
     */
    public void build(Map<String, Object> module, Map<String, Object> model, Map<String, Integer> num, Map<String, Object> common
            , String preComponent, FModelElementDo fModelElementDo, FComponentDo componentDo) {
        Map<String, Object> input = new HashMap<>(5);
        Map<String, Object> output = new HashMap<>(5);
        Map<String, Object> data = new HashMap<>(5);
        ComponentDsl componentDsl = new ComponentDsl();
        if (ALGO_TYPE.equals(componentDo.getTypes())) {
            data.put("train_data", Arrays.asList(preComponent + ".train"));
            if (preComponent.toLowerCase().contains(Data_Split)) {
                data.put("validate_data", Arrays.asList(preComponent + ".validate"));
            }
        } else {
            data.put("data", Arrays.asList(preComponent + ".train"));
        }
        output.put("data", Arrays.asList("train"));
        //判断是否是分割组件
        if (componentDo.getComponentEn().toLowerCase().endsWith(Data_Split)) {
            output.put("data", new String[]{"train", "validate", "test"});
        }
        input.put("data", data);
        output.put("model", Arrays.asList("model"));
        model.put(componentDo.getComponent().toLowerCase() + "_" + num.get(componentDo.getComponent().toLowerCase()), JsonUtil.toJsonObject(fModelElementDo.getConfig()));
        componentDsl.setInput(input);
        componentDsl.setOutput(output);
        componentDsl.setModule(componentDo.getComponentEn());
        module.put(componentDo.getComponent().toLowerCase() + "_" + num.get(componentDo.getComponent().toLowerCase()), componentDsl);

    }

    /**
     * @Description 组件校验
     * @Author wangh
     * @Date 2021-09-30
     */
    public abstract CheckComponent check(FModelElementDo fModelElementDo, FComponentDo componentDo, String component);
}
