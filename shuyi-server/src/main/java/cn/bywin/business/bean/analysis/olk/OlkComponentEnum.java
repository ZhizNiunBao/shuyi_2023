package cn.bywin.business.bean.analysis.olk;

import cn.bywin.business.bean.analysis.olk.template.OlkAggregationComponent;
import cn.bywin.business.bean.analysis.olk.template.OlkCollectionComponent;
import cn.bywin.business.bean.analysis.olk.template.OlkDataSourceOutPutComponent;
import cn.bywin.business.bean.analysis.olk.template.OlkFieldConcatComponent;
import cn.bywin.business.bean.analysis.olk.template.OlkFieldFilterComponent;
import cn.bywin.business.bean.analysis.olk.template.OlkFieldFuncComponent;
import cn.bywin.business.bean.analysis.olk.template.OlkFieldSelectComponent;
import cn.bywin.business.bean.analysis.olk.template.OlkIntersectionComponent;
import cn.bywin.business.bean.analysis.olk.template.OlkJoinComponent;
import cn.bywin.business.bean.analysis.olk.template.OlkSqlOperatorComponent;
import cn.bywin.business.bean.analysis.olk.template.OlkTableComponent;

/**
 * @Description 组件模板
 * @Author wangh
 * @Date 2021-10-20
 */
public enum OlkComponentEnum {

    //表组件
    Data_COMPONENT("t_data", OlkTableComponent.class),
    //连接组件
    Join_COMPONENT("t_join", OlkJoinComponent.class),
    //聚合组件
    GROUP_COMPONENT("t_aggregation", OlkAggregationComponent.class),
    //字段过滤组件
    FieldFilter_COMPONENT("t_filter", OlkFieldFilterComponent.class),
    //合并行
    FieldConcat_COMPONENT("t_concat", OlkFieldConcatComponent.class),
    //差集
    Collection_COMPONENT("t_collection", OlkCollectionComponent.class),
    //交集
    Intersect_COMPONENT("t_intersect", OlkIntersectionComponent.class),
    //计算列函数
    FieldFunc_COMPONENT("t_func", OlkFieldFuncComponent.class),
    //sql算子
    SqlOperator_COMPONENT("t_operator", OlkSqlOperatorComponent.class),
    //数据源输出
    DataSourceOutPut_COMPONENT("t_datasource", OlkDataSourceOutPutComponent.class),
    //字段设置
    FieldSelect_COMPONENT("t_select", OlkFieldSelectComponent.class);

    private String componentName;
    private Class<? extends OlkBaseComponenT> clazz;

    OlkComponentEnum( String componentName, Class<? extends OlkBaseComponenT> component) {
        this.componentName = componentName;
        this.clazz = component;
    }


    public Class<? extends OlkBaseComponenT> getClazz() {
        return clazz;
    }

    public String getComponentName() {
        return componentName;
    }

    public static OlkBaseComponenT getInstanceByName( String componentName) {
        OlkComponentEnum[] enums = values();
        for ( OlkComponentEnum truComponentEnum : enums) {
            if ( truComponentEnum.getComponentName().equals(componentName)) {
                try {
                    OlkBaseComponenT modelComponent = truComponentEnum.getClazz().newInstance();
                    return modelComponent;
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
