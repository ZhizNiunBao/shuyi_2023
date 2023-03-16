package cn.bywin.business.bean.analysis;

import cn.bywin.business.bean.analysis.template.*;

/**
 * @Description 组件模板
 * @Author wangh
 * @Date 2021-10-20
 */
public enum TruComponentEnum {

    //表组件
    Data_COMPONENT("t_data", TruTableComponent.class),
    //连接组件
    Join_COMPONENT("t_join", TruJoinComponent.class),
    //聚合组件
    GROUP_COMPONENT("t_aggregation", TruAggregationComponent.class),
    //字段过滤组件
    FieldFilter_COMPONENT("t_filter", TruFieldFilterComponent.class),
    //合并行
    FieldConcat_COMPONENT("t_concat", TruFieldConcatComponent.class),
    //差集
    Collection_COMPONENT("t_collection", TruCollectionComponent.class),
    //交集
    Intersect_COMPONENT("t_intersect", TruIntersectionComponent.class),
    //计算列函数
    FieldFunc_COMPONENT("t_func", TruFieldFuncComponent.class),
    //数据源输出
    DataSourceOutPut_COMPONENT("t_datasource", TruDataSourceOutPutComponent.class),
    //字段设置
    FieldSelect_COMPONENT("t_select", TruFieldSelectComponent.class);

    private String componentName;
    private Class<? extends TruBaseComponenT> clazz;

    TruComponentEnum( String componentName, Class<? extends TruBaseComponenT> component) {
        this.componentName = componentName;
        this.clazz = component;
    }


    public Class<? extends TruBaseComponenT> getClazz() {
        return clazz;
    }

    public String getComponentName() {
        return componentName;
    }

    public static TruBaseComponenT getInstanceByName( String componentName) {
        TruComponentEnum[] enums = values();
        for ( TruComponentEnum truComponentEnum : enums) {
            if ( truComponentEnum.getComponentName().equals(componentName)) {
                try {
                    TruBaseComponenT modelComponent = truComponentEnum.getClazz().newInstance();
                    return modelComponent;
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
