package cn.bywin.business.bean.analysis.graph;
/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
public class TruEdge<T> {


    public TruVertex<T> truVertex;

    public TruEdge( TruVertex<T> endTruVertex ) {
        truVertex = endTruVertex;
    }

    public TruVertex<T> getEndVertex() {
        return truVertex;
    }
}
