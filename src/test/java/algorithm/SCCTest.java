
package algorithm;

import graph.Edge;
import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class SCCTest {

    @Test
    void testSimpleCycle() {
        Graph graph = new Graph(4, true);

        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 0, 4);
        graph.addEdge(2, 3, 5);

        Kosaraju.Result result = Kosaraju.findSCC(graph);

        assertEquals(2, result.componentCount);
        assertEquals(result.component[0], result.component[1]);
        assertEquals(result.component[1], result.component[2]);
        assertNotEquals(result.component[2], result.component[3]);

        assertEquals(8, result.edgeScans);
    }

    @Test
    void testIsolatedVertices() {
        Graph graph = new Graph(5, true);

        Kosaraju.Result result = Kosaraju.findSCC(graph);

        assertEquals(5, result.componentCount);
        assertEquals(0, result.edgeScans);
    }

    @Test
    void testSelfLoopsAndParallelEdges() {
        Graph graph = new Graph(3, true);

        graph.addEdge(0, 0, 1);
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 1, 3);
        graph.addEdge(1, 0, 4);
        graph.addEdge(1, 2, 5);

        Kosaraju.Result result = Kosaraju.findSCC(graph);

        assertEquals(2, result.componentCount);
        assertEquals(result.component[0], result.component[1]);
        assertNotEquals(result.component[1], result.component[2]);
        assertEquals(10, result.edgeScans);
    }

    // BFS: find all vertices reachable from start
    private boolean[] reachable(Graph graph, int start) {
        boolean[] visited = new boolean[graph.size()];
        ArrayDeque<Integer> queue = new ArrayDeque<>();

        visited[start] = true;
        queue.add(start);

        while (!queue.isEmpty()) {
            int vertex = queue.poll();

            for (Edge edge : graph.neighbors(vertex)) {
                if (!visited[edge.to]) {
                    visited[edge.to] = true;
                    queue.add(edge.to);
                }
            }
        }

        return visited;
    }

    @Test
    void testRandomGraphsAgainstBruteForce() {
        Random random = new Random(42);

        for (int test = 0; test < 30; test++) {
            int n = 2 + random.nextInt(199);
            Graph graph = new Graph(n, true);

            int m = n * 3;

            for (int i = 0; i < m; i++) {
                int u = random.nextInt(n);
                int v = random.nextInt(n);

                graph.addEdge(u, v, 1);
            }

            Kosaraju.Result result = Kosaraju.findSCC(graph);

            boolean[][] reach = new boolean[n][];

            for (int i = 0; i < n; i++) {
                reach[i] = reachable(graph, i);
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    boolean sameSCC =
                            reach[i][j] && reach[j][i];

                    boolean sameKosaraju =
                            result.component[i] == result.component[j];

                    assertEquals(
                            sameSCC,
                            sameKosaraju,
                            "SCC mismatch: test=" + test +
                                    ", vertices=" + i + "," + j
                    );
                }
            }

            assertEquals(2L * m, result.edgeScans);
        }
    }

    @Test
    void testStackSafety100000Vertices() {
        int n = 100_000;
        Graph graph = new Graph(n, true);

        for (int i = 0; i < n - 1; i++) {
            graph.addEdge(i, i + 1, 1);
        }

        Kosaraju.Result result =
                assertDoesNotThrow(() -> Kosaraju.findSCC(graph));

        assertEquals(n, result.componentCount);
        assertEquals(2L * (n - 1), result.edgeScans);
        assertTrue(result.maxDepth > 0);
    }
}
