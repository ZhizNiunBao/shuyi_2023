package cn.bywin.business.bean.component.template;

import static cn.bywin.business.bean.component.ComponentEnum.Feature_Binning;

import cn.bywin.business.bean.federal.CheckComponent;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.common.util.JsonUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description 特征选择组件模板
 * @Author wangh
 * @Date 2021-07-30
 */
@Data
public class THeteroFeatureSelectionComponent extends BaseComponenT {

    private Object select_col_indexes = -1;
    private String[] filter_methods = new String[]{
            "manually"
            ,
            "unique_value",
            "iv_value_thres",
            "coefficient_of_variation_value_thres",
            "iv_percentile",
            "outlier_cols"
    };
    private List<String> select_names = new ArrayList<>();
//
//    @Data
//    private class SptParam {
//        private BigDecimal threshold = new BigDecimal("0.00001");
//        private String metrics = "feature_importance";
//        private String filter_type = "threshold";
//        private Boolean take_high = true;
//    }
//
//    private SptParam sbt_param = new SptParam();

    @Data
    private class ManuallyParam {
        private List<String> filter_out_names = new ArrayList<>();
        private List<Integer> filter_out_indexes = new ArrayList<>();
        private List<String> left_col_names = new ArrayList<>();
        private List<Integer> left_col_indexes = new ArrayList<>();
    }

    @Data
    private class UniqueParam {
        private BigDecimal eps = new BigDecimal("0.00001");
    }

    @Data
    private class IvValueParam {
        private BigDecimal value_threshold = new BigDecimal("0.1");
    }

    @Data
    private class IvPercentileParam {
        private BigDecimal percentile_threshold = new BigDecimal("0.9");
    }

    @Data
    private class VarianceCoeParam {
        private BigDecimal value_threshold = new BigDecimal("0.3");
    }

    @Data
    private class OutlierParam {
        private BigDecimal percentile = new BigDecimal("0.95");
        private BigDecimal upper_threshold = new BigDecimal("2.0");
    }

    private ManuallyParam manually_param = new ManuallyParam();
    private OutlierParam outlier_param = new OutlierParam();
    private VarianceCoeParam variance_coe_param = new VarianceCoeParam();
    private IvPercentileParam iv_percentile_param = new IvPercentileParam();
    private IvValueParam iv_value_param = new IvValueParam();
    private UniqueParam unique_param = new UniqueParam();

    public THeteroFeatureSelectionComponent() {
    }

    @Override
    public Object init(FModelElementDo preModelElementDo) {

        THeteroFeatureSelectionComponent fillMissingComponentT = new THeteroFeatureSelectionComponent();
        return JsonUtil.toSimpleJson(fillMissingComponentT);
    }

    @Override
    public CheckComponent check(FModelElementDo fModelElementDo, FComponentDo lastComponent, String component) {
//        if (!DATA_TYPE.equals(lastComponent.getTypes()) && !TRANSFROM_TYPE.equals(lastComponent.getTypes())) {
//            return new CheckComponent(false, String.format("组件[%s]中发生了异常：组件前置配置不匹配(前置组件应为数据集组件或者特征工程组件)，请检查流程是否合理。", component));
//        }
//        if (!HeteroFeatureBinning_COMPONENT.getComponentName().contains(lastComponent.getComponentEn().toLowerCase())) {
//            return new CheckComponent(false, String.format("组件[%s]中发生了异常：组件前置配置不匹配(前置组件应为分箱编码组件)，请检查流程是否合理。", component));
//        }
        return new CheckComponent(true, "success");
    }

    @Override
    public void build(Map<String, Object> module, Map<String, Object> model, Map<String, Integer> num,
                      Map<String, Object> common, String preComponent,
                      FModelElementDo fModelElementDo, FComponentDo componentDo) {
        Map<String, Object> input = new HashMap<>(5);
        Map<String, Object> output = new HashMap<>(5);
        Map<String, Object> data = new HashMap<>(5);
        ComponentDsl componentDsl = new ComponentDsl();
        data.put("data", Arrays.asList(preComponent + ".train"));
        output.put("data", Arrays.asList("train"));
        input.put("data", data);
        //判断是否是分割组件
        if (preComponent.toLowerCase().contains(Feature_Binning)) {
            input.put("isometric_model", Arrays.asList(preComponent + ".model"));
        }
        if (preComponent.toLowerCase().contains("statistic")) {
            input.put("isometric_model", Arrays.asList(preComponent + ".model"));
        }
        output.put("model", Arrays.asList("model"));
        THeteroFeatureSelectionComponent aggregationComponent = JsonUtil.gson().fromJson(JsonUtil.toJson(JsonUtil.gson().fromJson(fModelElementDo.getConfig(), Map.class)), THeteroFeatureSelectionComponent.class);
        List<Integer> sum_cols = new ArrayList<>();
        if (StringUtils.isNotBlank(fModelElementDo.getData())) {
            String[] split = fModelElementDo.getData().split(",");
            List<String> splitList = Arrays.asList(split);
            for (String cls : aggregationComponent.getManually_param().getFilter_out_names()) {
                int indexOf = splitList.indexOf(cls);
                if (indexOf >= 0) {
                    sum_cols.add(indexOf);
                }
            }
        }

        aggregationComponent.setSelect_col_indexes(-1);
        String[] filter_methods = aggregationComponent.getFilter_methods();
        List<String> strings = Arrays.asList(filter_methods);
        if (!strings.contains("iv_percentile")) {
            aggregationComponent.setIv_percentile_param(null);
        }
        if (!strings.contains("iv_value_thres")) {
            aggregationComponent.setIv_value_param(null);
        }
        if (!strings.contains("unique_value")) {
            aggregationComponent.setUnique_param(null);
        }
        if (!strings.contains("coefficient_of_variation_value_thres")) {
            aggregationComponent.setVariance_coe_param(null);
        }
        if (!strings.contains("outlier_cols")) {
            aggregationComponent.setOutlier_param(null);
        }
        if (!strings.contains("manually")) {
            aggregationComponent.setManually_param(null);
        }else {
            aggregationComponent.getManually_param().setLeft_col_names(aggregationComponent.getManually_param().getFilter_out_names());
            aggregationComponent.getManually_param().setLeft_col_indexes(new ArrayList<>());
            aggregationComponent.getManually_param().setFilter_out_indexes(null);
            aggregationComponent.getManually_param().setFilter_out_names(null);
        }

        model.put(componentDo.getComponent().toLowerCase() + "_" + num.get(componentDo.getComponent().toLowerCase()), JsonUtil.toJsonObject(JsonUtil.toSimpleJson(aggregationComponent)));
        componentDsl.setInput(input);
        componentDsl.setOutput(output);
        componentDsl.setModule(componentDo.getComponentEn());
        module.put(componentDo.getComponent().toLowerCase() + "_" + num.get(componentDo.getComponent().toLowerCase()), componentDsl);
    }


}
