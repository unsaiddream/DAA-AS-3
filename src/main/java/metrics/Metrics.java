
package metrics;

import java.util.Locale;

public class Metrics {

    public final String algorithm;
    public final String graph;
    public final int n;
    public final int m;

    public final double timeMs;
    public final long dsuOps;
    public final long edgeScans;
    public final long comparisons;
    public final int maxDepth;
    public final long result;

    public Metrics(String algorithm, String graph,
                   int n, int m, double timeMs,
                   long dsuOps, long edgeScans,
                   long comparisons, int maxDepth,
                   long result) {

        this.algorithm = algorithm;
        this.graph = graph;
        this.n = n;
        this.m = m;
        this.timeMs = timeMs;
        this.dsuOps = dsuOps;
        this.edgeScans = edgeScans;
        this.comparisons = comparisons;
        this.maxDepth = maxDepth;
        this.result = result;
    }

    public String toCsv() {
        return String.format(
                Locale.US,
                "%s,%s,%d,%d,%.4f,%d,%d,%d,%d,%d",
                algorithm, graph, n, m, timeMs,
                dsuOps, edgeScans, comparisons,
                maxDepth, result
        );
    }
}
