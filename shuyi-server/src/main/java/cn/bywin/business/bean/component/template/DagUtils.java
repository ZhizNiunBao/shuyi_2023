package cn.bywin.business.bean.component.template;


import static cn.bywin.business.bean.component.ComponentEnum.FillMissing_COMPONENT;

import cn.bywin.business.bean.component.ComponentEnum;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FDataPartyDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.bean.federal.FModelElementRelDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.federal.graph.DirectedGraph;
import cn.bywin.business.bean.federal.graph.Vertex;
import cn.bywin.business.common.util.JsonUtil;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description 构建dag 图
 * @Author  wangh
 * @Date 2021-07-27
 */


public class DagUtils {

    public static JobRuntimeConf buildDag(List<FModelElementDo> elements, List<FModelElementRelDo> elementRels, Map<String, FComponentDo> idComponentMap,
                                          FNodePartyDo host, List<Integer> guest, List<FDataPartyDo> hlist) {
        JobRuntimeConf jobRuntimeConf = new JobRuntimeConf(host, guest);
        // 判断构建的任务链是否可以运行
        DirectedGraph<String> directedGraph = new DirectedGraph();
        elements.forEach(element -> {
            directedGraph.addVertex(element.getId());
        });
        elementRels.forEach(element -> {
            directedGraph.addEdge(element.getStartElementId(), element.getEndElementId());
        });
        if (directedGraph.hasIsolatedVertices()) {
            throw new IllegalArgumentException("存在孤立节点,请重新配置");
        }
        if (directedGraph.hasRecycle()) {
            throw new IllegalArgumentException("组件存在循环依赖,请重新配置");
        }
        Map<String, FModelElementDo> idElementMap = elements.stream().collect(Collectors.toMap(FModelElementDo::getId, e -> e));
       // Map<String, FComponentDo> idComponentMap = fComponentDos.stream().collect(Collectors.toMap(FComponentDo::getId, e -> e));
        List<Vertex<String>> schedulePath = directedGraph.buildTaskSchedulePath();
        String preComponent = "";
        Map<String, Object> common = Maps.newLinkedHashMap();
        Map<String, Object> module = Maps.newLinkedHashMap();
        Map<String, Integer> num = new HashMap<>(10);
        Map<String, Object> model =Maps.newLinkedHashMap();
        for (Vertex<String> element : schedulePath) {
            FModelElementDo fModelElementDo = idElementMap.get(element.getId());
            FComponentDo componentDo = idComponentMap.get(fModelElementDo.getComponentId());
            BaseComponenT baseComponenT = ComponentEnum.getInstanceByName(componentDo.getComponentEn().toLowerCase());
            if (baseComponenT==null) {
                throw new IllegalArgumentException("组件配置错误");
            }
            //dag图 上级节点只有一个
            if (element.getPreviousVertexs().size()>1){
                throw new IllegalArgumentException("模型流程配置异常");
            }
            if (element.getPreviousVertexs().size()==1){
                FModelElementDo lastModelElementDo = idElementMap.get(element.getPreviousVertexs().get(0).getId());
                FComponentDo lastComponentDo = idComponentMap.get(lastModelElementDo.getComponentId());
                if (!lastComponentDo.getComponent().equals(FillMissing_COMPONENT.getComponentName())){
                    preComponent = lastComponentDo.getComponent().toLowerCase() + "_"+num.get(lastComponentDo.getComponent().toLowerCase());
                }
            }
            //保存dag流程组件角标记录
            if (num.containsKey(componentDo.getComponent().toLowerCase())){
                num.put(componentDo.getComponent().toLowerCase(),num.get(componentDo.getComponent().toLowerCase())+1);
            }else {
                num.put(componentDo.getComponent().toLowerCase(),0);
            }
            baseComponenT.setData(hlist);
            baseComponenT.build(module,model,num,common,preComponent,fModelElementDo,componentDo);
        }
        common.put("common", JsonUtil.toJsonObject(JsonUtil.toJson(model)));
        jobRuntimeConf.putconf("component_parameters", JsonUtil.toJsonObject(JsonUtil.toJson(common)));
        jobRuntimeConf.putdsl("components",JsonUtil.toJsonObject(JsonUtil.toJson(module)) );
      //  jobRuntimeConf.setJob_runtime_conf(jobRuntimeConf.getJob_runtime_conf());
        return jobRuntimeConf;
    }

}
