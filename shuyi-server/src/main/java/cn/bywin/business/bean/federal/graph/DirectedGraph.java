package cn.bywin.business.bean.federal.graph;


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
public class DirectedGraph<T> {

    private Map<T, Vertex<T>> vertices;

    public DirectedGraph() {
        vertices = new HashMap<>();
    }

    public void addVertex(T vertexId) {
        Vertex vertex = new Vertex(vertexId);
        vertices.put(vertexId, vertex);
    }

    public void addEdge(T begin, T end) {
        Vertex<T> beginVertex = vertices.get(begin);
        Vertex<T> endVertex = vertices.get(end);
        if (beginVertex != null && endVertex != null) {
            beginVertex.connect(endVertex);
        }
    }

    /**
     * 根据该有向无环图获取任务执行链
     * 调用前需要调用 @see {@link #hasRecycle()} 判断是否存在环形
     * @return          广度遍历路径
     */
    public List<Vertex<T>> buildTaskSchedulePath() {
        List<Vertex<T>> result = new ArrayList<>();

        // 将所有顶点都设置为未访问
        resetVertices();

        // 找出入度为0的点
        List<Vertex<T>> sourceVertices = vertices.values()
                .stream()
                .filter(vertex -> vertex.getPreviousVertexs().size() == 0)
                .collect(Collectors.toList());

        // 广度遍历辅助队列
        Queue<Vertex<T>> queue = new LinkedList<>();
        sourceVertices.forEach(vertex -> queue.add(vertex));

        while (!queue.isEmpty()) {
            Vertex<T> topVertex = queue.poll();
            List<Vertex<T>> previousVertexs = topVertex.getPreviousVertexs();
            boolean changePosition = false;
            for (Vertex<T> previousVertex : previousVertexs) {
                // 如果存在前置节点还没被访问的情况,则把该节点放到队列最后
                if (!previousVertex.isVisited()) {
                    queue.offer(topVertex);
                    changePosition = true;
                    break;
                }
            }
            List<Vertex<T>> unVisitedNeighbors = topVertex.getNeighbors(false);
            unVisitedNeighbors.forEach(neighbors -> {
                if (!queue.contains(neighbors)) {
                    queue.offer(neighbors);
                }
            });
            if (!changePosition) {
                topVertex.visit();
                result.add(topVertex);
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
        for (Vertex<T> vertex : vertices.values()) {
            if (vertex.getPreviousVertexs().isEmpty() && vertex.getEdges().isEmpty()) {
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
        LinkedList<Vertex<T>> vertexStack = new LinkedList<>();
        // 已经遍历过的点
        List<Vertex<T>> traversaledVertexs = new ArrayList<>();

        for (Vertex<T> vertex : vertices.values()) {
            if (vertex.isVisited()) {
                continue;
            }
            vertex.visit();
            vertexStack.push(vertex);
            while (!vertexStack.isEmpty()) {
                Vertex<T> topVertex = vertexStack.peek();
                List<Vertex<T>> visitedNeighbors = topVertex.getNeighbors(true);
                for (Vertex<T> neighbor : visitedNeighbors) {
                    if (!traversaledVertexs.contains(neighbor)) {
                        return true;
                    }
                }
                Vertex<T> nextNeighbor = topVertex.getUnvisitedNeighbor();
                if (nextNeighbor != null) {
                    nextNeighbor.visit();
                    vertexStack.push(nextNeighbor);
                } else {
                    vertexStack.pop();
                    traversaledVertexs.add(topVertex);
                }
            }
        }
        return false;
    }

    /**
     * 将所有点都设置为未访问
     */
    private void resetVertices() {
        Iterator<Vertex<T>> vertexIterator = vertices.values().iterator();
        while (vertexIterator.hasNext()) {
            Vertex<T> nextVertex = vertexIterator.next();
            nextVertex.unVisit();
        }
    }
}
