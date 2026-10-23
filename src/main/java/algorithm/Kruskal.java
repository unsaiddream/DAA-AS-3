
package algorithm;

import graph.Graph;
import graph.Edge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Kruskal {

    public static class Result {
        public final long totalWeight;
        public final List<Edge> mstEdges;
        public final int components;
        public final long dsuOps;
        public final long edgeScans;
        public final long comparisons;

        public Result(long totalWeight, List<Edge> mstEdges,
                      int components, long dsuOps,
                      long edgeScans, long comparisons) {
            this.totalWeight = totalWeight;
            this.mstEdges = mstEdges;
            this.components = components;
            this.dsuOps = dsuOps;
            this.edgeScans = edgeScans;
            this.comparisons = comparisons;
        }
    }

    public static Result findMST(Graph graph) {

        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kruskal requires an undirected graph"
            );
        }

        List<Edge> edges = new ArrayList<>(graph.getEdges());

        final long[] comparisons = {0};

        edges.sort((a, b) -> {
            comparisons[0]++;
            return Integer.compare(a.weight, b.weight);
        });

        DSU dsu = DSU.makeSet(graph.size());

        List<Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        long edgeScans = 0;

        for (Edge edge : edges) {
            edgeScans++;

            if (dsu.union(edge.from, edge.to)) {
                mstEdges.add(edge);
                totalWeight += edge.weight;
            }
        }

        long dsuOps = dsu.getFindCalls()
                + dsu.getSuccessfulUnions();

        return new Result(
                totalWeight,
                mstEdges,
                dsu.components(),
                dsuOps,
                edgeScans,
                comparisons[0]
        );
    }
}
