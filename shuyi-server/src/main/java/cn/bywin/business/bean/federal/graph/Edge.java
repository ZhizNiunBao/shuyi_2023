package cn.bywin.business.bean.federal.graph;
/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
public class Edge<T> {


    public Vertex<T> vertex;

    public Edge(Vertex<T> endVertex) {
        vertex = endVertex;
    }

    public Vertex<T> getEndVertex() {
        return vertex;
    }
}
