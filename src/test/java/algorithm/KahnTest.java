
package algorithm;

import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class KahnTest {

    @Test
    void testSimpleDAG() {
        Graph graph = new Graph(5, true);

        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 5);
        graph.addEdge(1, 3, 2);
        graph.addEdge(2, 3, 4);
        graph.addEdge(3, 4, 1);

        Kahn.Result result = Kahn.topologicalSort(graph);

        assertFalse(result.hasCycle);
        assertEquals(5, result.order.size());
        assertTrue(Kahn.isValidOrder(graph, result.order));
        assertEquals(5, result.edgeScans);
    }

    @Test
    void testCycleDetection() {
        Graph graph = new Graph(3, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        Kahn.Result result = Kahn.topologicalSort(graph);

        assertTrue(result.hasCycle);
        assertTrue(result.order.size() < graph.size());
        assertFalse(Kahn.isValidOrder(graph, result.order));
    }

    @Test
    void testIsolatedVertices() {
        Graph graph = new Graph(4, true);

        Kahn.Result result = Kahn.topologicalSort(graph);

        assertFalse(result.hasCycle);
        assertEquals(4, result.order.size());
        assertTrue(Kahn.isValidOrder(graph, result.order));
        assertEquals(0, result.edgeScans);
    }

    @Test
    void testInvalidOrder() {
        Graph graph = new Graph(3, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);

        assertFalse(
                Kahn.isValidOrder(graph, Arrays.asList(2, 1, 0))
        );

        assertFalse(
                Kahn.isValidOrder(graph, Arrays.asList(0, 1, 1))
        );

        assertFalse(
                Kahn.isValidOrder(graph, Arrays.asList(0, 1))
        );

        assertTrue(
                Kahn.isValidOrder(graph, Arrays.asList(0, 1, 2))
        );
    }

    @Test
    void testLargeDAG() {
        int n = 100_000;
        Graph graph = new Graph(n, true);

        for (int i = 0; i < n - 1; i++) {
            graph.addEdge(i, i + 1, 1);
        }

        Kahn.Result result = Kahn.topologicalSort(graph);

        assertFalse(result.hasCycle);
        assertEquals(n, result.order.size());
        assertTrue(Kahn.isValidOrder(graph, result.order));
        assertEquals(n - 1, result.edgeScans);
    }
}
