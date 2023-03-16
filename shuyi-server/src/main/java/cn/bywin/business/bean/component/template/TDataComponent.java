package cn.bywin.business.bean.component.template;

import cn.bywin.business.bean.federal.CheckComponent;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FDataPartyDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.common.util.JsonUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;

/**
 * @Description 数据集组件模板
 * @Author wangh
 * @Date 2021-07-30
 */
@Data
public class TDataComponent extends BaseComponenT {


    private Map<String, Object> args = new HashMap<>();

    @Data
    private static class Table {
        private String name;
        private String namespace;
    }

    public TDataComponent() {
    }

    public TDataComponent put(String key, Object value) {
        args.put(key, value);
        return this;
    }

    public Map<String, Object> getResultMap() {
        return args;
    }

    @Override
    public Object init(FModelElementDo preModelElementDo) {
        return null;
    }

    @Override
    public void build(Map<String, Object> module, Map<String, Object> model, Map<String, Integer> num,
                      Map<String, Object> common, String preComponent,
                      FModelElementDo fModelElementDo, FComponentDo componentDo) {
       // ComponentDsl componentDsl = new ComponentDsl();
        Map<String, Object> componentDsl = new HashMap<>();
        Map<String, Object> input = new HashMap<>();
        Map<String, Object> output = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        String reader = buildReader(module, model, num);
        data.put("data", Arrays.asList(reader + ".data"));
        input.put("data", data);
        output.put("data", Arrays.asList("train"));
        output.put("model", Arrays.asList("model"));
        common.put("role", JsonUtil.toJsonObject(fModelElementDo.getConfig()));
        module.put(componentDo.getComponent().toLowerCase()
                + "_" + num.get(componentDo.getComponent().toLowerCase()), componentDsl);
        Map<String, Object> dataio = new HashMap<>();
        dataio.put("output_format", "dense");
        dataio.put("missing_fill", true);
        dataio.put("missing_fill_method", "mean");
        model.put(componentDo.getComponent().toLowerCase() + "_" + num.get(componentDo.getComponent().toLowerCase()),
                dataio);
        componentDsl.put("input",input);
        componentDsl.put("output",output);
        componentDsl.put("module",componentDo.getComponentEn());
    }

    public String buildReader(Map<String, Object> module, Map<String, Object> model, Map<String, Integer> num) {
        if (num.containsKey("reader")) {
            num.put("reader", num.get("reader") + 1);
        } else {
            num.put("reader", 0);
        }
        Map<String, Object> output = new HashMap<>();
        output.put("data", Arrays.asList("data"));
        Map<String, Object> componentDsl = new HashMap<>();
        componentDsl.put("output",output);
        componentDsl.put("module","Reader");
        module.put("reader_" + num.get("reader"), componentDsl);
        return "reader_" + num.get("reader");
    }

    @Override
    public CheckComponent check(FModelElementDo fModelElementDo, FComponentDo componentDo, String component) {

        return new CheckComponent(true, "success");
    }


    public static Object init(List<FDataPartyDo> fDataPartyDo, boolean isGuest) {

        TDataComponent dataComponentT = new TDataComponent();
        Map<String, List<FDataPartyDo>> subListMap = fDataPartyDo.stream().filter(x -> x.getNodeId() != null).collect(Collectors.groupingBy(FDataPartyDo::getNodeId));
        int num = 0;
        for (String key : subListMap.keySet()) {
            Map<String, Object> role = new HashMap<>();
            for (int i = 0; i < subListMap.get(key).size(); i++) {
                Map<String, Object> reder = new HashMap<>();
                Map<String, Object> data = new HashMap<>();
                int index = i;
                Table table = new Table();
                table.setName( subListMap.get(key).get(i).getTableName());
                table.setNamespace( subListMap.get(key).get(i).getNamespace());
                reder.put("table", table);
                data.put("with_label",  subListMap.get(key).get(i).getWithLabel() == 1 ? true : false);
                role.put("reader_" + index, reder);
                role.put("data_transform_" + index, data);
                //1为 数据有标签 isGuest 是否为发起者
                if ( subListMap.get(key).get(index).getWithLabel() == 1 && isGuest) {
                } else {
                    Map<String, Object> evaluation = new HashMap<>();
                    evaluation.put("need_run", false);
                    role.put("evaluation_" + index, evaluation);
                }
            }
            dataComponentT.put(String.valueOf(num), role);
            num++;
        }
        return dataComponentT.getResultMap();
    }


}
