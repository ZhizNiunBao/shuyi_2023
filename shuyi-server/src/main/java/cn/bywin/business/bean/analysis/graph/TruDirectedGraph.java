package cn.bywin.business.bean.analysis.graph;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
public class TruDirectedGraph<T> {

    private Map<T, TruVertex<T>> vertices;

    public TruDirectedGraph() {
        vertices = new HashMap<>();
    }

    public void addVertex(T vertexId) {
        TruVertex truVertex = new TruVertex(vertexId);
        vertices.put(vertexId, truVertex );
    }

    public void addEdge(T begin, T end) {
        TruVertex<T> beginTruVertex = vertices.get(begin);
        TruVertex<T> endTruVertex = vertices.get(end);
        if ( beginTruVertex != null && endTruVertex != null) {
            beginTruVertex.connect( endTruVertex );
        }
    }

    /**
     * 根据该有向无环图获取任务执行链
     * 调用前需要调用 @see {@link #hasRecycle()} 判断是否存在环形
     * @return          广度遍历路径
     */
    public List<TruVertex<T>> buildTaskSchedulePath() {
        List<TruVertex<T>> result = new ArrayList<>();

        // 将所有顶点都设置为未访问
        resetVertices();

        // 找出入度为0的点
        List<TruVertex<T>> sourceVertices = vertices.values()
                .stream()
                .filter( truVertex -> truVertex.getPreviousVertexs().size() == 0)
                .collect(Collectors.toList());

        // 广度遍历辅助队列
        Queue<TruVertex<T>> queue = new LinkedList<>();
        sourceVertices.forEach( truVertex -> queue.add( truVertex ));

        while (!queue.isEmpty()) {
            TruVertex<T> topTruVertex = queue.poll();
            List<TruVertex<T>> previousTruVertices = topTruVertex.getPreviousVertexs();
            boolean changePosition = false;
            for ( TruVertex<T> previousTruVertex : previousTruVertices ) {
                // 如果存在前置节点还没被访问的情况,则把该节点放到队列最后
                if (!previousTruVertex.isVisited()) {
                    queue.offer( topTruVertex );
                    changePosition = true;
                    break;
                }
            }
            List<TruVertex<T>> unVisitedNeighbors = topTruVertex.getNeighbors(false);
            unVisitedNeighbors.forEach(neighbors -> {
                if (!queue.contains(neighbors)) {
                    queue.offer(neighbors);
                }
            });
            if (!changePosition) {
                topTruVertex.visit();
                result.add( topTruVertex );
            }
        }
        return result;
    }


//
//    public Map<String, ComponentDsl> buildTaskSchedulePath() {
//        Map<String, ComponentDsl> result=new HashMap<>();
//        // 将所有顶点都设置为未访问
//        resetVertices();
//
//        // 找出入度为0的点
//        List<Vertex<T>> sourceVertices = vertices.values()
//                .stream()
//                .filter(vertex -> vertex.getPreviousVertexs().size() == 0)
//                .collect(Collectors.toList());
//
//        // 广度遍历辅助队列
//        Queue<Vertex<T>> queue = new LinkedList<>();
//        sourceVertices.forEach(vertex -> queue.add(vertex));
//
//        while (!queue.isEmpty()) {
//            Vertex<T> topVertex = queue.poll();
//            List<Vertex<T>> previousVertexs = topVertex.getPreviousVertexs();
//            boolean changePosition = false;
//            for (Vertex<T> previousVertex : previousVertexs) {
//                // 如果存在前置节点还没被访问的情况,则把该节点放到队列最后
//                if (!previousVertex.isVisited()) {
//                    queue.offer(topVertex);
//                    changePosition = true;
//                    break;
//                }
//            }
//            List<Vertex<T>> unVisitedNeighbors = topVertex.getNeighbors(false);
//            unVisitedNeighbors.forEach(neighbors -> {
//                if (!queue.contains(neighbors)) {
//                    queue.offer(neighbors);
//                }
//            });
//            if (!changePosition) {
//                topVertex.visit();
//                result.add(topVertex);
//            }
//        }
//        return result;
//    }

    public boolean  hasIsolatedVertices() {
        for ( TruVertex<T> truVertex : vertices.values()) {
            if ( truVertex.getPreviousVertexs().isEmpty() && truVertex.getEdges().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断从某个顶点出发是否存在环
     * @return
     */
    public boolean hasRecycle() {
        resetVertices();

        // 深度遍历的辅助栈
        LinkedList<TruVertex<T>> truVertexStack = new LinkedList<>();
        // 已经遍历过的点
        List<TruVertex<T>> traversaledTruVertices = new ArrayList<>();

        for ( TruVertex<T> truVertex : vertices.values()) {
            if ( truVertex.isVisited()) {
                continue;
            }
            truVertex.visit();
            truVertexStack.push( truVertex );
            while (!truVertexStack.isEmpty()) {
                TruVertex<T> topTruVertex = truVertexStack.peek();
                List<TruVertex<T>> visitedNeighbors = topTruVertex.getNeighbors(true);
                for ( TruVertex<T> neighbor : visitedNeighbors) {
                    if (!traversaledTruVertices.contains(neighbor)) {
                        return true;
                    }
                }
                TruVertex<T> nextNeighbor = topTruVertex.getUnvisitedNeighbor();
                if (nextNeighbor != null) {
                    nextNeighbor.visit();
                    truVertexStack.push(nextNeighbor);
                } else {
                    truVertexStack.pop();
                    traversaledTruVertices.add( topTruVertex );
                }
            }
        }
        return false;
    }

    /**
     * 将所有点都设置为未访问
     */
    private void resetVertices() {
        Iterator<TruVertex<T>> vertexIterator = vertices.values().iterator();
        while (vertexIterator.hasNext()) {
            TruVertex<T> nextTruVertex = vertexIterator.next();
            nextTruVertex.unVisit();
        }
    }
}
