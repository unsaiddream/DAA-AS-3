
package algorithm;

import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MSTTest {

    @Test
    void testSimpleGraph() {
        Graph graph = new Graph(4, false);

        graph.addEdge(0, 1, 5);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, 2);
        graph.addEdge(0, 3, 10);
        graph.addEdge(0, 2, 7);

        Kruskal.Result kruskal = Kruskal.findMST(graph);
        Prim.Result prim = Prim.findMST(graph);

        assertEquals(10, kruskal.totalWeight);
        assertEquals(10, prim.totalWeight);

        assertEquals(3, kruskal.mstEdges.size());
        assertEquals(3, prim.mstEdges.size());

        assertEquals(1, kruskal.components);
        assertEquals(1, prim.components);
    }

    @Test
    void testDisconnectedGraph() {
        Graph graph = new Graph(5, false);

        graph.addEdge(0, 1, 4);
        graph.addEdge(2, 3, 6);

        Kruskal.Result kruskal = Kruskal.findMST(graph);
        Prim.Result prim = Prim.findMST(graph);

        assertEquals(10, kruskal.totalWeight);
        assertEquals(kruskal.totalWeight, prim.totalWeight);

        assertEquals(3, kruskal.components);
        assertEquals(3, prim.components);

        assertEquals(2, kruskal.mstEdges.size());
        assertEquals(2, prim.mstEdges.size());
    }

    @Test
    void testSelfLoopsAndParallelEdges() {
        Graph graph = new Graph(3, false);

        graph.addEdge(0, 0, -100);
        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);

        Kruskal.Result kruskal = Kruskal.findMST(graph);
        Prim.Result prim = Prim.findMST(graph);

        assertEquals(5, kruskal.totalWeight);
        assertEquals(5, prim.totalWeight);

        assertEquals(2, kruskal.mstEdges.size());
        assertEquals(2, prim.mstEdges.size());
    }

    @Test
    void testRandomConnectedGraphs() {
        Random random = new Random(42);

        for (int test = 0; test < 100; test++) {

            int n = 2 + random.nextInt(49);
            Graph graph = new Graph(n, false);

            // Build a spanning tree to ensure connectivity
            for (int i = 1; i < n; i++) {
                int parent = random.nextInt(i);
                int weight = 1 + random.nextInt(100);

                graph.addEdge(parent, i, weight);
            }

            // Add random extra edges
            int extraEdges = n * 3;

            for (int i = 0; i < extraEdges; i++) {
                int u = random.nextInt(n);
                int v = random.nextInt(n);

                if (u == v) {
                    continue;
                }

                int weight = 1 + random.nextInt(100);
                graph.addEdge(u, v, weight);
            }

            Kruskal.Result kruskal = Kruskal.findMST(graph);
            Prim.Result prim = Prim.findMST(graph);

            assertEquals(
                    kruskal.totalWeight,
                    prim.totalWeight,
                    "MST weight mismatch on test " + test
            );

            assertEquals(n - 1, kruskal.mstEdges.size());
            assertEquals(n - 1, prim.mstEdges.size());

            assertEquals(1, kruskal.components);
            assertEquals(1, prim.components);
        }
    }

    @Test
    void testIsolatedVertices() {
        Graph graph = new Graph(4, false);

        Kruskal.Result kruskal = Kruskal.findMST(graph);
        Prim.Result prim = Prim.findMST(graph);

        assertEquals(0, kruskal.totalWeight);
        assertEquals(0, prim.totalWeight);

        assertEquals(4, kruskal.components);
        assertEquals(4, prim.components);

        assertTrue(kruskal.mstEdges.isEmpty());
        assertTrue(prim.mstEdges.isEmpty());
    }
}
