package cn.bywin.business.bean.federal.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
public class Vertex<T> {

    private T id;

    private List<Edge> edgeList;

    private List<Vertex<T>> previousVertexs;

    private boolean visited;

    public Vertex(T id) {
        this.id = id;
        edgeList = new LinkedList<>();
        previousVertexs = new ArrayList<>();
        visited = false;
    }

    public T getId() {
        return id;
    }

    public void connect(Vertex<T> endVertex) {
        edgeList.add(new Edge(endVertex));
        endVertex.addPreviousVertex(this);
    }

    public void addPreviousVertex(Vertex<T> previousVertex) {
        previousVertexs.add(previousVertex);
    }

    public boolean isVisited() {
        return visited;
    }

    public void visit() {
        visited = true;
    }

    public void unVisit() {
        visited = false;
    }

    public List<Vertex<T>> getPreviousVertexs() {
        return previousVertexs;
    }

    public List<Edge> getEdges() {
        return edgeList;
    }

    public List<Vertex<T>> getNeighbors(boolean visited) {
        List<Vertex<T>> result = new ArrayList<>();
        Iterator<Edge> edgeIterator = edgeList.iterator();
        while (edgeIterator.hasNext()) {
            Vertex nextVertex = edgeIterator.next().getEndVertex();
            if (visited == nextVertex.isVisited()) {
                result.add(nextVertex);
            }
        }
        return result;
    }

    public Vertex<T> getUnvisitedNeighbor() {
        Iterator<Edge> edgeIterator = edgeList.iterator();
        while (edgeIterator.hasNext()) {
            Vertex nextVertex = edgeIterator.next().getEndVertex();
            if (!nextVertex.isVisited()) {
                return nextVertex;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vertex) {
            Vertex<T> otherVertex = (Vertex<T>) obj;
            T otherVertexId = otherVertex.getId();
            if (otherVertexId != null && otherVertexId.equals(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
