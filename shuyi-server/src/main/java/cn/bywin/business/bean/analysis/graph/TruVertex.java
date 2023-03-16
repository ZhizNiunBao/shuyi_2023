package cn.bywin.business.bean.analysis.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
public class TruVertex<T> {

    private T id;

    private List<TruEdge> truEdgeList;

    private List<TruVertex<T>> previousTruVertices;

    private boolean visited;

    public TruVertex( T id) {
        this.id = id;
        truEdgeList = new LinkedList<>();
        previousTruVertices = new ArrayList<>();
        visited = false;
    }

    public T getId() {
        return id;
    }

    public void connect( TruVertex<T> endTruVertex ) {
        truEdgeList.add(new TruEdge( endTruVertex ));
        endTruVertex.addPreviousVertex(this);
    }

    public void addPreviousVertex( TruVertex<T> previousTruVertex ) {
        previousTruVertices.add( previousTruVertex );
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

    public List<TruVertex<T>> getPreviousVertexs() {
        return previousTruVertices;
    }

    public List<TruEdge> getEdges() {
        return truEdgeList;
    }

    public List<TruVertex<T>> getNeighbors( boolean visited) {
        List<TruVertex<T>> result = new ArrayList<>();
        Iterator<TruEdge> edgeIterator = truEdgeList.iterator();
        while (edgeIterator.hasNext()) {
            TruVertex nextTruVertex = edgeIterator.next().getEndVertex();
            if (visited == nextTruVertex.isVisited()) {
                result.add( nextTruVertex );
            }
        }
        return result;
    }

    public TruVertex<T> getUnvisitedNeighbor() {
        Iterator<TruEdge> edgeIterator = truEdgeList.iterator();
        while (edgeIterator.hasNext()) {
            TruVertex nextTruVertex = edgeIterator.next().getEndVertex();
            if (!nextTruVertex.isVisited()) {
                return nextTruVertex;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TruVertex ) {
            TruVertex<T> otherTruVertex = (TruVertex<T>) obj;
            T otherVertexId = otherTruVertex.getId();
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
