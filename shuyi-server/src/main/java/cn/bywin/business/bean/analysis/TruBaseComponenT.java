package cn.bywin.business.bean.analysis;


import cn.bywin.business.bean.bydb.TTruModelComponentDo;
import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.bydb.TTruModelElementDo;
import cn.bywin.business.bean.bydb.TTruModelElementRelDo;
import cn.bywin.business.bean.bydb.TTruModelFieldDo;
import cn.bywin.business.bean.bydb.TTruModelObjectDo;
import cn.bywin.business.bean.view.bydb.TruNode;
import java.util.List;

/**
 * @Description 组件模板
 * @Author wangh
 * @Date 2021-10-20
 */
public abstract class TruBaseComponenT {

    /**
     * @Description 表数据
     * @Author wangh
     * @Date 2021-10-20
     */
    protected List<TTruModelObjectDo> datasource;

    /**
     * @Description 前置组件配置
     * @Author wangh
     * @Date 2021-10-20
     */
    protected List<TTruModelElementDo> preModel;

    /**
     * @Description 后置组件配置
     * @Author wangh
     * @Date 2021-10-20
     */
    protected List<TTruModelElementDo> nextModel;

    /**
     * @Description 前置组件配置
     * @Author wangh
     * @Date 2021-10-20
     */
    protected List<TTruModelElementDo> model;


    /**
     * @Description 上级组件
     * @Author wangh
     * @Date 2021-10-20
     */
    protected List<TTruModelComponentDo> components;



    protected  List<TTruModelFieldDo> extendsDos;

    public List<TTruModelObjectDo> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<TTruModelObjectDo> datasource) {
        this.datasource = datasource;
    }

    public List<TTruModelElementDo> getPreModel() {
        return preModel;
    }

    public void setPreModel(List<TTruModelElementDo> preModel) {
        this.preModel = preModel;
    }
    public List<TTruModelElementDo> getNextModel() {
        return nextModel;
    }

    public void setNextModel(List<TTruModelElementDo> nextModel) {
        this.nextModel = nextModel;
    }

    public List<TTruModelElementDo> getModel() {
        return model;
    }

    public void setModel(List<TTruModelElementDo> model) {
        this.model = model;
    }

    public List<TTruModelComponentDo> getComponents() {
        return components;
    }

    public void setComponents(List<TTruModelComponentDo> components) {
        this.components = components;
    }
    public List<TTruModelFieldDo> getExtendsDos() {
        return extendsDos;
    }

    public void setExtendsDos(List<TTruModelFieldDo> extendsDos) {
        this.extendsDos = extendsDos;
    }
    /**
     *
     * @return 初始化配置
     * @DescriptionFModelElementDo fModelElementDo
     * @Author wangh
     * @Date 2021-10-20
     */
    public abstract TruNode init( TruNode nodes, TTruModelElementDo fModelElementDo) throws Exception;


    /**
     * 重名相同字段
     * @param truNode
     * @param fModelElementDo
     * @return
     * @throws Exception
     */
    public abstract boolean changeSameFieldName( TruNode truNode, TTruModelElementDo fModelElementDo) throws Exception;

    /**
     * @Description 组件校验
     * @Author wangh
     * @Date 2021-10-20
     */
    public abstract TruCheckComponent check( TruNode truNode, TTruModelElementDo fModelElementDo) throws Exception;


    /**
     * @Description 组件构建
     * @Author wangh
     * @Date 2021-10-18
     */
    public abstract void build( TruNode truNode, TTruModelElementDo fModelElementDo, TTruModelDo model) throws Exception;

    /**
     * @Description 组件字段继承
     * @Author wangh
     * @Date 2021-10-18
     */
    public abstract List<TTruModelFieldDo> relExtends(TTruModelElementRelDo tBydbModelElementRelDo) throws Exception;


    /**
     * @Description 组件字段回显
     * @Author wangh
     * @Date 2021-11-28
     */
    public abstract List<TTruModelFieldDo> getShowField(List<TTruModelFieldDo> fieldDos) throws Exception;

    /**
     * @Description 组件字段是否被引用
     * @Author wangh
     * @Date 2021-11-25
     */
   //public abstract CheckComponent checkField(Node node,TTruModelElementDo elementInfo, List<TTruModelFieldDo> fieldDos);
}
