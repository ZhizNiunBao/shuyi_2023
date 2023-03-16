package cn.bywin.business.bean.analysis.olk.graph;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
public class OlkEdge<T> {


    public OlkVertex<T> truVertex;

    public OlkEdge( OlkVertex<T> endTruVertex ) {
        truVertex = endTruVertex;
    }

    public OlkVertex<T> getEndVertex() {
        return truVertex;
    }
}
