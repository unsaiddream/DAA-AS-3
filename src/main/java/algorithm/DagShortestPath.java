
package algorithm;

import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DagShortestPath {

    public static class Result {
        public final long[] distance;
        public final int[] parent;
        public final long edgeScans;
        public final long totalDistance;

        public Result(long[] distance, int[] parent,
                      long edgeScans, long totalDistance) {
            this.distance = distance;
            this.parent = parent;
            this.edgeScans = edgeScans;
            this.totalDistance = totalDistance;
        }

        public List<Integer> getPath(int target) {
            if (target < 0 || target >= distance.length) {
                throw new IllegalArgumentException("Invalid vertex");
            }

            List<Integer> path = new ArrayList<>();

            if (distance[target] == Long.MAX_VALUE) {
                return path;
            }

            int current = target;

            while (current != -1) {
                path.add(current);
                current = parent[current];
            }

            Collections.reverse(path);
            return path;
        }
    }

    public static Result shortestPaths(Graph graph) {

        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "DAG shortest paths require a directed graph"
            );
        }

        Kahn.Result topological = Kahn.topologicalSort(graph);

        if (topological.hasCycle) {
            throw new IllegalArgumentException(
                    "Shortest paths require a DAG without cycles"
            );
        }

        int n = graph.size();

        long[] distance = new long[n];
        int[] parent = new int[n];
        int[] inDegree = new int[n];

        for (int i = 0; i < n; i++) {
            distance[i] = Long.MAX_VALUE;
            parent[i] = -1;
        }

        for (Edge edge : graph.getEdges()) {
            inDegree[edge.to]++;
        }

        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                distance[i] = 0;
            }
        }

        long edgeScans = 0;

        for (int vertex : topological.order) {

            if (distance[vertex] == Long.MAX_VALUE) {
                continue;
            }

            for (Edge edge : graph.neighbors(vertex)) {
                edgeScans++;

                long newDistance =
                        distance[vertex] + edge.weight;

                if (newDistance < distance[edge.to]) {
                    distance[edge.to] = newDistance;
                    parent[edge.to] = vertex;
                }
            }
        }

        long totalDistance = 0;

        for (long d : distance) {
            if (d != Long.MAX_VALUE) {
                totalDistance += d;
            }
        }

        return new Result(
                distance,
                parent,
                edgeScans,
                totalDistance
        );
    }
}
