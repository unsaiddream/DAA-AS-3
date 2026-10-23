
package algorithm;

import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Prim {

    public static class Result {
        public final long totalWeight;
        public final List<Edge> mstEdges;
        public final int components;
        public final long edgeScans;
        public final long comparisons;

        public Result(long totalWeight, List<Edge> mstEdges,
                      int components, long edgeScans,
                      long comparisons) {
            this.totalWeight = totalWeight;
            this.mstEdges = mstEdges;
            this.components = components;
            this.edgeScans = edgeScans;
            this.comparisons = comparisons;
        }
    }

    public static Result findMST(Graph graph) {

        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Prim requires an undirected graph"
            );
        }

        int n = graph.size();

        boolean[] visited = new boolean[n];
        List<Edge> mstEdges = new ArrayList<>();

        final long[] comparisons = {0};

        PriorityQueue<Edge> pq = new PriorityQueue<>(
                (a, b) -> {
                    comparisons[0]++;
                    return Integer.compare(a.weight, b.weight);
                }
        );

        long totalWeight = 0;
        long edgeScans = 0;
        int components = 0;

        for (int start = 0; start < n; start++) {

            if (visited[start]) {
                continue;
            }

            components++;
            visited[start] = true;

            for (Edge edge : graph.neighbors(start)) {
                edgeScans++;
                pq.add(edge);
            }

            while (!pq.isEmpty()) {

                Edge edge = pq.poll();

                if (visited[edge.to]) {
                    continue;
                }

                visited[edge.to] = true;
                mstEdges.add(edge);
                totalWeight += edge.weight;

                for (Edge next : graph.neighbors(edge.to)) {
                    edgeScans++;

                    if (!visited[next.to]) {
                        pq.add(next);
                    }
                }
            }
        }

        return new Result(
                totalWeight,
                mstEdges,
                components,
                edgeScans,
                comparisons[0]
        );
    }
}
