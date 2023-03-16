package cn.bywin.business.bean.analysis.olk;


import cn.bywin.business.bean.olk.TOlkModelComponentDo;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.view.olk.OlkNode;
import java.util.List;

/**
 * @Description 组件模板
 * @Author wangh
 * @Date 2021-10-20
 */
public abstract class OlkBaseComponenT {

    /**
     * @Description 表数据
     * @Author wangh
     * @Date 2021-10-20
     */
    protected List<TOlkModelObjectDo> datasource;

    /**
     * @Description 前置组件配置
     * @Author wangh
     * @Date 2021-10-20
     */
    protected List<TOlkModelElementDo> preModel;

    /**
     * @Description 后置组件配置
     * @Author wangh
     * @Date 2021-10-20
     */
    protected List<TOlkModelElementDo> nextModel;

    /**
     * @Description 前置组件配置
     * @Author wangh
     * @Date 2021-10-20
     */
    protected List<TOlkModelElementDo> model;


    /**
     * @Description 上级组件
     * @Author wangh
     * @Date 2021-10-20
     */
    protected List<TOlkModelComponentDo> components;



    protected  List<TOlkModelFieldDo> extendsDos;

    public List<TOlkModelObjectDo> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<TOlkModelObjectDo> datasource) {
        this.datasource = datasource;
    }

    public List<TOlkModelElementDo> getPreModel() {
        return preModel;
    }

    public void setPreModel(List<TOlkModelElementDo> preModel) {
        this.preModel = preModel;
    }
    public List<TOlkModelElementDo> getNextModel() {
        return nextModel;
    }

    public void setNextModel(List<TOlkModelElementDo> nextModel) {
        this.nextModel = nextModel;
    }

    public List<TOlkModelElementDo> getModel() {
        return model;
    }

    public void setModel(List<TOlkModelElementDo> model) {
        this.model = model;
    }

    public List<TOlkModelComponentDo> getComponents() {
        return components;
    }

    public void setComponents(List<TOlkModelComponentDo> components) {
        this.components = components;
    }
    public List<TOlkModelFieldDo> getExtendsDos() {
        return extendsDos;
    }

    public void setExtendsDos(List<TOlkModelFieldDo> extendsDos) {
        this.extendsDos = extendsDos;
    }
    /**
     *
     * @return 初始化配置
     * @DescriptionFModelElementDo fModelElementDo
     * @Author wangh
     * @Date 2021-10-20
     */
    public abstract OlkNode init( OlkNode nodes, TOlkModelElementDo fModelElementDo) throws Exception;


    /**
     * 重名相同字段
     * @param truNode
     * @param fModelElementDo
     * @return
     * @throws Exception
     */
    public abstract boolean changeSameFieldName( OlkNode truNode, TOlkModelElementDo fModelElementDo) throws Exception;

    /**
     * @Description 组件校验
     * @Author wangh
     * @Date 2021-10-20
     */
    public abstract OlkCheckComponent check( OlkNode truNode, TOlkModelElementDo fModelElementDo) throws Exception;


    /**
     * @Description 组件构建
     * @Author wangh
     * @Date 2021-10-18
     */
    public abstract void build( OlkNode truNode, TOlkModelElementDo fModelElementDo, TOlkModelDo model) throws Exception;

    /**
     * @Description 组件字段继承
     * @Author wangh
     * @Date 2021-10-18
     */
    public abstract List<TOlkModelFieldDo> relExtends(TOlkModelElementRelDo tBydbModelElementRelDo) throws Exception;


    /**
     * @Description 组件字段回显
     * @Author wangh
     * @Date 2021-11-28
     */
    public abstract List<TOlkModelFieldDo> getShowField(List<TOlkModelFieldDo> fieldDos) throws Exception;

    /**
     * @Description 组件字段是否被引用
     * @Author wangh
     * @Date 2021-11-25
     */
   //public abstract CheckComponent checkField(Node node,TOlkModelElementDo elementInfo, List<TOlkModelFieldDo> fieldDos);
}
