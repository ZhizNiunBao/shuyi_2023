package cn.bywin.business.bean.component;

import cn.bywin.business.bean.component.template.BaseComponenT;
import cn.bywin.business.bean.component.template.TDataComponent;
import cn.bywin.business.bean.component.template.TDataStatisticsComponent;
import cn.bywin.business.bean.component.template.TEvaluationComponent;
import cn.bywin.business.bean.component.template.TFeatureScaleComponent;
import cn.bywin.business.bean.component.template.TFeldmanVerifiableSumComponent;
import cn.bywin.business.bean.component.template.TFillMissingComponent;
import cn.bywin.business.bean.component.template.THeteroDataSplitComponent;
import cn.bywin.business.bean.component.template.THeteroFeatureBinningComponent;
import cn.bywin.business.bean.component.template.THeteroFeatureSelectionComponent;
import cn.bywin.business.bean.component.template.THeteroKmeansComponent;
import cn.bywin.business.bean.component.template.THeteroLinrComponent;
import cn.bywin.business.bean.component.template.THeteroLrComponent;
import cn.bywin.business.bean.component.template.THeteroNNComponent;
import cn.bywin.business.bean.component.template.THeteroPearsonComponent;
import cn.bywin.business.bean.component.template.THeteroPoissonComponent;
import cn.bywin.business.bean.component.template.THeteroSecureBoostComponent;
import cn.bywin.business.bean.component.template.THomoDataSplitComponent;
import cn.bywin.business.bean.component.template.THomoFeatureBinningComponent;
import cn.bywin.business.bean.component.template.THomoLrComponent;
import cn.bywin.business.bean.component.template.THomoNNComponent;
import cn.bywin.business.bean.component.template.THomoOneHotEncoderComponent;
import cn.bywin.business.bean.component.template.THomoSecureBoostComponent;
import cn.bywin.business.bean.component.template.TIntersectionComponent;
import cn.bywin.business.bean.component.template.TOneHotEncoderComponent;
import cn.bywin.business.bean.component.template.TPsiComponent;
import cn.bywin.business.bean.component.template.TUnionComponent;

/**
 * @Description
 * @Author wangh
 * @Date 2021-07-27
 */
public enum ComponentEnum {

    //数据集组件
    Data_COMPONENT("datatransform", TDataComponent.class),
    //交集组件
    Intersection_COMPONENT("intersection", TIntersectionComponent.class),
    // 特征统计
    DataStatistics_COMPONENT("datastatistics", TDataStatisticsComponent.class),
    //特征化组件
    FeatureScale_COMPONENT("featurescale", TFeatureScaleComponent.class),
    // 数据缺省值
    FillMissing_COMPONENT("fillmissing", TFillMissingComponent.class),
    //纵向数据分割
    HeteroDataSplit_COMPONENT("heterodatasplit", THeteroDataSplitComponent.class),
    //横向数据分割
    HomoDataSplit_COMPONENT("homodatasplit", THomoDataSplitComponent.class),
    //纵向特征分箱组件
    HeteroFeatureBinning_COMPONENT("heterofeaturebinning", THeteroFeatureBinningComponent.class),
    //纵向特征分箱组件
    HomoFeatureBinning_COMPONENT("homofeaturebinning", THomoFeatureBinningComponent.class),
    //纵向特征选择组件
    HeteroFeatureSelection_COMPONENT("heterofeatureselection", THeteroFeatureSelectionComponent.class),
    //one-hot数据组件
    OneHotEncoder_COMPONENT("onehotencoder", TOneHotEncoderComponent.class),
    //横向one-hot数据组件
    HomoOneHotEncoder_COMPONENT("homoonehotencoder", THomoOneHotEncoderComponent.class),
    //纵向逻辑组件
    HeteroLr_COMPONENT("heterolr", THeteroLrComponent.class),
    //纵向岭回归组件
    HeteroLinR_COMPONENT("heterolinr", THeteroLinrComponent.class),
    //纵向特征sum组件模板
    FeldmanVerifiableSum_COMPONENT("feldmanverifiablesum", TFeldmanVerifiableSumComponent.class),
    //纵向Kmeans组件
    HeteroKmeans_COMPONENT("heterokmeans", THeteroKmeansComponent.class),
    //横向boost组件
    HomoSecureBoost_COMPONENT("homosecureboost", THomoSecureBoostComponent.class),
    //纵向boost组件
    HeteroSecureBoost_COMPONENT("heterosecureboost", THeteroSecureBoostComponent.class),
    //横向逻辑组件
    HomoLr_COMPONENT("homolr", THomoLrComponent.class),
    //横向神经网络算法
    HomoNN_COMPONENT("homonn", THomoNNComponent.class),
    //纵向神经网络算法
    HeteroNN_COMPONENT("heteronn", THeteroNNComponent.class),
    //Psi
    Psi_COMPONENT("psi", TPsiComponent.class),
    //Psi
    Pearson_COMPONENT("heteropearson", THeteroPearsonComponent.class),
    //Psi
    Poisson_COMPONENT("poisson", THeteroPoissonComponent.class),
    //Psi
    Union_COMPONENT("union", TUnionComponent.class),
    //预测组件
    Evaluation_COMPONENT("evaluation", TEvaluationComponent.class);

    /**
     * 数据集类型
     */
    public static final Integer DATA_TYPE = 2;
    /**
     * 数据处理类型
     */
    public static final Integer TRANSFROM_TYPE = 3;
    /**
     * 算法类型
     */
    public static final Integer ALGO_TYPE = 4;
    /**
     * 预测类型
     */
    public static final Integer EVL_TYPE = 5;

    /**
     * 数据分割前置条件判断
     */
    public static final String Data_Split = "split";

    /**
     * 数据分箱前置条件判断
     */
    public static final String Feature_Binning = "binning";
    private String componentName;
    private Class<? extends BaseComponenT> clazz;

    ComponentEnum(String componentName, Class<? extends BaseComponenT> component) {
        this.componentName = componentName;
        this.clazz = component;
    }

    public Class<? extends BaseComponenT> getClazz() {
        return clazz;
    }

    public String getComponentName() {
        return componentName;
    }

    public static BaseComponenT getInstanceByName(String componentName) {
        ComponentEnum[] enums = values();
        for (ComponentEnum componentEnum : enums) {
            if (componentEnum.getComponentName().equals(componentName)) {
                try {
                    BaseComponenT modelComponent = componentEnum.getClazz().newInstance();
                    return modelComponent;
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }


}
