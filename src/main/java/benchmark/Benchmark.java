
package benchmark;

import algorithm.*;
import graph.Graph;
import metrics.Metrics;
import metrics.CsvWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

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

    public static void main(String[] args) throws IOException {

        List<Metrics> results = new ArrayList<>();
        int index = 0;

        for (int n : SIZES) {
            for (int density : new int[]{4, 20}) {

                int m = n * density;
                String type = density == 4 ? "sparse" : "dense";

                System.out.println(
                        "\nChecking n=" + n + ", m=" + m
                );

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

                double kruskalTime = measureMedian(
                        () -> Kruskal.findMST(undirected)
                );

                double primTime = measureMedian(
                        () -> Prim.findMST(undirected)
                );

                results.add(new Metrics(
                        "Kruskal", "undirected_" + type,
                        n, m, kruskalTime,
                        kruskal.dsuOps,
                        kruskal.edgeScans,
                        kruskal.comparisons,
                        0,
                        kruskal.totalWeight
                ));

                results.add(new Metrics(
                        "Prim", "undirected_" + type,
                        n, m, primTime,
                        0,
                        prim.edgeScans,
                        prim.comparisons,
                        0,
                        prim.totalWeight
                ));

                System.out.println(
                        "MST weight: " + kruskal.totalWeight + " OK"
                );

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

                double sccTime = measureMedian(
                        () -> Kosaraju.findSCC(directed)
                );

                results.add(new Metrics(
                        "Kosaraju", "directed_" + type,
                        n, m, sccTime,
                        0,
                        scc.edgeScans,
                        0,
                        scc.maxDepth,
                        scc.componentCount
                ));

                System.out.println(
                        "SCC count: " + scc.componentCount + " OK"
                );

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

                check(
                        paths.totalDistance == DAG_EXPECTED[index],
                        "Incorrect DAG distance sum"
                );

                check(
                        paths.edgeScans == m,
                        "Incorrect DAG edge scans"
                );

                double kahnTime = measureMedian(
                        () -> Kahn.topologicalSort(dag)
                );

                double pathsTime = measureMedian(
                        () -> DagShortestPath.shortestPaths(dag)
                );

                results.add(new Metrics(
                        "Kahn", "dag_" + type,
                        n, m, kahnTime,
                        0,
                        kahn.edgeScans,
                        0,
                        0,
                        kahn.order.size()
                ));

                results.add(new Metrics(
                        "DagShortestPath", "dag_" + type,
                        n, m, pathsTime,
                        0,
                        paths.edgeScans,
                        0,
                        0,
                        paths.totalDistance
                ));

                System.out.println(
                        "DAG distance sum: " +
                                paths.totalDistance + " OK"
                );

                index++;
            }
        }

        check(
                results.size() == 50,
                "Expected 50 CSV rows"
        );

        CsvWriter.write("results/results.csv", results);

        System.out.println(
                "\nAll reference values matched!"
        );

        System.out.println(
                "CSV saved: results/results.csv"
        );
    }

    private static <T> double measureMedian(
            Supplier<T> algorithm) {

        algorithm.get();
        algorithm.get();

        double[] times = new double[5];

        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();

            algorithm.get();

            long end = System.nanoTime();

            times[i] = (end - start) / 1_000_000.0;
        }

        Arrays.sort(times);
        return times[2];
    }

    private static void check(
            boolean condition, String message) {

        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
