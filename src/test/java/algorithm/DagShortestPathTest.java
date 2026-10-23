
package algorithm;

import graph.Edge;
import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DagShortestPathTest {

    // Independent Bellman-Ford reference implementation
    private long[] bellmanFord(Graph graph) {
        int n = graph.size();
        long inf = Long.MAX_VALUE;

        long[] dist = new long[n];
        int[] inDegree = new int[n];

        Arrays.fill(dist, inf);

        for (Edge edge : graph.getEdges()) {
            inDegree[edge.to]++;
        }

        // Virtual super-source
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                dist[i] = 0;
            }
        }

        for (int i = 0; i < n - 1; i++) {
            boolean changed = false;

            for (Edge edge : graph.getEdges()) {
                if (dist[edge.from] == inf) {
                    continue;
                }

                long candidate =
                        dist[edge.from] + edge.weight;

                if (candidate < dist[edge.to]) {
                    dist[edge.to] = candidate;
                    changed = true;
                }
            }

            if (!changed) {
                break;
            }
        }

        return dist;
    }

    @Test
    void testSimpleDAG() {
        Graph graph = new Graph(5, true);

        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 5);
        graph.addEdge(1, 3, 2);
        graph.addEdge(2, 3, 4);
        graph.addEdge(3, 4, 1);

        DagShortestPath.Result result =
                DagShortestPath.shortestPaths(graph);

        assertArrayEquals(
                new long[]{0, 3, 5, 5, 6},
                result.distance
        );

        assertEquals(19, result.totalDistance);
        assertEquals(5, result.edgeScans);

        assertEquals(
                List.of(0, 1, 3, 4),
                result.getPath(4)
        );
    }

    @Test
    void testNegativeWeights() {
        Graph graph = new Graph(4, true);

        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, -4);
        graph.addEdge(2, 3, 2);

        DagShortestPath.Result result =
                DagShortestPath.shortestPaths(graph);

        assertArrayEquals(
                new long[]{0, 5, 3, 1},
                result.distance
        );

        assertEquals(9, result.totalDistance);
        assertEquals(4, result.edgeScans);

        assertEquals(
                List.of(0, 1, 3),
                result.getPath(3)
        );
    }

    @Test
    void testMultipleSources() {
        Graph graph = new Graph(4, true);

        graph.addEdge(0, 2, 10);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, 2);

        DagShortestPath.Result result =
                DagShortestPath.shortestPaths(graph);

        assertArrayEquals(
                new long[]{0, 0, 3, 5},
                result.distance
        );

        assertEquals(
                List.of(1, 2, 3),
                result.getPath(3)
        );

        assertEquals(3, result.edgeScans);
    }

    @Test
    void testCycleRejected() {
        Graph graph = new Graph(3, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, -5);

        assertThrows(
                IllegalArgumentException.class,
                () -> DagShortestPath.shortestPaths(graph)
        );
    }

    @Test
    void testRandomDAGsAgainstBellmanFord() {
        Random random = new Random(42);

        for (int test = 0; test < 100; test++) {

            int n = 2 + random.nextInt(49);
            Graph graph = new Graph(n, true);

            int m = n * 4;

            for (int i = 0; i < m; i++) {
                int u = random.nextInt(n);
                int v = random.nextInt(n);

                if (u == v) {
                    continue;
                }

                // All edges go from lower to higher vertex.
                // Therefore the graph cannot contain cycles.
                if (u > v) {
                    int temp = u;
                    u = v;
                    v = temp;
                }

                int weight = random.nextInt(41) - 20;

                graph.addEdge(u, v, weight);
            }

            DagShortestPath.Result result =
                    DagShortestPath.shortestPaths(graph);

            long[] expected = bellmanFord(graph);

            assertArrayEquals(
                    expected,
                    result.distance,
                    "Mismatch on random DAG test " + test
            );

            assertEquals(
                    graph.getEdges().size(),
                    result.edgeScans
            );
        }
    }
}
