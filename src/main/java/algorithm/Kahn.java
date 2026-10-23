
package algorithm;

import graph.Edge;
import graph.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Kahn {

    public static class Result {
        public final List<Integer> order;
        public final boolean hasCycle;
        public final long edgeScans;

        public Result(List<Integer> order,
                      boolean hasCycle,
                      long edgeScans) {
            this.order = order;
            this.hasCycle = hasCycle;
            this.edgeScans = edgeScans;
        }
    }

    public static Result topologicalSort(Graph graph) {

        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kahn requires a directed graph"
            );
        }

        int n = graph.size();
        int[] inDegree = new int[n];

        for (Edge edge : graph.getEdges()) {
            inDegree[edge.to]++;
        }

        ArrayDeque<Integer> queue = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> order = new ArrayList<>();
        long edgeScans = 0;

        while (!queue.isEmpty()) {

            int vertex = queue.poll();
            order.add(vertex);

            for (Edge edge : graph.neighbors(vertex)) {
                edgeScans++;

                inDegree[edge.to]--;

                if (inDegree[edge.to] == 0) {
                    queue.add(edge.to);
                }
            }
        }

        boolean hasCycle = order.size() != n;

        return new Result(order, hasCycle, edgeScans);
    }

    public static boolean isValidOrder(
            Graph graph, List<Integer> order) {

        int n = graph.size();

        if (order.size() != n) {
            return false;
        }

        int[] position = new int[n];
        boolean[] seen = new boolean[n];

        for (int i = 0; i < n; i++) {
            int vertex = order.get(i);

            if (vertex < 0 || vertex >= n || seen[vertex]) {
                return false;
            }

            seen[vertex] = true;
            position[vertex] = i;
        }

        for (Edge edge : graph.getEdges()) {
            if (position[edge.from] >= position[edge.to]) {
                return false;
            }
        }

        return true;
    }
}
