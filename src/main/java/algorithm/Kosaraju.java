
package algorithm;

import graph.Edge;
import graph.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Kosaraju {

    public static class Result {
        public final int componentCount;
        public final int[] component;
        public final long edgeScans;
        public final int maxDepth;

        public Result(int componentCount, int[] component,
                      long edgeScans, int maxDepth) {
            this.componentCount = componentCount;
            this.component = component;
            this.edgeScans = edgeScans;
            this.maxDepth = maxDepth;
        }
    }

    private static class Frame {
        int vertex;
        int nextIndex;

        Frame(int vertex) {
            this.vertex = vertex;
            this.nextIndex = 0;
        }
    }

    public static Result findSCC(Graph graph) {

        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kosaraju requires a directed graph"
            );
        }

        int n = graph.size();

        boolean[] visited = new boolean[n];
        List<Integer> order = new ArrayList<>(n);

        long edgeScans = 0;
        int maxDepth = 0;

        for (int start = 0; start < n; start++) {

            if (visited[start]) {
                continue;
            }

            ArrayDeque<Frame> stack = new ArrayDeque<>();
            visited[start] = true;
            stack.push(new Frame(start));

            maxDepth = Math.max(maxDepth, stack.size());

            while (!stack.isEmpty()) {

                Frame frame = stack.peek();
                List<Edge> neighbors = graph.neighbors(frame.vertex);

                if (frame.nextIndex < neighbors.size()) {

                    Edge edge = neighbors.get(frame.nextIndex++);
                    edgeScans++;

                    if (!visited[edge.to]) {
                        visited[edge.to] = true;
                        stack.push(new Frame(edge.to));

                        maxDepth = Math.max(maxDepth, stack.size());
                    }

                } else {
                    order.add(frame.vertex);
                    stack.pop();
                }
            }
        }

        Graph reversed = new Graph(n, true);

        for (Edge edge : graph.getEdges()) {
            reversed.addEdge(edge.to, edge.from, edge.weight);
        }

        boolean[] visitedReverse = new boolean[n];
        int[] component = new int[n];
        int componentCount = 0;

        for (int i = order.size() - 1; i >= 0; i--) {

            int start = order.get(i);

            if (visitedReverse[start]) {
                continue;
            }

            ArrayDeque<Integer> stack = new ArrayDeque<>();
            stack.push(start);
            visitedReverse[start] = true;

            maxDepth = Math.max(maxDepth, stack.size());

            while (!stack.isEmpty()) {

                int vertex = stack.pop();
                component[vertex] = componentCount;

                for (Edge edge : reversed.neighbors(vertex)) {
                    edgeScans++;

                    if (!visitedReverse[edge.to]) {
                        visitedReverse[edge.to] = true;
                        stack.push(edge.to);

                        maxDepth = Math.max(maxDepth, stack.size());
                    }
                }
            }

            componentCount++;
        }

        return new Result(
                componentCount,
                component,
                edgeScans,
                maxDepth
        );
    }
}
