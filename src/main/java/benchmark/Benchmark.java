
package benchmark;

import algorithm.*;
import graph.Graph;

public class Benchmark {

    private static final int[] SIZES = {
            1000, 3162, 10000, 31623, 100000
    };

    private static final long[] MST_EXPECTED = {
            147970, 30363,
            468911, 97589,
            1486139, 309550,
            4715515, 968517,
            15012094, 3056681
    };

    private static final int[] SCC_EXPECTED = {
            27, 1,
            137, 1,
            373, 1,
            1188, 1,
            3923, 1
    };

    private static final long[] DAG_EXPECTED = {
            396274, 252411,
            1271911, 723157,
            3941828, 2381342,
            12551891, 7376612,
            40096539, 23512087
    };

    public static void main(String[] args) {

        int index = 0;

        for (int n : SIZES) {
            for (int density : new int[]{4, 20}) {

                int m = n * density;

                System.out.println(
                        "\nChecking n=" + n + ", m=" + m
                );

                // Undirected graph: MST
                Graph undirected =
                        GraphGenerator.undirected(n, m);

                Kruskal.Result kruskal =
                        Kruskal.findMST(undirected);

                Prim.Result prim =
                        Prim.findMST(undirected);

                check(
                        kruskal.totalWeight == prim.totalWeight,
                        "Kruskal and Prim weights differ"
                );

                check(
                        kruskal.totalWeight == MST_EXPECTED[index],
                        "Incorrect MST weight"
                );

                check(
                        kruskal.components == 1 &&
                                prim.components == 1,
                        "MST graph must be connected"
                );

                System.out.println(
                        "MST weight: " + kruskal.totalWeight + " OK"
                );

                // Directed graph: SCC
                Graph directed =
                        GraphGenerator.directed(n, m);

                Kosaraju.Result scc =
                        Kosaraju.findSCC(directed);

                check(
                        scc.componentCount == SCC_EXPECTED[index],
                        "Incorrect SCC count"
                );

                check(
                        scc.edgeScans == 2L * m,
                        "Incorrect Kosaraju edge scans"
                );

                System.out.println(
                        "SCC count: " + scc.componentCount + " OK"
                );

                // DAG: topological order and shortest paths
                Graph dag = GraphGenerator.dag(n, m);

                Kahn.Result kahn =
                        Kahn.topologicalSort(dag);

                check(
                        !kahn.hasCycle &&
                                Kahn.isValidOrder(dag, kahn.order),
                        "Invalid topological order"
                );

                check(
                        kahn.edgeScans == m,
                        "Incorrect Kahn edge scans"
                );

                DagShortestPath.Result paths =
                        DagShortestPath.shortestPaths(dag);

                System.out.println("Expected DAG sum: " + DAG_EXPECTED[index]);
                System.out.println("Actual DAG sum: " + paths.totalDistance);

                check(
                        paths.totalDistance == DAG_EXPECTED[index],
                        "Incorrect DAG distance sum"
                );

                check(
                        paths.edgeScans == m,
                        "Incorrect DAG edge scans"
                );

                System.out.println(
                        "DAG distance sum: " +
                                paths.totalDistance + " OK"
                );

                index++;
            }
        }

        System.out.println(
                "\nAll reference values matched!"
        );
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
