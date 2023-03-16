package cn.bywin.business.bean.analysis.olk.graph;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
public class OlkVertex<T> {

    private T id;

    private List<OlkEdge> truEdgeList;

    private List<OlkVertex<T>> previousTruVertices;

    private boolean visited;

    public OlkVertex( T id) {
        this.id = id;
        truEdgeList = new LinkedList<>();
        previousTruVertices = new ArrayList<>();
        visited = false;
    }

    public T getId() {
        return id;
    }

    public void connect( OlkVertex<T> endTruVertex ) {
        truEdgeList.add(new OlkEdge( endTruVertex ));
        endTruVertex.addPreviousVertex(this);
    }

    public void addPreviousVertex( OlkVertex<T> previousTruVertex ) {
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

    public List<OlkVertex<T>> getPreviousVertexs() {
        return previousTruVertices;
    }

    public List<OlkEdge> getEdges() {
        return truEdgeList;
    }

    public List<OlkVertex<T>> getNeighbors( boolean visited) {
        List<OlkVertex<T>> result = new ArrayList<>();
        Iterator<OlkEdge> edgeIterator = truEdgeList.iterator();
        while (edgeIterator.hasNext()) {
            OlkVertex nextTruVertex = edgeIterator.next().getEndVertex();
            if (visited == nextTruVertex.isVisited()) {
                result.add( nextTruVertex );
            }
        }
        return result;
    }

    public OlkVertex<T> getUnvisitedNeighbor() {
        Iterator<OlkEdge> edgeIterator = truEdgeList.iterator();
        while (edgeIterator.hasNext()) {
            OlkVertex nextTruVertex = edgeIterator.next().getEndVertex();
            if (!nextTruVertex.isVisited()) {
                return nextTruVertex;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OlkVertex ) {
            OlkVertex<T> otherTruVertex = (OlkVertex<T>) obj;
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
